# WordNet::Similarity::vector.pm version 2.04
# (Last updated $Id: vector.pm,v 1.24 2008/03/27 06:21:17 sidz1979 Exp $)
#
# Module accepts two WordNet synsets and returns a floating point
# number that indicates how similar those two synsets are, using a
# gloss vector overlap measure based on "context vectors" described by
# Schütze (1998).
#
# Copyright (c) 2005,
#
# Ted Pedersen, University of Minnesota Duluth
# tpederse at d.umn.edu
#
# Siddharth Patwardhan, University of Utah, Salt Lake City
# sidd at cs.utah.edu
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

package WordNet::Similarity::vector;

=head1 NAME

WordNet::Similarity::vector - Perl module for computing semantic relatedness
of word senses using second order co-occurrence vectors of glosses of the word
senses.

=head1 SYNOPSIS

  use WordNet::Similarity::vector;

  use WordNet::QueryData;

  my $wn = WordNet::QueryData->new();

  my $vector = WordNet::Similarity::vector->new($wn);

  my $value = $vector->getRelatedness("car#n#1", "bus#n#2");

  ($error, $errorString) = $vector->getError();

  die "$errorString\n" if($error);

  print "car (sense 1) <-> bus (sense 2) = $value\n";

=head1 DESCRIPTION

SchE<uuml>tze (1998) creates what he calls context vectors (second order
co-occurrence vectors) of pieces of text for the purpose of Word Sense
Discrimination. This idea is adopted by Patwardhan and Pedersen to represent
the word senses by second-order co-occurrence vectors of their dictionary
(WordNet) definitions. The relatedness of two senses is then computed as
the cosine of their representative gloss vectors.

=over

=cut

use strict;
use WordNet::get_wn_info;
use WordNet::stem;
use WordNet::vectorFile;
use WordNet::Similarity;
use File::Spec;
use vars qw($VERSION @ISA);

@ISA = qw(WordNet::Similarity);

$VERSION = '2.04';

WordNet::Similarity::addConfigOption("relation", 0, "p", undef);
WordNet::Similarity::addConfigOption("vectordb", 0, "p", undef);
WordNet::Similarity::addConfigOption("stop", 0, "p", undef);
WordNet::Similarity::addConfigOption("stem", 0, "i", 0);
WordNet::Similarity::addConfigOption("textsize", 0, "i", "-1");

=item $vector->setPosList()

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
  return 1;
}

=item $vector->initialize($file)

Overrides the initialize method in the parent class (GlossFinder.pm). This method
essentially initializes the measure for use.

Parameters: $file -- configuration file.

Returns: none.

=cut 

