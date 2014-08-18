# WordNet::Similarity::PathFinder version 2.04
# (Last updated $Id: PathFinder.pm,v 1.39 2008/03/27 06:21:17 sidz1979 Exp $)
#
# Module containing path-finding code for the various measures of semantic
# relatedness.
#
# Copyright (c) 2005,
#
# Ted Pedersen, University of Minnesota Duluth
# tpederse at d.umn.edu
#
# Jason Michelizzi, Univeristy of Minnesota Duluth
# mich0212 at d.umn.edu
#
# Siddharth Patwardhan, University of Utah, Salt Lake City
# sidd at cs.utah.edu
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

package WordNet::Similarity::PathFinder;

=head1 NAME

WordNet::Similarity::PathFinder - module to implement path finding methods
(by node counting) for WordNet::Similarity measures of semantic relatedness

=head1 SYNOPSIS

 use WordNet::QueryData;
 my $wn = WordNet::QueryData->new;

 use WordNet::Similarity::PathFinder;
 my $obj = WordNet::Similarity::PathFinder->new ($wn);

 my $wps1 = 'winston_churchill#n#1';
 my $wps2 = 'england#n#1';

 # parseWps returns reference to an array that contains 
 # word1 pos1 sense1 offset1 word2 pos2 sense2 offset2

 my $result = $obj->parseWps($wps1, $wps2);
 print "@$result\n";

 # path is a reference to an array that contains the path between
 # wps1 and wps2 expressed as a series of wps values

 my @paths = $obj->getShortestPath($wps1, $wps2, 'n', 'wps');
 my ($length, $path) = @{shift @paths};
 defined $path or die "No path between synsets";
 print "shortest path between $wps1 and $wps2 is $length edges long\n";
 print "@$path\n";

 my $offset1 = $wn -> offset($wps1);
 my $offset2 = $wn -> offset($wps2);

 # path is a reference to an array that contains the path between
 # offset1 and offset2 expressed as a series of offset values

 my @paths = $obj->getShortestPath($offset1, $offset2, 'n', 'offset');
 my ($length, $path) = @{shift @paths};
 defined $path or die "No path between synsets";
 print "shortest path between $offset1 and $offset2 is $length edges  long\n";
 print "@$path\n";

=head1 DESCRIPTION

=head2 Introduction

This class is derived from (i.e., is a sub-class of) WordNet::Similarity.

The methods in this module are useful for finding paths between concepts
in WordNet's 'is-a' taxonomies.  Concept A is-a concept B if, and only if,
B is a hypernym of A or A is in the hypernym tree of B.  N.B., only nouns
and verbs have hypernyms.

