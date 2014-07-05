#! /usr/bin/perl -w

# trace.t version 2.05
# ($Id: trace.t,v 1.9 2008/05/30 23:12:40 sidz1979 Exp $)
#
# Copyright (C) 2004
#
# Jason Michelizzi, University of Minnesota Duluth
# mich0212 at d.umn.edu
#
# Ted Pedersen, University of Minnesota Duluth
# tpederse at d.umn.edu

# Before 'make install' is performed this script should be runnable with
# 'make test'.  After 'make install' it should work as 'perl t/trace.t'

# A script to tracing in the relatedness modules.  The following tests are
# run:
# * A fixed set of word pairs is used to generate trace strings which are
#   compared to a "key" (stored in t/keys).

# This script supports two options:
# 1) the --key option can be used to generate a key, in which case no actual
#    tests are run.
# 2) the --keydir option can be used to specify where the key is or should be
#    stored.  The default value is t/keys.

use strict;
use warnings;

my @measures;
my $num_tests;
my @wordpairs;
sub diff ($$);

BEGIN {
  # the names of the measures we're going to test.  Testing random
  # doesn't seem to make much since, and the vector module is still
  # experimental.
  @measures = qw/hso jcn lch lesk lin path res wup/;

  # the set of words for which we are going to calculate relatedness
  # values (the testing is being done on the trace strings generated
  # while calculating relatedness)
  @wordpairs = (['dog#n#1', 'cat#n#1'],
		['untruly#r#1', 'expositive#a#1'],
		['noncyclic#a#1', 'indecisive#a#2'],
		['art#n#3','Hadrian#n#1'],
		['mortise#v#1', 'Cystophora_cristata#n#1'],
		['polychromize#v#1', 'stress#v#2'],
#		['ambrose#n#1', 'john_chrysostom#n#1'],
		['professional_football#n#1', 'scat#n#1']
	       );
  $num_tests = 20
    + 7 * (scalar @measures)
    + scalar (@measures) * scalar (@wordpairs);
}

use Test::More tests => $num_tests;

BEGIN {use_ok 'Getopt::Long'}

our ($opt_key, $opt_keydir);

my $result = GetOptions ("key", "keydir=s");
ok ($result);

BEGIN {use_ok 'WordNet::QueryData'}
BEGIN {use_ok 'WordNet::Tools'}
BEGIN {use_ok 'WordNet::Similarity::hso'}
BEGIN {use_ok 'WordNet::Similarity::jcn'}
BEGIN {use_ok 'WordNet::Similarity::lch'}
BEGIN {use_ok 'WordNet::Similarity::lesk'}
BEGIN {use_ok 'WordNet::Similarity::lin'}
BEGIN {use_ok 'WordNet::Similarity::path'}
#BEGIN {use_ok 'WordNet::Similarity::random'}
BEGIN {use_ok 'WordNet::Similarity::res'}
#BEGIN {use_ok 'WordNet::Similarity::vector'}
BEGIN {use_ok 'WordNet::Similarity::wup'}
BEGIN {use_ok 'File::Spec'}
BEGIN {use_ok 'File::Copy'}

my $wn = WordNet::QueryData->new;
ok ($wn) or diag "Could not create WordNet::QueryData object";

my $wntools = WordNet::Tools->new($wn);
ok ($wntools);

my $wnHash = $wntools->hashCode();
ok ($wnHash);

my $config = "trace$$.cfg";
my $tempfile = "trace$$.tmp";

my $keydir = File::Spec->catdir ('t', 'keys');
$keydir = $opt_keydir if ($opt_keydir);

my $wnkey = File::Spec->catfile ($keydir, "wnver.key");
ok (open WNF, $wnkey);
my $wnver = <WNF>;
ok (defined $wnver);
$wnver = "" if(!defined($wnver));
$wnver =~ s/[\r\f\n]+//g;
$wnver =~ s/^\s+//;
$wnver =~ s/\s+$//;
ok (close WNF);

SKIP: {
  skip "Hash-code of key file(s) does not match installed WordNet", 112 if($wnver ne $wnHash);

  foreach my $measure (@measures) {
    ok (open CFH, '>', $config) or diag "Could not open $config for writing: $!";
    print CFH "WordNet::Similarity::$measure\ntrace::1\n";
    ok (close CFH) or diag "Could not close $config: $!";

    my $module = "WordNet::Similarity::$measure"->new ($wn, $config);
    # my $module = "WordNet::Similarity::$measure"->new ($wn);
    ok ($module) or diag "Failed to create $measure module";
    my ($err, $errstr) = $module->getError ();
    is ($err, 0) or diag $errstr;

    # turn on tracing
    $module->{trace} = 1;

    ok (open TFH, '>', $tempfile) or diag "Could not open $tempfile: $!";

    foreach my $pair (@wordpairs) {
      my ($wps1, $wps2) = @$pair;
      my $score = $module->getRelatedness ($wps1, $wps2);
      my $tracestr = $module->getTraceString ();
      $module->getError (); # clear errors
      ok ($tracestr) or diag "$measure ($wps1, $wps2) has no trace";
      print TFH $tracestr;
    }

    my $keyfile = File::Spec->catfile ($keydir, "${measure}trace.key");

    unless ($opt_key) {
      ok (close TFH) or diag "Could not close $tempfile: $!";
      my ($diff, $msg) = diff ($tempfile, $keyfile);
      is ($diff, 0) or diag "$measure: $msg";
    }
    else {
      ok (close TFH) or diag "Could not close $tempfile: $!";
      ok (copy $tempfile, $keyfile)
        or diag "Could not copy file $tempfile to $keyfile: $!";
    }
  }
}

END {
  unlink $config;
  unlink $tempfile;
}

sub diff ($$) {
  my ($file1, $file2) = @_;
  open FH1, $file1 or return (1, "Couldn't open '$file1': $!");
  open FH2, $file2 or return (2, "Couldn't open '$file2': $!");

  # chomp'ing the lines might not be necessary, but it might help
  # if one file were generated on DOS and the other on Unix.  On
  # the other hand, it might not, but in any case, I don't think that
  # it'd hurt.
  my @lines1 = map {chomp; $_} <FH1>;
  my @lines2 = map {chomp; $_} <FH2>;

  unless (scalar (@lines1) == scalar (@lines2)) {
    my $got = scalar @lines1;
    my $want = scalar @lines2;
    return (3, "Wrong number of lines: got $got expected $want");
  }

  for (0..$#lines1) {
    if ($lines1[$_] ne $lines2[$_]) {
      return (4, "The lines '$lines1[$_]' and '$lines2[$_]' differ");
    }
  }

  return 0;
}
