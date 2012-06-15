    PROJECT: SeqWare
    FILE: README.md
    UPDATED: 20120530
    HOMEPAGE: http://seqware.sourceforge.net

INTRODUCTION
====================

This is top level of the SeqWare Project. For now, this contains work on the
SeqWare query engine version 2 and the documentation for the seqware-webservice. 

Eventually this will also contain the components of the SeqWare project that 
follow and are currently hosted at sourceforge:
* seqware-meta-db
* seqware-webservice
* seqware-portal
* seqware-pipeline
* seqware-queryengine

In addition the seqware-common sub-project provides a location for common code
and most of the other sub-projects have this as a dependency.

BUILDING
========

PREREQUISITES
-------------

Currently we are evaluating two serialization frameworks: [Kryo](http://code.google.com/p/kryo/) and [protobuf](http://code.google.com/p/protobuf/)

Protobuf requires the following installation steps:

    wget http://protobuf.googlecode.com/files/protobuf-2.4.1.tar.gz
    cd protobuf-2.4.1
    ./configure
    make
    make install

BUILDING THE PROJECT
--------------------

We're moving to Maven (2.2.x or greater) for our builds, this is currently how
you do it in the trunk directory:

    mvn clean install

You can also skip the tests for a faster build with:

    mvn clean install -Dmaven.test.skip=true
  
You can also build individual components such as the new query engine with: 

    cd seqware-queryengine
    mvn clean install

LOCAL UNIT TESTING SETUP
------------------------

Get HBase. Either versions 0.92.1 and 0.94.0 are fine:

    wget http://apache.raffsoftware.com/hbase/hbase-0.94.0/hbase-0.94.0.tar.gz
    tar xzf hbase-0.94.0.tar.gz
    cd hbase-0.94.0

Prepare a local directory for the HBase database by populating the file `conf/hbase-site.xml` with (adjust the file path `/opt/local/var/db/hbase` to your needs):

    <?xml version="1.0"?>
    <?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
    <configuration>
      <property>
        <name>hbase.rootdir</name>
        <value>file:///opt/local/var/db/hbase</value>
      </property>
    </configuration>

Start the HBase server:

    ./bin/start-hbase.sh

Stopping the HBase server is similarly simple:

    ./bin/stop-hbase.sh

Some libraries make use of HBase's Stargate interface (a REST interface), which can be started via:

    ./bin/hbase rest start

INSTALLING
====================

See http://seqware.sourceforge.net for detailed installation instructions
including links to a pre-configured virtual machine that can be used for
testing, development, and deployment.


COPYRIGHT
====================

Copyright 2008-2012 Brian D O'Connor, Denis Yuen, Joachim Baran

LICENSE
====================

SeqWare is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SeqWare is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SeqWare.  If not, see <http://www.gnu.org/licenses/>.
