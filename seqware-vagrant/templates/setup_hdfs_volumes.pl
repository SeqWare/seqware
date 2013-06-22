use strict;

# this script attempts to format, mount, and add volumes to HDFS

my $list = `ls -1 /dev/sd* /dev/xv*`;
my @list = split /\n/, $list;
foreach my $dev (@list) {
  next if (!-e $dev || -l $dev);
  next if (blacklist($dev));
  print "DEV: $dev\n";
  if(mounted($dev)) {
    print "  NOT MOUTING SINCE ALREADY MOUNTED!\n";
    my $mount_path = find_mount_path($dev);
    if (!-e "$mount_path/var/lib/hadoop-hdfs/cache/hdfs/dfs/data") {
      print "  MOUNT NOT IN HDFS SETTINGS\n";
      system("mkdir -p $mount_path/var/lib/hadoop-hdfs/cache/hdfs/dfs/data && chown -R hdfs:hdfs $mount_path/var");
      add_to_config("$mount_path/var/lib/hadoop-hdfs/cache/hdfs/dfs/data");
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
        my $mount = system("bash -c 'mkdir -p /mnt/$dev_name && mount $dev /mnt/$dev_name && mkdir -p /mnt/$dev_name/hadoop-hdfs/cache/hdfs/dfs/data && chown -R hdfs:hdfs /mnt/$dev_name/hadoop-hdfs'");
        if ($mount) { print "  UNABLE TO MOUNT $dev on /mnt/$1\n"; }
        else {
          # <value>file:///var/lib/hadoop-hdfs/cache/${user.name}/dfs/data</value>
          add_to_config("/mnt/$dev_name/hadoop-hdfs/cache/hdfs/dfs/data");
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

sub find_mount_path {
  my $dev = shift;
  my $path = `df -h | grep $dev | awk '{ print \$6}'`;
  chomp $path;
  return($path);
}
