# WordNet::Similarity.pm version 2.05
# (Last updated $Id: Similarity.pm,v 1.50 2008/05/30 10:16:38 sidz1979 Exp $)
#
# Module containing the version information and pod
# for the WordNet::Similarity package, and all measures are
# derived from this class.
#
# Copyright (c) 2005,
#
# Ted Pedersen, University of Minnesota Duluth
# tpederse at d.umn.edu
#
# Siddharth Patwardhan, University of Utah, Salt Lake City
# sidd at cs.utah.edu
#
# Jason Michelizzi, Univeristy of Minnesota Duluth
# mich0212 at d.umn.edu
#
# Satanjeev Banerjee, Carnegie Mellon University, Pittsburgh
# banerjee+ at cs.cmu.edu
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to 
#
# The Free Software Foundation, Inc., 
# 59 Temple Place - Suite 330, 
# Boston, MA  02111-1307, USA.
#
# ------------------------------------------------------------------

package WordNet::Similarity;

=head1 NAME

WordNet::Similarity - Perl modules for computing measures of semantic
relatedness.

=head1 SYNOPSIS

=head2 Basic Usage Example

  use WordNet::QueryData;

  use WordNet::Similarity::path;

  my $wn = WordNet::QueryData->new;

  my $measure = WordNet::Similarity::path->new ($wn);

  my $value = $measure->getRelatedness("car#n#1", "bus#n#2");

  my ($error, $errorString) = $measure->getError();

  die $errorString if $error;

  print "car (sense 1) <-> bus (sense 2) = $value\n";

=head2 Using a configuration file to initialize the measure

  use WordNet::Similarity::path;

  my $sim = WordNet::Similarity::path->new($wn, "mypath.cfg");

  my $value = $sim->getRelatedness("dog#n#1", "cat#n#1");

  ($error, $errorString) = $sim->getError();

  die $errorString if $error;

  print "dog (sense 1) <-> cat (sense 1) = $value\n";

=head2 Printing traces

  print "Trace String -> ".($sim->getTraceString())."\n";

=head1 DESCRIPTION

=head2 Introduction

We observe that humans find it extremely easy to say if two words are
related and if one word is more related to a given word than another. For
example, if we come across two words, 'car' and 'bicycle', we know they
are related as both are means of transport. Also, we easily observe that
'bicycle' is more related to 'car' than 'fork' is. But is there some way to
assign a quantitative value to this relatedness? Some ideas have been put
forth by researchers to quantify the concept of relatedness of words, with
encouraging results.

Eight of these different measures of relatedness have been implemented in
this software package. A simple edge counting measure and a random measure
have also been provided. These measures rely heavily on the vast store of
knowledge available in the online electronic dictionary -- WordNet. So, we
use a Perl interface for WordNet called WordNet::QueryData to make it
easier for us to access WordNet. The modules in this package REQUIRE that
the WordNet::QueryData module be installed on the system before these
modules are installed.

=head2 Function

The following function is defined:

=over

=cut

use strict;
use Carp;
use Exporter;

# please use these, but remember that constants are not interpolated:
#  print "Root: ROOT\n";         # wrong!
#  print "Root: ".ROOT."\n";     # right
#  m/ROOT/;                      # wrong!
#  $pattern = ROOT; m/$pattern/; # okay
use constant ROOT => "*Root*";
use constant ROOT_N => "*Root*#n#1";
use constant ROOT_V => "*Root*#v#1";

# JM 12/9/03
# we would like this to be numeric
use constant UNRELATED => -1_000_000;

# if we are using an unlimited cache size, it's easier to fake an
# unlimited cache with a really big value.
use constant UNLIMITED_CACHE => 2_147_483_647;

use constant DEFAULT_CACHE => 5_000;

use WordNet::Tools;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS);

@ISA = qw(Exporter);

%EXPORT_TAGS = ();

@EXPORT_OK = ();

@EXPORT = ();

$VERSION = '2.05';

# a hash to contain the module-specific configuration options.
our %config_options;

=item addConfigOption ($name, $required, $type, $default_val)

Adds the configuration option, $name, to the list of known config
options (cf. configure()).  If $required is true, then the option
requires a value; otherwise, the value is optional, and the default
value $default_val is used if a value is not specified in the config
file.  $type is the type of value the option takes.  It can be
'i' for integer, 'f' for floating-point, 's' for string, or 'p' for
a file name.

returns: nothing, but will C<die> on error.  You can put the call to
this function in an C<eval> block to trap the exception (N.B., the
C<eval BLOCK> form of C<eval> does not significantly degrade performance,
unlike the C<eval EXPR> form of C<eval>.  See C<perldoc -f eval>).

