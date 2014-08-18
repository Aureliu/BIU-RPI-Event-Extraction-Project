# WordNet::Similarity::wup.pm version 2.04
# (Last updated $Id: wup.pm,v 1.27 2008/03/27 06:21:17 sidz1979 Exp $)
#
# Semantic Similarity Measure package implementing the semantic
# relatedness measure described by Wu & Palmer (1994) as revised
# by Resnik (1999).
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

package WordNet::Similarity::wup;

=head1 NAME

WordNet::Similarity::wup - Perl module for computing semantic
relatedness of word senses using the edge counting method of the
of Wu & Palmer (1994)

=head1 SYNOPSIS

 use WordNet::Similarity::wup;

 use WordNet::QueryData;

 my $wn = WordNet::QueryData->new();

 my $wup = WordNet::Similarity::wup->new($wn);

 my $value = $wup->getRelatedness('dog#n#1', 'cat#n#1');

 my ($error, $errorString) = $wup->getError();

 die $errorString if $error;

 print "dog (sense 1) <-> cat (sense 1) = $value\n";

=head1 DESCRIPTION

Resnik (1999) revises the Wu & Palmer (1994) method of measuring semantic
relatedness.  Resnik uses use an edge distance method by taking into
account the most specific node subsuming the two concepts.  Here we have
implemented the original Wu & Palmer method, which uses node-counting.

=head2 Methods

This module defines the following methods:

=over

=cut

use strict;
use warnings;

use WordNet::Similarity::DepthFinder;

our @ISA = qw/WordNet::Similarity::DepthFinder/;

our $VERSION = '2.04';

=item $wup->getRelatedness ($synset1, $synset2)

Computes the relatedness of two word senses using a node counting scheme.
For details on how relatedness is computed, see the discussion section
below.

Parameters: two word senses in "word#pos#sense" format.

Returns: Unless a problem occurs, the return value is the relatedness
score.  If no path exists between the two word senses, then a large
negative number is returned.  If an error occurs, then the error level is
set to non-zero and an error string is created (see the description
of getError()).  Note: the error level will also be set to 1 and an error
string will be created if no path exists between the words.

=cut

sub getRelatedness
{
  my $self = shift;
  my $in1 = shift;
  my $in2 = shift;

  my $class = ref $self || $self;

  # initialize trace string
  $self->{traceString} = "";

  # JM 1-21-04
  # moved input validation code to WordNet::Similarity::parseWps()
  my $ret = $self->parseWps ($in1, $in2);
  ref $ret or return $ret;
  my ($word1, $pos1, $sense1, $offset1, $word2, $pos2, $sense2, $offset2)
    = @{$ret};

  defined $word1 or return undef;

  my $wps1 = "$word1#$pos1#$sense1";
  my $wps2 = "$word2#$pos2#$sense2";

  my $score = $self->fetchFromCache ($wps1, $wps2);
  return $score if defined $score;

  my @LCSs = $self->getLCSbyDepth ($wps1, $wps2, $pos1, 'wps');
  my $temp = shift @LCSs;
  unless (ref $temp) {
    return $temp;
  }
  my ($lcs, $depth, $root) = @{$temp};

  unless (defined $lcs) {
    # no lcs found, return unrelated (errors already generated)
    return $self->UNRELATED;
  }

  # now find the depth of $wps1 and $wps2
  my $trace = $self->{trace};
  $self->{trace} = 0;
  my @paths1 = $self->getShortestPath ($wps1, $lcs, $pos1, 'wps');
  my @paths2 = $self->getShortestPath ($wps2, $lcs, $pos1, 'wps');
  $self->{trace} = $trace;

  my ($length1, undef) = @{shift @paths1};
  my ($length2, undef) = @{shift @paths2};

  if (ref $length1) {
      die "Length 1 is a ref\n";
  }
  # If we've already found an lcs, then there must be a path, so this
  # error should never occur, but there's little harm in checking anyways
  unless (defined $length1) {
      $self->{errorString} .= "Length 1 is undefined.";
      $self->{error} = 1;
      return undef;
  }
  unless (defined $length2) {
      $self->{errorString} .= "Length 2 is undefined.";
      $self->{error} = 1;
      return undef;
  }

  my $depth1 = $depth + $length1 - 1;
  my $depth2 = $depth + $length2 - 1;

  $score = 2 * $depth / ($depth1 + $depth2);

  $self->storeToCache ($wps1, $wps2, $score) if $self->{doCache};

  if ($self->{trace}) {
    $self->{traceString} .= "\nDepth(";
    $self->printSet ($pos1, 'wps', $in1);
    $self->{traceString} .= ") = $depth1\nDepth(";
    $self->printSet ($pos1, 'wps', $in2);
    $self->{traceString} .= ") = $depth2\n";
  }
  return $score;
}

