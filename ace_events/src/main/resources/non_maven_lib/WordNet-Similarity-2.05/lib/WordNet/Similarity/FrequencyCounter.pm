# WordNet::Similarity::FrequencyCounter.pm version 2.05
# (Last updated $Id: FrequencyCounter.pm,v 1.1 2008/05/30 23:12:45 sidz1979 Exp $)
#
# Module providing support functions for frequency counting
# programs used to estimate the information content of concepts.
#
# Copyright (c) 2008,
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

package WordNet::Similarity::FrequencyCounter;

=head1 NAME

WordNet::Similarity::FrequencyCounter - Support functions for frequency
counting programs used to estimate the information content of concepts.

=head1 METHODS

=over

=cut

use strict;
use Carp;
use Exporter;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS);

@ISA = qw(Exporter);
%EXPORT_TAGS = ();
@EXPORT_OK = ();
@EXPORT = ();
$VERSION = '2.05';

=item createTopHash ($wn)

Creates and loads the topmost nodes hash from WordNet.

returns: $topHash, a reference to a hash containing the topmost nodes
of the noun and verb hierarchies in WordNet.

=cut

# Creates and loads the topmost nodes hash.
sub createTopHash
{
  my $wn = shift;
  return undef if(!defined($wn));
  
  my $topHash = {};
  my $datapath = $wn->dataPath();
  my $unixfile_n = "${datapath}/data.noun";
  my $windozefile_n = "${datapath}\\noun.dat";
  my $nounfile = -e $windozefile_n ? $windozefile_n : $unixfile_n;
  open(NFH, '<', $nounfile) or die "Cannot open '$nounfile': $!";
  while (<NFH>)
  {
    next if "  " eq substr $_, 0, 2;
    next if / \@i? \d\d\d\d\d\d\d\d /;
    next unless /^(\d\d\d\d\d\d\d\d) /;
    my $offset = $1 + 0;

    # QueryData::getSense will die() if the $offset and pos are not
    # found.  Putting this in an eval will catch the exception.  See
    # perldoc -f eval
    my $wps;
    eval {$wps = $wn->getSense($offset, 'n')};
    if($@)
    {
      die "(offset '$offset' not found) $@";
    }
    $topHash->{n}->{$offset} = 1;
  }
  close NFH;
  my $unixfile_v = "${datapath}/data.verb";
  my $windozefile_v = "${datapath}\\verb.dat";
  my $verbfile = -e $windozefile_v ? $windozefile_v : $unixfile_v;
  open(VFH, '<', $verbfile) or die "Cannot open '$verbfile': $!";
  while (<VFH>)
  {
    next if " " eq substr($_, 0, 2);
    next if / \@i? \d\d\d\d\d\d\d\d /;
    next unless /^(\d\d\d\d\d\d\d\d) /;
    my $offset = $1 + 0;
    $topHash->{v}->{$offset} = 1;
  }
  close VFH;
  
  return $topHash;
}

=item updateWordFrequency ($word, $offsetFreq, $wn [, $opt_resnik])

Updates the counts of a given word in a given offset-frequency hash. It
finds the offsets of all concepts corresponding to the given word in
WordNet, and increments the frequency counts of these in the hash.

returns: nothing.

=cut

