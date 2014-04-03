This is a place for one-shot clean-up scripts that remove invalid or useless records from the database.

Identify affected records with a command like:

    psql --single-transaction -v ON_ERROR_STOP=1 -d test_seqware_meta_db -f identify_orphan_processing.sql

Delete using a command like:

    psql --single-transaction -v ON_ERROR_STOP=1 -d test_seqware_meta_db -f clean_orphan_processing.sql
