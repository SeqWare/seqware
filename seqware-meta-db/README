PACKAGE: SeqWare MetaDB
AUTHOR: boconnor@ucla.edu
UPDATED: 4/6/2010
VERSION: 0.7.0
HOMEPAGE: http://seqware.sourceforge.net

INTRODUCTION:

The first step in setting up any of the SeqWare components is to install the
MetaDB which tracks all metadata for the various SeqWare components.

You can find the database schema in the seqware-meta-db directory in
subversion. Installing the SeqWare database requires two files:
- seqware_meta_db.sql      : The SeqWare schema.
- seqware_meta_db_data.sql : Base SeqWare data such as organisms and
                             sequencer platforms.

INSTALLING:

Make sure you've installed PostgreSQL for your distribution. Then, as the
postgres user do the following:

 psql --command "CREATE ROLE seqware"
 psql --command "CREATE DATABASE seqware_meta_db WITH OWNER = seqware;" template1
 psql seqware_meta_db < seqware_meta_db.sql
 psql seqware_meta_db < seqware_meta_db_data.sql

If you have problems please see the excellent PostgreSQL
[http://www.postgresql.org/docs documentation].  Setting up permissions in
PostgreSQL can sometimes be tricky so make sure you read the documentation
carefully.

Once created, you should be able to login to the db and see the following tables:

psql -h localhost -U seqware -W seqware_meta_db
Password for user seqware: 
psql (8.4.1)
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
 public | lane                             | table | seqware
 public | lane_type                        | table | seqware
 public | library_selection                | table | seqware
 public | library_source                   | table | seqware
 public | library_strategy                 | table | seqware
 public | organism                         | table | seqware
 public | platform                         | table | seqware
 public | processing                       | table | seqware
 public | processing_lanes                 | table | seqware
 public | processing_relationship          | table | seqware
 public | registration                     | table | seqware
 public | sample                           | table | seqware
 public | sample_attribute                 | table | seqware
 public | sample_link                      | table | seqware
 public | sequencer_run                    | table | seqware
 public | study                            | table | seqware
 public | study_attribute                  | table | seqware
 public | study_link                       | table | seqware
 public | study_type                       | table | seqware
 public | version                          | table | seqware
 public | workflow                         | table | seqware
(27 rows)

