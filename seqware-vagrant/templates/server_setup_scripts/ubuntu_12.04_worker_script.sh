#!/bin/bash -vx

# setup hosts
# NOTE: the hostname seems to already be set at least on BioNimubs OS
echo '%{HOSTS}' >> /etc/hosts


# general apt-get
apt-get update
export DEBIAN_FRONTEND=noninteractive

# install hadoop
apt-get -q -y --force-yes install git maven sysv-rc-conf xfsprogs hadoop-0.20-mapreduce-tasktracker hadoop-hdfs-datanode hadoop-client

# setup the HDFS drives
# TODO
###perl /vagrant/setup_hdfs_volumes.pl

# configuration for hadoop
cp /vagrant/conf.worker/etc/hadoop/
cd /etc/hadoop/
tar zxf conf.worker.tar.gz
cd -
update-alternatives --install /etc/hadoop/conf hadoop-conf /etc/hadoop/conf.my_cluster 50
update-alternatives --set hadoop-conf /etc/hadoop/conf.my_cluster

# hdfs config
# should setup multiple directories in hdfs-site.xml
# TODO: this assumes /mnt has the ephemeral drive!
ln -s /mnt /data
mkdir -p /data/1/dfs/nn /data/1/dfs/dn
chown -R hdfs:hdfs /data/1/dfs/nn /data/1/dfs/dn
chmod 700 /data/1/dfs/nn /data/1/dfs/dn

# start all the hadoop daemons
for x in `cd /etc/init.d ; ls hadoop-hdfs-*` ; do sudo service $x start ; done

# start mapred
for x in `cd /etc/init.d ; ls hadoop-0.20-mapreduce-*` ; do sudo service $x start ; done

# setup hbase
# TODO
#service hbase-master start

# setup daemons to start on boot
for i in cron hadoop-hdfs-datanode hadoop-0.20-mapreduce-tasktracker; do echo $i; sysv-rc-conf $i on; done

# setup NFS
# seqware tutorials
apt-get -q -y --force-yes install rpcbind nfs-common
mkdir -p /usr/tmp/seqware-oozie
echo 'rpcbind : ALL' >> /etc/hosts.deny
echo 'rpcbind : %{MASTER_PIP}' >> /etc/hosts.allow
mount %{MASTER_PIP}:/home /home
mount %{MASTER_PIP}:/usr/tmp/seqware-oozie /usr/tmp/seqware-oozie
mount %{MASTER_PIP}:/datastore /datastore

chmod a+rwx /home
chmod a+rwx /usr/tmp/seqware-oozie
chmod a+rwx /datastore


# add seqware user
useradd -d /home/seqware -m seqware -s /bin/bash

# required for running oozie jobs
mkdir /usr/lib/hadoop-0.20-mapreduce/.seqware
cp /home/seqware/.seqware/settings /usr/lib/hadoop-0.20-mapreduce/.seqware/settings
chown -R mapred:mapred /usr/lib/hadoop-0.20-mapreduce/.seqware


