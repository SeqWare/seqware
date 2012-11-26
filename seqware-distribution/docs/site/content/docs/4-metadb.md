---

title:                 "SeqWare MetaDB"
markdown:              advanced
toc_includes_sections: true
is_dynamic:            true

---


## Overview

A database built with PostgreSQL, the SeqWare MetaDB includes tables to model the run of the sequencer, analysis "processing" events, and the ability to trigger standard analysis through triggering the SeqWare Pipeline. Features include: 

* Short Read Archive-compliant schema
* A "processing" table to model analytical processes and results for samples
* A common database for tracking metadata, used by the other SeqWare components 

## Setup

### Version ###

These directions have been validated on the SeqWare VM for 0.12.5.1 (see [Installation](/docs/2-installation) and the [developer guide](/docs/3-getting-started/developer-tutorial/) for more information). Portions of this tutorial have been validated on a pre-production VM for the 0.13.x series. Note that the VM is already setup with SeqWare MetaDB, so these instructions should only be of use if you intend on installing SeqWare yourself or if you require the upgrading instructions to upgrade an existing installation of SeqWare.

### Requirements ###

You will need PostgreSQL (version >= 8.4.5) to use this database.  Version 8.4.x can be installed following directions from http://openscg.org or http://fedoraproject.org/wiki/EPEL.   A version for MySQL or Oracle is not available but if anyone wants to submit that we would happily add it to the repository.

These directions assume you can "sudo" as your user on the Linux computer you are installing the SeqWare MetaDB on. Talk to your admin if you cannot.

### Installation ###

You can find the database schema in the seqware-meta-db directory in subversion.

* Make sure PostgreSQL is configured to accept TCP/IP connections, look in file /var/lib/pgsql/data/postgresql.conf (/etc/postgresql/8.4/main/postgresql.conf on Ubuntu) for the following and uncomment listen_address and port:

<pre>
# - Connection Settings -

listen_addresses = '*'          # what IP address(es) to listen on; 
					# comma-separated list of addresses;
					# defaults to 'localhost', '*' = all
port = 5432
</pre>

