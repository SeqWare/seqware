#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o xtrace

psql test_seqware_meta_db --command "drop schema if exists public cascade;"
psql test_seqware_meta_db --command "create schema public;"
psql test_seqware_meta_db -f seqware_meta_db.sql
psql test_seqware_meta_db -f seqware_meta_db_data.sql
