#!/bin/bash -vx

# prepare a location for the DCC portal
mkdir -p /usr/local/dcc-portal
cp /vagrant/settings.yml /usr/local/dcc-portal/
cd /usr/local/dcc-portal
# get the web app
#wget http://seqwaremaven.oicr.on.ca/artifactory/dcc-snapshot/org/icgc/dcc/dcc-portal-api/1.11-SNAPSHOT/dcc-portal-api-1.11-20130909.152040-159.jar
# FIXME: the above URL expired and was removed by Jenkins via some rotate mechanism so I'm storing a copy in S3
wget https://s3.amazonaws.com/ca.on.oicr.icgc/index_dumps/dcc-portal-api-1.11-20130912.203400-180.jar
# get the index
wget https://s3.amazonaws.com/ca.on.oicr.icgc/index_dumps/dcc-release-r--dev-06d-6-24.tar.gz
# load the index into elasticsearch
# NOTE: I've had problems with this in the past, where elasticsearch fails for some reason
curl -XPOST 'master:9200/dcc-release-r--dev-06d-6-24/_import?target=/usr/local/dcc-portal/dcc-release-r--dev-06d-6-24&millis=600000' &> es_load.log
# launch the portal
nohup java -Xmx4G -jar dcc-portal-api-1.11-20130912.203400-180.jar server settings.yml &