* Make sure /var/lib/pgsql/data/pg_hba.conf (/etc/postgresql/8.4/main/pg_hba.conf on Ubuntu) has an appropriate authentication line for your user. Keep in mind you need to put the more specific rules first, for example your md5 auth for seqware should be the first line and then you can have another line for all other databases/user after it. Take a look at the [http://www.postgresql.org/docs/ PostgreSQL docs] for more info about this file, it's a little tricky.  Make sure to restart postgres after changing this file. You may want something like:

<pre>
# Database administrative login by Unix domain socket
local   all             postgres                                ident

#HOST   DATABASE          USER                              AUTH
local   all     seqware         md5

# IPv4 local connections:
host    all     seqware         127.0.0.1/32    md5
</pre>

One tip, by default PostgreSQL uses "ident" to ensure that the user logging in via psql is the same as the user logged into the shell. I typically turn this off for the seqware user so I can log in as seqware under any users account. Note that the seqware user needs to be able to log on both to 'seqware_meta_db' and 'test_seqware_meta_db' (if you would like to run the tests).


* Create a database user for the SeqWare MetaDB with the following command.  ''seqware'' is the username and ''password'' is the password for this user. Please change as appropriate for your site:

<pre>
$ sudo -u postgres psql -c "CREATE USER seqware WITH PASSWORD 'password' CREATEDB;"
</pre>

* Create a database using the following command where seqware_meta_db is the name of your new database and seqware is the name of your database user:

<pre>
$ sudo -u postgres psql --command "CREATE DATABASE seqware_meta_db WITH OWNER = seqware;"
</pre>

### Setup Postgres Language ###

Once you create the database you may have to load a stored procedure programming language in order for stored procedures in the database to work properly. From the commandline, run:

	$ sudo -u postgres createlang plpgsql seqware_meta_db

In order to enable this for all of the databases created on your instance of PostgreSQL in the future, also apply this to the template1 database. This step is required for the tests to work properly.

	$ sudo -u postgres createlang plpgsql template1

### Populate the Database ###

There are two ways to build the database: for production use and for testing use. Production databases should be created for real data runs, whereas testing databases should be created for developer use during testing. 

There are three files in Subversion that are used for building and populating the database.

* seqware_meta_db.sql -- this is the schema for the database without any data
* seqware_meta_db_data.sql -- this contains the basic data for the database, including organisms, admin registrations, etc.
* seqware_meta_db_testdata.sql -- this contains the testing data for the database, including sequencer_runs, lanes, studies, and processing events.

Production databases should not have any testing data in them so that the test data does not interfere with real data. Accordingly, production databases are built using seqware_meta_db.sql and then applying seqware_meta_db_data.sql.

Testing databases are built using all three SQL files in sequence. The test data SQL file contains only testing data - none of the basic data from the second SQL file should be contained within it at all. There should not be any overlap between the two data files (data and testdata) in order to prevent errors and duplicated rows.

These files are structured this way for several reasons. First, if the schema changes in the future, the changes can be more easily put into the database by merely updating the schema, and then generating the data and testdata files. Second, it has previously been established that sequences in PostgreSQL (such as that used by the sw_accession, a key that is used throughout the database) can introduce errors in the form of duplicated keys when the schema and data are simultaneously loaded.

### Method for production databases ###
Populate a production database by loading the provided schema/data. You will be prompted to enter the password for your database user:

	$ psql -U seqware -W seqware_meta_db < seqware_meta_db.sql 
	$ psql -U seqware -W seqware_meta_db < seqware_meta_db_data.sql

If you have problems please see the excellent PostgreSQL [documentation](http://www.postgresql.org/docs).  Setting up permissions in PostgreSQL can sometimes be tricky so make sure you read the documentation carefully. 

Once created, you should be able to login to the db and see the following tables:

	psql -h localhost -U seqware -W seqware_meta_db
	Password for user seqware: 
	psql (8.4.8)
	Type "help" for help.

	seqware_meta_db=> \dt
			      List of relations
	 Schema |               Name               | Type  |  Owner  
	--------+----------------------------------+-------+---------
	 public | experiment                       | table | seqware
	 public | experiment_attribute             | table | seqware
	 public | experiment_library_design        | table | seqware
	 public | experiment_link                  | table | seqware
	 public | experiment_spot_design           | table | seqware
	 public | experiment_spot_design_read_spec | table | seqware
	 public | file                             | table | seqware
	 public | file_report                      | table | seqware
	 public | file_type                        | table | seqware
	 public | ius                              | table | seqware
	 public | ius_attribute                    | table | seqware
	 public | ius_link                         | table | seqware
	 public | ius_workflow_runs                | table | seqware
	 public | lane                             | table | seqware
	 public | lane_attribute                   | table | seqware
	 public | lane_link                        | table | seqware
	 public | lane_type                        | table | seqware
	 public | lane_workflow_runs               | table | seqware
	 public | library_selection                | table | seqware
	 public | library_source                   | table | seqware
	 public | library_strategy                 | table | seqware
	 public | organism                         | table | seqware
	 public | platform                         | table | seqware
	 public | processing                       | table | seqware
	 public | processing_attribute             | table | seqware
	 public | processing_experiments           | table | seqware
	 public | processing_files                 | table | seqware
	 public | processing_ius                   | table | seqware
	 public | processing_lanes                 | table | seqware
	 public | processing_relationship          | table | seqware
	 public | processing_samples               | table | seqware
	 public | processing_sequencer_runs        | table | seqware
	 public | processing_studies               | table | seqware
	 public | registration                     | table | seqware
	 public | sample                           | table | seqware
	 public | sample_attribute                 | table | seqware
	 public | sample_hierarchy                 | table | seqware
	 public | sample_link                      | table | seqware
	 public | sample_relationship              | table | seqware
	 public | sample_report                    | table | seqware
	 public | sequencer_run                    | table | seqware
	 public | sequencer_run_attribute          | table | seqware
	 public | share_experiment                 | table | seqware
	 public | share_file                       | table | seqware
	 public | share_lane                       | table | seqware
	 public | share_processing                 | table | seqware
	 public | share_sample                     | table | seqware
	 public | share_study                      | table | seqware
	 public | share_workflow_run               | table | seqware
	 public | study                            | table | seqware
	 public | study_attribute                  | table | seqware
	 public | study_link                       | table | seqware
	 public | study_type                       | table | seqware
	 public | version                          | table | seqware
	 public | workflow                         | table | seqware
	 public | workflow_param                   | table | seqware
	 public | workflow_param_value             | table | seqware
	 public | workflow_run                     | table | seqware
	 public | workflow_run_param               | table | seqware
	(59 rows)


### Method for testing databases ###
Follow the directions as for production databases, but additionally load the testdata SQL file:

	$ psql -U seqware -W seqware_meta_db < seqware_meta_db_testdata.sql

For for information on this file and how to create it, please see [Updating the SeqWare MetadataDB](https://sourceforge.net/apps/mediawiki/seqware/index.php?title=Updating_the_SeqWare_MetadataDB)

### Triggers ###

There are a few stored procedures in the DB that are used for reporting.  If you add these to an existing DB you'll need to run them once:

	select fill_file_report();
	select fill_sample_report();

### Trigger Performance Issues ###

Originally, these stored procedures were run as triggers whenever an update occurred in the database. We have discovered issues with the triggers when the DB is large and you are writing a lot of records to it consistently.  For an install like this it would be much better to periodically populate the report tables using the above stored procedures.  You'll want to disable the triggers and then setup the cron:

	# this disables them
	alter table workflow_run disable trigger user;
	alter table processing_files disable trigger user;

	# here are the triggers:

	CREATE FUNCTION "FileReportDelete"() RETURNS trigger
	CREATE FUNCTION "FileReportInsert"() RETURNS trigger
	CREATE FUNCTION "SampleReportDelete"() RETURNS trigger
	CREATE FUNCTION "SampleReportUpdate"() RETURNS trigger

	# the two report tables
	seqware_meta_db=# \d sample_report
					       Table "public.sample_report"
	     Column      |          Type          |                           Modifiers                            
	-----------------+------------------------+----------------------------------------------------------------
	 study_id        | integer                | 
	 child_sample_id | integer                | 
	 workflow_id     | integer                | 
	 status          | character varying(255) | 
	 row_id          | integer                | not null default nextval('sample_report_row_id_seq'::regclass)
	Indexes:
	    "sample_report_pkey" PRIMARY KEY, btree (row_id)
	    "sample_report_study_id_child_sample_id_workflow_id_idx" btree (study_id, child_sample_id, workflow_id)

	seqware_meta_db=# \d file_report
					Table "public.file_report"
	     Column      |  Type   |                          Modifiers                           
	-----------------+---------+--------------------------------------------------------------
	 row_id          | integer | not null default nextval('file_report_row_id_seq'::regclass)
	 study_id        | integer | 
	 ius_id          | integer | 
	 lane_id         | integer | 
	 file_id         | integer | 
	 sample_id       | integer | 
	 experiment_id   | integer | 
	 child_sample_id | integer | 
	 processing_id   | integer | 
	Indexes:
	    "file_report_pkey" PRIMARY KEY, btree (row_id)
	    "file_report_study_id_idx" btree (study_id)


I developed a nightly script that will 1) ensure the filters are offline, 2) delete the contents of the report tables, 3) repopulate with stored procedures:

	#!/bin/bash

	# a very simple shell script that will disable these triggers, purge the
	# tables, and repop using stored procedures

	psql seqware_meta_db -c "alter table workflow_run disable trigger user; alter table processing_files disable trigger user; delete from sample_report; delete from file_report; select fill_file_report(); select fill_sample_report();"



