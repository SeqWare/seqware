seqware
=======

This is the SeqWare Project's main repo.

Local unit-testing set-up
-------------------------

Get HBase. Prepare a local directory for the HBase database by populating the file `conf/hbase-site.xml` with (adjust the file path to your needs):

    <?xml version="1.0"?>
    <?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
    <configuration>
      <property>
        <name>hbase.rootdir</name>
        <value>file:///opt/local/var/db/hbase</value>
      </property>
    </configuration>

Start the HBase server:

    start-hbase.sh

Stopping the HBase server is similarly simple:

    stop-hbase.sh

Some libraries make use of HBase's Stargate interface (a REST interface), which can be started via:

    ./bin/hbase rest start