The methods that find path lengths (such as C<getShortestPath()> and
C<getAllPaths()> compute the lengths using node-counting not edge-counting.
In general, the length of a path using node-counting will always be one
more than the length using edge-counting.  For example, if concept A
is a hyponym of concept B, then the path length between A and B using
node-counting is 2, but the length using edge-counting is 1.  Likewise, the
path between A and A is 1 using node-counting and 0 using edge-counting.

=head2 Methods

This module inherits all the methods of WordNet::Similarity.  Additionally,
the following methods are also defined.

=head3 Public methods

=over

=cut

use strict;
use warnings;
use WordNet::Similarity;
use File::Spec;

our @ISA = qw/WordNet::Similarity/;

our $VERSION = '2.04';

WordNet::Similarity::addConfigOption ('rootNode', 0, 'i', 1);

=item $measure->setPosList(Z<>)

Specifies the parts of speech that measures derived from this module
support (namely, nouns and verbs).

parameters: none

returns: true

=cut

sub setPosList
{
    my $self = shift;
    $self->{n} = 1;
    $self->{v} = 1;
    return 1;
}


=item $self->traceOptions(Z<>)

Overrides method of same name in WordNet::Similarity.  Prints module-specific
configuration options to the trace string (if tracing is on).  PathFinder
supports one module specific option: rootNode.

Parameters: none

returns: nothing

=cut

sub traceOptions
{
    my $self = shift;
    $self->{traceString} .= "root node :: $self->{rootNode}\n";
    $self->SUPER::traceOptions();
}


=item $measure->parseWps($synset1, $synset2)

parameters: synset1, synset2 -- two synsets in wps format

returns: a reference to an array, WordNet::Similarity::UNRELATED, or undef

Overrides the parseWps() method in WordNet::Similarity in order to run
additional checks, but calls WordNet::Similarity::parseWps() to get
those checks accomplished as well.  Thus, this method does everything
that WordNet::Similarity::parseWps does.

=over

=item quote from WordNet::Similarity::parseWps:

This method checks the format of the two input synsets by calling
validateSynset() for each synset.

If the synsets are in wps format, a reference to an array will be returned.
This array has the form [$word1, $pos1, $sense1, $offset1, $word2, $pos2,
$sense2, $offset2] where $word1 is the word part of $wps1, $pos1, is the
part of speech of $wps1, $sense1 is the sense from $wps.  $offset1 is the
offset for $wps1.

If an error occurs (such as a synset being poorly-formed), then undef
is returned, the error level is set to non-zero, and an error message is
appended to the error string.

=back

In addition, if the two synsets are from different parts of speech, then
WordNet::Similarity::UNRELATED is returned, the error level is set to 1, and
a message is appended to the error string.

If either synset is not a noun or a verb, then the error level
is set to 1, a message is appended to the error string, and undef
is returned.

If the synsets are in wps format, a reference to an array will be returned.
This array has the form [$word1, $pos1, $sense1, $offset1, $word2, $pos2,
$sense2, $offset2].

=cut

sub parseWps
{
    my $self = shift;
    my $ret = $self->SUPER::parseWps (@_);
    my $class = ref $self || $self;

    ref $ret or return $ret;
    my ($w1, $pos1, $s1, $off1, $w2, $pos2, $s2, $off2) = @{$ret};

    # check to make sure both input words are of the same part of speech
    if ($pos1 ne $pos2) {
	$self->{error} = $self->{error} < 1 ? 1 : $self->{error};
	$self->{errorString} .= "\nWarning (${class}::parseWps()) - ";
	$self->{errorString} .=
	    "$w1#$pos1 and $w2#$pos2 belong to different parts of speech.";
	if ($self->{trace}) {
	    $self->{traceString} .= "\n";
	    $self->printSet ($pos1, 'wps', "$w1#$pos1#$s1");
	    $self->{traceString} .= " and ";
	    $self->printSet ($pos2, 'wps', "$w2#$pos2#$s2");
	    $self->{traceString} .= " belong to different parts of speech.";
	}
	return $self->UNRELATED;
    }

    # check to make sure that the pos is a noun or verb
    if (index ("nv", $pos1) < $[) {
	if ($self->{trace}) {
	    $self->{traceString} .=
		"Only verbs and nouns have hypernym trees ($w1#$pos1, $w2#$pos2).\n";
	}
	$self->{error} = $self->{error} < 1 ? 1 : $self->{error};
	$self->{errorString} .= "\nWarning (${class}::parseWps()) - ";
	$self->{errorString} .=
	    "Only verbs and nouns have hypernym trees ($w1#$pos1, $w2#$pos2).";
	return undef;
    }

    return $ret;
}


=item $measure->getShortestPath($synset1, $synset2, $pos, $mode)

Given two input synsets, returns the shortest path between the two synsets.

Parameters: two synsets, a part-of-speech, and a mode indicator
(i.e., the string 'offset' or 'wps').  If the mode is 'offset', then the
synsets should be WordNet offsets.  If the mode is 'wps', then the synsets
should be in word#pos#sense format.

Returns: a list of references to arrays.  Each array has the form
C<($path_length, $path_ref)> where $path_ref is
a reference to an array whose elements are the synsets along the shortest
path between the two input synsets.  There will be as many array references
returned as there are shortest paths between the synsets.  That is, there
will be no arrays returned if there is no path between the synsets, and there
will be at least one array returned if there is a path between the synsets.
If there are multiple paths tied for being shortest in length, then all
those paths are returned (hence, this is why multiple array references
can be returned).

Upon error, returns undef, sets the error level to non-zero, and appends
a message to the error string.

=cut

sub getShortestPath
{
  my $self = shift;
  my $synset1 = shift;
  my $synset2 = shift;
  my $pos = shift;
  my $mode = shift;

  my $class = ref $self || $self;
  my $wn = $self->{wn};

  # JM 2/9/04 - we do this in validateSynset() now
  #if ($mode eq 'wps') {
  #  # this prevents problems when the two input words are different word
  #  # senses from the same synset (e.g., car#n#1 and auto#n#1)
  #  ($synset1) = $wn->querySense ($synset1, "syns");
  #  ($synset2) = $wn->querySense ($synset2, "syns");
  #}

  my @paths = $self->getAllPaths ($synset1, $synset2, $pos, $mode);

  # check to see if any paths were found; if none were found, then
  # $paths[0] will be undefined
  unless (defined $paths[0]) {
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::getShortestPath()) - ";

    my ($wps1, $wps2) = ($synset1, $synset2);
    if ($mode eq 'offset') {
      $wps1 = $wn->getSense ($synset1, $pos);
      $wps2 = $wn->getSense ($synset2, $pos);
    }
    $self->{errorString} .= "No path between synsets $wps1 and $wps2 found.";

    if ($self->{trace}) {
      $self->{traceString} .= "\nNo path between synsets ";
      $self->printSet ($pos, 'wps', $wps1);
      $self->{traceString} .= " and ";
      $self->printSet ($pos, 'wps', $wps2);
      $self->{traceString} .= " found.";
    }
    return undef;
  }

  my $best_length = $paths[0]->[1];

  my @return = ([$paths[0]->[1], $paths[0]->[2]]);

  foreach (1..$#paths) {
    last if $paths[$_]->[1] > $best_length;
    push @return, [$paths[$_]->[1], $paths[$_]->[2]];
  }

  #my $length = $paths[0]->[1];
  #my $path = $paths[0]->[2];

  if ($self->{trace}) {
    for (@return) {
      $self->{traceString} .= "\nShortest path: ";
      $self->printSet ($pos, $mode, @{$_->[1]});
      $self->{traceString} .= "\nPath length = " . $_->[0];
    }
  }
  return @return;
}


=item $measure->getAllPaths($synset1, $synset2, $pos, $mode)

Given two input synsets, returns all the paths between the two synsets.

Parameters: a reference to the object, two synsets, a part-of-speech, and
a mode indicator (the string 'offset' or 'wps').

If the mode is 'offset', then the synsets should be WordNet offsets.  If the
mode is 'wps', then they should be strings in word#pos#sense format.

Returns: A list of all paths, sorted by path length in ascending order.  The
format for each item in the list is a reference to an array that has the
format: [$top, $length, [@synsets_list]] where @synset_list is a list
of synsets along the path (including the two input synsets)

Returns undef on error.

=cut

sub getAllPaths
{
  my $self = shift;
  my $class = ref $self || $self;
  my $synset1 = shift;
  my $synset2 = shift;
  my $pos = shift;
  my $mode = shift;

  if (($mode ne 'offset') && ($mode ne 'wps')) {
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::getAllPaths()) - ";
    $self->{errorString} .= "Mode must be either 'offset' or 'wps'";
    return undef;
  }

  my @lTrees = $self->_getHypernymTrees ($synset1, $pos, $mode);
  my @rTrees = $self->_getHypernymTrees ($synset2, $pos, $mode);

  # [trace]
  if($self->{trace}) {
    foreach my $lTree (@lTrees) {
      $self->{traceString} .= "HyperTree: ";
      $self->printSet ($pos, $mode, @$lTree);
      $self->{traceString} .= "\n";
    }
    foreach my $rTree (@rTrees) {
      $self->{traceString} .= "HyperTree: ";
      $self->printSet ($pos, $mode, @$rTree);
      $self->{traceString} .= "\n";
    }
  }
  # [/trace]

  # Find the length of each path in these trees.
  my @return;
#  my $root = $mode eq 'offset'
#    ? 0
#    : ($pos eq 'n') ? $self->ROOT_N : $self->ROOT_V;

 LTREE:
  foreach my $lTree (@lTrees) {
  RTREE:
    foreach my $rTree (@rTrees) {
      my $subsumer;
      $subsumer = $self->_getSubsumerFromTrees ($lTree, $rTree, $mode);

      next RTREE unless defined $subsumer;
      #next RTREE if ($subsumer eq $root) and !$self->{rootNode};

      my $lCount = 0;
      my @lpath;
      foreach my $offset (reverse @{$lTree}) {
  	$lCount++;
  	last if($offset eq $subsumer);
	push @lpath, $offset;
      }
      my $rCount = 0;
      my @rpath;
      foreach my $offset (reverse @{$rTree}) {
  	$rCount++;
  	last if($offset eq $subsumer);
	unshift @rpath, $offset;
      }

      my $path = [@lpath, $subsumer, @rpath];

      push @return, [$subsumer, $rCount + $lCount - 1, $path];
    }
  }

  return sort {$a->[1] <=> $b->[1]} @return;
}


