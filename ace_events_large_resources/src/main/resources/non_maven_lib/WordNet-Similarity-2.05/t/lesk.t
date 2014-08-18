#! /usr/bin/perl -w

# Before `make install' is performed this script should be runnable with
# `make test'. After `make install' it should work as `perl t/lesk.t'

##################### We start with some black magic to print on failure.

BEGIN { $| = 1; print "1..7\n"; }
END {print "not ok 1\n" unless $loaded;}
use WordNet::Similarity;
use WordNet::QueryData;
use WordNet::Similarity::lesk;
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

############ Load lesk

$lesk = WordNet::Similarity::lesk->new($wn);
if($lesk)
{
    ($err, $errString) = $lesk->getError();
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

############ Load lesk with undef QueryData.

$badLesk = WordNet::Similarity::lesk->new(undef);
if($badLesk)
{
    ($err, $errString) = $badLesk->getError();
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

$value = $lesk->getRelatedness("object#n#1", "object#n#1");
if($value && $value =~ /[0-9]+/)
{
    if($value > 10)
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

if(defined $lesk->getRelatedness("hjxlq#n", "pynbr#n"))
{
    print "not ok 6\n";
}
else
{
    ($err, $errString) = $lesk->getError();
    if($err == 1)
    {
	print "ok 6\n";
    }
    else
    {
	print "not ok 6\n";
    }
}

########### getRelatedness across parts of speech

$value = $lesk->getRelatedness("pay#v#1", "money#n#1");
if($value && $value =~ /[0-9]+/)
{
    if($value > 0)
    {
	print "ok 7\n";
    }
    else
    {
	print "not ok 7\n";
    }
}
else
{
    print "not ok 7\n";
}