# Initialization of the WordNet::Similarity::vector object... parses the config file and sets up
# global variables, or sets them to default values.
# INPUT PARAMS  : $paramFile .. File containing the module specific params.
# RETURN VALUES : (none)
sub initialize
{
    my $self = shift;
    my $vectorDB;
    my $documentCount;
    my $wn = $self->{wn};
    my $gwi;
    my $readDims;
    my $readVectors;
    my %stopHash = ();

    # Stemming? Compounds? StopWords?
    $self->{stem} = 0;
    $self->{stopHash} = {};

    # Call the initialize method of the super-class.
    $self->SUPER::initialize(@_);

    # Initialize the vector cache.
    $self->{vCache} = ();
    $self->{vCacheQ} = ();
    $self->{vCacheSize} = 80;

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
	    $self->{errorString} .= "\nWarning (WordNet::Similarity::vector->initialize()) - ";
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

    # Initialize the word vector database interface...
    if(!defined $self->{vectordb} || $self->{vectordb} eq "")
    {	
        my $path;
        my $header;
        my @possiblePaths = ();
        $vectorDB = "";

        # Look for all possible default data files installed.
        foreach $path (@INC) 
        {
            # JM 1-16-04  -- modified to use File::Spec
            my $file = File::Spec->catfile($path, 'WordNet', 'wordvectors.dat');
            push @possiblePaths, $file if(-e $file);
        }
        
        # If there are multiple possibilities, get the one in the correct format.
        foreach $path (@possiblePaths) 
        {
            next if(!open(VECTORS, $path));
            $header = <VECTORS>;
            $header =~ s/\s+//g;
            if($header =~ /DOCUMENTCOUNT/)
            {
                $vectorDB = $path;
                $self->{vectordb} = $path;
                close(VECTORS);
                last;
            }
            close(VECTORS);
        }
    }
    else
    {
        $vectorDB = $self->{vectordb};
    }

    # Initialize the word vector database interface...
    if(!defined $vectorDB || $vectorDB eq "")
    {	
	$self->{errorString} .= "\nError (WordNet::Similarity::vector->initialize()) - ";
	$self->{errorString} .= "No usable Word Vector database found. Use configuration file.";
	$self->{error} = 2;
	return;
    }

    # Get the documentCount, dimensions and vectors...
    ($documentCount, $readDims, $readVectors) = WordNet::vectorFile->readVectors($vectorDB);
    if(!defined $documentCount || !defined $readDims || !defined $readVectors)
    {
	$self->{errorString} .= "\nError (WordNet::Similarity::vector->initialize()) - ";
	$self->{errorString} .= "Error reading the vector database file.";
	$self->{error} = 2;
	return;
    }
    
    # Load the word vector dimensions...
    my $key;
    $self->{numberOfDimensions} = scalar(keys(%{$readDims}));
    foreach $key (keys %{$readDims})
    {
	my $ans = $readDims->{$key};
	my @prts = split(/\s+/, $ans);
	$self->{wordIndex}->{$key} = $prts[0];
	$self->{indexWord}->[$prts[0]] = $key;
    }

    # Set up the interface to the word vectors...
    foreach $key (keys %{$readVectors})
    {
	my $vec = $readVectors->{$key};
	if(defined $vec)
	{
	    $self->{table}->{$key} = $vec;
	}
    }

    # If relation file not specified... manually add the relations to
    # be used... Look for the default vector relation file...
    if(!defined $self->{relation}) 
    {
        my $path;
        my $header;
        my @possiblePaths = ();
        
        # Look for all possible default data files installed.
        foreach $path (@INC) 
        {
            # JM 1-16-04  -- modified to use File::Spec
            my $file = File::Spec->catfile($path, 'WordNet', 'vector-relation.dat');
            push @possiblePaths, $file if(-e $file);
        }
        
        # If there are multiple possibilities, get the one in the correct format.
        foreach $path (@possiblePaths) 
        {
            next if(!open(RELATIONS, $path));
            $header = <RELATIONS>;
            $header =~ s/\s+//g;
            if($header =~ /VectorRelationFile/)
            {
                $self->{relation} = $path;
                close(RELATIONS);
                last;
            }
            close(RELATIONS);
        }
    }
    if(!(defined $self->{relation}))
    {
	$self->{weights}->[0] = 1;
	$self->{functions}->[0]->[0] = "glosexample";
    }
    else
    {
	# Load the relations data
	my $header;
	my $relation;
        my $relationFile = $self->{relation};
	
	if(open(RELATIONS, $relationFile))
	{
	    $header = <RELATIONS>;
	    $header =~ s/[\r\f\n]//g;
	    $header =~ s/\s+//g;
	    if($header =~ /VectorRelationFile/)
	    {
		my $index = 0;
		$self->{functions} = ();
		$self->{weights} = ();
		while($relation = <RELATIONS>)
		{
		    $relation =~ s/[\r\f\n]//g;
	
		    # now for each line in the <REL> file, extract the
		    # nested functions if any, check if they are defined,
		    # if it makes sense to nest them, and then finally put
		    # them into the @functions triple dimensioned array!
	
		    # remove leading/trailing spaces from the relation
		    $relation =~ s/^\s+//;
		    $relation =~ s/\s+$//;
	
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
	
                    # Need to remove strict for this block.
                    {
                        no strict;
                        
                        $relation =~ s/[\s\)]//g;
                        my @functionArray = split(/\(/, $relation);
                        
                        my $j = 0;
                        my $fn = $functionArray[$#functionArray];
                        if(!($gwi->can($fn)))
                        {
                            $self->{errorString} .= "\nError (WordNet::Similarity::vector->initialize()) - ";
                            $self->{errorString} .= "Undefined function ($functionArray[$#functionArray]) in relations file.";
                            $self->{error} = 2;
                            close(RELATIONS);
                            return;
                        }
                        
                        $self->{functions}->[$index]->[$j++] = $functionArray[$#functionArray];
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
				$self->{errorString} .= "\nError (WordNet::Similarity::vector->initialize()) - ";
				$self->{errorString} .= "Undefined function ($functionArray[$k]) in relations file.";
				$self->{error} = 2;
				close(RELATIONS);
				return;
			    }
	
			    ($input, $dummy) = $gwi->$fn2($dummy, 1);
			    ($dummy, $output) = $gwi->$fn3($dummy, 1);
	
			    if($input != $output)
			    {
				$self->{errorString} .= "\nError (WordNet::Similarity::vector->initialize()) - ";
				$self->{errorString} .= "Invalid function combination - $functionArray[$k]($functionArray[$k+1]).";
				$self->{error} = 2;
				close(RELATIONS);
				return;
			    }
	
			    $self->{functions}->[$index]->[$j++] = $functionArray[$k];
			}
			
			# if the output of the outermost function is synset array (1)
			# wrap a glosexample around it
			my $xfn = $functionArray[0];
			($dummy, $output) = $gwi->$xfn($dummy, 1);
			if($output == 1)
			{
			    $self->{functions}->[$index]->[$j++] = "glosexample";
			}
		    }
	
		    $index++;
		}
	    }
	    else
	    {
		$self->{errorString} .= "\nError (WordNet::Similarity::vector->initialize()) - ";
		$self->{errorString} .= "Bad file format ($relationFile).";
		$self->{error} = 2;
		close(RELATIONS);
		return;		
	    }
	    close(RELATIONS);
	}
	else
	{
	    $self->{errorString} .= "\nError (WordNet::Similarity::vector->initialize()) - ";
	    $self->{errorString} .= "Unable to open $relationFile.";
	    $self->{error} = 2;
	    return;
	}
    }

    $self->{textsize} = -1 if(!defined $self->{textsize});
}

