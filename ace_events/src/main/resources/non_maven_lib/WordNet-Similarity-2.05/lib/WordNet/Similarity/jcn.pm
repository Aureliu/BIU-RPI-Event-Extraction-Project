# WordNet::Similarity::jcn.pm version 2.04
# (Last updated $Id: jcn.pm,v 1.23 2008/03/27 06:21:17 sidz1979 Exp $)
#
# Semantic Similarity Measure package implementing the measure
# described by Jiang and Conrath (1997).
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

package WordNet::Similarity::jcn;

=head1 NAME

WordNet::Similarity::jcn - Perl module for computing semantic relatedness
of word senses according to the method described by Jiang and Conrath
(1997).

=head1 SYNOPSIS

  use WordNet::Similarity::jcn;

  use WordNet::QueryData;

  my $wn = WordNet::QueryData->new();

  my $rel = WordNet::Similarity::jcn->new($wn);

  my $value = $rel->getRelatedness("car#n#1", "bus#n#2");

  ($error, $errorString) = $rel->getError();

  die "$errorString\n" if($error);

  print "car (sense 1) <-> bus (sense 2) = $value\n";

=head1 DESCRIPTION

This module computes the semantic relatedness of word senses according to
the method described by Jiang and Conrath (1997). This measure is based on
a combination of using edge counts in the WordNet 'is-a' hierarchy and
using the information content values of the WordNet concepts, as described
in the paper by Jiang and Conrath. Their measure, however, computes values
that indicate the semantic distance between words (as opposed to their
semantic relatedness). In this implementation of the measure we invert the
value so as to obtain a measure of semantic relatedness. Other issues that
arise due to this inversion (such as handling of zero values in the
denominator) have been taken care of as special cases.

=over

=cut

use strict;
use warnings;

use Exporter;
use WordNet::Similarity::ICFinder;

our (@ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS);

@ISA = qw(WordNet::Similarity::ICFinder);

%EXPORT_TAGS = ();

@EXPORT_OK = ();

@EXPORT = ();

our $VERSION = '2.04';

# the 'new' method is supplied by WordNet::Similarity

=item $jcn->getRelatedness ($synset1, $synset2)

Computes the relatedness of two word senses using an information content
scheme.  See the discussion section below for detailed information on how
the jcn measure calculates relatedness.

Parameters: two word senses in "word#pos#sense" format.

Returns: Unless a problem occurs, the return value is the relatedness
score.  If no path exists between the two word senses, then a large
negative number is returned.  If an error occures, then the error level
is set to non-zero and an error string is created (see the description
of getError()).  Note: the error level will also be set to 1 and an
an error string will be created if no path exists between the words.

=cut

sub getRelatedness
{
  my $self = shift;
  my $wps1 = shift;
  my $wps2 = shift;
  my $wn = $self->{wn};
  my $class = ref $self || $self;

  # Check the existence of the WordNet::QueryData object.
  unless ($wn) {
    $self->{errorString} .= "\nError (${class}::getRelatedness()) - ";
    $self->{errorString} .= "A WordNet::QueryData object is required.";
    $self->{error} = 2;
    return undef;
  }

  # Initialize traces.
  $self->{traceString} = "";

  # JM 1-21-04
  # moved input validation code to parseInput() in a super-class
  my $ret = $self->parseWps ($wps1, $wps2);
  ref $ret or return $ret;
  my ($word1, $pos1, undef, $offset1, $word2, $pos2, undef, $offset2) = @{$ret};

  my $pos = $pos1;

  # Now check if the similarity value for these two synsets is in
  # fact in the cache... if so return the cached value.
  my $relatedness =
    $self->{doCache} ? $self->fetchFromCache ($wps1, $wps2) : undef;
  defined $relatedness and return $relatedness;

  # Now get down to really finding the relatedness of these two.
  my $mode = 'offset';
  my @LCSs = $self->getLCSbyIC ($offset1, $offset2, $pos, 'offset');

  my $ref = shift @LCSs;
  # check if $ref is a reference, if not, then return undefined
  # $ref will not be a reference if no LCS was found
  unless (ref $ref) {
    return $self->UNRELATED;
  }

  my ($lcs, $lcsic) = @{$ref};
  my $lcsfreq = $self->getFrequency ($lcs, $pos, 'offset');

  # Check for the rare possibility of the root node having 0
  # frequency count...
  # If normal (i.e. freqCount(root) > 0)... Set the minimum distance to the
  # greatest distance possible + 1... (my replacement for infinity)...
  # If zero root frequency count... return 0 relatedness, with a warning...

  my $maxScore;
  my $rootFreq = $self->getFrequency (0, $pos, 'offset');
  if($rootFreq) {
#    $minDist = (2*(-log(0.001/($self->{offsetFreq}->{$pos}->{0})))) + 1;
    $maxScore = 2 * -log (0.001 / $rootFreq) + 1;
  }
  else {
    $self->{errorString} .= "\nWarning (${class}::getRelatedness()) - ";
    $self->{errorString} .= "Root node has a zero frequency count.";
    $self->{error} = ($self->{error} < 1) ? 1 : $self->{error};
    return 0;
  }

  # Foreach lowest common subsumer...
  # Find the minimum jcn distance between the two subsuming concepts...
  # Making sure that neither of the 2 concepts have 0 infocontent
  my $ic1 = $self->IC($offset1, $pos);
  my $ic2 = $self->IC($offset2, $pos);
  if ($self->{trace}) {
    $self->{traceString} .= "Concept1: ";
    $self->printSet ($pos, $mode, $offset1);
    $self->{traceString} .= " (IC=";
    $self->{traceString} .= sprintf ("%.6f", $ic1);
    $self->{traceString} .= ")\n";
    $self->{traceString} .= "Concept2: ";
    $self->printSet ($pos, $mode, $offset2);
    $self->{traceString} .= " (IC=";
    $self->{traceString} .= sprintf ("%.6f", $ic2);
    $self->{traceString} .= ")\n";
  }

  my $distance;

  # If either of the two concepts have a zero information content...
  # return 0, for lack of data...
  if($ic1 && $ic2) {
    my $ic3 = $self->IC($lcs, $pos);

    $distance = $ic1 + $ic2 - (2 * $ic3);
  }
  else {
    return 0;
  }

  # Now if distance turns out to be 0...
  # implies ic1 == ic2 == ic3 (most probably all three represent
  # the same concept)... i.e. maximum relatedness... i.e. infinity...
  # We'll return the maximum possible value ("Our infinity").
  # Here's how we got our infinity...
  # distance = ic1 + ic2 - (2 x ic3)
  # Largest possible value for (1/distance) is infinity, when distance = 0.
  # That won't work for us... Whats the next value on the list...
  # the smallest value of distance greater than 0...
  # Consider the formula again... distance = ic1 + ic2 - (2 x ic3)
  # We want the value of distance when ic1 or ic2 have information content
  # slightly more than that of the root (ic3)... (let ic2 == ic3 == 0)
  # Assume frequency counts of 0.01 less than the frequency count of the
  # root for computing ic1...
  # sim = 1/ic1
  # sim = 1/(-log((freq(root) - 0.01)/freq(root)))

  my $score;

  if ($distance == 0) {
    if ($rootFreq > 0.01) {
      $score = 1 / -log (($rootFreq - 0.01) / $rootFreq);
    }
    else {
      # root frequency is 0
      return 0;
    }
  }
  else { # distance is non-zero
    $score = 1 / $distance
  }
  $self->{doCache} and $self->storeToCache ($wps1, $wps2, $score);
  return $score;
}

