# WordNet::Similarity::path.pm version 2.04
# (Last updated $Id: path.pm,v 1.19 2008/03/27 06:21:17 sidz1979 Exp $)
#
# N.B., this module was formerly named edge.pm
#
# Semantic Similarity Measure package implementing a simple
# path-length (node-counting) semantic relatedness measure.
#
# Copyright (c) 2005,
#
# Ted Pedersen, University of Minnesota Duluth
# tpederse at d.umn.edu
#
# Siddharth Patwardhan, University of Utah, Salt Lake City
# sidd at cs.utah.edu
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

package WordNet::Similarity::path;

=head1 NAME

WordNet::Similarity::path - Perl module for computing semantic relatedness
of word senses by counting nodes in the noun and verb WordNet 'is-a'
hierarchies.

=head1 SYNOPSIS

  use WordNet::Similarity::path;

  use WordNet::QueryData;

  my $wn = WordNet::QueryData->new();

  my $path = WordNet::Similarity::path->new($wn);

  my $value = $measure->getRelatedness("car#n#1", "bus#n#2");

  my ($error, $errorString) = $measure->getError();

  die "$errorString\n" if($error);

  print "car (sense 1) <-> bus (sense 2) = $value\n";

=head1 DESCRIPTION

This module computes the semantic relatedness of word senses by counting
the number of nodes along the shortest path between the senses in the
'is-a' hierarchies of WordNet.  The path lengths include the end nodes.
For example, the path between shrub#n#1 and tree#n#1 is
S<shrub#n#1 - woody_plant#n#1 - tree#n#1>.

Since a longer path length indicate less relatedness, the relatedness
value returned is the multiplicative inverse of the path length (distance)
between the two concepts: S<relatedness = 1 / distance>.  If the two
concepts are identical, then the distance between them is one; therefore,
their relatedness is also 1.  If no path is found, then a large negative
number is returned and an error is generated (see C<getError()>).

The following methods are defined:

=over

=cut

use strict;
use WordNet::Similarity::PathFinder;
use constant MAX_DIST => 100;

our @ISA = qw(WordNet::Similarity::PathFinder);

our $VERSION = '2.04';

=item $path->getRelatedness()

Computes the relatedness of two word senses using a node counting scheme.
The relatedness score is inversely proportional to the number of nodes
along the shortest path between the two word senses.

Parameters: two word senses in "word#pos#sense" format.

Returns: Unless a problem occurs, the return value is the relatedness
score, which belongs to the interval (0, 1].  If no path exists between
the two word senses, then a large negative number is returned.  If an
error occurs, then the error level is set to non-zero and an error string
is created (see the description of getError()).  Note: the error level
will also be set to 1 and an error string will be created if no path
exists between the words.

=cut

sub getRelatedness
{
  my $self = shift;
  my $wps1 = shift;
  my $wps2 = shift;
  my $wn = $self->{wn};
  my $class = ref $self || $self;

  # Initialize traces.
  $self->{traceString} = "" if($self->{'trace'});

  # JM 1-21-04
  # moved input validation code to WordNet::Similarity::parseInput()
  my $ret = $self->parseWps ($wps1, $wps2);
  ref $ret or return $ret;

  my ($word1, $pos1, $sense1, $offset1, $word2, $pos2, $sense2, $offset2)
    = @{$ret};

  $wps1 = "$word1#$pos1#$sense1";
  $wps2 = "$word2#$pos2#$sense2";

  # Now check if the similarity value for these two synsets is in
  # fact in the cache... if so return the cached value.
  my $relatedness =
    $self->{doCache} ? $self->fetchFromCache ($wps1, $wps2) : undef;
  defined $relatedness and return $relatedness;


  # JM 1/23/04
  # Most of the code that does the work of finding the LCS and
  # hypernym trees has been moved into LCSFinder and PathFinder

  #my ($dist, $pathref) = $self->getShortestPath ($offset1, $offset2,
  #	          				 $pos1, 'offset');

  # there can be multiple shortest paths (i.e., paths of the same length)
  my @paths = $self->getShortestPath ($offset1, $offset2, $pos1, 'offset');

  my $path = shift @paths;

  # $path will be undef if no path was found (error messages already generated)
  unless (defined $path) {
    return $self->UNRELATED;
  }

  my $dist = $path->[0];

  # JM 1-29-04
  # most of the code that does path-finding is now in PathFinder

  if ($dist > 0) {
    my $score = 1.0 / $dist;

    $self->{doCache} and $self->storeToCache ($wps1, $wps2, $score);

    return $score;
  }
  else {
    $self->{errorString} .= "\nWarning (${class}::getRelatedness()) - ";
    $self->{errorString} .= "Internal error while finding relatedness.";
    $self->{error} = ($self->{error} < 1) ? 1 : $self->{error};
    return undef;
  }
}

# JM
# Much of the code that was shared between measures has been relocated.
# WordNet::Similarity contains code common to (almost) all measures,
# WordNet::Similarity::PathFinder has code common among path finding
# measures.

1;

__END__

=back

=head2 Discussion

If the two synsets being compared are the same, then the resulting relatedness
score will be 1.  For exaple, the score for car#n#1 and car#n#1 is 1.

Due to multiple inheritance in the WordNet taxonomies, it is possible for
there to be a tie for the shortest path between synsets.  If such a tie
occurs, then all of the paths that are tied will be printed to the
trace string.

The relatedness value returned by C<getRelatedness()> is the multiplicative
inverse of the path length between the two synsets (1/path_length).  This
has a slightly subtle effect: it shifts the relative magnitude of scores.
For example, if we have the following pairs of synsets with the given
path lengths:

  synset1 synset2: 3
  synset3 synset4: 4
  synset5 synset6: 5

