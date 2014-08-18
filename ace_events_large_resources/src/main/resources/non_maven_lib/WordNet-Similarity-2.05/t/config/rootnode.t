#! /usr/bin/perl -w

# Before 'make install' is run this script should be runnable with
# 'make test'.  After 'make install' it should work as 'perl t/config/rootnode.t'

# A script to run tests on the handling of root nodes by the
# res, lin, jcn, path, lch, and wup measures.  The following cases are
# tested:
#
# 1) the relatedness of a noun and a verb
# 2) the relatedness of the roots of two taxonomies of the same part of speech
# 3) the relatedness words in the same taxonomy (and same part of speech)

use strict;
use warnings;

my $numtests;
my @measures;

BEGIN {
  @measures = qw/res lin jcn path lch wup/;
  $numtests = 10 + 14 * scalar (@measures);
}

use Test::More tests => $numtests;

BEGIN {use_ok ('WordNet::QueryData')}
BEGIN {use_ok ('WordNet::Similarity::PathFinder')}
BEGIN {use_ok ('WordNet::Similarity::res')}
BEGIN {use_ok ('WordNet::Similarity::lin')}
BEGIN {use_ok ('WordNet::Similarity::jcn')}
BEGIN {use_ok ('WordNet::Similarity::path')}
BEGIN {use_ok ('WordNet::Similarity::lch')}
BEGIN {use_ok ('WordNet::Similarity::wup')}

my $wn = new WordNet::QueryData;
ok ($wn);

my $wnver = '0.0';
$wnver = $wn->version() if($wn->can('version'));
ok ($wnver);

# array of temporary file names
my @tempfiles;
# make sure that the files get deleted even if the
# script dies early
END { unlink @tempfiles }

foreach my $measure (@measures) {
  my $config = "root${measure}.txt";

  push @tempfiles, $config;

  ok (open FH, ">$config") or diag "Cannot open $config for writing: $!";
  print FH "WordNet::Similarity::$measure\n";
  print FH "rootNode::0\n";
  ok (close FH);

  my $module = "WordNet::Similarity::$measure"->new ($wn, $config);
  ok ($module);
  my ($err, $errString) = $module->getError();
  is ($err, 0) or diag "$errString";

  # now do some tests

  # dog#n#1 and cat#n#1 should always be related (relatedness > 0)
  my $score = $module->getRelatedness ('dog#n#1', 'cat#n#1');
  is (($module->getError())[0], 0);
  ok ($score > 0) or diag "Bad relatedness using $measure";

  # dog#n#1 and bark#v#4 should have undefined relatedness for these
  # measures
  $score = $module->getRelatedness ('dog#n#1', 'bark#v#4');
  is (($module->getError())[0], 1);
  ok ($score < 0);

  # entity#n#1 and event#n#1 are the root nodes of different noun taxonomies
  # When the root node is "turned off", they will have undefined relatedness
  $score = $module->getRelatedness ('entity#n#1', 'event#n#1');
  SKIP:
  {
    skip("Skipping tests for WordNet v2.1, v2.0 and v1.7.1", 4) unless($wnver eq '2.1' || $wnver eq '2.0' || $wnver eq '1.7.1');
    if($wnver eq '2.0' || $wnver eq '1.7.1')
    {
      is (($module->getError())[0], 1);
      ok ($score < 0);
    }
    if($wnver eq '2.1')
    {
      is (($module->getError())[0], 0);
      ok ($score >= 0);
    }

    if($wnver eq '2.0' || $wnver eq '1.7.1')
    {
      # carry#v#30 is a troponym (hyponym) of grow#v#7 => they should always
      # have a non-negative relatedness
      $score = $module->getRelatedness ('grow#v#7', 'carry#v#30');
    }
    if($wnver eq '2.1')
    {
      # carry#v#31 is a troponym (hyponym) of grow#v#7 => they should always
      # have a non-negative relatedness
      $score = $module->getRelatedness ('grow#v#7', 'carry#v#31');
    }
    is (($module->getError())[0], 0);
    ok ($score >= 0);
  }

  # recognize#v#3 and cultivate#v#1 are roots of different verb taxonomies
  $score = $module->getRelatedness ('recognize#v#3', 'cultivate#v#1');
  is (($module->getError())[0], 1);
  ok ($score < 0);
}

