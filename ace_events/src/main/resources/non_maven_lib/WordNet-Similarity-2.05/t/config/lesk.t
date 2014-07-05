#! /usr/bin/perl -w

# Before 'make install' is run this script should be runnable with
# 'make test'.  After 'make install' it should work as 'perl t/config/lesk.t'

# A script to test configuration options specific to the
# WordNet::Similarity::lesk module.  This test works by
# constructing a config file that contains options specific
# to the lesk measure and then running some getRelatedness
# queries.

use strict;
use warnings;

my $num_tests;

BEGIN {
  $num_tests = 12;
}

use Test::More tests => $num_tests;

BEGIN {use_ok ('WordNet::QueryData')}
BEGIN {use_ok ('WordNet::Similarity::lesk')}

my $wn = new WordNet::QueryData;
ok ($wn);

my $config = "lesk$$.txt";
my $relation = "leskrelation$$.txt";
my $stop = "leskstop$$.txt";

# create a temporary file to use as a config file
ok (open FH, ">$config") or diag "Cannot create temporary config file: $!";
print FH "WordNet::Similarity::lesk\n";
print FH "stem::1\n";
print FH "relation::$relation\n";
print FH "normalize::0\n";
ok (close FH);

# create a temporary file to use as a relation file
ok (open RFH, ">$relation") or diag "Cannot create temporary file: $!";
print RFH "LeskRelationFile\n";
print RFH "glosexample-glosexample\n";
print RFH "glosexample(hype)-glosexample(hype)\n";
print RFH "glosexample(hypo)-glosexample(hypo)\n";
print RFH "glosexample(hype)-glosexample(hypo)\n";
print RFH "glosexample(hypo)-glosexample(hype)\n";
ok (close RFH);

my $module = new WordNet::Similarity::lesk ($wn, $config);
ok ($module);
my ($err, $errstr) = $module->getError();
is ($err, 0) or diag "$errstr";

my $score = $module->getRelatedness ('tree#n#1', 'shrub#n#1');
($err, $errstr) = $module->getError();
is ($err, 0);
ok ($score > 4800);
# was 4963 before Text::OverlapFinder was integrated
# would be 4909 without stemming

END {ok unlink ($config, $relation, $stop)}


