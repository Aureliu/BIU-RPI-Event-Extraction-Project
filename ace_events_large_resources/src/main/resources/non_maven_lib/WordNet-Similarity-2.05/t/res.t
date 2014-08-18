#! /usr/bin/perl -w

# Before `make install' is performed this script should be runnable with
# `make test'. After `make install' it should work as `perl t/res.t'

# A script to test the Resnik (res.pm) measure.  This script runs the
# following tests:
#
# 1) tests whether the modules are constructed correctly
# 2) tests whether an error is given when no WordNet::QueryData object
#    is supplied
# 3) simply getRelatedness queries are performed on valid words, invalid
#    words, and words from different parts of speech

##################### We start with some black magic to print on failure.

BEGIN { $| = 1; print "1..8\n"; }
END {print "not ok 1\n" unless $loaded;}
use WordNet::Similarity;
use WordNet::QueryData;
use WordNet::Similarity::res;
$loaded = 1;
print "ok 1\n";

######################### End of black magic.

# Insert your test code below (better if it prints "ok 13"
# (correspondingly "not ok 13") depending on the success of chunk 13
# of the test code):

############ Load QueryData

$wn = WordNet::QueryData->new();
if($wn)
{
    print "ok 2\n";
}
else
{
    print "not ok 2\n";
}

############ Load res

$m = WordNet::Similarity::res->new($wn);
if($m)
{
    ($err, $errString) = $m->getError();
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

############ Load res with undef QueryData.

$bad = WordNet::Similarity::res->new(undef);
if($bad)
{
    ($err, $errString) = $bad->getError();
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

############ GetRelatedness of same synset.

$value = $m->getRelatedness("object#n#1", "object#n#1");
if($value && $value =~ /[0-9]+(\.[0-9]+)?/)
{
    if($value > 0)
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

############ getRelatedness of badly formed synset.
## (Tried getRelatedness of unknown synsets... "hjxlq#n#1", "pynbr#n#2"... 
##  QueryData complains... can't trap that error myself.)

if(defined $m->getRelatedness("hjxlq#n", "pynbr#n"))
{
    print "not ok 6\n";
}
else
{
    ($err, $errString) = $m->getError();
    if($err == 1)
    {
	print "ok 6\n";
    }
    else
    {
	print "not ok 6\n";
    }
}

############ Relatedness across parts of speech.

$m->{'trace'} = 1;
if($m->getRelatedness("object#n#1", "run#v#1") >= 0)
{
    print "not ok 7\n";
}
else
{
    print "ok 7\n";
}

############ Test traces.
# JM 1-6-04
# we changed how words from different parts of speech are handled
#if($m->getTraceString() !~ /Relatedness 0 across parts of speech/)

if (($m->getError())[0] != 1)
{
    print "not ok 8\n";
}
else
{
    print "ok 8\n";
}
