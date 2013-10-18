WITH attribute_key_counts AS (
SELECT f.sw_accession, tag, count(value) from expense_attribute fa 
JOIN expense f ON f.expense_id = fa.expense_id
GROUP BY f.sw_accession, tag
UNION
SELECT f.sw_accession, tag, count(value) from experiment_attribute fa 
JOIN experiment f ON f.experiment_id = fa.experiment_id
GROUP BY f.sw_accession, tag
UNION
SELECT f.sw_accession, tag, count(value) from file_attribute fa 
JOIN file f ON f.file_id = fa.file_id
GROUP BY f.sw_accession, tag
UNION
SELECT f.sw_accession, tag, count(value) from invoice_attribute fa 
JOIN invoice f ON f.invoice_id = fa.invoice_id
GROUP BY f.sw_accession, tag
UNION
SELECT f.sw_accession, fa.tag, count(value) from ius_attribute fa 
JOIN ius f ON f.ius_id = fa.ius_id
GROUP BY f.sw_accession, fa.tag
UNION
SELECT f.sw_accession, tag, count(value) from lane_attribute fa 
JOIN lane f ON f.lane_id = fa.lane_id
GROUP BY f.sw_accession, tag
UNION
SELECT f.sw_accession, tag, count(value) from processing_attribute fa 
JOIN processing f ON f.processing_id = fa.processing_id
GROUP BY f.sw_accession, tag
UNION
SELECT f.sw_accession, tag, count(value) from sequencer_run_attribute fa 
JOIN sequencer_run f ON f.sequencer_run_id = fa.sample_id
GROUP BY f.sw_accession, tag
UNION
SELECT f.sw_accession, tag, count(value) from study_attribute fa 
JOIN study f ON f.study_id = fa.study_id
GROUP BY f.sw_accession, tag
UNION
SELECT f.sw_accession, tag, count(value) from workflow_attribute fa 
JOIN workflow f ON f.workflow_id = fa.workflow_id
GROUP BY f.sw_accession, tag
UNION
SELECT f.sw_accession, tag, count(value) from workflow_run_attribute fa 
JOIN workflow_run f ON f.workflow_run_id = fa.workflow_run_id
GROUP BY f.sw_accession, tag
) 
SELECT * from attribute_key_counts WHERE count > 1 ORDER BY count DESC;
