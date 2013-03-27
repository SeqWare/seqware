---

title:                 "SeqWare Pipeline User Configuration"
toc_includes_sections: true
markdown:              basic

---


## Overview

The SeqWare jar file uses a simple configuration file that has been setup for you already on the VM. By default the location is ~/.seqware/settings.

This file contains the web address of the RESTful web service, your username and password, and you Amazon public and private keys that will allow you to push and pull data files to and from the cloud, etc.  Here is the example settings file from the VM, this will be ready to work on the VM but keep in mind, this is where you would change settings if you, for example, setup the Web Service and MetaDB on another server or you launched a VM on the cloud and wanted to use the local VM command line jar to control the remote server.  Another common thing you may want to do is use the ProvisionFiles module (described later) to push and pull data into/out of the cloud. This is the file where you would supply your access and secret keys that you got when signing up for Amazon (keep those safe!). For this tutorial the config file should be ready to go, you will not need to modify it.

Note that the sections for the Oozie Workflow Engine, General Hadoop, Query Engine, and Amazon Cloud Settings are all optional, so they do not need to be filled in for every deployment of SeqWare.

<pre>
#
# SEQWARE PIPELINE SETTINGS
#
# the name of the cluster as defined in the Pegasus sites.xml config file
SW_CLUSTER=seqwarevm
# the directory used to store the generated DAX workflow documents before submission to the cluster
SW_DAX_DIR=/home/seqware/SeqWare/pegasus-dax
# the directory containing all the Pegasus config files this instance of SeqWare should use
SW_PEGASUS_CONFIG_DIR=/home/seqware/.seqware/pegasus
# SeqWare MetaDB communication method, can be "database" or "webservice" or "none"
SW_METADATA_METHOD=webservice
# a directory to copy bundles to for archiving/installing
SW_BUNDLE_DIR=/home/seqware/SeqWare/provisioned-bundles
# the central repository for installed bundles
SW_BUNDLE_REPO_DIR=/home/seqware/SeqWare/released-bundles
#
# SEQWARE WEBSERVICE SETTINGS
#
# the base URL for the RESTful SeqWare API
SW_REST_URL=http://localhost:8080/SeqWareWebService
# the username and password to connect to the REST API, this is used by SeqWare Pipeline to write back processing info to the DB
SW_REST_USER=admin@admin.com
SW_REST_PASS=admin
#
# SEQWARE DATABASE SETTINGS
#
SW_DB_USER=seqware
SW_DB_PASS=seqware
SW_DB_SERVER=localhost
SW_DB=test_seqware_meta_db
#
# AMAZON CLOUD SETTINGS
# used by tools reading and writing to S3 buckets (dependency data/software bundles, inputs, outputs, etc)
#
AWS_ACCESS_KEY=FILLMEIN
AWS_SECRET_KEY=FILLMEIN
#
# SEQWARE PIPELINE
# OOZIE WORKFLOW ENGINE SETTINGS
#
OOZIE_URL=http://localhost:11000/oozie
OOZIE_APP_ROOT=seqware_workflow
OOZIE_APP_PATH=hdfs://localhost:8020/user/seqware/
OOZIE_JOBTRACKER=localhost:8021
OOZIE_NAMENODE=hdfs://localhost:8020
OOZIE_QUEUENAME=default
OOZIE_WORK_DIR=/usr/tmp/seqware-oozie
#
# SEQWARE QUERY ENGINE
#
HBASE.ZOOKEEPER.QUORUM=localhost
HBASE.ZOOKEEPER.PROPERTY.CLIENTPORT=2181
HBASE.MASTER=localhost:60000
#
# SEQWARE GENERAL HADOOP SETTINGS
#
MAPRED.JOB.TRACKER=localhost:8021
FS.DEFAULT.NAME=hdfs://localhost:8020
FS.DEFAULTFS=hdfs://localhost:8020
FS.HDFS.IMPL=org.apache.hadoop.hdfs.DistributedFileSystem
</pre>