###
# JM 1-16-04
# All of the code that used to follow here has been replaced by code in
# PathFinder.
###

1;

__END__

=back

=head2 Discussion

The Wu & Palmer measure calculates relatedness by considering the depths
of the two synsets in the WordNet taxonomies, along with the depth
of the LCS.  The formula is S<score = 2*depth(lcs) / (depth(s1) + depth(s2))>.
This means that S<0 < score <= 1>.  The score can never be zero because the
depth of the LCS is never zero (the depth of the root of a taxonomy is one).
The score is one if the two input synsets are the same.

=head1 USAGE

The semantic relatedness modules in this distribution are built as classes
that define the following methods:

  new()
  getRelatedness()
  getError()
  getTraceString()

See the WordNet::Similarity(3) documentation for details of these methods.

=head1 TYPICAL USAGE EXAMPLES

  use WordNet::Similarity::wup;
  my $measure->new($wn, 'wup.conf');

'$wn' contains a WordNet::QueryData object that should have been
constructed already.  The second (and optional) parameter to the 'new'
method is the path of a configuration file for the Wu-Palmer measure.
If the 'new' method is unable to construct the object, then '$measure'
will be undefined.  This may be tested.

  my ($error, $errorString) = $measure->getError ();
  die $errorString."\n" if $err;

To find the sematic relatedness of the first sense of the noun 'car' and
the second sense of the noun 'bus' using the measure, we would write
the following piece of code:

  $relatedness = $measure->getRelatedness('car#n#1', 'bus#n#2');

To get traces for the above computation:

  print $measure->getTraceString();

However, traces must be enabled using configuration files. By default
traces are turned off.

=head1 CONFIGURATION FILE

The behavior of the measures of semantic relatedness can be controlled
by using configuration files.  These configuration files specify how
certain parameters are initialized with the object.  A configuration file
may be specified as a parameter during the creation of an object using
the new method.  The configuration files must follow a fixed format.

Every configuration file starts with the name of the module ON THE FIRST
LINE of the file.  For example, a configuration file for the wup module
will have on the first line 'WordNet::Similarity::wup'.  This is followed
by the various parameters, each on a new line and having the form
'name::value'.  The 'value' of a parameter is option (in the case of boolean
parameters).  In case 'value' is omitted, we would have just 'name::' on 
that line.  Comments are allowed in the configuration file.  Anything
following a '#' is ignored till the end of the line.

The module parses the configuration file and recognizes the following
parameters:

=over

=item trace

The value of this parameter specifies the level of tracing that should
be employed for generating the traces. This value
is an integer equal to 0, 1, or 2. If the value is omitted, then the
default value, 0, is used. A value of 0 switches tracing off. A value
of 1 or 2 switches tracing on. A trace of level 1 means the synsets are
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

=item synsetDepthsFile

The value for this parameter should be a string that specifies the location
of a synset depths file (as generated by wnDepths.pl.  If no path is
specified, then the default file is used, which was generated when the
Similarity package was installed.

=back

=head1 SEE ALSO

perl(1), WordNet::Similarity(3), WordNet::QueryData(3)

http://www.d.umn.edu/~mich0212/

http://www.d.umn.edu/~tpederse/similarity.html

http://wordnet.princeton.edu

http://www.ai.mit.edu/people/jrennie/WordNet/

=head1 AUTHORS

  Ted Pedersen, University of Minnesota Duluth
  tpederse at d.umn.edu

  Jason Michelizzi, University of Minnesota Duluth
  mich0212 at d.umn.edu

  Siddharth Patwardhan, University of Utah, Salt Lake City
  sidd at cs.utah.edu

=head1 COPYRIGHT AND LICENSE

Copyright (c) 2005, Ted Pedersen, Jason Michelizzi and Siddharth Patwardhan

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
at <http://www.gnu.org/licenses/gpl.txt> and is included in this
distribution as GPL.txt.

=cut
