use strict;
use Getopt::Long;

# VARS

# OS_AUTH_URL=https://api.opensciencedatacloud.org:5000/sullivan/v2.0/
# EC2_URL=https://api.opensciencedatacloud.org:8773/sullivan/services/Cloud


# skips all unit and integration tests
my $default_seqware_build_cmd = 'mvn clean install -DskipTests';
my $default_seqware_it_cmd = "mvn clean install -DskipITs=false -P 'extITs,!embeddedTomcat,!embeddedHBase'";
# runs unit tests
# my $seqware_build_cmd = 'mvn clean install &> build.log';
# all unit and integration tests that only require postgres
#my $seqware_build_cmd = 'mvn clean install -DskipITs=false &> build.log';
# the full unit and integration tests including those needing globus/oozie
#my $seqware_build_cmd = 'mvn clean install -DskipITs=false -P extITs &> build.log';
my $seqware_version = 'UNKNOWN';
my $aws_key = '';
my $aws_secret_key = '';
my $launch_aws = 0;
my $launch_vb = 0;
my $launch_os = 0;
my $launch_cmd = "vagrant up &> vagrant_launch.log";
my $work_dir = "target";
my $config_file = 'vagrant_launch.conf';
my $skip_its = 0;

GetOptions (
  "use-aws" => \$launch_aws,
  "use-virtualbox" => \$launch_vb,
  "use-openstack" => \$launch_os,
  "working-dir=s" => \$work_dir,
  "config-file=s" => \$config_file,
  "skip-it-tests" => \$skip_its,
);


# MAIN

# figure out the current seqware version based on the most-recently built jar
$seqware_version = find_version();

# config object used for find and replace
my $configs = {};
read_config($config_file, $configs);
if (!defined($configs->{'%{SEQWARE_BUILD_CMD}'})) { $configs->{'%{SEQWARE_BUILD_CMD}'} = $default_seqware_build_cmd; }
$configs->{'%{SEQWARE_VERSION}'} = $seqware_version;

# make this explicit, one or the other, aws is given priority
if ($launch_vb) {
  $launch_cmd = "vagrant up &> vagrant_launch.log";
  $configs->{'%{BOX}'} = "Ubuntu_12.04";
  $configs->{'%{BOX_URL}'} = "http://cloud-images.ubuntu.com/precise/current/precise-server-cloudimg-vagrant-amd64-disk1.box";
} elsif ($launch_os) {
  $launch_cmd = "vagrant up --provider=openstack &> vagrant_launch.log";
  $configs->{'%{BOX}'} = "dummy";
  $configs->{'%{BOX_URL}'} = "https://github.com/cloudbau/vagrant-openstack-plugin/raw/master/dummy.box";
} elsif ($launch_aws) {
  $launch_cmd = "vagrant up --provider=aws &> vagrant_launch.log";
  $configs->{'%{BOX}'} = "dummy";
  $configs->{'%{BOX_URL}'} = "https://github.com/mitchellh/vagrant-aws/raw/master/dummy.box";
} else {
  die "Don't understand the launcher type to use: AWS, OpenStack, or VirtualBox. Please specify with a --use-* param\n";
}

# add the integration tests
if (!$skip_its) { $configs->{'%{SEQWARE_IT_CMD}'} = "mvn clean install -DskipITs=false -P 'extITs,!embeddedTomcat,!embeddedHBase' &> build.log"; }

prepare_files();
launch_instances();



# SUBS

sub read_config() {
  my ($file, $config) = @_;
  open IN, "<$file" or die "Can't open your vagrant launch config file: $file\n";
  while (<IN>) {
   chomp;
   next if (/^#/);
   if (/^\s*(\S+)\s*=\s*(.*)$/) {
     $config->{'%{'.$1.'}'} = $2;
     #print "$1 \t $2\n";
   }
  }
  close IN;
}


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
  #copy("../seqware-distribution/target/seqware-distribution-$seqware_version-full.jar", "$work_dir/seqware-distribution-$seqware_version-full.jar");
  # the web service
  #copy("../seqware-webservice/target/seqware-webservice-$seqware_version.war", "$work_dir/seqware-webservice-$seqware_version.war");
  replace("../seqware-webservice/target/seqware-webservice-$seqware_version.xml", "$work_dir/seqware-webservice-$seqware_version.xml", "jdbc:postgresql://localhost:5432/test_seqware_meta_db", "jdbc:postgresql://localhost:5432/seqware_meta_db");
  # the portal
  #copy("../seqware-portal/target/seqware-portal-$seqware_version.war", "$work_dir/seqware-portal-$seqware_version.war");
  replace("../seqware-portal/target/seqware-portal-$seqware_version.xml", "$work_dir/seqware-portal-$seqware_version.xml", "jdbc:postgresql://localhost:5432/test_seqware_meta_db", "jdbc:postgresql://localhost:5432/seqware_meta_db");
  # Vagrantfile
  autoreplace("templates/Vagrantfile.template", "$work_dir/Vagrantfile");
  # the master configuration script
  autoreplace("templates/ubuntu_12.04_master_script.sh", "$work_dir/ubuntu_12.04_master_script.sh");
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
  # script for setting up hadoop hdfs
  copy("templates/setup_hdfs_volumes.pl", "$work_dir/setup_hdfs_volumes.pl");
}

sub autoreplace {
  my ($src, $dest) = @_;
  print "AUTOREPLACE: $src $dest\n";
  open IN, "<$src" or die "Can't open input file $src\n";
  open OUT, ">$dest" or die "Can't open output file $dest\n";
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
  my $result = system("bash -c '$cmd'");
  if ($result != 0) { "\nERROR!!! CMD RESULTED IN RETURN VALUE OF $result\n\n"; }
}