=item $measure->validateSynset($synset)

parameters: synset -- a string in word#pos#sense format

returns: a list or undef on error

This method overrides the method of the same name in WordNet::Similarity
to provide additional behavior but calls WordNet::Similarity::validateSynset
to accomplish that method's behavior.  Thus, this method does everything
that WordNet::Similarity::validateSynset does.

=over

=item quote from WordNet::Similarity::validateSynset:

This method does the following:

=over

=item 1.

Verifies that the synset is well-formed (i.e., that it consists of three
parts separated by #s, the pos is one of {n, v, a, r} and that sense
is a natural number).  A synset that matches the pattern '[^\#]+\#[nvar]\#\d+'
is considered well-formed.

=item 2.

Checks if the synset exists by trying to find the offset for the synset

=back

=back

This method, however, has a slightly different return value.  Instead of
merely breaking the synset into three parts, it returns the "safe" form
of the synset.  That is, if a synset has multiple word senses, this
method returns the first word sense in that synset (this is so that
other path-finding methods work properly).  For example, if the input
to this method is auto#n#1, the return value is ('car', 'n', 1, 2853224)
since the sense 'car#n#1' is the first member of the synset to which
'auto#n#1' belongs.

If any of these tests fails, then the error level is set to non-zero, a
message is appended to the error string, and undef is returned.

