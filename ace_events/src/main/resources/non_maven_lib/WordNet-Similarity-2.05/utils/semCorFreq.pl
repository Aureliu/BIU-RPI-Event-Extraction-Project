#! /usr/bin/perl -w
#
# semCorFreq.pl version 2.05
# (Last updated $Id: semCorFreq.pl,v 1.13 2008/05/30 23:12:44 sidz1979 Exp $)
#
# -----------------------------------------------------------------

# Include other packages
use strict;
use WordNet::QueryData;
use WordNet::Tools;
use Getopt::Long;
use WordNet::Similarity::FrequencyCounter;

# Global Variable declaration.
my %offsetMnem;
my %mnemFreq;
my %offsetFreq;

# Get Command-Line options.
our ($opt_help, $opt_version, $opt_wnpath, $opt_outfile, $opt_smooth);
&GetOptions("help", "version", "wnpath=s", "outfile=s", "smooth=s");

# Check if help has been requested ... If so ... display help.
if(defined $opt_help)
{
  &showHelp;
  exit;
}

# Check if version number has been requested ... If so ... display version.
if(defined $opt_version)
{
  &showVersion;
  exit;
}

# Check if path to WordNet Data files has been provided ... If so ... save it.
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

# Output file must be specified
unless(defined $opt_outfile)
{
  &showUsage;
  print "Type 'semCorFreq.pl --help' for detailed help.\n";
  exit;
}

# Initialize POS Map.
my %posMap;
$posMap{"1"} = "n";
$posMap{"2"} = "v";

# Get a WordNet::QueryData object...
print STDERR "Loading WordNet ... ";
my $wn = ((defined $opt_wnpath) ? (WordNet::QueryData->new($opt_wnpath)) : (WordNet::QueryData->new()));
die "Unable to create WordNet::QueryData object.\n" if(!$wn);
$wnPCPath = $wnUnixPath = $wn->dataPath() if($wn->can('dataPath'));
my $wntools = WordNet::Tools->new($wn);
die "Unable to create WordNet::Tools object.\n" if(!$wntools);
print STDERR "done.\n";

# Loading the Sense Indices.
print STDERR "Loading sense indices ... ";
open(IDX, $wnUnixPath."/index.sense") || open(IDX, $wnPCPath."\\sense.idx") || die "Unable to open sense index file.\n";
while(<IDX>)
{
  chomp;
  my @line = split / +/;
  if($line[0] =~ /%([12]):/)
  {
    my $posHere = $1;
    $line[1] =~ s/^0*//;
    push @{$offsetMnem{$line[1].$posMap{$posHere}}}, $line[0];
  }
}
close(IDX);
print STDERR "done.\n";

# Loading the frequency counts from 'cntlist'.
print STDERR "Loading cntlist ... ";
open(CNT, $wnUnixPath."/cntlist") || open(CNT, $wnPCPath."\\cntlist") || die "Unable to open cntlist.\n";
while(<CNT>)
{
  chomp;
  my @line = split / /;
  if($line[1] =~ /%[12]:/)
  {
    $mnemFreq{$line[1]}=$line[0];
  }
}
close(CNT);
print STDERR "done.\n";

# Mapping the frequency counts to offsets...
print STDERR "Mapping offsets to frequencies ... ";
my $unknownSmooth = 0;
foreach my $tPos ("noun", "verb")
{
  my $xPos = $tPos;
  my $line;
  $xPos =~ s/(^[nv]).*/$1/;
  open(DATA, $wnUnixPath."/data.$tPos") || open(DATA, $wnPCPath."\\$tPos.dat") || die "Unable to open data file.\n";
  while($line=<DATA>)
  {
    next if "  " eq substr $line, 0, 2;
    $line =~ /^([0-9]+)\s+/;
    my $offset = $1;
    $offset =~ s/^0*//;
    if(exists $offsetMnem{$offset."$xPos"})
    {
      foreach my $mnem (@{$offsetMnem{$offset."$xPos"}})
      {
        if($offsetFreq{"$xPos"}{$offset})
        {
          $offsetFreq{"$xPos"}{$offset} += ($mnemFreq{$mnem}) ? $mnemFreq{$mnem} : 0;
        }
        else
        {

          # [old]
          # Using initial value of 1 for add-1 smoothing. (added 06/22/2002)
          # $offsetFreq{$offset} = ($mnemFreq{$mnem}) ? $mnemFreq{$mnem} : 0;
          # [/old]
          # No more add-1 (09/13/2002)
          # Option for add-1 ! (05/01/2003)
          $offsetFreq{"$xPos"}{$offset} = ($mnemFreq{$mnem}) ? $mnemFreq{$mnem} : 0;
          if(defined $opt_smooth)
          {
            if($opt_smooth eq 'ADD1')
            {
              $offsetFreq{"$xPos"}{$offset}++;
            }
            else
            {
              $unknownSmooth = 1;
            }
          }
        }
      }
    }
    else
    {

      # Code added for Add-1 smoothing (06/22/2002)
      # Code changed... no more add-1 (09/13/2002)
      # Code changed... option for add-1 (05/01/2003)
      $offsetFreq{"$xPos"}{$offset} = 0;
      if(defined $opt_smooth)
      {
        if($opt_smooth eq 'ADD1')
        {
          $offsetFreq{"$xPos"}{$offset}++;
        }
        else
        {
          $unknownSmooth = 1;
        }
      }
    }
  }
  close(DATA);
}
print STDERR "done.\n";
print "Unknown smoothing scheme '$opt_smooth'.\nContinuing without smoothing.\n" if($unknownSmooth);

