with recursive study_samples (study_id, experiment_id, sample_id) as (
    select st.study_id
         , e.experiment_id
         , s.sample_id
    from study st
    join experiment e on e.study_id = st.study_id
    join sample s on s.experiment_id = e.experiment_id
    --studyWhereClause
)

, study_processings (study_id, experiment_id, sample_id, sequencer_run_id, lane_id, ius_id, processing_id) as (
    select ss.study_id
         , ss.experiment_id
         , ss.sample_id
         , null as sequencer_run_id
         , null as lane_id
         , null as ius_id
         , ps.processing_id
    from processing_samples ps
    join study_samples ss on ss.sample_id = ps.sample_id
union
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

, study_attrs as (
    select study_id, tag, array_to_string(array_agg(value), ';') as vals
    from study_attribute sta
    where study_id is not null and tag is not null
    group by study_id, tag
)


, study_attrs_str as (
    select study_id
         , array_to_string(array_agg('study.'||tag||'='||vals), ';') as attrs
    from study_attrs attr
    group by study_id
)

, experiment_attrs as (
    select experiment_id, tag, array_to_string(array_agg(value), ';') as vals
    from experiment_attribute sta
    where experiment_id is not null and tag is not null
    group by experiment_id, tag
)

, experiment_attrs_str as (
    select experiment_id
         , array_to_string(array_agg('experiment.'||tag||'='||vals), ';') as attrs
    from experiment_attrs attr
    group by experiment_id
)

, sample_attrs as (
    select sample_id, tag, array_to_string(array_agg(value), ';') as vals
    from sample_attribute sta
    where sample_id is not null and tag is not null
    group by sample_id, tag
)

, sample_attrs_str as (
    select sample_id
         , array_to_string(array_agg('sample.'||tag||'='||vals), ';') as attrs
    from sample_attrs attr
    group by sample_id
)

, sequencer_run_attrs as (
    -- bug: table has sample_id instead of sequencer_run_id
    select sample_id as sequencer_run_id, tag, array_to_string(array_agg(value), ';') as vals
    from sequencer_run_attribute sta
    where sample_id is not null and tag is not null
    group by sample_id, tag
)

, sequencer_run_attrs_str as (
    select sequencer_run_id
         , array_to_string(array_agg('sequencerrun.'||tag||'='||vals), ';') as attrs
    from sequencer_run_attrs attr
    group by sequencer_run_id
)

, lane_attrs as (
    select lane_id, tag, array_to_string(array_agg(value), ';') as vals
    from lane_attribute sta
    where lane_id is not null and tag is not null
    group by lane_id, tag
)

, lane_attrs_str as (
    select lane_id
         , array_to_string(array_agg('lane.'||tag||'='||vals), ';') as attrs
    from lane_attrs attr
    group by lane_id
)

, ius_attrs as (
    select ius_id, tag, array_to_string(array_agg(value), ';') as vals
    from ius_attribute sta
    where ius_id is not null and tag is not null
    group by ius_id, tag
)

, ius_attrs_str as (
    select ius_id
         , array_to_string(array_agg('ius.'||tag||'='||vals), ';') as attrs
    from ius_attrs attr
    group by ius_id
)

, processing_attrs as (
    select processing_id, tag, array_to_string(array_agg(value), ';') as vals
    from processing_attribute sta
    where processing_id is not null and tag is not null
    group by processing_id, tag
)

, processing_attrs_str as (
    select processing_id
         , array_to_string(array_agg('processing.'||tag||'='||vals), ';') as attrs
    from processing_attrs attr
    group by processing_id
)

-- concatenated values from sample parents
, sample_parent_strs as (
    select anc.sample_id
         , array_to_string(array_agg(name), ':') as parent_names
         , array_to_string(array_agg(sw_accession), ':') as parent_swas
         , array_to_string(array_agg('parent_sample.'||tag||'.'||sw_accession||'='||vals), ';') as parent_attrs
    from sample_ancestors anc
    join sample_attrs attr on attr.sample_id = anc.ancestor_id
    join sample s on s.sample_id = anc.ancestor_id
    group by anc.sample_id
)

select p.update_tstmp as last_modified
     , translate(st.title, ' ', '_') as study_title
     , st.sw_accession as study_swa
     , sta.attrs as study_attrs
     , translate(case when e.name is not null and e.name <> '' then e.name else e.title end , ' ', '_') as experiment_name
     , e.sw_accession as experiment_swa
     , ea.attrs as experiment_attrs
     , translate(sp.parent_names, ' ', '_') as sample_parent_names
     , sp.parent_swas as sample_parent_swas
     , sp.parent_attrs as sample_parent_attrs
     , translate(case when s.name is not null and s.name <> '' then s.name else s.title end , ' ', '_') as sample_name
     , s.sw_accession as sample_swa
     , sa.attrs as sample_attrs
     , translate(sr.name, ' ', '_') as sequencer_run_name
     , sr.sw_accession as sequencer_run_swa
     , sra.attrs as sequencer_run_attrs
     , translate(l.name, ' ', '_') as lane_name
     , l.lane_index+1 as lane_number
     , l.sw_accession as lane_swa
     , la.attrs as lane_attrs
     , i.tag as ius_tag
     , i.sw_accession as ius_swa
     , ia.attrs as ius_attrs
     , translate(wf.name, ' ', '_') as workflow_name
     , wf.version as workflow_version
     , wf.sw_accession as workflow_swa
     , translate(wfr.name, ' ', '_') as workflow_run_name
     , wfr.status as workflow_run_status
     , wfr.sw_accession as workflow_run_swa
     , p.algorithm as processing_algorithm
     , p.sw_accession as processing_swa
     , pa.attrs as processing_attrs
     , f.meta_type as file_meta_type
     , f.sw_accession as file_swa
     , f.file_path as file_path
from study_report_ids ids
join file f on f.file_id = ids.file_id
join study st on st.study_id = ids.study_id
left join study_attrs_str sta on sta.study_id = ids.study_id
join experiment e on e.experiment_id = ids.experiment_id
left join experiment_attrs_str ea on ea.experiment_id = ids.experiment_id
join processing p on p.processing_id = ids.processing_id
left join processing_attrs_str pa on pa.processing_id = ids.processing_id
left join sample_parent_strs sp on sp.sample_id = ids.sample_id
left join sample s on s.sample_id = ids.sample_id
left join sample_attrs_str sa on sa.sample_id = ids.sample_id
left join lane l on l.lane_id = ids.lane_id
left join lane_attrs_str la on la.lane_id = ids.lane_id
left join sequencer_run sr on sr.sequencer_run_id = ids.sequencer_run_id
left join sequencer_run_attrs_str sra on sra.sequencer_run_id = ids.sequencer_run_id
left join ius i on i.ius_id = ids.ius_id
left join ius_attrs_str ia on ia.ius_id = ids.ius_id
left join workflow_run wfr on (p.workflow_run_id is not null and wfr.workflow_run_id = p.workflow_run_id)
                           or (p.workflow_run_id is null and wfr.workflow_run_id = p.ancestor_workflow_run_id)
left join workflow wf on wf.workflow_id = wfr.workflow_id