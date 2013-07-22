#!/bin/bash
# install the hadoop repo
wget -q http://archive.cloudera.com/cdh4/one-click-install/precise/amd64/cdh4-repository_1.0_all.deb
dpkg -i cdh4-repository_1.0_all.deb
curl -s http://archive.cloudera.com/cdh4/ubuntu/precise/amd64/cdh/archive.key | sudo apt-key add -

# basic tools
apt-get install curl unzip -y

# setup cloudera manager repo (not used)
REPOCM=${REPOCM:-cm4}
CM_REPO_HOST=${CM_REPO_HOST:-archive.cloudera.com}
CM_MAJOR_VERSION=$(echo $REPOCM | sed -e 's/cm\\([0-9]\\).*/\\1/')
CM_VERSION=$(echo $REPOCM | sed -e 's/cm\\([0-9][0-9]*\\)/\\1/')
OS_CODENAME=$(lsb_release -sc)
OS_DISTID=$(lsb_release -si | tr '[A-Z]' '[a-z]')
if [ $CM_MAJOR_VERSION -ge 4 ]; then
  cat > /etc/apt/sources.list.d/cloudera-$REPOCM.list <<EOF
deb [arch=amd64] http://$CM_REPO_HOST/cm$CM_MAJOR_VERSION/$OS_DISTID/$OS_CODENAME/amd64/cm $OS_CODENAME-$REPOCM contrib
deb-src http://$CM_REPO_HOST/cm$CM_MAJOR_VERSION/$OS_DISTID/$OS_CODENAME/amd64/cm $OS_CODENAME-$REPOCM contrib
EOF
curl -s http://$CM_REPO_HOST/cm$CM_MAJOR_VERSION/$OS_DISTID/$OS_CODENAME/amd64/cm/archive.key > key
apt-key add key
rm key
fi

# get packages
apt-get update
export DEBIAN_FRONTEND=noninteractive
#apt-get -q -y --force-yes install oracle-j2sdk1.6 cloudera-manager-server-db cloudera-manager-server cloudera-manager-daemons
#apt-get -q -y --force-yes install oracle-j2sdk1.6 hadoop-0.20-conf-pseudo hue hue-server hue-plugins oozie oozie-client postgresql-9.1 postgresql-client-9.1 tomcat6-common tomcat6 apache2 git maven sysv-rc-conf hbase-master xfsprogs
# get Java
apt-get -q -y --force-yes install libasound2 libxi6 libxtst6 libxt6 
wget http://archive.cloudera.com/cm4/ubuntu/precise/amd64/cm/pool/contrib/o/oracle-j2sdk1.6/oracle-j2sdk1.6_1.6.0+update31_amd64.deb
dpkg -i oracle-j2sdk1.6_1.6.0+update31_amd64.deb
apt-get -q -y --force-yes install hadoop-0.20-conf-pseudo hue hue-server hue-plugins oozie oozie-client postgresql-9.1 postgresql-client-9.1 tomcat6-common tomcat6 apache2 git maven sysv-rc-conf hbase-master xfsprogs

# setup the HDFS drives
perl /vagrant/setup_hdfs_volumes.pl

# start cloudera manager
#service cloudera-scm-server-db initdb
#service cloudera-scm-server-db start
#service cloudera-scm-server start

# format the name node
sudo -u hdfs hdfs namenode -format -force

# start all the hadoop daemons
for x in `cd /etc/init.d ; ls hadoop-hdfs-*` ; do sudo service $x start ; done

# setup various HDFS directories
sudo -u hdfs hadoop fs -mkdir /tmp 
sudo -u hdfs hadoop fs -chmod -R 1777 /tmp
sudo -u hdfs hadoop fs -mkdir -p /var/lib/hadoop-hdfs/cache/mapred/mapred/staging
sudo -u hdfs hadoop fs -chmod 1777 /var/lib/hadoop-hdfs/cache/mapred/mapred/staging
sudo -u hdfs hadoop fs -chown -R mapred /var/lib/hadoop-hdfs/cache/mapred

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

# add seqware user
useradd -d /home/seqware -m seqware -s /bin/bash

# configure dirs for seqware
mkdir -p /usr/tmp/seqware-oozie 
chmod -R a+rwx /usr/tmp/
chown -R seqware:seqware /usr/tmp/seqware-oozie

# various seqware dirs
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
su - seqware -c 'cd /home/seqware/gitroot/seqware; %{SEQWARE_BUILD_CMD} &> build.log'

# setup jar
cp /home/seqware/gitroot/seqware/seqware-distribution/target/seqware-distribution-%{SEQWARE_VERSION}-full.jar /home/seqware/jars/

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
# this is the DB actually used by people
sudo -u postgres psql --command "CREATE DATABASE seqware_meta_db WITH OWNER = seqware;"
sudo -u postgres psql seqware_meta_db < /vagrant/seqware_meta_db.sql
sudo -u postgres psql seqware_meta_db < /vagrant/seqware_meta_db_data.sql
# the testing DB
sudo -u postgres psql --command "CREATE DATABASE test_seqware_meta_db WITH OWNER = seqware;"
sudo -u postgres psql test_seqware_meta_db < /vagrant/seqware_meta_db.sql
sudo -u postgres psql test_seqware_meta_db < /vagrant/seqware_meta_db_data.sql

# stop tomcat6
/etc/init.d/tomcat6 stop

# remove landing page for tomcat
rm -rf /var/lib/tomcat6/webapps/ROOT

# seqware web service
cp /home/seqware/gitroot/seqware/seqware-webservice/target/seqware-webservice-%{SEQWARE_VERSION}.war /var/lib/tomcat6/webapps/SeqWareWebService.war
mv /vagrant/seqware-webservice-%{SEQWARE_VERSION}.xml /etc/tomcat6/Catalina/localhost/SeqWareWebService.xml

# seqware portal
cp /home/seqware/gitroot/seqware/seqware-portal/target/seqware-portal-%{SEQWARE_VERSION}.war /var/lib/tomcat6/webapps/SeqWarePortal.war
mv /vagrant/seqware-portal-%{SEQWARE_VERSION}.xml /etc/tomcat6/Catalina/localhost/SeqWarePortal.xml

# restart tomcat6
/etc/init.d/tomcat6 start

# seqware landing page
mv /vagrant/vm_landing/* /var/www/

# seqware tutorials
sudo mkdir /datastore
sudo chown seqware /datastore
# required for running oozie jobs
mkdir /usr/lib/hadoop-0.20-mapreduce/.seqware
cp /home/seqware/.seqware/settings /usr/lib/hadoop-0.20-mapreduce/.seqware/settings
chown -R mapred:mapred /usr/lib/hadoop-0.20-mapreduce/.seqware

# run full integration testing
su - seqware -c 'cd /home/seqware/gitroot/seqware; %{SEQWARE_IT_CMD} &> it.log'