=cut

sub addConfigOption
{
  my $name = shift;
  my $required = shift;
  my $type = shift;
  my $default = shift;

  my ($package, $filename, $line) = caller;
  if ($package eq 'vector') {
    print "vector\n"
  }

  $config_options{$name}->{$package} = [($required ? 1 : 0), $type, $default];
}

=back

=head2 Methods

The following methods are defined in this package:

=head3 Public methods

=over

=item $obj->new ($wn, $config_file)

The constructor for WordNet::Similarity::* objects.

Parameters: $wn is a WordNet::QueryData object, $config_file is a
configuration file (optional).

Return value: the new blessed object

=cut

sub new
{
  my $class = shift;
  my $this = {};

  $class = ref $class || $class;

  $this->{errorString} = '';
  $this->{error} = 0;

  if ($class eq 'WordNet::Similarity') {
    $this->{errorString} .= "\nWarning (${class}::new()) - ";
    $this->{errorString} .= "This class is intended to be an abstract base class for a measure.  Your class should override it.";
    $this->{error} = 1;
  }

  $this->{wn} = shift;
  unless (defined $this->{wn}) {
    $this->{errorString} .= "\nError (${class}::new()) - ";
    $this->{errorString} .= "A WordNet::QueryData object is required.";
    $this->{error} = 2;
  }
  else {
    # queryWord() in older versions of WordNet::QueryData was broken
    $this->{wn}->VERSION (1.30);
    my $wntools = WordNet::Tools->new($this->{wn});
    unless (defined $wntools) {
      $this->{errorString} .= "\nError (${class}::new()) - ";
      $this->{errorString} .= "Error creating WordNet::Tools object.";
      $this->{error} = 2;
    }
    $this->{wntools} = $wntools;
  }

  bless $this, $class;

  $this->initialize (shift) if $this->{error} < 2;

  $this->setPosList();

  # [trace]
  if ($this->{trace}) {
    $this->{traceString} = "${class} object created:\n";
    $this->{traceString} .= "trace :: $this->{trace}\n";
    $this->{traceString} .= "cache :: $this->{doCache}\n";
    $this->{traceString} .= "cache size :: $this->{maxCacheSize}\n";
    $this->traceOptions ();
  }
  # [/trace]

  return $this;
}


=item $obj->initialize ($config_file)

Performs some initialization on the module.

Parameter: the location of a configuration file

Returns: nothing

=cut

sub initialize
{
  my $self = shift;

  # initialize cache--caching is ON by default
  $self->{doCache} = 1;
  $self->{simCache} = ();
  $self->{traceCache} = ();
  $self->{cacheQ} = ();

  # (JM - 11/26/03)
  # Using unlimited cache can cause simCache and esp. traceCache
  # to use huge amounts of memory if a lot of queries are performed.
  # $self->{maxCacheSize} = UNLIMITED_CACHE;

  $self->{maxCacheSize} = DEFAULT_CACHE;

  # initialize tracing--tracing is OFF by default
  $self->{trace} = 0;

  # JM 1/26/04
  # moved option for root node to PathFinder.pm
  #
  # use a virtual root node (if applicable)
  #  six of the measures (res, lin, jcn, path, wup, lch) use a virtual
  #  root node in some way, and it is present by default in these cases.
  #  Three of the measures--path, wup, and lch--allow this root node to be
  #  turned off (i.e., the measure would be run without a root node).
  # $self->{rootNode} = 1;

  return $self->configure (@_);
}


=item $obj->configure($config_file)

Parses a configuration file.

If you write a module and want to add a new configuration option, you
can use the addConfigOption function to specify the name and nature
of the option.

The value of the option is place in "self": $self->{optionname}.

parameter: a file name

returns: true if parsing of config file was successful, false on error

=cut

