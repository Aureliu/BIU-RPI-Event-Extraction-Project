# WordNet::stem.pm version 2.04
# (Last updated $Id: stem.pm,v 1.1 2008/03/27 05:13:01 sidz1979 Exp $)
#
# Package used by WordNet::Similarity::lesk module that
# computes semantic relatedness of word senses in WordNet
# using gloss overlaps.
#
# Copyright (c) 2005,
#
# Ted Pedersen, University of Minnesota Duluth
# tpederse at d.umn.edu
#
# Satanjeev Banerjee, Carnegie Mellon University, Pittsburgh
# banerjee+ at cs.cmu.edu
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

package WordNet::stem;

=head1 NAME

WordNet::stem - Module that find the stem of a word or the stems of a
string of words, using WordNet.

=head1 SYNOPSIS

  use WordNet::stem;

  my $wn = WordNet::QueryData->new();

  my $stemmer = WordNet::stem->new($wn)

  my @stems = $stemmer->stemWord($word);

  my $string = $stemmer->stemString($inString, $cache);

=head1 DESCRIPTION

This module uses the internal stemming algorithm of WordNet to
stem words and strings of words. This module is used by the
lesk measure of the WordNet::Similarity package.

=head2 Methods

=over

=cut

use strict;
use Exporter;
use vars qw($VERSION @ISA @EXPORT @EXPORT_OK %EXPORT_TAGS);

@ISA = qw(Exporter);

%EXPORT_TAGS = ();

@EXPORT_OK = ();

@EXPORT = ();

$VERSION = '2.04';

=item new

Creates a new stemmer object and initilizes it with a 
WordNet::QueryData object.

Parameters: $wn

Returns: $stemmer

=cut

# function to create the stemmer object
sub new
{
    my $className = shift;
    my $wn = shift;
    my $self = {};

    $self->{wn} = $wn;
    $self->{wordStemHash} = ();
    $self->{stringStemHash} = ();
    bless($self, $className);

    return $self;
}

=item stemString

Takes a string of words as input and returns a string of stemmed words.

Parameters: $inString

Returns: $retString

=cut

# Function to take a string, and process it in such a way that all the
# words in it get stemmed. Note that if a single word has two or more
# possible stems, we return the original surface form since there is
# no way to select from the competing stems. The stem of the string
# can be cached if requested. Useful if the calling function knows
# which strings it will have to stem over and over again. Strings that
# will be only stemmed ones need not be cached - thereby saving space.
sub stemString
{
    my $self = shift;
    my $inputString = shift;
    my $cache = shift;
    
    # whether or not this string has been requested for cacheing,
    # check in the cache
    return $self->{'stringStemHash'}->{$inputString} if (defined $self->{'stringStemHash'}->{$inputString});
    
    # Not in cache. Stem.
    
    # for each word in the input get the stem and put in the output string
    my $outputString = "";
    while ($inputString =~ /(\w+)/g)
    {
	my $word = $1;
	my @stems = $self->stemWord($word);
	
	# if multiple or no stems, use surface form.
	$outputString .= ($#stems != 0) ? "$word " : "$stems[0] ";
    }
    
    # if cache required, do so
    $self->{'stringStemHash'}->{$inputString} = $outputString if (defined($cache));
    
    # return the string
    return($outputString);
}

=item stemWord

Takes a word as input and returns its stems. A word may have more than
one stem. All are returned.

Parameters: $word

Returns: @stems

=back

=cut

# stem the word passed to this function and return an array of words
# that contain all the possible stems of this word. All possible stems
# of the word may include the surface form too if its a valid WordNet
# lemma.
sub stemWord
{
    my $self = shift;
    my $word = shift;
    my $wn = $self->{wn};
    my @stems = ();
    
    # if not in the cache, create and put in cache
    if (!defined $self->{wordStemHash}->{$word})
    {
	# So not in the hash. gotta check for all possible parts of speech.
	my %stems = ();
	my $possiblePartsOfSpeech = "nvar";
	
	my $pos;
	while ("nvar" =~ /(.)/g)
	{
	    foreach ($wn->validForms("$word\#$1"))
	    {
		# put underscore for space
		$_ =~ s/ /_/g;
		
		# remove part of speech if any
		$_ =~ s/\#\w$//;
		
		# put in stems hash (the hash allows us to not worry about
		# multiple copies of the same stem!)
		$stems{$_} = 1;
	    }
	}
	
	# put in the cache
	$self->{wordStemHash}->{$word} = join(" ", (keys %stems));
    }
    
    # return the stems
    return (split / /, $self->{wordStemHash}->{$word});
}

1;

__END__

=head1 SEE ALSO

perl(1), WordNet::Similarity(3), WordNet::QueryData(3)

http://www.cs.utah.edu/~sidd

http://wordnet.princeton.edu

http://www.ai.mit.edu/~jrennie/WordNet

http://groups.yahoo.com/group/wn-similarity

=head1 AUTHORS

 Ted Pedersen, University of Minnesota Duluth
 tpederse at d.umn.edu

 Satanjeev Banerjee, Carnegie Mellon University, Pittsburgh
 banerjee+ at cs.cmu.edu

=head1 BUGS

None.

To report bugs, go to http://groups.yahoo.com/group/wn-similarity/ or
e-mail "S<tpederse at d.umn.edu>".

=head1 COPYRIGHT AND LICENSE

Copyright (c) 2005, Ted Pedersen and Satanjeev Banerjee

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
