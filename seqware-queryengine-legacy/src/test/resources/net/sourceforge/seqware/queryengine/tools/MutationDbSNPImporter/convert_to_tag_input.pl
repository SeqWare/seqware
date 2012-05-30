#!/usr/bin/perl

# the file input format is:
# mismatch_id \t tag \t value \n

use strict;

my ($not_dbSNP, $is_dbSNP) = @ARGV;

open NOT, "<$not_dbSNP" or die;
open IS, "<$is_dbSNP" or die;

while(<NOT>) {
  next if (/^#/);
  my @t = split /\s+/;
  $t[3] =~ /mismatch_id=(\d+)/;
  my $id = $1;
  print "$id\tnot_dbSNP\t\n";
}
close NOT;

while(<IS>) {
  next if (/^#/);
  my @t = split /\s+/;
  $t[1] =~ /mismatch_id=(\d+)/;
  my $id = $1;
  print "$id\tis_dbSNP\t$t[2]\n";
}
close IS;

