WITH recursive study_processings (ius_id, processing_id) as (
    select i.ius_id
         , pi.processing_id
    from processing_ius pi
    join ius i on i.ius_id = pi.ius_id
union
    select sp.ius_id
         , pr.child_id as processing_id
    from study_processings sp
    join processing_relationship pr on pr.parent_id = sp.processing_id
)
, stub_report as (
    select i.tag as ius_tag
     , i.sw_accession as ius_swa
     , translate(wf.name, ' ', '_') as workflow_name
     , wf.version as workflow_version
     , wf.sw_accession as workflow_swa
     , translate(wfr.name, ' ', '_') as workflow_run_name
     , wfr.status as workflow_run_status
     , wfr.sw_accession as workflow_run_swa
     , p.algorithm as processing_algorithm
     , p.sw_accession as processing_swa
  from study_processings ids
  join processing p on p.processing_id = ids.processing_id
  left join ius i on i.ius_id = ids.ius_id
  left join workflow_run wfr on (p.workflow_run_id is not null and wfr.workflow_run_id = p.workflow_run_id)
                           or (p.workflow_run_id is null and wfr.workflow_run_id = p.ancestor_workflow_run_id)
  left join workflow wf on wf.workflow_id = wfr.workflow_id
)

SELECT sw_accession from workflow_run WHERE sw_accession 
IN (SELECT sw_accession from workflow_run wr, ius_workflow_runs iwr
WHERE wr.workflow_run_id = iwr.workflow_run_id 
EXCEPT
SELECT distinct workflow_run_swa from stub_report)  
AND status != 'failed'
;

