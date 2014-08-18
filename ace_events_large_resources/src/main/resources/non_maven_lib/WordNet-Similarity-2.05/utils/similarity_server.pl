#!/usr/bin/perl -wT
#
# similarity_server.pl version 2.05
# (Last updated $Id: similarity_server.pl,v 1.7 2008/05/30 23:12:44 sidz1979 Exp $)
#
# ---------------------------------------------------------------------

# Include external packages
use strict;
use Getopt::Long;
use File::Temp;
use File::Spec;
use WordNet::Similarity;
use POSIX ':sys_wait_h';  # for waitpid() and friends; used by reaper()
use POSIX qw(setsid);     # to daemonize

# Get the command-line options
our($opt_wnhome, $opt_port, $opt_logfile, $opt_maxchild, $opt_stoplist, $opt_version, $opt_help);
&GetOptions("wnhome=s", "port=i", "logfile=s", "maxchild=i", "stoplist=s", "version",  "help");

# Check for version
if(defined($opt_version))
{
  print "similarity_server.pl version 2.05\n";
  print "WordNet::Similarity version ".($WordNet::Similarity::VERSION)."\n";
  print "Copyright (c) 2005-2008, Ted Pedersen, Siddharth Patwardhan and Jason Michelizzi.\n";
  exit;
}

# Check for help
if(defined($opt_help))
{
  print "Usage: similarity_server.pl [--port PORTNUMBER] [--logfile LOGFILE] [--maxchild NUM] [--stoplist STOPFILE]\n";
  print "                            | --help\n";
  print "                            | --version\n";
  print "\nStarts the similarity server, which listens for requests on a predefined\n";
  print "port. It presents a netork interface to the WordNet::Similarity moduels.\n\n";
  print "Options:\n";
  print "--port        Specify the port PORTNUMBER for the server to listen on.\n";
  print "--logfile     The output LOGFILE where any error or warning messages\n";
  print "              should be written out.\n";
  print "--maxchild    Specify the maximum number NUM of the processes that should\n";
  print "              be forked to handle the requests.\n";
  print "--stoplist    A file STOPFILE of stop words to be provided to the lesk and\n";
  print "              vector modules.\n";
  print "--help        Display this help message and quit.\n";
  print "--version     Display the version information and quit.\n";
  exit;
}

# Local variables
my $localport = 31134;
my $error_log = undef;
my $stoplist;
my $maxchild = 4; # max number of child processes at one time
sub reaper;

# Set the log file, if specified
$error_log = $1 if(defined($opt_logfile) and $opt_logfile ne "" and $opt_logfile =~ /^(.*)$/);
print STDERR "Error log = ".($error_log?$error_log:"<none>")."\n";

# Set the stop list, if specified
$stoplist = $1 if(defined($opt_stoplist) and $opt_stoplist ne "" and $opt_stoplist =~ /^(.*)$/);
print STDERR "Stoplist = ".($stoplist?$stoplist:"<none>")."\n";

# Set the port
$localport = $opt_port if(defined($opt_port));
print STDERR "Local port = $localport\n";

# Set the maxchild
$maxchild = $opt_maxchild if(defined($opt_maxchild));
print STDERR "Maxchild = $maxchild\n";

# Create the temporary lock file
my $lockfh = File::Temp->new();
my $lock_file = $lockfh->filename();
die "Error: Unable to create temporary lock file.\n" if(!$lockfh);
print $lockfh $$;
close $lockfh or die "Error: Cannot close lock file: $! \n";
print STDERR "Loading modules... ";