sub configure
{
  my $self = shift;
  my $file = shift;
  my $class = ref $self || $self;

  while (my ($opt, $classHash) = each %config_options) {
      while (my ($class, $arrayRef) = each %$classHash) {
	  if ($self->isa ($class)) {
	      $self->{$opt} = $arrayRef->[2];
	  }
      }
  }
  return unless defined $file;

#  my %options = %config_options;
#  foreach my $optionstr (@options) {
#    next unless $optionstr;
#    my $patternstr = '^(\w+)([=:])([ifps])$';
#    unless ($optionstr =~ m/$patternstr/o) {
#      $self->{errorString} .= "\nWarning (${class}::configure) - ";
#      $self->{errorString} .= "Bad option string $optionstr: option strings";
#      $self->{errorString} .= " must match the pattern ${patternstr}.";
#      $self->{error} = ($self->{error} > 1) ? $self->{error} : 1;
#    }
#    $options{$1} = [$2, $3];
#  }

  unless (open CF, $file) {
    my $class = ref $self || $self;
    $self->{errorString} .= "\nError (${class}::configure) - ";
    $self->{errorString} .= "Unable to open config file $file.";
    $self->{error} = 2;
    return undef;
  }

  $_ = <CF>;
  unless (m/^$class/) {
    close CF;
    my $class = ref $self || $self;
    $self->{errorString} .= "\nError (${class}::configure()) - ";
    $self->{errorString} .= "$file does not appear to be a config file for $class.";
    $self->{error} = 2;
    return undef;
  }

  # keep track of which options we've already seen
  my %optionCache;

  my %rtn;
 OPTION_READ:
  while (<CF>) {
    s/\s+|\#.*//g; # ignore comments

    # JM 12/4/03 (#3)
    # edited the below to
    #  (1) ensure the values for options are valid
    #  (2) handle options without values in a consistent manner

    if ($_ eq "") {
      next;
    }

    # JM 1/9/04 (#1)
    # added the following block to check for repeated options
    my ($option, $value) = m/^(\w+)::(.*)$/;
    if ($option) {
      unless (defined $optionCache{$option}) {
	$optionCache{$option} = defined $value ? $value : 1;
      }
      else {
	# we've already seen this option
	$self->{errorString} .= "\nWarning (${class}::configure()) - ";
	$self->{errorString} .= "configuration option '$option' encountered twice in config file";
	$self->{error} = ($self->{error} > 1) ? $self->{error} : 1;
      }
    }

    if (m/^trace::(.*)/i) {
      my $trace = $1;

      # JM 12/4/03 (#3)
      # $self->{trace} = 1;
      # $self->{trace} = $trace if $trace =~ m/^[012]$/;

      next OPTION_READ if $trace eq "";
      if ($trace =~ m/^[012]$/) {
	$self->{trace} = $trace;
      }
      else {
	$self->{errorString} .= "\nWarning (${class}::configure()) - ";
	$self->{errorString} .= "$trace is an invalid value for option trace.";
	$self->{error} = ($self->{error} > 1) ? $self->{error} : 1;
      }
    }
    elsif (m/^cache::(.*)/i) {
      my $cache = $1;

      # JM 12/4/03 (#3)
      # $self->{doCache} = 1;
      # $self->{doCache} = $cache if $cache =~ m/^[01]$/;

      next OPTION_READ if $cache eq "";
      if ($cache =~ m/^[01]$/) {
	$self->{doCache} = $cache;
      }
      else {
	$self->{errorString} .= "\nWarning (${class}::configure()) - ";
	$self->{errorString} .= "$cache is an invalid value for option cache.";
	$self->{error} = ($self->{error} > 1) ? $self->{error} : 1;
      }
    }
    elsif (m/^maxCacheSize::(.*)/i) {
      my $mcs = $1;

      # JM 12/4/03 (#3)
      # $self->{maxCacheSize} = DEFAULT_CACHE;
      # if ($mcs =~ /unlimited/i) {
      #   $self->{maxCacheSize} = UNLIMITED_CACHE;
      #   next;
      # }	
      # $self->{maxCacheSize} = $mcs if defined ($mcs) and $mcs =~ m/^\d+$/;

      next OPTION_READ if $mcs eq "";
      if ($mcs =~ m/^unlimited/i) {
	$self->{maxCacheSize} = UNLIMITED_CACHE;
      }
      elsif ($mcs =~ m/^\d+$/) {
	$self->{maxCacheSize} = $mcs + 0;
      }
      else {
	$self->{errorString} .= "\nWarning (${class}::configure()) - ";
	$self->{errorString} .= "$mcs is an invalid value for option maxCacheSize.";
	$self->{error} = ($self->{error} > 1) ? $self->{error} : 1;	
      }
    }
    # JM 1/26/04
    # moved code for the rootNode option to PathFinder.pm
    else {
OPTION_LOOP:
      foreach my $option (keys %config_options) {
	  my $found = 0;
CLASS_LOOP:
	  foreach my $class (keys %{$config_options{$option}}) {
	      if ($self->isa ($class)
		  and defined $config_options{$option}->{$class}) {
		  $found = $class;
		  last CLASS_LOOP;
	      }
	  }
	  next OPTION_LOOP unless $found;
	
	  if (not defined $config_options{$option}->{$found}) {
	      print STDERR "$option $class\n";
	  }
	  my ($required, $type, $dflt)= @{$config_options{$option}->{$found}};


	if (m/^${option}::(.*)$/i) {
	  my $t = $1;
	  if ($t =~ m/^\s*$/) {
	    if ($required) {
	      #error
	      $self->{errorString} .= "\nWarning (${class}::configure()) - ";
	      $self->{errorString} .= "Option $option has no value.";
	      $self->{error} .= $self->{error} > 1 ? $self->{error} : 1;
	    }
	    else {
	      # do nothing
	      $self->{$option} = $dflt
	    }
	  }
	  else {
	    if ($type eq 'i') {
	      # JM 12/4/03 (#3)
	      # $self->{$option} = int ($t);
	      if ($t =~ m/^\d+$/) {
		$self->{$option} = $t + 0;
	      }
	      else {
		$self->{errorString} .= "\nWarning (${class}::configure()) - ";
		$self->{errorString} .=
		  "$t is an invalid value for option $option.";
		$self->{error} = ($self->{error} > 1) ? $self->{error} : 1;
	      }
	    }
	    elsif ($type eq 'f') {
	      # JM 12/4/03 (#3)

	      # check if this is a float
	      if ($t =~ /^[+-]?(?:\d+\.?\d*|\.\d+)(?:e[+-]?\d+)?$/) {
		$self->{$option} = $t + 0.0;
	      }
	      else {
		$self->{errorString} .= "\nWarning (${class}::configure()) - ";
		$self->{errorString} .=
		  "$t is an invalid value for option $option.";
		$self->{error} = ($self->{error} > 1) ? $self->{error} : 1;
	      }
	    }
	    elsif ($type eq 'p') {
	      if (-e $t) {
		$self->{$option} = $t;
	      }
	      else {
		$self->{errorString} .= "\nWarning (${class}::configure()) - ";
		$self->{errorString} .=
		  "$t is not a valid filename for option $option.";
		$self->{error} = ($self->{error} > 1) ? $self->{error} : 1;
	      }
	    }
	    elsif ($type eq 's') {
	      $self->{$option} = $t;
	    }
	    else {
	      $self->{errorString} .= "\nWarning (${class}::configure()) - ";
	      $self->{errorString} .=
		"Unknown/invalid option type $type.\n";
	    }
	  }
	  next OPTION_READ;
	}
      }
      # error
      s/::.*//;
      my $class = ref $self || $self;
      $self->{errorString} .="\nWarning (${class}::configure()) - ";
      $self->{errorString} .= "Ignoring unrecognized option '$_'.";
      $self->{error} = $self->{error} > 1 ? $self->{error} : 1;
    }
  }
  close CF;
  return 1;
}

