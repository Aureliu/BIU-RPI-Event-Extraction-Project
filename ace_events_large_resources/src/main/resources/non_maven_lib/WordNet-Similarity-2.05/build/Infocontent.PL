# This script generates default infocontent files for WordNet::Similarity
# (Last updated $Id: Infocontent.PL,v 1.2 2008/03/07 22:06:49 sidz1979 Exp $)
#
# ----------------------------------------------------------------------------

use File::Spec;

# Set up @ARGV
print STDERR "Generating default information content files.\n";
my $fname = shift;
die "Undefined information content file specified.\n" if(!defined($fname));
@ARGV = ("--outfile=$fname"); 
if(open(WNPATH, "build/wnpath.txt"))
{
  my $wndictpath = <WNPATH>;
  $wndictpath =~ s/[\r\f\n]+//g;
  $wndictpath =~ s/^\s+//;
  $wndictpath =~ s/\s+$//;
  $wndictpath = File::Spec->catfile($wndictpath, 'dict');
  push(@ARGV, "--wnpath=$wndictpath");
  close(WNPATH);
}

# Call the semCorFreq command
require "utils/semCorFreq.pl";

# Check if the file was generated
if(-z $fname) 
{
  print STDERR "Error: Failed to create default infocontent file.\n";
  exit(1);
}
