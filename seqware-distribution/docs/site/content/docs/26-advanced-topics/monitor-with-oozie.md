---

title:                 "Monitoring Oozie"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

##Installing the Oozie client.
1. Add the cdh4 repository (detailed cdh4 guide is here:<a href="http://www.cloudera.com/content/cloudera-content/cloudera-docs/CDH4/4.2.0/CDH4-Installation-Guide/cdh4ig_topic_4_4.html">http://www.cloudera.com/content/cloudera-content/cloudera-docs/CDH4/4.2.0/CDH4-Installation-Guide/cdh4ig_topic_4_4.html</a>) to your sources list (`/etc/apt/sources.list.d/cloudera.list`)
   <pre><code>
deb [arch=amd64] http://archive.cloudera.com/cdh4/ubuntu/precise/amd64/cdh precise-cdh4 contrib
deb-src http://archive.cloudera.com/cdh4/ubuntu/precise/amd64/cdh precise-cdh4 contrib
</code></pre>

2. Install the oozie client:
   <pre><code>#!bash
sudo apt-get update
sudo apt-get install oozie-client
</code></pre>

##Configuring the Oozie client
1. Add oozie host and timezone to ~/.bashrc:
   <pre><code>#!bash
export OOZIE_URL="http://localhost:11000/oozie"
export OOZIE_TIMEZONE="EST" </code></pre>
2. Source your bashrc file:
   <pre><code>#!bash
   source ~/.bashrc </code></pre>
3. Test your configuration:
   <pre><code>#!console
user@localhost:~$ oozie admin -version
Oozie server build version: 3.3.2-cdh4.5.0 </code></pre>

##Commands to query Oozie:
1. List top 100 jobs:
   <pre><code>#!console
user@localhost:~$ oozie jobs</code></pre>
2. List top 5 (or any other specific number) jobs:
   <pre><code>#!console
user@localhost:~$ oozie jobs -len 5</code></pre>
3. Get detailed information on an oozie job:
   <pre><code>#!console
user@localhost:~$ oozie job -info &lt;OOZIE_JOB_ID&gt; [-verbose]</code></pre>
4. Get detailed information on an oozie action:
   <pre><code>#!console
user@localhost:~$ oozie job -info &lt;OOZIE_ACTION_ID&gt; </code></pre>
5. Get the log for an oozie job:
   <pre><code>#!console
user@localhost:~$ oozie job -log &lt;OOZIE_JOB_ID&gt; </code></pre>

##Terminating an oozie job:
First, use get the job ID by finding it in the list from `oozie jobs` or the details of `oozie job -info <OOZIE_JOB_ID>`. Then kill the job:
   <pre><code>#!console
user@localhost:~$ oozie job -kill &lt;OOZIE_JOB_ID&gt; </code></pre>

##Restarting a terminated Oozie job
1. You will need a "job.properties" file which is used to configure the job when it resumes. If you don't have one, you can create one. Here is an example:
   <pre><code>#!properties
nameNode=hdfs://hsqwstage-node2.hpc.oicr.on.ca:8020
jobTracker=hsqwstage-node2.hpc.oicr.on.ca:8021
queueName=default
oozie.wf.application.path=<OOZIE APP PATH, eg: hdfs://hsqwstage-node2.hpc.oicr.on.ca:8020/user/mlaszloffy/seqware_workflow/oozie-da66ddd3-1f1b-4441-8f04-658db9a8644a>
oozie.wf.rerun.failnodes=true
</code></pre>
2. Re-run the oozie job using the properties file:
   <pre><code>#!console
user@localhost:~$ oozie job -rerun &lt;OOZIE_JOB_ID&gt; -config job.properties
</code></pre>

##Resuming a suspended Oozie job
1. It's probably a good idea to first try to determine why the job was suspended. You can chekc the log, or the job info:
   <pre><code>#!console
user@localhost:~$ oozie job -info &lt;OOZIE_JOB_ID&gt; [-verbose]</code></pre>
Or:
   <pre><code>#!console
user@localhost:~$ oozie job -log &lt;OOZIE_JOB_ID&gt; </code></pre>
2. Resume the oozie job:
   <pre><code>#!console
user@localhost:~$ oozie job -resume &lt;OOZIE_JOB_ID&gt; </code></pre>
  