=item $obj->getTraceString(Z<>)

Returns the current trace string and resets the trace string to empty.  If
tracing is turned off, then an empty string will always be returned.

=cut

sub getTraceString {
  my $self = shift;

  return '' unless $self->{trace} and defined $self->{traceString};
  my $str = $self->{traceString};
  $self->{traceString} = '';
  $str =~ s/\n{2,}$//;
  return $str;
}

=item $obj->getError(Z<>)

Checks to see if any errors have occurred.
Returns a list of the form S<($level, $string)>.  If $level is 0, then
no errors have occurred; if $level is non-zero, then an error has occurred.
A value of 1 is considered a warning, and a value of 2 is considered an
error.  If $level is non-zero, then $string will have a (hopefully)
meaningful error message.

=cut

sub getError {
  my $self = shift;
  my $error = $self->{error};
  my $errorString = $self->{errorString};
  $self->{error} = 0;
  $self->{errorString} = "";
  $errorString =~ s/^[\r\n\t ]+//;
  return ($error, $errorString);
}


=item $obj->traceOptions(Z<>)

Prints module-specific options to the trace string.  Any module that
adds configuration options via addConfigOption should override this
method.

Options should be printed out using the following format:

  $self->{traceString} .= "option_name :: $option_value\n"

Note that the option name is separated from its current value by a
space, two colons, and another space.  The string should be terminated
by a newline.

Since multiple modules may be overriding this method, any module
that overrides this method should insure that the superclass'
method gets called as well.  You do this by putting this line at
the end of your method:

  $self->SUPER::traceOptions();

returns: nothing

=cut

# JM 12/5/03 (#1)
sub traceOptions {
  # nothing to do here, this is a just a placeholder
  # subclasses should override this to print all config options to
  # the traceString
}


