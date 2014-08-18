# WordNet::Similarity::GlossFinder version 2.04
# (Last updated $Id: GlossFinder.pm,v 1.13 2008/03/27 06:21:17 sidz1979 Exp $)
#
# Module containing gloss-finding code for the various measures of semantic
# relatedness (lesk, vector).
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

package WordNet::Similarity::GlossFinder;

=head1 NAME

WordNet::Similarity::GlossFinder - module to implement gloss finding methods
for WordNet::Similarity measures of semantic relatedness (specifically, lesk 
and vector)

=head1 SYNOPSIS

  use WordNet::QueryData;
  my $wn = WordNet::QueryData->new;
  defined $wn or die "Construction of WordNet::QueryData failed";

  use WordNet::Similarity::GlossFinder;
  my $obj = WordNet::Similarity::GlossFinder->new ($wn);
  my ($err, $errString) = $obj->getError ();
  $err and die $errString;

  my $wps1 = 'england#n#1';
  my $wps2 = 'winston_churchill#n#1';

  # get the glosses of these two synsets, since we are not using a 
  # configuation file to specify relations, we will only get the 
  # immediate glosses of the two wps entries. The default weight and
  # relation appear in $weight and $relation - these can be modified
  # via a configuration file. 

  my ($wps1gloss, $wps2gloss, $weight, $relation ) = $obj -> getSuperGlosses ($wps1, $wps2);
  print "$wps1gloss->[0]\n";
  print "$wps2gloss->[0]\n";
  print "$weight->[0]\n";
  print "$relation->[0]\n";

=head1 DESCRIPTION

=head2 Introduction

This class is derived from (i.e., is a sub-class of) WordNet::Similarity. Two
of the measures of similarity, provided in this package, viz. WordNet::Similarity::lesk
and WordNet::Similarity::vector deal with WordNet glosses. This module provides
methods for easy access to the required glosses.

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
use WordNet::get_wn_info;

our @ISA = qw/WordNet::Similarity/;

our $VERSION = '2.04';

WordNet::Similarity::addConfigOption("relation", 0, "p", undef);
WordNet::Similarity::addConfigOption("stop", 0, "p", undef);
WordNet::Similarity::addConfigOption("stem", 0, "i", 0);

=item $measure->setPosList(Z<>)

Specifies the parts of speech that measures derived from this module
support (namely, nouns, verbs, adjectives and adverbs).

parameters: none

returns: true

=cut

sub setPosList
{
    my $self = shift;
    $self->{n} = 1;
    $self->{v} = 1;
    $self->{a} = 1;
    $self->{r} = 1;
    return 1;
}

=item $self->traceOptions(Z<>)

Overrides method of same name in WordNet::Similarity.  Prints module-specific
configuration options to the trace string (if tracing is on).  GlossFinder
supports module specific options: relation, stop and stem.

Parameters: none

Returns: nothing

=cut

sub traceOptions
{
    my $self = shift;
    $self->{traceString} .= "relation file :: ".((defined $self->{relation}) ? ($self->{relation}) : "")."\n";
    $self->{traceString} .= "stopwords file :: ".((defined $self->{stop}) ? ($self->{stop}) : "")."\n";
    $self->{traceString} .= "stem :: ".((defined $self->{stem}) ? ($self->{stem}) : "")."\n";
    $self->SUPER::traceOptions();
}

=item $self->configure($file)

Overrides the configure method in WordNet::Similarity. This method loads
various data files, such as the stop words and relations.

Parameters: $file -- path of the configuration file.

Returns: nothing

=cut

sub configure
{
    my $self = shift;
    my $class = ref $self || $self;
    my %stopHash;
    my $gwi;

    # Call the configure method in parent (WordNet::Similarity)
    $self->SUPER::configure(@_);
    $self->{maxCache} = 5000;
    
    # Initialize the stop list.
    $self->{stopHash} = {};
    my $wn = $self->{wn};
    
    # Use default relation file if specified by module...
    $self->{relation} = $self->{relationDefault}
    if(!($self->{relation}) && defined $self->{relationDefault} && $self->{relationDefault} ne ""); 

    # Load the stop list.
    if(defined $self->{stop})
    {
	my $line;
        my $stopFile = $self->{stop};

	if(open(STOP, $stopFile))
	{
	    while($line = <STOP>)
	    {
		$line =~ s/[\r\f\n]//g;
		$line =~ s/^\s+//;
		$line =~ s/\s+$//;
		$line =~ s/\s+/_/g;
                $stopHash{$line} = 1;
		$self->{stopHash}->{$line} = 1;
	    }
	    close(STOP);
	}
	else
	{
	    $self->{errorString} .= "\nWarning ($class->configure()) - ";
	    $self->{errorString} .= "Unable to open $stopFile.";
	    $self->{error} = 1 if($self->{error} < 1);
	}
    }

    # so now we are ready to initialize the get_wn_info package with
    # the wordnet object, 0/1 depending on if stemming is required and
    # the stop hash
    if($self->{stem})
    {
	$gwi = WordNet::get_wn_info->new($wn, 1, %stopHash);
	$self->{gwi} = $gwi;
    }
    else
    {
	$gwi = WordNet::get_wn_info->new($wn, 0, %stopHash);
	$self->{gwi} = $gwi;
    }

    # Load the relations
    $self->_loadRelationFile();

    # Initialize traces for relations...
    $self->{relationTraces} = [];
    my $i = 0;
    while(defined $self->{functions}->[$i])
    {
	my $functionsString = "";
        my $weight = $self->{weights}->[$i];
	
	# see if any traces reqd. if so, create the functions string
	# however don't send it to the trace string immediately - will
	# print it only if there are any overlaps for this rel pair
        $functionsString = "Functions: ";
        my $j = 0;
        while(defined $self->{functions}->[$i]->[0]->[$j])
        {
            $functionsString .= ($self->{functions}->[$i]->[0]->[$j])." ";
            $j++;
        }

        $functionsString .= "- ";
        $j = 0;
        while(defined $self->{functions}->[$i]->[1]->[$j])
        {
            $functionsString .= ($self->{functions}->[$i]->[1]->[$j])." ";
            $j++;
        }
        push(@{$self->{relationTraces}}, $functionsString);
        $i++;
    }
}

