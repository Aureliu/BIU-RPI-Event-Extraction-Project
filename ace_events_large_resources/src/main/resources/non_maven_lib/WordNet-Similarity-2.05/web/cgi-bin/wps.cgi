#!/usr/bin/perl -wT

use strict;

use CGI;
use Socket;

my $remote_host = '127.0.0.1';
my $remote_port = 31134;
my $doc_base = '../../similarity';

my $cgi = CGI->new;

print $cgi->header;

my $wps = $cgi->param ('wps') || 'undefined word';

print <<"EOB";
<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                      "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>Gloss for $wps</title>
  <link rel="stylesheet" href="$doc_base/sim-style.css" type="text/css" />
</head>
<body>
EOB

unless ($wps =~ /[^#]+#[nvars]#\d+/) {
    print "<p>Error: bad input word: $wps</p>\n";
    goto SHOW_END;
}

# connect to Similarity server
socket (Server, PF_INET, SOCK_STREAM, getprotobyname ('tcp'));

my $internet_addr = inet_aton ($remote_host) or do {
    print "<p>Could not convert $remote_host to an IP address: $!</p>\n";
    goto SHOW_END;
};

my $paddr = sockaddr_in ($remote_port, $internet_addr);

unless (connect (Server, $paddr)) {
    print "<p>Cannot connect to server $remote_host:$remote_port ($!)</p>\n";
    goto SHOW_END;
}

select ((select (Server), $|=1)[0]);

print Server "g $wps\015\012";
print Server "\015\012";

while (my $line = <Server>) {
    last if $line eq "\015\012";
    my ($type, $str) = $line =~ m/^(\S+) (.+)/;
    if ($type eq 'g' or $type eq '1') {
	my ($wps, $gloss) = $str =~ m/([^#]+#[nvar]#\d+) (.*)/;
	print "<dl><dt>$wps</dt><dd>$gloss</dd></dl>\n";
    }
    elsif ($type eq '!') {
	print "<p>$str</p>\n";
    }
    else {
	print "<p>Error: odd message from server: ($type) $str</p>\n";
    }
}

SHOW_END:

close Server;

print <<'EOH';
</body>
</html>
EOH

__END__

=head1 NAME

wps.cgi

=head1 DESCRIPTION

This script takes one parameter 'wps', which is a valid word#pos#sense string
and produces a web page that displays the gloss of the synset to which that
word sense belongs.

=head1 AUTHORS

 Ted Pedersen, University of Minnesota Duluth
 tpederse at d.umn.edu

 Jason Michelizzi

=head1 BUGS

None known.

=head1 COPYRIGHT

Copyright (c) 2005-2008, Ted Pedersen and Jason Michelizzi

This program is free software; you may redistribute and/or modify it under the
terms of the GNU General Public License, version 2 or, at your option, any
later version.

=cut

