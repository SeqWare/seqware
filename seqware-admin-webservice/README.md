# Build 

The seqware-admin-web service is built using:

    mvn clean install (builds and runs unit tests)

Integration tests are in a different directory:

    mvn clean install -DskipITs=false (builds and runs integration tests)
    mvn clean install -DskipITs=false -P extITs (builds and runs integration tests as well as extended integration tests)

# Running

## Prerequisite

By default, you will need a postgres instance running with the test_seqware_meta_db database available.
If you wish to use a different postgres database, edit the seqware_meta_db properties in the root pom.xml file, rebuild, and re-run.

## Running from NetBeans IDE

* Use one of:
    * NetBeans 7.3.0 with Glassfish 3.1.2.2 included 
    * NetBeans 7.3.1+ and download GlassFish 3.1.2.2 and install it as a server 
* Go through the tutorial above http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/RESTfulWebServices/RESTfulWebservices.htm?print=preview and follow steps 1 through 3a under "Generate RESTful Web Services" You want to register a JDNI name which is consistent with your local install of seqware metadb. You may need to add a Database connection under the Services tab if you have not already
* Run the project as a whole  

## Running in standalone Glassfish

* mvn clean install
* cd to your Glassfish 3.1.2.2 installation binaries (or include them in your path) (cd ~/glassfish3/bin)
* change the domain port to 38080 by opening /glassfish/domains/domain1/config/domain.xml and changing the default port from 8080 to 38080
* start your Glassfish server ./asadmin start-domain --verbose 
* copy the postgres jar into the domain lib (cp ~/seqware-sandbox/seqware-admin-webservice/target/lib/postgresql-9.1-901.jdbc4.jar ~/glassfish3/glassfish/domains/domain1/lib)
* list existing applications ./asadmin list-applications
* undeploy an existing application ./asadmin undeploy seqware-admin-webservice
* deploy the web service ./asadmin deploy --name seqware-admin-webservice --contextroot seqware-admin-webservice   ~/seqware_github/seqware-admin-webservice/target/seqware-admin-webservice-1.0.7-SNAPSHOT.war 
* browse to http://localhost:38080/seqware-admin-webservice/test-services.html

## Running with embedded Glassfish with a maven target

* mvn embedded-glassfish:run

# Development

If the database changes note that re-generation will destroy all modifications in the following directories:

* src/main/java/io/seqware/webservice/generated/client
* src/main/java/io/seqware/webservice/generated/controller
* src/main/java/io/seqware/webservice/generated/model

Therefore, we recommend that customization occur in the following directories:

* src/main/java/io/seqware/webservice/adapter
* src/main/java/io/seqware/webservice/client
* src/main/java/io/seqware/webservice/controller


# Troubleshooting

* If you have issues starting the server, check that you do not have other services blocking the port 38080 (such as apache2 or tomcat)
* Currently, when deleting a object with foreign key references to it (for example an experiment referenced by samples), deletion should fail with HTTP status 500 (javax.servlet.ServletException: javax.transaction.RollbackException: Transaction marked for rollback.) 
* A successful delete will return with HTTP status 200 
* Cascading is controlled by JPA annotations on the actual model objects
     * for example, in the current io.seqware.webservice.model.Experiment, deletes will cascade to ProcessingExperiments and Experiment attribute due to CascadeType.All


# Background and Historical Info

## The goal here was to autogenerate:

* web service
* java web service client
* javascript web service client

## The tech used appears to be:

* for data objects: http://en.wikipedia.org/wiki/Java_Persistence_API
* for REST: http://en.wikipedia.org/wiki/Jax-rs

## The idea is this could:

* replace the existing web service
    * improve coverage of tables
    * improve speed
* replace the existing java client
    * chance to cleanup a lackluster API
* possibly be a starting point for new versions of:
    * portal
    * future monitoring tools

The procedure used to auto-generate the the client, database model, and REST endpoints was
http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/RESTfulWebServices/RESTfulWebservices.htm?print=preview

## For the evaluation:

* generate the above
    * possibly need simple script for making client
* test the querying and creating of objects in the DB... can I replicate the client from old SeqWare?
* implement a reporting endpoint... how hard is this?
* write a quick script that will test a few "big" queries on both WebServices
* write a quick markdown README.md that has the comparison results graphed and notes from here
* check into git
* will later need to deal with authentication... maybe just set it up so that only admins can connect to this service?

## Problems:

* how would I make a war+XML and deploy in tomcat7?
* this seems like the way you create new templates: https://platform.netbeans.org/tutorials/nbm-filetemplates.html
* I'm interested in customizing: JpaControllersFromEntities
    * look at ./j2ee.persistence/src/org/netbeans/modules/j2ee/persistence/wizard/jpacontroller/
* where do I add SSL, authentication, custom authentication against registration table, custom rules for authorization?
* the OneToMany collections in the model are not populated yet the ManyToOne collections are!?
    * looks like this is related to loops in the XML/Json... see @XmlTransient
    * It looks like I will need to walk my tree and make sure @XmlTransient is going in the correct order Study->Exp->Sample etc.  If not I need to correct (probably not too bad) and may need to provide helper methods if I need bi-directional traversal.  See http://stackoverflow.com/questions/2219694/cyclic-serialisation-with-many-to-many-relationship-with-hibernate
* This page http://jaxb.java.net/guide/Mapping_cyclic_references_to_XML.html in the "Unofficial JAXB Guide" offers three strategies for dealing with cycles. They are (in summary):

	Mark one of the reference attributes that form the cycle as @XmlTransient.
	Use @XmlID and @XmlIDREF so that the references are represented using XML ids arather than by containment.
	Use the CycleRecoverable interface to deal with cycles programmatically.

* it's not at all clear how persistence.xml gets configured to connect to a specific database... I had to do this in the GUI by editing the presistence.xml in the non-source view and setting up a new DB connection that way... really terrible, want this to be a clear setting somewhere 

## Fixes:

* first, upgrade to the latest Netbeans 7.3 and make sure the plugins are upgraded... older versions did not work for me!
* in the Generated classes window when doing "New RESTful Web Services from Database" choose "Use Jersey..."
* FIXED, use JavaEE6 -- when setting up the initial web app choose Java EE 5 not 6 because of https://netbeans.org/bugzilla/show_bug.cgi?id=216345
* FIXED, kinda, the NPE is no longer being thrown -- there is no logging in the web service... exceptions are ignored!  It looks like I can modify the template that generates these. But still, what a pain!
* FIXED, J2EE6 doesn't have this code -- the autogenerated code is really low quality, for example, no null checking:

	Collection<Lane> laneCollectionNew = organism.getLaneCollection();
	for (Lane laneCollectionNewLaneToAttach : laneCollectionNew) {
        	laneCollectionNewLaneToAttach = em.getReference(laneCollectionNewLaneToAttach.getClass(), laneCollectionNewLaneToAttach.getLaneId());
        	attachedLaneCollectionNew.add(laneCollectionNewLaneToAttach);
      	}

## Results:
 
* So this works... I get a REST web service and it works so long as I use the latest version of Netbeans.  It makes controller, entity, and REST web service classes.
* generates identical entity classes for both client and server... should be common (just add to same project? Or use a seqware-common as we did before?)
* not using swagger https://developers.helloreverb.com/swagger/ although it does have a functional interactive web client... just not pretty
