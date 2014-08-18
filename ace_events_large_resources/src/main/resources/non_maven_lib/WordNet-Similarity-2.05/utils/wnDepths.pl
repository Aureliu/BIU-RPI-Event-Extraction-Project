#! /usr/bin/perl -w
#
# wnDepths.pl version 2.05
# (Last updated $Id: wnDepths.pl,v 1.36 2008/05/30 23:12:44 sidz1979 Exp $)
#
# ---------------------------------------------------------------------

# Use external packages
use strict;
use warnings;
use Getopt::Long;
use WordNet::QueryData;
use WordNet::Tools;
use File::Spec;

# Get the command-line arguments
our ($opt_wnpath, $opt_outfile, $opt_help, $opt_version);
our ($opt_depthfile, $opt_wps, $opt_verbose);
my $result = GetOptions("wnpath=s","outfile=s","depthfile=s","help","version","wps","verbose");

# Nothing on command-line
unless($result)
{
  showUsage();
  exit(1);
}

# Help requested
if($opt_help)
{
  showHelp();
  exit(0);
}

# Version information requested
if($opt_version)
{
  showVersion();
  exit(0);
}

# Output to STDOUT
undef($opt_outfile) if $opt_outfile and ($opt_outfile eq "-");

# Check output file
if($opt_outfile)
{
  open OUTFH, ">$opt_outfile" or die "Cannot open $opt_outfile: $!";
}
else
{
  *OUTFH = *STDOUT;
}

# Check if path to WordNet Data files has been provided ... If so ... save it.
my $wnPCPath;
my $wnUnixPath;
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

# I think the actual OS name for most versions of Windows is 'MSWin32',
# even for 64-bit Windows.  See here for why:
# http://www.perlmonks.org/index.pl?node_id=315372
my $wnpath = ($^O =~ /^MSWin/i) ? $wnPCPath : $wnUnixPath;
print STDERR "Loading WordNet::QueryData... ";
my $wn = WordNet::QueryData->new($wnpath);
unless ($wn)
{
  print STDERR ("failed.\n");
  exit(1);
}
my $wntools = WordNet::Tools->new($wn);
unless ($wntools)
{
  print STDERR ("failed.\n");
  exit(1);
}
print STDERR "done\n";

# Find top-level nodes for nouns
my %top_level;
my $datafile = File::Spec->catfile($wnpath, "data.noun");
$datafile = File::Spec->catfile($wnpath, "noun.dat") if ($^O =~ m/^MSWin/i);
open FH, "$datafile" or die "Cannot open $datafile: $!";
my $line;
while ($line = <FH>)
{
  next if substr($line, 0, 2) eq "  ";
  next if $line =~ m/\@/;
  my ($offset) = split /\s+/, $line;
  $top_level{n}->{$offset} = -1;
}
close FH;
if ($opt_verbose)
{
  print "Offsets of top-level nouns\n",join(", ", keys(%{$top_level{n}})),"\n";
  print "There are ", scalar(keys %{$top_level{n}}), " nouns\n";
}

# Find top-level nodes for verbs
$datafile = File::Spec->catfile($wnpath, "data.verb");
$datafile = File::Spec->catfile($wnpath, "verb.dat") if ($^O =~ /MSWin/i);
open FH, "$datafile" or die "Cannot open $datafile: $!";
while($line = <FH>)
{
  next if substr($line, 0, 2) eq "  ";
  next if $line =~ m/\@/;
  my ($offset) = split /\s+/, $line;
  $top_level{v}->{$offset} = -1;
}
if ($opt_verbose)
{
  print "Offsets of top-level verbs\n", join(", ", keys(%{$top_level{v}})),"\n";
  print "There are ", scalar(keys(%{$top_level{v}})), " top-level verbs.\n";
}

# Determine WordNet version
my $wnver = $wntools->hashCode();
print OUTFH "wnver::$wnver\n";

