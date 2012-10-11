PACKAGE: SeqWare LIMS
AUTHOR: boconnor@ucla.edu
UPDATED: 4/6/2010
VERSION: 0.7.0
HOMEPAGE: http://seqware.sourceforge.net

INTRODUCTION:

This web application is used to track the processing of SeqWare sequence data
through the SeqWare-supplied pipeline, alignment to a reference genome, and
report generation.  It is part of the larger SeqWare project that looks to
streamline the manupulation of sequence data produced by next gen sequencers.
The SeqWare LIMS system is closely tied with the SeqWare Pipeline software that
controls the actual processing, alignment, and annotation of sequence data.
SeqWare LIMS collects various bits of metadata related to each flowcell and lane
used in the sequencer and passes this information via a common database backend
to the SeqWare Pipeline software.  SeqWare Pipeline then records the status of
each step in the processing pipeline back to the database.  These "processing"
messages are then displayed in the LIMS system.  This makes it very easy for
researchers to both follow the progress of a particular run and also get back
to the reports automatically created based on their initial settings.

As of December 2009, we are working on an overhaul of SeqWareLIMS to better
support Short Reach Archive (SRA)-style metadata.  And to also support arbitrary
workflow execution through the SeqWare Pipeline.


REQUIREMENTS:

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


INSTALLING:

If you just want to get a copy installed without compiling, perform the following steps:

* Download and install apache-tomcat, postgresql and java. Typically this
  involves using yum or apt-get depending on your Linux distribution. You can
  also install manually. The steps below assume your apache tomcat install is
  located in /<some_dir>/apache-tomcat-6.0.13/ If you installed via yum/apt-get
  the location of config/war files may be different and the way you start the
  server may be different as well. 

* Setup the postgres database. These examples assume the postgres user on your system
  is named 'postgres'.

** Create a database user for the LIMS with the command:
   $ sudo -u postgres psql -c "CREATE USER seqware WITH PASSWORD 'password';"
   Where seqware is the user and password is the password for this user.

** Make sure pg_hba.conf has an appropriate authentication line for your LIMS user.
   If your database is on the same machine as your webserver, you may want:

   #HOST   DATABASE          USER                              AUTH
   local   seqware_meta_db   seqware                            md5

   #Added above the system default of:
   local   all           all                               ident sameuser

** You should restart the database daemon once you make these changes.

   sudo /etc/init.d/postgresql restart

** Create a database using the command:

   $ sudo -u postgres psql --command "CREATE DATABASE seqware_meta_db WITH OWNER = seqware;"

   Where seqware_meta_db is the name of your new database and seqware is the name of your
   database user.

** Populate the database by loading the provided dump:

   $ sudo -u postgres psql < ../seqware-meta-db/seqware_meta_db.sql seqware_meta_db -U seqware

   You will be prompted to enter the password for your database user.

** Check the PostgreSQL documentation if you have any problems with the above steps.

* Copy dist/SeqWareLIMS.war to the apache-tomcat-6.0.13/webapps directory of your tomcat
  installation

* In tomcat, create the subdirectory apache-tomcat-6.0.13/conf/Catalina/localhost/

* Copy dist/SeqWareLIMS.xml to apache-tomcat-6.0.13/conf/Catalina/localhost

* Copy web/WEB-INF/lib/postgresql-jdbc3.jar to apache-tomcat-6.0.13/lib/

* Modify apache-tomcat-6.0.13/conf/Catalina/localhost/SeqWareLIMS.xml to reflect the
  correct username, password and url for your database.


STARTING:

* You can now start the server using the apache-tomcat-6.0.13/bin/startup.sh script and
  you can stop the server using the shutdown.sh script.
  
* Once the service is running you should be able to load http://localhost:8080/SeqWareLIMS,
  register an account, and start setting up SeqWare experiments.

* There is a default admin user setup in the seqware_meta_db. The username is
  'admin@admin.com' and the password is 'admin'. Make sure you update this account in
  the seqware_meta_db before making your SeqWare LIMS publically available.


BUILDING (Optional):

I use the Eclipse IDE for coding and building the application.  There's an Ant file 
included that manages the build, you can call this from the command line, use the
"build-war" target.  Here's the steps I go through to setup and build SeqWareLIMS
using Eclipse, your setup may be different:

* Download the source code from http://sf.net/projects/seqware subversion into a local
  directory, I'm using /home/username/svnroot/seqware/seqware-lims

* Start Eclipse, choose File->New->Project then select "Java Project" and, on the
  next page, choose "Create project from existing source" and enter the directory
  for your svn checkout of SeqWareLIMS e.g.
  /home/username/svnroot/seqwaretools/seqware-lims

* Add all the jar files in 
  seqware-lims/web/WEB-INF/lib under the "Libraries" tab.  If you compiling this on
  the command line make sure all these jars are in your classpath. Also, you should
  add the catalina-ant.jar and servlet-api.jar files from your Apache Tomcat installation.

* Click "Finish".  You now have a SeqWareLIMS project setup in Eclipse.

* Update any hard-coded information in the build.xml file, for example the port of the
  Apache Tomcat server.

* Under the Ant tab on the right-hand side of Eclipse, click the + icon and add the
  build.xml file.  This will let you use the build targets to compile and package.

* Under the Ant tab, select the "init" item and run it using the "play" button, this
  will setup some directories.

* Set the env var CATALINA_HOME, e.g. CATALINA_HOME=/opt/apache-tomcat-6.0.13. 
  Also set J2EE_HOME and JAVA_HOME.

* Setup the database user with something like this: "create user seqware with password 'seqware' createdb;"
  Done as the postgres user. Then log in as the seqware user and issue a:
  "create database seqware_meta_db".  Change the database settings in database.properties if you're
  using a different database or user name. See the INSTALLING section for more details.

* Use the "database-create" Ant target to setup the database.  If you get errors about users
  or permissions you haven't setup the seqware user or database correctly.  You can always try
  manually setting up the database, just make sure you can connect with the seqware username
  and password. Again see the INSTALLING section for more details.

* Build and package the application using the "build-war" target.

* You shouldn't see any errors in the console.  If everything worked you should now have a
  dist/SeqWareLIMS.war and a dist/SeqWareLIMS.xml file.  Move the war file into
  /opt/apache-tomcat-6.0.13/webapps and the xml file into
  /opt/apache-tomcat-6.0.13/conf/Catalina/localhost/

* Make sure you've added the postgresql-jdbc3.jar PostgreSQL driver to apache-tomcat-6.0.13/lib.
  If you're using MySQL, make sure you copy the driver here instead.  You can find the PostgreSQL
  driver in the /home/username/svnroot/seqwaretools/seqware-lims/web/WEB-INF/lib/ directory.
  Be sure to update the reference in database.properties and make sure you use the correct driver
  for your version of postgreSQL (http://jdbc.postgresql.org/download.html).

* Once you're ready to deploy the application you just need to move the SeqWareLIMS.war file
  and SeqWareLIMS.xml settings file to the production web server's correct directories and restart.
  You can change the DB the application points to by editing the SeqWareLIMS.xml file.

* You can now start the server using the apache-tomcat-6.0.13/bin/startup.sh script and 
  you can stop the server using the shutdown.sh script. If you have installed Tomcat via RPMs
  on CentOS or Fedora then you can restart it using the /etc/init.d/tomcat6 script.

* Once the service is running you should be able to load http://localhost:8080/SeqWareLIMS,
  register an account (or use the built-in admin account), and start setting up SeqWare experiments.


ADDITIONAL HELP:

Email the user or developer list at the SourceForge homepage 
http://sourceforge.net/projects/seqwaretools

