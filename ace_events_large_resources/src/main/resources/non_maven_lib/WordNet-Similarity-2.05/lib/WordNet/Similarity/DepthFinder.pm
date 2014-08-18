# WordNet::Similarity::DepthFinder version 2.04
# (Last updated $Id: DepthFinder.pm,v 1.20 2008/03/27 06:21:17 sidz1979 Exp $)
#
# Module containing code to find the depths of (noun and verb) synsets in
# the WordNet 'is-a' taxonomies
#
# Copyright (c) 2005,
#
# Ted Pedersen, University of Minnesota Duluth
# tpederse at d.umn.edu
#
# Jason Michelizzi, Univeristy of Minnesota Duluth
# mich0212 at d.umn.edu
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to 
#
# The Free Software Foundation, Inc., 
# 59 Temple Place - Suite 330, 
# Boston, MA  02111-1307, USA.
#
# ------------------------------------------------------------------

package WordNet::Similarity::DepthFinder;

=head1 NAME

WordNet::Similarity::DepthFinder - methods to find the depth of synsets in
WordNet taxonomies

=head1 SYNOPSIS

 use WordNet::QueryData;
 my $wn = WordNet::QueryData->new;
 defined $wn or die "Construction of WordNet::QueryData failed";

 use WordNet::Similarity::DepthFinder;

 my $obj = WordNet::Similarity::DepthFinder->new ($wn);
 my ($err, $errString) = $obj->getError ();
 $err and die $errString;

 my $wps1 = 'car#n#4';
 my $wps2 = 'oil#n#1';

 my $offset1 = $wn -> offset ($wps1);
 my $offset2= $wn -> offset ($wps2);

 my @roots = $obj->getTaxonomies ($offset1, 'n');
 my $taxonomy_depth = $obj->getTaxonomyDepth ($roots[0], 'n');
 print "The maximum depth of the taxonomy where $wps1 is found is $taxonomy_depth\n";

 my @depths = $obj->getSynsetDepth ($offset1, 'n');
 print "The depth of $offset1 is $depths[0]->[0]\n";

 my @lcsbyic = $obj -> getLCSbyDepth($wps1,$wps2,'n','wps');
 print "$wps1 and $wps2 have LCS $lcsbyic[0]->[0] with Depth $lcsbyic[0]->[1]\n";

 my @lcsbyic = $obj -> getLCSbyDepth($offset1,$offset2,'n','offset');
 print "$offset1 and $offset2 have LCS $lcsbyic[0]->[0] with Depth $lcsbyic[0]->[1]\n";

=head1 DESCRIPTION

The following methods are provided by this module:

=over

=cut

use strict;
use warnings;

use WordNet::Similarity::PathFinder;

our @ISA = qw/WordNet::Similarity::PathFinder/;

our $VERSION = '2.04';

WordNet::Similarity::addConfigOption ("taxonomyDepthsFile", 1, "p", undef);
WordNet::Similarity::addConfigOption ("synsetDepthsFile", 1, "p", undef);

=item $obj->initialize ($configfile)

Overrides the initialize method in WordNet::Similarity to look for and
process depths files.  The initialize method of the superclass is also called.

=cut