# Removing unwanted data structures...
print STDERR "Cleaning junk from memory ... ";
undef %offsetMnem;
undef %mnemFreq;
print STDERR "done.\n";

# Determine the topmost nodes of all hierarchies...
print STDERR "Determining topmost nodes of all hierarchies ... ";
my $topHash = WordNet::Similarity::FrequencyCounter::createTopHash($wn);
print STDERR "done.\n";

# Propagate the frequencies up...
print STDERR "Propagating frequencies up through WordNet ... ";
my $newFreq = WordNet::Similarity::FrequencyCounter::propagateFrequency(\%offsetFreq, $wn, $topHash);
print STDERR "done.\n";

# Write out the information content file...
print STDERR "Writing infocontent file ... ";
open(DATA, ">$opt_outfile") || die "Unable to open data file for writing.\n";
print DATA "wnver::".$wntools->hashCode()."\n";
foreach my $offset (sort {$a <=> $b} keys %{$newFreq->{"n"}})
{
  print DATA $offset."n ".$newFreq->{"n"}->{$offset};
  print DATA " ROOT" if($topHash->{"n"}->{$offset});
  print DATA "\n";
}
foreach my $offset (sort {$a <=> $b} keys %{$newFreq->{"v"}})
{
  print DATA $offset."v ".$newFreq->{"v"}->{$offset};
  print DATA " ROOT" if($topHash->{"v"}->{$offset});
  print DATA "\n";
}
close(DATA);
print STDERR "done.\n";
print STDERR "Wrote file '$opt_outfile'.\n";

# ---------------------- Subroutines start here -------------------------
# Subroutine to display Usage
sub showUsage
{
  print "Usage: semCorFreq.pl [{ --outfile FILE [--wnpath PATH] [--smooth SCHEME] | --help | --version }]\n";
}

# Subroutine to show detailed help.
sub showHelp
{
  &showUsage;
  print "\nA helper tool Perl program for WordNet::Similarity.\n";
  print "This program is used to generate the frequency count data\n";
  print "files which are used by the Jiang Conrath, Resnik and Lin\n";
  print "measures to calculate the information content of synsets in\n";
  print "WordNet.\n";
  print "\nOptions:\n";
  print "--outfile     Name of the output file (FILE) to write out the\n";
  print "              information content data to.\n";
  print "--wnpath      Option to specify the path to the WordNet data\n";
  print "              files as PATH.\n";
  print "--smooth      Specifies the smoothing to be used on the\n";
  print "              probabilities computed. SCHEME specifies the type\n";
  print "              of smoothing to perform. It is a string, which can be\n";
  print "              only be 'ADD1' as of now. Other smoothing schemes\n";
  print "              will be added in future releases.\n";
  print "--help        Displays this help screen.\n";
  print "--version     Displays version information.\n";
}

# Subroutine to display version information.
sub showVersion
{
  print "semCorFreq.pl version 2.05\n";
  print "Copyright (c) 2005-2008, Ted Pedersen and Siddharth Patwardhan.\n";
}

1;

__END__

=head1 NAME

semCorFreq.pl - Compute Information Content from SemCor sense-tagged corpus 

=head1 SYNOPSIS

 semCorFreq.pl [{ --outfile FILE [--wnpath PATH] [--smooth SCHEME] 
	| --help | --version }]

=head1 DESCRIPTION

This program is used to generate the default information
content file (ic-semcor.dat) that is used by 
WordNet::Similarity in the Jiang Conrath, Resnik and Lin 
measures. 

It uses the cntlist file as provided by WordNet as the 
source of frequency counts. These are derived from sense tagged
corpora which include a portion of the Brown Corpus and
the Red Badge of Courage. This collection of of sense tagged
text is referred to as SemCor, and is not distributed by
WordNet any longer. Also, note that the cntlist file is 
no longer officially supported by WordNet, so the information 
provided therein may not be reliable. 

The SemCor data we use comes from Rada Mihalcea, who has
mapped SemCor from its original version to each successive
version of WordNet. In this program we ignore the
SenseTags and simply treat SemCor as raw text. This is
to allow for the comparison of the effect of counting
from sense tags (as done in L<semCorFreq.pl>) versus
raw or plain word forms (as done here).

=head1 OPTIONS

B<--outfile>=I<filename>

    The name of a file to which output should be written

B<--wnpath>=I<path>

    Location of the WordNet data files (e.g.,
    /usr/local/WordNet-3.0/dict)

B<--smooth>=I<SCHEME>

    Smoothing should used on the probabilities computed.  SCHEME can
    only be ADD1 at this time

B<--help>

    Show a help message

B<--version>

    Display version information

=head1 BUGS

Report to WordNet::Similarity mailing list :
 L<http://groups.yahoo.com/group/wn-similarity>

=head1 SEE ALSO

L<WordNet::Similarity>

SemCor Download (from Rada Mihalcea):
 L<http://www.cs.unt.edu/~rada/downloads.html#semcor>

WordNet home page : 
 L<http://wordnet.princeton.edu>

WordNet::Similarity home page :
 L<http://wn-similarity.sourceforge.net>

=head1 AUTHORS

 Ted Pedersen, University of Minnesota, Duluth
 tpederse at d.umn.edu

 Siddharth Patwardhan, University of Utah, Salt Lake City
 sidd at cs.utah.edu

=head1 COPYRIGHT

Copyright (c) 2005-2008, Ted Pedersen and Siddharth Patwardhan

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