# Find the leaf nodes
my $noun_leafs_ref = findLeafs('n');
if ($opt_verbose)
{
  print "There are ", scalar(@{$noun_leafs_ref}), " noun leafs.\n";
}
my $verb_leafs_ref = findLeafs('v');
if ($opt_verbose)
{
  print "There are ", scalar(@{$verb_leafs_ref}), " verb leafs.\n";
}

# Find the depth of every taxonomy
my %wpsDepths;
print STDERR "Finding depths of noun taxonomies... ";
foreach my $offset (@{$noun_leafs_ref})
{
  my ($depth, $root_offset) = findDepth($offset, 'n');
  $root_offset = sprintf("%08d", $root_offset);
  if (!defined($top_level{n}->{$root_offset}) || $top_level{n}->{$root_offset} < $depth)
  {
    $top_level{n}->{$root_offset} = $depth;
  }
}
print STDERR "done.\n";

# Generate depth-file
my %depth;
if ($opt_depthfile)
{
  print STDERR "Writing depths to $opt_depthfile... ";
  open DFH, '>', $opt_depthfile or die "Cannot open $opt_depthfile: $!";
  print DFH "wnver::$wnver\n";
  my $noundepth = 0;
  my @keys = sort keys %wpsDepths;
  foreach my $wps (@keys)
  {
    my $depth = 100;
    for (@{$wpsDepths{$wps}})
    {
      $depth = $_->[0] if $depth > $_->[0];
    }
    $noundepth = $depth if $depth > $noundepth;
  }
  $noundepth = 2 * ($noundepth + 1) - 1;
  foreach my $key (@keys)
  {
    my %tmp;
    foreach (@{$wpsDepths{$key}})
    {
      if ($opt_wps)
      {
        $tmp{"$_->[0]:$_->[1]"} = 1;
      }
      else
      {
        my $offset = sprintf("%08d", $wn->offset($_->[1]));
        $tmp{"$_->[0]:$offset"} = 1;
      }
    }
    my @depths = sort keys %tmp;
    my $offset = sprintf("%08d", $wn->offset($key));
    my $str = $opt_wps ? "n $key " : "n $offset ";
    $str .= join(" ", @depths) . "\n";
    $depth{n}->{$offset} = $str;
  }
  print STDERR "done.\n";
}
print STDERR "Cleaning junk from memory... ";
undef %wpsDepths;
print STDERR "done.\n";

# Find the depth of every taxonomy
print STDERR "Finding depths of verb taxonomies... ";
foreach my $offset (@{$verb_leafs_ref})
{
  my ($depth, $root_offset) = findDepth($offset, 'v');
  $root_offset = sprintf("%08d", $root_offset);
  if (!defined($top_level{v}->{$root_offset}) || $top_level{v}->{$root_offset} < $depth)
  {
    $top_level{v}->{$root_offset} = $depth;
  }
}
print STDERR "done.\n";

# Generate depth-file
if ($opt_depthfile)
{
  print STDERR "Writing depths to $opt_depthfile... ";
  my $verbdepth = 0;
  my @keys = sort keys %wpsDepths;
  foreach my $wps (@keys)
  {
    my $depth = 100;
    for (@{$wpsDepths{$wps}})
    {
      $depth = $_->[0] if $depth > $_->[0];
    }
    $verbdepth = $depth if $depth > $verbdepth;
  }
  $verbdepth = 2 * ($verbdepth + 1) - 1;
  foreach my $key (@keys)
  {
    my %tmp;
    foreach (@{$wpsDepths{$key}})
    {
      if ($opt_wps)
      {
        $tmp{"$_->[0]:$_->[1]"} = 1;
      }
      else
      {
        my $offset = sprintf("%08d", $wn->offset($_->[1]));
        $tmp{"$_->[0]:$offset"} = 1;
      }
    }
    my @depths = sort keys %tmp;
    my $offset = sprintf("%08d", $wn->offset($key));
    my $str = $opt_wps ? "v $key " : "v $offset ";
    $str .= join(" ", @depths) . "\n";
    $depth{v}->{$offset} = $str;
  }
  for my $pos (qw/n v/)
  {
    foreach my $key (sort keys %{$depth{$pos}})
    {
      print DFH $depth{$pos}->{$key};
    }
  }
  print STDERR "done.\n";
  close DFH;
}
print STDERR "Cleaning junk from memory... ";
undef %wpsDepths;
print STDERR "done.\n";

