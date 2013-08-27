#!/bin/bash -vx

# setup hosts
# NOTE: the hostname seems to already be set at least on BioNimubs OS
echo '%{HOSTS}' >> /etc/hosts

# general apt-get
apt-get update
export DEBIAN_FRONTEND=noninteractive


# setup zookeeper
apt-get -q -y --force-yes install zookeeper zookeeper-server
service zookeeper-server init
service zookeeper-server start

# the repos have been setup in the minimal script
##apt-get -q -y --force-yes install hadoop-0.20-conf-pseudo hue hue-server hue-plugins oozie oozie-client postgresql-9.1 postgresql-client-9.1 tomcat6-common tomcat6 apache2 git maven sysv-rc-conf hbase-master xfsprogs
apt-get -q -y --force-yes install postgresql-9.1 postgresql-client-9.1 tomcat6-common tomcat6 apache2 git maven sysv-rc-conf xfsprogs

# install Hadoop deps, the master node runs the NameNode, SecondaryNameNode and JobTracker
# NOTE: shouldn't really use secondary name node on same box for production
apt-get -q -y --force-yes install hadoop-0.20-mapreduce-jobtracker hadoop-hdfs-namenode hadoop-0.20-mapreduce-tasktracker hadoop-hdfs-datanode hadoop-client hue hue-server hue-plugins oozie oozie-client postgresql-9.1 postgresql-client-9.1 tomcat6-common tomcat6 apache2 git maven sysv-rc-conf hbase-master xfsprogs


# setup LZO
#wget -q http://archive.cloudera.com/gplextras/ubuntu/lucid/amd64/gplextras/cloudera.list
#mv cloudera.list /etc/apt/sources.list.d/gplextras.list
#apt-get update
#apt-get -q -y --force-yes install hadoop-lzo-cdh4

# configuration for hadoop
cp /vagrant/conf.master.tar.gz /etc/hadoop/
cd /etc/hadoop/
tar zxf conf.master.tar.gz
cd -
update-alternatives --install /etc/hadoop/conf hadoop-conf /etc/hadoop/conf.my_cluster 50
update-alternatives --set hadoop-conf /etc/hadoop/conf.my_cluster

# setup HDFS configs
#echo '<?xml version="1.0"?>
#<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
#
#<configuration>
#  <property>
#   <name>fs.defaultFS</name>
#   <value>hdfs://master/</value>
#  </property>
#</configuration>
#' > /etc/hadoop/conf/core-site.xml

# hdfs config
# should setup multiple directories in hdfs-site.xml
# TODO: this assumes /mnt has the ephemeral drive!
ln -s /mnt /data
mkdir -p /data/1/dfs/nn /data/1/dfs/dn
chown -R hdfs:hdfs /data/1/dfs/nn /data/1/dfs/dn
chmod 700 /data/1/dfs/nn /data/1/dfs/dn

#echo '<?xml version="1.0"?>
#<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
#<configuration>
#  <property>
#     <name>dfs.name.dir</name>
#     <value>/var/lib/hadoop-hdfs/cache/hdfs/dfs/name</value>
#  </property>
#  <property>
#     <name>dfs.permissions.superusergroup</name>
#     <value>hadoop</value>
#  </property>
#  <property>
#     <name>dfs.namenode.name.dir</name>
#     <value>/data/1/dfs/nn</value>
#  </property>
#  <property>
#     <name>dfs.namenode.data.dir</name>
#     <value>/data/1/dfs/dn</value>
#  </property>
#</configuration>
#' > /etc/hadoop/conf/hdfs-site.xml

# format HDFS
sudo -u hdfs hadoop namenode -format -force

# setup the HDFS drives
# TODO: this perl script should do all of the above
#perl /vagrant/setup_hdfs_volumes.pl

# start all the hadoop daemons
for x in `cd /etc/init.d ; ls hadoop-hdfs-*` ; do sudo service $x start ; done

