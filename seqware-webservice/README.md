AUTHOR:       boconnor@ucla.edu
PROJECT:      SeqWare Query Engine - Webserver
LAST UPDATED: 2010-01-04

DESCRIPTION:

See README in the root directory for more information about this project.

TESTING:

Setup metadatabase:

You need to create a database to track metadata such as the location of the
various databases to be served via this web interface. See the Postgresql
documentation for more information, these are the steps I use to setup the
database.

 createuser -P -E seqware 
 createdb -O seqware seqware_meta_db
 # setup your pg_hba.conf, see http://www.postgresql.org/docs/8.3/interactive/auth-pg-hba-conf.html
 psql -h localhost -U seqware -W seqware_meta_db < /tmp/seqware_meta_db.sql

Start server:
 
 # setup database
 # see the postgresql docs on how to do this
 # start test server
 # server uses environmental variables, change these as needed
 export DBSERVER=localhost
 export DB=seqware_meta_db
 export USER=seqware
 export PASS=seqware
 export ROOTURL=http://<host>:8181 # can be used if you want to have a more complex URL e.g. http://server/<rooturl>/queryengine/...
 export WORKINGDIR=<path to server dir>
 export MAXCONNECTIONS=4  # the max number of simultaneous connections to each DB
 ant test-server

At this point you then should be able to load the following URL in your browser:

 http://localhost:8181/queryengine

You can also start the server manually:
 
 # define the export variables above then do:
 java -Ddbserver=$DBSERVER -Ddb=$DB -Duser=$USER -Dpass=$PASS -Drooturl=$ROOTURL -Dworkingdir=$WORKINGDIR -Dmaxconnections=$MAXCONNECTIONS -cp ../backend/lib/db.jar:../backend/dist/seqware-qe-0.4.0.jar:lib/org.restlet.jar:dist/seqware-qe-ws-0.4.0.jar:lib/freemarker.jar:lib/postgresql-jdbc3.jar net.sourceforge.seqware.queryengine.webservice.controller.SeqWareWebServiceMain

 # here's another example pointing it to a specific BerkeleyDB install
 export DBSERVER=10.1.1.2
 export DB=seqware_meta_db
 export USER=seqware
 export PASS=seqware
 export ROOTURL=http://genome.ucla.edu/seqware
 export WORKINGDIR=/home/solexa/svnroot/solexatools/solexa-queryengine/webservice
 export URLHACK=/
 export MAXCONNECTIONS=4
 /home/solexa/programs/jdk1.6.0_13/bin/java -Djava.library.path=/home/solexa/programs/BerkeleyDB.4.7/lib -Durlhack=$URLHACK -Ddbserver=$DBSERVER -Ddb=$DB -Duser=$USER -Dpass=$PASS -Drooturl=$ROOTURL -Dworkingdir=$WORKINGDIR -Dmaxconnections=$MAXCONNECTIONS -cp ../backend/lib/db.jar:../backend/dist/seqware-qe-0.4.0.jar:lib/org.restlet.jar:dist/seqware-qe-ws-0.4.0.jar:lib/freemarker.jar:lib/postgresql-jdbc3.jar net.sourceforge.seqware.queryengine.webservice.controller.SeqWareWebServiceMain

