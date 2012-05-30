use strict;

system("rm -rf /storage/data1/bdb_u87_test/*");

system("mkdir -p /storage/data1/bdb_u87_test");
system("mkdir -p /storage/data1/bdb_u87_test_bed");

# tracking
my $start_time = time;
my $d = {};
$d->{start} = $start_time;

for (my $i=22; $i>=1; $i--) {
  print "\n\nTiming chr$i\n";

  if (-e "/storage/data1/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_single_indels_not_on_ends_20090804/bfast.U87.rmdup.filtered.indels.chr$i.complete.pileup.gz" && !-e "/storage/data1/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_single_indels_not_on_ends_20090804/bfast.U87.rmdup.filtered.indels.chr$i.complete.pileup") {
    system("zcat /storage/data1/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_single_indels_not_on_ends_20090804/bfast.U87.rmdup.filtered.indels.chr$i.complete.pileup.gz > /storage/data1/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_single_indels_not_on_ends_20090804/bfast.U87.rmdup.filtered.indels.chr$i.complete.pileup"); 
  }
  if (-e "/storage/data1/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/bfast.U87.mismatch.only.rmdup.sorted.chr$i.complete.pileup.gz" && !-e "/storage/data1/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/bfast.U87.mismatch.only.rmdup.sorted.chr$i.complete.pileup") {
    system("zcat /storage/data1/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/bfast.U87.mismatch.only.rmdup.sorted.chr$i.complete.pileup.gz > /storage/data1/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/bfast.U87.mismatch.only.rmdup.sorted.chr$i.complete.pileup"); 
  }

  # 4294967296 2500000
  my $import_start = time;
  print " +loading database...\n";
  system("time java -Djava.library.path=/home/brianoc/programs/BerkeleyDB.4.7/lib -cp \$CLASSPATH:dist/seqware-qe-0.6.2.jar:lib/db.jar net.sourceforge.seqware.queryengine.tools.importers.VariantImporter PileupImportWorker /storage/data1/bdb_u87_test true 1 100 10 false true false 33 4294967296 2500000 1 BerkeleyDB U87 hg18 /storage/data1/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_single_indels_not_on_ends_20090804/bfast.U87.rmdup.filtered.indels.chr$i.complete.pileup");
  system("time java -Djava.library.path=/home/brianoc/programs/BerkeleyDB.4.7/lib -cp \$CLASSPATH:dist/seqware-qe-0.6.2.jar:lib/db.jar net.sourceforge.seqware.queryengine.tools.importers.VariantImporter PileupImportWorker /storage/data1/bdb_u87_test true 1 100 10 false false true 33 4294967296 2500000 1 BerkeleyDB U87 hg18 /storage/data1/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/bfast.U87.mismatch.only.rmdup.sorted.chr$i.complete.pileup");
  my $import_stop = time;
  $d->{contigs}{'import'}{$i} = $import_stop - $import_start;

  print " +dumping database...\n";
  my $dump_start = time;
  # <db_dir> <output_prefix> <output_dir> <include_indels> <include_snv> <minCoverage> <maxCoverage> <minObservations> <minObservationsPerStrand> <minSNPPhred> <SNPPhredGreaterThanGenomePhred> <minPercent> <heterozygousRange> <homozygousRange> <cacheSize> <locks> <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <split_on_contig> <lookup_by_tags> <contig_str[s]_comma_sep> <tag_str[s]_comma_sep>
  system("time java -Djava.library.path=/home/brianoc/programs/BerkeleyDB.4.7/lib -cp \$CLASSPATH:dist/seqware-qe-0.6.2.jar:lib/db.jar net.sourceforge.seqware.queryengine.tools.exporters.BEDExporter /storage/data1/bdb_u87_test bdb_test /storage/data1/bdb_u87_test_bed true true 1 100 1 1 10 false 0 0-100 0-100 4294967296 2500000 BerkeleyDB U87 hg18 false false");
  my $dump_stop = time;
  $d->{contigs}{'dump'}{$i} = $dump_stop - $dump_start;

  print " +counting lines...\n";
  my $variant_count_str = `wc -l /storage/data1/bdb_u87_test_bed/bdb_test.all.bed`;
  $variant_count_str =~ /(\d+) /;
  my $variant_count = $1;
  $d->{contigs}{'variant_count'}{$i} = $variant_count;

  print " +database size...\n";
  system("du -sh /storage/data1/bdb_u87_test");

  print "\n";
  print "Start: ".$d->{start}."\n";
  print "Stop: ".$d->{stop}."\n";
  print "\t";
  print join "\t", keys %{$d->{contigs}};
  print "\n";
  for (my $i=22; $i>=1; $i--) {
    print "chr$i\t";
    foreach my $col (keys %{$d->{contigs}}) {
      print $d->{contigs}{$col}{$i}."\t";
    }
    print "\n";
  }
  print "\n";
}


#print Dumper $d;

print "\nFinal Numbers Indels+SNVs\n";
print "Start: ".$d->{start}."\n";
print "Stop: ".$d->{stop}."\n";
print "\t";
print join "\t", keys %{$d->{contigs}};
print "\n";
for (my $i=22; $i>=1; $i--) {
  print "chr$i\t";
  foreach my $col (keys %{$d->{contigs}}) {
    print $d->{contigs}{$col}{$i}."\t";
  }
  print "\n";
}
print "\n";


