#!/bin/bash

# a very simple shell script that will disable these triggers, purge the
# tables, and repop using stored procedures

source ~/.bash_profile

psql seqware_meta_db -c "alter table workflow_run disable trigger user;"
psql seqware_meta_db -c "alter table processing_files disable trigger user;"
psql seqware_meta_db -c "delete from sample_report;"
psql seqware_meta_db -c "delete from file_report;"
psql seqware_meta_db -c "select fill_file_report();"
psql seqware_meta_db -c "select fill_sample_report();"

