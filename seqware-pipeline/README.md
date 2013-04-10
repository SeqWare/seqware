<p class="warning"><strong>Note:</strong> This guide is useful if you want to install SeqWare from scratch on your local infrastructure. Be warned, there are rather vague sections and this guide was really written as notes for setting up our VMs. It is geared towards experienced Linux sysadmins and we cannot guarantee this guide is always up to date. </p>

If you just want to get started with SeqWare quickly please see [Installing the SeqWare VM](/docs/2-installation/#installing-with-a-local-vm) for instructions on downloading a VM that is ready for workflow development and testing. We also recommend this approach for production installs because you can connect easily these VMs to a real cluster.

<!-- For the previous version of these directions please see [[Creating a SeqWare VM v1]] and [[Creating a SeqWare VM v2]] and [[Upgrade SeqWare VM to Globus 5.2]] for information about upgrading to the latest version of Globus (this guide below is up-to-date). -->

## Introduction

The directions below are focused on creating a virtual machine using VirtualBox that uses the latest version of the various SeqWare sub-projects and dependencies including Pegasus 3.0. The major goal here is to make sure I have very clear directions on how to:

* setup a VM using VirtualBox with Pegasus 3.0, Condor 7.6.x, and Globus 5.2.x
* take that setup and configure it to either
** submit jobs to itself (the default configuration)
** submit jobs to a real cluster e.g. deploy this VM as a "real" SeqWare install

The installation and configuration of this software is a fairly complex process.  Even if you don't use this VM these directions will be extremely useful for correctly installing and configuring Pegasus and it's dependencies for use with SeqWare Pipeline.  I recommend setting up in a VM environment and validating that works before trying to setup this software on a real server. Actually, I don't recommend setting this up on a real server, using a VM for both development and production is actually a really good idea since failed servers can quickly be replaced with VM snapshots. This alone makes running a VM across the board appealing. VirtualBox VMs can be converted easily to KVM VMs for use in a server environment. '''The recommended route for installing SeqWare is to download this VM (see [Installing the SeqWare VM](/docs/2-installation/#installing-with-a-local-vm)) and deploying it as a production machine, skipping the difficult dependency install all together'''.

The VM currently has the following SeqWare dependencies installed (from lowest to highest-level):

* [SGE](http://www.oracle.com/us/products/tools/oracle-grid-engine-075549.html) version 6.2.x (note, this is now Oracle Grid Engine but we'll use the existing gridengine RPM packages): this is the cluster scheduler that actually schedules jobs out to real nodes, managing memory and CPUs available. The VM has only one "slot" available.
* [The Globus Toolkit](http://www.globus.org/toolkit/) version 5.2.x: provides user authentication, transport protocol for sending jobs to a cluster in a scheduler (GRAM5), and a transfer protocol for sending data around (GridFTP)
** GRAM5
** GridFTP
* [Condor](http://www.cs.wisc.edu/condor/) version 7.6.x: this provides a virtual cluster that jobs can be submitted to and parsed out to one or more real clusters. It is the component that interprets the job dependencies and submits jobs in the correct order to the real cluster.
* [Pegasus WMS](http://pegasus.isi.edu/wms/) version 3.0.x: this project provides a workflow language and various convenience configuration files that simplifies the use of the other tools in the stack. It is the only software project in the stack that SeqWare is "aware" of and directly depends on.

The VM currently has the following SeqWare sub-projects installed:

* [SeqWare Portal](/docs/5-portal/)
* [SeqWare Pipeline](/docs/6-pipeline/)
* [SeqWare Query Engine](/docs/8-query-engine/)
* [SeqWare MetaDB](/docs/4-metadb/)

## Create a CentOS VM

The first step is to create a CentOS VM using [VirtualBox](http://www.virtualbox.org/)  (or whatever virtualization technology your prefer). If you don't know how to do this then you should use the pre-fab virtual machine, see [Using the SeqWare VM](/docs/2-installation/#installing-with-a-local-vm).

The steps to creating a VM are:

* create a new VM using CentOS 6 64-bit ISO
* setup networking using a static IP address if possible
* create a seqware user account with seqware as the password, set the root account password to seqware
* set the hostname via /etc/hosts and /etc/sysconfig/network, here we will use seqwarevm
* create a separate datastore volume for large files, create various dirs (tmp, references, etc), add it to /etc/fstab so it automounts (optional)
* make sure to setup the kernel devel package (e.g. yum install kernel-devel-2.6.18-194.el6 or similar), mount the VirtualBox Guest Additions ISO and call their script to setup guest additions on this VM
* I turned off the firewall (/etc/init.d/iptables stop) which is fine for a local VM. But in production you will want to use the proper firewall settings and turn it back on.  Google for GRAM/GridFTP/SGE ports that need to be open.
* I used chkconfig to ensure the SGE, Condor, Globus, and any other desired services run on startup

Once you've setup the base OS take a snapshot in case you need to roll back.  '''You should take snapshots throughout the install process so if you mess up you can roll back'''. When you finish and the entire install is working make sure you take periodic snapshots for use in disaster recovery for your production systems.

The VirtualBox VMs are great for running locally for testing and development but can also be converted into KVM or another more server-oriented VM technology. These directions are outside the scope of this guide, however. Google for more information.

## Install Dependencies

Now that you have a VM you will want to log in as root, configure the yum repositories, and install some base packages. 

### Setup Repositories

See the following sites for info on setting up 3rd party yum repositories, you will want to have these repos installed before you continue.

* http://fedoraproject.org/wiki/EPEL
* http://www.cs.wisc.edu/condor/yum/
* http://www.globus.org/toolkit/downloads

### Install Java

Install the Oracle JVM from [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk6-downloads-1637591.html)  (we test on JDK 1.6.x), install it's RPMs, and configure using alternatives:

	rpm -Uvh jdk-6u20-linux-amd64.rpm
	alternatives --install /usr/bin/java java /usr/java/latest/bin/java 20000
	alternatives --install /usr/bin/javaws javaws /usr/java/latest/bin/javaws 20000
	alternatives --install /usr/bin/javac javac /usr/java/latest/bin/javac 20000

### Install Maven

Unfortunately they do not provide a yum repo for this.

	wget http://apache.mirror.iweb.ca/maven/binaries/apache-maven-2.2.1-bin.tar.gz

Unzip this to /usr/local then setup a /etc/profile.d/maven.sh file for config:

	export M2_HOME=/usr/local/apache-maven-2.2.1
	export M2=$M2_HOME/bin
	export PATH=$M2:$PATH

This will ensure users have maven setup correctly.

### Installing Dependencies

Once you have the VM setup the next step is to install the various prerequisites. As root:

	yum -y install postgresql-server postgresql tomcat6 tomcat6-servlet-2.5-api tomcat6-admin-webapps 

### Other Packages

We will install other packages later in this install guide.

## Configuring Linux ##

I found that when running large clusters there seemed to be a large number of failures, mainly related to files going missing on gluster/NFS and with too many file handles open. The 5.2.x release of Globus seems to have addressed a lot of these scalability issues.  Regardless, here are the guides I used for tuning Condor/Linux for large numbers of jobs:

* ~~[http://www.cs.wisc.edu/condor/condorg/linux_scalability.html](http://www.cs.wisc.edu/condor/condorg/linux_scalability.html)~~ 
* ~~[http://www.cs.wisc.edu/condor/condorg/goldenrules.html](http://www.cs.wisc.edu/condor/condorg/goldenrules.html)~~

(Regretfully, these guides no longer appear active. We will post an alternative if one comes up)

Changes to /etc/sysctl.conf:

	# Done for Condor
	# http://www.cs.wisc.edu/condor/condorg/linux_scalability.html
	# process IDs
	kernel.pid_max = 4194303

	# max open files
	fs.file-max = 2274339


Changes to /etc/security/limits.conf

	root soft nofile 32768
	root hard nofile 32768
	seqware soft nofile 32768
	seqware hard nofile 32768

Also for Hadoop setup later, we will need

hdfs  -       nofile  32768
hbase -       nofile  32768

Services in /etc/services

Make sure the following are defined in /etc/services: 

	sge_qmaster     6444/tcp
	sge_execd       6445/tcp
	gsiftp          2811/tcp                        # GSI FTP
	gsiftp          2811/udp                        # GSI FTP
	gsigatekeeper   2119/tcp                        # GSIGATEKEEPER
	gsigatekeeper   2119/udp                        # GSIGATEKEEPER

## Setup Hadoop, HBase, and other Related Tools 

HBase is required for the Query Engine. These instructions are current to Cloudera's CDH4 distribution.

### Base Hadoop ###

We will go through the MRv1 instructions at [Cloudera](https://ccp.cloudera.com/display/CDH4DOC/Installing+CDH4+on+a+Single+Linux+Node+in+Pseudo-distributed+Mode) 

While we have a version of Java installed that satisfies [the requirements](https://ccp.cloudera.com/display/CDH4DOC/Before+You+Install+CDH4+on+a+Single+Node#BeforeYouInstallCDH4onaSingleNode-InstalltheOracleJavaDevelopmentKit) we need to set the JAVA_HOME for the root user (and probably the seqware user) by adding 

	export JAVA_HOME=<jdk-install-dir>
	export PATH=$JAVA_HOME/bin:$PATH
to both their .bashrc files.

We then add the "Red Hat/CentOS 6 (64-bit)" RPM and add the optional GPG key. (RedHat/CentOS/Oracle 5 for CentOS 5.5)

For the record, pulling down hadoop-0.20-conf-pseudo grabs

	 bigtop-jsvc                                            x86_64                      0.4+300-1.cdh4.0.1.p0.1.el6                          cloudera-cdh4                       27 k
	 bigtop-utils                                           noarch                      0.4+300-1.cdh4.0.1.p0.1.el6                          cloudera-cdh4                      7.4 k
	 hadoop                                                 x86_64                      2.0.0+91-1.cdh4.0.1.p0.1.el6                         cloudera-cdh4                       18 M
	 hadoop-0.20-mapreduce                                  x86_64                      0.20.2+1216-1.cdh4.0.1.p0.1.el6                      cloudera-cdh4                       36 M
	 hadoop-0.20-mapreduce-jobtracker                       noarch                      0.20.2+1216-1.cdh4.0.1.p0.1.el6                      cloudera-cdh4                      4.1 k
	 hadoop-0.20-mapreduce-tasktracker                      noarch                      0.20.2+1216-1.cdh4.0.1.p0.1.el6                      cloudera-cdh4                      4.0 k
	 hadoop-hdfs                                            x86_64                      2.0.0+91-1.cdh4.0.1.p0.1.el6                         cloudera-cdh4                      8.7 M
	 hadoop-hdfs-datanode                                   x86_64                      2.0.0+91-1.cdh4.0.1.p0.1.el6                         cloudera-cdh4                      5.0 k
	 hadoop-hdfs-namenode                                   x86_64                      2.0.0+91-1.cdh4.0.1.p0.1.el6                         cloudera-cdh4                      4.8 k
	 hadoop-hdfs-secondarynamenode                          x86_64                      2.0.0+91-1.cdh4.0.1.p0.1.el6                         cloudera-cdh4                      4.8 k
	 zookeeper                                              noarch                      3.4.3+15-1.cdh4.0.1.p0.1.el6                         cloudera-cdh4                      3.6 M

When configuring file paths we use in the hdfs-site.xml (for the Amazon ami):

	  <property>
	     <name>dfs.namenode.name.dir</name>
	     <value>/mnt/xvdc1/nn,/mnt/xvdb1/nn</value>
	  </property>
	  <property>
	     <name>dfs.datanode.data.dir</name>
	     <value>/mnt/xvdc1/dn,/mnt/xvdb1/dn</value>
	  </property>

There are no changes until Step 4 where I think they forgot a step, so you need to run (while logged-in as hdfs)

	hadoop fs -mkdir /user
to match their structure.

A user directory was created for seqware (while logged-in as hdfs)

	hadoop fs -mkdir /user/seqware
	hadoop fs -chown seqware /user/seqware
	 
The rest of the instructions are followed until the end of the MRv1 section (do not install YARN). 

Looking through the zookeeper install instructions, it looks like that is sufficient for a standalone server, so we move onto Hive

### Hive ###

Follow the instructions [for Hive](https://ccp.cloudera.com/display/CDH4DOC/Hive+Installation#HiveInstallation-InstallingHive)
For the record, pulling down hive grabs

	Installing:
	 hive                                      noarch                          0.8.1+61-1.cdh4.0.1.p0.1.el6                              cloudera-cdh4                           22 M
	Installing for dependencies:
	 hadoop-client                             x86_64                          2.0.0+91-1.cdh4.0.1.p0.1.el6                              cloudera-cdh4                           13 k
	 hadoop-mapreduce                          x86_64                          2.0.0+91-1.cdh4.0.1.p0.1.el6                              cloudera-cdh4                          9.5 M
	 hadoop-yarn                               x86_64                          2.0.0+91-1.cdh4.0.1.p0.1.el6                              cloudera-cdh4                          8.6 M
	 hbase                                     noarch                          0.92.1+67-1.cdh4.0.1.p0.1.el6                             cloudera-cdh4                           34 M

These instructions are stopped after we test Hive but before the optimization by installing MySQL and connecting Hive to it. 

### Pig 

Follow the instructions [for Pig](https://ccp.cloudera.com/display/CDH4DOC/Pig+Installation#PigInstallation-InstallingPig)

	 pig                              noarch                              0.9.2+26-1.cdh4.0.1.p0.1.el6                               cloudera-cdh4                               55 M

### Hue 

Follow the instructions at [for Hue](https://ccp.cloudera.com/display/CDH4DOC/Hue+Installation) using the instructions for pseudo-distributed mode

	Installing:
	 hue                                       x86_64                           2.0.0+59-1.cdh4.0.1.p0.1.el6                            cloudera-cdh4                           2.5 k
	 hue-server                                x86_64                           2.0.0+59-1.cdh4.0.1.p0.1.el6                            cloudera-cdh4                           4.2 k
	Installing for dependencies:
	 hue-about                                 x86_64                           2.0.0+59-1.cdh4.0.1.p0.1.el6                            cloudera-cdh4                           9.1 k
	 hue-beeswax                               x86_64                           2.0.0+59-1.cdh4.0.1.p0.1.el6                            cloudera-cdh4                           456 k
	 hue-common                                x86_64                           2.0.0+59-1.cdh4.0.1.p0.1.el6                            cloudera-cdh4                            48 M
	 hue-filebrowser                           x86_64                           2.0.0+59-1.cdh4.0.1.p0.1.el6                            cloudera-cdh4                            41 k
	 hue-help                                  x86_64                           2.0.0+59-1.cdh4.0.1.p0.1.el6                            cloudera-cdh4                            13 k
	 hue-jobbrowser                            x86_64                           2.0.0+59-1.cdh4.0.1.p0.1.el6                            cloudera-cdh4                            45 k
	 hue-jobsub                                x86_64                           2.0.0+59-1.cdh4.0.1.p0.1.el6                            cloudera-cdh4                           193 k
	 hue-plugins                               x86_64                           2.0.0+59-1.cdh4.0.1.p0.1.el6                            cloudera-cdh4                           1.5 M
	 hue-proxy                                 x86_64                           2.0.0+59-1.cdh4.0.1.p0.1.el6                            cloudera-cdh4                            12 k
	 hue-shell                                 x86_64                           2.0.0+59-1.cdh4.0.1.p0.1.el6                            cloudera-cdh4                            37 k
	 hue-useradmin                             x86_64                           2.0.0+59-1.cdh4.0.1.p0.1.el6                            cloudera-cdh4                            48 k

Follow the instructions through "Hadoop Configurations for Hue."

The command for "You can confirm that the plugins are running correctly by tailing the daemon logs:" is incorrect and is actually
<pre>tail --lines=500 /var/log/hadoop-0.20-mapreduce/hadoop*jobtracker*.log | grep ThriftPlugin</pre> The oozie instruction that follows is skipped since I don't think we've installed oozie (in the lists of packages anyways). The "HADOOP_CLASSPATH Caveat" is skipped since there doesn't look like there is a hadoop-env.sh in /etc/hadoop/conf .
We skipped over "Configuring Hue for SSL" and skipped to "Installing and Configuring Hue Shell"

We login to Hue via localhost:8888 using seqware:seqware

(This is incomplete)

### HBase 
 
We follow the instructions starting from [here](https://ccp.cloudera.com/display/CDH4DOC/HBase+Installation) and follow the pseudo-distributed sections.
FYI, this will briefly segue into installing the zookeeper server.

It looks like the Cloudera install doesn't setup HBase to survive a reboot. Fixed this via

	chkconfig hbase-master on
	chkconfig hadoop-zookeeper-server on
	chkconfig hadoop-hbase-regionserver on

### Oozie

Follow the instructions [for Oozie](https://ccp.cloudera.com/display/CDH4DOC/Oozie+Installation#OozieInstallation-InstallingOozie)

         oozie                              noarch                              0.9.2+26-1.cdh4.0.1.p0.1.el6                               cloudera-cdh4                               55 M
         oozie-client                              noarch                              0.9.2+26-1.cdh4.0.1.p0.1.el6                               cloudera-cdh4                               55 M

Oozie is the an alternative to the Pegasus/Condor/Globus/SGE software stack and provides a way to run SeqWare Workflows on a Hadoop cluster. We are really excited about this workflow engine and thing it may be the default back-end recommended for SeqWare workflows on our VM and Amazon's cloud given it's really fast scheduling performance and ease of integration with Hadoop (so you can mix traditional Bash jobs with MapReduce, Pig, Hive, etc jobs).

Please follow the directions on Cloudera carefully, you need to make sure you initialize the Oozie database properly. By default we use the built in Derby database but you may want to setup another daemon like PostgreSQL.

You also will probably want to enable the web console for Oozie, in which case you will need to follow the directions to install ExtJS. This will give you a nice interface at http://<OOZIE_HOSTNAME>:11000/oozie where you can browse the running workflows.


## Setup SGE

Now install SGE, the cluster engine. You are going to setup this VM as both an SGE master and an SGE worker, essentially it is a one-node cluster.  Later, you can setup this VM as a submit host to your real cluster so you can submit dozens of workflows simultaneously on a many nodes.

We use the stock gridengine RPMs that can be installed using yum:

	yum install gridengine gridengine-execd gridengine-qmaster gridengine-qmon 

All I had to do was expand the tarballs to /usr/local/ge-6.1u6 then run:

	 /usr/share/gridengine/install_qmaster
	 /usr/share/gridengine/install_execd

And follow the directions, the vast majority of settings can be left as default.

These setup the environment. If you have questions look at the install guide on Oracle's site.  Next, make sure the following profiles.d script is present as /etc/profile.d/sge.sh:

	if [ -z "$SGE_ROOT" ] ; then
		if [ -f /etc/sysconfig/gridengine ] ; then
			. /etc/sysconfig/gridengine
			export SGE_ROOT
			export SGE_CELL
		fi
	fi

	source /usr/share/gridengine/default/common/settings.sh


You will need to edit /etc/hosts and create link the seqware hostname to the IP address of your VM (which depends on your site). For example:

	164.67.97.181   seqwarevm

This is what I used for my network, you will need to use an IP address that's appropriate for your setup. You can change the seqware hostname here but the instructions assume it's seqwarevm.

### Testing

You should submit a test job to make sure it works OK.  There are more examples in the testing section later in this doc.

	# startus SGE
	/etc/init.d/sgemaster start
	/etc/init.d/sgeexecd start
	echo 'hostname' | qsub

If everything worked you'll see a STDIN.o* file in the current directory that contains the hostname (seqware).

### Load Issue

I noticed on VirtualBox on the Mac that the load in top doesn't seem to be very accurate. The load is easily 4 even when the virtual machine is idle.  So, be careful since the default load threshold I think is set to 1.75, essentially no jobs will run because the machine is always beyond this.  Issue the following command to tweak this setting:

	 qconf -mq all.q

And look for the following line:

	 load_thresholds       np_load_avg=10.0

I changed this to 10.0. The effect seems immediate.

### Logging Issue

Globus needs job logging in SGE turned on, for more info see: [this](http://technical.bestgrid.org/index.php/Setting_up_an_NG2/SGE_specific_parts#Prerequisites_2)

To do this:

	 /usr/share/gridengine/bin/lx26-amd64/qconf -mconf

Look to change the config so reporting and joblog are true:

	reporting_params             accounting=true reporting=true \
				     flush_time=00:00:15 joblog=true sharelog=00:00:00

### Adding Parallel Environment

Use the following commands to add a "serial" parallel environment, specify the number of processor cores you've given to the VM:

	# setup the parallel env
	qconf -ap serial
	# now add "serial" to the pe_list variable
	qconf -mq all.q

## Setup Globus

The [Globus Toolkit](http://www.globus.org/toolkit/about.html) is a fairly large, feature rich, and complex toolkit for interacting with remote clusters.  For SeqWare, we're primarily interested in the GridFTP component, which Pegasus uses to transfer data and binaries to and from remote clusters, GRAM5, which sends jobs off to real compute clusters, and the certificate generation/management and user authentication features.

I followed the [quickstart](http://www.globus.org/toolkit/docs/5.2/5.2.0/admin/quickstart/) which simplifies the install considerably since previous releases.  Keep in mind I don't use the MyProxy server, instead I use the directions below. I was not able to get MyProxy to work with my setup (but in theory it should and it's the recommended way of handling user certificates).

### Install Packages

Word of warning: if you use "poll" rather than "seg" change the setup RPM below to "globus-gram-job-manager-sge-setup-poll"

	# all together...
	yum groupinstall globus-gridftp globus-gram5
	yum install gridengine gridengine-execd gridengine-qmaster gridengine-qmon globus-gridftp-server-progs globus-gass-copy-progs myproxy myproxy-server myproxy-admin globus-simple-ca globus-gss-assist-progs globus-gram-job-manager-sge globus-gram-job-manager-sge-setup-seg

### Setup Certificates for Authentication

These steps use tools from Globus to setup both a Certificate Authority (CA) and public/private keys for the host and individual users.  If you intend to use this Pegasus setup with an existing cluster they must be recognized as a certificate authority and issue you a public/private key.  Frankly, I really don't know what I'm talking about here so I'll just point you to a couple websites with more info:

* [Grid setup](https://confluence.pegasus.isi.edu/display/pegasus/Grid+Setup)
* [Globus admin](http://www.globus.org/toolkit/docs/5.0/5.0.1/admin/install/#gtadmin-basic-host)
* [Globus user security](http://www.globus.org/toolkit/docs/5.0/5.0.1/user/#gtuser-security)

For the purposes of a self-contained testing/development environment the instructions below should work just fine.

As root:

	# create the certificate authority
	grid-ca-create
	# I used the defaults (bumped to 50 years!) and signed with password

FYI, if you need to change the passphrase:

	[root@swmaster ~]# grid-change-pass-phrase -file .globus/simpleCA/private/cakey.pem 

Some info from the certificate authority creation:

	A self-signed certificate has been generated 
	for the Certificate Authority with the subject: 

	/O=Grid/OU=GlobusTest/OU=simpleCA-pegasus01/CN=Globus Simple CA

	If this is invalid, rerun this script 

	/usr/local/globus/default/setup/globus/setup-simple-ca

	and enter the appropriate fields.

	-------------------------------------------------------------------

	The private key of the CA is stored in /root/.globus/simpleCA//private/cakey.pem
	The public CA certificate is stored in /root/.globus/simpleCA//cacert.pem

	The distribution package built for this CA is stored in

	/root/.globus/simpleCA//globus_simple_ca_091518a9_setup-0.20.tar.gz

	This file must be distributed to any host wishing to request
	certificates from this CA.

	CA setup complete.

	The following commands will now be run to setup the security
	configuration files for this CA:

	$GLOBUS_LOCATION/sbin/gpt-build /root/.globus/simpleCA//globus_simple_ca_091518a9_setup-0.20.tar.gz

	$GLOBUS_LOCATION/sbin/gpt-postinstall

After installing got the following files in /etc/grid-security/certificates:

	-rw-r--r-- 1 root root  924 May 19 06:54 091518a9.0
	-rw-r--r-- 1 root root 1.4K May 19 06:54 091518a9.signing_policy
	-rw-r--r-- 1 root root 2.7K May 19 06:54 globus-host-ssl.conf.091518a9
	-rw-r--r-- 1 root root 2.8K May 19 06:54 globus-user-ssl.conf.091518a9
	-rw-r--r-- 1 root root 1.4K May 19 06:54 grid-security.conf.091518a9

Now create the host cert/key for the master host as root:

	grid-cert-request -host seqware
	ls -lth /etc/grid-security/
	# host cert should be good for 10 years
	grid-ca-sign -in /etc/grid-security/hostcert_request.pem -out /etc/grid-security/hostcert.pem -days 3650
	ln -s /etc/grid-security/hostkey.pem /etc/grid-security/containerkey.pem
	ln -s /etc/grid-security/hostcert.pem /etc/grid-security/containercert.pem

Now create user request as seqware:

	[seqware@seqware ~]$ grid-cert-request -nopw
	A private key and a certificate request has been generated with the subject:

	/O=Grid/OU=GlobusTest/OU=simpleCA-pegasus01/CN=SeqWare

	If the CN=SeqWare is not appropriate, rerun this
	script with the -force -cn "Common Name" options.

	Your private key is stored in /home/seqware/.globus/userkey.pem
	Your request is stored in /home/seqware/.globus/usercert_request.pem

	Please e-mail the request to the Globus Simple CA briandoconnor@gmail.com
	You may use a command similar to the following:

	  cat /home/seqware/.globus/usercert_request.pem | mail foo@gmail.com

	Only use the above if this machine can send AND receive e-mail. if not, please
	mail using some other method.

	Your certificate will be mailed to you within two working days.
	If you receive no response, contact Globus Simple CA at foo@gmail.com

Now as root sign the request and issue keys:

	# sign for 10 years
	[root@seqware ~]# grid-ca-sign -in ~seqware/.globus/usercert_request.pem -out /tmp/usercert.pem -days 3650

	To sign the request
	please enter the password for the CA key:

	The new signed certificate is at: /root/.globus/simpleCA//newcerts/01.pem

From the certificate, copy the Subject information, and create a mapping to the user 
on the local cluster in (/etc/grid-security/grid-mapfile):

	"/O=Grid/OU=GlobusTest/OU=simpleCA-pegasus01/CN=SeqWare" seqware

As seqware copy the key to home:

 cp /tmp/usercert.pem ~/.globus/

Copy the usercert and userkey to /opt/usercerts/seqware-cert.pem and /opt/usercerts/seqware-key.pem. These then needs to be put in the created seqware user's homedir by the starcluster user plugin.

#### Issue with Certificate Path

I ran into a major roadblock issue that took forever to debug.  It turns out OpenSSL (the most recent version) installs certificates and generates a subject hash in a new way while Globus expects to find them using the old hash. The errors generated make it almost impossible to figure this out. The result is that the globus submission from Condor fails almost silently.  To fix this you need to figure out the old hash and symlink cert files using the old hash as part of the name:

	openssl x509 -hash -noout < /etc/grid-security/certificates/3847a079.0
	openssl x509 -subject_hash_old -noout < /etc/grid-security/certificates/3847a079.0
	ln -s grid-security.conf.3847a079 grid-security.conf.44d1fecb

In the above example I see 44d1fecb is the old hash while 3847a079 is the new one.

### Setup GRAM

[GRAM](http://www.globus.org/toolkit/docs/5.0/5.0.0/execution/gram5/rn/) is the agent that allows condor to communicate with other clusters and submit jobs in a scheduler agnostic way.

There are some workflows where I want to be able to add the following:

	 <profile namespace="globus" key="jobtype">condor</profile>
	 <profile namespace="globus" key="count">8</profile>

And this means that particular step needs 8 threads on the node.  I used "condor" here since this was the only jobtype value recognized by GRAM that wasn't already being used in sge.pm.  To get this to work make sure your parallel environment "serial" is setup in SGE or, if you've named it something else, you modify sge.pm correctly.

Now apply the following patch as root to /usr/share/perl5/vendor_perl/Globus/GRAM/JobManager/sge.pm. You may find it easier just to copy the new lines added below rather than use the patch command.

<%= render '/includes/setup_gram_1/' %>

If a particular module in one of your workflows needs to use a specific amount of memory you may also need to modify the sge.pm globus object depending on how you've setup memory management on your cluster.  For example, this is the change I made to work on our cluster where memory was setup as a "consumable resource" and accessed by the following in the DAX, note this number is in megabytes:

	 <profile namespace="globus" key="maxmemory">100</profile>

These are the changes to sge.pm:

<%= render '/includes/setup_gram_2/' %>

Think very carefully about these changes above before you make them.  Your workflows may not need multi-threading and the version of GRAM you use (if not identical to the one here) may already allow you to specify without modifying the sge.pm file.  Contact the Pegasus developers for help on this.

After making your changes to the sge.pm make sure you use "perl -c" to check the syntax. It needs to be a valid Perl file to work!

### Starting Daemons

At this point Globus should be setup correctly, all you have to do is start three services: GRAM, GridFTP, and the SGE log monitor for GRAM.  You should use chkconfig to ensure these start on boot too.

	service globus-gatekeeper start
	service globus-gridftp-server start
	# only use if you setup logging in SGE which you should
	service globus-scheduler-event-generator start

## Setup Condor 

Pegasus submits jobs to Condor and Condor then submits to an actual cluster execution engine.  Here we're using SGE as our cluster execution engine but Condor could be configured to submit to another cluster engine (via GRAM) such as PBS or LSF or even run jobs itself.  Pegasus doesn't need to know about these details since it always just submits to Condor. Condor is also the layer that provides job dependency management.

As root, you may need to download condor manually from http://www.cs.wisc.edu/condor/downloads-v2/download.pl or use [yum](http://research.cs.wisc.edu/condor/yum/).  For example:

	wget http://parrot.cs.wisc.edu//symlink/20100518141501/7/7.6/7.6.0/75af7599baf63b6fb8f27ab2091cf69f/condor-7.6.0-linux-x86_64-rhel5-dynamic-1.x86_64.rpm
	rpm -Uvh condor-7.6.0-linux-x86_64-rhel5-dynamic-1.x86_64.rpm

Starting with the 7.5 release of condor the project has cleaned up their RPMs to install software in the "proper" RedHat/CentOS places.  I had to use 7.6.7 and not the latest version since I experience seg faults with the latest.

This is what I added/changed in /etc/condor/condor_config.local as root in previous VM setups.  In this current VM I'm using stock but you may be interested in tuning these params:

	DAEMON_LIST = COLLECTOR, MASTER, NEGOTIATOR, SCHEDD

	# SEQWARE SETTINGS
	JAVA_MAXHEAP_ARGUMENT = -Xmx1024m
	# Limit port range to firewall range (Jordan)
	LOWPORT=40000
	HIGHPORT=41000
	# Bind to all IP's including public
	BIND_ALL_INTERFACES=TRUE
	UPDATE_INTERVAL = 1
	JOB_START_COUNT = 1
	JOB_START_DELAY = 1
	NEGOTIATOR_INTERVAL = 1
	NEGOTIATOR_MATCHLIST_CACHING = FALSE
	GRIDMANAGER_MAX_SUBMITTED_JOBS_PER_RESOURCE = 100
	GRIDMANAGER_MAX_PENDING_SUBMITS_PER_RESOURCE = 100
	GRIDMANAGER_MAX_JOBMANAGERS_PER_RESOURCE = 50
	# Misc tuning params http://wisent.d-grid.de/bi.offis.uni-oldenburg.de/wisent-wiki/tiki-index85b0.html?page=Condor-GT4-Admin#id464444 
	#UPDATE_INTERVAL = 30
	#JOB_START_COUNT = 10
	#JOB_START_DELAY = 2
	#NEGOTIATOR_INTERVAL = 30
	#NEGOTIATOR_MATCHLIST_CACHING = FALSE
	#GRIDMANAGER_MAX_PENDING_SUBMITS_PER_RESOURCE = 20
	#GRIDMANAGER_MAX_JOBMANAGERS_PER_RESOURCE = 10
	# Do NOT set the below configuration for production clusters unless you want to hear bad words from your admin.
	ENABLE_GRID_MONITOR=FALSE

Now startup the daemons as root:

	[root@seqware ~]# /etc/init.d/condor start

And make sure you use chkconfig to ensure condor is started on boot.

See [this](http://wisent.d-grid.de/bi.offis.uni-oldenburg.de/wisent-wiki/tiki-index85b0.html) for more info on tuning Condor.

## Testing SGE, Globus GRAM, Globus GridFTP, and Condor

The next step is to test the whole software stack you installed as user seqware. If you can get through these tests then the most complex part of the configuration and installation is behind you. If you can't get these to work then please email the support mailing lists for the appropriate projects.  Globus and Condor both have very active lists and the Pegasus developers are extremely helpful.  Unless you can successfully do all of the following do not attempt to install SeqWare or Pegasus since they will not work!

Also, it's worth pointing out that you should test that you can specify the number of "slots/threads" and the amount of memory for qsub submissions using the correct syntax that you setup for this SGE instance.

	# first, test if SGE is working
	[seqware@seqware ~]$ echo /bin/hostname | qsub -cwd
	Your job 418 ("STDIN") has been submitted

	# you should then see a STDIN.o418 (or whatever the number above is) in the directory that contains "seqwarevm"

	# now test authentication via Globus
	[seqware@seqware ~]$ grid-proxy-init 
	Your identity: /O=Grid/OU=GlobusTest/OU=simpleCA-pegasus01/CN=SeqWare
	Creating proxy ....................................... Done
	Your proxy is valid until: Wed May 19 19:23:44 2010

	# test GridFTP locally
	[seqware@seqware ~]$ globus-url-copy -v file:/etc/group file:/tmp/group
	Source: file:/etc/
	Dest:   file:/tmp/
	  group

	# now test it using the GridFTP network transfer
	[seqware@seqware ~]$ globus-url-copy -v file:/etc/group gsiftp://seqwarevm/tmp/group
	Source: file:/etc/
	Dest:   gsiftp://seqware/tmp/
	  group

	# if the above worked without error GridFTP has been setup properly

	# now attempt to authenticate via Globus GRAM
	[seqware@seqware ~]$ globusrun -a -r seqwarevm
	GRAM Authentication test successful

	# now run a local job using Globus GRAM
	[seqware@seqware ~]$ globus-job-run seqwarevm/jobmanager-fork /bin/hostname
	seqwarevm

	# now the moment of truth, attempt to run a cluster job using SGE via Globus GRAM
	# you can use "watch qstat -f" to monitor this in another terminal while it runs
	[seqware@seqware ~]$ globus-job-run seqwarevm/jobmanager-sge /bin/hostname
	Job is running.
	Job is running.
	Job is still queued for execution.
	Job 3 has completed.
	Writing job STDOUT and STDERR to cache files.
	seqwarevm
	Returning job success.

If all of the above worked then Globus authentication, Globus GRAM, and SGE are all working.  The next layer to check is Condor. As seqware, you can create a simple Condor workflow and submit it:

	# create the sub file
	[seqware@seqware ~]$ echo "executable = /bin/hostname
	transfer_executable = false
	universe = grid
	grid_resource = gt5 seqwarevm/jobmanager-sge
	output = work.out
	log = work.log
	error = work.err
	queue
	" >  workflow.sub 

	# submit as seqware
	[seqware@seqware ~]$ condor_submit workflow.sub 
	Submitting job(s).
	Logging submit event(s).
	1 job(s) submitted to cluster 2.

	# in two different terminals you can monitor the progress as seqware using
	watch condor_q
	watch qstat -f

If these tests pass without errors the whole software stack SGE/Globus/Condor should be working and that's a major hurdle to pass.  Pat yourself on the back!

## Setup Pegasus

I setup the yum repo as documented [here](http://pegasus.isi.edu/wms/download.php). Check this link to make sure the directions are the same but I created a file /etc/yum.repos.d/pegasus.repo with the following contents:

	# Pegasus 3.0
	[Pegasus30]
	name=Pegasus30
	baseurl=http://pegasus.isi.edu/wms/download/3.0/yum/rhel/$releasever/$basearch/
	gpgcheck=0
	enabled=1

I then installed the latest Pegasus (version 3.0):

	 yum install pegasus-3.0

Now as root create the following file: /etc/profile.d/pegasus.sh

	PATH=/opt/pegasus/3.0/bin:$PATH
	export PATH

Now link 3.0 to the default:

	cd /opt/pegasus/
	ln -s 3.0 default

Keep in mind that, at the time of this writing, the most recent version of Pegasus is 4.0.  We currently have not updated and do not support beyond 3.0.x for SeqWare workflows.

## Testing Pegasus

The next step is to make sure Pegasus is working correctly.  They include some sample workflows that can be executed in /opt/pegasus/default/examples/.

The following is all done as user seqware.

	cp -R /opt/pegasus/default/examples/grid-blackdiamond-java /home/seqware/pegasus_testing
	cd /home/seqware/pegasus_testing/grid-blackdiamond-java

	# make sure you're authenticated
	grid-proxy-init

	# you will need to edit the submit script and the sites.xml, see below
	./submit

	# you'll see a bunch of output including the status command, use it to monitor the run status 
	watch pegasus-status -l /home/seqware/grid-blackdiamond-java/work/seqware/pegasus/blackdiamond/20110512T125947-0700

	# sample output
	blackdiamond-0.dag succeeded
	05/12/11 13:09:03  Done     Pre   Queued    Post   Ready   Un-Ready   Failed
	05/12/11 13:09:03   ===     ===      ===     ===     ===        ===      ===
	05/12/11 13:09:03    14       0        0       0       0          0        0

	WORKFLOW STATUS : COMPLETED | 14/14 ( 100% ) | (all jobs finished successfully)

	# watch the cluster jobs in a separate terminal
	watch qstat -f

It should say COMPLETED after a few minutes if everything works OK.

Here are the edits I did for the ./submit script:

	CLUSTER_NAME="seqware"
	CLUSTER_HOSTNAME="seqware"
	CLUSTER_GATEKEEPER_TYPE="gt5"
	CLUSTER_GATEKEEPER_PORT="2119"
	CLUSTER_SCHEDULER="sge"
	CLUSTER_WORK_DIR="/tmp"
	CLUSTER_PEGASUS_HOME="/opt/pegasus/3.0"
	CLUSTER_GLOBUS_LOCATION="/usr/local/globus/default"

Here are the edits for the sites.xml

	<profile namespace="env" key="PEGASUS_HOME" >/opt/pegasus/3.0</profile>
	<profile namespace="env" key="GLOBUS_LOCATION" >/usr/local/globus/default</profile>

## Setup Users' Bash Profile, Pegasus & SeqWare Settings

These changes should really happen after all the software is installed, to make sure the paths below are valid. Also, we should make use of /etc/profiles.d files too which will make it easier to maintain settings across user accounts.

### Bash Profile

Make sure both seqware and root have their environments setup correctly. Here's seqware's .bash_profile for your reference:

	# .bash_profile

	# Get the aliases and functions
	if [ -f ~/.bashrc ]; then
		. ~/.bashrc
	fi

	# User specific environment and startup programs
	export PATH=$PATH:$HOME/bin:/opt/pegasus/3.0/bin/
	export JAVA_HOME=/usr/java/default

	# this points to the seqware settings file which if left undefined defaults to ~/.seqware/settings
	export SEQWARE_SETTINGS=~/.seqware/settings

### Pegasus Settings

Make a .seqware/pegasus directory in your home directory.

Now you will need to create four pegasus files in this directory:

#### properties

The most interesting property here is the dagman.retry=1.  In this case jobs will try two times (retried once). You can increase this when running in production to increase your chances of finishing workflows.

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
	pegasus.dagman.projection.maxjobs 2

#### sites.xml3 

	<?xml version="1.0" encoding="UTF-8"?>
	<sitecatalog xmlns="http://pegasus.isi.edu/schema/sitecatalog" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://pegasus.isi.edu/schema/sitecatalog http://pegasus.isi.edu/schema/sc-3.0.xsd" version="3.0">
		<site  handle="local" arch="x86_64" os="LINUX" osrelease="" osversion="" glibc="">
			<grid  type="gt5" contact="seqwarevm/jobmanager-fork" scheduler="Fork" jobtype="auxillary"/>
			<grid  type="gt5" contact="seqwarevm/jobmanager-sge" scheduler="SGE" jobtype="compute"/>
			<head-fs>
				<scratch>
					<shared>
						<file-server protocol="gsiftp" url="gsiftp://seqwarevm" mount-point="/home/seqware/SeqWare/pegasus-working"/>
						<internal-mount-point mount-point="/home/seqware/SeqWare/pegasus-working"/>
					</shared>
				</scratch>
				<storage>
					<shared>
						<file-server protocol="gsiftp" url="gsiftp://seqwarevm" mount-point="/"/>
						<internal-mount-point mount-point="/"/>
					</shared>
				</storage>
			</head-fs>
			<replica-catalog  type="LRC" url="rlsn://smarty.isi.edu"/>
			<profile namespace="env" key="GLOBUS_LOCATION">/usr</profile>
			<profile namespace="env" key="JAVA_HOME">/usr/java/default</profile>
			<!--profile namespace="env" key="LD_LIBRARY_PATH">/.mounts/labs/seqware/public/globus/default/lib</profile-->
			<profile namespace="env" key="PEGASUS_HOME">/opt/pegasus/3.0</profile>
		</site>
		<site  handle="seqwarevm" arch="x86_64" os="LINUX" osrelease="" osversion="" glibc="">
			<grid  type="gt5" contact="seqwarevm/jobmanager-fork" scheduler="Fork" jobtype="auxillary"/>
			<grid  type="gt5" contact="seqwarevm/jobmanager-sge" scheduler="SGE" jobtype="compute"/>
			<head-fs>
				<scratch>
					<shared>
						<file-server protocol="gsiftp" url="gsiftp://seqwarevm" mount-point="/home/seqware/SeqWare/pegasus-working"/>
						<internal-mount-point mount-point="/home/seqware/SeqWare/pegasus-working"/>
					</shared>
				</scratch>
				<storage>
					<shared>
						<file-server protocol="gsiftp" url="gsiftp://seqwarevm" mount-point="/"/>
						<internal-mount-point mount-point="/"/>
					</shared>
				</storage>
			</head-fs>
			<replica-catalog  type="LRC" url="rlsn://smarty.isi.edu"/>
			<profile namespace="env" key="GLOBUS_LOCATION">/usr</profile>
			<profile namespace="env" key="JAVA_HOME">/usr/java/default</profile>
			<!--profile namespace="env" key="LD_LIBRARY_PATH">/.mounts/labs/seqware/public/globus/default/lib</profile-->
			<profile namespace="env" key="PEGASUS_HOME">/opt/pegasus/3.0</profile>
		</site>
	</sitecatalog>

#### rc.data 

Just touch this file, we don't use it so it can be empty.

#### tc.data 

Just touch this file, we don't use it so it can be empty.

### SeqWare Settings

This is the file to put all SeqWare settings, we have (or will have) APIs for Perl and Java that will find this file (based on the SEQWARE_SETTINGS env variable), parse it, and make the values available. Put these contents in the .seqware/settings file:

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

	# the central repository for installed/archiving bundles
	SW_BUNDLE_REPO_DIR=/home/seqware/SeqWare/released-bundles

	# encryption and decryption keys optionally used by ProvisionFiles
	# cryptographic DESede keys in Base64 encoded text
	SW_ENCRYPT_KEY=FILLMEIN
	SW_DECRYPT_KEY=FILLMEIN

	#
	# SEQWARE WEBSERVICE SETTINGS
	#
	# the base URL for the RESTful SeqWare API
	SW_REST_URL=https://localhost:8080/SeqWareWebService

	# the username and password to connect to the REST API, this is used by SeqWare Pipeline to write back processing info to the DB
	SW_REST_USER=admin@admin.com
	SW_REST_PASS=admin

	#
	# AMAZON CLOUD SETTINGS
	#
	# used by tools reading and writing to S3 buckets (dependency data/software bundles, inputs, outputs, etc)
	# most likely not used here at OICR
	AWS_ACCESS_KEY=FILLMEIN
	AWS_SECRET_KEY=FILLMEIN

## Setup SeqWare Pipeline ##

### Checking Out SeqWare ###

As user seqware log in and do the following:

	[seqware@seqware ~]$ mkdir SeqWare
	[seqware@seqware ~]$ cd SeqWare/
	[seqware@seqware svnroot]$ svn co https://seqware.svn.sourceforge.net/svnroot/seqware/trunk trunk

The seqware user will now have a complete SeqWare checkout in the directory trunk.

### Building SeqWare ###

We use Maven to build the project. In the trunk directory you can:

	mvn clean install -Dmaven.test.skip=true

If everything works properly you should find a jar file in the seqware-distribution/target directory that can be used for invoking most of the SeqWare Pipeline tools (some other tools are scripts in the seqware-pipeline/bin directory).

	 seqware-distribution/target/seqware-distribution-0.12.3-SNAPSHOT-full.jar

You will also find other sub-project output files in the target directories under their respective sub-projects. For example the war file for the Portal and Web Service.

### Setup Other Directories

You will also need to create various directories in the ~/SeqWare directory:

	[seqware@seqwarevm SeqWare]$ ls
	pegasus-dax  pegasus-working  provisioned-bundles  released-bundles  trunk

### Testing SeqWare Pipeline

SeqWare Pipeline includes a "HelloWorld" module and workflow that is useful for testing if the whole stack is working.  Log into the VM as "seqware" and cd in: 

	 /home/seqware/svnroot/seqware-complete/trunk

Make sure SeqWare Pipeline has been built, see above.

Now you should be ready to run the HelloWorld workflow, use whatever the latest jar file is in the target directory:

	 java -jar seqware-distribution-0.12.3-SNAPSHOT-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -b ~/SeqWare/released-bundles/Workflow_Bundle_HelloWorld_0.11.4_SeqWare_0.11.4.zip -t -w HelloWorld --version 0.11.4

The output will tell you how to follow the results, for example, I used the following command to watch the status (note I use -l for a more readable output):

	 watch pegasus-status -l /tmp/pegasus_dax/seqware/seqware/pegasus/HelloWorld/run0001

Where "run0001" is replaced with the run mentioned in the ./bin/pegasus-run.pl output.

If everything worked OK you should see the job complete:

	HelloWorld-0.dag succeeded
	05/12/11 20:05:06  Done     Pre   Queued    Post   Ready   Un-Ready   Failed
	05/12/11 20:05:06   ===     ===      ===     ===     ===        ===      ===
	05/12/11 20:05:06    17       0        0       0       0          0        0

	WORKFLOW STATUS : COMPLETED | 17/17 ( 100% ) | (all jobs finished successfully)

If anything goes wrong take a look at the (many) files in:

	 /tmp/pegasus_dax/seqware/seqware/pegasus/HelloWorld/run0001

Where "run0001" is replaced with the run mentioned in the ./bin/pegasus-run.pl output.

## Setup SeqWare MetaDB

The SeqWare MetaDB is the common database for tracking metadata throughout the project.  So it's the first of the sub-projects that needs to be setup.  We'll also pre-load it with useful examples so VM users can have something to play with upon first boot.

See [Setup SeqWare MetaDB](/docs/github_readme/3-metadb/) for directions on how to set this up.

## Setup SeqWare Portal

The SeqWare Portal will be the entry point for many users of the VM to explore the sample data bundled with the VM and, ultimately, upload and launch new analysis.

The URL to access the Portal is http://localhost:8080/seqware-portal-<version>

See [Setup SeqWare Portal](/docs/github_readme/5-portal/) for directions on how to set this up.


## Setup SeqWare Web Service

The SeqWare WebService provides data to our command-line utilities. It is installed much like the SeqWare Portal as a Tomcat web application.

The URL to access the Portal is http://localhost:8080/seqware-webservice-<version>

See [Setup SeqWare Web Service](/docs/github_readme/4-webservice/) for directions on how to set this up.


## Next Steps

There are two main next steps:

### Develop and Test New Workflow Bundles

Once you can run the HelloWorlds workflow you should be ready to use this VM for the development of new modules and/or workflows or running/testing existing ones.  Take a look at the [Workflow Manuals](/docs/6-pipeline/#workflow-manuals) for information about how to develop for SeqWare Pipeline.

### Connect the VM to a Real Cluster

One thing to keep in mind is, if you're using this VM that has already been setup you will still want to edit the sge.pm file since it contains paths and ports that will need to change given your environment.  If you're using the VM as a self-contained environment you don't need to worry about this.  But if you're connecting to a real cluster then you need to have these setup properly.  For example you would need to customize the following section in /usr/share/perl5/vendor_perl/Globus/GRAM/JobManager/sge.pm:

	BEGIN
	{
	    $qsub        = '/usr/local/ge-6.1u6/bin/lx24-amd64/qsub';
	    $qstat       = '/usr/local/ge-6.1u6/bin/lx24-amd64/qstat';
	    $qdel        = '/usr/local/ge-6.1u6/bin/lx24-amd64/qdel';
	    #
	    $mpirun      = 'no';
	    $sun_mprun   = 'no';
	    $mpi_pe      = '';
	    #
	    if(($mpirun eq "no") && ($sun_mprun eq "no"))
	      { $supported_job_types = "(single|multiple|condor)"; }
	    else
	      { $supported_job_types = "(mpi|single|multiple|condor)"; }
	    #
	    $cat         = '/bin/cat';
	    #
	    $SGE_ROOT    = '/usr/local/ge-6.1u6';
	    $SGE_CELL    = 'default';
	    $SGE_MODE    = 'SGE';
	    $SGE_RELEASE = '6.1u6';

	    $SGE_ARCH='lx26-amd64';
	    $SGE_EXECD_PORT=6445;
	    $SGE_QMASTER_PORT=6444;
	    $ENV{"SGE_ROOT"} = $SGE_ROOT;
	    $ENV{"SGE_ARCH"}=$SGE_ARCH;
	    $ENV{"SGE_EXECD_PORT"}=$SGE_EXECD_PORT;
	    $ENV{"SGE_QMASTER_PORT"}=$SGE_QMASTER_PORT;
	 }

Make sure the paths and ports are valid for the cluster you're connecting too.  You'll also want to take a look at the other modifications made to sge.pm to see if they are valid for the new cluster you are connecting to.  For example, what you call your parallel environment (serial) and what variable you use to define the memory consumable resource differ from SGE install to SGE install.

Also, keep in mind it's possible to connect to a cluster type different than SGE.  You will need to install the appropriate GRAM configuration RPM and follow any configuration needed for the other cluster type (similar to the way we modified sge.pm).  But in theory you should be able to customize this VM to submit to another SGE, PBS, Condor, etc cluster fairly easily.  See the Globus Toolkit website and docs for more information.

