use strict;

system("mkdir -p /tmp/bdb_test");
system("mkdir -p /tmp/bdb_test_bed");

for (my $i=22; $i>2; $i--) {
  print "\n\nTiming chr$i\n";

# 4294967296 2500000
  print " +loading database...\n";
  system("time java -Djava.library.path=/home/brianoc/programs/BerkeleyDB.4.7/lib -cp \$CLASSPATH:dist/seqware-qe-0.7.0.jar:lib/db.jar net.sourceforge.seqware.queryengine.tools.importers.VariantImporter PileupImportWorker /tmp/bdb_test true 1 80 10 false true true 33 4294967296 2500000 1 BerkeleyDB 1102N hg18Timing /storage/hdfs/user/brianoc/input/1102N.all.srma.chr$i.pileup > /dev/null");

  print " +dumping database...\n";
  # <db_dir> <output_prefix> <output_dir> <include_indels> <include_snv> <minCoverage> <maxCoverage> <minObservations> <minObservationsPerStrand> <minSNPPhred> <SNPPhredGreaterThanGenomePhred> <minPercent> <heterozygousRange> <homozygousRange> <cacheSize> <locks> <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <split_on_contig> <lookup_by_tags> <contig_str[s]_comma_sep> <tag_str[s]_comma_sep>
  system("time java -Djava.library.path=/home/brianoc/programs/BerkeleyDB.4.7/lib -cp \$CLASSPATH:dist/seqware-qe-0.7.0.jar:lib/db.jar net.sourceforge.seqware.queryengine.tools.exporters.BEDExporter /tmp/bdb_test bdb_test /tmp/bdb_test_bed true true 1 80 1 1 10 false 0 0-100 0-100 4294967296 2500000 BerkeleyDB 1102N hg18Timing false false > /dev/null");

  print " +counting lines...\n";
  system ("wc -l /tmp/bdb_test_bed/bdb_test.all.bed");

  print " +database size...\n";
  system("du -sh /tmp/bdb_test");

}
