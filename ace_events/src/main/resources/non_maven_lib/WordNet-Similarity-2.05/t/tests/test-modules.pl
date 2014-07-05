#! /usr/bin/perl -w

use strict;
use warnings;
use constant SCRIPT_NAME => 'test-modules.pl';

use Getopt::Long;

use WordNet::Similarity::res;
use WordNet::Similarity::lin;
use WordNet::Similarity::jcn;
use WordNet::Similarity::path;
use WordNet::Similarity::wup;
use WordNet::Similarity::lch;
use WordNet::Similarity::hso;
use WordNet::Similarity::lesk;
use WordNet::Similarity::random;
use WordNet::Similarity::vector;

use WordNet::QueryData;


our ($opt_wnpath, $opt_version, $opt_help, $opt_type, $opt_outfile);
our ($opt_config);

my $result = GetOptions ("wnpath=s", "version", "help", "type=s", "outfile=s",
			 "config=s");

unless ($result) {
  showUsage ();
  exit (1);
}

if ($opt_help) {
  showHelp ();
  exit (0);
}

if ($opt_version) {
  print "".SCRIPT_NAME." version 2.05\n";
  print "Copyright (C) 2003, Jason Michelizzi and Ted Pedersen\n";
  exit (0);
}

unless ($opt_type) {
  showUsage ();
  exit (1);
}

my $outfh = *STDOUT;
if ($opt_outfile) {
  open ($outfh, ">$opt_outfile") or die "Cannot open $opt_outfile: $!";
}

# find WordNet
my $wnhome = '/usr/local/WordNet-2.0/dict';
$wnhome = $ENV{WNHOME} if $ENV{WNHOME};
$wnhome = $opt_wnpath if $opt_wnpath;

print STDERR "Loading WordNet... ";
my $wn = new WordNet::QueryData ($wnhome);
if ($wn) {
  print STDERR "done.\n";
}
else {
  print STDERR "failed.\n";
  exit (1);
}

my $module = $opt_type;
$module = "WordNet::Similarity::".$module
  unless $module =~ m/WordNet::Similarity/;


print STDERR "Loading $module... ";
my $measure = $opt_config ? new $module ($wn, $opt_config) : new $module ($wn);
print STDERR "done.\n";

my ($err, $errstr) = $measure->getError ();
$err and die "Error creating $module: ($errstr)";

# now do some tests
my @pairs = (['contrition#n#1', 'compunction#n#1'],
	     ['dog#n#1', 'hunting_dog#n#1'],
	     ['dog#n#1', 'cat#n#1'],
	     ['levity#n#1', 'contrition#n#1'],
	     ['hope#n#2', 'compunction#n#1'],
	     ['like#v#2', 'cotton#v#1'],
	     ['adore#v#1', 'like#v#2'],
	     ['hope#n#2', 'like#v#2'],
	     ['blue_sky#n#1', 'Earth#n#4'],
	     ['worship#v#1', 'adore#v#1'],
	     ['outer_space#n#1', 'abstraction#n#6'],
	     ['languish#v#2', 'weep#v#1'],
	     ['hjxlq#n', 'ynbr#n'],
	     ['intelligent#a#1', 'intelligence#n#1'],
	     ['weep#v#1', 'erupt#v#6'],
	     ['leather_carp#n#1', 'grotto#n#1'],
	    );

foreach my $pair (@pairs) {
  print $outfh "$pair->[0], $pair->[1]: ";
  my $score = $measure->getRelatedness ($pair->[0], $pair->[1]);
  if (defined $score) {
    print $outfh "$score\n";
  }
  else {
    my ($err, $errString) = $measure->getError ();
    print $outfh "undefined (error level $err)\n";
  }
}

close $outfh if $opt_outfile;

sub showUsage {
  print "Usage: ".SCRIPT_NAME." {--type=measure | --version | --help}\n";
}

sub showHelp {
  print "Usage: ".SCRIPT_NAME." {--type=measure | --version | --help}\n";
  print "Options:\n";
  print "\t--type=measure     specifies the name of a measure to test\n";
  print "\t--version          show version information\n";
  print "\t--help             show this help message\n";
}

__END__

=head1 NAME

test-modules.pl -- run tests on WordNet::Similarity modules

=head1 SYNOPSIS

test-modules.pl [--type=measure | --version | --help ]

=head1 DESCRIPTION

B<Warning>: The following is out-of-date and just plain wrong.

Use this to test WordNet::Similarity.  You must have run Makefile.PL and
make first (but before you run make install).  Runs some tests on a bunch
of pairs of word senses, printing to the standard output.  To see if a
new distribution is working correctly, do this:

  ./test-distro.pl res --no-debug > stable.text
  ./test-distro.pl res > test.text
  diff stable.text test.text

You may substitute the measure of your choice for 'res' above.

=head1 OPTIONS

B<--no-debug>

    When this option is given, the version of WordNet::Similarity found
    in /usr/lib/perl5/site_perl/5.8.0 (or something similar) is used instead
    of the version in ./blib/lib/