sub initialize
{
    my $self = shift;
    my $class = ref $self || $self;

    my $wn = $self->{wn};

    my $defaultdepths = "synsetdepths.dat";
    my $defaultroots = "treedepths.dat";

    $self->SUPER::initialize (@_);

    my $depthsfile = $self->{synsetDepthsFile};

    unless (defined $depthsfile) {
    DEPTHS_SEARCH:
	foreach (@INC) {
	    my $file = File::Spec->catfile ($_, 'WordNet', $defaultdepths);
	    if (-e $file) {
		if (-r $file) {
		    $depthsfile = $file;
		    last DEPTHS_SEARCH;
		}
		else {
		    # The file not readable--is this an error?
		    # I suppose we shouldn't punish people for having
		    # unreadable files lying around; let's do nothing.
		}
	    }
	}
    }

    unless (defined $depthsfile) {
	$self->{error} = 2;
	$self->{errorString} .= "\nError (${class}::initialize()) - ";
	$self->{errorString} .= "No depths file found.";
	return undef;
    }

    $self->_processSynsetsFile ($depthsfile) or return undef;

    my $rootsfile = $self->{treeDepthsFile};

    unless (defined $rootsfile) {
    TAXONOMY_SEARCH:
	foreach (@INC) {
	    my $file = File::Spec->catfile ($_, 'WordNet', $defaultroots);
	    if (-e $file) {
		if (-r $file) {
		    $rootsfile = $file;
		    last TAXONOMY_SEARCH;
		}
		else {
		    # The file not readable--is this an error?
		    # I suppose we shouldn't punish people for having
		    # unreadable files lying around; let's do nothing.
		}
	    }
	}
    }

    $self->_processTaxonomyFile ($rootsfile) or return undef;

    return 1;
}


=item $obj->getSynsetDepth ($offset, $pos)

Returns the depth(s) of the synset denoted by $offset and $pos.  The return
value is a list of references to arrays.  Each array has the form
S<(depth, root)>.

=cut

sub getSynsetDepth
{
    my $self = shift;
    my $class = ref $self || $self;
    my $offset = shift;
    my $pos = shift;

    my $ref = $self->{depths}->{$pos}->{$offset};
    my @depths = @$ref;


    unless (defined $depths[0]) {
	$self->{errorString} .= "\nWarning (${class}::getSynsetDepth()) - ";
	$self->{errorString} .= "No depth found for '$offset#$pos'.";
	$self->{error} = $self->{error} < 1 ? 1 : $self->{error};
	return undef;
    }

    return @depths;
}


=item $obj->getTaxonomyDepth ($offset, $pos)

Returns the maximum depth of the taxonomy rooted at the synset identified
by $offset and $pos.  If $offset and $pos does not identify a root of
a taxonomy, then undef is returned and an error is raised.

=cut

sub getTaxonomyDepth
{
    my $self = shift;
    my $class = ref $self || $self;
    my $synset = shift;
    my $pos = shift;

    my $depth = $self->{taxonomyDepths}->{$pos}->{$synset};

    unless (defined $depth) {
	$self->{error} = $self->{error} < 1 ? 1 : $self->{error};
	$self->{errorString} .= "\nWarning (${class}::getTaxonomyDepth()) - ";
	$self->{errorString} .= "No taxonomy is rooted at $synset#$pos.";
	return undef;
    }

    return $depth;
}

=item $obj->getTaxonomies ($offset, $pos)

Returns a list of the roots of the taxonomies to which the synset identified
by $offset and $pos belongs.

=cut

sub getTaxonomies
{
    my $self = shift;
    my $offset = shift;
    my $pos = shift;
    my $class = ref $self || $self;

    my $ref = $self->{depths}->{$pos}->{$offset};
    my @tmp = @$ref;
    my %tmp;
    foreach (@tmp) {
	$tmp{$_->[1]} = 1;
    }
    my @rtn = keys %tmp;
    unless (defined $rtn[0]) {
	$self->{errorString} .= "\nWarning (${class}::getTaxonomies()) - ";
	$self->{errorString} .= "No root information for $offset#$pos.";
	$self->{error} = $self->{error} < 1 ? 1 : $self->{error};
	return undef;
    }
    return @rtn;
}

=item getLCSbyDepth($synset1, $synset2, $pos, $mode)

Given two input synsets, finds the least common subsumer (LCS) of them.
If there are multiple candidates for the LCS (due to multiple inheritance
in WordNet), the LCS with the greatest depth is chosen (i.e., the candidate
whose shortest path to the root is the longest).

