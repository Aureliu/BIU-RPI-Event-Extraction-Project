#! /usr/bin/perl -w
#
# similarity.pl version 2.05
# (Last updated $Id: similarity.pl,v 1.30 2008/05/30 23:12:43 sidz1979 Exp $)
#
# ---------------------------------------------------------------------

# Include external packages
use strict;
use WordNet::QueryData 1.40;
use Getopt::Long;
use WordNet::Similarity;

# If no Command-Line arguments given ... show minimal help screen ... exit.
if($#ARGV < 0)
{

  # usage info moved to showUsage()
  showUsage();
  exit(0);
}

# Get Command-Line options.
our ($opt_help, $opt_version, $opt_wnpath, $opt_simpath, $opt_type);
our ($opt_config, $opt_file, $opt_trace, $opt_allsenses, $opt_offsets);
our ($opt_interact);
&GetOptions("help", "version", "wnpath=s", "simpath=s", "type=s","config=s", "file=s", "trace", "allsenses", "offsets","interact");

# To be able to use a local install of similarity modules.
if(defined $opt_simpath)
{
  my @tmpINC = @INC;
  @INC = ($opt_simpath);
  push(@INC, @tmpINC);
}

# Check if help has been requested ... If so ... display help.
if(defined $opt_help)
{
  &showHelp;
  exit 0;
}

# Check if version number has been requested ... If so ... display version.
if(defined $opt_version)
{
  &showVersion;
  exit 0;
}

# Which similarity measure must be used ...
if(!defined $opt_type)
{
  print STDERR "Required switch '--type' missing.\n";
  &showUsage;
  exit 1;
}

# If the file option has not been provided, then
# the two words must be on the command line.
# Get the two words if they have been provided.
if(!(defined $opt_file) && !(defined $opt_interact) && $#ARGV < 1)
{
  print STDERR "Required parameter(s) missing.\n";
  &showUsage;
  exit 1;
}

# Initialize the WordNet::QueryData module.
print STDERR "Loading WordNet... ";
my $wn = (defined $opt_wnpath) ? WordNet::QueryData->new($opt_wnpath) : WordNet::QueryData->new();
if(!$wn)
{
  print STDERR "Unable to create WordNet object.\n";
  exit 1;
}
print STDERR "done.\n";

# Load the WordNet::Similarity module.
print STDERR "Loading Module... ";
my $type = $opt_type;
$opt_type =~ s/::/\//g;
$opt_type .= ".pm";
require $opt_type;
my $measure = undef;
if(defined $opt_config)
{
  $measure = $type->new($wn, $opt_config);
}
else
{
  $measure = $type->new($wn);
}

# If object not created.
if(!$measure)
{
  print STDERR "Unable to create WordNet::Similarity object.\n";
  exit 1;
}

# If serious error... stop.
my ($error, $errorString) = $measure->getError();
if($error > 1)
{
  print STDERR $errorString."\n";
  exit 1;
}

# Set the appropriate trace params.
if(defined $opt_trace)
{
  if ($measure->{trace} >= 2)
  {
    $opt_offsets = 1;
  }
  else
  {
    $measure->{'trace'} = ((defined $opt_offsets) ? 2 : 1);
  }
}
else
{

  # JM 1-28-04
  # The following code was causing problems if a user specifies a trace
  # level of 2 in a config file but not the --offsets option, then the user
  # still only sees level 1 traces.
  #if($measure->{'trace'})
  #  {
  #      $opt_trace = 1;
  #	$measure->{'trace'} = ((defined $opt_offsets) ? 2 : 1);
  #  }
  $opt_offsets = 1 if $measure->{trace} >= 2;
  $opt_trace = $measure->{trace};
}
print STDERR "done.\n";

# Get the module initialization parameters.
if(defined $opt_trace)
{
  my $loctr = $measure->getTraceString();
  print "\n$loctr\n" if($loctr !~ /^\s*$/);
}
print STDERR $errorString."\n" if($error == 1);

# Process the input data...
if(defined $opt_interact)
{
  my ($con1, $con2);
  print "Starting interactive mode (Enter blank fields to end session)...\n";
  $con1 = $con2 = "x";           # Hack to start the interactive while loop.
  while($con1 ne "" && $con2 ne "")
  {
    print "Concept \#1: ";
    $con1 = <STDIN>;
    $con1 =~ s/[\r\f\n]//g;
    $con1 =~ s/^\s+//;
    $con1 =~ s/\s+$//;
    last if($con1 eq "");
    print "Concept \#2: ";
    $con2 = <STDIN>;
    $con2 =~ s/[\r\f\n]//g;
    $con2 =~ s/^\s+//;
    $con2 =~ s/\s+$//;
    last if($con2 eq "");
    print "$con1  $con2\n" if(defined $opt_trace);
    &process($con1, $con2);
    print "\n" if(defined $opt_trace);
  }
}
elsif(defined $opt_file)
{
  open(DATA, $opt_file) || die "Unable to open file: $opt_file\n";
  while(<DATA>)
  {

    # 12/9/03 JM (#2)
    # modified below to remove comments and ensure that each
    # line contains exactly two words
    s/[\r\f\n]//g;
    s/^\s+//;
    s/^\#.*//; # remove lines beginning with # (i.e., comments)
    s|//.*||; # remove anything following // (i.e., more comments)
    s/\s+$//;
    next if 0 == length;
    my @words = split /\s+/;

    unless (scalar(@words) == 2)
    {
      print STDERR "Warning: line $. of $opt_file does not contain exactly two words: skipping line.\n";
    }
    else #(scalar(@words) && defined $words[0] && defined $words[1])
    {
      print "$words[0]  $words[1]\n" if(defined $opt_trace);
      &process($words[0], $words[1]);
      print "\n" if(defined $opt_trace);
    }
  }
  close(DATA);
}
else
{
  &process(shift, shift);
}

## ------------------- Subroutines Start Here ------------------- ##
# Subroutine that processes two words (finds relatedness).
sub process
{
  my $input1 = shift;
  my $input2 = shift;
  my $word1 = $input1;
  my $word2 = $input2;
  my $wps;
  my @w1options;
  my @w2options;
  my @senses1;
  my @senses2;
  my %distanceHash;

  if(!(defined $word1 && defined $word2))
  {
    print STDERR "Undefined input word(s).\n";
    return;
  }
  $word1 =~ s/[\r\f\n]//g;
  $word1 =~ s/^\s+//;
  $word1 =~ s/\s+$//;
  $word1 =~ s/\s+/_/g;
  $word2 =~ s/[\r\f\n]//g;
  $word2 =~ s/^\s+//;
  $word2 =~ s/\s+$//;
  $word2 =~ s/\s+/_/g;
  @w1options = &getWNSynsets($word1);
  @w2options = &getWNSynsets($word2);

  if(!(scalar(@w1options) && scalar(@w2options)))
  {
    print STDERR "'$word1' not found in WordNet.\n" if(!scalar(@w1options));
    print STDERR "'$word2' not found in WordNet.\n" if(!scalar(@w2options));
    return;
  }

  @senses1 = ();
  @senses2 = ();
  foreach $wps (@w1options)
  {
    if($wps =~ /\#([nvar])\#/)
    {
      push(@senses1, $wps) if($measure->{$1});
    }
  }
  foreach $wps (@w2options)
  {
    if($wps =~ /\#([nvar])\#/)
    {
      push(@senses2, $wps) if($measure->{$1});
    }
  }
  if(!scalar(@senses1) || !scalar(@senses2))
  {
    print STDERR "Possible part(s) of speech of word(s) cannot be handled by module.\n";
    return;
  }

  %distanceHash = &getDistances([@senses1], [@senses2]);

  if(defined $opt_allsenses)
  {
    my $key;
    print "$input1  $input2  (all senses)\n";
    foreach $key (sort {$distanceHash{$b} <=> $distanceHash{$a}} keys %distanceHash)
    {
      my ($op1, $op2) = split(/\s+/, $key);
      &printSet($op1);
      print "  ";
      &printSet($op2);
      print "  $distanceHash{$key}\n";
    }
  }
  else
  {
    my ($key) = sort {$distanceHash{$b} <=> $distanceHash{$a}} keys %distanceHash;
    my ($op1, $op2) = split(/\s+/, $key);
    &printSet($op1);
    print "  ";
    &printSet($op2);
    print "  $distanceHash{$key}\n";
  }
}

# Subroutine to get all possible synsets corresponding to a word(#pos(#sense))
sub getWNSynsets
{
  my $word = shift;
  my $pos;
  my $sense;
  my $key;
  my @senses;
  return () if(!defined $word);

  # First separately handle the case when the word is in word#pos or
  # word#pos#sense form.
  if($word =~ /\#/)
  {
    if($word =~ /^([^\#]+)\#([^\#])\#([^\#]+)$/)
    {
      $word = $1;
      $pos = $2;
      $sense = $3;
      return () if($sense !~ /[0-9]+/ || $pos !~ /^[nvar]$/);
      @senses = $wn->querySense($word."\#".$pos);
      foreach $key (@senses)
      {
        if($key =~ /\#$sense$/)
        {
          return ($key);
        }
      }
      return ();
    }
    elsif($word =~ /^([^\#]+)\#([^\#]+)$/)
    {
      $word = $1;
      $pos = $2;
      return () if($pos !~ /[nvar]/);
    }
    else
    {
      return ();
    }
  }
  else
  {
    $pos = "nvar";
  }

  # Get the senses corresponding to the raw form of the word.
  @senses = ();
  foreach $key ("n", "v", "a", "r")
  {
    if($pos =~ /$key/)
    {
      push(@senses, $wn->querySense($word."\#".$key));
    }
  }

  # If no senses corresponding to the raw form of the word,
  # ONLY then look for morphological variations.
  if(!scalar(@senses))
  {
    foreach $key ("n", "v", "a", "r")
    {
      if($pos =~ /$key/)
      {
        my @tArr = ();
        push(@tArr, $wn->validForms($word."\#".$key));
        push(@senses, $wn->querySense($tArr[0])) if(defined $tArr[0]);
      }
    }
  }
  return @senses;
}

# Subroutine to compute relatedness between all pairs of senses.
sub getDistances
{
  my $list1 = shift;
  my $list2 = shift;
  my $synset1;
  my $synset2;
  my $tracePrinted = 0;
  my %retHash = ();
  return {} if(!defined $list1 || !defined $list2);
  my %errcache;
LEVEL2:

  foreach $synset1 (@{$list1})
  {
    foreach $synset2 (@{$list2})
    {

      # modified 12/8/03 by JM
      # it is possible for getRelatedness to return a non-numeric value,
      # and this can cause problems in ::process() when the relatedness
      # values are sorted
      #$retHash{"$synset1 $synset2"} = $measure->getRelatedness($synset1, $synset2);
      my $score = $measure->getRelatedness($synset1, $synset2);
      $retHash{"$synset1 $synset2"} = $score;
      my ($err, $errString) = $measure->getError();

      #end modifications
      if($err)
      {

        # 12/9/03 JM (#1)
        # cache error strings indicating that two words belong
        # to different parts of speech
        $errString =~ m/(\S+\#[nvar])(?:\#\d+)? and (\S+\#[nvar])(?:\#\d+)?/;
        my $keystr = "$1 $2";
        print STDERR "$errString\n" unless $errcache{$keystr};
        $errcache{$keystr} = 1;

        # JM 12/8/2003
        # getRelatedness() can return a warning if the two concepts
        # are from different taxonomies, but we need to keep
        # comparing relatedness values anyways
        #
        # last LEVEL2;
        last LEVEL2 if ($err > 1);
      }
      if(defined $opt_trace)
      {
        my $loctr = $measure->getTraceString();
        if($loctr !~ /^\s*$/)
        {
          print "$synset1 $synset2:\n";
          print "$loctr\n";
          $tracePrinted = 1;
        }
      }
    }
  }
  print "\n\n" if(defined $opt_trace && $tracePrinted);
  return %retHash;
}

# Print routine to print synsets...
sub printSet
{
  my $synset = shift;
  my $offset;
  my $printString = "";
  if($synset =~ /(.*)\#([nvar])\#(.*)/)
  {
    if(defined $opt_offsets)
    {
      $offset = $wn->offset($synset);
      $printString = sprintf("$1\#$2\#%08d", $offset);
      $printString =~ s/\s+$//;
      $printString =~ s/^\s+//;
    }
    else
    {
      $printString = "$synset";
      $printString =~ s/\s+$//;
      $printString =~ s/^\s+//;
    }
  }
  print "$printString";
}

# Subroutine to show minimal help.
sub showUsage
{
  print "Usage: similarity.pl [{--type TYPE [--config CONFIGFILE] [--allsenses] [--offsets]";
  print " [--trace] [--wnpath PATH] [--simpath SIMPATH] {--interact | --file FILENAME | WORD1 WORD2}\n";
  print "                     |--help \n";
  print "                     |--version }]\n";
}

# Subroutine to show detailed help.
sub showHelp
{
  &showUsage;
  print "\nDisplays the semantic similarity between the base forms of WORD1 and\n";
  print "WORD2 using various similarity measures described in Budanitsky Hirst\n";
  print "(2001). The parts of speech of WORD1 and/or WORD2 can be restricted\n";
  print "by appending the part of speech (n, v, a, r) to the word.\n";
  print "(For eg. car#n will consider only the noun forms of the word 'car' and\n";
  print "walk#nv will consider the verb and noun forms of 'walk').\n";
  print "Individual senses of can also be given as input, in the form of\n";
  print "word#pos#sense strings (For eg., car#n#1 represents the first sense of\n";
  print "the noun 'car').\n\n";
  print "Options:\n";
  print "--type        Switch to select the type of similarity measure\n";
  print "              to be used while calculating the semantic\n";
  print "              relatedness. The following strings are defined.\n";
  print "               'WordNet::Similarity::path'   Simple edge-counts (inverted).\n";
  print "               'WordNet::Similarity::hso'    The Hirst St. Onge measure.\n";
  print "               'WordNet::Similarity::lch'    The Leacock Chodorow measure.\n";
  print "               'WordNet::Similarity::lesk'   Extended Gloss Overlaps measure.\n";
  print "               'WordNet::Similarity::lin'    The Lin measure.\n";
  print "               'WordNet::Similarity::jcn'    The Jiang Conrath measure.\n";
  print "               'WordNet::Similarity::random' A random measure.\n";
  print "               'WordNet::Similarity::res'    The Resnik measure.\n";
  print "               'WordNet::Similarity::vector_pairs' Gloss Vector overlap measure.\n";
  print "               'WordNet::Similarity::wup'    Wu Palmer measure.\n";
  print "--config      Module-specific configuration file CONFIGFILE. This file\n";
  print "              contains the configuration that is used by the\n";
  print "              WordNet::Similarity modules during initialization. The format\n";
  print "              of this file is specific to each modules and is specified in\n";
  print "              the module man pages and in the documentation of the\n";
  print "              WordNet::Similarity package.\n";
  print "--allsenses   Displays the relatedness between every sense pair of the\n";
  print "              two input words WORD1 and WORD2.\n";
  print "--offsets     Displays all synsets (in the output, including traces) as\n";
  print "              synset offsets and part of speech, instead of the \n";
  print "              word#partOfSpeech#senseNumber format used by QueryData.\n";
  print "              With this option any WordNet synset is displayed as \n";
  print "              word#partOfSpeech#synsetOffset in the output.\n";
  print "--trace       Switches on 'Trace' mode. Displays as output on STDOUT,\n";
  print "              the various stages of the processing. This option overrides\n";
  print "              the trace option in the module configuration file (if\n";
  print "              specified).\n";
  print "--interact    Starts the interactive mode. Useful for demoes, for debugging\n";
  print "              and to play around with the measures.\n";
  print "--file        Allows the user to specify an input file FILENAME\n";
  print "              containing pairs of word whose semantic similarity needs\n";
  print "              to be measured. The file is assumed to be a plain text\n";
  print "              file with pairs of words separated by newlines, and the\n";
  print "              words of each pair separated by a space.\n";
  print "--wnpath      Option to specify the path of the WordNet data files\n";
  print "              as PATH. (Defaults to /usr/local/WordNet-3.0/dict on Unix\n";
  print "              systems and C:\\WordNet\\3.0\\dict on Windows systems)\n";
  print "--simpath     If the relatedness module to be used, is locally installed,\n";
  print "              then SIMPATH can be used to indicate the location of the local\n";
  print "              install of the measure.\n";
  print "--help        Displays this help screen.\n";
  print "--version     Displays version information.\n\n";
  print "\nNOTE: The environment variables WNHOME and WNSEARCHDIR, if present,\n";
  print "are used to determine the location of the WordNet data files.\n";
  print "Use '--wnpath' to override this.\n\n";
  print "ANOTHER NOTE: During any given session, only one of three modes of input\n";
  print "can be specified to the program -- command-line input (WORD1 WORD2), file\n";
  print "input (--file option) or the interactive input (--interact option). If more\n";
  print "than one mode of input is invoked at a given time, only one of those modes\n";
  print "will work, according to the following levels of priority:\n";
  print "  interactive mode (--interact option) has highest priority.\n";
  print "  file input (--file option) has medium priority.\n";
  print "  command-line input (WORD1 WORD2) has lowest priority.\n";
}

# Subroutine to display version information.
sub showVersion
{
  print "similarity.pl  version 2.05\n";
  print "WordNet::Similarity version ".($WordNet::Similarity::VERSION)."\n";

  # 12/8/2003 JM (#3)
  # Print version of module if the --type option was given.
  if ($opt_type)
  {
    my $module = $opt_type;
    $module =~ s/::/\//g;
    $module .= '.pm';
    require $module;
    print "$opt_type  version ".$opt_type->VERSION()."\n";
  }
  print "Copyright (c) 2005-2008, Ted Pedersen, Siddharth Patwardhan, Satanjeev Banerjee and Jason Michelizzi.\n";
}

__END__

=head1 NAME

similarity.pl - Command line interface to WordNet::Similarity

=head1 SYNOPSIS

similarity.pl [--type=TYPE [--config=CONFIGFILE] [--allsense] [--offsets] [--trace] 
              [--wnpath=PATH] [--simpath=SIMPATH] {--interact | --file=FILENAME | WORD1 WORD2}
              | --help
              | --version]

=head1 DESCRIPTION

This program is a command line interface to the WordNet::Similarity
package, which is an implementation of semantic relatedness measures   
between words. This project began in an effort to replicate the measures 
described in Budanitsky and Hirst (1995) "Semantic distance in WordNet:  
An Experimental, application-oriented evaluation of five measures", and
has since grown to include additional measures.  The measures described 
and implemented are as follows (those included in Budanitksy and Hirst's
work are denoted with a *):

   (1) Leacock and Chodorow (1998) *
   (2) Jiang and Conrath (1997) *
   (3) Resnik (1995) *
   (4) Lin (1998) *
   (5) Hirst St-Onge (1998) *
   (6) Wu and Palmer (1994)
   (7) Extended Gloss Overlaps (Banerjee & Pedersen, 2003)
   (8) Edge Counting 
   (9) Gloss Vector (Patwardhan, 2003)
   (10) Random

=head1 OPTIONS

B<--type>=I<type>
    the type of similarity measure.  Valid values are

    WordNet::Similarity::path - simple edge counting
    WordNet::Similarity::hso - Hirst & St-Onge (1998)
    WordNet::Similarity::lch - Leacock & Chodorow (1998)
    WordNet::Similarity::lesk - Extended Gloss Overlaps (Pedersen & Banerjee 2003)
    WordNet::Similarity::lin - Lin (1998)
    WordNet::Similarity::jcn - Jiang & Conrath (1997)
    WordNet::Similarity::random - returns random numbers
    WordNet::Similarity::res - Resnik (1995)
    WordNet::Similarity::vector - Gloss Vector (Patwardhan 2003)
    WordNet::Similarity::wup - Wu & Palmer (1994)

B<--config>=I<configfile>
    the path to a module-specific configuration file

B<--allsenses>
    Show the relatedness between every sense of the two input words

B<--offsets>
    show all synsets as offsets and a part-of-speech letter

B<--trace>
    switches on "Trace" mode.  Output goes to stdout.

B<--interace>
    starts the interactive mode (experimental)

B<--file>=I<filename>
    input words are read from I<filename>.  This file must contain a pair
    of words on each line.  Comments are allowed: anything following //
    on a line is ignored.

B<--wnpath>=I<path>
    looks for WordNet in I<path>. Usual values are
    /usr/local/WordNet/3.0/dict and C:\WordNet\3.0\dict.

B<--simpath>=I<path>
    look the relatedness module in I<path>.  This is useful if
    the module is locally installed.

B<--help>
    show a detailed help message

B<--version>
    show version information

=head1 BUGS

Report to WordNet::Similarity mailing list :
 L<http://groups.yahoo.com/group/wn-similarity>

=head1 SEE ALSO

L<WordNet::Similarity>

WordNet home page : 
 L<http://wordnet.princeton.edu>

WordNet::Similarity home page :
 L<http://wn-similarity.sourceforge.net>

=head1 AUTHORS

 Ted Pedersen, University of Minnesota Duluth
 tpederse at d.umn.edu

 Siddharth Patwardhan, University of Utah, Salt Lake City
 sidd at cs.utah.edu

 Satanjeev Banerjee, Carnegie Mellon University, Pittsburgh
 banerjee+ at cs.cmu.edu

 Jason Michelizzi

=head1 COPYRIGHT

Copyright (c) 2005-2008, Ted Pedersen, Siddharth Patwardhan, Satanjeev
Banerjee and Jason Michelizzi

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2 of the License, or (at your
option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

=cut
