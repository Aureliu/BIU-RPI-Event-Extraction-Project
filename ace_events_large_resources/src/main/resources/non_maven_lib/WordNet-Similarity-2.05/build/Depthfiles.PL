# This script generates default depth files for WordNet::Similarity
# (Last updated $Id: Depthfiles.PL,v 1.2 2008/03/07 22:06:49 sidz1979 Exp $)
#
# ----------------------------------------------------------------------------

use File::Spec;

# Set up @ARGV
print STDERR "Generating default depth files.\n";
my $synsetsfile = shift;
die "Undefined synset depths file.\n" if(!defined($synsetsfile));
my $taxonomyfile = $synsetsfile;
$taxonomyfile =~ s/[a-zA-Z_\.]+$/treedepths.dat/;
@ARGV = ("--outfile=$taxonomyfile", "--depthfile=$synsetsfile");
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

# Call the wnDepths command
require "utils/wnDepths.pl";

# Check if it worked
if(-z $synsetsfile or -z $taxonomyfile)
{
  print STDERR "Error: failed to create WordNet depths files.\n";
  exit(1);
}
