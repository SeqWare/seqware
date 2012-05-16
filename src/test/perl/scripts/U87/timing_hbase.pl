use strict;

system("mkdir -p /tmp/hbase_test_bed");


for (my $i=22; $i>2; $i--) {
  print "\n\nTiming chr$i\n";

  my $create = "false";
  if ($i == 22) { $create = "true"; }

  print " +loading database...\n";
  # VariantImporter <worker_module> <db_dir> <create_db> <min_coverage> <max_coverage> <min_snp_quality> <compressed> <include_indels> <include_snv> <fastqConvNum> <cacheSize> <locks> <max_thread_count> <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <input_file(s)>
  system("time java -Djava.library.path=/home/brianoc/programs/BerkeleyDB.4.7/lib -cp dist/seqware-qe-0.7.0.jar:lib/db.jar:\$CLASSPATH net.sourceforge.seqware.queryengine.tools.importers.VariantImporter PileupImportWorker NA $create 1 80 10 false true true 33 1 1 1 HBase Timing hg18Timing /storage/hdfs/user/brianoc/input/1102N.all.srma.chr$i.pileup");

  print " +dumping database...\n";
  # <db_dir> <output_prefix> <output_dir> <include_indels> <include_snv> <minCoverage> <maxCoverage> <minObservations> <minObservationsPerStrand> <minSNPPhred> <SNPPhredGreaterThanGenomePhred> <minPercent> <heterozygousRange> <homozygousRange> <cacheSize> <locks> <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <split_on_contig> <lookup_by_tags> <contig_str[s]_comma_sep> <tag_str[s]_comma_sep>
  system("time java -Djava.library.path=/home/brianoc/programs/BerkeleyDB.4.7/lib -cp dist/seqware-qe-0.7.0.jar:lib/db.jar:\$CLASSPATH net.sourceforge.seqware.queryengine.tools.exporters.BEDExporter NA hbase_test /tmp/hbase_test_bed true true 1 80 1 1 10 false 0 0-100 0-100 1 1 HBase Timing hg18Timing false false");

  print " +counting lines with API...\n";
  system ("wc -l /tmp/hbase_test_bed/hbase_test.all.bed");

  print " +counting lines with M/R...\n";
  system ("time hadoop jar /usr/lib/hbase-0.20/hbase-0.20.3-1.cloudera.jar rowcounter hg18TimingTable");

  print " +database size...\n";
  system("du -sh /storage/hdfs/hbase/hg18TimingTable");

  print " +database tag size...\n";
  system("du -sh /storage/hdfs/hbase/GenomeTimingTagIndexTable");

}
