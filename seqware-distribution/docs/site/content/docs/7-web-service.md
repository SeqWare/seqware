---

title:                 "SeqWare Web Service"
toc_includes_sections: true
markdown:              basic

---


## Overview

The purpose of the Web service in SeqWare is threefold. First, it allows individuals to connect to the database with user-specific permissions. Second, it prevents direct access to the database. Third, it allows remote users to query the database without needing to install the PostgreSQL client locally. 

In the first case, not all users should be able to access all studies in the MetadataDB. Some studies have proprietary information that should not be widely available. PostgreSQL cannot grant row-specific permissions for users. Authentication through the Web service will allow those users to view and change only those rows that they have permission to view.

Secondly, direct access to the database should be discouraged. There is a great deal of business logic built into SeqWare that is not available at the database level. The database allows for much more flexibility than SeqWare Pipeline expects. Therefore it is advisable to redirect all database queries through a business logic layer that will preserve the hierarchy in the database.

Thirdly, remote users can query the database without having to construct an SQL query and without needing to install the PostgreSQL client. We are using a RESTful Web service, in which most of the information needed by the Web service is provided in the HTTP URL and the message type. For example, navigating to /seqware-webservice-0.10.0/workflows is equivalent to 'SELECT * FROM workflow;' in psql, and going to /seqware-webservice-0.10.0/workflows/1 is equivalent to 'SELECT * FROM workflow WHERE sw_accession = 1;'. These queries may be executed either in a browser or programmatically.

### Configuration ###

If you are working on our CentOS VM from [Installation](/docs/2-installation/) your settings file will already be present. Otherwise, your SeqWare settings file needs to be configured to use the Web service rather than the database or no metadata. This file is usually located at ~/.seqware/settings.

There are four variables that need to be changed: SW_METADATA_METHOD, SW_REST_URL, SW_REST_USER, and SW_REST_PASS. The SW_REST_URL is the location of the deployed WebService from the previous step. ''The SW_REST_USER and SW_REST_PASS are the web service username and password''. Below is an example snippet of a .seqware/settings file.

	SW_METADATA_METHOD=webservice
	SW_REST_URL=http://localhost:8080/seqware-webservice-0.11.4
	SW_REST_USER=admin@admin.com
	SW_REST_PASS=admin

### Using the Web Service ###
 
Providing the Web service is already installed for you, there are three approaches to using the Web service. In order from least to most programming, these are the options:

* '''Use SeqWare Pipeline with the Web service enabled:''' The only configuration necessary is to change your .seqware/settings file to point to the Web service. The seqware-pipeline jar will use the Web service instead of a direct database connection with no further changes.
* '''Use the Java API''': When writing SeqWare plugins or workflow modules, you can access the Webservice through the Metadata object. This object gives you more direct control while hiding the business logic. For example, you can install a new workflow, create processing events, and schedule workflow runs programmatically through this system.
* '''Script to the Web service directly''': Which would involve sending HTTP requests to the RESTful URLs and processing the response. Simple queries can also be entered directly into your browser, which will return XML describing the object. For example, you can get an XML representation of all of the studies in the database by going to http://localhost:8080/seqware-webservice-0.11.4/studies. Very little business logic is built into the Web service directly. The exception to this is [Running workflows through the Web service](https://sourceforge.net/apps/mediawiki/seqware/index.php?title=Running_workflows_through_the_Web_service) See the Web Service [API](/docs/11-api/) for more details.

The .seqware/settings file needs to be configured to use the Web service for the first two options. In the third option, you must provide the URL, username and password yourself.

## Setup the Web Service

The SeqWare Web service is the primary mechanism by which users can reach the SeqWare MetaDB. The Web service prevents the user from having to make SQL queries and facilitates building services on top of the MetaDB. Currently, there is a Java client located in the seqware-commons package that can be used to access the WebService, which is configured through the .seqware/settings file.

### Requirements ###
SeqWare Web service requires:
* Apache Tomcat 6.0+
* Access to a Seqware MetaDB PostgreSQL database (See [SeqWare MetaDB](/docs/4-metadb/))
* A locally running PostgreSQL install that has a 'seqware' user with CREATEDB privileges.
* Maven 2.2.1+
* The SeqWare WebService source code. (See [Source Code](/docs/13-code/)) 

### Testing ###

You need a test_seqware_meta_db database for the tests to work. You also need a seqware user (who owns this database) and our sample data loaded. For testing purposes, the Web Service creates its own database using the seqware user, so this user must have CREATEDB privileges in PostgreSQL.  Also, you should setup the plpgsql language in the test_seqware_meta_db, a very easy way to do this is to set it up in the template1 database.  Again, see the MetaDB setup guide for both steps.

