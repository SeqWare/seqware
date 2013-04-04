    AUTHOR:       boconnor@oicr.on.ca
    PROJECT:      SeqWare Web Service
    LAST UPDATED: 2013-04-04

### Testing ###

For testing purposes, the Web Service creates its own database using the seqware user, so this user must have CREATEDB privileges in PostgreSQL. This database will be called test_seqware_meta_db and is automatically dropped and re-created during our tests. You should setup the plpgsql language in the template1 database so it automatically transfers to the test database when it is created. Again, see the MetaDB setup guide for both steps.

If you've setup your seqware user with createdb privileges and the plpgsql language you can actually just build and test the web service with Maven, it has a built-in Jetty server that will startup and test the web service.  You can trigger this just by doing:

	mvn clean install -DskipITs=false

If you want to startup the Jetty server for interactive testing you can simply do:

	mvn jetty:run

You will need to make sure that your ~/.seqware/settings file includes the line 

	SW_REST_URL=http://localhost:8889/seqware-webservice

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
In order to build without running all of the tests and create the WAR file for deployment, you can run the following from the root directory of the trunk. The WS has dependencies to seqware-common, seqware-pipeline, and seqware-queryengine, so all of those need to be available to Maven for building.

	mvn clean install -DskipTests

Please see [the latest build notes](https://github.com/SeqWare/seqware/blob/master/README.md) for further details.

Running the above should skip all of the tests and create the WAR file and XML file for deployment in seqware-webservice/target. The reason for skipping the tests is because the tests attempt to create and connect to a new database (named "test_seqware_meta_db") and they will fail if the Web service is not configured to connect to this database. Since you changed the database to your 'real' database in the last step, the tests will fail.

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

