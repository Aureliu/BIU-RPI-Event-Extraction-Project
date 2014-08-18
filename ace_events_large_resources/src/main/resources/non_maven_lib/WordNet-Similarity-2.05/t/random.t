#! /usr/bin/perl -w

# Before `make install' is performed this script should be runnable with
# `make test'. After `make install' it should work as `perl t/random.t'

##################### We start with some black magic to print on failure.

BEGIN { $| = 1; print "1..7\n"; }
END {print "not ok 1\n" unless $loaded;}
use WordNet::Similarity;
use WordNet::QueryData;
use WordNet::Similarity::random;
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

############ Load random

$random = WordNet::Similarity::random->new($wn);
if($random)
{
    ($err, $errString) = $random->getError();
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

############ Load random with undef QueryData.

$badRandom = WordNet::Similarity::random->new(undef);
if($badRandom)
{
    ($err, $errString) = $badRandom->getError();
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

$value = $random->getRelatedness("object#n#1", "object#n#1");
if($value && $value =~ /[0-9]+(\.[0-9]+)?/)
{
    print "ok 5\n";
}
else
{
    print "not ok 5\n";
}

########### getRelatedness across parts of speech

$value2 = $random->getRelatedness("pay#v#1", "money#n#1");
if($value2 && $value2 =~ /[0-9]+/)
{
    if($value2 > 0)
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

########### Test the cache.

$value2 = $random->getRelatedness("object#n#1", "object#n#1");
if($value2 && $value && $value == $value2)
{
    print "ok 7\n";
}
else
{
    print "not ok 7\n";
}
