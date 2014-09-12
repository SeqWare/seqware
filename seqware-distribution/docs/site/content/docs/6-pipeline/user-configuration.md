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

For versions of the oozie-sge plugin from 1.0.3 onwards, two kinds of error codes are possible. Error codes of the form SGE[0-9]+ refer to the exit status of the actual Bash scripts that form steps in your workflows. Error codes of the form SGEF[0-9]+ refer to the failure code of the SGE infrastructure itself. 

For example, the following output from "qacct -j" refers to a workflow step which failed with an error code of 1 (which would correspond to SGE1 for the Oozie XML parameter above). 

	$ qacct -j 3702
	==============================================================
	qname        main.q              
	hostname     master           
	group        seqware               
	owner        seqware               
	project      NONE                
	department   defaultdepartment   
	jobname      annotate_5          
	jobnumber    3702                
	taskid       undefined
	account      sge                 
	priority     0                   
	qsub_time    Fri Aug 29 16:40:08 2014
	start_time   Fri Aug 29 16:40:20 2014
	end_time     Fri Aug 29 16:40:21 2014
	granted_pe   NONE                
	slots        1                   
	failed       0    
	exit_status  1                   
	ru_wallclock 1            
	ru_utime     1.468        
	ru_stime     0.072        
	ru_maxrss    112212              
	ru_ixrss     0                   
	ru_ismrss    0                   
	ru_idrss     0                   
	ru_isrss     0                   
	ru_minflt    42375               
	ru_majflt    0                   
	ru_nswap     0                   
	ru_inblock   0                   
	ru_oublock   168                 
	ru_msgsnd    0                   
	ru_msgrcv    0                   
	ru_nsignals  0                   
	ru_nvcsw     726                 
	ru_nivcsw    269                 
	cpu          1.540        
	mem          0.306             
	io           0.006             
	iow          0.000             
	maxvmem      557.734M
	arid         undefined

The following output from "qacct -j" refers to a workflow step where the actual qsub failed since a logging directory was unavailable (leading to a Eqw state). This would correspond to an Oozie error code of SGEF26. 

	$ qacct -j 3801
	==============================================================
	qname        main.q              
	hostname     master           
	group        seqware               
	owner        seqware               
	project      NONE                
	department   defaultdepartment   
	jobname      start_0             
	jobnumber    3801                
	taskid       undefined
	account      sge                 
	priority     0                   
	qsub_time    Fri Sep 12 15:03:02 2014
	start_time   -/-
	end_time     -/-
	granted_pe   NONE                
	slots        1                   
	failed       26  : opening input/output file
	exit_status  0                   
	ru_wallclock 0            
	ru_utime     0.000        
	ru_stime     0.000        
	ru_maxrss    0                   
	ru_ixrss     0                   
	ru_ismrss    0                   
	ru_idrss     0                   
	ru_isrss     0                   
	ru_minflt    0                   
	ru_majflt    0                   
	ru_nswap     0                   
	ru_inblock   0                   
	ru_oublock   0                   
	ru_msgsnd    0                   
	ru_msgrcv    0                   
	ru_nsignals  0                   
	ru_nvcsw     0                   
	ru_nivcsw    0                   
	cpu          0.000        
	mem          0.000             
	io           0.000             
	iow          0.000             
	maxvmem      0.000
	arid         undefined
 