If you've setup your seqware user with createdb privileges and the plpgsql language you can actually just build and test the web service with Maven, it has a built-in Jetty server that will startup and test the web service.  You can trigger this just by doing:

	mvn clean install

If you want to startup the Jetty server for interactive testing you can simply do:

	mvn jetty:run

### Configuring the database connection ###

In the source, two files need to be edited with database information in order to successfully build, test and run the Web service on Apache Tomcat (assuming you want to point it to your real DB rather than the test DB created above).

These files are located at:

* Running: [seqware-webservice/src/main/webapp/META-INF/context.xml](https://github.com/SeqWare/seqware/blob/master/seqware-webservice/src/main/webapp/META-INF/context.xml) - change all parameters to match your local seqware_meta_db connection.
* Testing: [seqware-webservice/src/main/webapp/WEB-INF/jetty-env.xml](https://github.com/SeqWare/seqware/blob/master/seqware-webservice/src/main/webapp/WEB-INF/jetty-env.xml) - leave the DB name as test_seqware_meta_db. This DB will be dropped and re-created at test runtime.

Three variables need to be changed in each file to reflect your local setup:

* url="jdbc:postgresql://localhost:5432/seqware_meta_db"
* username="seqware"
* password="password"

The url, username and password need to be changed to reflect the local database. ''The username and password are the PostgreSQL database username and password.''

### Building ###
In order to run all of the tests and create the WAR file for deployment, you can run the following from the root directory of the trunk. The WS has dependencies to seqware-common, seqware-pipeline, and seqware-queryengine, so all of those need to be available to Maven for building.

	mvn clean install -DskipTests

Please see [the latest build notes](https://github.com/SeqWare/seqware/blob/master/README.md) for further details.

Running the above should run all of the tests and create the WAR file and XML file for deployment in seqware-webservice/target. The reason for skipping the tests is because the tests attempt to create and connect to a new database (named "test_seqware_meta_db") and they will fail if the Web service is not configured to connect to this database. Since you changed the database to your 'real' database in the last step, the tests will fail.

### Deploying ###
In order to deploy the Web service into Tomcat, drop the WAR from seqware-webservice/target into the webapps directory, and the XML into TOMCAT_HOME/conf/Catalina/localhost (maps to /etc/tomcat6/Catalina/localhost/ on many Linux distributions). On the SeqWare VM, these directories are /var/lib/tomcat6/webapps and /etc/tomcat6/Catalina/localhost.

SeqWare WebService consumes quite a bit of memory, so configure your Tomcat instance with the following attributes:

	JAVA_OPTS= -server -Xss1024K -Xms1G -Xmx2G -XX:MaxPermSize=128M -XX:NewSize=512m

This environment variable should either be set on your command line or in the conf/tomcat6.conf file, depending on your setup. On our production machines, these memory values are all doubled.

Restart Tomcat with <tt>bin/shutdown.sh;bin/startup.sh</tt> (<tt>/etc/init.d/tomcat6 restart</tt> for Tomcat 6).

You can double-check whether this setting was successfully set by going to http://localhost:8080/manager/status/all and checking the JVM section. You may need to edit your <tt>/etc/tomcat6/tomcat-users.xml</tt> file and add/enable the following lines in order to enable access to the tomcat manager

	<role rolename="manager"/>
	<role rolename="admin"/>
	<user username="admin" password="admin" roles="admin,manager"/>

In some cases, tomcat will ignore configuring the JAVA_OPTS either on command line or in the /etc/init.d/tomcat6 . In these cases, you can try adding a setenv.sh file with the single line 

	export JAVA_OPTS="-server -Xss1024K -Xms1G -Xmx2G -XX:MaxPermSize=128M -XX:NewSize=512m"

''Note for Tomcat 6 Users'': Tomcat may ship with an outdated postgresql-jdbc jar that will conflict with the jar in the WAR. Replace any postgresql-jdbc jar in the lib directory with the most recent version of postgresq-jdbc (We use 9.0-801.jdbc3).

The jar will deploy at http://localhost:8080/seqware-webservice-0.11.4, or http://host.address:port/seqware-webservice-x.x.x, where your host.address is the address of the machine running the Web service, the port is the Tomcat port, and seqware-webservice-x.x.x is the appropriate version number on the seqware-webservice WAR. Navigating to this address in your browser should not show an error message (you will have to authenticate with an appropriate username and password as specified in the MetadataDB).

## Coming Soon ##

*This guide is a work in progress.* In the future this will include more information on the following topics.

### Admin Setup

### Features

### Reporting

### Workflow Launching, Monitoring

### Data Retrieval


