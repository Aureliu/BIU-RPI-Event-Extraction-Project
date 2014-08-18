#!/usr/bin/perl -wT

use strict;

# where do we connect to the Similarity server?  Here:
my $remote_host = '127.0.0.1';
my $remote_port = '31134';
my $doc_base = '../../similarity';

use CGI;
use Socket;

BEGIN {
    # Our University's webserver uses an ancient version of CGI::Carp
    # so we can't do fatalsToBrowser.
    # The carpout() function lets us modify the format of messages sent to
    # a filehandle (in this case STDERR) to include timestamps
    use CGI::Carp 'carpout';
    carpout(*STDOUT);
}

# subroutine prototypes
sub showForm ($$$$);
sub round ($);

my $cgi = CGI->new;

# These are the colors of the text when we alternate text colors (when
# showing errors, for example).
my $text_color1 = 'black';
my $text_color2 = '#d03000';

# Mapping from hash-code to version
my %versionMap = ('eOS9lXC6GvMWznF1wkZofDdtbBU' => '3.0', 'LL1BZMsWkr0YOuiewfbiL656+Q4' => '2.1');

# print the HTTP header
print $cgi->header;

# if the showform parameter is no, then don't show the form--this is how
# we avoid showing the form in popups
my $showform = $cgi->param ('showform') || 'yes';

# show the start of the page (all the usual HTML that goes at the top
# of a page, etc.)
showPageStart ();

# check if we want to show the version information (version of WordNet, etc.)
my $showversion = $cgi->param ('version');
if ($showversion) {
    socket (Server, PF_INET, SOCK_STREAM, getprotobyname ('tcp'));

    my $internet_addr = inet_aton ($remote_host)
	or die "Could not convert $remote_host to an Internet addr: $!\n";
    my $paddr = sockaddr_in ($remote_port, $internet_addr);

    unless (connect (Server, $paddr)) {
	print "<p>Cannot connect to server $remote_host:$remote_port</p>\n";
	goto SHOW_END;
    }

    select ((select (Server), $|=1)[0]);

    print Server "v\015\012\015\012";
    print "<h2>Version information</h2>\n";
    while (my $line = <Server>) {
	last if $line eq "\015\012";
	if ($line =~ /^v (\S+) (\S+)/) {
            if($1 eq "WordNet") {
              my $verstring = $versionMap{$2};
	      print "<p>$1 version $verstring (hash-code: $2)</p>\n" if(defined($verstring));
	      print "<p>$1 hash-code: $2</p>\n" if(!defined($verstring));
            }
            else {
	      print "<p>$1 version $2</p>\n";
            }
	}
	elsif ($line =~ m/^! (.*)/) {
	    print "<p>$1</p>\n";
	}
	else {
	    print "<p>Strange message from server: $line\n";
	}
    }

    local $ENV{PATH} = "/usr/local/bin:/usr/bin:/bin:/sbin";
    my $t_osinfo = `uname -a` || "Couldn't get system information: $!";
    # $t_osinfo is tainted.  Use it in a pattern match and $1 will
    # be untainted.
    $t_osinfo =~ /(.*)/;
    print "<p>HTTP server: $ENV{HTTP_HOST} ($1)</p>\n";
    print "<p>Similarity server: $remote_host</p>\n";
    goto SHOW_END;
}