# Print output
my $deepest_n = 0;
while (my ($off, $depth) = each %{$top_level{n}})
{
  $deepest_n = $depth if ($depth > $deepest_n);
  if ($opt_wps)
  {
    my $wps = $wn->getSense($off, 'n');
    print OUTFH "n $wps $depth\n";
  }
  else
  {
    print OUTFH "n $off $depth\n";
  }
}
$deepest_n++;
print OUTFH "n ", ($opt_wps ? '*Root*#n#1' : '00000000'), " $deepest_n\n";
my $deepest_v = 0;
while (my ($off, $depth) = each %{$top_level{v}})
{
  $deepest_v = $depth if ($depth > $deepest_v);
  if ($opt_wps)
  {
    my $wps = $wn->getSense($off, 'v');
    print OUTFH "v $wps $depth\n";
  }
  else
  {
    print OUTFH "v $off $depth\n";
  }
}
$deepest_v++;
print OUTFH "v ", ($opt_wps ? '*Root*#v#1' : '00000000'), " $deepest_v\n";
exit;

########## subroutines follow ##########
sub findLeafs
{
  my $pos = shift || die "No pos specifed";
  my $file;
  if ($pos eq "n")
  {
    $file = File::Spec->catfile($wnpath, "data.noun");
    $file = File::Spec->catfile($wnpath, "noun.dat") if $^O =~ /MSWin/i;
  }
  elsif ($pos eq "v")
  {
    $file = File::Spec->catfile($wnpath, "data.verb");
    $file = File::Spec->catfile($wnpath, "verb.dat") if $^O =~ /MSWin/i;
  }
  else
  {
    die "Invalid pos: $pos";
  }
  open WN, $file or die "Cannot open $file";
  my @rtr = ();
  while (my $line = <WN>)
  {
    next if index($line, " ") == 0;

    # Was: next if $line =~ m/~/;
    # Failed on tilde#n#1 because the gloss contains ~.
    # Fix provided by Ben Haskell (03/04/08).
    next if $line =~ m/~.*\|/;
    my ($offset) = split /\s+/, $line;
    push @rtr, $offset;
  }
  close WN or warn "Cannot close $file";
  return \@rtr;
}

sub findWPSDepths
{
  my $wps = shift;
  my $curPath = shift;
  defined $wpsDepths{$wps} and return @{$wpsDepths{$wps}};
  my @hypernyms = $wn->querySense($wps, "hypes");
  $curPath->{$wn->offset($wps)} = 1;
  unless (scalar @hypernyms > 0)
  {
    $wpsDepths{$wps} = [[1, $wps]];
  }
  else
  {
    my @all_paths = ();
    foreach my $hype (@hypernyms)
    {
      unless(defined($curPath->{$wn->offset($hype)}))
      {
        my %pathCopy = %{$curPath};
        push @all_paths, findWPSDepths($hype, \%pathCopy);
      }
    }
    @all_paths = map {[$_->[0] + 1, $_->[1]]} @all_paths;
    push(@all_paths, [1, $wps]) if(scalar(@all_paths) <= 0);
    $wpsDepths{$wps} = \@all_paths;
  }
  return @{$wpsDepths{$wps}};
}

