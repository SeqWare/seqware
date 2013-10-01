---

title:                 "SeqWare Settings"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---


## Overview

The SeqWare jar file uses a simple configuration file that has been setup for
you already on the VM. By default the location is ~/.seqware/settings. You can
control this location using an environment variable:

	SEQWARE_SETTINGS=~/.seqware/settings

This file contains the web address of the RESTful web service, your username
and password, and you Amazon public and private keys that will allow you to
push and pull data files to and from the cloud, etc.  Here is the example
settings file from the VM, this will be ready to work on the VM but keep in
mind, this is where you would change settings if you, for example, setup the
Web Service and MetaDB on another server or you launched a VM on the cloud and
wanted to use the local VM command line jar to control the remote server.
Another common thing you may want to do is use the ProvisionFiles module
(described later) to push and pull data into/out of the cloud. This is the file
where you would supply your access and secret keys that you got when signing up
for Amazon (keep those safe!). For this tutorial the config file available on the VM should be
ready to go, you will not need to modify it.

Note that the sections for the Oozie Workflow Engine, General Hadoop, Query
Engine, and Amazon Cloud Settings are all optional, so they do not need to be
filled in for every deployment of SeqWare, just those using these tools. Also note that the
settings file needs to have read and write permissions for only the owner for security reasons. 
Our tools will abort and refuse to run if this is not set. 

<pre><code>#!ini

# SEQWARE PIPELINE SETTINGS

# The settings in this file are tagged by when they are used.
# COMMON: Used by all components
# INSTALL: Used when installing a workflow bundle
# SCHEDULE: Used when a user wants to schedule a workflow run
# LAUNCH: Used when a workflow run is to be launched
#
# Remote users need COMMON and SCHEDULE.
# Workflow developers need COMMON and LAUNCH for testing.
# Administrators need COMMON and INSTALL.
# Cronjobs/daemon processes will need COMMON and LAUNCH.


# COMMON

# SeqWare MetaDB communication method, can be 'database' or 'webservice' or 'none'
SW_METADATA_METHOD=webservice

# SeqWare webservice settings. Only used if SW_METADATA_METHOD=webservice
SW_REST_URL=http://localhost:8080/SeqWareWebService
SW_REST_USER=admin@admin.com
SW_REST_PASS=admin

# SeqWare database settings. Only used if SW_METADATA_METHOD=database
SW_DB_USER=seqware
SW_DB_PASS=seqware
SW_DB_SERVER=localhost
SW_DB=seqware_meta_db

# Amazon cloud settings. Only used if reading and writing to S3 buckets.
AWS_ACCESS_KEY=FILLMEIN
AWS_SECRET_KEY=FILLMEIN


# SCHEDULE, LAUNCH

# the default engine to use if otherwise unspecified (one of: oozie, oozie-sge, pegasus)
SW_DEFAULT_WORKFLOW_ENGINE=oozie


# INSTALL, LAUNCH

# The directory containing bundle directories (into which bundle archives are unzipped)
SW_BUNDLE_DIR=/home/seqware/SeqWare/provisioned-bundles


# INSTALL

# The directory containing bundle archives (into which a bundle archive is first copied during install)
SW_BUNDLE_REPO_DIR=/home/seqware/SeqWare/released-bundles


# LAUNCH

# Oozie engine settings. Only used for both 'oozie' and 'oozie-sge' engines.
OOZIE_URL=http://localhost:11000/oozie
OOZIE_APP_ROOT=seqware_workflow
OOZIE_APP_PATH=hdfs://localhost:8020/user/seqware/
OOZIE_JOBTRACKER=localhost:8021
OOZIE_NAMENODE=hdfs://localhost:8020
OOZIE_QUEUENAME=default
OOZIE_WORK_DIR=/usr/tmp/seqware-oozie
MAPRED.JOB.TRACKER=localhost:8021
FS.DEFAULTFS=hdfs://localhost:8020
FS.HDFS.IMPL=org.apache.hadoop.hdfs.DistributedFileSystem

# Oozie-SGE engine settings. Only used for 'oozie-sge' engine.
## Format of qsub flag for specifying number of threads.
## If present, ${threads} will be replaced with the job-specific value.
OOZIE_SGE_THREADS_PARAM_FORMAT=-pe serial ${threads}
## Format of qsub flag for specifying the max memory.
## If present, ${maxMemory} will be replaced with the job-specific value.
OOZIE_SGE_MAX_MEMORY_PARAM_FORMAT=-l h_vmem=${maxMemory}M

