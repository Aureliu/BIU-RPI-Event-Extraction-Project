#! /usr/bin/perl -w

# Before 'make install' is run this script should be runnable with
# 'make test'.  After 'make install' it should work as 'perl t/utils/compounds.t'

# A script to test the compounds.pl utility.  The utility is used
# to generate a list of compounds.  That list is then search for
# certain pre-determined compounds.

use strict;
use warnings;

use Test::More tests => 13;

BEGIN {use_ok ('File::Spec')}

my $perl = $^X;
my $compounds = File::Spec->catfile ("utils", "compounds.pl");

# we need a null device
my $devnull = File::Spec->devnull;

ok ($devnull);
ok (-e $devnull);
ok (-e $compounds);
ok (-r $compounds);
ok (-x $compounds);

my $output = qx|$perl -MExtUtils::testlib $compounds 2> $devnull|;
is ($? >> 8, 0) or diag "Failed to execute '$compounds' properly";

# make sure that certain compounds are found in the output
like ($output, qr/\bablative_absolute\b/);
like ($output, qr/\bbaseball_diamond\b/);
like ($output, qr/\bhockey_stick\b/);
like ($output, qr/\bvocative_case\b/);
like ($output, qr/\bclassical_greek\b/);
like ($output, qr/\bvulgar_latin\b/);