sub findDepth
{
  my ($offset, $pos) = @_;
  ($offset and $pos)
    or die "Internal error: bad input to findDepth($offset, $pos)";
  my ($wps) = $wn->getSense($offset, $pos);
  $wps or die "Internal error: bad offset $offset";

  #my ($depth, $root) = findWPSDepths ($wps);
  my @paths = findWPSDepths($wps, {});
  my $mindepth = 1_000;
  my $root;
  foreach my $path (@paths)
  {
    if ($path->[0] < $mindepth)
    {
      $mindepth = $path->[0];
      $root = $path->[1];
    }
  }
  my $root_offset;
  eval {$root_offset = $wn->offset($root)};
  if ($@)
  {
    die "$@ \t $root (depth $mindepth) has no offset, baseoffset is $offset";
  }
  return ($mindepth, $wn->offset($root));
}

sub showUsage
{
  print "Usage: wnDepths.pl [[--wnpath=PATH] [--outfile=FILE] [--depthfile=FILE] [--wps] [--verbose]]\n";
  print "                    | --help | --version]\n";
}

sub showHelp
{
  showUsage();
  print "Options:\n";
  print "\t--wnpath=PATH    PATH is the path to WordNet. The default is\n";
  print "\t                 /usr/local/WordNet-3.0/dict on Unix and\n";
  print "\t                 C:\\WordNet\\3.0\\dict on Windows\n";
  print "\t--outfile=FILE   File to which the maximum depths of the taxon-\n";
  print "\t                 omies should be output.\n";
  print "\t--depthfile=FILE File to which the depth of every synset should\n";
  print "\t                 be output\n";
  print "\t--wps            output is in 'word#part_of_speech#sense format\n";
  print "\t                 instead of offset format\n";
  print "\t--verbose        be verbose\n";
  print "\t--help           show this help message\n";
  print "\t--version        show version information\n";
}

sub showVersion
{
  print "wnDepths.pl version 2.05\n";
  print "Copyright (c) 2005-2008, Ted Pedersen, Jason Michelizzi and Siddharth Patwardhan\n\n";
  print "This program comes with ABSOLUTELY NO WARRANTY.  This program\n";
  print "is free software, and you are welcome to redistribute it under\n";
  print "certain conditions.  See the file GPL.txt for warranty and\n";
  print "copyright information.\n";
}
1;
__END__

=head1 NAME

wnDepths.pl - Find the depths of WordNet taxonomies

=head1 SYNOPSIS

  wnDepths.pl [[--wnpath=PATH] [--outfile=FILE|-] 
		[--depthfile=FILE] [--wps] [--verbose]]
		| --help | --version]

=head1 DESCRIPTION

B<wnDepths.pl> finds the depths of WordNet's noun and verb taxonomies; it
can also find the depth of each synset in WordNet.  This program
was originally written for use with the WordNet::Similarity::lch
and WordNet::Similarity::wup measures of semantic similarity, but it
likely has other uses as well.

=head1 OPTIONS

B<--wnpath>=I<path>

The path to WordNet data files.  The default is /usr/local/WordNet-3.0/dict on
Unix and C:\WordNet\3.0\dict on Windows.

B<--outfile>=I<file>

The file to which the maximum depths of the taxomomies should be output.
If this option is not given, or if the file name is I<->, then output
will be sent to the standard output.

B<--depthfile>=I<file>

The file to which the depth of every noun and verb synset should be sent.

B<--wps>

The names of synsets in the output as "word#part_of_speech#sense" strings
rather than as offsets.

B<--verbose>

Be verbose.

B<--help>

Show detailed help message.

B<--version>

Show version information.

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

 Jason Michelizzi

 Siddharth Patwardhan, University of Utah, Salt Lake City
 sidd at cs.utah.edu

=head1 COPYRIGHT

Copyright (c) 2005-2008, Ted Pedersen, Jason Michelizzi and Siddharth Patwardhan

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

    The Free Software Foundation, Inc.,
    59 Temple Place - Suite 330,
    Boston, MA  02111-1307, USA.

Note: a copy of the GNU General Public License is available on the web
at L<http://www.gnu.org/licenses/gpl.txt> and is included in this
distribution as GPL.txt.

=cut
