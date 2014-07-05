#! /usr/bin/perl -w
#
# compounds.pl version 2.05
# (Last updated $Id: compounds.pl,v 1.13 2008/05/30 23:12:44 sidz1979 Exp $)
#
# -----------------------------------------------------------------------------

use strict;
use Getopt::Long;

# Now get the options!
our ($opt_version, $opt_help, $opt_wnpath);
&GetOptions("version", "help", "wnpath=s");

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

open(NIDX, $wnUnixPath."/index.noun") || open(NIDX, $wnPCPath."\\noun.idx") || die "Unable to open index file.\n";
open(VIDX, $wnUnixPath."/index.verb") || open(VIDX, $wnPCPath."\\verb.idx") || die "Unable to open index file.\n";
open(AIDX, $wnUnixPath."/index.adj") || open(AIDX, $wnPCPath."\\adj.idx") || die "Unable to open index file.\n";
open(RIDX, $wnUnixPath."/index.adv") || open(RIDX, $wnPCPath."\\adv.idx") || die "Unable to open index file.\n";
my $line = "";
while($line = <NIDX>)
{
  next if "  " eq substr $line, 0, 2;
  $line =~ s/[\r\f\n]//g;
  $line =~ s/^\s+//;
  $line =~ s/\s+$//;
  my ($word) = split(/\s+/, $line, 2);
  print "$word\n" if($word =~ /_/);
}
while($line = <VIDX>)
{
  next if "  " eq substr $line, 0, 2;
  $line =~ s/[\r\f\n]//g;
  $line =~ s/^\s+//;
  $line =~ s/\s+$//;
  my ($word) = split(/\s+/, $line, 2);
  print "$word\n" if($word =~ /_/);
}
while($line = <AIDX>)
{
  next if "  " eq substr $line, 0, 2;
  $line =~ s/[\r\f\n]//g;
  $line =~ s/^\s+//;
  $line =~ s/\s+$//;
  my ($word) = split(/\s+/, $line, 2);
  print "$word\n" if($word =~ /_/);
}
while($line = <RIDX>)
{
  next if "  " eq substr $line, 0, 2;
  $line =~ s/[\r\f\n]//g;
  $line =~ s/^\s+//;
  $line =~ s/\s+$//;
  my ($word) = split(/\s+/, $line, 2);
  print "$word\n" if($word =~ /_/);
}
close(NIDX);
close(VIDX);
close(AIDX);
close(RIDX);

# Subroutine to print detailed help
sub printHelp
{
  &printUsage();
  print "\nThis program generates a list of all compound words found\n";
  print "in WordNet\n";
  print "Options: \n";
  print "--wnpath         WNPATH specifies the path of the WordNet data files.\n";
  print "                 Ordinarily, this path is determined from the \$WNHOME\n";
  print "                 environment variable. But this option overides this\n";
  print "                 behavior.\n";
  print "--help           Displays this help screen.\n";
  print "--version        Displays version information.\n\n";
}

# Subroutine to print minimal usage notes
sub minimalUsageNotes
{
  &printUsage();
  print "Type compounds.pl --help for detailed help.\n";
}

# Subroutine that prints the usage
sub printUsage
{
  print "compounds.pl [{ --wnpath WNPATH | --help | --version }]\n";
}

# Subroutine to print the version information
sub printVersion
{
  print "compounds.pl version 2.05\n";
  print "Copyright (c) 2005-2008, Ted Pedersen, Satanjeev Banerjee, Siddharth Patwardhan and Jason Michelizzi.\n";
}

__END__

=head1 NAME

compounds.pl - extract compound words (collocations) from WordNet

=head1 SYNOPSIS

 compounds.pl [--wnpath=PATH | --help | --version]

=head1 DESCRIPTION

B<compounds.pl> extracts compound words (collocations) from WordNet
and writes the resultant list to the standard output.

=head1 OPTIONS

B<--wnpath>=I<path>

    Location of the WordNet data files (e.g.,
    /usr/local/WordNet-3.0/dict)

=head1 BUGS

Report to WordNet::Similarity mailing list :
 L<http://groups.yahoo.com/group/wn-similarity>

=head1 SEE ALSO

L<WordNet::Similarity>

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
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, 
USA.

=cut

