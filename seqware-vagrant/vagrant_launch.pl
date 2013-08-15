use strict;
use Getopt::Long;

# VARS

# Notes:
# OS_AUTH_URL=https://api.opensciencedatacloud.org:5000/sullivan/v2.0/
# EC2_URL=https://api.opensciencedatacloud.org:8773/sullivan/services/Cloud


# skips all unit and integration tests
my $default_seqware_build_cmd = 'mvn clean install -DskipTests';
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
my $launch_cmd = "vagrant up";
my $work_dir = "target";
my $config_file = 'vagrant_launch.conf';
my $skip_its = 0;
my $skip_launch = 0;
my $config_scripts = "templates/server_setup_scripts/ubuntu_12.04_master_script.sh";
my $secondary_config_scripts = "";

GetOptions (
  "use-aws" => \$launch_aws,
  "use-virtualbox" => \$launch_vb,
  "use-openstack" => \$launch_os,
  "working-dir=s" => \$work_dir,
  "config-file=s" => \$config_file,
  "os-primary-config-scripts=s" => \$config_scripts,
  "os-secondary-config-scripts=s" => \$secondary_config_scripts,
  "skip-it-tests" => \$skip_its,
  "skip-launch" => \$skip_launch,
);


# MAIN

# figure out the current seqware version based on the most-recently built jar
$seqware_version = find_version();

# make the target dir
run("mkdir $work_dir");

# config object used for find and replace
my $configs = {};
read_config($config_file, $configs);
if (!defined($configs->{'%{SEQWARE_BUILD_CMD}'})) { $configs->{'%{SEQWARE_BUILD_CMD}'} = $default_seqware_build_cmd; }

$configs->{'%{SEQWARE_VERSION}'} = $seqware_version;

# make this explicit, one or the other, aws is given priority
if ($launch_vb) {
  $launch_cmd = "vagrant up";
  $configs->{'%{BOX}'} = "Ubuntu_12.04";
  $configs->{'%{BOX_URL}'} = "http://cloud-images.ubuntu.com/precise/current/precise-server-cloudimg-vagrant-amd64-disk1.box";
} elsif ($launch_os) {
  $launch_cmd = "vagrant up --provider=openstack";
  $configs->{'%{BOX}'} = "dummy";
  $configs->{'%{BOX_URL}'} = "https://github.com/cloudbau/vagrant-openstack-plugin/raw/master/dummy.box";
} elsif ($launch_aws) {
  $launch_cmd = "vagrant up --provider=aws";
  $configs->{'%{BOX}'} = "dummy";
  $configs->{'%{BOX_URL}'} = "https://github.com/mitchellh/vagrant-aws/raw/master/dummy.box";
} else {
  die "Don't understand the launcher type to use: AWS, OpenStack, or VirtualBox. Please specify with a --use-* param\n";
}

# skip the integration tests if specified --skip-its
if ($skip_its) { $configs->{'%{SEQWARE_IT_CMD}'} = ""; }

# process server scripts into single bash script
setup_os_config_scripts($config_scripts, "$work_dir/os_server_setup.sh");
prepare_files();
if (!$skip_launch) {
  # this launches and does first round setup
  launch_instances();
  # this finds IP addresses and does second round of setup
  provision_instances();
}


# SUBS

sub find_node_info {
  my $d = {};

  run("cd $work_dir");
  
  return($d);
}

# this finds all the host IP addresses and then runs the second provisioning on them
sub provision_intances {
  # first, find all the hosts and get their info
  my $hosts = find_node_info();
  print Dumper($hosts);

  # LEFT OFF HERE
}

# this basically cats files together after doing an autoreplace
sub setup_os_config_scripts() {
  my ($config_scripts, $output) = @_;
  my @scripts = split /,/, $config_scripts;
  foreach my $script (@scripts) {
    autoreplace($script, "$output.temp"); 
    run("cat $output.temp >> $output");
    run("rm $output.temp");
  }
}

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
   print "SEQWARE VERSION: $1\n";
   return($1);
  } else { 
    die "ERROR: CAN'T FIGURE OUT VERSION FROM FILE: $file\n";
  }
}

sub prepare_files {
   # Vagrantfile
  autoreplace("templates/Vagrantfile.template", "$work_dir/Vagrantfile");
  # cron
  autoreplace("templates/status.cron", "$work_dir/status.cron");
  # settings, user data
  copy("templates/settings", "$work_dir/settings");
  copy("templates/user_data.txt", "$work_dir/user_data.txt");
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
