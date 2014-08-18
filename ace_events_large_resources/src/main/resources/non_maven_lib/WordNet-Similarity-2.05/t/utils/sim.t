#! /usr/bin/perl -w

# sim.t version 2.05
# ($Id: sim.t,v 1.8 2008/05/30 23:12:43 sidz1979 Exp $)
#
# Copyright (C) 2004
#
# Jason Michelizzi, University of Minnesota Duluth
# mich0212 at d.umn.edu
#
# Ted Pedersen, University of Minnesota Duluth
# tpederse at d.umn.edu

# Before 'make install' is run this script should be runnable with
# 'make test'.  After 'make install' it should work as 'perl t/utils/sim.t'

# A script to run general tests on the similarity.pl utility.  The following
# tests are run:
# 1) test whether similarity.pl runs correctly given two input words on
#    the command line
# 2) test whether similarity.pl runs correctly when a configuration file
#    is given and two words are specified on the command line

use strict;
use warnings;

my @measures;
my $num_tests;

BEGIN {
  @measures = qw/res lin jcn path lch wup random lesk hso/; # vector
  $num_tests = 6 + 6 * scalar (@measures);
}

use Test::More tests => $num_tests;

BEGIN {use_ok ('File::Spec')}

my $perl = $^X;
my $similarity = File::Spec->catfile ('utils', 'similarity.pl');

# get name of null device (such as /dev/null)
my $devnull = File::Spec->devnull ();

ok (-e $devnull);
ok (-e $similarity);
ok (-r $similarity);
ok (-x $similarity);

@measures = map "WordNet::Similarity::$_", @measures;

# run a simple test using all measures only making sure that no errors occured
foreach my $measure (@measures) {
  my $output = qx|$perl -MExtUtils::testlib $similarity --type=$measure dog#n#1 cat#n#1 2>&1|;

  unlike ($output, qr/Error|Warning/i);
  is ($? >> 8, 0, "Exited without errors");
}

my $config = "config$$.txt";

# now we run some more simple tests, this time using config files
foreach my $measure (@measures) {
  ok (open FH, ">$config") or diag "Could not create a temporary file: $!";
  print FH "$measure\n";
  print FH "cache::1\n";
  print FH "trace::1\n";
  ok (close FH);

  # again, just make sure no errors occured
  my $output = qx|$perl -MExtUtils::testlib $similarity --type=$measure --config=$config dog#n#1 cat#n#1 2>&1|;
  is ($? >> 8, 0);
  unlike ($output, qr/Error|Warning/i);
}

END {
  ok (unlink $config);
}
