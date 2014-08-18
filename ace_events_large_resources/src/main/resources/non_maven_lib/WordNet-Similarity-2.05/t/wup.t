#! /usr/bin/perl -w

# Before `make install' is performed this script should be runnable with
# `make test'. After `make install' it should work as `perl t/wup.t'

# A script to test the Wu & Palmer (wup.pm) measure.  This script runs the
# following tests:
#
# 1) tests whether the modules are constructed correctly
# 2) tests whether an error is given when no WordNet::QueryData object
#    is supplied
# 3) simple getRelatedness queries are performed on valid words, invalid
#    words, and words from different parts of speech

# We start with some black magic to print on failure.
BEGIN { $| = 1; print "1..11\n"; }
END {print "not ok 1\n" unless $loaded;}
use WordNet::Similarity;
use WordNet::QueryData;
use WordNet::Similarity::wup;
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

# Load wup
my $wup = WordNet::Similarity::wup->new($wn);
if($wup)
{
  my ($err, $errString) = $wup->getError();
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

# Load wup with undef QueryData.
my $badwup = WordNet::Similarity::wup->new(undef);
if($badwup)
{
  my ($err, $errString) = $badwup->getError();
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
my $value = undef;
eval {$value = $wup->getRelatedness("object#n#1", "object#n#1");};
if($@)
{
  print "not ok 5\n";
}
elsif($value && $value =~ /[0-9]/)
{
  if($value == 1)
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

$value = undef;
eval {$value = $wup->getRelatedness("eating_apple#n#1", "eating_apple#n#1");};
if($@)
{
  print "not ok 6\n";
}
elsif(defined $value && $value =~ /\d+/)
{
  if($value == 1)
  {
    print "ok 6\n";
  }
  else 
  {
    print "not ok 6\n";
  }
}
else
{
  print "not ok 6\n";
}

# getRelatedness of badly formed synset.
$value = undef;
eval {$value = $wup->getRelatedness("hjxlq#n", "pynbr#n");};
if($@)
{
  print "not ok 7\n";
}
elsif($value)
{
  print "not ok 7\n";
}
else
{
  my ($err, $errString) = $wup->getError();
  if($err == 1)
  {
    print "ok 7\n";
  }
  else
  {
    print "not ok 7\n";
  }
}

# Relatedness across parts of speech.
$wup->{trace} = 1;
$value = undef;
eval {$value = $wup->getRelatedness("object#n#1", "run#v#1");};
if($@)
{
  print "not ok 8\n";
}
elsif($value && $value >= 0)
{
  print "not ok 8\n";
}
else
{
  print "ok 8\n";
}

# Test traces.
# JM 1-6-04
# we changed how words from different parts of speech are handled
# if($m->getTraceString() !~ /Relatedness 0 across parts of speech/)
if(($wup->getError())[0] != 1)
{
  print "not ok 9\n";
}
else
{
  print "ok 9\n";
}

# Test tilde self-similarity
eval {$value = $wup->getRelatedness("tilde#n#1", "tilde#n#1");};
if($@)
{
  print "not ok 10\n";
}
elsif($value && $value =~ /[0-9]/)
{
  if($value == 1)
  {
    print "ok 10\n";
  }
  else
  {
    print "not ok 10\n";
  }
}
else
{
  print "not ok 10\n";
}

# getRelatedness of badly formed synset.
# (Tried getRelatedness of unknown synsets... "hjxlq#n#1", "pynbr#n#2"...
#  QueryData complains... trap that error myself.)
$value = undef;
eval {$value = $wup->getRelatedness("hjxlq#n#1", "pynbr#n#2");};
if($@)
{
  print "ok 11\n";
}
elsif(defined $value)
{
  print "not ok 11\n";
}
else
{
  print "ok 11\n";
}