# prototypes:
sub getAllForms ($);
sub getlock ();
sub releaselock ();
sub timestamp ($);
use sigtrap handler => \&bailout, 'normal-signals';
use IO::Socket::INET;
use WordNet::QueryData;
use WordNet::Tools;
use WordNet::Similarity::hso;
use WordNet::Similarity::jcn;
use WordNet::Similarity::lch;
use WordNet::Similarity::lesk;
use WordNet::Similarity::lin;
use WordNet::Similarity::path;
use WordNet::Similarity::random;
use WordNet::Similarity::res;
use WordNet::Similarity::vector;
use WordNet::Similarity::vector_pairs;
use WordNet::Similarity::wup;
my $wnlocation = undef;
$wnlocation = File::Spec->catfile($opt_wnhome, "dict") if(defined($opt_wnhome));
my $wn = WordNet::QueryData->new($wnlocation);
$wn or die "Error: Couldn't construct WordNet::QueryData object.\n";
my $wntools = WordNet::Tools->new($wn);
$wntools or die "Error: Unable to create WordNet::Tools object.\n";
our $hso = WordNet::Similarity::hso->new($wn);
our $jcn = WordNet::Similarity::jcn->new($wn);
our $lch = WordNet::Similarity::lch->new($wn);
my $leskfh = File::Temp->new();
my $leskcfg = $leskfh->filename();
die "Error: Unable to create temporary config file.\n" if(!$leskfh);
print $leskfh "WordNet::Similarity::lesk\n";
print $leskfh "stem::1\n";
print $leskfh "stop::$stoplist\n" if(defined($stoplist) && -e $stoplist);
close $leskfh or die "Error: Unable to close config file.\n";
our $lesk = WordNet::Similarity::lesk->new($wn, $leskcfg);
my $vectorfh = File::Temp->new();
my $vectorcfg = $vectorfh->filename();
die "Error: Unable to create temporary config file.\n" if(!$vectorfh);
print $vectorfh "WordNet::Similarity::vector_pairs\n";
print $vectorfh "stop::$stoplist\n" if(defined($stoplist) && -e $stoplist);
print $vectorfh "stem::1\n";
close $vectorfh or die "Error: Unable to close config file.\n";
our $vector_pairs = WordNet::Similarity::vector_pairs->new($wn, $vectorcfg);
my $vectorpfh = File::Temp->new();
my $vectorpcfg = $vectorfh->filename();
die "Error: Unable to create temporary config file.\n" if(!$vectorpfh);
print $vectorpfh "WordNet::Similarity::vector\n";
print $vectorpfh "stop::$stoplist\n" if(defined($stoplist) && -e $stoplist);
print $vectorpfh "stem::1\n";
close $vectorpfh or die "Error: Unable to close config file.\n";
our $vector = WordNet::Similarity::vector->new($wn, $vectorpcfg);
our $lin = WordNet::Similarity::lin->new($wn);
our $path = WordNet::Similarity::path->new($wn);
our $random = WordNet::Similarity::random->new($wn);
our $res = WordNet::Similarity::res->new($wn);
our $wup = WordNet::Similarity::wup->new($wn);
my @measures = ($hso, $jcn, $lch, $lesk, $lin, $path, $random, $res, $wup, $vector, $vector_pairs);

foreach (@measures)
{
  my ($err, $errstr) = $_->getError();
  die "$errstr died.\n" if $err;
}
undef @measures;

# reset (untaint) the PATH
$ENV{PATH} = '/bin:/usr/bin:/usr/local/bin';
print STDERR "done.\n";
print STDERR "Starting server... going into background.\n";

# Daemonize
open STDIN, '/dev/null' or die "Can't read /dev/null: $! \n";
open STDOUT, '>>/dev/null' or die "Can't write to /dev/null: $! \n";

# The is the socket we listen to
my $socket = IO::Socket::INET->new(
  LocalPort => $localport,
  Listen => SOMAXCONN,
  Reuse => 1,
  Type => SOCK_STREAM
) or die "Could not bind to network port: $! \n";
print STDERR "Closing output to terminal.\n";
if(defined($error_log))
{
  print STDERR "All future messages will be routed to the log file.\n";
  if(!open(STDERR, '>>', $error_log))
  {
    print "Error: Could open error log.\n";
    die "Error: Could not re-open STDERR.\n";
  }
  chmod 0664, $error_log;
}
else
{
  print STDERR "No more messages will be printed (even if the server dies).\n";
  open STDERR, '>>/dev/null' or die "Can't write to /dev/null: $! \n";
}
chdir '/' or die(&timestamp("Can't chdir to /: $! \n"));
defined(my $pid = fork) or die(&timestamp("Can't fork: $! \n"));
exit if $pid;
setsid or die(&timestamp("Can't start a new session: $! \n"));
umask 0;