=cut

sub validateSynset
{
  my $self = shift;
  my $synset = shift;
  my ($word, $pos, $sense, $offset) = $self->SUPER::validateSynset ($synset);
  my $class = ref $self || $self;

  # check to see if previous call encountered an error:
  return undef if $self->{error};

  my @synset = $self->{wn}->querySense ($synset, "syns");
  my $safewps = shift @synset;

  unless (defined $safewps) {
    # safety check--we shouldn't ever get here.  querySense shouldn't
    # return undef unless the input synset is bad, but we've already
    # checked that synset
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::validateSynset()) - ";
    $self->{errorString} .= "No synset appears to exist for $synset.";
    return undef;
  }

  unless ($safewps =~ /^([^\s\#]+)\#([nvar])\#(\d+)$/) {
    # we should never get here -- if QueryData doesn't return word senses
    # in the right format, then we're in a lot of trouble... nevertheless,
    # we check just to be sure
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::validateSynset()) - ";
    $self->{errorString} .= "Internal error: $safewps is not well-formed.  Has WordNet or WordNet::QueryData changed format?";
    return undef;
  }

  return ($1, $2, $3, $offset);
}


=back

=head3 Private methods

=over

=item $measure->_getHypernymTrees($synset, $pos, $mode)

This method takes as input a synset and returns a list of references
to arrays where these arrays are paths from the input synset to the
top of the taxonomy (*Root*#[nv]#1 if the root node is on).

Parameters: a synset, a part-of-speech, and a mode.
The mode must be either the string 'wps' or 'offset'.  If
the mode is 'wps', then the synset must be in wps format; otherwise, it
must be an offset.

Returns: a list of references to arrays.  These arrays are paths (hypernym
trees).

=cut

# Suroutine that returns an array of hypernym trees, given the offset of
# the synset. Each hypernym tree is an array of offsets.
# INPUT PARAMS  : $offset .. Offset of the synset.
#               : $pos    .. Part of speech.
# RETURN VALUES : (@tree1, @tree2, ...) .. an array of Hypernym trees (offsets)
sub _getHypernymTrees
{
  my $self = shift;
  my $wn = $self->{wn};
  my $synset = shift;
  my $pos = shift;
  my $mode = shift;
  my $curPath = shift;
  $curPath = {} if(!defined($curPath));
  $curPath->{$synset} = 1;
  

  my $wordForm = $synset;
  if ($mode eq 'offset') {
    # check if the input synset is one of the imaginary root nodes
    if ($synset == 0) {
      return ([0]);
    }
    $wordForm = $wn->getSense($synset, $pos);
  }
  else {
    # check for root node
    if ($synset =~ /\*ROOT\*/i) {
      return ([$synset]);
    }
  }

  my @hypernyms = $wn->querySense($wordForm, "hypes");
  my @returnArray = ();
  if($#hypernyms < 0) {
    my @tmpArray = $synset;
    if ($self->{rootNode}) {
      if ($mode eq 'offset') {
	unshift @tmpArray, 0;
      }
      else {
	unshift @tmpArray, ($pos eq 'n') ? $self->ROOT_N : $self->ROOT_V;
      }
    }
    push @returnArray, [@tmpArray];
  }
  else {
    foreach my $hypernym (@hypernyms) {
      my $hypesynset = $mode eq 'offset' ? $wn->offset ($hypernym) : $hypernym;
      if(!defined($curPath->{$hypesynset}))
      {
        my %localCopy = %{$curPath};
        my @tmpArray = $self->_getHypernymTrees ($hypesynset, $pos, $mode, \%localCopy);

        foreach my $element (@tmpArray) {
	  push @$element, $synset;
          push @returnArray, [@$element];
        }
      }
      if(scalar(@returnArray) <= 0) {
        my @tmpArray = $synset;
        if ($self->{rootNode}) {
          if ($mode eq 'offset') {
	    unshift @tmpArray, 0;
          }
          else {
	    unshift @tmpArray, ($pos eq 'n') ? $self->ROOT_N : $self->ROOT_V;
          }
        }
        push @returnArray, [@tmpArray];
      }
    }
  }
  return @returnArray;
}

=item getLCSbyPath($synset1, $synset2, $pos, $mode)

Given two input synsets, finds the least common subsumer (LCS) of them.
If there are multiple candidates for the LCS (due to multiple inheritance),
the LCS that results in the shortest path between in input concepts is
chosen.

Parameters: two synsets, a part of speech, and a mode.

Returns: a list of references to arrays where each array has the from
C<($lcs, $pathlength)>.  $pathlength is the length
of the path between the two input concepts.  There can be multiple LCSs
returned if there are ties for the shortest path between the two synsets.
Returns undef on error.

=cut

sub getLCSbyPath
{
  my $self = shift;
  my $synset1 = shift;
  my $synset2 = shift;
  my $pos = shift;
  my $mode = shift;
  my $class = ref $self || $self;

  my @paths = $self->getAllPaths ($synset1, $synset2, $pos, $mode);

  # if no paths were found, $paths[0] should be undefined
  unless (defined $paths[0]) {
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::getLCSbyPath()) - ";
    $self->{errorString} .= "No LCS found.";
    return undef;
  }

  if ($self->{trace}) {
    $self->{traceString} .= "Lowest Common Subsumer(s): ";
  }

  my @return;

  # put the best LCS(s) into @return; do some tracing at the same time.
  foreach my $pathref (@paths) {
    if ($self->{trace}) {
      # print path to trace string
      $self->printSet ($pos, $mode, $pathref->[0]);	
      $self->{traceString} .= " (Length=".$pathref->[1].")\n";
    }

    # push onto return array if this path length is tied for best
    if ($pathref->[1] <= $paths[0]->[1]) {
      push @return, [$pathref->[0], $pathref->[1]];
    }
  }

  if ($self->{trace}) {
    $self->{traceString} .= "\n\n";
  }

  return @return;
}


