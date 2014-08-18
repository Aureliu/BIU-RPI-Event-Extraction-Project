# WordNet::Similarity::random.pm version 2.04
# (Last updated $Id: random.pm,v 1.15 2008/03/27 06:21:17 sidz1979 Exp $)
#
# Random semantic distance generator module.
#
# Copyright (c) 2005,
#
# Ted Pedersen, University of Minnesota Duluth
# tpederse at d.umn.edu
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

package WordNet::Similarity::random;

=head1 NAME

WordNet::Similarity::random - Perl module for computing semantic relatedness
of word senses using a random measure.

=head1 SYNOPSIS

  use WordNet::Similarity::random;

  use WordNet::QueryData;

  my $wn = WordNet::QueryData->new();

  my $random = WordNet::Similarity::random->new($wn);

  my $value = $random->getRelatedness("car#n#1", "bus#n#2");

  ($error, $errorString) = $random->getError();

  die "$errorString\n" if($error);

  print "car (sense 1) <-> bus (sense 2) = $value\n";

=head1 DESCRIPTION

This module generates random numbers as a measure of semantic relatedness
of word senses. It is possible to assign a random value for a word sense
pair and return the same value if the same word sense pair is passed as
input. It is also possible to generate a new random value for the same
word sense pair every time.

=head2 Methods

=over

=cut

use strict;

use WordNet::Similarity;

use vars qw($VERSION @ISA @EXPORT @EXPORT_OK %EXPORT_TAGS);

@ISA = qw(WordNet::Similarity);

%EXPORT_TAGS = ();

@EXPORT_OK = ();

@EXPORT = ();

$VERSION = '2.04';

WordNet::Similarity::addConfigOption ('maxrand', 1, 'f', 1.0);

=item $random->setPosList()

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
  $self->{a} = 1;
  $self->{r} = 1;
}

=item $random->initialize($file)

Overrides the initialize method in the parent class. This method
essentially initializes the measure for use.

Parameters: $file -- configuration file.

Returns: none.

=cut 

# Initialization of the WordNet::Similarity::random object... parses the config file and sets up
# global variables, or sets them to default values.
# INPUT PARAMS  : $paramFile .. File containing the module specific params.
# RETURN VALUES : (none)
sub initialize
{
  my $self = shift;
  $self->SUPER::initialize (@_);
  $self->{maxCacheSize} = WordNet::Similarity::UNLIMITED_CACHE;
}

=item $random->getRelatedness ($synset1, $synset2)

Returns a value for the relatedness of the two synsets.  This value is
a random number greater-than or equal-to zero and less-than 'maxrand'.

If the synsets are not properly formed word#pos#sense strings, or if they
are not found in WordNet, then the error level will be set to non-zero and
an error string will be generated.

=cut

sub getRelatedness
{
  my $self = shift;
  my $wps1 = shift;
  my $wps2 = shift;
  my $wn = $self->{wn};
  my $class = ref $self || $self;

  # check if the synsets are well-formed and are found in WordNet
  my $ref = $self->parseWps ($wps1, $wps2);
  ref $ref or return $ref;

  # Initialize traces.
  $self->{traceString} = "";

  # Now check if the similarity value for these two synsets is in
  # fact in the cache... if so return the cached value.
  my $relatedness =
    $self->{doCache} ? $self->fetchFromCache ($wps1, $wps2) : undef;
  defined $relatedness and return $relatedness;

  # Now get down to really finding the relatedness of these two.

  my $score = rand ($self->{maxrand});
  $score = sprintf ("%.3f", $score);

  $self->{doCache} and $self->storeToCache ($wps1, $wps2, $score);
  return $score;
}

=item $random->traceOptions()

This method is internally called to determine the extra options
specified by this measure (apart from the default options specified
in the WordNet::Similarity base class).

Parameters: none.

Returns: none.

=cut 

# 12/5/03 JM (#1)
# show all config options specific to this module
sub traceOptions {
  my $self = shift;
  $self->{traceString} .= "maxrand :: $self->{maxrand}\n";
  $self->SUPER::traceOptions();
}

1;

__END__

=back

=head2 Usage

The semantic relatedness modules in this distribution are built as classes
that define the following methods:

  new()
  getRelatedness()
  getError()
  getTraceString()

See the WordNet::Similarity(3) documentation for details of these methods.

=head3 Typical Usage Examples

To create an object of the random measure, we would have the following
lines of code in the Perl program.

   use WordNet::Similarity::random;
   $measure = WordNet::Similarity::random->new($wn, '/home/sid/random.conf');

The reference of the initialized object is stored in the scalar variable
'$measure'. '$wn' contains a WordNet::QueryData object that should have been
created earlier in the program. The second parameter to the 'new' method is
the path of the configuration file for the random measure. If the 'new'
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
the file. For example, a configuration file for the random module will have
on the first line 'WordNet::Similarity::random'. This is followed by the various
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
of 1 or 2 switches tracing on, but the random measure is so simple, that
generating traces makes little senses, so this option has no effect.

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

=item maxrand

The value of this option is the maximum random number that will be generated.
The value of this option must be a positive floating-point number.  The
default value is 1.0.  All random numbers generated will be in the range
[0, maxrand).

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

=head1 BUGS

None.

To report a bug, go to http://groups.yahoo.com/group/wn-similarity/
or e-mail "S<tpederse at d.umn.edu>".

=head1 COPYRIGHT AND LICENSE

Copyright (c) 2005, Ted Pedersen and Siddharth Patwardhan

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
