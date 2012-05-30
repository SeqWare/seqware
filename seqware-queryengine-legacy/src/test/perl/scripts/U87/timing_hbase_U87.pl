use strict;
use Data::Dumper;

# FIXME:
# * could track runtime inside perl

# tracking
my $start_time = time;
my $d = {};
$d->{start} = $start_time;

system("mkdir -p /tmp/hbase_u87_test_bed");

for (my $i=22; $i>=1; $i--) {
  print "\n\nTiming chr$i\n";

  my $create = "false";
  if ($i == 22) { $create = "true"; }

  if (-e "/storage/hdfs/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_single_indels_not_on_ends_20090804/bfast.U87.rmdup.filtered.indels.chr$i.complete.pileup.gz" && !-e "/storage/hdfs/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_single_indels_not_on_ends_20090804/bfast.U87.rmdup.filtered.indels.chr$i.complete.pileup") {
    system("zcat /storage/hdfs/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_single_indels_not_on_ends_20090804/bfast.U87.rmdup.filtered.indels.chr$i.complete.pileup.gz > /storage/hdfs/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_single_indels_not_on_ends_20090804/bfast.U87.rmdup.filtered.indels.chr$i.complete.pileup");
  }
  if (-e "/storage/hdfs/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/bfast.U87.mismatch.only.rmdup.sorted.chr$i.complete.pileup.gz" && !-e "/storage/hdfs/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/bfast.U87.mismatch.only.rmdup.sorted.chr$i.complete.pileup") {
    system("zcat /storage/hdfs/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/bfast.U87.mismatch.only.rmdup.sorted.chr$i.complete.pileup.gz > /storage/hdfs/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/bfast.U87.mismatch.only.rmdup.sorted.chr$i.complete.pileup");
  }

  print "+loading database...\n";
  my $import_start = time;
  # VariantImporter <worker_module> <db_dir> <create_db> <min_coverage> <max_coverage> <min_snp_quality> <compressed> <include_indels> <include_snv> <fastqConvNum> <cacheSize> <locks> <max_thread_count> <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <input_file(s)>
  system("time java -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl -Djava.library.path=/home/brianoc/programs/BerkeleyDB.4.7/lib -cp dist/seqware-qe-0.7.0.jar:lib/db.jar:\$CLASSPATH net.sourceforge.seqware.queryengine.tools.importers.VariantImporter PileupVariantImportWorker NA $create 1 100 10 false true false 33 1 1 1 HBase U87 hg18 /storage/hdfs/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_single_indels_not_on_ends_20090804/bfast.U87.rmdup.filtered.indels.chr$i.complete.pileup");
  system("time java -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl -Djava.library.path=/home/brianoc/programs/BerkeleyDB.4.7/lib -cp dist/seqware-qe-0.7.0.jar:lib/db.jar:\$CLASSPATH net.sourceforge.seqware.queryengine.tools.importers.VariantImporter PileupVariantImportWorker NA false 1 100 10 false false true 33 1 1 1 HBase U87 hg18 /storage/hdfs/user/brianoc/datasets/U87/pileup_20090804/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/bfast.U87.mismatch.only.rmdup.sorted.chr$i.complete.pileup");
  my $import_stop = time;
  $d->{contigs}{'import'}{$i} = $import_stop - $import_start;

  print "+dumping database...\n";
  my $dump_start = time;
  # <db_dir> <output_prefix> <output_dir> <include_indels> <include_snv> <minCoverage> <maxCoverage> <minObservations> <minObservationsPerStrand> <minSNPPhred> <SNPPhredGreaterThanGenomePhred> <minPercent> <heterozygousRange> <homozygousRange> <cacheSize> <locks> <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <split_on_contig> <lookup_by_tags> <contig_str[s]_comma_sep> <tag_str[s]_comma_sep>
  system("time java -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl -Djava.library.path=/home/brianoc/programs/BerkeleyDB.4.7/lib -cp dist/seqware-qe-0.7.0.jar:lib/db.jar:\$CLASSPATH net.sourceforge.seqware.queryengine.tools.exporters.BEDExporter NA hbase_test /tmp/hbase_u87_test_bed true true 1 100 1 1 10 false 0 0-100 0-100 1 1 HBase U87 hg18 false false");
  my $dump_stop = time;
  $d->{contigs}{'dump'}{$i} = $dump_stop - $dump_start;

  print "+counting lines with API...\n";
  my $variant_count_str = `wc -l /tmp/hbase_u87_test_bed/hbase_test.all.bed`;
  $variant_count_str =~ /(\d+) /;
  my $variant_count = $1;
  $d->{contigs}{'variant_count'}{$i} = $variant_count;

  print "+counting lines with M/R...\n";
  my $mr_start = time;
  system ("time hadoop jar /usr/lib/hbase-0.20/hbase-0.20.3-1.cloudera.jar rowcounter hg18Table");
  my $mr_stop = time;
  $d->{contigs}{'mr'}{$i} = $mr_stop - $mr_start;

  print "+database size...\n";
  system("du -sh /storage/hdfs/hbase/hg18Table");

  print "+database tag size...\n";
  system("du -sh /storage/hdfs/hbase/GenomeU87TagIndexTable");

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

$d->{stop} = time;

#print Dumper $d;

print "\nFinal Numbers\n";
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

