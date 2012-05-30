use strict;

# Author:      briandoconnor@gmail.com
# Description: This is a simple script that converts a tag file to use the HBase mismatch_id.
#              This is not really a useful script for anything other than some hacking I'm doing
#              to load the U87 dataset annotations into the HBase backend.

while(<STDIN>) {
  chomp;
  my @t = split /\t/;
  $t[4] =~ /mismatch_id=(\d+)/;

  $t[6] =~ /(\S+):(\d+)-(\d+)/;
  my $contig = $1;
  my $start = pad($2);
  my $type;
  my $nuc;
  if ($t[4] =~ /INS:\-+>([ATGC]+)/) {
    $nuc = $1;
  } elsif ($t[4] =~ /DEL:([ATGC]+)-/) {
    $nuc = $1;
  } elsif ($t[4] =~ /([ATGC]+)->([ATGC]+)/) {
    $nuc = $2;
  }
  $_ =~ s/mismatch_id=(\d+)/mismatch_id=hg18.$contig.$start.variant.GenomeU87.$nuc/g;
  #print "hg18.$contig.$start.variant.GenomeU87.$nuc\t$key";
  print "$_\n";
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
