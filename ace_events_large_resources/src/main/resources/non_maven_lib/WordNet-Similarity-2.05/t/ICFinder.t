#! /usr/bin/perl -w

# ICFinder.t version 2.01
# (Updated 2/12/2004 -- Jason)
#
# Copyright (c) 2005
#
# Jason Michelizzi, University of Minnesota Duluth
# mich0212 at d.umn.edu
#
# Ted Pedersen, University of Minnesota Duluth
# tpederse at d.umn.edu

# Before 'make install' is run this script should be runnable with
# 'make test'.  After 'make install' it should work as 'perl t/ICFinder.t'

# A script to test the WordNet::Similarity::Infocontent module.
# Specifically, for a given set of words, it checks that the probability
# and infocontent values computed by Infocontent.pm are reasonably close
# to pre-computed values.


use strict;
use warnings;

use Test::More tests => 21;

# Tests 1, 2
BEGIN {use_ok 'WordNet::QueryData'}
BEGIN {use_ok 'WordNet::Similarity::ICFinder'}

# Test 3
my $wn = WordNet::QueryData->new;
ok ($wn) or diag "Failed to construct WordNet::QueryData object";

# get WordNet version (not the version of WordNet::QueryData)
my $wnver = '0.0';
$wnver = $wn->version () if($wn->can('version'));
my $module = WordNet::Similarity::ICFinder->new ($wn);

# Tests 4, 5
ok ($module)
  or diag "Failed to construct WordNet::Similarity::ICFinder object";
my ($err, $errstr) = $module->getError ();
is ($err, 0) or diag "$errstr";

# we are using the default infocontent file, ic-semtag.dat,  here: i.e.,
# statistics extracted from WordNet (which used SemCor tagged text).

# some variable we'll need repeatedly         '
my $offset;
my $prob;
my $ic;

SKIP: 
{
  skip "Skipping tests for WordNet v2.1, v2.0 and v1.7.1", 12 unless ($wnver eq '2.1' || $wnver eq '2.0' || $wnver eq '1.7.1');

  # Tests 6, 7, 8, 9
  # find probability and ic of entity#n#1
  if ($wnver eq '2.1')
  {
    $offset = 0 + '00001740';
    $prob = $module->probability ($offset, 'n');
    cmp_ok($prob, "<=", 1.00001);
    cmp_ok($prob, ">=", 0.99999);

    $ic = $module->IC ($offset, 'n');
    cmp_ok($ic, "<=", 0.00001);
    cmp_ok($ic, ">=", -0.00001);
  }
  elsif ($wnver eq '2.0')
  {
    $offset = 0 + '00001740';
    $prob = $module->probability ($offset, 'n');

    # should be around .425088
    cmp_ok ($prob, '<=', .42509);
    cmp_ok ($prob, '>=', .42507);

    $ic = $module->IC ($offset, 'n');
    # should be around .855459
    cmp_ok ($ic, '<=', .85546);
    cmp_ok ($ic, '>=', .85544);
  }
  elsif ($wnver eq '1.7.1') 
  {
    $offset = 0 + '00001742';
    $prob = $module->probability ($offset, 'n');

    # should be around .43663
    cmp_ok ($prob, '<=', .43664);
    cmp_ok ($prob, '>=', .43663);

    $ic = $module->IC ($offset, 'n');
    # should be around .82866
    cmp_ok ($ic, '<=', .82867);
    cmp_ok ($ic, '>=', .82865);
  }

  # Tests 10, 11, 12, 13
  # find probability and ic of breathe#v#1
  if ($wnver eq '2.1')
  {
    $offset = 0 + '00001740';
    $prob = $module->probability ($offset, 'v');
    cmp_ok($prob, "<=", 0.00090766);
    cmp_ok($prob, ">=", 0.00090764);

    $ic = $module->IC ($offset, 'v');
    cmp_ok($ic, "<=", 7.0047);
    cmp_ok($ic, ">=", 7.0045);
  } 
  elsif ($wnver eq '2.0')
  {
    $offset = 0 + '00001740';
    $prob = $module->probability ($offset, 'v');

    # should be around .000907285
    cmp_ok ($prob, '<=', .00090729);
    cmp_ok ($prob, '>=', .00090727);

    $ic = $module->IC ($offset, 'v');
    # should be around 7.00505
    cmp_ok ($ic, '<=', 7.0051);
    cmp_ok ($ic, '>=', 7.0050);
  }
  elsif ($wnver eq '1.7.1')
  {
    $offset = 0 + '00001742';
    $prob = $module->probability ($offset, 'v');
    # should be around .00090283
    cmp_ok ($prob, '<=', .00090284);
    cmp_ok ($prob, '>=', .00090282);

    $ic = $module->IC ($offset, 'v');
    # should be around 7.0100
    cmp_ok ($ic, '<=', 7.0101);
    cmp_ok ($ic, '>=', 7.0099);
  }

  # Tests 14, 15, 16, 17
  # find probability and ic of rest#v#7
  if($wnver eq '2.1')
  {
    $offset = 0 + '02639123';
    $prob = $module->probability ($offset, 'v');
    cmp_ok($prob, "<=", 0.00000818);
    cmp_ok($prob, ">=", 0.00000816);

    $ic = $module->IC ($offset, 'v');
    cmp_ok($ic, "<=", 11.72);
    cmp_ok($ic, ">=", 11.70);
  }
  elsif ($wnver eq '2.0')
  {
    $offset = 0 + '02584611';
    $prob = $module->probability ($offset, 'v');

    # should be around 0.00000817374
    cmp_ok ($prob, '<=', 0.00000818);
    cmp_ok ($prob, '>=', 0.00000816);

    $ic = $module->IC ($offset, 'v');
    # should be around 11.7146
    cmp_ok ($ic, '<=', 11.72);
    cmp_ok ($ic, '>=', 11.70);
  }
  elsif ($wnver eq '1.7.1')
  {
    $offset = 0 + '02097157';
    $prob = $module->probability ($offset, 'v');

    # should be around .0000081336
    cmp_ok ($prob, '<=', .0000081337);
    cmp_ok ($prob, '>=', .0000081335);

    $ic = $module->IC ($offset, 'v');
    # should be around 11.720
    cmp_ok ($ic, '<=', 11.721);
    cmp_ok ($ic, '>=', 11.719);
  }
}

# find probability and ic of root nodes (this should be the same regardless
# of the WordNet version)

$offset = 0; # the offset of the root is always 0

# Tests 18, 19
# check noun root node
$prob = $module->probability ($offset, 'n');
cmp_ok ($prob, '==', 1);
$ic = $module->IC ($offset, 'n');
cmp_ok ($ic, '==', 0);

# Tests 20, 21
# check verb root node
$prob = $module->probability ($offset, 'v');
cmp_ok ($prob, '==', 1);
$ic = $module->IC ($offset, 'v');
cmp_ok ($ic, '==', 0);

