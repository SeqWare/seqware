select count(*) from (select processing_id from processing WHERE workflow_run_id IS NULL AND ancestor_workflow_run_id IS NULL 
	EXCEPT select parent_id from processing_relationship 
	EXCEPT select child_id from processing_relationship 
	EXCEPT select processing_id from processing_experiments 
	EXCEPT select processing_id from processing_files 
	EXCEPT select processing_id from processing_ius 
	EXCEPT select processing_id from processing_lanes 
	EXCEPT select processing_id from processing_samples 
	EXCEPT select processing_id from processing_sequencer_runs 
	EXCEPT select processing_id from processing_studies) AS foo;
