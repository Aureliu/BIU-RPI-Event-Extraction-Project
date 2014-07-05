#! /usr/bin/perl -w
#
# rawtextFreq.pl version 2.05
# (Last updated $Id: rawtextFreq.pl,v 1.27 2008/06/03 00:04:43 sidz1979 Exp $)
#
# -----------------------------------------------------------------------------
use strict;
use File::Find;
use WordNet::Similarity::FrequencyCounter;

# Variable declarations
my %stopWords;
my %offsetFreq;

# Some modules used
use Getopt::Long;
use WordNet::QueryData;
use WordNet::Tools;

# First check if no commandline options have been provided... in which case
# print out the usage notes!
if($#ARGV == -1)
{
  &minimalUsageNotes();
  exit 1;
}
our @opt_infiles;
our ($opt_version, $opt_help, $opt_stopfile, $opt_outfile);
our ($opt_wnpath, $opt_resnik, $opt_smooth, $opt_stdin);

# Now get the options!
my $ok = GetOptions("version", "help", "stopfile=s", "outfile=s","wnpath=s", "resnik", "smooth=s", "stdin","infile=s" => \@opt_infiles);

# GetOptions should have already printed out a detail error message if
# $ok is false
$ok or die "Error getting command-line arguments\n";

# If the version information has been requested
if(defined $opt_version)
{
  &printVersion();
  exit 0;
}

# If detailed help has been requested
if(defined $opt_help)
{
  &printHelp();
  exit 0;
}

# make sure either --stdin or --infile was given
unless($opt_stdin or scalar @opt_infiles)
{
  minimalUsageNotes();
  exit 1;
}

# make sure that both --stdin and --infile were NOT given
if($opt_stdin and scalar @opt_infiles)
{
  minimalUsageNotes();
  exit 1;
}

# make sure --outfile is given
if(!defined $opt_outfile)
{
  &minimalUsageNotes();
  exit 1;
}

# Load the stop words if specified
if(defined $opt_stopfile)
{
  print STDERR "Loading stoplist... ";
  open(WORDS, '<', "$opt_stopfile") or die "Couldn't open $opt_stopfile.\n";
  while(<WORDS>)
  {
    s/[\r\f\n]//g;
    $stopWords{$_} = 1;
  }
  close WORDS;
  print STDERR "done.\n";
}

# Get the path to WordNet...
my ($wnPCPath, $wnUnixPath);
if(defined $opt_wnpath)
{
  $wnPCPath = $opt_wnpath;
  $wnUnixPath = $opt_wnpath;
}
elsif(defined $ENV{WNSEARCHDIR})
{
  $wnPCPath = $ENV{WNSEARCHDIR};
  $wnUnixPath = $ENV{WNSEARCHDIR};
}
elsif(defined $ENV{WNHOME})
{
  $wnPCPath = $ENV{WNHOME} . "\\dict";
  $wnUnixPath = $ENV{WNHOME} . "/dict";
}
else
{
  $wnPCPath = "C:\\Program Files\\WordNet\\3.0\\dict";
  $wnUnixPath = "/usr/local/WordNet-3.0/dict";
}

# Load up WordNet
print STDERR "Loading WordNet... ";
my $wn =
    (defined $opt_wnpath)
  ? (WordNet::QueryData->new($opt_wnpath))
  : (WordNet::QueryData->new());
die "Unable to create WordNet::QueryData object.\n" if(!$wn);
$wnPCPath = $wnUnixPath = $wn->dataPath() if(defined $wn->can('dataPath'));
my $wntools = WordNet::Tools->new($wn);
die "Unable to create WordNet::Tools object.\n" if(!$wntools);
print STDERR "done.\n";

# Load the topmost nodes of the hierarchies
print STDERR "Loading topmost nodes of the hierarchies... ";
my $topHash = WordNet::Similarity::FrequencyCounter::createTopHash($wn);
die "Unable to create topHash.\n" if(!defined($topHash));
print STDERR "done.\n";

# Read the input, form sentences and process each
#  We need to process each sentence (or clause really) at the same time.
#  This is because process() tries to find compounds in the sentence.  It is
#  not sufficient to process a line at a time because a sentence often
#  spans more than one line; therefore, a compound could also span a line
#  break (for example, the first word of a compound could be the last word
#  on a line, and the second word of the compound could be the first word
#  on the next line).
my $sentence = "";

# check if we're reading from a file or from STDIN
if(scalar @opt_infiles)
{
  # first we have to figure out what files to process.  The files
  # are specified with in --infile option.  The value of the option
  # can be a filename, a directory, or a pattern (as understood by
  # Perl's glob() function).
  my @infiles = getFiles(@opt_infiles);
  print STDERR "Computing frequencies...\n";
  foreach my $i (0..$#infiles)
  {
    my $infile = $infiles[$i];
    print STDERR ("  Processing '$infile' (", $i + 1, "/",$#infiles + 1, " files)... ");
    open(IFH, '<', $infile) or die "Cannot open file '$infile': $!";
    while(my $line = <IFH>)
    {
      $line =~ s/[\r\f\n]//g;
      my @parts = split(/[.?!,;:]/, $line);
      foreach (1..$#parts)
      {
        $sentence .= shift(@parts)." ";
        &process($sentence);
        $sentence = "";
      }
      $sentence .= shift(@parts)." " if(@parts);
    }
    &process($sentence);
    close IFH or die "Cannot not close file '$infile': $!";
    print STDERR "  done.\n";
  }
}
else
{
  print STDERR "Computing frequencies... ";
  while (my $line = <STDIN>)
  {
    $line =~ s/[\r\f\n]//g;
    my @parts = split(/[.?!,;:]/, $line);
    foreach (1..$#parts)
    {
      $sentence .= shift(@parts) . " ";
      &process($sentence);
      $sentence = "";
    }
    $sentence .= shift(@parts) . " " if @parts;
  }
  &process($sentence);
  print STDERR "done.\n";
}

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
open(OUT, ">$opt_outfile") || die "Unable to open $opt_outfile for writing: $!\n";
print OUT "wnver::".$wntools->hashCode()."\n";
foreach my $pos ("n", "v")
{
  foreach my $offset (sort {$a <=> $b} keys %{$newFreq->{$pos}})
  {
    print OUT "$offset$pos ".($newFreq->{$pos}->{$offset});
    print OUT " ROOT" if($topHash->{$pos}->{$offset});
    print OUT "\n";
  }
}
close(OUT);
print STDERR "done.\n";

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

sub getFiles
{
  my @inpatterns = @_;
  my @infiles;

  # the options to pass to File::Find::find()
  my %options = (
    wanted => sub {
      unless(-d $File::Find::name)
      {
        push @infiles, $File::Find::name;
      }
    },
    follow_fast => 1
  );
  foreach my $pattern (@inpatterns)
  {
    if(-d $pattern)
    {
      find(\%options, $pattern);
    }
    elsif(-e $pattern and not -d $pattern)
    {
      push @infiles, $pattern;
    }
    else
    {
      my @files = glob $pattern;
      foreach my $file (@files)
      {
        if(-d $file)
        {
          find(\%options, $pattern);
        }
        else
        {
          push @infiles, $file;
        }
      }
    }
  }
  return @infiles;
}

# Subroutine to print detailed help
sub printHelp
{
  &printUsage();
  print "\nThis program computes the information content of concepts, by\n";
  print "counting the frequency of their occurrence in raw text.\n";
  print "Options: \n";
  print "--outfile        Specifies the output file OUTFILE.\n";
  print "--stdin          Read the input from the standard input\n";
  print "--infile         INFILE is the name of an input file\n";
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
  print "Type rawtextFreq.pl --help for detailed help.\n";
}

# Subroutine that prints the usage
sub printUsage
{
  print <<'EOT';
Usage: rawtextFreq.pl --outfile OUTFILE
                       {--stdin | --infile FILE [--infile FILE ...]}
                       [--stopfile FILE] [--resnik] [--wnpath PATH]
                       [--smooth SCHEME]
                      | --help | --version
EOT
}

# Subroutine to print the version information
sub printVersion
{
  print "rawtextFreq.pl version 2.05\n";
  print "Copyright (c) 2005-2008, Ted Pedersen, Satanjeev Banerjee, Siddharth Patwardhan and Jason Michelizzi.\n";
}
__END__

=head1 NAME

rawtextFreq.pl - Compute Information Content from Raw / Plain Text

=head1 SYNOPSIS

 rawtextFreq.pl --outfile OUTFILE [--stopfile=STOPFILE]
               {--stdin | --infile FILE [--infile FILE ...]} 
		[--wnpath WNPATH] [--resnik] [--smooth=SCHEME] 
		| --help | --version

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

B<--stdin>

    Read from the standard input the text that is to be used for
    counting the frequency of words.

B<--infile>=I<PATTERN>

    The name of a raw text file to be used to count word frequencies.
    This can actually be a filename, a directory name, or a pattern (as
    understood by Perl's glob() function).  If the value is a directory
    name, then all the files in that directory and its subdirectories will
    be used.

    If you are looking for some interesting files to use, check out
    Project Gutenberg: <http://www.gutenberg.org>.

    This option may be given more than once (if more than one file
    should be used).

=head1 DESCRIPTION 

This program reads a corpus of plain text and computes frequency 
counts from that corpus and then uses those to determine the 
information content of each synset in WordNet. In brief it does this 
by first assigning counts to each synset for which it obtains
a frequency count in the corpus, and then those counts are 
propagated up the WordNet hierarchy. More details on this process
can be found in the documentation of the lin, res, and jcn measures
in L<WordNet::Similarity> and in the publication by Patwardhan, et. al. 
(2003) referred to below. 

The utility programs L<BNCFreq.pl>, L<SemCorRawFreq.pl>, 
L<treebankFreq.pl>, L<brownFreq.pl> all function in exactly the same 
way as this plain text program (rawtextFreq.pl), except that they 
include the ability to deal with the format of the corpus with which
they are used.

None of these programs requires sense-tagged text; instead they simply  
distribute the counts of the observed form of word to all the synsets 
in the corpus to which it could be associated. The different forms of a 
word are found via the validForms and querySense methods of 
L<WordNet::QueryData>. 

For example, if the observed word is 'bank', then a count is given to 
the synsets associated with the financial institution, a river shore, 
the act of turning a plane, etc. 

=head2 Distributing Counts to Synsets

If the corpora is sense-tagged, then distributing the counts of 
sense-tagged words to synsets is trivial; you increment the count of 
each synset for which you have a sense tagged instance. It is very hard 
to obtain large quantities of sense tagged text, so in general it is not 
feasible to obtain information content values from large sense-tagged 
corpora. 

As such this program and the related *Freq.pl utilities are all trying 
to increment the counts of synsets based on the occurence of raw 
untagged word forms. In this case it is less obvious how to proceed. 
This program supports two methods for distributing the counts of an 
observed word forms in untagged text to synsets. 

One is our default method, and we refer to the other as Resnik 
counting. In our default counting scheme, each synset receives 
the total count of each word form associated with it. 

Suppose the word 'bank' can be associated with six different 
synets. In our default scheme each of those synsets would receive
a count for each occurrence of 'bank'. In Resnik counting, the
count would be divided between the possible synsets, so
in this case each synset would get one sixth (1/6) of the total
count. 

=head2 How are These Counts Used? 

This program maps word forms to synsets. These synset counts are then
propagated up the WordNet hierarchy to arrive at Information Content 
values for each synset, which are then used by the Lin (lin), Resnik 
(res), and Jiang & Conrath (jcn) measures of semantic similarity. 

By default these measures use counts derived from the cntlist file
provided by WordNet, which is based on frequency counts 
from the sense-tagged SemCor corpus. This consists of approximately
200,000 sense tagged tokens taken from the Brown Corpus and 
the Red Badge of Courage. 

A file called ic-semcor.dat is created during installation of 
L<WordNet::Similarity> from cntlist. In fact, the util program 
semCorFreq.pl is used to do this. This is the only one of the *Freq.pl 
utility programs that uses sense tagged text, and in fact it only uses 
the counts from cntlist, not the actual sense tagged text. 

This program simply creates an alternative version of the ic-semcor.dat 
file based on counts obtained from raw untagged text. 

=head2 Why Use This Program?

The default information content file (ic-semcor.dat) is based on SemCor, 
which includes sense tagged portions of the Brown Corpus and the Red 
Badge of Courage. It has the advantage of being sense tagged, but is 
from a rather limited domain and is somewhat small in size (200,000 
sense tagged tokens). 

If you are working in a different domain or have access to a larger 
quantity of corpora, you might find that this program provides 
information content values that better reflect your underlying domain or 
problem. 

=head2 How can these counts be reliable if they aren't based on sense tagged text? 

Remember once the counts are given to a synset, those counts
are propogated upwards, so that each synset receives the counts of
its children. These are then used in the calculation of the information
content of each synset, which is simply :

	information content (synset) = - log [probability (synset)]

More details on this calculation and how they are used in the res,
lin, and jcn measures can be found in the WordNet::Similarity module
doumentation, and in the following publication:

 Using Measures of Semantic Relatedness for Word Sense Disambiguation 
 (Patwardhan, Banerjee and Pedersen) - Appears in the Proceedings of 
 the Fourth International Conference on Intelligent Text Processing and 
 Computational Linguistics, pp. 241-257, February 17-21, 2003, Mexico City.
 L<http://www.d.umn.edu/~tpederse/Pubs/cicling2003-3.pdf>

We believe that a propagation effect will result in concentrations or
clusters of information content values in the WordNet hierarchy. For 
example, if you have a text about banking, while the different counts of
"bank" will be dispersed around WordNet, there will also be other
financial terms that occur with bank that will occur near the financial
synset in WordNet, and lead to a concentration of counts in that
region of WordNet. It is best to view this as a conjecture or hypothesis
at this time. Evidence for or against would be most interesting.

You can use raw text of any kind in this program. We sometimes use
text from Project Gutenburg, for example the Complete Works of 
Shakespeare, available from L<http://www.gutenberg.org/ebooks/100> 

=head1 BUGS

Report to WordNet::Similarity mailing list :
 L<http://groups.yahoo.com/group/wn-similarity>

=head1 SEE ALSO

L<utils.pod>

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

 Jason Michelizzi

=head1 COPYRIGHT 

Copyright (c) 2005-2008, Ted Pedersen, Satanjeev Banerjee, Siddharth Patwardhan and Jason Michelizzi

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to

 Free Software Foundation, Inc.
 59 Temple Place - Suite 330
 Boston, MA  02111-1307, USA

=cut