=item $measure->_getSubsumerFromTrees($treeref1, $treeref2, $mode)

This subroutine returns takes two trees as produced by getHypernymTrees
and returns the most specific subsumer from them.

Parameters: two references to arrays, and
a string indicating mode ('wps' or 'offset').

Returns: the subsumer or undef

=cut

sub _getSubsumerFromTrees
{
  my $self = shift;
  my $array1 = shift;
  my $array2 = shift;
  my $mode = shift;
  my @tree1 = reverse @{$array1};
  my @tree2 = reverse @{$array2};
  my $class = ref $self || $self;

  my $tmpString = " " . join (" ", @tree1) . " ";

  foreach my $element (@tree2) {
    my $pattern = ($mode eq 'offset') ? qr/ 0*$element / : qr/ \Q$element\E /;
    if ($tmpString =~ /$pattern/) {
      return $element;
    }
  }

  # no common subsumer found, check to see if we are using a root node
  return undef unless $self->{rootNode};

  $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
  $self->{error} .= "\nWarning (${class}::getSubsumerFromTrees()) - ";
  $self->{errorString} .= "root node 'on' but no subsumer found.";
  return undef;
}

=item getDepth()

This method is non-functional and likely to be moved to a different module
soon.

=cut

sub getDepth
{
  use Carp;
  croak "This method is non-functional";
  my $self = shift;
  my $synset = shift;
  my $pos = shift;
  my $mode = shift;
  my $class = ref $self || $self;
  my $offset;

  if ($mode eq 'offset') {
    $offset = $synset;
    return 1 if $offset == 0;
  }
  elsif ($mode eq 'wps') {
    $offset = $self->{wn}->offset ($synset);
    return 1 if $synset =~ /^\*Root\*/i;
  }
  else {
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::getAllPaths()) - ";
    $self->{errorString} .= "Mode must be either 'offset' or 'wps'";
    return undef;
  }

  my $depth = $self->{depths}->{$pos}->{$offset};
  defined $depth and return $depth;

  $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
  $self->{errorString} .= "\nWarning (${class}::getDepth) - ";
  $self->{errorString} .= "$synset appears to have undefined depth.";
  return undef;
}