=item $self->getSuperGlosses($wps1, $wps2)

This method returns a list of large blocks of concatenated glosses (super-gloss) for
each specified synset. A super-gloss is the block of text formed by concatenating the
glosses of a synset with glosses of synsets related to it in WordNet. "Related"
synsets are identified by specific relations specified in the "relations" file.
If no relations file was specified in the configuration, only the gloss of that
synset is returned.

Parameters: wps1 and wps2 -- two synsets.

Returns: List of superglosses for both synsets (2-D array).

=cut

sub getSuperGlosses
{
    my $self = shift;
    my $wps1 = shift;
    my $wps2 = shift;
    my $class = ref $self || $self;
    my $rArray = [];
    my $gwi = $self->{gwi};

    # NOTE: Thanks to Wybo Wiersma for providing the following (faster)
    #       super-gloss code.

    # check if the supergloss of the left word is in the cache.
    # If it is not, add it.
    if(!defined($self->{cache}->[0]->{$wps1}))
    {
        push(@{$self->{cachelist}->[0]}, $wps1);

        # Remove the oldest cache-entry if there's no more room
        if(scalar(@{$self->{cachelist}->[0]}) > $self->{maxCache})
        {
            my $todel = shift(@{$self->{cachelist}->[0]});
            delete ($self->{cache}->[0]->{$todel});
        }
        
        $self->{cache}->[0]->{$wps1} = $self->_getSuperGlosses($wps1, $gwi, 0);
    }
    
    # check if the supergloss of the right word is in the cache.
    # If it is not, add it.
    if(!defined($self->{cache}->[1]->{$wps2}))
    {
        push(@{$self->{cachelist}->[1]}, $wps2);

        # Remove the oldest cache-entry if there's no more room
        if(scalar(@{$self->{cachelist}->[1]}) > $self->{maxCache})
        {
            my $todel = shift(@{$self->{cachelist}->[1]});
            delete ($self->{cache}->[1]->{$todel});
        }
        
        $self->{cache}->[1]->{$wps2} = $self->_getSuperGlosses($wps2, $gwi, 1);
    }
    
    return ($self->{cache}->[0]->{$wps1}, $self->{cache}->[1]->{$wps2}, $self->{weights}, $self->{relationTraces});
}

sub _getSuperGlosses()
{
    my $self = shift;
    my ($wps, $gwi, $zron) = @_;
    my @stringArray;

    # and now go thru the functions array, get the strings
    my $i = 0;
    while(defined $self->{functions}->[$i])
    {
	# now get the string for the first set of synsets
        my %seth = ();
        $seth{$wps} = 1;
	my @arguments = \%seth;
	
	# apply the functions to the arguments, passing the output of
	# the inner functions to the inputs of the outer ones
	my $j = 0;
	while(defined $self->{functions}->[$i]->[$zron]->[$j])
	{
	    my $fn = $self->{functions}->[$i]->[$zron]->[$j];
	    @arguments = $gwi->$fn(@arguments);
	    $j++;
	}
	
	# finally we should have one cute little string!
        push(@stringArray, $arguments[0]);
	$i++;
    }

    return \@stringArray;
}

=back

=head3 Private Methods

=over

=item $self->_loadRelationFile()

This method loads relations from a relation file.

Parameters: none

Returns: nothing

=back

=cut

