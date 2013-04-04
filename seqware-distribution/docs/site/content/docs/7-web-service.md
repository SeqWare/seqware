---

title:                 "SeqWare Web Service"
toc_includes_sections: true
markdown:              advanced

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

* '''Use SeqWare Pipeline with the Web service enabled:''' The only configuration necessary is to change your .seqware/settings file to point to the Web service. The seqware-distribution jar will use the Web service instead of a direct database connection with no further changes.
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

### Install Guide ###

Please see the [Install Guide](/docs/github_readme/4-webservice/)

<!-- 

## Coming Soon ##

*This guide is a work in progress.* In the future this will include more information on the following topics.

### Admin Setup

See the [Admin Guide](/docs/3-getting-started/admin-tutorial/)

### Features

### Reporting

### Workflow Launching, Monitoring

### Data Retrieval


-->