Parameters: a blessed reference, two synsets, a part of speech, and a mode.
The mode must the either the string 'wps' or 'offset'.  If the mode is wps,
then the two input synsets must be in word#pos#sense format.  If the mode
is offset, then the input synsets must be WordNet offsets.

Returns: a list of the form ($lcs, $depth) where $lcs is the LCS (in wps
format if mode is 'wps' or an offset if mode is 'offset'.  $depth is the
depth of the LCS in its taxonomy.  Returns undef on error.

=cut

sub getLCSbyDepth
{
  my $self = shift;
  my $synset1 = shift;
  my $synset2 = shift;
  my $pos = shift;
  my $mode = shift;
  my $class = ref $self || $self;

  my @paths = $self->getAllPaths ($synset1, $synset2, $pos, $mode);
  unless (defined $paths[0]) {
    # no paths found
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::getLCSbyDepth()) - ";
    $self->{errorString} .= "No path between synsets found.";
    return $self->UNRELATED;
  }

  my $wn = $self->{wn};
  my %depth;           # a hash to hold the depth of each LCS candidate

  # find the depth of each LCS candidate
  foreach (@paths) {
    my $offset = $_->[0];
    if ($mode eq 'wps') {
      if (index ($_->[0], "*Root*") >= $[) {
	$offset = 0;
      }
      else {
	$offset = $wn->offset ($_->[0]);
      }
    }

    my @depths = $self->getSynsetDepth ($offset, $pos);
    my ($depth, $root) = @{$depths[0]};
    unless (defined $depth) {
      # serious internal error -- possible problem with depths file?
      $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
      $self->{errorString} .= "\nWarning (${class}::getLCSbyDepth()) - ";
      $self->{errorString} .= "Undefined depth for $_->[0].  ";
      $self->{errorString} .= "Possible problem with the depths file?";
      return undef;
    }
    $depth{$_->[0]} = [$depth, $root];
  }

  # sort according to depth (descending order)
  my @tmp = sort {$b->[1] <=> $a->[1]} map [$_, @{$depth{$_}}], keys %depth;

  # remove from the array all the subsumers that are not tied for best
  foreach (0..$#tmp) {
    if ($tmp[$_]->[1] == $tmp[0]->[1]) {
      # do nothing
    }
    else {
      # kill the rest of the array and exit the loop
      $#tmp = $_ - 1;
      last;
    }
  }

  unless (defined $tmp[0]) {
    my $wps1 = $synset1;
    my $wps2 = $synset2;
    if ($mode eq 'offset') {
      $wps1 = $synset1 ? $wn->getSense ($synset1, $pos) : "*Root*#$pos#1";
      $wps2 = $synset2 ? $wn->getSynse ($synset2, $pos) : "*Root*#$pos#1";
    }

    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::getLCSbyDepth() - ";
    $self->{errorString} .= "No LCS found for $wps1 and $wps2.";

    if ($self->{trace}) {
      $self->{traceString} .= "\nNo LCS found for ";
      $self->printSet ($pos, 'wps', $wps1);
      $self->{traceString} .= ", ";
      $self->printSet ($pos, 'wps', $wps2);
      $self->{traceString} .= ".";
    }
    return undef;
  }

  if ($self->{trace}) {
    $self->{traceString} .= "\nLowest Common Subsumers: ";
    foreach (@tmp) {
      $self->printSet ($pos, $mode, $_->[0]);
      $self->{traceString} .= " (Depth=$_->[1]) ";
    }
  }

  return @tmp;
}



=item $obj->_processSynsetsFile ($filename)

Reads and processes a synsets file as output by wnDepths.pl

=cut

sub _processSynsetsFile
{
    my $self = shift;
    my $file = shift;
    my $class = ref $self || $self;
    my $wnver = $self->{wntools}->hashCode ();

    unless (open FH, '<', $file) {
	$self->{error} = 2;
	$self->{errorString} .= "\nError (${class}::_processSynsetsFile()) - ";
	$self->{errorString} .= "Cannot open $file for reading: $!.";
	return 0;
    }

    my $line = <FH>;
    unless ($line =~ /^wnver::(\S+)$/) {
	$self->{errorString} .= "\nError (${class}::_processSynsetsFile()) - ";
	$self->{errorString} .= "File $file has bad format.";
	$self->{error} = 2;
	return 0;
    }
    unless ($1 eq $wnver) {
	$self->{errorString} .= "\nError (${class}::_processSynsetsFile()) - ";
	$self->{errorString} .= "Bad WordNet hash-code in $file, $1, should be $wnver.";
	$self->{error} = 2;
	return 0;
    }

    # If we are using a root node, then we need to slightly adjust all
    # the synset depths.  Thus, the correction will be 1 if the root node
    # is on and 0 otherwise.
    my $correction = $self->{rootNode} ? 1 : 0;

    while ($line = <FH>) {
	my ($pos, $offset, @depths) = split /\s+/, $line;
	# convert the offset string to a number.  When we make the number
	# into a string again, there won't be any leading zeros.
	$offset = 0 + $offset;

	# We assume the the first depth listed is the smallest.
	# The wnDepths.pl program should guarantee this behavior.
	my @refs;
	foreach (@depths) {
	    my ($depth, $root) = split /:/;
	    # make root a number; see above for why.  If the root node
	    # is on, then all roots are the root node, so adjust for that.
	    $root = $self->{rootNode} ? 0 : $root + 0;
	    $depth += $correction;
	    push @refs, [$depth, $root];
	}
	$self->{depths}->{$pos}->{$offset} = [@refs];
    }

    if ($self->{rootNode}) {
	# set the depth of the root nodes to be one
	$self->{depths}->{n}->{0} = [[1, 0]];
	$self->{depths}->{v}->{0} = [[1, 0]];
    }

    return 1;
}

=item $obj->_processTaxonomyFile ($filename)

Reads and processes a taxonomies file as produced by wnDepths.pl

=cut

sub _processTaxonomyFile
{
    my $self = shift;
    my $filename = shift;
    my $class = ref $self || $self;

    unless (open FH, '<', $filename) {
	$self->{errorString} .= "Error (${class}::_processTaxonomyFile()) - ";
	$self->{errorString} .= "Could not open '$filename' for reading: $!.";
	$self->{error} = 2;
	return 0;
    }

    my $line = <FH>;

    unless ($line =~ /^wnver::(\S+)$/) {
	$self->{errorString} .= "Error (${class}::_processTaxonomyFile()) - ";
	$self->{errorString} .= "Bad file format for $filename.";
	$self->{error} = 2;
	return 0;
    }

    while ($line = <FH>) {
	my ($p, $o, $d) = split /\s+/, $line;

	# add 0 to offset to make it a number; see above for why
	$o = $o + 0;

        $self->{taxonomyDepths}->{$p}->{$o} = $d;
    }

    close FH;
    return 1;
}

1;

__END__

=back

=head1 AUTHORS

 Ted Pedersen, University of Minnesota Duluth
 tpederse at d.umn.edu

 Jason Michelizzi, University of Minnesota Duluth
 mich0212 at d.umn.edu

=head1 BUGS

None.

To report bugs, e-mail tpederse at d.umn.edu or go to
http://groups.yahoo.com/group/wn-similarity/.

=head1 SEE ALSO

WordNet::Similarity(3)
WordNet::Similarity::wup(3)
WordNet::Similarity::lch(3)

=head1 COPYRIGHT

Copyright (c) 2005, Ted Pedersen and Jason Michelizzi

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the Free
Software Foundation; either version 2 of the License, or (at your option)
any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to

 The Free Software Foundation, Inc.,
 59 Temple Place - Suite 330,
 Boston, MA  02111-1307, USA.

Note: a copy of the GNU General Public License is available on the web
at L<http://www.gnu.org/licenses/gpl.txt> and is included in this
distribution as GPL.txt.

=cut
