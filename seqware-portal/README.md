## Build

The Portal is built and unit tested as with any other SeqWare component, using:

	mvn clean install (builds and runs unit tests)

## Installation 

### Requirements

These requirements are based on what I'm using for developement.  You may be able 
to use older/newer versions but they may or may not work.  You'll find many of the
required jars in the lib directory in svn.

This is what you'll need to build/run the web applications:

* apache-tomcat >= 6.0.13
* Java SE >= 1.6
* Java EE >= 5
* postgresql >= 8.1.3
* PostgreSQL JDBC Driver (postgresql-jdbc3.jar)
* apache-ant >= 1.7.0
* hibernate >= 3.2
* spring-framework >= 2.1

### Procedure

If you just want to get a copy installed without compiling, perform the following steps:

* Download and install apache-tomcat, postgresql and java. Typically this
  involves using yum or apt-get depending on your Linux distribution. You can
  also install manually. The steps below assume your apache tomcat install is
  located in /<some_dir>/apache-tomcat-6.0.13/ If you installed via yum/apt-get
  the location of config/war files may be different and the way you start the
  server may be different as well. 

* Follow the SeqWare MetaDB [install guide](http://seqware.github.com/docs/github_readme/3-metadb/) to setup your SeqWare MetaDB

* Copy target/seqware-portal-<version>.war to the apache-tomcat-6.0.13/webapps directory of your tomcat installation

* In tomcat, create the subdirectory apache-tomcat-6.0.13/conf/Catalina/localhost/

* Copy target/seqware-portal-<version>.xml to apache-tomcat-6.0.13/conf/Catalina/localhost

* Copy web/WEB-INF/lib/postgresql-jdbc3.jar to apache-tomcat-6.0.13/lib/

* Modify apache-tomcat-6.0.13/conf/Catalina/localhost/seqware-portal-<version>.xml to reflect the
  correct username, password and url for your database.

### Starting the Web Service 

* You can now start the server using the apache-tomcat-6.0.13/bin/startup.sh script and
  you can stop the server using the shutdown.sh script.
  
* Once the service is running you should be able to load http://localhost:8080/seqware-portal-<version>,
  register an account, and start setting up SeqWare experiments.

* There is a default admin user setup in the seqware_meta_db. The username is
  'admin@admin.com' and the password is 'admin'. Make sure you update this account in
  the seqware_meta_db before making your SeqWare Portal publically available.