=item $obj->parseWps($synset1, $synset2)

parameters: synset1, synset2

returns: a reference to an array [$word1, $pos1, $sense1, $offset1, $word2,
$pos2, $sense2, $offset2] or undef

This method checks the format of the two input synsets by calling
validateSynset() for each synset.

If the synsets are in wps format, a reference to an array will be returned.
This array has the form [$word1, $pos1, $sense1, $offset1, $word2, $pos2,
$sense2, $offset2] where $word1 is the word part of $wps1, $pos1, is the
part of speech of $wps1, $sense1 is the sense from $wps.  $offset1 is the
offset for $wps1.

If an error occurs (such as a synset being poorly-formed), then undef
is returned, the error level is set to non-zero, and an error message is
appended to the error string.

=cut

sub parseWps
{
  my $self = shift;
  my $wps1 = shift;
  my $wps2 = shift;

  my $class = ref $self || $self;

  # Undefined input cannot go unpunished.
  unless (defined $wps1 and defined $wps2 and length $wps1 and length $wps2) {
    $self->{errorString} .= "\nWarning (${class}::parseWps()) - ";
    $self->{errorString} .= "Variable for input synset ".(length($wps1) ? 2 : 1)." undefined.";
    $self->{error} = ($self->{error} < 1) ? 1 : $self->{error};
    return undef;
  }

  my ($word1, $pos1, $sense1, $offset1) = $self->validateSynset ($wps1);
  my ($word2, $pos2, $sense2, $offset2) = $self->validateSynset ($wps2);

  # Check to see if validation of synsets succeeded, if not, then
  # bail out (error message already set by validateSynset).
  unless (defined $word1 and defined $pos1 and defined $sense1
	  and defined $word2 and defined $pos2 and defined $sense2) {
    return undef;
  }

  return [$word1, $pos1, $sense1, $offset1, $word2, $pos2, $sense2, $offset2];
}


=item $obj->validateSynset($synset)

parameter: synset

returns: a list or undef on error

synset is a string in word#pos#sense format

This method does the following:

=over

=item 1.

Verifies that the synset is well-formed (i.e., that it consists of three
parts separated by #s, the pos is one of {n, v, a, r} and that sense
is a natural number).  A synset that matches the pattern '[^\#]+\#[nvar]\#\d+'
is considered well-formed.

=item 2.

Checks if the synset exists by trying to find the offset for the synset

=back

If any of these tests fails, then the error level is set to non-zero, a
message is appended to the error string, and undef is returned.

If the synset is well-formed and exists, then a list is returned that
has the format ($word, $pos, $sense, $offset).

=cut

sub validateSynset
{
  my $self = shift;
  my $synset = shift;
  my $class = ref $self || $self;

  # check to see that synset is in w#p#s format

  unless (defined ($synset) and length ($synset)) {
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::validateSynset()) - ";
    $self->{errorString} .= "Variable representing synset is undefined (or an empty string).";
    return undef;
  }

  my ($word, $pos, $sense) = split (/\#/, $synset);

  unless (defined $word) {
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::validateSynset()) - ";
    $self->{errorString} .= "Invalid synset ($synset): word undefined.";
    return undef;
  }

  unless (defined $pos) {
    no strict 'vars';
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::validateSynset()) - ";
    $self->{errorString} .= "Invalid synset ($synset): part of speech undefined.";
    return undef;
  }

  unless (defined $sense) {
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::validateSynset()) - ";
    $self->{errorString} .= "Invalid synset ($synset): sense number undefined.";
    return undef;
  }

  # check to make sure the word, pos, and sense are well-formed
  if ($word !~ /^[^\#]+$/) {
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::validateSynset()) - ";
    $self->{errorString} .= "$synset has a poorly-formed word ($word).";
    return undef;
  }
  elsif ($pos !~ /^[nvar]$/) {
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::validateSynset()) - ";
    $self->{errorString} .= "$synset has a bad part of speech ($pos). Part of speech must be one of n, v, a, r.";
    return undef;
  }
  elsif ($sense !~ /^\d+$/) {
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::validateSynset()) - ";
    $self->{errorString} .= "$synset has a bad sense number ($pos). Sense number must be a natural number.";
    return undef;
  }

  # check to see if synset exists
  my $offset = $self->{wn}->offset ($synset);
  unless ($offset) {
    $self->{error} = $self->{error} < 1 ? 1 : $self->{error};
    $self->{errorString} .= "\nWarning (${class}::validateSynset()) - ";
    $self->{errorString} .= "$synset not found in WordNet.";
    return undef;
  }

  return ($word, $pos, $sense, $offset);
}

