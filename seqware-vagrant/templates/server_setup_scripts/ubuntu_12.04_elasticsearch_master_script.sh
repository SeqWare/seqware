#!/bin/bash -vx

# basic tools
export DEBIAN_FRONTEND=noninteractive
apt-get update

echo "elasticsearch - nofile  65535
elasticsearch - memlock unlimited
" > /etc/security/limits.d/elasticsearch.conf

# install elasticsearch
wget https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-0.90.1.deb
dpkg -i elasticsearch-0.90.1.deb

# for backup/restore
/usr/share/elasticsearch/bin/plugin -remove knapsack
/usr/share/elasticsearch/bin/plugin -url http://dl.bintray.com/jprante/elasticsearch-plugins/org/xbib/elasticsearch/plugin/elasticsearch-knapsack/2.0.0/elasticsearch-knapsack-2.0.0.zip?direct -install knapsack

# display
/usr/share/elasticsearch/bin/plugin -remove mobz/elasticsearch-head
/usr/share/elasticsearch/bin/plugin -install mobz/elasticsearch-head

# fix memory TODO: need to make this an option!
perl -pi -e 's/\#ES_HEAP_SIZE=2g/ES_HEAP_SIZE=8g/' /etc/init.d/elasticsearch
perl -pi -e 's/\#bootstrap.mlockall: true/bootstrap.mlockall: true/' /etc/elasticsearch/elasticsearch.yml
/etc/init.d/elasticsearch restart

