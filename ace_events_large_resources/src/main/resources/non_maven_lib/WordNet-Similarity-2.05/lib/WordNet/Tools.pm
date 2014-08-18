# WordNet::Tools v2.05
# (Last updated $Id: Tools.pm,v 1.5 2008/06/04 18:38:01 sidz1979 Exp $)
#
# This module provides some WordNet tools for use with the
# WordNet::Similarity modules.
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

package WordNet::Tools;

=head1 NAME

WordNet::Tools - Some tools for use with WordNet.

=head1 SYNOPSIS

  use WordNet::QueryData;

  use WordNet::Tools;

  my $wn = WordNet::QueryData->new;

  my $wntools = WordNet::Tools->new($wn);

  my $wnHashCode = $wntools->hashCode();

  my $newstring = $wntools->compoundify("find compound words like new york city in this text");

=head1 DESCRIPTION

This module provides some tools for use with WordNet. For example, the
'compoundify' method detects compound words (as found in WordNet) in a
text string and it combines these words into single tokens using
underscore separators. Another tool in this module generates a unique
hash code corresponding to a WordNet distribution. This hash code is
meant to replace the "version" information in WordNet, which is no
longer reliable.

=head1 METHODS

The following methods are defined:

=over

=cut

use strict;
use warnings;
use Exporter;
use WordNet::QueryData;
use Digest::SHA1  qw(sha1_base64);

use constant MAX_COMPOUND_SIZE => 9;

our @ISA = qw(Exporter);
our $VERSION = '2.05';

=item WordNet::Tools->new($wn)

This is a constructor for this class (and creates a new object of this
class). It requires a WordNet::QueryData object as a parameter.

Parameters: $wn -- a WordNet::QueryData object.

Returns: a new WordNet::Tools object.

=cut

# Constructor for this module
sub new
{
  my $class = shift;
  my $wn    = shift;
  my $self  = {};

  # Create the preprocessor object
  $class = ref $class || $class;
  bless($self, $class);

  # Verify the given WordNet::QueryData object
  return undef if(!defined $wn || !ref $wn || ref($wn) ne "WordNet::QueryData");
  $self->{wn} = $wn;

  # Get the compounds from WordNet
  foreach my $pos ('n', 'v', 'a', 'r')
  {
    foreach my $word ($wn->listAllWords($pos))
    {
      $self->{compounds}->{$word} = 1 if ($word =~ /_/);
    }
  }

  # Compute the WordNet hash-code and store
  $self->{hashcode} = $self->_computeHashCode();
  return undef if(!defined($self->{hashcode}));

  return $self;
}

=item $wntools->compoundify($string)

This is method identifies all compound words occurring in the given input
string. Compound words are multi-word tokens appearing in WordNet.

Parameters: $string -- an input text string.

Returns: a string with compound words identified.

=cut

# Detect compounds in a block of text
sub compoundify
{
  my $self  = shift;
  my $block = shift;

  return $block if(!defined $block || !ref $self || !defined $self->{compounds});

  my $string;
  my $done;
  my $temp;
  my $firstPointer;
  my $secondPointer;
  my @wordsArray;

  # get all the words into an array
  @wordsArray = ();
  while($block =~ /([a-zA-Z0-9_\.\-\/\']+)/g)
  {
    push(@wordsArray, $1);
  }

  # now compoundify, GREEDILY!!
  $firstPointer = 0;
  $string = "";

  while($firstPointer <= $#wordsArray)
  {
    $secondPointer = (($#wordsArray > ($firstPointer + MAX_COMPOUND_SIZE - 1)) ? ($firstPointer + MAX_COMPOUND_SIZE - 1) : ($#wordsArray));
    $done = 0;
    while(($secondPointer > $firstPointer) && !$done)
    {
      $temp = join("_", @wordsArray[$firstPointer .. $secondPointer]);
      if(defined $self->{compounds}->{$temp})
      {
        $string .= "$temp ";
        $done = 1;
      }
      else
      {
        $secondPointer--;
      }
    }
    $string .= "$wordsArray[$firstPointer] " unless($done);
    $firstPointer = $secondPointer + 1;
  }
  $string =~ s/\s+$//;

  return $string;
}

=item $wntools->getCompoundsList()

This method returns the list of compound words present in WordNet.

Parameters: none

Returns: reference to an array of compounds.

=cut

# Return the list of WordNet compounds
# Since a deep-copy is performed, this method can be slow. Consequently,
# this method should be used sparingly
sub getCompoundsList
{
  my $self = shift;
  my @cList = keys(%{$self->{compounds}});
  return \@cList;
}

=item $wntools->hashCode()

This is method returns a unique identifier representing a specific
distribution of WordNet.

Parameters: none.

Returns: a unique identifier (string).

=cut

# Return the computed hash-code
sub hashCode
{
  my $self = shift;
  return $self->{hashcode};
}

# Compute the hash code for the given WordNet distribution
# Most of this code was written by Ben Haskell <ben at clarity dot princeton dot edu>
sub _computeHashCode
{
  my $self = shift;
  my $qd = $self->{wn};
  return undef if(!defined($qd));

  my $dir = $qd->dataPath();
  my $pos = '{noun,verb,adj,adv}';
  my @files = sort grep -f, map glob("\Q$dir\E/$_"), "{index,data}.$pos", "$pos.{idx,dat}";

  # (stat)[7] returns file size in bytes
  my $concat = join '.', map { (stat)[7] } @files;
  return sha1_base64($concat);
}

1;

__END__

=back

=head1 EXPORT

None by default.

=head1 SEE ALSO

perl(1)

WordNet::QueryData(3)

=head1 AUTHORS

  Ted Pedersen, University of Minnesota, Duluth
  tpederse at d.umn.edu

  Siddharth Patwardhan, University of Utah, Salt Lake City
  sidd at cs.utah.edu

=head1 COPYRIGHT AND LICENSE

Copyright (c) 2005, Ted Pedersen and Siddharth Patwardhan

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
