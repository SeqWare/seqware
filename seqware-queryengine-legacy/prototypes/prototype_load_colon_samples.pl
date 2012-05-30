use strict;

my @glob = glob "/datastore/nextgenproc/seq_vs_snpchip/colon/pileup/*.trimmed.annotated.translated_to_genomic.pileup.gz";

#my $CLASSPATH = $ENV{'CLASSPATH'};

foreach my $file (@glob) {
  print "$file\n";
  next if ($file =~ /100813_UNC5-RDR300700_00020_FC_62A20AAXX.4/);
  $file =~ /\/([^\/]+).trimmed.annotated.translated_to_genomic.pileup.gz/;
  my $filename = $1;
  $filename =~ s/\./-/g;
  print "echo \"time java -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl -Djava.library.path=/home/brianoc/programs/BerkeleyDB.4.7/lib -cp dist/seqware-qe-0.7.0.jar:lib/db.jar:\$CLASSPATH net.sourceforge.seqware.queryengine.tools.importers.VariantImporter PileupVariantImportWorker NA true 1 1000000 10 true true true 33 1 1 1 HBase $filename hg19 $file; time java -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl -Djava.library.path=/home/brianoc/programs/BerkeleyDB.4.7/lib -cp dist/seqware-qe-0.7.0.jar:lib/db.jar:\$CLASSPATH net.sourceforge.seqware.queryengine.tools.importers.PileupCoverageImporter HBase $filename hg19 NA 1 1 true 1000 $file\" | qsub -cwd -S /bin/bash -N hbase\n";
}

