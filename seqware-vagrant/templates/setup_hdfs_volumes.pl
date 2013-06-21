use strict;

# this script attempts to format, mount, and add volumes to HDFS

my $list = `ls -1 /dev/sd* /dev/xv*`;
my @list = split /\n/, $list;
foreach my $dev (@list) {
  next if (!-e $dev || -l $dev);
  print "DEV: $dev\n";
  my $format = system("mkfs.xfs $dev &> /dev/null");
  if ($format) { print "  UNABLE TO FORMAT!\n"; }
  else {
     print "  FORMATTED!\n";
     print "  MOUNTING\n";
     $dev =~ /\/dev\/(\S+)/;
     my $dev_name = $1;
     my $mount = system("mkdir /mnt/$1 && mount $dev /mnt/$1 && mkdir -p /mnt/$dev_name/hadoop-hdfs/cache/");
     if ($mount) { print "  UNABLE TO MOUNT $dev on mnt/$1\n"; }
     else {
       # <value>file:///var/lib/hadoop-hdfs/cache/${user.name}/dfs/data</value>
       if (-e "/etc/hadoop/conf/hdfs-site.xml") {
         open CONF, "</etc/hadoop/conf/hdfs-site.xml";
         my $newfile = "";
         while(<CONF>) {
          chomp;
          $_ =~ s/file:\/\/\/var\/lib\/hadoop-hdfs\/cache\/\$\{user.name\}\/dfs\/data/file:\/\/\/var\/lib\/hadoop-hdfs\/cache\/\$\{user.name\}\/dfs\/data,file:\/\/\/mnt\/$dev_name\/hadoop-hdfs\/cache\/\$\{user.name\}\/dfs\/data/g; 
          $newfile .= "$_\n";
         }
         close CONF;
         system("cp /etc/hadoop/conf/hdfs-site.xml /etc/hadoop/conf/hdfs-site.dist");
         open CONF, ">/etc/hadoop/conf/hdfs-site.xml";
         print CONF $newfile;
         close CONF;
       } else {
         print "  ERROR: can't find /etc/hadoop/conf/hdfs-site.xml\n";
       }
     }
   }
}
