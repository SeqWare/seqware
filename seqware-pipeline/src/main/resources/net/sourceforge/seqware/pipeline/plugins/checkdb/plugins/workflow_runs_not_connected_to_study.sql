with recursive study_samples (study_id, experiment_id, sample_id) as (
    select study.study_id
         , experiment.experiment_id
         , sample.sample_id
    from study
    join experiment on experiment.study_id = study.study_id
    join sample on sample.experiment_id = experiment.experiment_id
    --studyWhereClause
)

, study_processings (study_id, experiment_id, sample_id, sequencer_run_id, lane_id, ius_id, processing_id) as (
    select ss.study_id
         , ss.experiment_id
         , ss.sample_id
         , sr.sequencer_run_id
         , l.lane_id
         , i.ius_id
         , pi.processing_id
    from processing_ius pi
    join ius i on i.ius_id = pi.ius_id
    join lane l on l.lane_id = i.lane_id
    join sequencer_run sr on sr.sequencer_run_id = l.sequencer_run_id
    join study_samples ss on ss.sample_id = i.sample_id
union
    select sp.study_id
         , sp.experiment_id
         , sp.sample_id
         , sp.sequencer_run_id
         , sp.lane_id
         , sp.ius_id
         , pr.child_id as processing_id
    from study_processings sp
    join processing_relationship pr on pr.parent_id = sp.processing_id
)

, study_report_ids (study_id, experiment_id, sample_id, sequencer_run_id, lane_id, ius_id, processing_id, file_id) as (
    select sp.study_id
         , sp.experiment_id
         , sp.sample_id
         , sp.sequencer_run_id
         , sp.lane_id
         , sp.ius_id
         , sp.processing_id
         , pf.file_id
    from study_processings sp
    join processing_files pf on pf.processing_id = sp.processing_id
)

, study_report_wf_ids (study_id, experiment_id, sample_id, sequencer_run_id, lane_id, ius_id, workflow_id, workflow_run_id, processing_id, file_id) as (
    select so.study_id
         , so.experiment_id
         , so.sample_id
         , so.sequencer_run_id
         , so.lane_id
         , so.ius_id
         , wr.workflow_id
         , wr.workflow_run_id
         , so.processing_id
         , so.file_id
    from study_report_ids so
    join processing p on p.processing_id = so.processing_id
    left join workflow_run wr on (p.workflow_run_id is not null and wr.workflow_run_id = p.workflow_run_id)
                              or (p.workflow_run_id is null and wr.workflow_run_id = p.ancestor_workflow_run_id)
)

, sample_ancestors (sample_id, ancestor_id) as (
    select sample_id
         , parent_id as ancestor_id
    from sample_hierarchy
    where parent_id is not null
union all
    select sa.sample_id
         , sh.parent_id as ancestor_id
    from sample_ancestors sa
    join sample_hierarchy sh on sh.sample_id = sa.ancestor_id
    where parent_id is not null
)
, short_study_report as (
    select p.update_tstmp as last_modified
     , translate(st.title, ' ', '_') as study_title
     , st.sw_accession as study_swa
     , translate(case when e.name is not null and e.name <> '' then e.name else e.title end , ' ', '_') as experiment_name
     , e.sw_accession as experiment_swa
     , translate(case when s.name is not null and s.name <> '' then s.name else s.title end , ' ', '_') as sample_name
     , s.sw_accession as sample_swa
     , translate(sr.name, ' ', '_') as sequencer_run_name
     , sr.sw_accession as sequencer_run_swa
     , translate(l.name, ' ', '_') as lane_name
     , l.lane_index+1 as lane_number
     , l.sw_accession as lane_swa
     , i.tag as ius_tag
     , i.sw_accession as ius_swa
     , translate(wf.name, ' ', '_') as workflow_name
     , wf.version as workflow_version
     , wf.sw_accession as workflow_swa
     , translate(wfr.name, ' ', '_') as workflow_run_name
     , wfr.status as workflow_run_status
     , wfr.sw_accession as workflow_run_swa
     , p.algorithm as processing_algorithm
     , p.sw_accession as processing_swa
     , f.meta_type as file_meta_type
     , f.sw_accession as file_swa
     , f.file_path as file_path
  from study_report_ids ids
  join file f on f.file_id = ids.file_id
  join study st on st.study_id = ids.study_id
  join experiment e on e.experiment_id = ids.experiment_id
  join processing p on p.processing_id = ids.processing_id
  left join sample s on s.sample_id = ids.sample_id
  left join lane l on l.lane_id = ids.lane_id
  left join sequencer_run sr on sr.sequencer_run_id = ids.sequencer_run_id
  left join ius i on i.ius_id = ids.ius_id
  left join workflow_run wfr on (p.workflow_run_id is not null and wfr.workflow_run_id = p.workflow_run_id)
                           or (p.workflow_run_id is null and wfr.workflow_run_id = p.ancestor_workflow_run_id)
  left join workflow wf on wf.workflow_id = wfr.workflow_id
)

SELECT workflow_run_swa, count(distinct study_swa) FROM short_study_report GROUP BY workflow_run_swa 
UNION
SELECT sw_accession,0 FROM workflow_run WHERE status = 'completed' EXCEPT select workflow_run_swa,0  FROM short_study_report

ORDER BY count