# this variable is incremented after every fork, and is
# updated by reaper() when a child process dies
my $num_children = 0;
## SEE BELOW
# automatically reap child processes
#$SIG{CHLD} = 'IGNORE';
##
## BETTER WAY:
# handle death of child process
$SIG{CHLD} = \&reaper;
my $interrupted = 0;
ACCEPT:
while((my $client = $socket->accept) or $interrupted)
{
  $interrupted = 0;
  next unless $client; # a SIGCHLD was raised

  # check to see if it's okay to handle this request
  if($num_children >= $maxchild)
  {
    print $client "busy\015\012";
    $client->close;
    undef $client;
    next ACCEPT;
  }
  my $childpid;

  # fork; let the child handle the actual request
  if($childpid = fork)
  {

    # This is the parent
    $num_children++;

    # go wait for next request
    undef $client;
    next ACCEPT;
  }

  # This is the child process
  defined $childpid or die(&timestamp("Could not fork: $! \n"));

  # here we're the child, so we actually handle the request
  my @requests;
  while(my $request = <$client>)
  {
    last if $request eq "\015\012";
    push @requests, $request;
  }
  foreach my $i (0..$#requests)
  {
    my $request = $requests[$i];
    my $rnum = $i + 1;
    my $type = 'UNDEFINED';
    if($request =~ m/^(\w)\b/)
    {
      $type = $1;
    }
    else
    {
      $type = 'UNDEFINED';
    }
    if($type eq 'v')
    {
      eval{
        # get version information
        my $qdver = $wn->VERSION();
        my $wnver = $wntools->hashCode();
        my $simver = $WordNet::Similarity::VERSION;
        print $client "v WordNet $wnver\015\012";
        print $client "v WordNet::QueryData $qdver\015\012";
        print $client "v WordNet::Similarity $simver\015\012";
      };
      print(STDERR &timestamp("$@\n")) if($@);
    }
    elsif($type eq 's')
    {
      my (undef, $word) = split /\s+/, $request;
      if(!defined($word) || $word eq "")
      {
        print $client "! Usage: s WORD\015\012";
        goto EXIT_CHILD;
      }
      my @senses = getAllForms($word);
      unless(scalar @senses)
      {
        print $client "! $word was not found in WordNet\015\012";
        goto EXIT_CHILD;
      }
      getlock;
      foreach my $wps (@senses)
      {
        eval{
          my @synset = $wn->querySense($wps, "syns");
          print $client "$rnum $wps ", join(" ", @synset), "\015\012";
        };
        print(STDERR &timestamp("$@\n")) if($@);
      }
      releaselock;
    }
    elsif($type eq 'g')
    {
      my $word = undef;
      (undef, $word) = split /\s+/, $request;
      if(!defined($word) || $word eq "")
      {
        print $client "! Usage: g WORD\015\012";
        goto EXIT_CHILD;
      }
      my @senses = getAllForms($word);
      unless(scalar @senses)
      {
        print $client "! $word was not found in WordNet\015\012";
        goto EXIT_CHILD;
      }
      getlock;
      foreach my $wps (@senses)
      {
        eval{
          my ($gloss) = $wn->querySense($wps, "glos");
          print $client "$rnum $wps ${gloss}\015\012";
        };
        print(STDERR &timestamp("$@\n")) if($@);
      }
      releaselock;
    }
    elsif($type eq 'r')
    {
      my (undef, $word1, $word2, $measure, $trace, $gloss, $syns, $root)= split /\s+/, $request;
      unless(defined $word1 and defined $word2)
      {
        print $client "! Error: undefined input words\015\012";
        sleep 2;
        goto EXIT_CHILD;
      }
      my $module;
      if($measure =~ /^(?:hso|jcn|lch|lesk|lin|path|random|res|wup|vector|vector_pairs)$/)
      {
        no strict 'refs';
        $module = $$measure;
        unless(defined $module)
        {
          print $client "! Error: Couldn't get reference to measure\015\012";
          sleep 2;
          goto EXIT_CHILD;
        }
      }
      else
      {
        print $client "! Error: no such measure $measure\015\012";
        sleep 2;
        goto EXIT_CHILD;
      }
      my @wps1 = getAllForms($word1);
      unless(scalar @wps1)
      {
        print $client "! $word1 was not found in WordNet\015\012";
        goto EXIT_CHILD;
      }
      my @wps2 = getAllForms($word2);
      unless(scalar @wps2)
      {
        print $client "! $word2 was not found in WordNet\015\012";
        goto EXIT_CHILD;
      }
      if(defined($trace) and $trace eq 'yes')
      {
        $module->{trace} = 1;
      }
      $module->{rootNode} = (defined($root) and $root eq 'yes') ? 1 : 0;
      if((defined($gloss) and $gloss eq 'yes') or (defined($syns) and $syns eq 'yes'))
      {
        getlock;
        foreach my $wps ((@wps1, @wps2))
        {
          if(defined($gloss) and $gloss eq 'yes')
          {
            eval{
              my ($gls) = $wn->querySense($wps, 'glos');
              print $client "g $wps $gls\015\012";
            };
            print(STDERR &timestamp("$@\n")) if($@);
          }
          if(defined($syns) and $syns eq 'yes')
          {
            eval{
              my @syns = $wn->querySense($wps, 'syns');
              print $client "s ", join(" ", @syns), "\015\012";
            };
            print(STDERR &timestamp("$@\n")) if($@);
          }
        }
        releaselock;
      }
      getlock;
      foreach my $wps1 (@wps1)
      {
        foreach my $wps2 (@wps2)
        {
          eval{
            my $score = $module->getRelatedness($wps1, $wps2);
            my ($err, $errstr) = $module->getError();
            if($err)
            {
              print $client "! $errstr\015\012";
            }
            else
            {
              print $client "r $measure $wps1 $wps2 $score\015\012";
            }
            if(defined($trace) and $trace eq 'yes')
            {
              my $tracestr = $module->getTraceString();
              $tracestr =~ s/[\015\012]+/<CRLF>/g;
              print $client "t $tracestr\015\012";
            }
          };
          print(STDERR &timestamp("$@\n")) if($@);
        }
      }
      releaselock;

      # reset traces to off
      $module->{trace} = 0;
    }
    else
    {
      print $client "! Bad request type `$type'\015\012";
    }
  }

  # Terminate ALL messages with CRLF (\015\012).  Do NOT use
  # \r\n (the meaning of \r and \n varies on different platforms).
EXIT_CHILD:
  print $client "\015\012";
  $client->close;
  $socket->close;

  # don't let the child accept:
  exit;
}
$socket->close;
exit;

sub getAllForms ($)
{
  my $word = shift;

  # check if it's a type III string already:
  return $word if $word =~ m/[^#]+\#[nvar]\#\d+/;

  # it must be a type I or II, so let's get all valid forms
  getlock;
  my @forms = ();
  eval{@forms = $wn->validForms($word);};
  print(STDERR &timestamp("$@\n")) if($@);
  releaselock;
  return () unless scalar @forms;
  my @wps_strings;

  # for each valid form, get all valid wps strings
  foreach my $form (@forms)
  {
    # form is a type II string
    getlock;
    my @strings = ();
    eval{@strings = $wn->querySense($form);};
    print(STDERR &timestamp("$@\n")) if($@);
    releaselock;
    next unless scalar @strings;
    push @wps_strings, @strings;
  }
  return @wps_strings;
}

# A signal handler, good for most normal signals (esp. INT).  Mostly we just
# want to close the socket we're listening to and delete the lock file.
sub bailout
{
  my $sig = shift;
  $sig = defined $sig ? $sig : "?UNKNOWN?";
  $socket->close if defined $socket;
  print(STDERR &timestamp("Bailing out (SIG$sig).\n"));
  releaselock if($lockfh);
  exit 1;
}
use Fcntl qw/:flock/;

# gets a lock on $lockfh.  The return value is that of flock.
sub getlock ()
{
  open($lockfh, '>>', $lock_file) or die(&timestamp("Cannot open lock file $lock_file: $! \n"));
  eval{flock $lockfh, LOCK_EX;};
  print(STDERR &timestamp("$@\n")) if($@);
}

# releases a lock on $lockfh.  The return value is that of flock.
sub releaselock ()
{
  eval{
    flock $lockfh, LOCK_UN;
    close $lockfh;
  };
  print(STDERR &timestamp("$@\n")) if($@);
}

# attach a time stamp
sub timestamp ($)
{
  my $instring = shift;
  return $instring if(!defined($instring));
  my @monthNames = qw(Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec);
  my ($second, $minute, $hour, $dayOfMonth, $month, $yearOffset, $dayOfWeek, $dayOfYear) = localtime();
  my $year = 1900 + $yearOffset;
  return "["."$dayOfMonth/$monthNames[$month]/$year:$hour:$minute:$second"."] $instring";
}

# sub to reap child processes (so they don't become zombies)
# also updates the num_children variable
#
# Sub was loosely inspired by an example at
# http://www.india-seo.com/perl/cookbook/ch16_20.htm
sub reaper
{
  my $moribund;
  if(my $pid = waitpid(-1, WNOHANG) > 0)
  {
    $num_children-- if WIFEXITED($?);
  }
  $interrupted = 1;
  $SIG{CHLD} = \&reaper; # cursed be SysV
}

__END__

=head1 NAME

similarity_server.pl - [Web] The backend WordNet::Similarity server for the Web Interface

=head1 SYNOPSIS

Usage: similarity_server.pl [--port PORTNUMBER] [--logfile LOGFILE] [--maxchild NUM] [--stoplist STOPFILE]
                            | --help
                            | --version


=head1 DESCRIPTION

This script implements the backend of the web interface for
WordNet::Similarity.

This script listens to a port waiting for a request form similarity.cgi or
wps.cgi.  The client script sends a message to this script as series of
queries (see QUERY FORMAT).  After all the queries, the client sends a
message containing only CRLF (carriage-return line-feed, or \015\012).

The server (this script) responds with the results (see MESSAGE FORMAT)
terminated by a message containing only CRLF.

=head2 Example:

 Client:
 g car#n#1CRLF
 CRLF

 Sever responds:
 g car#n#1 4-wheeled motor vehicle; usually propelled by an internal
 combustion engine; "he needs a car to get to work"CRLF
 CRLF

=head1 OPTIONS

B<--port>=I<PORTNUMBER>
    Specify the port PORTNUMBER for the server to listen on.

B<--logfile>=I<LOGFILE>
    The output LOGFILE where any error or warning messages should be
    written out.

B<--maxchild>=I<NUM>
    Specify the maximum number NUM of the processes that should be forked
    to handle the requests.

B<--stoplist>=I<STOPFILE>
    A file STOPFILE of stop words to be provided to the lesk and vector
    modules.

B<--help>
    Display the help message and quit.

B<--version>
    Display the version information and quit.

=head1 QUERY FORMAT

<CRLF> means carriage-return line-feed "\r\n" on Unix, "\n\r" on Macs,
\015\012 everywhere and anywhere (i.e., don't use \n or \r, use \015\012).

The queries consist of messages in the following formats:

 s <word1> <word2><CRLF> - server will return all senses of word1 and
 word2

 g <word><CRLF> - server will return the gloss for each synset to which
 word belongs

 r <wps1> <wps2> <measure> <etc...><CRLF> - server will return the
 relatedness of wps1 and wps2 using measure.

 v <CRLF> - get version information

=head1 MESSAGE FORMAT

The messages sent from this server will be in the following formats:

 ! <msg><CRLF> - indicates an error or warning

 g <wps> <gloss><CRLF> - the gloss of wps

 r <wps1> <wps2> <score><CRFL> - the relatedness score of wps1 and wps2

 t <msg><CRLF> - the trace output for the previous relatedness score

 s <wps1> <wps2> ... <wpsN><CRLF> - a synset

 v <package> <version number><CRLF> - the version of 'package' being used

=head1 BUGS

Report to WordNet::Similarity mailing list :
 L<http://groups.yahoo.com/group/wn-similarity>

=head1 SEE ALSO

L<WordNet::Similarity>

WordNet home page : 
 L<http://wordnet.princeton.edu>

WordNet::Similarity home page (provides link to web interface):
 L<http://wn-similarity.sourceforge.net>

=head1 AUTHORS

 Ted Pedersen, University of Minnesota, Duluth
 tpederse at d.umn.edu

 Siddharth Patwardhan, University of Utah
 sidd at cs.utah.edu

 Jason Michelizzi

=head1 COPYRIGHT

Copyright (c) 2005-2008, Ted Pedersen, Siddharth Patwardhan 
and Jason Michelizzi

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to: 

    The Free Software Foundation, Inc., 
    59 Temple Place - Suite 330, 
    Boston, MA  02111-1307, USA.

=cut
