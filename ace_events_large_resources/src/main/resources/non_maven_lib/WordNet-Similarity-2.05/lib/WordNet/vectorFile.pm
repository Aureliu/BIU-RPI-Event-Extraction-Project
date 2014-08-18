# WordNet::vectorFile.pm version 2.04
# (Last updated $Id: vectorFile.pm,v 1.1 2008/03/27 05:13:01 sidz1979 Exp $)
#
# Package used by WordNet::Similarity::vector module that
# computes semantic relatedness of word senses in WordNet
# using gloss vectors. This module provides a read/write
# interface into the word vectors file.
#
# Copyright (c) 2005,
#
# Ted Pedersen, University of Minnesota, Duluth
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

package WordNet::vectorFile;

=head1 NAME

WordNet::vectorFile - Provides access to the word vectors database (used
by the vector and vector_pairs WordNet::Similarity measures).

=head1 SYNOPSIS

  use WordNet::vectorFile;

  my ($dCount, $dims, $vecRef) = WordNet::vectorFile->readVectors($filename);

  WordNet::vectorFile->writeVectors($fname, $dCount, $dims, $vecRef);

=head1 DESCRIPTION

This module provides a read/write interface into the word vectors data
file. This module is used by WordNet::Similarity::vector and
WordNet::Similarity::vector_pairs as an interface into the word
vectors database. This module abstracts the format of the data file
away from the user.

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

=item readVectors

This method reads in a word vectors file, and returns the document count,
the words corresponding to the dimensions of the vectors, and a reference
to an array containing the list of word vectors. 

Parameters: $file -- the file name of the word vectors file.

Returns: ($dCount, $dims, $vecRef)

=cut

# Read the word vectors from a file.
sub readVectors
{
    my $className = shift;
    my $fname = shift;
    my $state = 0;
    my $docCount = 0;
    my $dimensions = {};
    my $vectors = {};
    my @parts = ();

    # Check that input values are defined.
    return (undef, undef, undef) if(!defined $className || !defined $fname || ref $className);
    
    # Read the data.
    open(IPFILE, $fname) || return (undef, undef, undef);
    while(<IPFILE>)
    {
        s/[\r\f\n]//g;
        s/^\s+//;
        s/\s+$//;
        if($state == 0)
        {
            if(/DOCUMENTCOUNT\s*=\s*([0-9]+)/)
            {
                $docCount = $1;
            }
            elsif(/--Dimensions Start--/)
            {
                $state = 1;
            }
            elsif(/--Vectors Start--/)
            {
                $state = 2;
            }
        }
        elsif($state == 1)
        {
            if(/--Dimensions End--/)
            {
                $state = 0;
            }
            elsif(/^--Dimensions/ || /^--Vectors/)
            {
                return (undef, undef, undef);
            }
            elsif($_ ne "")
            {
                @parts = split(/\s+/, $_, 2);
                $dimensions->{$parts[0]} = $parts[1];
            }
        }
        elsif($state == 2)
        {
            if(/--Vectors End--/)
            {
                $state = 0;
            }
            elsif(/^--Dimensions/ || /^--Vectors/)
            {
                return (undef, undef, undef);
            }
            elsif($_ ne "")
            {
                @parts = split(/\s+/, $_, 2);
                $vectors->{$parts[0]} = $parts[1];
            }
        }
        else
        {
            return (undef, undef, undef);
        }
    }
    close(IPFILE);

    # Return the data read.
    return ($docCount, $dimensions, $vectors);
}

=item writeVectors

This method writes out a list of word vectors to the word vectors file. 

Parameters: $fname, $dCount, $dims, $vecRef

Returns: none

=back

=cut

# Write the word vectors to a file.
sub writeVectors
{
    my $className = shift;
    my $fname = shift;
    my $documentCount = shift;
    my $dimensions = shift;
    my $vectors = shift;

    # Check that all input values are defined.
    return 0 if(!defined $className || !defined $fname || !defined $documentCount || !defined $dimensions || !defined $vectors);
    
    # Check that the className and filename aren't references.
    return 0 if(ref $className || ref $fname);

    # Check that document count is numeric.
    return 0 if($documentCount !~ /^[0-9]+$/);

    # Write the data to the file...
    # WARNING: No integrity check of data is performed.
    open(OPFILE, ">$fname") || return 0;
    print OPFILE "DOCUMENTCOUNT=$documentCount\n";
    print OPFILE "--Dimensions Start--\n";
    foreach my $key (keys %{$dimensions})
    {
        print OPFILE "$key ".($dimensions->{$key})."\n";
    }
    print OPFILE "--Dimensions End--\n";
    print OPFILE "--Vectors Start--\n";
    foreach my $key (keys %{$vectors})
    {
        print OPFILE "$key ".($vectors->{$key})."\n";
    }
    print OPFILE "--Vectors End--\n";
    close(OPFILE);
    
    # Success.
    return 1;
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

 Siddharth Patwardhan, University of Utah, Salt Lake City
 sidd at cs.utah.edu

=head1 BUGS

None.

To report bugs, go to http://groups.yahoo.com/group/wn-similarity/ or
e-mail "S<tpederse at d.umn.edu>".

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
