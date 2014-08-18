# WordNet::Similarity::lch.pm version 2.04
# (Last update $Id: lch.pm,v 1.24 2008/03/27 06:21:17 sidz1979 Exp $)
#
# Semantic Similarity Measure package implementing the measure
# described by Leacock and Chodorow (1998).
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

package WordNet::Similarity::lch;

=head1 NAME

WordNet::Similarity::lch - Perl module for computing semantic relatedness
of word senses using the method described by Leacock and Chodorow (1998).

=head1 SYNOPSIS

  use WordNet::Similarity::lch;

  use WordNet::QueryData;

  my $wn = WordNet::QueryData->new();

  my $myobj = WordNet::Similarity::lch->new($wn);

  my $value = $myobj->getRelatedness("car#n#1", "bus#n#2");

  ($error, $errorString) = $myobj->getError();

  die "$errorString\n" if($error);

  print "car (sense 1) <-> bus (sense 2) = $value\n";

=head1 DESCRIPTION

This module computes the semantic relatedness of word senses according
to a method described by Leacock and Chodorow (1998). This method counts up
the number of edges between the senses in the 'is-a' hierarchy of WordNet.
The value is then scaled by the maximum depth of the WordNet 'is-a'
hierarchy. A relatedness value is obtained by taking the negative log
of this scaled value.

=head2 Methods

=over

=cut

use strict;
use Exporter;
use WordNet::Similarity::DepthFinder;

our @ISA = qw/WordNet::Similarity::DepthFinder/;

our $VERSION = '2.04';

=item $lch->setPosList()

This method is internally called to determine the parts of speech
this measure is capable of dealing with.

Parameters: none.

Returns: none.

=cut

sub setPosList
{
  my $self = shift;
  $self->{n} = 1;
  $self->{v} = 1;
}

=item $lch->getRelatedness ($synset1, $synset2)

Computes the relatedness of two word senses using a node counting scheme.
For details on how relatedness is computed, see the Discussion section
below.

Parameters: two word senses in "word#pos#sense" format.

Returns: Unless a problem occurs, the return value is the relatedness
score.  If no path exists between the two word senses, then a large
negative number is returned.  If an error occurs, then the error level
is set to non-zero and an error string is created (see the description
of getError()).  Note: the error level will also be set to 1 and an error
string will be created if no path exists between the words.

=cut

sub getRelatedness
{
    my $self = shift;
    my $wps1 = shift;
    my $wps2 = shift;
    my $wn = $self->{wn};

    my $class = ref $self || $self;

    unless ($wn) {
	$self->{errorString} .= "\nError (${class}::getRelatedness()) - ";
	$self->{errorString} .= "A WordNet::QueryData object is required.";
	$self->{error} = 2;
	return undef;
    }

    # Initialize traces.
    $self->{traceString} = "";

    # JM 1-21-04
    # moved input validation code to parseWps() in a super-class
    my $ret = $self->parseWps ($wps1, $wps2);
    ref $ret or return $ret;
    my ($word1, $pos1, $sense1, $offset1, $word2, $pos2, $sense2, $offset2)
      = @{$ret};

    my $pos = $pos1;

    # Now check if the similarity value for these two synsets is in
    # fact in the cache... if so return the cached value.
    my $relatedness =
      $self->{doCache} ? $self->fetchFromCache ($wps1, $wps2) : undef;
    defined $relatedness and return $relatedness;

    # Now get down to really finding the relatedness of these two.

    # JM 3-9-04
    # Modified to use the methods of DepthFinder et al.

    my @LCSs = $self->getLCSbyPath ($offset1, $offset2, $pos1, 'offset');

    # check if there is no path between synsets
    unless (defined $LCSs[0]) {
      return $self->UNRELATED;
    }

    # find the LCS (well, path really) that is in the deepest taxonomy
    my $maxdepth = -1;
    my $length;
    foreach (@LCSs) {
	my $lcs;
	($lcs, $length) = @{$_};

	my @roots = $self->getTaxonomies ($lcs, $pos1);

	foreach my $root (@roots) {
	    my $depth = $self->getTaxonomyDepth ($root, $pos1);
	    unless (defined $depth) {
		$self->{error} = $self->{error} < 1 ? 1 : $self->{error};
		$self->{errorString} .="\nWarning (${class}::getRelatedness()) - ";
		$self->{errorString} .= "Taxonomy depth for $root undefined.";
		return undef;
	    }
	    $maxdepth = $depth if $depth > $maxdepth;
	}
    }

    if ($maxdepth <= 0) {
	$self->{error} = $self->{error} < 1 ? 1 : $self->{error};
	$self->{errorString} .= "\nWarning (${class}::getRelatedness()) - ";
	$self->{errorString} .= "Max depth of taxonomy is not positive.";
	return undef;
    }

    my $score = log (2 * $maxdepth / $length);

    $self->storeToCache ($offset1, $offset2, $score);

    return $score;
}

