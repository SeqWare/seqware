#!/bin/bash -vx

# basic tools
apt-get install curl unzip -y

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

# make everything owned by seqware
chown -R seqware:seqware /home/seqware