1;

__END__

=back

=head2 Discussion

Many of the methods in this module can work with either offsets or
wps strings internally.  There are several interesting consequences
of each mode.

=over

=item 1.

An offset is not a unique identifier for a synset, but neither is
a wps string.  An offset only indicates a byte offset in one of the
WordNet data files (data.noun, data.verb, etc. on Unix-like systems).
An offset along with a part of speech, however, does uniquely identify
a synset.

A word#pos#sense string, on the other hand, is the opposite extreme.
A word#pos#sense string is an identifier for a unique word sense.  A
synset can have several word senses in it (i.e., a synset is a set
of word senses that are synonymous).  The synset {beer_mug#n#1, stein#n#1}
has two word senses.  The wps strings 'beer_mug#n#1' and 'stein#n#1' can
both be used to refer to the synset.  For simplicity, we usually just
use the first wps string when referring to the synset.  N.B., the
wps representation was developed by WordNet::QueryData.

=item 2.

Early versions of WordNet::Similarity::* used offsets internally for
finding paths, hypernym trees, subsumers, etc.  The module WordNet::QueryData
that is used by Similarity, however, accepts only wps strings as input
to its querySense method, which is used to find hypernyms.  We have found
that it is more efficient (faster) to use wps strings internally.

=back

=head1 AUTHORS

 Ted Pedersen, University of Minnesota Duluth
 tpederse at d.umn.edu

 Jason Michelizzi, University of Minnesota Duluth
 mich0212 at d.umn.edu

 Siddharth Patwardhan, University of Utah, Salt Lake City
 sidd at cs.utah.edu

=head1 BUGS

None.

=head1 SEE ALSO

WordNet::Similarity(3)
WordNet::Similarity::path(3)
WordNet::Similarity::lch(3)
WordNet::Similarity::wup(3)

=head1 COPYRIGHT

Copyright (c) 2005, Ted Pedersen, Siddharth Patwardhan and Jason Michelizzi

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
