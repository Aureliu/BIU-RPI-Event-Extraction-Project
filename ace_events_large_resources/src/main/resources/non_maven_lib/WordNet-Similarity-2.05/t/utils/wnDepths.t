#! /usr/bin/perl -w

# Before 'make install' is run this script should be runnable with
# 'make test'.  After 'make install' it should work as 'perl t/utils/wnDepths.t'

# A script to test the wnDepths.pl utility program.  The following
# tests are performed:
#
# 1) wnDepths.pl is run and the output is inspected for a few specific
#    cases.
# 2) the entire output is compared to a "key" file.

# This script supports two options:
# 1) the --key option can be used to generate a key, in which case no actual
#    tests are run.
# 2) the --keydir option can be used to specify where the key is or should be
#    stored.  The default value is t/keys.


use strict;
use warnings;

use Getopt::Long;

our ($opt_key, $opt_keydir);

GetOptions ("key", "keydir=s") or die "Bad command line options";

use Test::More 'no_plan';

BEGIN {use_ok 'File::Spec'}

my $wndepths = File::Spec->catfile ('utils', 'wnDepths.pl');
my $devnull = File::Spec->devnull;
my $perl = $^X;

ok (-e $devnull);
ok (-e $wndepths);

my $tempfile = "depths$$.txt";

ok (open FH, "$perl -MExtUtils::testlib $wndepths 2> $devnull |")
  or diag "Could not open pipe from $wndepths: $!";

my @depths = <FH>;

my $line = $depths[0];
chomp $line;
my (undef, $wnver) = split /::/, $line;
ok ($wnver);

SKIP:
{
  unless ($wnver eq '2.1' or $wnver eq '2.0' or $wnver eq '1.7.1') {
    skip ("Skipping tests dependent on deprecated 'version' method", 5)
  }

  for (1..$#depths) {
    my $line = $depths[$_];
    # here we test a few top level nodes to make sure
    # they are the correct depth

    my ($pos, $offset, $depth) = split /\s+/, $line;

    if ($wnver eq '2.1') {
      # entity#n#1
      if (($pos eq 'n') and (0 + '00001740' == $offset)) {
	is ($depth, 18);
      }
      # hold#v#3
      elsif (($pos eq 'v') and (0 + '01205350' == $offset)) {
	is ($depth, 5);
      }
      # move#v#3
      elsif (($pos eq 'v') and (0 + '01814387' == $offset)) {
	is ($depth, 6);
      }
      # express_emotion#v#1
      elsif (($pos eq 'v') and (0 + '01785325' == $offset)) {
	is ($depth, 3);
      }
      # touch#v#1
      elsif (($pos eq 'v') and (0 + '01194934' == $offset)) {
        is ($depth, 7);
      }
    }
    elsif ($wnver eq '2.0') {
      # event#n#1
      if (($pos eq 'n') and (0 + '00025950' == $offset)) {
	is ($depth, 9);
      }
      # entity#n#1
      elsif (($pos eq 'n') and (0 + '00001740' == $offset)) {
	is ($depth, 17);
      }
      # hold#v#3
      elsif (($pos eq 'v') and (0 + '01179760' == $offset)) {
	is ($depth, 5);
      }
      # move#v#3
      elsif (($pos eq 'v') and (0 + '01778173' == $offset)) {
	is ($depth, 6);
      }
      # express_emotion#v#1
      elsif (($pos eq 'v') and (0 + '01750193' == $offset)) {
	is ($depth, 3);
      }
    }
    elsif ($wnver eq '1.7.1') {
      # event#n#1
      if (($pos eq 'n') and (0 + '00021905' == $offset)) {
	is ($depth, 9);
      }
      # entity#n#1
      elsif (($pos eq 'n') and (0 + '00001742' == $offset)) {
	is ($depth, 17);
      }
      # hold#v#3
      elsif (($pos eq 'v') and (0 + '00961954' == $offset)) {
	is ($depth, 5);
      }
      # move#v#3
      elsif (($pos eq 'v') and (0 + '01438226' == $offset)) {
	is ($depth, 6);
      }
      # express_emotion#v#1
      elsif (($pos eq 'v') and (0 + '01416015' == $offset)) {
	is ($depth, 3);
      }
    }
  }
}

my $keydir = File::Spec->catdir ('t', 'keys');

$keydir = $opt_keydir if $opt_keydir;

my $keyfile = File::Spec->catfile ($keydir, 'wndepths.txt');

my $wfile = File::Spec->catfile ($keydir, "wnver.key");
ok (open WNF, $wfile);
my $wnHash = <WNF>;
ok ($wnHash);
$wnHash = "" if(!defined($wnHash));
$wnHash =~ s/[\r\f\n]+//g;
$wnHash =~ s/^\s+//;
$wnHash =~ s/\s+$//;
ok (close WNF);

# Since 5.8.0, Perl hash randomized its hash function.  Since wnDepths.pl
# stores the depths in a hash and then just dumps the depths to a file
# in the order that they come out, the order of the file might change
# every time wnDepths.pl is run.
@depths = sort @depths;

SKIP: {
  skip "Hash-code of key file(s) does not match installed WordNet", 1 if($wnHash ne $wnver);
  unless ($opt_key) {
    ok (open KFFH, $keyfile) or diag "Could not open $keyfile: $!";

    my @keys = <KFFH>;

    ok (close KFFH) or diag "Could not close $keyfile: $!";

    is (scalar @depths, scalar @keys);

    for (0..$#depths) {
      is ($depths[$_], $keys[$_]);
    }
  }
  else {
    ok (open KFFH, '>', $keyfile) or diag "Could not open $keyfile: $!";
    print KFFH @depths;
    ok (close KFFH);
    exit 0;
  }
}

__END__
