#!/bin/bash -vx

# basic tools
export DEBIAN_FRONTEND=noninteractive
apt-get update
apt-get install curl unzip -y

# add seqware user
useradd -d /home/seqware -m seqware -s /bin/bash

# install the hadoop repo
wget -q http://archive.cloudera.com/cdh4/one-click-install/precise/amd64/cdh4-repository_1.0_all.deb
dpkg -i cdh4-repository_1.0_all.deb
curl -s http://archive.cloudera.com/cdh4/ubuntu/precise/amd64/cdh/archive.key | sudo apt-key add -

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
#apt-get -q -y --force-yes install oracle-j2sdk1.6 cloudera-manager-server-db cloudera-manager-server cloudera-manager-daemons
#apt-get -q -y --force-yes install oracle-j2sdk1.6 hadoop-0.20-conf-pseudo hue hue-server hue-plugins oozie oozie-client postgresql-9.1 postgresql-client-9.1 tomcat6-common tomcat6 apache2 git maven sysv-rc-conf hbase-master xfsprogs
# get Java
apt-get -q -y --force-yes install libasound2 libxi6 libxtst6 libxt6 
wget http://archive.cloudera.com/cm4/ubuntu/precise/amd64/cm/pool/contrib/o/oracle-j2sdk1.6/oracle-j2sdk1.6_1.6.0+update31_amd64.deb
dpkg -i oracle-j2sdk1.6_1.6.0+update31_amd64.deb