## Upgrading your Database Version ##
If you have previously installed SeqWare MetaDb and want to upgrade to the latest version, you can easily do so by running the upgrade scripts available from the repository.

In the [SeqWare MetaDb repository in trunk](https://github.com/SeqWare/seqware/tree/master/seqware-meta-db), there are a number of upgrade scripts that allow you to update between versions. Here is a listing of the seqware_meta_db directory (Revision 4091) for an example.

	seqware-meta-db:~$ ls
	0.10.0_to_0.10.1.sql  0.7.5_to_0.8.0.sql  0.9.1_to_0.10.0.sql             dcc-export-tool  seqware_meta_db_data.sql      sra-export-tool
	0.10.1_to_0.10.2.sql  0.8.0_to_0.9.0.sql  2012_01_25_seqware_meta_db.sql  pom.xml          seqware_meta_db.sql           TODO
	0.10.2_to_0.11.2.sql  0.9.0_to_0.9.1.sql  CHANGES                         README           seqware_meta_db_testdata.sql

The numeric sql scripts, including 0.7.5_to_0.8.0.sql, 0.8.0_to_0.9.0.sql, 0.9.0_to_0.9.1.sql and 0.9.1_to_0.10.0.sql, contain the statements required to update the database schema between revisions. If you are not certain what version of the database you have, check the table below for the changes that were applied to each version:

| Version | Change from previous version | Check |
|:-----------|:------------|:------------|
| 0.8.0       |  Added <tt>sample_hierarchy</tt>, <tt>processing_experiments</tt> tables. Added foreign keys and key constraints to a number of tables. Inserted rows into <tt>library_strategy</tt>, <tt>library_source</tt> and <tt>library_selection</tt>. |  Look for presence of <tt>sample_hierarchy</tt> and <tt>processing_experiments</tt> tables. |
| 0.9.0     | Added new organisms (NCBI and OICR-specific), added new platforms, set the 'code' column in <tt>organism</tt> to be not null and the 'ncbi_taxid' column to be unique. | Check for the presence of an <tt>organism</tt> with the 'name' of "OICR Vaccinia JX-594" |
| 0.9.1       | Added primary keys and foreign keys. | Check to see if <tt>workflow_param_value</tt> has a primary key and a foreign key to <tt>workflow</tt>, i.e. running <tt>\d workflow_param_value</tt> in psql should show one index (pk_workflow_param_value) and one foreign key constraint (fk_workflow_param_value_workflow_param_id). |
| 0.10.0         |  Inserted the BAM file type into the <tt>file_type</tt> table. | Check if <tt>file_type</tt> has a row with the 'display_name' of "bam file". |
| 0.10.1       |  Added the permanent_bundle_location to workflow table; added the default FileImport workflow (version 0.10.0) | Check if the FileImport workflow exists in the workflow table |
| 0.10.2    | Fixed a constraint on the sequencer_run_attribute table to point to sequencer_run instead of sample | Check that the foreign key sample_id references sequencer_run (sequencer_run_id) |
| 0.11.2    | Added the file_report and sample_report tables and created triggers to populate them when data is added to the database | Check for the presence of the file_report and sample_report tables. |

After you have determined the version of your database, you must apply each of the successive patches ''in order''. So, in order to upgrade a database that is version 0.7.5 or earlier, you must apply these files in this order.

The files can be applied simply by using the script as an input to the psql call. For example:

	$ psql -U seqware -W -f 0.7.5_to_0.8.0.sql seqware_meta_db

We have done our best to avoid potential errors during upgrades. If you run into any problems, please contact the developers.

## Tables

The SeqWare MetaDB was built on top of the [Short Read Archive (SRA)](http://www.ncbi.nlm.nih.gov/Traces/sra) data model.  It is not a 100% translation of the data model, there may be places where the SRA model provides greater flexibility and places where we simplified the model for our own purposes.  But we generally tried to keep with the SRA model as much as possible.  You can find out more information from the following documents:

* [SRA Concepts](http://www.ncbi.nlm.nih.gov/Traces/sra/sra.cgi?cmd=show&f=concepts&m=doc&s=concepts)
* [Documentation](http://www.ncbi.nlm.nih.gov/Traces/sra/sra.cgi?cmd=show&f=rfc&m=doc&s=rfc SRA Schema)

![Simple DB](/assets/images/metadb/Db_simple.png)

Or, more complicatedly:

![Simple DB](/assets/images/metadb/Db_complicated.png)


## Sequence Data ##

SeqWare uses the meta-DB to track processing events and to connect samples to processed data and/or to connect one set of processed data to another. For example linking between a biological sample, a sequencing run, the generated raw sequence data, and downstream processed sequence data is possible. This is actually a complicated process that depends on the specific path through a pipeline, with details like multiple samples being pooled for sequencing, or lanes contain data from multiple samples (multiplexed or so-called bar-coded samples) arising. First, a simple example.


![Simple DB](/assets/images/metadb/Study_hierarchy.png)

The 'proper' way to traverse the database from a study or sequencer_run is to go through ius, processing_ius, and processing. So from study, you traverse through experiment, sample to sample through the sample_hierarchy table, and ius, to processing_ius, and then to all of the processings. The processing tree starts from the processing linked through processing_ius, and then through the processing_relationship table. Each new processing event has the parent of the previous processing event; for example, if WorkflowA is run on IUSA, and then WorkflowB is also run on IUSA using the results from WorkflowA, the processing tree looks like IUSA -> processing_ius -> Processing of step 1,2,3 of WorkflowA -> Processing of step 1,2,3 of WorkflowB.  Processings are linked to workflow_runs, which have connections to IUSes through ius_workflow_runs and/or Lanes through lane_workflow_runs, as a convenience only. These links are not necessarily present (though we try to make sure they are). The safest way to find all of the processing events linked with a Study is to traverse through the IUS to the root Processing through processing_ius, not through ius_workflow_runs.

