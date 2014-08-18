#! /usr/bin/perl -w

# Before 'make install' is run this script should be runnable with
# 'make test'.  After 'make install' it should work as 'perl t/config/invalid.t'

# A script that ensures that errors/warnings are raised when the modules
# are given invalid config files.  The following tests are run:
#
# 1) a config file contains an invalid value for an option
# 2) a config file contains the same option twice
# 3) a config file contains an unknown/nonsensical option


use strict;
use warnings;

my @measures;
my $num_tests;

BEGIN {
  @measures = qw/res lin jcn path lch wup hso lesk/; #vector
  $num_tests = 12 + 24 * scalar (@measures);
}

use Test::More tests => $num_tests;

BEGIN {use_ok ('WordNet::QueryData')}
BEGIN {use_ok ('WordNet::Similarity::res')}
BEGIN {use_ok ('WordNet::Similarity::lin')}
BEGIN {use_ok ('WordNet::Similarity::jcn')}
BEGIN {use_ok ('WordNet::Similarity::path')}
BEGIN {use_ok ('WordNet::Similarity::lch')}
BEGIN {use_ok ('WordNet::Similarity::wup')}
BEGIN {use_ok ('WordNet::Similarity::hso')}
BEGIN {use_ok ('WordNet::Similarity::lesk')}
BEGIN {use_ok ('WordNet::Similarity::random')}
#BEGIN {use_ok ('WordNet::Similarity::vector')}

my $wn = WordNet::QueryData->new;
ok ($wn);

my $config = "tempconf$$.txt";

# first batch of invalid files: bad trace level
foreach my $measure (@measures) {
  ok (open FH, ">$config") or diag "Could not create temporary file: $!";
  print FH "WordNet::Similarity::$measure\n";
  print FH "trace::11\n";
  ok (close FH);

  my $module = "WordNet::Similarity::$measure"->new ($wn, $config);
  ok ($module);
  my ($err, $errstr) = $module->getError ();
  cmp_ok ($err, '>', 0);
}

# try using an unknown and nonsensical option
foreach my $measure (@measures) {
  ok (open FH, ">$config") or diag "Could not create temporary file: $!";
  print FH "WordNet::Similarity::$measure\n";
  print FH "adfjkl::1\n";
  ok (close FH);

  my $module = "WordNet::Similarity::$measure"->new ($wn, $config);
  ok ($module);
  my ($err, $errstr) = $module->getError ();
  cmp_ok ($err, '>', 0);
}

# try using an invalid value for the cache:: option
foreach my $measure (@measures) {
  ok (open FH, ">$config") or diag "Could not create temporary file: $!";
  print FH "WordNet::Similarity::$measure\n";
  print FH "cache::2\n";
  ok (close FH);

  my $module = "WordNet::Similarity::$measure"->new ($wn, $config);
  ok ($module);
  my ($err, $errstr) = $module->getError ();
  cmp_ok ($err, '>', 0);
}

# try using an invalid value for the maxCacheSize option
foreach my $measure (@measures) {
  ok (open FH, ">$config") or diag "Could not create temporary file: $!";
  print FH "WordNet::Similarity::$measure\n";
  print FH "maxCacheSize::-1\n";
  ok (close FH);

  my $module = "WordNet::Similarity::$measure"->new ($wn, $config);
  ok ($module);
  my ($err, $errstr) = $module->getError ();
  cmp_ok ($err, '>', 0);
}

# try an invalid value for the trace option
foreach my $measure (@measures) {
  ok (open FH, ">$config") or diag "Could not create temporary file: $!";
  print FH "WordNet::Similarity::$measure\n";
  print FH "trace::nothanks\n";
  ok (close FH);

  my $module = "WordNet::Similarity::$measure"->new ($wn, $config);
  ok ($module);
  my ($err, $errstr) = $module->getError ();
  cmp_ok ($err, '>', 0);
}

foreach my $measure (@measures) {
  ok (open FH, ">$config") or diag "Could not create temporary file: $!";
  print FH "WordNet::Similarity::$measure\n";
  print FH "trace::0\ntrace::1\n";
  ok (close FH);

  my $module = "WordNet::Similarity::$measure"->new ($wn, $config);
  ok ($module);
  my ($err, $errstr) = $module->getError ();
  cmp_ok ($err, '>', 0);
}


END {
  ok (unlink $config);
}
