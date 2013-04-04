## Build

We can start by getting your development environment setup with the appropriate prerequisites. The [Installing with a Local VM](/docs/2-installation/) guide will give you access to a VM which has these setup correctly. However, if you wish to set this up yourself and you have git and mvn installed please continue.

If you already have HBase setup (or are using the VM), it is also worth double-checking the web interface for HBase which is usually at [http://localhost:60010/master-status](http://localhost:60010/master-status) before going through our tutorials in order to confirm that HBase is setup correctly (replace localhost with the name of your master node if working on a distributed Hadoop install).

At this point, the Query Engine is built and unit tested as with any other SeqWare component, using:

	mvn clean install (builds and runs unit tests)

### Prerequisites ON Mac OS

We use [protobuf](http://code.google.com/p/protobuf/) to handle serialization and de-serialization.

On Mac OS, Protobuf requires the following installation steps:

    wget http://protobuf.googlecode.com/files/protobuf-2.4.1.tar.gz
    tar xzf protobuf-2.4.1.tar.gz
    cd protobuf-2.4.1
    ./configure
    make
    make install

### Integration Testing 

However, if you wish to do some development or run the integration tests, there are usually a number of constants that have to be set, particularly for a developer. These are currently in the <code>com.github.seqware.queryengine.Constants</code> file although can be overridden by ~/.seqware/settings. In particular, you should set your <code>NAMESPACE</code> to avoid collisions with other developers and if you wish for your distribution jar to be automatically copied to the cluster when launching MapReduce tasks, you will need to correct the <code>DEVELOPMENT_DEPENDENCY</code>.
<p class="warning"><strong>Note:</strong>
	   It is important that you check your <code>NAMESPACE</code>, <code>HBASE_REMOTE_TESTING</code>, and <code>HBASE_PROPERTIES</code> variables. They currently control the prefix for your tables, whether you connect to a local install of HBase, and which remote install of HBase you want to connect to respectively. These settings can also be controlled via the <code>~/.seqware/settings</code> settings file and in this case, the settings file will override the hard-coded variables. Instructions on how to create these key-values are available inside <code>com.github.seqware.queryengine.Constants</code>.
</p>

For our tutorial, use the following values in your ~/.seqware/settings

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

## Installation

By default, our integration test suite runs tests against the [hbase-maven-plugin](https://github.com/wibidata/hbase-maven-plugin). You can, however, run the full test suite against a real Hadoop and HBase cluster; for setup, a good start is to follow Cloudera's [quick start guide](https://ccp.cloudera.com/display/CDH4DOC/CDH4+Quick+Start+Guide). You will then need to set the HBase configuration in `seqware-queryengine/src/main/java/com/github/seqware/queryengine/Constants.java` by turning on HBASE_REMOTE_TESTING and completing a family of terms for HBASE\_PROPERTIES. You can also set these in an external ~/.seqware/settings file.

Note that when you setup a real Hadoop/HBase cluster and point the query engine toward it using ~/.seqware/settings, this is essentially the installation procedure for BHase.

If you run into the following error when the hbase-plugin starts up, please check for an incorrect entry in your <code>/etc/hosts</code> file.

    org.apache.hadoop.hbase.client.NoServerForRegionException: Unable to find region for  after 10 tries.
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegionInMeta(HConnectionManager.java:908)
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegion(HConnectionManager.java:814)
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegion(HConnectionManager.java:782)
    at org.apache.hadoop.hbase.client.HTable.finishSetup(HTable.java:249)
    at org.apache.hadoop.hbase.client.HTable.<init>(HTable.java:213)
    at org.apache.hadoop.hbase.HBaseTestingUtility.startMiniHBaseCluster(HBaseTestingUtility.java:526)

In particular, recent versions of Debian (including Ubuntu and Linux Mint) have on the second line <code>127.0.1.1  \<your hostname\></code> which should be modified to <code>127.0.0.1  \<your hostname\></code>

You can find the original bug report showing that this was done on purpose here: http://bugs.debian.org/cgi-bin/bugreport.cgi?bug=316099

I don't think RedHat-based distributions use this same convention.

If you run into the following error when the hbase-plugin starts up, please check for an incorrect entry in your <code>/etc/hosts</code> file.

    org.apache.hadoop.hbase.client.NoServerForRegionException: Unable to find region for  after 10 tries.
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegionInMeta(HConnectionManager.java:908)
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegion(HConnectionManager.java:814)
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegion(HConnectionManager.java:782)
    at org.apache.hadoop.hbase.client.HTable.finishSetup(HTable.java:249)
    at org.apache.hadoop.hbase.client.HTable.<init>(HTable.java:213)
    at org.apache.hadoop.hbase.HBaseTestingUtility.startMiniHBaseCluster(HBaseTestingUtility.java:526)

In particular, the latest (v. 13) version of Linux Mint has on the second line <code>127.0.1.1  \<your hostname\></code> which should be modified to <code>127.0.0.1  \<your hostname\></code>  

