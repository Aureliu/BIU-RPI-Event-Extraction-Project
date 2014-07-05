#! /usr/bin/perl -w

# wn30loop.t version 2.05
# ($Id: wn30loop.t,v 1.3 2008/05/30 23:12:40 sidz1979 Exp $)
#
# Copyright (C) 2007
#
# Ted Pedersen, University of Minnesota Duluth
# tpederse at d.umn.edu
#
# Siddharth Patwardhan, University of Utah, Salt Lake City
# sidd at cs.utah.edu

# Before 'make install' is run this script should be runnable with
# 'make test'.  After 'make install' it should work as 'perl t/wn30loop.t'

use strict;
use warnings;

my $num_tests;
my @cases;
my @measures;

BEGIN {
  @cases = (['car#n#1', 'truck#n#1'],
            ['choke#v#7', 'quench#v#3'],
            ['choke#v#7', 'hold#v#36'],
            ['hold#v#36', 'quench#v#3'],
            ['hold#v#36', 'inhibit#v#4'],
            ['restrain#v#1', 'inhibit#v#4']);

  @measures = qw/res lin jcn lch path wup vector vector_pairs lesk hso random/;

  $num_tests = 13 + scalar(@measures) * (1 + scalar(@cases));
}

use Test::More tests => $num_tests;

BEGIN {use_ok 'WordNet::QueryData'}
BEGIN {use_ok 'WordNet::Similarity::res'}
BEGIN {use_ok 'WordNet::Similarity::lin'}
BEGIN {use_ok 'WordNet::Similarity::jcn'}
BEGIN {use_ok 'WordNet::Similarity::lch'}
BEGIN {use_ok 'WordNet::Similarity::path'}
BEGIN {use_ok 'WordNet::Similarity::wup'}
BEGIN {use_ok 'WordNet::Similarity::vector'}
BEGIN {use_ok 'WordNet::Similarity::vector_pairs'}
BEGIN {use_ok 'WordNet::Similarity::lesk'}
BEGIN {use_ok 'WordNet::Similarity::hso'}
BEGIN {use_ok 'WordNet::Similarity::random'}


my $wn = WordNet::QueryData->new();
ok ($wn) or diag "Failed to load WordNet::QueryData";

foreach my $measure (@measures) {
  my $module = "WordNet::Similarity::$measure"->new($wn);
  ok ($module) or diag "Unable to load module WordNet::Similarity::$measure";

  SKIP: {
    skip "Unable to load module. Skipping remaining tests for WordNet::Similarity::$measure", (scalar(@cases)*2) if(!$module);
    if($module)
    {
      foreach my $case (@cases) {
        my ($wps1, $wps2) = @$case;
        my $offset1 = $wn->offset($wps1);
        my $offset2 = $wn->offset($wps2);
        SKIP: {
          skip "Test sense not present in this version of WordNet. Skipping this test", 1 if(!$offset1 or !$offset2);
          if($offset1 and $offset2)
          {
            my $score = $module->getRelatedness($wps1, $wps2);
            ok (defined($score));
          }
        }
      }
    }
  }
}
