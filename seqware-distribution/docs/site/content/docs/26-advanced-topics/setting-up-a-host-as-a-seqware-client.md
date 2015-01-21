---

title:                 "Setting up a host as a seqware client"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

This document will describe how to connect a host to a SeqWare client that is running inside a VirtualBox instance.

##1. Download the seqware script.
    The list of releases of SeqWare can be found here: <a href="https://github.com/SeqWare/seqware/releases">https://github.com/SeqWare/seqware/releases</a>
    In your browser, the URL for a specific version's script is: <a href="https://github.com/SeqWare/seqware/releases/download/1.1.0-beta.1/seqware">https://github.com/SeqWare/seqware/releases/doiwnload/1.1.0-beta.1/seqware</a>
    In your terminal, you can get it like this:
<pre>    
    curl -L https://github.com/SeqWare/seqware/releases/doiwnload/1.1.0-beta.1/seqware > seqware
</pre>
    Copy the script to a directory that is on your Path, such as `~/bin` or `/usr/bin`.
<pre>
    echo $PATH
    /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/usr/lib/jvm/java-7-oracle-cloudera
</pre>
<p class="warning"><strong>Note:</strong>SeqWare will require Java 7. If on a machine where you can directly upgrade Java, use your favorite package manager (apt-get or yum) to download Java 1.7 and update your java bin to point to the new location.

If installing on your own: Download 1.7 JRE either from Oracle (Do not use the OpenJDK implementation). Unzip it in a stable location, like your home directory or on the Isilon mount. Set JAVA_HOME to be the root directory, and JAVA_CMD to be the direct location of the java executable.
</p>
## 2. Remove your ~/.seqware folder, if it exists.
    If you have run SeqWare before on this macine, you will already have an existing ~/.seqware folder. If you don't want to delete this folder, you can rename it for now.
<pre>
    mv ~/.seqware ~./seqware.bk
</pre>
## 3. Initialize SeqWare
    SeqWare can now generate your configuration file once it is in your path. The correct webservice URL for the moment is below. You should refer to the page that I can't find right now for the most recent web service.
    Set the default workflow engine to Oozie:
<pre>
$ seqware init
 
Initializing Seqware settings:
  Seqware WebService URL: http://hsqwstage-www2.hpc.oicr.on.ca:8080/seqware-webservice-1.0.12
  Seqware WebService Username: admin@admin.com
  Seqware WebService Password: ****************
  Default Workflow Engine [oozie]: oozie-sge
 
Created Seqware settings file at /u/someUser/.seqware/settings
 
Seqware is ready to use!
</pre>
## 4. Edit the settings file
	If you are just scheduling workflows, you will not need to have anything else configured. But if you are either directly launching workflows or scheduling and then launching them on the same host, you will need the following in your seqware settings file.
    In this case, I'm launching with hsqwstage-node2.hpc . Substitute a VM as appropriate.
<pre>
OOZIE_SGE_MAX_MEMORY_PARAM_FORMAT=-l h_vmem=${maxMemory}M
OOZIE_SGE_THREADS_PARAM_FORMAT=
 
OOZIE_URL=http://hsqwstage-node2.hpc.oicr.on.ca:11000/oozie
OOZIE_APP_ROOT=seqware_workflow
OOZIE_APP_PATH=hdfs://hsqwstage-node2.hpc.oicr.on.ca:8020/user/mtaschuk/
OOZIE_JOBTRACKER=hsqwstage-node2.hpc.oicr.on.ca:8021
OOZIE_NAMENODE=hdfs://hsqwstage-node2.hpc.oicr.on.ca:8020
OOZIE_QUEUENAME=default
OOZIE_WORK_DIR=$HOME/tmp
FS.DEFAULTFS=hdfs://hsqwstage-node2.hpc.oicr.on.ca:8020
FS.HDFS.IMPL=org.apache.hadoop.hdfs.DistributedFileSystem
HBASE.ZOOKEEPER.QUORUM=hsqwstage-node2.hpc.oicr.on.ca
HBASE.ZOOKEEPER.PROPERTY.CLIENTPORT=2181
HBASE.MASTER=hsqwstage-node2.hpc.oicr.on.ca:60000
MAPRED.JOB.TRACKER=hsqwstage-node2.hpc.oicr.on.ca:8021
</pre>
## 5. Configure permissions to write to HDFS
    If launching workflows directly, you will need permission to write to the Oozie HDFS. If you do not have permission, you will errors like the following:
<pre>
pipe@cobalt:~$ seqware bundle launch --dir /.mounts/labs/PDE/exchange/provisioned-bundles/Workflow_Bundle_IlluminaTargetedSequencingPipeline_1.0_SeqWare_1.0.11 \
> --ini /.mounts/labs/PDE/exchange/provisioned-bundles/Workflow_Bundle_IlluminaTargetedSequencingPipeline_1.0_SeqWare_1.0.11/Workflow_Bundle_IlluminaTargetedSequencingPipeline/1.0/config/workflow.ini,$MY_INI
Performing launch of workflow 'IlluminaTargetedSequencingPipeline' version '1.0'
Module caught exception during method: do_run:null
...
Caused by: java.lang.RuntimeException: org.apache.hadoop.security.AccessControlException: Permission denied: user=pipe, access=WRITE, inode="/user":hdfs:super
group:drwxr-xr-x
</pre>
    To resolve this, send an email to HelpDesk requesting access to the location of the FS.DEFAULTFS from your seqware settings file (in this case, hdfs://hsqwstage-node2.hpc.oicr.on.ca:8020).