#! /usr/bin/perl -w

# Before 'make install' is run this script should be runnable with
# 'make test'.  After 'make install' it should work as 'perl t/utils/sim-file.t'

# A script to ensure that similarity.pl handles input from files correctly
# and ensures that invalid files result in a warning or error.  The
# following cases are tested:
#
# 1) a valid file containing comments and words in full w#p#s format
#    as well as w#p and w formats
# 2) an invalid file with only one word on a line
# 3) an invalid file with a bad part of speech ina wps string
# 4) an invalid file with a bad sense number

use strict;
use warnings;

my @measures;
my $num_tests;

BEGIN {
  @measures = qw/res lin jcn path lch wup hso lesk/; # vector
  $num_tests = 12 + 5 * scalar (@measures);
}

use Test::More tests => $num_tests;

BEGIN {use_ok ('File::Spec')}

@measures = map "WordNet::Similarity::$_", @measures;

my $similarity = File::Spec->catfile ('utils', 'similarity.pl');
# find null device (e.g., /dev/null)
my $devnull = File::Spec->devnull ();

ok (-e $similarity);
ok (-r $similarity);
ok (-x $similarity);

my $perl = $^X;

# create a temporary file to serve as an input file to similarity.pl
my $wordsfile = "wordpairs$$.txt";
ok (open FH, ">$wordsfile") or diag "Could not create a temporary file: $!";

print FH "dog#n cat#n\n";
print FH "dog#n#1 cat#n#1\n";
print FH "// this is a comment\n";
print FH "trombone bassoon//another comment\n";
print FH "reed brass // each has multiple senses\n";

ok (close FH);

# just make sure that no errors/warnings occur with this file (since it
# is a valid file)
foreach my $measure (@measures) {
  my $output = qx|$perl -MExtUtils::testlib $similarity --type=$measure --file=$wordsfile 2>&1|;
  is ($?, 0);
  unlike ($output, qr/Warning|Error/i);
}

# create a new, invalid file
ok (open FH, ">$wordsfile") or diag "Could not re-create a temporary file: $!";
print FH "dog\n";
ok (close FH);

# check to make sure an error occurs when only one word is given
foreach my $measure (@measures) {
  my $output = qx/$perl -MExtUtils::testlib $similarity --type=$measure --file=$wordsfile 2>&1/;
  like ($output, qr/Warning: line 1/i);
}

# create a new, invalid file
ok (open FH, ">$wordsfile") or diag "Could not re-create a temporary file: $!";
print FH "cat#dog#1 dog#n#1\n";
ok (close FH);

foreach my $measure (@measures) {
  my $output = qx/$perl -MExtUtils::testlib $similarity --type=$measure --file=$wordsfile 2>&1/;
  like ($output, qr/not found in WordNet/i);
}

# create a new, invalid file
ok (open FH, ">$wordsfile") or diag "Could not re-create a temporary file: $!";
print FH "cat#n#n cat#n#2\n";
ok (close FH);

foreach my $measure (@measures) {
  my $output = qx/$perl -MExtUtils::testlib $similarity --type=$measure --file=$wordsfile 2>&1/;
  like ($output, qr/not found in WordNet/i);
}

END {
  unlink $wordsfile;
}
