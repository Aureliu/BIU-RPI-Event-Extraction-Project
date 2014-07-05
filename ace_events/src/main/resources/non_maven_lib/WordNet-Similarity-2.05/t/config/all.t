#! /usr/bin/perl -w

# all.t version 2.05
# ($Id: all.t,v 1.10 2008/05/30 23:12:42 sidz1979 Exp $)
#
# Copyright (C) 2004
#
# Jason Michelizzi, University of Minnesota Duluth
# mich0212 at d.umn.edu
#
# Ted Pedersen, University of Minnesota Duluth
# tpederse at d.umn.edu

# Before 'make install' is run this script should be runnable with
# 'make test'.  After 'make install' it should work as 'perl t/config/all.t'

# A script to run general tests of the ability of various WordNet::Similarity
# modules to read config files.  This script simply tries to contruct each
# measure while specifying a configuration file.

use strict;
use warnings;

use Test::More tests => 40;

BEGIN {use_ok ('File::Spec')}
BEGIN {use_ok ('WordNet::QueryData')}
BEGIN {use_ok ('WordNet::Similarity::hso')}
BEGIN {use_ok ('WordNet::Similarity::jcn')}
BEGIN {use_ok ('WordNet::Similarity::lch')}
BEGIN {use_ok ('WordNet::Similarity::lesk')}
BEGIN {use_ok ('WordNet::Similarity::lin')}
BEGIN {use_ok ('WordNet::Similarity::path')}
BEGIN {use_ok ('WordNet::Similarity::random')}
BEGIN {use_ok ('WordNet::Similarity::res')}
#BEGIN {use_ok ('WordNet::Similarity::vector')}
BEGIN {use_ok ('WordNet::Similarity::wup')}

my $wn = WordNet::QueryData->new;
ok ($wn);

my $fs = bless [], "File::Spec";

# find the sample config files that should come with this distribution
my $dir = $fs->catfile ('samples', 'config-files');
my $hsoconf = $fs->catfile ($dir, 'config-hso.conf');
my $jcnconf = $fs->catfile ($dir, 'config-jcn.conf');
my $lchconf = $fs->catfile ($dir, 'config-lch.conf');
my $leskconf = $fs->catfile ($dir, 'config-lesk.conf');
my $linconf = $fs->catfile ($dir, 'config-lin.conf');
my $pathconf = $fs->catfile ($dir, 'config-path.conf');
my $randomconf = $fs->catfile ($dir, 'config-random.conf');
my $resconf = $fs->catfile ($dir, 'config-res.conf');
#my $vectorconf = $fs->catfile ($dir, 'config-vector.conf');
my $wupconf = $fs->catfile ($dir, 'config-wup.conf');

# check to make sure the config files exist
ok (-e $dir) or diag "Can't find config files: be sure to run this script from the root of the directory tree";
ok (-e $hsoconf);
ok (-e $jcnconf);
ok (-e $lchconf);
ok (-e $leskconf);
ok (-e $linconf);
ok (-e $pathconf);
ok (-e $randomconf);
ok (-e $resconf);
#ok (-e $vectorconf);
ok (-e $wupconf);

# just try to create the modules using the config files

my $hso = WordNet::Similarity::hso->new ($wn, $hsoconf);
ok ($hso);
my ($err, $errstr) = $hso->getError ();
is ($err, 0) or diag "$errstr\n";

my $jcn = WordNet::Similarity::jcn->new ($wn, $jcnconf);
ok ($jcn);
($err, $errstr) = $jcn->getError ();
is ($err, 0) or diag "$errstr\n";

my $lch = WordNet::Similarity::lch->new ($wn, $lchconf);
ok ($lch);
($err, $errstr) = $lch->getError ();
is ($err, 0) or diag "$errstr\n";

my $lesk = WordNet::Similarity::lesk->new ($wn, $leskconf);
ok ($lesk);
($err, $errstr) = $lesk->getError ();
is ($err, 0) or diag "$errstr\n";

my $lin = WordNet::Similarity::lin->new ($wn, $linconf);
ok ($lin);
($err, $errstr) = $lin->getError ();
is ($err, 0) or diag "$errstr\n";

my $path = WordNet::Similarity::path->new ($wn, $pathconf);
ok ($path);
($err, $errstr) = $path->getError ();
is ($err, 0) or diag "$errstr\n";

 my $random = WordNet::Similarity::random->new ($wn, $randomconf);
ok ($random);
($err, $errstr) = $random->getError ();
is ($err, 0) or diag "$errstr\n";

my $res = WordNet::Similarity::res->new ($wn, $resconf);
ok ($res);
($err, $errstr) = $res->getError ();
is ($err, 0) or diag "$errstr\n";

#my $vector = WordNet::Similarity::vector->new ($wn, $vectorconf);
#ok ($vector);
#is (($vector->getError())[0], 0)

my $wup = WordNet::Similarity::wup->new ($wn, $wupconf);
ok ($wup);
($err, $errstr) = $res->getError ();
is ($err, 0) or diag "$errstr\n";


