use strict;

# Author:      briandoconnor@gmail.com
# Description: This is a simple script that converts a tag file to use the HBase mismatch_id.
#              This is not really a useful script for anything other than some hacking I'm doing
#              to load the U87 dataset annotations into the HBase backend.

while(<STDIN>) {
  chomp;
  my @t = split /\t/;
  $t[0] =~ /(\S+):(\d+)-(\d+)/;
  my $contig = $1;
  my $start = pad($2);
  my $type;
  my $nuc;
  my $key = $t[2];
  my $value = $t[3];
  if ($t[1] =~ /INS:\-+>([ATGC]+)/) {
    $nuc = $1;
  } elsif ($t[1] =~ /DEL:([ATGC]+)-/) {
    $nuc = $1;
  } elsif ($t[1] =~ /([ATGC]+)->([ATGC]+)/) {
    $nuc = $2;
  }

  print "hg18.$contig.$start.variant.GenomeU87.$nuc\t$key";
  if (defined($value) && $value ne "") { print "\t$value"; }
  print "\n";
}

sub pad {
  my ($str) = shift;
  my $zeros = 15 - length($str);
  my $padded = "";
  for (my $i=0; $i<$zeros; $i++) {
    $padded .= "0";
  }
  return($padded.$str);
}