# JM 1-16-04
# moved subroutine _getLeastCommonSubsumers to Infocontent.pm

1;

__END__

=back

=head2 Discussion

The relatedness value returned by the jcn measure is equal to
S<1 / jcn_distance>, where jcn_distance is equal to
S<IC(synset1) + IC(synset2) - 2 * IC(lcs)>.  The original metric proposed
by Jiang and Conrath was this distance measure.  By taking the
multiplicative inverse of it, we have converted it to a measure
of similarity, but by so doing, we have shifted the distribution of
scores.

For example, if we have the following pairs of synsets with the given
jcn distances:

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
to compute relatedness as S<max_distance - jcn_distance>, where max_distance
is the maximum possible jcn distance between any two synsets.
The original jcn distance can easily be determined by taking the inverse
of the value returned: S<1/score = 1/1/jcn_distance = jcn_distance>.

There are two special cases that need to be handled carefully when computing
relatedness; both of these involve the case when jcn_distance is zero.

In the first case, we have S<ic(synset1) = ic(synset2) = ic(lcs) = 0>.  In
an ideal world, this would only happen when all three concepts, viz.
synset1, synset2, and lcs, are the root node.  However, when a synset has
a frequency count of zero, we use the value 0 for the information content.
In this first case, we return 0 due to lack of data.

In the second case, we have S<ic(synset1) + ic(synset2) = 2 * ic(lics)>.
This is almost always found when S<synset1 = synset2 = lcs> (i.e., the
two input synsets are the same).  Intuitively this is the case of maximum
relatedness, which would be infinity, but it is impossible to return
infinity.  Insteady we find the smallest possible distance greater than
zero and return the multiplicative inverse of that distance.

=head2 Usage

The semantic relatedness modules in this distribution are built as classes
that define the following methods:
  new()
  getRelatedness()
  getError()
  getTraceString()

See the WordNet::Similarity(3) documentation for details of these methods.

=head3 Typical Usage Examples

To create an object of the jcn measure, we would have the following
lines of code in the Perl program.

   use WordNet::Similarity::jcn;
   $measure = WordNet::Similarity::jcn->new($wn, '/home/sid/jcn.conf');

The reference of the initialized object is stored in the scalar variable
'$measure'. '$wn' contains a WordNet::QueryData object that should have been
created earlier in the program. The second parameter to the 'new' method is
the path of the configuration file for the jcn measure. If the 'new'
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

Every configuration file starts with the name of the module ON THE FIRST LINE of
the file. For example, a configuration file for the jcn module will have
on the first line 'WordNet::Similarity::jcn'. This is followed by the various
parameters, each on a new line and having the form 'name::value'. The
'value' of a parameter is optional (in case of boolean parameters). In case
'value' is omitted, we would have just 'name::' on that line. Comments are
supported in the configuration file. Anything following a '#' is ignored till
the end of the line.

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

=item infocontent

The value for this parameter should be a string that specifies the path of
an information content file containing the frequency of occurrence of every
WordNet concept in a large corpus. A number of utility programs are
included in this distribution that can be used to generate an infocontent
file (see utils.pod).  If no path is specified, then the default infocontent
file is used, which was generated from SemCor using the sense-tags.

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

  Siddharth Patwardhan, University of Utah
  sidd at cs.utah.edu

  Jason Michelizzi, University of Minnesota Duluth
  mich0212 at d.umn.edu

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
