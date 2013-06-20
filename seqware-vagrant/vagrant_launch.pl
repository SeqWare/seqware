use strict;
use Getopt::Long;

# VARS

my $seqware_build_cmd = 'mvn clean install -DskipTests';
my $seqware_version = '1.0.1-SNAPSHOT';
my $aws_key = '';
my $aws_secret_key = '';
my $launch_aws = 1;
my $launch_vb = 0;
my $launch_os = 0;
my $launch_cmd = "vagrant up --provider=aws &> vagrant_launch.log";
my $work_dir = "target";

GetOptions (
  "seqware-build-cmd=s" => \$seqware_build_cmd,
  "aws-key=s" => \$aws_key,
  "aws-secret-key=s" => \$aws_secret_key,
  "use-aws" => \$launch_aws,
  "use-virtualbox" => \$launch_vb,
  "use-openstack" => \$launch_os,
  "working-dir=s" => \$work_dir,
);



# MAIN

# figure out the current seqware version based on the most-recently built jar
$seqware_version = find_version();

# make this explicit, one or the other, aws is given priority
if ($launch_vb) {
  $launch_cmd = "vagrant up &> vagrant_launch.log";
} elsif ($launch_os) {
  $launch_cmd = "vagrant up --provider=openstack &> vagrant_launch.log";
}

my $configs = {
  '%{SEQWARE_BUILD_CMD}' => $seqware_build_cmd,
  '%{SEQWARE_VERSION}' => $seqware_version,
  '%{AWS_KEY}' => $aws_key,
  '%{AWS_SECRET_KEY}' => $aws_secret_key,
};

prepare_files();
launch_instances();



# SUBS

sub launch_instances {
  run("cd $work_dir; $launch_cmd");
}

sub find_version {
  my $file = `ls ../seqware-distribution/target/seqware-distribution-*-full.jar | grep -v qe-full`;
  chomp $file;
  if ($file =~ /seqware-distribution-(\S+)-full.jar/) {
   return($1);
  } else { 
    die "ERROR: CAN'T FIGURE OUT VERSION FROM FILE: $file\n";
  }
}

sub prepare_files {
  run("mkdir $work_dir");
  # the jar file
  copy("../seqware-distribution/target/seqware-distribution-$seqware_version-full.jar", "$work_dir/seqware-distribution-$seqware_version-full.jar");
  # the web service
  copy("../seqware-webservice/target/seqware-webservice-$seqware_version.war", "$work_dir/seqware-webservice-$seqware_version.war");
  replace("../seqware-webservice/target/seqware-webservice-$seqware_version.xml", "$work_dir/seqware-webservice-$seqware_version.xml", "jdbc:postgresql://localhost:5432/test_seqware_meta_db", "jdbc:postgresql://localhost:5432/seqware_meta_db");
  # the portal
  copy("../seqware-portal/target/seqware-portal-$seqware_version.war", "$work_dir/seqware-portal-$seqware_version.war");
  replace("../seqware-portal/target/seqware-portal-$seqware_version.xml", "$work_dir/seqware-portal-$seqware_version.xml", "jdbc:postgresql://localhost:5432/test_seqware_meta_db", "jdbc:postgresql://localhost:5432/seqware_meta_db");
  # Vagrantfile
  autoreplace("templates/Vagrantfile", "$work_dir/Vagrantfile"); 
  # database
  copy("../seqware-meta-db/seqware_meta_db.sql", "$work_dir/seqware_meta_db.sql");
  copy("../seqware-meta-db/seqware_meta_db_data.sql", "$work_dir/seqware_meta_db_data.sql");
  # cron
  autoreplace("templates/status.cron", "$work_dir/status.cron");
  # settings, user data
  copy("templates/settings", "$work_dir/settings");
  copy("templates/user_data.txt", "$work_dir/user_data.txt");
  # landing page
  rec_copy("../seqware-distribution/docs/vm_landing", "$work_dir/");
}

sub autoreplace {
  my ($src, $dest) = @_;
  print "AUTOREPLACE: $src $dest\n";
  open IN, "<$src" or die;
  open OUT, ">$dest" or die;
  while(<IN>) {
    foreach my $key (keys %{$configs}) {
      my $value = $configs->{$key};
      $_ =~ s/$key/$value/g;
    }
    print OUT $_;
  }
  close IN; 
  close OUT;
}

sub replace {
  my ($src, $dest, $from, $to) = @_;
  print "REPLACE: $src, $dest, $from, $to\n";
  open IN, "<$src" or die;
  open OUT, ">$dest" or die;
  while(<IN>) {
    $_ =~ s/$from/$to/g;
    print OUT $_;
  }
  close IN; 
  close OUT;
}

sub copy {
  my ($src, $dest) = @_;
  print "COPYING: $src, $dest\n";
  open IN, "<$src" or die;
  open OUT, ">$dest" or die;
  while(<IN>) {
    print OUT $_;
  }
  close IN;
  close OUT;
}

sub rec_copy {
  my ($src, $dest) = @_;
  print "COPYING REC: $src, $dest\n";
  run("cp -r $src $dest");
}

sub run {
  my ($cmd) = @_;
  print "RUNNING: $cmd\n";
  my $result = system("$cmd");
  if ($result != 0) { "\nERROR!!! CMD RESULTED IN RETURN VALUE OF $result\n\n"; }
}
