WITH new_input_files AS 
(select wr.sw_accession AS workflow_run_swa, f.* from workflow_run wr, workflow_run_input_files wrif, file f
WHERE wr.workflow_run_id = wrif.workflow_run_id
AND wrif.file_id = f.file_id 
ORDER BY wr.sw_accession)
,
old_input_files AS 
(select wr.sw_accession AS workflow_run_swa, f.* from workflow_run wr, processing p1, 
processing_relationship pr, processing p2, processing_files pf, file f
WHERE wr.workflow_run_id = p1.workflow_run_id 
AND p1.processing_id = pr.child_id 
AND pr.parent_id = p2.processing_id 
AND p2.processing_id = pf.processing_id 
AND pf.file_id = f.file_id  
ORDER BY wr.sw_accession)

SELECT * from old_input_files except select * from new_input_files;