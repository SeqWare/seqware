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

The format for the settings file is based on [Java properties files](http://docs.oracle.com/javase/6/docs/api/java/util/Properties.html#load%28java.io.Reader%29). 

<pre><code>#!ini

<%= render '/includes/settings/' %>

</code></pre>

## Oozie Workflow Engine Configuration

In addition to the the user's ~/.seqware/settings file the only other configuration is that required for 
automatic retry. Like the Pegasus workflow engine, it is possible to control the number of attempts
that should be made before a job is considered failed in a workflow. 

Edit the Oozie site XML and add and/or add to the error codes that are listed. 

        <property>
            <name>oozie.service.LiteWorkflowStoreService.user.retry.error.code.ext</name>
            <value>SGE137</value>
        </property>
        <property>
            <name>oozie.service.LiteWorkflowStoreService.user.retry.max</name>
            <value>30</value>
        </property>

After restarting Oozie, Oozie will use the listed error codes in combination with the OOZIE_RETRY_MAX parameter to determine how many times steps will 
be retried in case of a specific error. For example, in the above jobs that return with an SGE error code of SGE137 will automatically be retried 30 or 
OOZIE_RETRY_MAX times, whatever is higher. The actual error codes will likely be dependent on your site. 

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
