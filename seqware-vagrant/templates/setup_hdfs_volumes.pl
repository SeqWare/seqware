use strict;

# PURPOSE:
# This script attempts to format, mount, encrypt, and add volumes to HDFS.  You
# will be left with various devices mounted as /mnt/<devname>/ with a directory
# called /mnt/<devname>/encrypted under which anything written will be
# encrypted using ecryptfs with a random key. Anything outside of this
# directory will not be encrypted. If ecryptfs is not installed the encrypted
# directory is not created.
# ASSUMPTIONS:
# * hdfs is not running
# * you have ecryptfs, mkfs.xfs, and hadoop installed
# * you want all drives to be used for HDFS

my $list = `ls -1 /dev/sd* /dev/xv*`;
my @list = split /\n/, $list;
foreach my $dev (@list) {
  # skip if doesn't exist
  next if (!-e $dev || -l $dev);
  # skip if the root partition
  next if (blacklist($dev));
  # then extra device so can continue
  print "DEV: $dev\n";
  # if already mounted just add directory
  if(mounted($dev)) {
    print "  NOT MOUTING SINCE ALREADY MOUNTED!\n";
    my $mount_path = find_mount_path($dev);
    # if ecryptfs was success, the mount path gets encrypted added to it
    if(setup_ecryptfs($mount_path)) {
      $mount_path = $mount_path."/encrypted";
    }
    if (!-e "$mount_path/hadoop-hdfs/cache/hdfs/dfs/data") {
      print "  MOUNT NOT IN HDFS SETTINGS\n";
      system("mkdir -p $mount_path/hadoop-hdfs/cache/hdfs/dfs/data && chown -R hdfs:hdfs $mount_path/hadoop-hdfs");
      add_to_config("$mount_path/hadoop-hdfs/cache/hdfs/dfs/data");
    }
  } else {
    print "  NOT MOUNTED!\n";
    my $format = system("bash -c 'mkfs.xfs -f $dev &> /dev/null'");
    if ($format) { print "  UNABLE TO FORMAT!\n"; }
    else {
      print "  FORMATTED OK!\n";
      $dev =~ /\/dev\/(\S+)/;
      my $dev_name = $1;
      if (!mounted($dev_name)) {
        print "  MOUNTING BECAUSE NOT MOUNTED\n";
        my $mount = system("bash -c 'mkdir -p /mnt/$dev_name && mount $dev /mnt/$dev_name");
        my $mount_path = "/mnt/$dev_name";
        if (setup_ecryptfs("/mnt/$dev_name")) { $mount_path = $mount_path."/encrypted"; }
        my $mount2 = system("mkdir -p $mount_path/hadoop-hdfs/cache/hdfs/dfs/data && chown -R hdfs:hdfs $mount_path/hadoop-hdfs'");
        if ($mount != 0 || $mount2 != 0) { print "  UNABLE TO MOUNT $dev on /mnt/$1\n"; }
        else {
          # <value>file:///var/lib/hadoop-hdfs/cache/${user.name}/dfs/data</value>
          add_to_config("$mount_path/hadoop-hdfs/cache/hdfs/dfs/data");
        }
      }
    }
  }
}

sub blacklist {
  my $dev = shift;
  if ($dev =~ /sda/ || $dev =~ /hda/ || $dev =~ /xvda/) {
    print "  BLACKLIST DEV $dev\n";
    return(1);
  }
  return(0);
}

sub mounted {
  my $dev = shift;
  # blacklist any drives that are likely to be root partition
  if ($dev =~ /sda/ || $dev =~ /hda/ || $dev =~ /xvda/) {
    print "  DEV BLACKLISTED: $dev\n";
    return(1);
  }
  my $count = `df -h | grep $dev | wc -l`;
  chomp $count;
  return($count);
}

sub add_to_config {
  my $path = shift;
  if (-e "/etc/hadoop/conf/hdfs-site.xml") {
         open CONF, "</etc/hadoop/conf/hdfs-site.xml";
         my $newfile = "";
         while(<CONF>) {
          chomp;
          $_ =~ s/file:\/\/\/var\/lib\/hadoop-hdfs\/cache\/\$\{user.name\}\/dfs\/data/file:\/\/\/var\/lib\/hadoop-hdfs\/cache\/\$\{user.name\}\/dfs\/data,file:\/\/$path/g;
          $newfile .= "$_\n";
         }
         close CONF;
         system("bash -c 'cp /etc/hadoop/conf/hdfs-site.xml /etc/hadoop/conf/hdfs-site.dist'");
         open CONF, ">/etc/hadoop/conf/hdfs-site.xml";
         print CONF $newfile;
         close CONF;
       } else {
         print "  ERROR: can't find /etc/hadoop/conf/hdfs-site.xml\n";
       }
}

sub setup_ecryptfs {
  my ($dir) = @_;
  my $ecrypt_result;
  # attempt to find this tool
  my $result = system("which mount.ecryptfs");
  if ($result == 0) {
    my @chars = ( "A" .. "Z", "a" .. "z", 0 .. 9 );
    my $password = join("", @chars[ map { rand @chars } ( 1 .. 11 ) ]);
    my $ecrypt_cmd = "mkdir -p $dir/encrypted && mount.ecryptfs $dir/encrypted $dir/encrypted -o ecryptfs_cipher=aes,ecryptfs_key_bytes=16,ecryptfs_passthrough=n,ecryptfs_enable_filename_crypto=n,no_sig_cache,key=passphrase:passwd=$password";
    $ecrypt_result = system($ecrypt_cmd);
    if ($ecrypt_result) {
       print "   ERROR: there was a problem running the ecrypt command $ecrypt_cmd\n";
       return(0);
    }
  } else {
    print "   ERROR: can't find mount.ecryptfs so skipping encryption of the HDFS volume\n";
    return(0);
  }
  return(1);
}

sub find_mount_path {
  my $dev = shift;
  my $path = `df -h | grep $dev | awk '{ print \$6}'`;
  chomp $path;
  return($path);
}
