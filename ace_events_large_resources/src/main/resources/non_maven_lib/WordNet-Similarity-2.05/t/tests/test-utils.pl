#! /usr/bin/perl -w

use strict;
use warnings;

use Getopt::Long;

our ($opt_version, $opt_help, $opt_wnpath, $opt_config, $opt_type);

my $res = GetOptions ("version", "help", "wnpath=s", "config=s", "type=s");

unless ($res) {
  showUsage ();
  exit 0;
}

if ($opt_help) {
  showHelp ();
  exit 0;
}

if ($opt_version) {
  showVersion();
  exit 0;
}

unless ($opt_type) {
  showUsage();
  exit (1);
}

# run some tests on similarity.pl and other utils here
my $wnhome = '/usr/local/WordNet-2.0/dict';
$wnhome = $ENV{WNHOME} if $ENV{WNHOME};
$wnhome = $opt_wnpath if $opt_wnpath;

$opt_type = "WordNet::Similarity::".$opt_type
  unless $opt_type =~ m/WordNet::Similarity/;

if ($opt_config) {
  system ('similarity.pl',
	  "--type=$opt_type",
	  "--wnpath=$wnhome",
	  "--config=$opt_config",
	  "dog#n#1",
	  "cat#n#1");
}
else {
  system ('similarity.pl', "--type=$opt_type", "--wnpath=$wnhome",
	  "dog#n#1", "cat#n#1");
}

exit ($? >> 8);

sub showUsage {
  print "Usage: test-utils.pl [[--wnpath=PATH] [--config=FILE]\n";
  print "                      | --version | --help]\n";
}

sub showHelp {
  showUsage();
  print "Options:\n";
  print "\t--wnpath=PATH    the path to WordNet data files\n";
  print "\t--config=FILE    the name of a config file\n";
  print "\t--help           show this help message\n";
  print "\t--version        show version information\n";
}

sub showVersion {
  print "test-utils.pl version 2.05\n";
  print "Copyright (C) 2003, Jason Michelizzi and Ted Pedersen\n";
  print "\nThis program is free software; you may redistribute it and/or\n";
  print "modify it under the same terms as Perl itself.\n";
}

__END__

