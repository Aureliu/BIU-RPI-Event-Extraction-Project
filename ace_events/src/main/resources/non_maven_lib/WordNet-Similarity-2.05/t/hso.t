#! /usr/bin/perl -w

# Before `make install' is performed this script should be runnable with
# `make test'. After `make install' it should work as `perl t/hso.t'

##################### We start with some black magic to print on failure.

BEGIN { $| = 1; print "1..12\n"; }
END {print "not ok 1\n" unless $loaded;}
use WordNet::Similarity;
use WordNet::QueryData;
use WordNet::Similarity::hso;
$loaded = 1;
print "ok 1\n";

######################### End of black magic.

# Insert your test code below (better if it prints "ok 13"
# (correspondingly "not ok 13") depending on the success of chunk 13
# of the test code):

############ Load QueryData

$wn = WordNet::QueryData->new;
if($wn)
{
    print "ok 2\n";
}
else
{
    print "not ok 2\n";
}

############ Load hso

$hso = WordNet::Similarity::hso->new($wn);
if($hso)
{
    ($err, $errString) = $hso->getError();
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

############ Load hso with undef QueryData.

$badHso = WordNet::Similarity::hso->new(undef);
if($badHso)
{
    ($err, $errString) = $badHso->getError();
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

$value = $hso->getRelatedness("object#n#1", "object#n#1");
if($value && $value =~ /[0-9]+/)
{
    if($value == 16)
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

if(defined $hso->getRelatedness("hjxlq#n", "pynbr#n"))
{
    print "not ok 6\n";
}
else
{
    ($err, $errString) = $hso->getError();
    if($err == 1)
    {
	print "ok 6\n";
    }
    else
    {
	print "not ok 6\n";
    }
}

############ Test horizontal link.

$hso->{'trace'} = 1;
$value = $hso->getRelatedness("intelligent#a#1", "intelligence#n#1");
if(defined $value && $value =~ /[0-9]+/)
{
    if($value == 16)
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

############ Test traces.

if($hso->getTraceString() !~ /Strong Rel \(Horizontal Match\)/)
{
    print "not ok 8\n";
}
else
{
    print "ok 8\n";
}

############ Compound Match...

$value = $hso->getRelatedness("school#n#1", "private_school#n#1");
if(defined $value && $value =~ /[0-9]+/)
{
    if($value == 16)
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

############ A path search...

$value = $hso->getRelatedness("bumper_car#n#1", "vehicle#n#1");
if(defined $value && $value =~ /[0-9]+/)
{
    if($value == 4)
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

## Antonymy doesn't seem to be working the way it should... as is the 
## case with some of the other relations.
## Wait for new version of QueryData?
## Test 11 and 12 to check that.
## Tried "query" instead of "querySense"... seems to work.

############ Check the horizontal link.

$value = $hso->getRelatedness("up#a#1", "down#a#1");
if(defined $value && $value =~ /[0-9]+/)
{
    if($value == 16)
    {
	print "ok 11\n";
    }
    else
    {
	print "not ok 11\n";
    }
}
else
{
    print "not ok 11\n";
}

# ############ Test traces.

if($hso->getTraceString() !~ /Strong Rel \(Horizontal Match\)/)
{
    print "not ok 12\n";
}
else
{
    print "ok 12\n";
}
