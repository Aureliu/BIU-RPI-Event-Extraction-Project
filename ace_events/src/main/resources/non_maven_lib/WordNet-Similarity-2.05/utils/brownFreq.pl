#! /usr/bin/perl -w
#
# brownFreq.pl version 2.05
# (Last updated $Id: brownFreq.pl,v 1.18 2008/05/30 23:12:43 sidz1979 Exp $)
#
# -----------------------------------------------------------------------------

# Some modules used
use strict;
use Getopt::Long;
use WordNet::QueryData;
use WordNet::Tools;
use WordNet::Similarity::FrequencyCounter;

# Variable declarations
my %stopWords;
my %offsetFreq;

# First check if no commandline options have been provided... in which case
# print out the usage notes!
if ($#ARGV == -1)
{
  &minimalUsageNotes();
  exit;
}

# Now get the options!
our ($opt_version, $opt_help, $opt_stopfile, $opt_outfile, $opt_wnpath, $opt_resnik, $opt_smooth);
&GetOptions("version", "help", "stopfile=s", "outfile=s", "wnpath=s", "resnik", "smooth=s");

# If the version information has been requested
if(defined $opt_version)
{
  &printVersion();
  exit;
}

# If detailed help has been requested
if(defined $opt_help)
{
  &printHelp();
  exit;
}

# Output file must be specified
unless(defined $opt_outfile)
{
  &minimalUsageNotes();
  exit;
}

# Get the path to WordNet...
my ($wnPCPath, $wnUnixPath);
if(defined $opt_wnpath)
{
  $wnPCPath = $opt_wnpath;
  $wnUnixPath = $opt_wnpath;
}
elsif (defined $ENV{WNSEARCHDIR})
{
  $wnPCPath = $ENV{WNSEARCHDIR};
  $wnUnixPath = $ENV{WNSEARCHDIR};
}
elsif (defined $ENV{WNHOME})
{
  $wnPCPath = $ENV{WNHOME} . "\\dict";
  $wnUnixPath = $ENV{WNHOME} . "/dict";
}
else
{
  $wnPCPath = "C:\\Program Files\\WordNet\\3.0\\dict";
  $wnUnixPath = "/usr/local/WordNet-3.0/dict";
}

# Load the stop words if specified
if(defined $opt_stopfile)
{
  print STDERR "Loading stoplist... ";
  open(WORDS, "$opt_stopfile") || die("Couldnt open $opt_stopfile.\n");
  while (<WORDS>)
  {
    s/[\r\f\n]//g;
    $stopWords{$_} = 1;
  }
  close WORDS;
  print STDERR "done.\n";
}

# Load up WordNet
print STDERR "Loading WordNet... ";
my $wn=(defined $opt_wnpath)? (WordNet::QueryData->new($opt_wnpath)):(WordNet::QueryData->new());
die "Unable to create WordNet::QueryData object.\n" if(!$wn);
$wnPCPath = $wnUnixPath = $wn->dataPath() if($wn->can('dataPath'));
my $wntools = WordNet::Tools->new($wn);
die "Unable to create WordNet::Tools object.\n" if(!$wntools);
print STDERR "done.\n";

# Load the topmost nodes of the hierarchies
print STDERR "Loading topmost nodes of the hierarchies... ";
my $topHash = WordNet::Similarity::FrequencyCounter::createTopHash($wn);
print STDERR "done.\n";

# Read the input, form sentences and process each
print STDERR "Computing frequencies... ";
my $sentence = "";
my $line = "";
while($line=<>)
{
  $line=~s/[\r\f\n]//g;
  $line=~s/^[A-Z]+[0-9]+ [0-9]+ //;
  my @parts = split(/[.?!]/, $line);
  foreach (1..$#parts)
  {
    $sentence .= shift(@parts)." ";
    &process($sentence);
    $sentence = "";
  }
  $sentence .= shift(@parts)." " if(@parts);
}
&process($sentence);
print STDERR "done.\n";

# Smoothing!
if(defined $opt_smooth)
{
  print STDERR "Smoothing... ";
  if($opt_smooth eq 'ADD1')
  {
    foreach my $pos ("noun", "verb")
    {
      my $localpos = $pos;
      if(!open(IDX, $wnUnixPath."/data.$pos"))
      {
        if(!open(IDX, $wnPCPath."/$pos.dat"))
        {
          print STDERR "Unable to open WordNet data files.\n";
          exit;
        }
      }
      $localpos =~ s/(^[nv]).*/$1/;
      while(<IDX>)
      {
        last if(/^\S/);
      }
      my ($offset) = split(/\s+/, $_, 2);
      $offset =~ s/^0*//;
      $offsetFreq{$localpos}{$offset}++;
      while(<IDX>)
      {
        ($offset) = split(/\s+/, $_, 2);
        $offset =~ s/^0*//;
        $offsetFreq{$localpos}{$offset}++;
      }
      close(IDX);
    }
    print STDERR "done.\n";
  }
  else
  {
    print STDERR "\nWarning: Unknown smoothing '$opt_smooth'.\n";
    print STDERR "Use --help for details.\n";
    print STDERR "Continuing without smoothing.\n";
  }
}

# Propagating frequencies up the WordNet hierarchies...
print STDERR "Propagating frequencies up through WordNet... ";
my $newFreq = WordNet::Similarity::FrequencyCounter::propagateFrequency(\%offsetFreq, $wn, $topHash);
print STDERR "done.\n";

# Print the output to file
print STDERR "Writing output file... ";
open(OUT, ">$opt_outfile") || die "Unable to open $opt_outfile for writing.\n";
print OUT "wnver::".$wntools->hashCode()."\n";
foreach my $pos ("n", "v")
{
  foreach my $offset (sort {$a <=> $b} keys %{$newFreq->{$pos}})
  {
    print OUT "$offset$pos $newFreq->{$pos}->{$offset}";
    print OUT " ROOT" if($topHash->{$pos}->{$offset});
    print OUT "\n";
  }
}
close(OUT);
print "done.\n";

# ----------------- Subroutines start Here ----------------------
# Processing of each sentence
# (1) Convert to lowercase
# (2) Remove all unwanted characters
# (3) Combine all consequetive occurrence of numbers into one
# (4) Remove leading and trailing spaces
# (5) Form all possible compounds in the words
# (6) Get the frequency counts
sub process
{
  my $block;
  $block = lc(shift);
  $block =~ s/\'//g;
  $block =~ s/[^a-z0-9]+/ /g;
  while($block =~ s/([0-9]+)\s+([0-9]+)/$1$2/g){}
  $block =~ s/^\s+//;
  $block =~ s/\s+$//;
  $block = $wntools->compoundify($block);

  while($block =~ /([\w_]+)/g)
  {
    WordNet::Similarity::FrequencyCounter::updateWordFrequency($1, \%offsetFreq, $wn, $opt_resnik) if(!defined $stopWords{$1});
  }
}

# Subroutine to print detailed help
sub printHelp
{
  &printUsage();
  print "\nThis program computes the information content of concepts, by\n";
  print "counting the frequency of their occurrence in the Brown Corpus.\n";
  print "Options: \n";
  print "--outfile        Specifies the output file OUTFILE.\n";
  print "--stopfile       STOPFILE is a list of stop listed words that will\n";
  print "                 not be considered in the frequency count.\n";
  print "--wnpath         Option to specify WNPATH as the location of WordNet data\n";
  print "                 files. If this option is not specified, the program tries\n";
  print "                 to determine the path to the WordNet data files using the\n";
  print "                 WNHOME environment variable.\n";
  print "--resnik         Option to specify that the frequency counting should\n";
  print "                 be performed according to the method described by\n";
  print "                 Resnik (1995).\n";
  print "--smooth         Specifies the smoothing to be used on the probabilities\n";
  print "                 computed. SCHEME specifies the type of smoothing to\n";
  print "                 perform. It is a string, which can be only be 'ADD1'\n";
  print "                 as of now. Other smoothing schemes will be added in\n";
  print "                 future releases.\n";
  print "--help           Displays this help screen.\n";
  print "--version        Displays version information.\n\n";
}

# Subroutine to print minimal usage notes
sub minimalUsageNotes
{
  &printUsage();
  print "Type brownFreq.pl --help for detailed help.\n";
}

# Subroutine that prints the usage
sub printUsage
{
  print "brownFreq.pl [{--outfile OUTFILE [--stopfile STOPFILE]";
  print " [--wnpath WNPATH] [--resnik] [--smooth SCHEME] [FILE...] | --help | --version }]\n";
}

# Subroutine to print the version information
sub printVersion
{
  print "brownFreq.pl version 2.05\n";
  print "Copyright (c) 2005-2008, Ted Pedersen, Satanjeev Banerjee and Siddharth Patwardhan.\n";
}
__END__

=head1 NAME

brownFreq.pl - Compute Information Content from the Brown Corpus

=head1 SYNOPSIS

 brownFreq.pl [--outfile=OUTFILE [--stopfile=STOPFILE] 
	 [--wnpath=WNPATH] [--resnik] [--smooth=SCHEME] FILES 
	| --help --version]

=head1 DESCRIPTION 

This program reads the Brown Corpus and computes the frequency counts
for each synset in WordNet. These frequency counts are used by 
various measures of semantic relatedness to calculate the information 
content values of concepts. The output is generated in a format as 
required by the WordNet::Similarity modules for computing semantic 
relatedness.

There are various forms of the Brown corpus available. The one we 
use for this program comes from the ICAME Collection of English
Language Corpora,  Second Edition, 1999. 
L<http://www.hit.uib.no/icame/cd> 

A more detailed description of how information content is calculated can
be found in L<rawtextFreq.pl>. This program uses exactly the same
techniques as described there.

=head1 OPTIONS

B<--outfile>=I<filename>

    The name of a file to which output should be written

B<--stopfile>=I<filename>

    A file containing a list of stop listed words that will not be
    considered in the frequency counts.  A sample file can be down-
    loaded from
    http://www.d.umn.edu/~tpederse/Group01/WordNet/words.txt

B<--wnpath>=I<path>

    Location of the WordNet data files (e.g.,
    /usr/local/WordNet-3.0/dict)

B<--resnik>

    Use Resnik (1995) frequency counting

B<--smooth>=I<SCHEME>

    Smoothing should used on the probabilities computed.  SCHEME can
    only be ADD1 at this time

B<--help>

    Show a help message

B<--version>

    Display version information

B<FILES>

    Files for the Brown Corpus (e.g., /home/sid/Brown/BROWN1/*.TXT).

=head1 BUGS

Report to WordNet::Similarity mailing list :
 L<http://groups.yahoo.com/group/wn-similarity>

=head1 SEE ALSO

L<WordNet::Similarity>

ICAME Corpus : 
 L<http://www.hit.uib.no/icame/cd> 

WordNet home page : 
 L<http://wordnet.princeton.edu>

WordNet::Similarity home page :
 L<http://wn-similarity.sourceforge.net>

=head1 AUTHORS

 Ted Pedersen, University of Minnesota, Duluth
 tpederse at d.umn.edu

 Satanjeev Banerjee, Carnegie Mellon University, Pittsburgh
 banerjee+ at cs.cmu.edu

 Siddharth Patwardhan, University of Utah, Salt Lake City
 sidd at cs.utah.edu

=head1 COPYRIGHT

Copyright (c) 2005-2008, Ted Pedersen, Satanjeev Banerjee, and 
Siddharth Patwardhan

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

=cut
