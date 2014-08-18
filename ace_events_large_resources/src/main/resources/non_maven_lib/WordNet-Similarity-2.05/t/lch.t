#! /usr/bin/perl -w

# Before `make install' is performed this script should be runnable with
# `make test'. After `make install' it should work as `perl t/lch.t'

# A script to test the Leacock & Chodorow (lch.pm) measure.  This
# script runs the following tests:
#
# 1) tests whether the modules are constructed correctly
# 2) tests whether an error is given when no WordNet::QueryData object
#    is supplied
# 3) simple getRelatedness queries are performed on valid words, invalid
#    words, and words from different parts of speech

# We start with some black magic to print on failure.

BEGIN { $| = 1; print "1..9\n"; }
END {print "not ok 1\n" unless $loaded;}
use WordNet::Similarity;
use WordNet::QueryData;
use WordNet::Similarity::lch;
$loaded = 1;
print "ok 1\n";

use strict;
use warnings;

# Load QueryData
my $wn = WordNet::QueryData->new();
if($wn)
{
  print "ok 2\n";
}
else
{
  print "not ok 2\n";
}

# Load lch
my $lch = WordNet::Similarity::lch->new($wn);
if($lch)
{
  my ($err, $errString) = $lch->getError();
  if($err)
  {
    print "not ok 3\n";
  }
  else
  {
    print "ok 3\n";
  }
}
else
{
  print "not ok 3\n";
}

# Load lch with undef QueryData.
my $badLch = WordNet::Similarity::lch->new(undef);
if($badLch)
{
  my ($err, $errString) = $badLch->getError();
  if($err < 2)
  {
    print "not ok 4\n";
  }
  elsif($err == 2)
  {
    if($errString =~ /A WordNet::QueryData object is required/)
    {
      print "ok 4\n";
    }
    else
    {
      print "not ok 4\n";
    }
  }
  else
  {
    print "not ok 4\n";
  }
}
else
{
  print "not ok 4\n";
}

# GetRelatedness of same synset.
my $value1 = undef;
my $value2 = undef;
eval {$value1 = $lch->getRelatedness("object#n#1", "object#n#1");};
eval {$value2 = $lch->getRelatedness("entity#n#1", "entity#n#1");};
if($@)
{
  print "not ok 5\n";
}
elsif($value1 && $value1 =~ /[0-9]+(\.[0-9]+)?/
   && $value2 && $value2 =~ /[0-9]+(\.[0-9]+)?/)
{
  if(($value1 - $value2) < 0.0001)
  {
    print "ok 5\n";
  }
  else
  {
    print "not ok 5\n";
  }
}
else
{
  print "not ok 5\n";
}

# getRelatedness of badly formed synset.
$value1 = undef;
eval {$value1 = $lch->getRelatedness("hjxlq#n", "pynbr#n");};
if($@ || defined($value1))
{
  print "not ok 6\n";
}
else
{
  my ($err, $errString) = $lch->getError();
  if($err == 1)
  {
    print "ok 6\n";
  }
  else
  {
    print "not ok 6\n";
  }
}

# Relatedness across parts of speech.
$lch->{'trace'} = 1;
$value1 = undef;
eval {$value1 = $lch->getRelatedness("object#n#1", "run#v#1");};
if($value1 >= 0)
{
  print "not ok 7\n";
}
else
{
  print "ok 7\n";
}

# Test traces.
# JM 1-6-04
# we changed how words from different parts of speech are handled
#if($m->getTraceString() !~ /Relatedness 0 across parts of speech/)
if(($lch->getError())[0] != 1)
{
  print "not ok 8\n";
}
else
{
  print "ok 8\n";
}

# Testing self-similarity of tilde
eval {$value1 = $lch->getRelatedness("tilde#n#1", "tilde#n#1");};
if($@)
{
  print "not ok 9\n";
}
elsif(defined($value1))
{
  if(($value1 - $value2) < 0.0001)
  {
    print "ok 9\n";
  }
  else
  {
    print "not ok 9\n";
  }
}
else
{
  print "not ok 9\n";
}