# setup various HDFS directories
sudo -u hdfs hadoop fs -mkdir /tmp 
sudo -u hdfs hadoop fs -chmod -R 1777 /tmp
sudo -u hdfs hadoop fs -mkdir -p /var/lib/hadoop-hdfs/cache/mapred/mapred/staging
sudo -u hdfs hadoop fs -chmod 1777 /var/lib/hadoop-hdfs/cache/mapred/mapred/staging
sudo -u hdfs hadoop fs -chown -R mapred /var/lib/hadoop-hdfs/cache/mapred
sudo -u hdfs hadoop fs -mkdir /tmp/mapred/system
sudo -u hdfs hadoop fs -chown mapred:hadoop /tmp/mapred/system

# start mapred
for x in `cd /etc/init.d ; ls hadoop-0.20-mapreduce-*` ; do sudo service $x start ; done

# setup hue
cd /usr/share/hue
cp desktop/libs/hadoop/java-lib/hue-plugins-*.jar /usr/lib/hadoop-0.20-mapreduce/lib
cd -
service hue start

# setup Oozie
sudo -u oozie /usr/lib/oozie/bin/ooziedb.sh create -run
wget -q http://extjs.com/deploy/ext-2.2.zip
unzip ext-2.2.zip
mv ext-2.2 /var/lib/oozie/
service oozie start

# setup hbase
service hbase-master start

# setup daemons to start on boot
for i in apache2 cron hadoop-hdfs-namenode hadoop-hdfs-datanode hadoop-hdfs-secondarynamenode hadoop-0.20-mapreduce-tasktracker hadoop-0.20-mapreduce-jobtracker hue oozie postgresql tomcat6 hbase-master; do echo $i; sysv-rc-conf $i on; done


## Setup NFS before seqware
# see https://help.ubuntu.com/community/SettingUpNFSHowTo#NFS_Server
apt-get -q -y --force-yes install rpcbind nfs-kernel-server
echo '%{EXPORTS}' >> /etc/exports
exportfs -ra
# TODO: get rid of portmap localhost setting
service portmap restart
service nfs-kernel-server restart

# add seqware user
useradd -d /home/seqware -m seqware -s /bin/bash

# configure dirs for seqware
mkdir -p /usr/tmp/seqware-oozie 
chmod -R a+rwx /usr/tmp/
chown -R seqware:seqware /usr/tmp/seqware-oozie

# various seqware dirs
mkdir -p /home/seqware/bin
mkdir -p /home/seqware/jars
mkdir -p /home/seqware/crons
mkdir -p /home/seqware/logs
mkdir -p /home/seqware/released-bundles
mkdir -p /home/seqware/provisioned-bundles
mkdir -p /home/seqware/workflow-dev
mkdir -p /home/seqware/.seqware
mkdir -p /home/seqware/gitroot/seqware
sudo -u hdfs hadoop fs -mkdir -p /user/seqware
sudo -u hdfs hadoop fs -chown -R seqware /user/seqware

# configure seqware settings
cp /vagrant/settings /home/seqware/.seqware

# install hubflow
cd /home/seqware/gitroot
git clone https://github.com/datasift/gitflow
cd gitflow
./install.sh

# checkout seqware
cd /home/seqware/gitroot
git clone https://github.com/SeqWare/seqware.git

# setup bash_profile for seqware
echo "export MAVEN_OPTS='-Xmx1024m -XX:MaxPermSize=512m'" >> /home/seqware/.bash_profile

# make everything owned by seqware
chown -R seqware:seqware /home/seqware

# correct permissions
su - seqware -c 'chmod 600 /home/seqware/.seqware/*'

# configure hubflow
su - seqware -c 'cd /home/seqware/gitroot/seqware; git hf init; git hf update'

# build with develop
su - seqware -c 'cd /home/seqware/gitroot/seqware; %{SEQWARE_BRANCH_CMD}'
su - seqware -c 'cd /home/seqware/gitroot/seqware; %{SEQWARE_BUILD_CMD} 2>&1 | tee build.log'

export SEQWARE_VERSION=`ls /home/seqware/gitroot/seqware/seqware-distribution/target/seqware-distribution-*-full.jar | grep -Eo '[0-9]+\.[0-9]+\.[0-9]+(-SNAPSHOT)?' | head -1`