=item $vector->traceOptions()

This method is internally called to determine the extra options
specified by this measure (apart from the default options specified
in the WordNet::Similarity base class).

Parameters: none.

Returns: none.

=cut 

# show all config options specific to this module
sub traceOptions {
  my $self = shift;
  $self->{traceString} .= "relation File :: ".((defined $self->{relation})?"$self->{relation}":"")."\n";
  $self->{traceString} .= "vectorDB File :: ".((defined $self->{vectordb})?"$self->{vectordb}":"")."\n";
  $self->{traceString} .= "stop File :: ".((defined $self->{stop})?"$self->{stop}":"")."\n";
  $self->{traceString} .= "stem :: $self->{stem}\n";
  $self->{traceString} .= "textsize :: $self->{textsize}\n";
}

=item $vector->getRelatedness

Computes the relatedness of two word senses using the Vector Algorithm.

Parameters: two word senses in "word#pos#sense" format.

Returns: Unless a problem occurs, the return value is the relatedness
score, which is greater-than or equal-to 0. If an error occurs,
then the error level is set to non-zero and an error
string is created (see the description of getError()).

=cut

sub getRelatedness
{
    my $self = shift;
    my $wps1 = shift;
    my $wps2 = shift;
    my $wn = $self->{wn};
    my $wntools = $self->{wntools};
    my $gwi = $self->{gwi};

    # Check the existence of the WordNet::QueryData object.
    if(!$wn)
    {
	$self->{errorString} .= "\nError (WordNet::Similarity::vector->getRelatedness()) - ";
	$self->{errorString} .= "A WordNet::QueryData object is required.";
	$self->{error} = 2;
	return undef;
    }

    # Check the existence of the WordNet::Tools object.
    if(!$wntools)
    {
	$self->{errorString} .= "\nError (WordNet::Similarity::vector->getRelatedness()) - ";
	$self->{errorString} .= "A WordNet::Tools object is required.";
	$self->{error} = 2;
	return undef;
    }

    # Initialize traces.
    $self->{traceString} = "" if($self->{trace});

    # Undefined input cannot go unpunished.
    if(!$wps1 || !$wps2)
    {
	$self->{errorString} .= "\nWarning (WordNet::Similarity::vector->getRelatedness()) - Undefined input values.";
	$self->{error} = 1 if($self->{error} < 1);
	return undef;
    }

    # Security check -- are the input strings in the correct format (word#pos#sense).
    if($wps1 !~ /^\S+\#([nvar])\#\d+$/)
    {
	$self->{errorString} .= "\nWarning (WordNet::Similarity::vector->getRelatedness()) - ";
	$self->{errorString} .= "Input not in word\#pos\#sense format.";
	$self->{error} = ($self->{error} < 1) ? 1 : $self->{error};
	return undef;
    }
    if($wps2 !~ /^\S+\#([nvar])\#\d+$/)
    {
	$self->{errorString} .= "\nWarning (WordNet::Similarity::vector->getRelatedness()) - ";
	$self->{errorString} .= "Input not in word\#pos\#sense format.";
	$self->{error} = ($self->{error} < 1) ? 1 : $self->{error};
	return undef;
    }

    # Now check if the similarity value for these two synsets is in
    # fact in the cache... if so return the cached value.
    my $relatedness = $self->{doCache} ? $self->fetchFromCache($wps1, $wps2) : undef;
    defined $relatedness and return $relatedness;

    # Are the gloss vectors present in the cache...
    if(defined $self->{vCache}->{$wps1} && defined $self->{vCache}->{$wps2})
    {
	if($self->{trace})
	{
	    # ah so we do need SOME traces! put in the synset names.
	    $self->{traceString} .= "Synset 1: $wps1 (Gloss Vector found in Cache)\n";
	    $self->{traceString} .= "Synset 2: $wps2 (Gloss Vector found in Cache)\n";
	}	
	my $a = $self->{vCache}->{$wps1};
	my $b = $self->{vCache}->{$wps2};
	my $score = &_inner($a, $b);

	# that does all the scoring. Put in cache if doing cacheing. Then
	# return the score.
        $self->{doCache} and $self->storeToCache($wps1, $wps2, $score);
	return $score;
    }

    # we shall put the first synset in a "set" of itself, and the
    # second synset in another "set" of itself. These sets may
    # increase in size as the functions are applied (since some
    # relations have a one to many mapping).

    # initialize the score
    my $score = 0;

    # and now go thru the functions array, get the strings and do the scoring
    my $i = 0;
    my %overlaps;
    my $firstString = "";
    my $secondString = "";
    while(defined $self->{functions}->[$i])
    {
	my $functionsString = "";
	my $funcStringPrinted = 0;
	my $functionsScore = 0;
	
	# see if any traces reqd. if so, create the functions string
	# however don't send it to the trace string immediately - will
	# print it only if there are any overlaps for this rel
	if($self->{trace})
	{
	    $functionsString = "Functions: ";
	    my $j = 0;
	    while(defined $self->{functions}->[$i]->[$j])
	    {
		$functionsString .= ($self->{functions}->[$i]->[$j])." ";
		$j++;
	    }
	}
	
	# now get the string for the first set of synsets
        my %seth1 = ();
        $seth1{$wps1} = 1;
	my @arguments = \%seth1;
	
	# apply the functions to the arguments, passing the output of
	# the inner functions to the inputs of the outer ones
	my $j = 0;
	no strict;

	while(defined $self->{functions}->[$i]->[$j])
	{
	    my $fn = $self->{functions}->[$i]->[$j];
	    @arguments = $gwi->$fn(@arguments);
	    $j++;
	}
	
	# finally we should have one cute little string!
	$firstString .= $arguments[0];
	
	# next do all this for the string for the second set
        my %seth2 = ();
        $seth2{$wps2} = 1;
	@arguments = \%seth2;
	
	$j = 0;
	while(defined $self->{functions}->[$i]->[$j])
	{
	    my $fn = $self->{functions}->[$i]->[$j];
	    @arguments = $gwi->$fn(@arguments);
	    $j++;
	}
	
	$secondString .= $arguments[0];
		
	# check if the two strings need to be reported in the trace.
	if($self->{trace})
	{
	    if(!$funcStringPrinted)
	    {
		$self->{traceString} .= "$functionsString\n";
		$funcStringPrinted = 1;
	    }
	}
	
	$i++;
    }

    # Preprocess...
    $firstString =~ s/\'//g;
    $firstString =~ s/[^a-z0-9]+/ /g;
    $firstString =~ s/^\s+//;
    $firstString =~ s/\s+$//;
    $firstString = $wntools->compoundify($firstString);
    $secondString =~ s/\'//g;
    $secondString =~ s/[^a-z0-9]+/ /g;
    $secondString =~ s/^\s+//;
    $secondString =~ s/\s+$//;
    $secondString = $wntools->compoundify($secondString);

    # Get vectors... score...
    my $a;
    my $maga;
    my $sizea;
    my $b;
    my $magb;
    my $sizeb;
    my $trr;

    # see if any traces reqd. if so, put in the synset arrays.
    if($self->{trace})
    {
	# ah so we do need SOME traces! put in the synset names.
	$self->{traceString} .= "Synset 1: $wps1";
    }
    $sizea = 0;
    if(defined $self->{vCache}->{$wps1})
    {
	$a = $self->{vCache}->{$wps1};
	$self->{traceString} .= " (Gloss vector found in cache)\n" if($self->{trace});
    }
    else
    {
	($a, $trr, $maga, $sizea) = $self->_getVector($firstString);
	$self->{traceString} .= "\nString: \"$firstString\"\n$trr\n" if($self->{trace});
	&_norm($a, $maga);
	$self->{vCache}->{$wps1} = $a;
	push(@{$self->{vCacheQ}}, $wps1);
	while(scalar(@{$self->{vCacheQ}}) > $self->{vCacheSize})
	{
	    my $wps = shift(@{$self->{vCacheQ}});
	    delete $self->{vCache}->{$wps}
	}
    }

    if($self->{trace})
    {
	# ah so we do need SOME traces! put in the synset names.
	$self->{traceString} .= "Synset 2: $wps2";
    }
    $sizeb = 0;
    if(defined $self->{vCache}->{$wps2})
    {
	$b = $self->{vCache}->{$wps2};
	$self->{traceString} .= " (Gloss vector found in cache)\n" if($self->{trace});
    }
    else
    {
	($b, $trr, $magb, $sizeb) = $self->_getVector($secondString);
	$self->{traceString} .= "\nString: \"$secondString\"\n$trr\n" if($self->{trace});
	&_norm($b, $magb);
	$self->{vCache}->{$wps2} = $b;
	push(@{$self->{vCacheQ}}, $wps2);
	while(scalar(@{$self->{vCacheQ}}) > $self->{vCacheSize})
	{
	    my $wps = shift(@{$self->{vCacheQ}});
	    delete $self->{vCache}->{$wps}
	}
    }

    $score = &_inner($a, $b);

    # that does all the scoring. Put in cache if doing cacheing. Then
    # return the score.
    $self->{doCache} and $self->storeToCache($wps1, $wps2, $score);

    return $score;
}


# Method to compute a context vector from a given body of text...
sub _getVector
{
    my $self = shift;
    my $text = shift;
    my $ret = {};
    return ($ret, "", 0, 0) if(!defined $text);
    my @words = split(/\s+/, $text);
    my $word;
    my %types;
    my $fstFlag = 1;
    my $localTraces = "";
    my $kk;
    my $mag;
    my $count = 0;

    # [trace]
    if($self->{trace})
    {
	$localTraces .= "Word Vectors for: ";
    }
    # [/trace]

    foreach $word (@words)
    {
        if($word !~ /[XGES]{3}\d{5}[XGES]{3}/)
        {
            $types{$word} = 1;
            $count++;
            last if($self->{textsize} >= 0 && $count > $self->{textsize});  
        }
    }
    foreach $word (keys %types)
    {
	if(defined $self->{table}->{$word} && !defined $self->{stopHash}->{$word})
	{
	    my %pieces = split(/\s+/, $self->{table}->{$word});

	    # [trace]
	    if($self->{trace})
	    {
		$localTraces .= ", " if(!$fstFlag);
		$localTraces .= "$word";
		$fstFlag = 0;
	    }
	    # [/trace]

	    foreach $kk (keys %pieces)
	    {
		$ret->{$kk} = ((defined $ret->{$kk})?($ret->{$kk}):0) + $pieces{$kk};
	    }
	}
    }

    $mag = 0;
    foreach $kk (keys %{$ret})
    {
	$mag += ($ret->{$kk} * $ret->{$kk});
    }

    return ($ret, $localTraces, sqrt($mag), $count);
}

# Normalizes the sparse vector.
sub _norm
{
    my $vec = shift;
    my $mag = shift;

    if(defined $vec && defined $mag && $mag != 0)
    {
	my $key;
	foreach $key (keys %{$vec})
	{
	    $vec->{$key} /= $mag;
	}
    }
}

# Inner product of two sparse vectors.
sub _inner
{
    my $vec1 = shift;
    my $vec2 = shift;
    my ($size1, $size2);
    my $prod = 0;

    return 0 if(!defined $vec1 || !defined $vec2);

    $size1 = scalar(keys(%{$vec1}));
    $size2 = scalar(keys(%{$vec2}));

    if(defined $size1 && defined $size2 && $size1 < $size2)
    {
	my $key;
	foreach $key (keys %{$vec1})
	{
	    $prod += ($vec1->{$key} * $vec2->{$key}) if(defined $vec2->{$key});
	}
    }
    else
    {
	my $key;
	foreach $key (keys %{$vec2})
	{
	    $prod += ($vec1->{$key} * $vec2->{$key}) if(defined $vec1->{$key});
	}
    }

    return $prod;
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

To create an object of the vector measure, we would have the following
lines of code in the Perl program.

  use WordNet::Similarity::vector;
  $measure = WordNet::Similarity::vector->new($wn, '/home/sid/vector.conf');

The reference of the initialized object is stored in the scalar variable
'$measure'. '$wn' contains a WordNet::QueryData object that should have been
created earlier in the program. The second parameter to the 'new' method is
the path of the configuration file for the vector measure. If the 'new'
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
of the file. For example, a configuration file for the vector module will have
on the first line 'WordNet::Similarity::vector'. This is followed by the
various parameters, each on a new line and having the form 'name::value'. The
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
of 1 or 2 switches tracing on.  A value of 1 displays as
traces only the gloss overlaps found. A value of 2 displays as traces all
the text being compared.

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

=item relation

The value of this parameter is the path to a file that contains a list of
WordNet relations.  The path may be either an absolute path or a relative
path.

The vector module combines the glosses of synsets related to the target
synsets by these relations and forms the gloss-vector from this combined
gloss.

WARNING: the format of the relation file is different for the vector and lesk
measures.

=item stop

The value of this parameter the path of a file containing a list of stop
words that should be ignored in the glosses.  The path may be either an
absolute path or a relative path.

=item stem

The value of this parameter indicates whether or not stemming should be
performed.  The value must be an integer equal to 0 or 1.  If the
value is omitted, then the default value, 0, is used.
A value of 1 switches 'on' stemming, and a value of 0 switches stemming
'off'. When stemming is enabled, all the words of the
glosses are stemmed before their vectors are created for the vector
measure or their overlaps are compared for the lesk measure.

=item vectordb

The value of this parameter is the path to a file
containing word vectors, i.e. co-occurrence vectors for all the words
in the WordNet glosses.  The value of this parameter may not be omitted,
and the vector measure will not run without a vectors file being specified
in a configuration file.

=back

=head1 RELATION FILE FORMAT

The relation file starts with the string "VectorRelationFile" on the first line
of the file. Following this, on each consecutive line, a relation is specified
in the form --

 func(func(func... (func)...)) [weight]

Where "func" can be any one of the following functions:

 hype() = Hypernym of
 hypo() = Hyponym of
 holo() = Holonym of
 mero() = Meronym of
 attr() = Attribute of
 also() = Also see
 sim() = Similar
 enta() = Entails
 caus() = Causes
 part() = Particle
 pert() = Pertainym of
 glos = gloss (without example)
 example = example (from the gloss)
 glosexample = gloss + example
 syns = the synset of the concept

Each of these specifies a WordNet relation. And the outermost function in the
nesting can only be one of glos, example, glosexample or syns. The functions specify which
glosses to use for forming the gloss vector of the synset. An optional weight can be
specified to weigh the contribution of that relation in the overall score.

For example,

 glos(hype(hypo)) 0.5

means that the gloss of the hypernym of the hyponym of the synset is used to
form the gloss vector of the synset, and the values in this vector are
weighted by 0.5. If one of "glos", "example", "glosexample" or "syns" is not
specified as the outermost function in the nesting,
then "glosexample" is assumed by default. This implies that

 glosexample(hypo(also))

and

 hypo(also)

are equivalent as far as the measure is concerned.

=head1 SEE ALSO

perl(1), WordNet::Similarity(3), WordNet::QueryData(3)

http://www.cs.utah.edu/~sidd

http://wordnet.princeton.edu

http://www.ai.mit.edu/~jrennie/WordNet

http://groups.yahoo.com/group/wn-similarity

=head1 AUTHORS

 Ted Pedersen, University of Minnesota, Duluth
 tpederse at d.umn.edu

 Siddharth Patwardhan, University of Utah, Salt Lake City
 sidd at cs.utah.edu

 Satanjeev Banerjee, Carnegie Mellon University, Pittsburgh
 banerjee+ at cs.cmu.edu

=head1 BUGS

To report bugs, go to http://groups.yahoo.com/group/wn-similarity/ or
send an e-mail to "S<tpederse at d.umn.edu>".

=head1 COPYRIGHT AND LICENSE

Copyright (c) 2005, Ted Pedersen, Siddharth Patwardhan and Satanjeev Banerjee

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