# Subroutine to update frequency tokens on "seeing" a word in text
sub updateWordFrequency
{
  my $word = shift;
  my $offsetFreq = shift;
  my $wn = shift;
  my $opt_resnik = shift;
  die "Input word not defined.\n" if(!defined($word));
  die "Offset-frequency hash reference not provided.\n" if(!defined($offsetFreq));
  die "WordNet::QueryData reference not provided.\n" if(!defined($wn));

  foreach my $pos ("n", "v")
  {
    my @forms = $wn->validForms($word."\#".$pos);
    my @senses = ();
    foreach my $form (@forms)
    {
      push @senses, $wn->querySense($form);
    }
    foreach (@senses)
    {
      if(defined $opt_resnik)
      {
        $offsetFreq->{$pos}->{$wn->offset($_)} += (1/($#senses + 1));
      }
      else
      {
        $offsetFreq->{$pos}->{$wn->offset($_)}++;
      }
    }
  }
}

=item propagateFrequency ($offsetFreq, $wn, $topHash)

This function propagates the frequencies up the is-a hierarchies in
WordNet, and return a hash with the new frequency counts.

returns: $newFreq, hash containing updated counts.

=cut

# Subroutine that propagates the frequencies up through WordNet
sub propagateFrequency
{
  my $offsetFreq = shift;
  my $wn = shift;
  my $topHash = shift;
  die "Offset-frequency hash not defined.\n" if(!defined($offsetFreq));
  die "WordNet::QueryData object not provided.\n" if(!defined($wn));
  die "Topmost nodes hash not provided.\n" if(!defined($topHash));
  
  my $newFreq = {};
  $offsetFreq->{n}->{0} = 0 if(!defined($offsetFreq->{n}->{0}));
  $offsetFreq->{v}->{0} = 0 if(!defined($offsetFreq->{v}->{0}));
  _propagateFrequency($newFreq, 0, "n", $topHash, $wn, $offsetFreq);
  _propagateFrequency($newFreq, 0, "v", $topHash, $wn, $offsetFreq);
  delete $newFreq->{n}->{0};
  delete $newFreq->{v}->{0};
  
  return $newFreq;
}

# Recursive subroutine that propagates the frequencies up through WordNet
sub _propagateFrequency
{
  my $newFreq = shift;
  my $node = shift;
  my $pos = shift;
  my $topHash = shift;
  my $wn = shift;
  my $offsetFreq = shift;
  die "Offset-frequency hash not defined.\n" if(!defined($offsetFreq));
  die "WordNet::QueryData object not provided.\n" if(!defined($wn));
  die "Topmost nodes hash not provided.\n" if(!defined($topHash));
  die "Curent frequency hash not defined.\n" if(!defined($newFreq));

  return $newFreq->{$pos}->{$node} if(defined($newFreq->{$pos}->{$node}));
  my $retValue = &_getHyponymOffsets($node, $pos, $topHash, $wn);
  if(!defined($retValue))
  {
    $newFreq->{$pos}->{$node} =
        $offsetFreq->{$pos}->{$node}
      ? $offsetFreq->{$pos}->{$node}
      : 0;
    return $offsetFreq->{$pos}->{$node}
      ? $offsetFreq->{$pos}->{$node}
      : 0;
  }
  my $sum = 0;
  if($#{$retValue} >= 0)
  {
    foreach my $hyponym (@{$retValue})
    {
      $sum += &_propagateFrequency($newFreq, $hyponym, $pos, $topHash, $wn, $offsetFreq);
    }
  }
  $newFreq->{$pos}->{$node} = (
      $offsetFreq->{$pos}->{$node}
    ? $offsetFreq->{$pos}->{$node}
    : 0
  ) + $sum;
  return (
      $offsetFreq->{$pos}->{$node}
    ? $offsetFreq->{$pos}->{$node}
    : 0
  ) + $sum;
}

# Subroutine that returns the hyponyms of a given synset.
sub _getHyponymOffsets
{
  my $offset = shift;
  my $pos = shift;
  my $topHash = shift;
  my $wn = shift;
  die "WordNet::QueryData object not provided.\n" if(!defined($wn));
  die "Topmost nodes hash not provided.\n" if(!defined($topHash));

  if($offset == 0)
  {
    my @retVal = keys %{$topHash->{$pos}};
    return [@retVal];
  }
  my $wordForm = $wn->getSense($offset, $pos);
  my @hyponyms = $wn->querySense($wordForm, "hypos");
  if(!@hyponyms || $#hyponyms < 0)
  {
    return undef;
  }
  my @retVal = ();
  foreach my $hyponym (@hyponyms)
  {
    $offset = $wn->offset($hyponym);
    push @retVal, $offset;
  }
  return [@retVal];
}

1;

__END__

=back

=head1 AUTHORS

  Ted Pedersen, University of Minnesota Duluth
  tpederse at d.umn.edu

  Siddharth Patwardhan, University of Utah, Salt Lake City
  sidd at cs.utah.edu

=head1 BUGS

None.

To submit a bug report, go to http://groups.yahoo.com/group/wn-similarity or
send e-mail to tpederse I<at> d.umn.edu.

=head1 SEE ALSO

perl(1), WordNet::Similarity(3)

http://www.cs.utah.edu/~sidd

http://wordnet.princeton.edu

http://www.ai.mit.edu/~jrennie/WordNet

http://groups.yahoo.com/group/wn-similarity

=head1 COPYRIGHT

Copyright (c) 2008, Ted Pedersen and Siddharth Patwardhan

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