# check if we're generating this page as the result of a query; if so, then
# we need to show the results.
my $word1 = $cgi->param ('word1');
my $word2 = $cgi->param ('word2');
if ($word1 and !$word2) {
    print "<p>Word 2 was not specified.</p>";
}
elsif (!$word1 and $word2) {
    print "<p>Word 1 was not specified.</p>";
}
elsif ($word1 and $word2) {
    print "<hr />\n";

    socket (Server, PF_INET, SOCK_STREAM, getprotobyname ('tcp'));

    my $internet_addr = inet_aton ($remote_host)
	or die "Could not convert $remote_host to an Internet addr: $!\n";
    my $paddr = sockaddr_in ($remote_port, $internet_addr);

    unless (connect (Server, $paddr)) {
	print "<p>Cannot connect to server $remote_host:$remote_port</p>\n";
	goto SHOW_END;
    }

    select ((select (Server), $|=1)[0]);

    # value of the parameters can be 'all', 'gloss', or 'synset'
    my $w1option = $cgi->param ('senses1');
    my $w2option = $cgi->param ('senses2');

    my $query_type = 2;

    if ($w1option eq 'gloss' or $w1option eq 'synset') {
	$query_type = 1 unless $word1 =~ m/[^#]+\#[nvars]\#\d+/;
    }
    if ($w2option eq 'gloss' or $w2option eq 'synset') {
	$query_type = 1 unless $word2 =~ m/[^#]+\#[nvars]\#\d+/;
    }

    if ($query_type == 1) {
	my @senses1;
	my @senses2;

	if ($w1option eq 'gloss') {
	    print Server "g $word1\015\012";
	}
	elsif ($w1option eq 'synset') {
	    print Server "s $word1\015\012";
	}
	elsif ($w1option eq 'all') {
	    @senses1 = ([$word1, ""]);
	}
	else {
	    print "<pre>Internal error: invalid option `$w1option'</pre>\n";
	}

	if ($w2option eq 'gloss') {
	    print Server "g $word2\015\012";
	}
	elsif ($w2option eq 'synset') {
	    print Server "s $word2\015\012";
	}
	elsif ($w2option eq 'all') {
	    @senses2 = ([$word2, ""]);
	}
	else {
	    print "<pre>Internal error: invalid option `$w2option'</pre>\n";
	}

	print Server "\015\012";

	while (my $response = <Server>) {
	    last if $response eq "\015\012";

	    my $prefix = substr $response, 0, 1;
	    my $end = substr $response, 2;
	    if ($prefix eq 'g') {
		my ($wps, $gloss) = m/([^#]+\#[nvar]\#\d+) (.*)/;
		print "<p>$wps: $gloss</p>";
	    }
	    elsif ($prefix eq '1') {
		my ($wps, $gloss) = $end =~ m/([^#]+\#[nvar]\#\d+) (.*)/;
		push @senses1, [$wps, $gloss];
	    }
	    elsif ($prefix eq '2') {
		my ($wps, $gloss) = $end =~ m/([^#]+\#[nvar]\#\d+) (.*)/;
		push @senses2, [$wps, $gloss];
	    }
	    else {
		print "Strange message from server `$response'";
	    }
	}

	my $measure = $cgi->param ('measure') || 'path';
	showForm (1, \@senses1, \@senses2, $measure);
	showPageEnd ();
	exit;
    }
    else {
	my $measure = $cgi->param ('measure');
	my $trace = $cgi->param ('trace') ? 'yes' : 'no';
	my $gloss = $cgi->param ('gloss') ? 'yes' : 'no';
	my $root = $cgi->param ('rootnode') ? 'yes' : 'no';
	my $syns  = $cgi->param ('synset') ? 'yes' : 'no';
	my $all_senses = $cgi->param ('sense') ? 1 : 0;

	$word1 =~ tr/A-Z /a-z_/;
	$word2 =~ tr/A-Z /a-z_/;

	# terminate all messages with CRLF (best to avoid \r\n because the
	# meaning of \r and \n varies from platform to platform
	if ($measure eq 'all') {
	    foreach my $m (qw/hso lch lesk lin jcn path res vector vector_pairs wup/) {
		print Server +("r $word1 $word2 $m $trace $gloss $syns $root",
			       "\015\012");
	    }
	    print Server "\015\012";
	}
	else {
	    print Server ("r $word1 $word2 $measure $trace $gloss $syns $root",
			  "\015\012\015\012");
	}

	my @glosses;
	my %scores;
	my @errors;
	my @synsets;
	my @version_info;
	my $lines = 0;
	my $last_measure = '';
	while (my $response = <Server>) {
	    last if $response eq "\015\012";
	    $lines++;
	    my $beginning = substr $response, 0, 1;
	    my $end = substr $response, 2;
	    if ($beginning eq '!') {
		$end =~ s/\s+$//;
		push @errors, $end;
	    }
	    elsif ($beginning eq 'r') {
		my ($measure, $wps1, $wps2, $score) = split /\s+/, $end;
		$score = round ($score);
		$last_measure = $measure;
		push @{$scores{$measure}}, [$score, $wps1, $wps2];
	    }
	    elsif ($beginning eq 't') {
		$end =~ s|<CRLF>|<br />|g;
		### FIXME -- we lost the traces
		push @{$scores{$last_measure}->[-1]}, "$end\n";
	    }
	    elsif ($beginning eq 'g') {
		my ($wps, @gloss_words) = split /\s+/, $end;
		push @glosses, [$wps, substr ($end, length ($wps))];
	    }
	    elsif ($beginning eq 's') {
		my (@synset_words) = split /\s+/, $end;
		push @synsets, [@synset_words];
	    }
	    elsif ($beginning eq 'v') {
		my ($package, $version) = split /\s+/, $end;
		push @version_info, [$package, $version];
	    }
	    else {
		push @errors,
		    "Error: received strange message from server `$response'";
	    }
	}

	my $query_string = $ENV{QUERY_STRING} || "";
	# replace literal ampersands with their XML entity equivalents
	$query_string =~ s/&/&amp;/g;

	if (scalar @version_info) {
	    foreach my $item (@version_info) {
		print "<p>$item->[0] version $item->[1]</p>\n";
	    }
	    goto SHOW_END;
	}

	# show errors, if any
	if (scalar @errors) {
	    unless ($cgi->param ('errors') eq 'show') {
		my $query = $query_string . '&amp;errors=show';
		my $url = "similarity.cgi?${query}";

		# Having onclick return false should keep the browser from
		# loading the page specified by href, but IE loads it
		# anyways.  That's why we set href to # instead of the
		# URL (setting it to the URL would let non-JavaScript
		# browsers see the page in the main window, but such
		# browsers are rare)
		print +("<p>",
			"<a href=\"#\" ",
			"onclick=\"showWindow ('$url', 'Errors'); return false;\">View errors</a>",
			'</p>',
			"\n");
	    }
	    else {
		print '<h2>Warnings/Errors:</h2>';
	
		print '<p class="errors">';
		my $parity = 0;
		foreach (0..$#errors) {
		    my $color = $parity ? $text_color1 : $text_color2;
		    print "<div style=\"color: $color\">$errors[$_]</div>";
		    $parity = !$parity;
		}
		print "</p>\n";

		goto SHOW_END;
	    }
	}

	# show glosses, if any
	if ($gloss eq 'yes') {
	    my $parity = 0;
	    if (scalar @glosses) {
		print '<h2>Glosses:</h2>';
		print '<p class="gloss">';

		print "<dl>";
		foreach my $ref (@glosses) {
		    print "<dt>$ref->[0]</dt><dd>$ref->[1]</dd>";
		}
		print "</dl>\n";
	    }
	    else {
		print "<p>Sorry, no glosses were found.</p>\n";
	    }
	    goto SHOW_END;
	}
	else {
	    my $query = $query_string . '&amp;gloss=yes';
	    my $url = "similarity.cgi?${query}";
	    print +('<p>',
		    "<a href=\"#\" ",
		    "onclick=\"showWindow ('$url', 'Glosses'); return false;\">",
                    "View glosses (definitions)</a>",
		    '</p>',
		    "\n");
	}

	if ($syns eq 'yes') {
	    # show complete synsets, if any were requested
	    if (scalar @synsets) {
		print '<h2>Synsets:</h2>';

		print '<p class="synset">';
		my $parity = 1;
		foreach (0..$#synsets) {
		    my $color = $parity ? $text_color1 : $text_color2;
		    print "<div style=\"color: $color\" class=\"synset\">{";
		    print join (', ', @{$synsets[$_]});
		    print "}</div>";
		    $parity = !$parity;
		}
		print "</p>\n";
	    }
	    else {
		print "<p>Sorry, no synsets were found.</p>\n";
	    }
	    goto SHOW_END;
	}
	else {
	    my $query = $query_string . '&amp;synset=yes';
	    my $url = "similarity.cgi?${query}";
	    print +('<p>',
		    "<a href=\"#\" ",
		    "onclick=\"showWindow ('$url', 'Synsets'); return false;\">View synsets</a>",
		    '</p>',
		    "\n");
	}

	if ($all_senses) {
	    print '<h2>Results:</h2>' if scalar keys %scores;
	    print '<table class="results" border="1">';
	    print '<tr><th>Measure</th><th>Word 1</th><th>Word 2</th><th>Score</th>';
	    print '<th>Trace</th>' if $trace eq 'yes';
	    print "</tr>\n";
	    foreach my $m (keys %scores) {
		my @scrs = sort {$b->[0] <=> $a->[0]} @{$scores{$m}};
		foreach (@scrs) {
		    my $wps1 = $_->[1];
		    $wps1 =~ s/\#/%23/g;
		    my $wps2 = $_->[2];
		    $wps2 =~ s/\#/%23/g;

		    print "<tr><td>$m</td>";
		    print "<td><a href=\"#\" onclick=\"showWindow ('wps.cgi?wps=$wps1', ''); return false;\">$_->[1]</a></td>";
		    print "<td><a href=\"#\" onclick=\"showWindow ('wps.cgi?wps=$wps2', ''); return false;\">$_->[2]</a></td>";
		    print "<td>$_->[0]</td>";

		    if ($trace eq 'yes') {
		        print "<td>$_->[3]</td>";
		    }

		    print "</tr>\n";
		}
	    }

	    print "</table>\n";
	}
	else {
	    my $query = $query_string;

	    # remove from the query string options that we don't want
	    $query =~ s/(?:&amp;)sense=yes//;
	    $query =~ s/(?:&amp;)?trace=yes//;
	    # now add the option we do want
	    $query .= '&amp;sense=yes';

	    # prepare two query strings--one without traces and one with
	    my $url_nt = "similarity.cgi?${query}"; # 'nt' means 'no trace'
	    my $url_trace = $url_nt . '&amp;trace=yes';

	    goto SHOW_END unless scalar keys %scores;

	    print '<h2>Results:</h2>';

	    foreach my $m (keys %scores) {
		my $good = $scores{$m}->[0];
		foreach my $i (1..$#{$scores{$m}}) {
		    if ($scores{$m}->[$i]->[0] > $good->[0]) {
			$good = $scores{$m}->[$i];
		    }
		}
		my $wps1 = $good->[1];
		$wps1 =~ s/\#/%23/g;
		my $wps2 = $good->[2];
		$wps2 =~ s/\#/%23/g;

		print +("\n<p class=\"results\">",
			"The relatedness of ",
			"<a href=\"#\" onclick=\"showWindow ('wps.cgi?wps=$wps1', ''); return false;\">$good->[1]</a> ",
			"and <a href=\"#\" onclick=\"showWindow ('wps.cgi?wps=$wps2', ''); return false;\">$good->[2]</a> ",
			"using $m is $good->[0].",
			"</p>\n");

		if ($trace eq 'yes') {
		    print "<p class=\"trace\">$good->[3]</p>";
		}
	    }

	    print +("<p><a href=\"#\" ",
                    "onclick=\"showWindow ('$url_nt', 'All senses'); return false\">",
		    "View relatedness of all senses (without traces)</a></p>\n");

	    print +("<p><a href=\"#\" ",
		    "onclick=\"showWindow ('$url_trace', 'All senses'); return false\">",
		    "View relatedness of all senses (with traces)</a></p>\n");
	}

	unless ($trace eq 'yes') {
	  my $urltrace = "similarity.cgi?${query_string}&amp;trace=yes";
	  print +("<p><a href=\"#\" ",
		  "onclick=\"showWindow ('$urltrace', 'Traces'); return false;\">",
		  "View traces</a></p>\n");
	}

    SHOW_END:
	print "<hr />";
	close Server;
    }
}

$word1 = defined $word1 ? $word1 : "";
$word2 = defined $word2 ? $word2 : "";
my $measure = 'path';#defined $measure ? $measure : 'path';


showForm (2, $word1, $word2, $measure) unless $showform eq 'no';
showPageEnd ();
exit;

# ========= subroutines =========

sub round ($)
{
    my $num = shift;
    my $str = sprintf ("%.4f", $num);
    $str =~ s/\.?0+$//;

    return $str;
}

sub showPageStart
{
    print <<"EOINTRO";
<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>Similarity</title>
  <link rel="stylesheet" href="$doc_base/sim-style.css" type="text/css" />
  <script type="text/javascript">
    <!-- hide script from old browsers
    function measureChanged ()
    {
        /* get the form that we want */
        var myform = document.getElementById ("queryform");
        /* get the currently selected measure, put it in mm */
        var mm = myform.measure.options[myform.measure.selectedIndex];

        if (mm.value == "path" || mm.value == "wup" || mm.value == "lch"
            || mm.value == "res" || mm.value == "lin" || mm.value == "jcn"
            || mm.value == "all") {
           myform.rootnode.disabled = "";
        }
        else {
            myform.rootnode.disabled = "disabled";
        }
    }

    function formReset ()
    {
        window.location = "similarity.cgi";
    }

    function showWindow (url, title)
    {
        url += '&showform=no';
        var nw = window.open (url, "", "width=625, height=625, scrollbars=yes, resizeable=yes, location=no, toolbar=no");
        nw.document.title = title;
    }

    // -->
  </script>

</head>
<body>

   <div id="umdlogo" style="float: left">
     <a href="http://www.d.umn.edu/"><img style="border: 0px"
        src="$doc_base/logo_black.gif"
       alt="University of Minnesota Duluth" /></a>
   </div>

  <h1>WordNet::Similarity</h1>
  <p>Read an overview of
    <a href="http://search.cpan.org/dist/WordNet-Similarity/doc/intro.pod">WordNet::Similarity</a>.
  </p>

EOINTRO
}


sub showForm ($$$$)
{
    my ($type, $arg1, $arg2, $arg3) = @_;
    # the 'action' attribute for the HTML form below--should be the script
    # name
    my $action = 'similarity.cgi';

    print <<"EOFORM1";
  <p>You may enter any two words in one of three formats:</p>
  <ol>
    <li><tt>word</tt></li>
    <li><tt>word#part_of_speech</tt> (where part_of_speech is one of n, v, a,
        or r)</li>
    <li><tt>word#part_of_speech#sense</tt> (where sense is a positive integer)</li>
  </ol>

  <p>If words are entered in format 1 or 2, then the relatedness of all
     valid forms of the words will be computed (e.g., if 'dogs' is entered,
     then 'dog' will be used to compute relatedness).
     <a href="$doc_base/instructions.html">More instructions</a>.</p>

  <form action="$action" method="get" id="queryform" onreset="formReset()">
    <p>
EOFORM1

    # check if we are trying to get the user to type in a pair of words or
    # if the user needs to select senses from a option menu.
    if ($type == 2) {
	# the user needs to type in two words

	print <<"EOT";
      <label for="word1in" class="leftlabel">Word 1:</label>
      <input type="text" name="word1" id="word1in" value=\"$arg1\" />
      <input type="radio" name="senses1" id="senses1Ain" checked="checked" value="all" />
      <label for="senses1Ain">Use all senses</label>
      <input type="radio" name="senses1" id="senses1Bin" value="gloss" />
      <label for="senses1Bin">Pick a sense by <a href="#" onclick="showWindow ('$doc_base/explanations.html#glossdef'); return false;">gloss</a></label>
      <input type="radio" name="senses1" id="senses1Cin" value="synset" />
      <label for="senses1Cin">Pick a sense by <a href="#" onclick="showWindow ('$doc_base/explanations.html#synsetdef'); return false;">synset</a></label>
      <br />

      <label for="word2in" class="leftlabel">Word 2:</label>
      <input type="text" name="word2" id="word2in" value=\"$arg2\" />
      <input type="radio" name="senses2" id="senses2Ain" checked="checked" value="all" />
      <label for="senses2Ain">Use all senses</label>
      <input type="radio" name="senses2" id="senses2Bin" value="gloss" />
      <label for="senses2Bin">Pick a sense by <a href="#" onclick="showWindow ('$doc_base/explanations.html#glossdef'); return false;">gloss</a></label>
      <input type="radio" name="senses2" id="senses2Cin" value="synset" />
      <label for="senses2Cin">Pick a sense by <a href="#" onclick="showWindow ('$doc_base/explanations.html#synsetdef'); return false;">synset</a></label>
      <br />
EOT
    }
    else {
	# the user needs to select word senses from a menu

	print "<label for=\"word1in\" class=\"leftlabel\">Word 1:</label>\n";
	print "<select name=\"word1\" id=\"word1in\" style=\"width: 4in\">\n";
	foreach my $ref (@$arg1) {
	    my ($sense, $gloss) = @$ref;
	    print "<option value=\"$sense\">$sense: $gloss</option>\n";
	}
	print "</select><br />\n";
	print "<label for=\"word2in\" class=\"leftlabel\">Word 2:</label>\n";
	print "<select name=\"word2\" id=\"word2in\" style=\"width: 4in\">\n";
	foreach my $ref (@$arg2) {
	    my ($sense, $gloss) = @$ref;
	    print "<option value=\"$sense\">$sense: $gloss</option>\n";
	}
	print "</select><br />\n";
    }

    print '<label for="measurepull" class="leftlabel">Measure:</label>', "\n";
    print '<select name="measure" id="measurepull" ',
      'onchange="measureChanged();">', "\n";
    my @measures = (['all', 'Use All Measures'],
		    ['path', 'Path Length'],
		    ['lch', 'Leacock &amp; Chodorow'],
		    ['wup', 'Wu &amp; Palmer'], 
		    ['res', 'Resnik'],
		    ['jcn', 'Jiang &amp; Conrath'],
		    ['lin', 'Lin'],
		    ['lesk', 'Adapted Lesk (Extended Gloss Overlaps)'],
                    ['vector', 'Gloss Vectors'],
		    ['vector_pairs', 'Gloss Vectors (pairwise)'],
		    ['hso', 'Hirst &amp; St-Onge'],
		    ['random', 'Random Measure']);

    foreach (@measures) {
	my $selected = $_->[0] eq $arg3 ? 'selected="selected"' : '';

	print "<option value=\"$_->[0]\" $selected>$_->[1]</option>\n";
    }
    print "</select>\n";

    print <<"EOFORM";
      <a href="$doc_base/measures.html">About the measures</a><br />

      <input type="checkbox" name="rootnode" id="rootin" value="yes"
       checked="checked" />
      <label for="rootin">Use <a href="#" onclick="showWindow ('$doc_base/explanations.html#rootnodedef', 'Definitions'); return false;">root node</a>?</label>
      <br />

    <!--
      <input type="checkbox" name="trace" id="tracein" value="yes" />
      <label for="tracein">Show trace?</label><br />
     -->

    <!--
      <input type="checkbox" name="gloss" id="glossin" value="yes" />
      <label for="glossin">Show glosses (definitions)?</label><br />
     -->

    <!--
      <input type="checkbox" name="synset" id="synsetin" value="yes" />
      <label for="synsetin">Show complete synsets?</label><br />
     -->

    <!--
      <input type="checkbox" name="sense" id="sensein" value="yes" />
      <label for="sensein">Show all senses?</label><br />
     -->

      <input type="submit" value="Compute" />
      <input type="reset" value="Clear" />
    </p>
  </form>

  <p><a href="similarity.cgi?version=yes">Show version info</a></p>

<hr />

EOFORM

}

sub showPageEnd
{
    print <<'ENDOFPAGE';
<div class="footer">
Created by Ted Pedersen and Jason Michelizzi
<br />E-mail: tpederse (at) d (dot) umn (dot) edu 
</div>
</body>
</html>
ENDOFPAGE
}

__END__

=head1 NAME

similarity.cgi - a CGI script implementing a portion of a web interface for
WordNet::Similarity

=head1 DESCRIPTION

This script works in conjunction with similarity_server.pl and wps.cgi to
provide a web interface for WordNet::Similarity.  The documentation
for similarity_server.pl describes how messages are passed between this
script and that one.

=head1 AUTHORS

 Ted Pedersen, University of Minnesota Duluth
 tpederse at d.umn.edu

 Jason Michelizzi

=head1 BUGS

None known.

=head1 COPYRIGHT

Copyright (c) 2005-2008, Ted Pedersen and Jason Michelizzi

This program is free software; you may redistribute and/or modify it under
the terms of the GNU General Public License version 2 or, at your option, any
later version.

=cut