# Pegasus engine settings.  Only used for 'pegasus' engine.
## the name of the cluster as defined in the Pegasus sites.xml config file
SW_CLUSTER=seqwarevm
## the directory used to store the generated DAX workflow documents before submission to the cluster
SW_DAX_DIR=/home/seqware/SeqWare/pegasus-dax
## the directory containing all the Pegasus config files this instance of SeqWare should use
SW_PEGASUS_CONFIG_DIR=/home/seqware/.seqware/pegasus
</code></pre>

## Pegasus Workflow Engine Configuration

The SeqWare Pipeline project can (currently) use two workflow engines: 1) the Pegasus/Condor/Globus/SGE engine or 2) the Oozie/Hadoop engine. Each requires a bit of additional information to make them work (and, obviously, the underlying cluster tools correctly installed and configured).  For the Pegasus engine you need a few extra files, referenced by the SW_PEGASUS_CONFIG_DIR parameter above:

### sites.xml3

<!-- see http://www.opinionatedgeek.com/DotNet/Tools/HTMLEncode/encode.aspx -->

<pre><code>#!xml
&lt;sitecatalog xmlns=&quot;http://pegasus.isi.edu/schema/sitecatalog&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xsi:schemaLocation=&quot;http://pegasus.isi.edu/schema/sitecatalog http://pegasus.isi.edu/schema/sc-3.0.xsd&quot; version=&quot;3.0&quot;&gt;
        &lt;site  handle=&quot;local&quot; arch=&quot;x86_64&quot; os=&quot;LINUX&quot; osrelease=&quot;&quot; osversion=&quot;&quot; glibc=&quot;&quot;&gt;
                &lt;grid  type=&quot;gt5&quot; contact=&quot;seqwarevm/jobmanager-fork&quot; scheduler=&quot;Fork&quot; jobtype=&quot;auxillary&quot;/&gt;
                &lt;grid  type=&quot;gt5&quot; contact=&quot;seqwarevm/jobmanager-sge&quot; scheduler=&quot;SGE&quot; jobtype=&quot;compute&quot;/&gt;
                &lt;head-fs&gt;
                        &lt;scratch&gt;
                                &lt;shared&gt;
                                        &lt;file-server protocol=&quot;gsiftp&quot; url=&quot;gsiftp://seqwarevm&quot; mount-point=&quot;/home/seqware/SeqWare/pegasus-working&quot;/&gt;
                                        &lt;internal-mount-point mount-point=&quot;/home/seqware/SeqWare/pegasus-working&quot;/&gt;
                                &lt;/shared&gt;
                        &lt;/scratch&gt;
                        &lt;storage&gt;
                                &lt;shared&gt;
                                        &lt;file-server protocol=&quot;gsiftp&quot; url=&quot;gsiftp://seqwarevm&quot; mount-point=&quot;/&quot;/&gt;
                                        &lt;internal-mount-point mount-point=&quot;/&quot;/&gt;
                                &lt;/shared&gt;
                        &lt;/storage&gt;
                &lt;/head-fs&gt;
                &lt;replica-catalog  type=&quot;LRC&quot; url=&quot;rlsn://smarty.isi.edu&quot;/&gt;
                &lt;profile namespace=&quot;env&quot; key=&quot;GLOBUS_LOCATION&quot;&gt;/usr&lt;/profile&gt;
                &lt;profile namespace=&quot;env&quot; key=&quot;JAVA_HOME&quot;&gt;/usr/java/default&lt;/profile&gt;
                &lt;!--profile namespace=&quot;env&quot; key=&quot;LD_LIBRARY_PATH&quot;&gt;/.mounts/labs/seqware/public/globus/default/lib&lt;/profile--&gt;
                &lt;profile namespace=&quot;env&quot; key=&quot;PEGASUS_HOME&quot;&gt;/opt/pegasus/3.0&lt;/profile&gt;
        &lt;/site&gt;
        &lt;site  handle=&quot;seqwarevm&quot; arch=&quot;x86_64&quot; os=&quot;LINUX&quot; osrelease=&quot;&quot; osversion=&quot;&quot; glibc=&quot;&quot;&gt;
                &lt;grid  type=&quot;gt5&quot; contact=&quot;seqwarevm/jobmanager-fork&quot; scheduler=&quot;Fork&quot; jobtype=&quot;auxillary&quot;/&gt;
                &lt;grid  type=&quot;gt5&quot; contact=&quot;seqwarevm/jobmanager-sge&quot; scheduler=&quot;SGE&quot; jobtype=&quot;compute&quot;/&gt;
                &lt;head-fs&gt;
                        &lt;scratch&gt;
                                &lt;shared&gt;
                                        &lt;file-server protocol=&quot;gsiftp&quot; url=&quot;gsiftp://seqwarevm&quot; mount-point=&quot;/home/seqware/SeqWare/pegasus-working&quot;/&gt;
                                        &lt;internal-mount-point mount-point=&quot;/home/seqware/SeqWare/pegasus-working&quot;/&gt;
                                &lt;/shared&gt;
                        &lt;/scratch&gt;
                        &lt;storage&gt;
                                &lt;shared&gt;
                                        &lt;file-server protocol=&quot;gsiftp&quot; url=&quot;gsiftp://seqwarevm&quot; mount-point=&quot;/&quot;/&gt;
                                        &lt;internal-mount-point mount-point=&quot;/&quot;/&gt;
                                &lt;/shared&gt;
                        &lt;/storage&gt;
                &lt;/head-fs&gt;
                &lt;replica-catalog  type=&quot;LRC&quot; url=&quot;rlsn://smarty.isi.edu&quot;/&gt;
                &lt;profile namespace=&quot;env&quot; key=&quot;GLOBUS_LOCATION&quot;&gt;/usr&lt;/profile&gt;
                &lt;profile namespace=&quot;env&quot; key=&quot;JAVA_HOME&quot;&gt;/usr/java/default&lt;/profile&gt;
                &lt;!--profile namespace=&quot;env&quot; key=&quot;LD_LIBRARY_PATH&quot;&gt;/.mounts/labs/seqware/public/globus/default/lib&lt;/profile--&gt;
                &lt;profile namespace=&quot;env&quot; key=&quot;PEGASUS_HOME&quot;&gt;/opt/pegasus/3.0&lt;/profile&gt;
        &lt;/site&gt;
