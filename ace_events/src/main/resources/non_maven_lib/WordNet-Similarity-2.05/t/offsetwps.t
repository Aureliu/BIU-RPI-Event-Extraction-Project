#! /usr/bin/perl -w

# offsetwps.t version 2.01
# (Updated 2/10/2004 -- Jason)
#
# Copyright (C) 2004
#
# Jason Michelizzi, University of Minnesota Duluth
# mich0212 at d.umn.edu
#
# Ted Pedersen, University of Minnesota Duluth
# tpederse at d.umn.edu

# Before 'make install' is run this script should be runnable with
# 'make test'.  After 'make install' it should work as 'perl t/offsetwps.t'

# A script to test whether the offset method of finding paths and LCSs
# is equivalent to the wps method.  The tests are conducted as follows:
#
# *  The LCS for a pair of word senses is found.  The offsets for those
#    words are found, and then the LCS is found for those offsets.  A
#    test succeeds if the offset of the LCS using wps is the same as the
#    LCS using offsets.

use strict;
use warnings;

my $num_tests;
my @cases;

BEGIN {
  # test cases: the first two elements are wps strings whose LCS will
  # be found.  The third element is the part of speech (n, v, a, or r).
  @cases = (['dog#n#1', 'cat#n#1', 'n'],
	    ['tree#n#1', 'bush#n#1', 'n'],
	    ['adore#v#1', 'worship#v#1', 'v'],
#	    ['shout#v#2', 'cry#v#1', 'v'],
#	    ['car#n#1', 'auto#n#1', 'n'],
	    ['poet#n#1', 'poet#n#1', 'n']);

  $num_tests = 5 + 2 * scalar @cases;
}

use Test::More tests => $num_tests;

BEGIN {use_ok 'WordNet::QueryData'}
BEGIN {use_ok 'WordNet::Similarity::PathFinder'}

my $wn = WordNet::QueryData->new;
ok ($wn) or diag "Failed to load WordNet::QueryData";

my $lf = WordNet::Similarity::PathFinder->new ($wn);
ok ($lf) or diag "Failed to create WordNet::Similarity::PathFinder module";
my ($err, $errstr) = $lf->getError ();
is ($err, 0) or diag ($errstr);

foreach my $case (@cases) {
  my ($wps1, $wps2, $pos) = @$case;

  my @wps_paths = $lf->getShortestPath ($wps1, $wps2, $pos, 'wps');

  my $wps_length = $wps_paths[0]->[0];

  my $offset1 = $wn->offset ($wps1, $pos);
  my $offset2 = $wn->offset ($wps2, $pos);

  my @off_paths = $lf->getShortestPath ($offset1, $offset2, $pos, 'offset');

  my $off_length = $off_paths[0]->[0];

  is ($off_length, $wps_length, "testing length between $wps1 and $wps2");


  my @wps_lcss = $lf->getLCSbyPath ($wps1, $wps2, $pos, 'wps');
  my $wps_lcs = $wps_lcss[0]->[0];

  my @off_lcss = $lf->getLCSbyPath ($offset1, $offset2, $pos, 'offset');
  my $off_lcs = $off_lcss[0]->[0];
  my $offset = $wps_lcs =~ /\Q*Root*\E/i ? 0 : $wn->offset ($wps_lcs);
  is ($off_lcs, $offset, "testing LCS of $wps1 and $wps2");
}