# setup jar
cp /home/seqware/gitroot/seqware/seqware-distribution/target/seqware-distribution-${SEQWARE_VERSION}-full.jar /home/seqware/jars/

# setup seqware cli
cp /home/seqware/gitroot/seqware/seqware-pipeline/target/seqware /home/seqware/bin
chmod +x /home/seqware/bin/seqware
echo 'export PATH=$PATH:/home/seqware/bin' >> /home/seqware/.bash_profile

# setup cronjobs
cp /vagrant/status.cron /home/seqware/crons/
chmod a+x /home/seqware/crons/status.cron
su - seqware -c '(echo "* * * * * /home/seqware/crons/status.cron >> /home/seqware/logs/status.log") | crontab -'

# make everything owned by seqware
chown -R seqware:seqware /home/seqware

# seqware database
/etc/init.d/postgresql start
sudo -u postgres psql -c "CREATE USER seqware WITH PASSWORD 'seqware' CREATEDB;"
sudo -u postgres psql --command "ALTER USER seqware WITH superuser;"
# expose sql scripts
cp /home/seqware/gitroot/seqware/seqware-meta-db/seqware_meta_db.sql /tmp/seqware_meta_db.sql
cp /home/seqware/gitroot/seqware/seqware-meta-db/seqware_meta_db_data.sql /tmp/seqware_meta_db_data.sql
chmod a+rx /tmp/seqware_meta_db.sql
chmod a+rx /tmp/seqware_meta_db_data.sql
# this is the DB actually used by people
sudo -u postgres psql --command "CREATE DATABASE seqware_meta_db WITH OWNER = seqware;"
sudo -u postgres psql seqware_meta_db < /tmp/seqware_meta_db.sql
sudo -u postgres psql seqware_meta_db < /tmp/seqware_meta_db_data.sql
# the testing DB
sudo -u postgres psql --command "CREATE DATABASE test_seqware_meta_db WITH OWNER = seqware;"
sudo -u postgres psql test_seqware_meta_db < /tmp/seqware_meta_db.sql
sudo -u postgres psql test_seqware_meta_db < /tmp/seqware_meta_db_data.sql

# stop tomcat6
/etc/init.d/tomcat6 stop

# remove landing page for tomcat
rm -rf /var/lib/tomcat6/webapps/ROOT

# seqware web service
cp /home/seqware/gitroot/seqware/seqware-webservice/target/seqware-webservice-${SEQWARE_VERSION}.war /var/lib/tomcat6/webapps/SeqWareWebService.war
cp /home/seqware/gitroot/seqware/seqware-webservice/target/seqware-webservice-${SEQWARE_VERSION}.xml /etc/tomcat6/Catalina/localhost/SeqWareWebService.xml
perl -pi -e "s/test_seqware_meta_db/seqware_meta_db/;" /etc/tomcat6/Catalina/localhost/SeqWareWebService.xml

# seqware portal
cp /home/seqware/gitroot/seqware/seqware-portal/target/seqware-portal-${SEQWARE_VERSION}.war /var/lib/tomcat6/webapps/SeqWarePortal.war
cp /home/seqware/gitroot/seqware/seqware-portal/target/seqware-portal-${SEQWARE_VERSION}.xml /etc/tomcat6/Catalina/localhost/SeqWarePortal.xml
perl -pi -e "s/test_seqware_meta_db/seqware_meta_db/;" /etc/tomcat6/Catalina/localhost/SeqWarePortal.xml

# restart tomcat6
/etc/init.d/tomcat6 start

# seqware landing page
cp -r /home/seqware/gitroot/seqware/seqware-distribution/docs/vm_landing/* /var/www/

# seqware tutorials
sudo mkdir /datastore
sudo chown seqware /datastore
# required for running oozie jobs
mkdir /usr/lib/hadoop-0.20-mapreduce/.seqware
cp /home/seqware/.seqware/settings /usr/lib/hadoop-0.20-mapreduce/.seqware/settings
chown -R mapred:mapred /usr/lib/hadoop-0.20-mapreduce/.seqware

# run full integration testing
su - seqware -c 'cd /home/seqware/gitroot/seqware; %{SEQWARE_IT_CMD} 2>&1 | tee it.log'


