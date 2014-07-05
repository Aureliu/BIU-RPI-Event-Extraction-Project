#! /usr/bin/perl -w

# Before 'make install' is run this script should be runnable with
# 'make test'.  After 'make install' it should work as 'perl t/pod.t'

use Test::More;
eval "use Test::Pod 1.00";
plan skip_all => "Test::Pod 1.00 required for testing POD" if $@;
all_pod_files_ok();