sub _loadRelationFile
{
    my $self = shift;
    my $class = ref $self || $self;
    my $gwi = $self->{gwi};

    if($self->{relation})
    {
	my $header;
	my $relation;

	if(open (RELATIONS, $self->{relation}))
        {
	    $header = <RELATIONS>;
	    $header =~ s/[\r\f\n]//g;
	    $header =~ s/\s+//g;
	    if(defined $header && $header =~ /RelationFile/)
            {
		my $index = 0;
		$self->{functions} = ();
		$self->{weights} = ();
		while($relation = <RELATIONS>) 
                {
		    $relation =~ s/[\r\f\n]//g;
		    
		    # now for each line in the file, extract the
		    # nested functions if any, check if they are defined,
		    # if it makes sense to nest them, and then finally put
		    # them into the @functions triple dimensioned array!
		    
		    # remove leading/trailing spaces from the relation
		    $relation =~ s/^\s*(\S*?)\s*$/$1/;

                    next if($relation =~ /^$/);
		    
		    # now extract the weight if any. if no weight, assume 1
		    if($relation =~ /(\S+)\s+(\S+)/)
		    {
			$relation = $1;
			$self->{weights}->[$index] = $2;
		    }
		    else
		    {
			$self->{weights}->[$index] = 1;
		    }

		    # check if we have a "proper" relation, that is a relation in
		    # there are two blocks of functions!
		    if($relation !~ /(.*)-(.*)/)
                    {
			$self->{errorString} .= "\nError (${class}::_loadRelationFile()) - ";
			$self->{errorString} .= "Bad file format ($self->{relation}).";
			$self->{error} = 2;
			close RELATIONS;
			return;		
		    }
		    
		    # get the two parts of the relation pair
		    my @twoParts;
		    my $l;
		    $twoParts[0] = $1;
		    $twoParts[1] = $2;
		    
		    # process the two parts and put into functions array
		    for($l = 0; $l < 2; $l++)
		    {
			#no strict 'subs';
			
			$twoParts[$l] =~ s/[\s\)]//g;
			my @functionArray = split(/\(/, $twoParts[$l]);
			
			my $j = 0;
			my $fn = $functionArray[$#functionArray];
			unless($gwi->can($fn))
                        {
			    $self->{errorString} .= "\nError (${class}::_loadRelationFile()) - ";
			    $self->{errorString} .= "Undefined function ($functionArray[$#functionArray]) in relations file.";
			    $self->{error} = 2;
			    close RELATIONS;
			    return;
			}
			
			$self->{functions}->[$index]->[$l]->[$j++] = $functionArray[$#functionArray];
			my $input;
			my $output;
			my $dummy;
			my $k;
			
			for ($k = $#functionArray-1; $k >= 0; $k--)
			{
			    my $fn2 = $functionArray[$k];
			    my $fn3 = $functionArray[$k+1];
			    if(!($gwi->can($fn2)))
			    {
				$self->{errorString} .= "\nError (${class}::_loadRelationFile()) - ";
				$self->{errorString} .= "Undefined function ($functionArray[$k]) in relations file.";
				$self->{error} = 2;
				close(RELATIONS);
				return;
			    }
			    
			    ($input, $dummy) = $gwi->$fn2($dummy, 1);
			    ($dummy, $output) = $gwi->$fn3($dummy, 1);
			    
                            if($input != $output)
                            {
                                $self->{errorString} .= "\nError (${class}::_loadRelationFile()) - ";
                                $self->{errorString} .= "Invalid function combination - $functionArray[$k]($functionArray[$k+1]).";
                                $self->{error} = 2;
                                close(RELATIONS);
                                return;
                            }
			    
			    $self->{functions}->[$index]->[$l]->[$j++] = $functionArray[$k];
			}
			
			# if the output of the outermost function is synset array (1)
			# wrap a glos around it
			my $xfn = $functionArray[0];
			($dummy, $output) = $gwi->$xfn($dummy, 1);
			if($output == 1)
			{
			    $self->{functions}->[$index]->[$l]->[$j++] = "glos";
			}
		    }
		    
		    $index++;
		}
	    }
	    else
	    {
		$self->{errorString} .= "\nError (${class}::_loadRelationFile()) - ";
		$self->{errorString} .= "Bad file format ($self->{relation}).";
		$self->{error} = 2;
		close(RELATIONS);
		return;		
	    }
	    close(RELATIONS);
	}
	else
	{
	    $self->{errorString} .= "\nError (${class}::_loadRelationFile()) - ";
	    $self->{errorString} .= "Unable to open $self->{relation}.";
	    $self->{error} = 2;
	    return;
	}
    }
    else
    {
	$self->{weights}->[0] = 1;
	$self->{functions}->[0]->[0]->[0] = "glosexample";
	$self->{functions}->[0]->[1]->[0] = "glosexample";
	return;
    }
}

1;

__END__

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

 Siddharth Patwardhan, University of Utah, Salt Lake City
 sidd at cs.utah.edu

=head1 BUGS

None.

=head1 SEE ALSO

WordNet::Similarity(3)
WordNet::Similarity::vector(3)
WordNet::Similarity::lesk(3)

=head1 COPYRIGHT

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