=item $obj->getRelatedness($synset1, $synset2)

parameters: synset1, synset2

returns: a relatedness score

This is a virtual method. It must be overridden by a module that
is derived from this class. This method takes two synsets and 
returns a numeric value as their score of relatedness.

=cut

sub getRelatedness {
  my $self = shift;
  my $class = ref $self || $self;
  $self->{errorString} .= "\nError (${class}::getRelatedness()) - ";
  $self->{errorString} .= "This is a virtual method provided by ";
  $self->{errorString} .= __PACKAGE__ . " that must be overridden.";
  $self->{error} = 2;
  return undef;
}

# Subroutine that takes as input an array of offsets
# or offsets(POS) and for each prints to traceString the
# WORD#POS#(<SENSE>/<OFFSET>)
# INPUT PARAMS  : $pos                             .. Part of speech
#               : ($offestpos1, $offsetpos2, ...)  .. Array of offsetPOS's
#                                                     or offests
# RETURN VALUES : none.
sub _printSet
{
  use Carp;
  carp "This method is deprecated; use printSet instead";
  my $self = shift;
  my $pos = shift;
  my $wn = $self->{wn};
  my @offsets = @_;
  my $wps;
  my $opstr = "";

  foreach my $offset (@offsets) {
    $offset =~ m/(\d+)([a-z])?/;
    $offset = $1;
    if($offset) {
      $wps = $wn->getSense($offset, ($2 ? $2 : $pos));
    }
    else {
      $wps = "*Root*\#". ($2 ? $2 : $pos) ."\#1";
    }
    $wps =~ s/ +/_/g;
    if($self->{trace} == 2 && defined $offset && $offset != 0) {
      $wps =~ s/\#[0-9]*$/\#$offset/;
    }
    $opstr .= "$wps ";
  }
  $opstr =~ s/\s+$//;
  $self->{traceString} .= $opstr if($self->{trace});
}


=item $obj->printSet ($pos, $mode, @synsets)

If tracing is turned on, prints the contents of @synsets to the trace string.
The contents of @synsets can be either wps strings or offsets.  If they
are wps strings, then $mode must be the string 'wps'; if they are offsets,
then the mode must be 'offset'.  Please don't try to mix wps and offsets.

Returns the string that was appended to the trace string.

=cut

sub printSet
{
  my $self = shift;
  my $pos = shift;
  my $mode = shift;
  my @synsets = @_;

  my $opstr = '';
  my $wn = $self->{wn};
  my $wps;

  if ($mode eq 'offset') {
    foreach my $offset (@synsets) {
      $offset =~ m/^(\d+)([a-z])?/;
      $offset = $1;
      if ($offset) {
	$wps = $wn->getSense ($offset, (defined $2 ? $2 : $pos));
      }
      else {
	$wps = "*Root*\#" . (defined $2 ? $2 : $pos) . "\#1";
      }
      $wps =~ tr/ /_/;
      if ($self->{trace} == 2 && defined $offset) {
	$wps =~ s/\#[0-9]+$/\#$offset/;
      }
      $opstr .= "$wps ";
    }
  }
  elsif ($mode eq 'wps') {
  WPS:
    foreach my $wps (@synsets) {
      unless ($self->{trace} == 2) {
	$opstr .= "$wps ";
	next WPS;
      }
      my $offset = scalar ($wps =~ /\*Root\*/i) ? 0 : $wn->offset ($wps);
      my ($word, $p) = $wps =~ /^(\S+)\#([nvar])\#\d+$/;
      $opstr .= "$word#$p#$offset ";
    }
  }
  $opstr =~ s/\s+$//;
  $self->{traceString} .= $opstr if $self->{trace};
  return $opstr;
}


# you should only call this if $self->{doCache} is true
# nothing bad will happen if you call anyways, but it will slow things down
#
# NEW!  You can specify whether or not relatedness is symmetric: if
# relatedness (c1, c2) = relatedness (c2, c1), then relatedness is symmetric.

=item $obj->fetchFromCache($wps1, $wps2, $non_symmetric)

