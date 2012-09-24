use strict;
use Data::Dumper;

# this script is used to test the command line tool suite
# it will find the latest jar distribution file and call
# the command line tools.
# MAKE SURE YOUR .seqware/settings FILE POINTS TO A DATABASE
# YOU DON'T MIND DUMPING A BUNCH OF RANDOM DATA INTO
# This code should be migrated to pure Java tests.

if (scalar(@ARGV) < 5) { print "jar repeat prefix file bucket\n"; }

my ($jar, $repeat, $prefix, $file, $bucket) = @ARGV;
my $errors = {};

for(my $i=0; $i<$repeat; $i++) {
  run("java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --list-tables", "list_tables");
  run("java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table study --list-fields", "list_study_fields");
  run("java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table experiment --list-fields", "list_exp_fields");
  run("java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table sample --list-fields", "list_sample_fields");

  run("java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table study --create --field 'title::$prefix New Test Study $i' --field 'description::This is a test description' --field 'accession::InternalID$i' --field 'center_name::Courtagen' --field 'center_project_name::Courtagen Test Project $i' --field study_type::4", "study_create") =~ /SWID: (\d+)/;
  my $study_id = $1;
  print "Study ID: $study_id\n";
  run("java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table experiment --create --field 'title::$prefix New Test Experiment $i' --field 'description::This is a test description' --field study_accession::$study_id --field platform_id::26", "exp_create") =~ /SWID: (\d+)/;
  my $exp_id = $1;
  print "Exp ID: $exp_id\n";
  run("java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table sample --create --field 'title::$prefix New Test Sample $i' --field 'description::This is a test description' --field experiment_accession::$exp_id --field organism_id::26", "sample_create") =~ /SWID: (\d+)/;
  my $sample_id = $1;
  print "Sample ID: $sample_id\n";

  my $prov_files_id = 0;
  if ($file ne '' && $bucket ne '') {
    run("java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles --metadata-output-file-prefix $bucket --metadata-parent-accession $sample_id --metadata-processing-accession-file new_accession.txt -- -im fastq::chemical/seq-na-fastq-gzip::$file -o $bucket", "provision_files") =~ /ProcessingAccession for this run is: (\d+)/;
    $prov_files_id = $1; 

    run("java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.GenericMetadataSaver --metadata-parent-accession $sample_id -- --gms-output-file fastq::chemical/seq-na-fastq-gzip::$bucket$file --gms-algorithm Upload$i$prefix --gms-suppress-output-file-check", "generic_metadata_saver");
  } 

  run("java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-install", "list_workflows");

  run("java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-workflow-params --workflow-accession 27133 > workflow_params.txt", "list_workflow_params");

  run("java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow_params.txt --workflow-accession 27133 --schedule --parent-accessions $prov_files_id", "sched_workflow");
}

print Dumper($errors);

sub run {
  my ($cmd, $name) = @_;
  print "RUNNING: $cmd\n";
  my $ret = system("$cmd > temp.status");
  my $state = "failed";
  if ($ret == 0) { $state = "success"; }
  else { print "WARNING: got an error for $name\n"; }
  $errors->{$name}{$state}++;
  return(`cat temp.status`);
}
