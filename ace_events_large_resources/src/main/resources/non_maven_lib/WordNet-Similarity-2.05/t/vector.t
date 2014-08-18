#! /usr/bin/perl -w

# Before `make install' is performed this script should be runnable with
# `make test'. After `make install' it should work as `perl t/vector.t'

##################### We start with some black magic to print on failure.

BEGIN { $| = 1; print "1..6\n"; }
END {print "not ok 1\n" unless $loaded;}
use WordNet::Similarity;
use WordNet::QueryData;
use WordNet::Similarity::vector;
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

############ Load vector

$vector = WordNet::Similarity::vector->new($wn);
if($vector)
{
    ($err, $errString) = $vector->getError();
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

############ Load vector with undef QueryData.

$badVector = WordNet::Similarity::vector->new(undef);
if($badVector)
{
    ($err, $errString) = $badVector->getError();
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

############ getRelatedness of badly formed synset.
## (Tried getRelatedness of unknown synsets... "hjxlq#n#1", "pynbr#n#2"... 
##  QueryData complains... cannot trap that error myself.)

if(defined $vector->getRelatedness("hjxlq#n", "pynbr#n"))
{
    print "not ok 5\n";
}
else
{
    ($err, $errString) = $vector->getError();
    if($err == 1)
    {
	print "ok 5\n";
    }
    else
    {
	print "not ok 5\n";
    }
}

########### getRelatedness across parts of speech

$value = $vector->getRelatedness("pay#v#1", "money#n#1");
if(defined $value && $value =~ /[\.0-9]+/)
{
    if($value > 0)
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
