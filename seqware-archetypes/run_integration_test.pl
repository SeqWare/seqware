#!/usr/bin/perl

# starting point for integration tests
# example: /home/seqware/Temp/run_integration_test.pl ~/seqware-full.jar simple-legacy-ftl-workflow 1.0-SNAPSHOT

use strict;

if (@ARGV != 3) { print "Usage: run_integration_test.pl <seqware_jar_path> <workflow_name> <workflow_version>\n"; }

my ($jar, $workflow_name, $workflow_version) = @ARGV;

er("TESTING BUNDLEMANAGER LISTING", "java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -l -b `pwd`");
#er("TESTING BUNDLEMANAGER TEST", "java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -t -b `pwd` --workflow $workflow_name --version $workflow_version");
my $install_str = er("TESTING BUNDLEMANAGER INSTALL", "java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -i -b `pwd`");
$install_str =~ /WORKFLOW_ACCESSION: (\d+)/;
my $workflow_acc = $1;
my $ini_file = er("LIST WORKFLOW PARAMS", "java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-workflow-params --workflow-accession $workflow_acc");
my @ini_lines = split /\n/, $ini_file;
my $new_ini = "";
foreach my $line (@ini_lines) {
  next if ($line =~ /^-/ || $line =~ /^=/ || $line =~ /^$/ || $line =~ /Running Plugin/ || $line =~ /Setting Up Plugin/);
  $new_ini .= $line."\n";
}
open OUT, ">workflow.ini" or die;
print OUT $new_ini;
close OUT;


my $workflow_run_txt = er("TESTING SCHEDULING OF WORKFLOW", "java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow.ini --workflow-accession $workflow_acc --schedule --parent-accessions 839 --host `hostname --long`");
$workflow_run_txt =~ /WORKFLOW_RUN ACCESSION: (\d+)/;
my $workflow_run_accession = $1;


er("TESTING LAUNCHING OF SCHEDULED WORKFLOW", "java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher --  --launch-scheduled");


er("TESTING LAUNCHING AND NOT WAITING FOR WORKFLOW", "java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow.ini --workflow-accession $workflow_acc --parent-accessions 839 --host `hostname --long`");


er("TESTING MONITORING OF SCHEDULED/LAUNCHED WORKFLOW", "java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowStatusChecker -- --workflow-run-accession $workflow_run_accession");


er("TESTING LAUNCHING AND WAITING FOR WORKFLOW", "java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow.ini --workflow-accession $workflow_acc --parent-accessions 839 --wait --host `hostname --long`");


er("TESTING LAUNCHING AND WAITING FOR WORKFLOW NO METADATA", "java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow.ini --workflow-accession $workflow_acc --no-metadata --wait --host `hostname --long`");


er("TESTING MONITORING OF SCHEDULED/LAUNCHED WORKFLOW ONE MORE TIME", "java -jar $jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowStatusChecker -- --workflow-run-accession $workflow_run_accession");


sub er {
  my ($msg, $cmd) = @_;

  print "\n$msg\n\nRUNNING COMMAND: $cmd\n\n";

  my $stdout = `$cmd`;
  my $ret = $?;

  if ($ret != 0) {
    print "The command failed with a return value of $ret\n";
    print "$cmd\n";
    exit (-1);
  }

  print "$stdout\n\n";
  print "\nCOMMAND WORKED!!!!\n\n";
  return($stdout);
}