We observe that the difference in the score for synset1-synset2 and
synset3-synset4 is the same as for synset3-synset4 and synset5-synset6.
When we take the multiplicative inverse of them, we get:

  synset1 synset2: .333
  synset3 synset4: .25
  synset5 synset6: .2

Now the difference between the scores for synset3-synset4 is less than the
difference for synset1-synset2 and synset3-synset4.  This can have negative
consequences when computing correlation coefficients.  It might be useful
to compute relatedness as S<max_distance - path_length>, where max_distance
is the longest possible shortest path between two synsets.  The original path
length can be easily determined by taking the multiplicative inverse
of the returned relatedness score: S<1/score = 1/(1/path_length) = path_length>.
The computation of max_distance is left as an exercise for the reader.

If two different word senses (wps strings) are given as input to getRelatedness,
but both word senses belong to the same synset, then 1 is returned (e.g.,
car#n#1 and auto#n#1 both belong to the same synset).

=head2 Usage

The semantic relatedness modules in this distribution are built as classes
that define the following methods:

  new()
  getRelatedness()
  getError()
  getTraceString()

See the WordNet::Similarity(3) documentation for details of these methods.

=head3 Typical Usage Examples

To create an object of the path measure, we would have the following
lines of code in the Perl program.

   use WordNet::Similarity::path;
   $measure = WordNet::Similarity::path->new($wn, '/home/sid/path.conf');

The reference of the initialized object is stored in the scalar variable
'$measure'. '$wn' contains a WordNet::QueryData object that should have been
created earlier in the program. The second parameter to the 'new' method is
the path of the configuration file for the path measure. If the 'new'
method is unable to create the object, '$measure' would be undefined. This,
as well as any other error/warning may be tested.

   die "Unable to create object.\n" if(!defined $measure);
   ($err, $errString) = $measure->getError();
   die $errString."\n" if($err);

To find the semantic relatedness of the first sense of the noun 'car' and
the second sense of the noun 'bus' using the measure, we would write
the following piece of code:

   $relatedness = $measure->getRelatedness('car#n#1', 'bus#n#2');

To get traces for the above computation:

   print $measure->getTraceString();

However, traces must be enabled using configuration files. By default
traces are turned off.

=head1 CONFIGURATION FILE

The behavior of the measures of semantic relatedness can be controlled by
using configuration files. These configuration files specify how certain
parameters are initialized within the object. A configuration file may be
specified as a parameter during the creation of an object using the new
method. The configuration files must follow a fixed format.

Every configuration file starts with the name of the module ON THE FIRST LINE
of the file. For example, a configuration file for the path module will have
on the first line 'WordNet::Similarity::path'. This is followed by the various
parameters, each on a new line and having the form 'name::value'. The
'value' of a parameter is optional (in case of boolean parameters). In case
'value' is omitted, we would have just 'name::' on that line. Comments are
supported in the configuration file. Anything following a '#' is ignored.

The module parses the configuration file and recognizes the following
parameters:

=over

=item trace

The value of this parameter specifies the level of tracing that should
be employed for generating the traces. This value
is an integer equal to 0, 1, or 2. If the value is omitted, then the
default value, 0, is used. A value of 0 switches tracing off. A value
of 1 or 2 switches tracing on.  A trace level of 1 means the synsets are
represented as word#pos#sense strings, while for level 2, the synsets
are represented as word#pos#offset strings.

=item cache

The value of this parameter specifies whether or not caching of the
relatedness values should be performed.  This value is an
integer equal to  0 or 1.  If the value is omitted, then the default
value, 1, is used. A value of 0 switches caching 'off', and
a value of 1 switches caching 'on'.

=item maxCacheSize

The value of this parameter indicates the size of the cache, used for
storing the computed relatedness value. The specified value must be
a non-negative integer.  If the value is omitted, then the default
value, 5,000, is used. Setting maxCacheSize to zero has
the same effect as setting cache to zero, but setting cache to zero is
likely to be more efficient.  Caching and tracing at the same time can result
in excessive memory usage because the trace strings are also cached.  If
you intend to perform a large number of relatedness queries, then you
might want to turn tracing off.

=item rootNode

The value of this parameter indicates whether or not a unique root node
should be used. In WordNet, there is no unique root node for the noun and
verb taxonomies. If this parameter is set to 1 (or if the value is omitted),
then certain measures (wup, path, lch, res, lin, and jcn) will "fake" a
unique root node. If the value is set to 0, then no unique root node will
be used.  If the value is omitted, then the default value, 1, is used.

=back

=head1 SEE ALSO

perl(1), WordNet::Similarity(3), WordNet::QueryData(3)

http://www.cs.utah.edu/~sidd

http://wordnet.princeton.edu

http://www.ai.mit.edu/~jrennie/WordNet

http://groups.yahoo.com/group/wn-similarity

=head1 AUTHORS

 Ted Pedersen, University of Minnesota Duluth
 tpederse at d.umn.edu

 Siddharth Patwardhan, University of Utah, Salt Lake City
 sidd at cs.utah.edu

 Jason Michelizzi, University of Minnesota Duluth
 mich0212 at d.umn.edu

=head1 BUGS

None.

To report bugs, go to L<http://groups.yahoo.com/group/wn-similarity>.

=head1 COPYRIGHT AND LICENSE

Copyright (c) 2005, Ted Pedersen, Siddharth Patwardhan and Jason Michelizzi

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