Looks for the relatedness value of ($wps1, $wps2) in the cache.  If
$non_symmetric is false (or isn't specified), then the cache is searched
for ($wps2, $wps1) if ($wps1, $wps2) isn't found.

Returns: a relatedness value or undef if none found in the cache.

=cut

sub fetchFromCache
{
  my $self = shift;
  my ($wps1, $wps2, $non_symmetric) = @_;

  $self->{doCache} or return undef;

  $non_symmetric = 0 unless defined $non_symmetric;

  if (defined $self->{simCache}->{"${wps1}::$wps2"}) {
    if ($self->{traceCache}->{"${wps1}::$wps2"}) {
      $self->{traceString} .= $self->{traceCache}->{"${wps1}::$wps2"};
    }
    return $self->{simCache}->{"${wps1}::$wps2"};
  }
  elsif (!$non_symmetric and defined $self->{simCache}->{"${wps2}::$wps1"}) {
    if ($self->{traceCache}->{"${wps2}::$wps1"}) {
      $self->{traceString} .= $self->{traceCache}->{"${wps2}::$wps1"};
    }
    return $self->{simCache}->{"${wps2}::$wps1"};
  }
  return undef;
}

=item $obj->storeToCache ($wps1, $wps2, $score)

Stores the relatedness value, $score, of ($wps1, $wps2) to the cache.

Returns: nothing

=cut

sub storeToCache
{
  my $self = shift;
  my ($wps1, $wps2, $score) = @_;

  $self->{doCache} or return;

  $self->{simCache}->{"${wps1}::$wps2"} = $score;
  if ($self->{trace}) {
    $self->{traceCache}->{"${wps1}::$wps2"} = $self->{traceString}
  }
  push (@{$self->{cacheQ}}, "${wps1}::$wps2");
  if (($self->{maxCacheSize} >= 0)
      and ($self->{maxCacheSize} != UNLIMITED_CACHE)) {
    while (scalar (@{$self->{cacheQ}}) > $self->{maxCacheSize}) {
      my $delItem = shift(@{$self->{cacheQ}});
      delete $self->{simCache}->{$delItem};
      delete $self->{traceCache}->{$delItem};
    }
  }
}


1;

__END__

=back

=head2 Discussion

This package consists of Perl modules along with supporting Perl programs
that implement the semantic relatedness measures described by Leacock Chodorow
(1998), Jiang Conrath (1997), Resnik (1995), Lin (1998), Wu Palmer (1993),
Hirst St-Onge (1998) the Extended Gloss Overlaps measure by Banerjee and 
Pedersen (2002) and a Gloss Vector measure recently introduced by Patwardhan 
and Pedersen. The package contains Perl modules designed as object classes with
methods that take as input two word senses. The semantic distance between these
word senses is returned by these methods. A quantitative measure of the degree 
to which two word senses are related has wide ranging applications in 
numerous areas, such as word sense disambiguation, information retrieval,
etc. For example, in order to determine which sense of a given word is being 
used in a particular context, the sense having the highest relatedness with 
its context word senses is most likely to be the sense being used. Similarly,
in information retrieval, retrieving documents containing highly related
concepts are more likely to have higher precision and recall values.

A command line interface to these modules is also present in the
package. The simple, user-friendly interface simply returns the relatedness
measure of two given words. Number of switches and options have been
provided to modify the output and enhance it with trace information and
other useful output. Support programs for generating information
content files from various corpora are also available in the package. The
information content files are required by three of the measures for
computing the relatedness of concepts.  There is also a tool to find the
depths of the taxonomies in WordNet.

=head3 Configuration files

The behavior of the measures of semantic relatedness can be controlled by
using configuration files. These configuration files specify how certain
parameters are initialized within the object. A configuration file may be
specified as a parameter during the creation of an object using the new
method. The configuration files must follow a fixed format.

Every configuration file starts with the name of the module ON THE FIRST LINE
of the file. For example, a configuration file for the res module will have
on the first line 'WordNet::Similarity::res'. This is followed by the various
parameters, each on a new line and having the form 'name::value'. The
'value' of a parameter is optional (in case of boolean parameters). In case
'value' is omitted, we would have just 'name::' on that line. Comments are
supported in the configuration file. Anything following a '#' is ignored in
the configuration file.

Sample configuration files are present in the '/samples' subdirectory of
the package. Each of the modules has specific parameters that can be
set/reset using the configuration files. Please read the manpages or the
perldocs of the respective modules for details on the parameters specific
to each of the modules. For instance, 'man WordNet::Similarity::res' or
'perldoc WordNet::Similarity::res' should display the documentation for the
Resnik module.
The module parses the configuration file and recognizes the following 
parameters:

=over

=item trace

This option is supported by all measures.

The value of this parameter specifies the level of tracing that should
be employed for generating the traces. This value
is an integer equal to 0, 1, or 2. If the value is omitted, then the
default value, 0, is used. A value of 0 switches tracing off. A value
of 1 or 2 switches tracing on.  The difference between a value of 1 or 2
depends upon the measure being used.

For vector and lesk, a value of 1 displays as
traces only the gloss overlaps found. A value of 2 displays as traces all
the text being compared.

For the res, lin, jcn, wup, lch, path, and hso
measures, a trace of level 1 means the synsets are represented as
word#pos#sense strings, while for level 2, the synsets are represented as
word#pos#offset strings.

=item cache

This option is supported by all measures.

The value of this parameter specifies whether or not caching of the
relatedness values should be performed.  This value is an
integer equal to  0 or 1.  If the value is omitted, then the default
value, 1, is used. A value of 0 switches caching 'off', and
a value of 1 switches caching 'on'.

=item maxCacheSize

This option is supported by all measures.

The value of this parameter indicates the size of the cache, used for
storing the computed relatedness value. The specified value must be
a non-negative integer.  If the value is omitted, then the default
value, 5,000, is used. Setting maxCacheSize to zero has
the same effect as setting cache to zero, but setting cache to zero is
likely to be more efficient.  Caching and tracing at the same time can result
in excessive memory usage because the trace strings are also cached.  If
you intend to perform a large number of relatedness queries, then you
might want to turn tracing off.

=back

=head2 Usage

The semantic relatedness modules in this distribution are built as classes.
The classes define four methods that are useful in finding relatedness
values for pairs of synsets.

  new()
  getRelatedness()
  getError()
  getTraceString()

=head3 Typical Usage Examples

To create an object of the Resnik measure, we would have the following
lines of code in the Perl program.

   use WordNet::Similarity::res;
   $object = WordNet::Similarity::res->new($wn, '~/resnik.conf');

The reference of the initialized object is stored in the scalar variable
'$object'. '$wn' contains a WordNet::QueryData object that should have been
created earlier in the program. The second parameter to the 'new' method is
the path of the configuration file for the resnik measure. If the 'new'
method is unable to create the object, '$object' would be undefined. This, as
well as any other error/warning may be tested.

   die "Unable to create resnik object.\n" unless defined $object;
   ($err, $errString) = $object->getError();
   die $errString."\n" if($err);

To create a Leacock-Chodorow measure object, using default values, i.e. no
configuration file, we would have the following:

   use WordNet::Similarity::lch;
   $measure = WordNet::Similarity::lch->new($wn);

To find the semantic relatedness of the first sense of the noun 'car' and
the second sense of the noun 'bus' using the resnik measure, we would write
the following piece of code:

   $relatedness = $object->getRelatedness('car#n#1', 'bus#n#2');

To get traces for the above computation:

   print $object->getTraceString();

However, traces must be enabled using configuration files. By default
traces are turned off.

=head1 AUTHORS

  Ted Pedersen, University of Minnesota Duluth
  tpederse at d.umn.edu

  Siddharth Patwardhan, University of Utah, Salt Lake City
  sidd at cs.utah.edu

  Jason Michelizzi, Univeristy of Minnesota Duluth
  mich0212 at d.umn.edu

  Satanjeev Banerjee, Carnegie Mellon University, Pittsburgh
  banerjee+ at cs.cmu.edu

=head1 BUGS

None.

To submit a bug report, go to http://groups.yahoo.com/group/wn-similarity or
send e-mail to tpederse I<at> d.umn.edu.

=head1 SEE ALSO

perl(1), WordNet::Similarity::jcn(3), WordNet::Similarity::res(3),
WordNet::Similarity::lin(3), WordNet::Similarity::lch(3),
WordNet::Similarity::hso(3), WordNet::Similarity::lesk(3),
WordNet::Similarity::wup(3), WordNet::Similarity::path(3),
WordNet::Similarity::random(3), WordNet::Similarity::ICFinder(3),
WordNet::Similarity::PathFinder(3)
WordNet::QueryData(3)

http://www.cs.utah.edu/~sidd

http://wordnet.princeton.edu

http://www.ai.mit.edu/~jrennie/WordNet

http://groups.yahoo.com/group/wn-similarity


=head1 COPYRIGHT

Copyright (c) 2005, Ted Pedersen, Siddharth Patwardhan, Jason Michelizzi and Satanjeev Banerjee

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the Free
Software Foundation; either version 2 of the License, or (at your option)
any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to

    The Free Software Foundation, Inc.,
    59 Temple Place - Suite 330,
    Boston, MA  02111-1307, USA.

Note: a copy of the GNU General Public License is available on the web
at L<http://www.gnu.org/licenses/gpl.txt> and is included in this
distribution as GPL.txt.

=cut
