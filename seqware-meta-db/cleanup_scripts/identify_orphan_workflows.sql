SELECT sw_accession FROM workflow WHERE workflow_id NOT IN (select workflow_id from workflow_run);
