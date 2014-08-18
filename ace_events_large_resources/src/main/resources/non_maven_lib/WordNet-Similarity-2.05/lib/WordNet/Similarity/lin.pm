# WordNet::Similarity::lin.pm version 2.04
# (Last updated $Id: lin.pm,v 1.22 2008/03/27 06:21:17 sidz1979 Exp $)
#
# Semantic Similarity Measure package implementing the measure
# described by Lin (1998).
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

package WordNet::Similarity::lin;

=head1 NAME

WordNet::Similarity::lin - Perl module for computing semantic relatedness
of word senses using the information content based measure described by
Lin (1998).

=head1 SYNOPSIS

  use WordNet::Similarity::lin;

  use WordNet::QueryData;

  my $wn = WordNet::QueryData->new();

  my $mymeasure = WordNet::Similarity::lin->new($wn);

  my $value = $mymeasure->getRelatedness("car#n#1", "bus#n#2");

  ($error, $errorString) = $mymeasure->getError();

  die "$errorString\n" if($error);

  print "car (sense 1) <-> bus (sense 2) = $value\n";

=head1 DESCRIPTION

Lin (1998) describes a method to compute the semantic relatedness of word
senses using the information content of the concepts in WordNet and the
'Similarity Theorem' (also described in the paper). This module implements
this measure of semantic relatedness of concepts.

=over

=cut

use strict;
use WordNet::Similarity::ICFinder;

our @ISA = qw(WordNet::Similarity::ICFinder);

our $VERSION = '2.04';

=item $lin->getRelatedness ($synset1, $synset1)

Computes the relatedness of two word senses using an information content
scheme.  The relatedness is equal to twice the information content of the
LCS divided by the sum of the information content of each input synset.

Parameters: two word senses in "word#pos#sense" format.

Returns: Unless a problem occurs, the return value is the relatedness
score.  If no path exists between
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
  my $wn = $self->{'wn'};
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
  # moved input validation code to WordNet::Similarity::parseInput()
  my $ret = $self->parseWps ($wps1, $wps2);
  ref $ret or return $ret;
  my ($word1, $pos1, $sense1, $offset1, $word2, $pos2, $sense2, $offset2)
    = @{$ret};

  my $synset1 = "$word1#$pos1#$sense1";
  my $synset2 = "$word2#$pos2#$sense2";

  # Now check if the similarity value for these two synsets is in
  # fact in the cache... if so return the cached value.
  my $relatedness =
    $self->{doCache} ? $self->fetchFromCache ($synset1, $synset2) : undef;
  defined $relatedness and return $relatedness;

  # Now get down to really finding the relatedness of these two.
  my @LCSs = $self->getLCSbyIC ($synset1, $synset2, $pos1, 'wps');

  my $ref = shift @LCSs;

  unless (defined $ref) {
    return $self->UNRELATED;
  }

  my ($lcs, $lcsic) = @{$ref};

  my $ic1 = $self->IC ($offset1, $pos1);
  my $ic2 = $self->IC ($offset2, $pos2);
  my $score = ($ic1 && $ic2) ? ((2 * $lcsic) / ($ic1 + $ic2)) : 0;

  # what does this do?
  $score = ($score == -1) ? 0 : $score;

  if ($self->{trace}) {
    $self->{traceString} .= "Concept1: $synset1 (IC=";
    $self->{traceString} .= sprintf ("%.6f", $ic1);
    $self->{traceString} .= ")\n";
    $self->{traceString} .= "Concept2: $synset2 (IC=";
    $self->{traceString} .= sprintf ("%.6f", $ic2);
    $self->{traceString} .= ")\n";
  }

  $self->{doCache} and $self->storeToCache ($wps1, $wps2, $score);
  return $score;
}

# JM 1-16-04
# moved subroutine _getLeastCommonSubsumers to ICFinder.pm

1;

__END__

=back

=head2 Discussion

The relatedness value returned by the lin measure is a number equal to
S<2 * IC(lcs) / (IC(synset1) + IC(synset2))>.  Where IC(x) is the information
content of x.  One can observe, then, that the relatedness value will be
greater-than or equal-to zero and less-than or equal-to one.

If the information content of any of either synset1 or synset2 is zero,
then zero is returned as the relatedness score, due to lack of data.
Ideally, the information content of a synset would be zero only if that
synset were the root node, but when the frequency of a synset is zero,
we use the value of zero as the information content because of a lack
of better alternatives.

=head2 Usage

The semantic relatedness modules in this distribution are built as classes
that expose the following methods:
  new()
  getRelatedness()
  getError()
  getTraceString()

See the WordNet::Similarity(3) documentation for details of these methods.

=head3 Typical Usage Examples

To create an object of the lin measure, we would have the following
lines of code in the Perl program.

   use WordNet::Similarity::lin;
   $measure = WordNet::Similarity::lin->new($wn, '/home/sid/lin.conf');

The reference of the initialized object is stored in the scalar variable
'$measure'. '$wn' contains a WordNet::QueryData object that should have been
created earlier in the program. The second parameter to the 'new' method is
the path of the configuration file for the lin measure. If the 'new'
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
the file. For example, a configuration file for the lin module will have
on the first line 'WordNet::Similarity::lin'. This is followed by the various
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

  Siddharth Patwardhan, University of Utah, Salt Lake City
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