&lt;/sitecatalog&gt;
</code></pre>

This file is from Pegasus and the handle="clustername" is how you tell SeqWare which cluster to submit to. The setup of cluster resources in the sites.xml3 file is beyond the scope of SeqWare so we refer you to the [Pegasus documentation](http://pegasus.isi.edu/).

### properties

<pre><code>#!ini
##########################
# PEGASUS USER PROPERTIES
##########################

## SELECT THE REPLICA CATALOG MODE AND URL
pegasus.catalog.replica = SimpleFile
pegasus.catalog.replica.file = /home/seqware/.seqware/pegasus/rc.data

## SELECT THE SITE CATALOG MODE AND FILE
pegasus.catalog.site = XML3
pegasus.catalog.site.file = /home/seqware/.seqware/pegasus/sites.xml3


## SELECT THE TRANSFORMATION CATALOG MODE AND FILE
pegasus.catalog.transformation = File
pegasus.catalog.transformation.file = /home/seqware/.seqware/pegasus/tc.data

## USE DAGMAN RETRY FEATURE FOR FAILURES
dagman.retry=1

## STAGE ALL OUR EXECUTABLES OR USE INSTALLED ONES
pegasus.catalog.transformation.mapper = All

## CHECK JOB EXIT CODES FOR FAILURE
pegasus.exitcode.scope=all

## OPTIMIZE DATA & EXECUTABLE TRANSFERS
pegasus.transfer.refiner=Bundle
pegasus.transfer.links = true

# JOB Priorities
pegasus.job.priority=10
pegasus.transfer.*.priority=100

#JOB CATEGORIES
pegasus.dagman.projection.maxjobs=2
</code></pre>

The Pegasus properties file controls where the sites.xml3 file lives and a few
other Pegasus parameters (our tc.data and rc.data files in SeqWare are empty).
The most important parameter above is "dagman.retry=1" which controls how many
attempts should be made before job is considered failed in a workflow.  In this
example "1" means it should be retried once before failing.  There are other
parameters that might be useful for Pegasus, see the [Pegasus
documentation](http://pegasus.isi.edu/) for more information.


## Oozie Workflow Engine Configuration

The alternative, Oozie (Hadoop) workflow engine only uses configurations in the
users ~/.seqware/settings file.  No other configuration is required on the user
side.
