---

title:                 "Monitoring Oozie"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

#Installing the Oozie client.
1. Add the cdh4 repository (cdh4 guide: http://www.cloudera.com/content/cloudera-content/cloudera-docs/CDH4/4.2.0/CDH4-Installation-Guide/cdh4ig_topic_4_4.html) to your sources list (/etc/apt/sources.list.d/cloudera.list)
<pre>
    deb [arch=amd64] http://archive.cloudera.com/cdh4/ubuntu/precise/amd64/cdh precise-cdh4 contrib
    deb-src http://archive.cloudera.com/cdh4/ubuntu/precise/amd64/cdh precise-cdh4 contrib
</pre>
2. Install the oozie client:
<pre>
    sudo apt-get update
    sudo apt-get install oozie-client
</pre>

#Configuring the Oozie client
1. Add oozie host and timezone to ~/.bashrc:
<pre>
    export OOZIE_URL="http://localhost:11000/oozie"
    export OOZIE_TIMEZONE="EST"
</pre>
2. Source your bashrc file:
<pre>
    source ~/.bashrc
</pre>
3. Test your configuration:
<pre>
    user@localhost:~$ oozie admin -version
    Oozie server build version: 3.3.2-cdh4.5.0
</pre>

#Commands to query Oozie:
1. List top 100 jobs:
<pre>
    user@localhost:~$ oozie jobs
</pre>
2. List top 5 (or any other specific number) jobs:
<pre>
    user@localhost:~$ oozie jobs -len 5
</pre>
3. Get detailed information on an oozie job:
<pre>
    user@localhost:~$ oozie job -info <OOZIE_JOB_ID> [-verbose]
</pre>
4. Get detailed information on an oozie action:
<pre>
    user@localhost:~$ oozie job -info <OOZIE_ACTION_ID>
</pre>
5. Get the log for an oozie job:
<pre>
    user@localhost:~$ oozie job -log <OOZIE_JOB_ID>
</pre>
6. Terminating an oozie job:
<pre>
    user@localhost:~$ oozie job -kill <OOZIE_JOB_ID>
</pre>