1;

__END__

=back

=head2 Discussion

The relatedness measure proposed by Leacock and Chodorow is
S<-log (length / (2 * D))>, where length is the length of the shortest
path between the two synsets (using node-counting) and D is the
maximum depth of the taxonomy.

The fact that the lch measure takes into account the depth of the taxonomy
in which the synsets are found means that the behavior of the measure is
profoundly affected by the presence or absence of a unique root node.  If
there is a unique root node, then there are only two taxonomies: one for
nouns and one for verbs.  All nouns, then, will be in the same taxonomy and
all verbs will be in the same taxonomy.  D for the noun taxonomy will be
somewhere around 18, depending upon the version of WordNet, and for verbs,
it will be 14. If the root node is not being used, however, then there are
nine different noun taxonomies and over 560 different verb taxonomies, each
with a different value for D.

If the root node is not being used, then it is possible for synsets to belong
to more than one taxonomy.  For example, the synset containing turtledove#n#2
belongs to two taxonomies:  one rooted at group#n#1 and one rooted at
entity#n#1.  In such a case, the relatedness is computed by finding the LCS
that results in the shortest path between the synsets.  The value of D, then,
is the maximum depth of the taxonomy in which the LCS is found.  If the LCS
belongs to more than one taxonomy, then the taxonomy with the greatest
maximum depth is selected (i.e., the largest value for D).

=head2 Usage

The semantic relatedness modules in this distribution are built as classes
that define the following methods:

  new()
  getRelatedness()
  getError()
  getTraceString()

See the WordNet::Similarity(3) documentation for details of these methods.

=head3 Typical Usage Examples

To create an object of the lch measure, we would have the following
lines of code in the Perl program.

   use WordNet::Similarity::lch;
   $measure = WordNet::Similarity::lch->new($wn, '/home/sid/lch.conf');

The reference of the initialized object is stored in the scalar variable
'$measure'. '$wn' contains a WordNet::QueryData object that should have been
created earlier in the program. The second parameter to the 'new' method is
the path of the configuration file for the lch measure. If the 'new'
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

Every configuration file starts with the name of the module ON THE
FIRST LINE of the file. For example, a configuration file for the
WordNet::Similarity::lch module will have on the first line
'WordNet::Similarity::lch'. This is followed by the various
parameters, each on a new line and having the form 'name::value'. The
'value' of a parameter is optional (in case of boolean parameters). In
case 'value' is omitted, we would have just 'name::' on that
line. Comments are supported in the configuration file. Anything
following a '#' is ignored till the end of the line.

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

=item taxonomyDepthsFile

The value for this parameter should be a string that specifies the location
of a taxonomy depths file (as generated by wnDepths.pl). If no path is
specified, then the default file is used, which was generated when the
Similarity package was installed.

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

To report bugs, go to http://groups.yahoo.com/group/wn-similarity/ or
e-mail tpederse at d.umn.edu.

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
at <http://www.gnu.org/licenses/gpl.txt> and is included in this
distribution as GPL.txt.

=cut
