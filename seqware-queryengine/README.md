## Installation 

We can start by getting your development environment setup with the appropriate prerequisites. The [Installing with a Local VM](/docs/2-installation/) guide will give you access to a VM which has these setup correctly. However, if you wish to set this up yourself and you have git and mvn installed:

1.	You will want to go to the [SeqWare Github repository](https://github.com/SeqWare/seqware) and run the command <code>git clone git@github.com:SeqWare/seqware.git seqware_github</code>     
2. 	If you want to have the Query Engine connect to a distributed Hadoop and HBase cluster, we highly recommend [Cloudera's CDH packages](https://ccp.cloudera.com/display/CDH4DOC/CDH4+Quick+Start+Guide) since they are tested for package incompatibilities between Hadoop projects (which are still common). It is also worth double-checking the web interface for HBase which is usually at [http://localhost:60010/master-status](http://localhost:60010/master-status) once you have gone through the setup tutorials in order to confirm that HBase is setup correctly (replace localhost with the name of your master node if working on a distributed Hadoop install).

For both the VM and a local development environment, continue here:

There are a number of constants that may have to be set, particularly for a developer. These are currently in the <code>com.github.seqware.queryengine.Constants</code> file although can be overridden by ~/.seqware/settings. In particular, you should set your <code>NAMESPACE</code> to avoid collisions with other developers and if you wish for your distribution jar to be automatically copied to the cluster when launching MapReduce tasks, you will need to correct the <code>DEVELOPMENT_DEPENDENCY</code>.
<p class="warning"><strong>Note:</strong>
	   It is important that you check your <code>NAMESPACE</code>, <code>HBASE_REMOTE_TESTING</code>, and <code>HBASE_PROPERTIES</code> variables. They currently control the prefix for your tables, whether you connect to a local install of HBase, and which remote install of HBase you want to connect to respectively. These settings can also be controlled via the <code>~/.seqware/settings</code> settings file and in this case, the settings file will override the hard-coded variables. Instructions on how to create these key-values are available inside <code>com.github.seqware.queryengine.Constants</code>.
</p>

For this tutorial, use the following values in your ~/.seqware/settings

	#
	# SEQWARE QUERY ENGINE AND GENERAL HADOOP SETTINGS
	#
	HBASE.ZOOKEEPER.QUORUM=localhost
	HBASE.ZOOKEEPER.PROPERTY.CLIENTPORT=2181
	HBASE.MASTER=localhost:60000
	MAPRED.JOB.TRACKER=localhost:8021
	FS.DEFAULT.NAME=hdfs://localhost:8020
	FS.DEFAULTFS=hdfs://localhost:8020
	FS.HDFS.IMPL=org.apache.hadoop.hdfs.DistributedFileSystem

	# SEQWARE QUERY ENGINE SETTINGS
	QE_NAMESPACE=BATMAN
	QE_DEVELOPMENT_DEPENDENCY=file:/home/seqware/Development/gitroot/seqware-github/seqware-distribution/target/seqware-distribution-0.13.6.5-qe-full.jar
	QE_PERSIST=true
	QE_HBASE_REMOTE_TESTING=false
	QE_HBASE_PROPERTIES=LOCAL
	

1. 	Refresh the code for the query engine by doing a <code>git fetch</code> and <code>git pull</code> in the seqware_github directory. On the VM, you may need to merge changes or simply discard changes with a command such as <code>git checkout seqware-queryengine/src/main/java/com/github/seqware/queryengine/Constants.java</code>
2. 	If the [web interface](http://localhost:60010/master-status) for HBase stalls or is inactive, you may need to restart the HBase processes. This can be done by the following commands:
	<pre title="Title of the snippet">
	sudo - root (or sudo bash)
	/etc/init.d/hbase-regionserver stop
	/etc/init.d/hbase-master stop
	/etc/init.d/zookeeper-server stop
	/etc/init.d/hbase-regionserver start
	/etc/init.d/hbase-master start
	/etc/init.d/zookeeper-server start
	jps	
	</pre>
3. 	When setup of Hadoop and HBase is complete, you can go into the query-engine directory, compile it, and run the tests. Note that theintegration tests will spin-up a mini-HBase cluster and perform MapReduce tasks (~10 minutes for integration tests). Please note that the web-service and legacy directories in the root have additional dependencies and may not necessarily compile following only these instructions.
	<pre title="Title of the snippet">
	mvn clean install -DskipITs=false
	mvn javadoc:javadoc
	mvn javadoc:test-javadoc
	</pre>
This will generate javadoc documentation for both the main code and the testing code in <code>seqware-queryengine/target/site/apidocs/index.html</code> and <code>seqware-queryengine/target/site/testapidocs/index.html</code> respectively. 

