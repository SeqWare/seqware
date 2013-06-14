
-- tuples of leaf samples and the child/parent walk up to the root sample(s)
-- first tuple is bootstrapped as (leaf, leaf, leaf's parent)
with recursive sample_ancestors(leaf_id, child_id, parent_id) as (
    select c.sample_id, h.sample_id, h.parent_id
    from (select distinct sample_id from sample_hierarchy
          except
          select distinct parent_id from sample_hierarchy) c
    join sample_hierarchy h on c.sample_id = h.sample_id
union all
    select a.leaf_id, h.sample_id, h.parent_id
    from sample_ancestors a
    join sample_hierarchy h on a.parent_id = h.sample_id
)

-- tuples of sample and one of its ancestors
-- root samples do not appear in the results
, sample_ancestor_ids (sample_id, ancestor_id) as (
    select leaf_id as sample_id, child_id as ancestor_id
    from sample_ancestors
    where leaf_id <> child_id and parent_id is not null
)


-- paths from study to sample
, sample_id_path as (
    select e.study_id, s.experiment_id, s.sample_id
    from experiment e
    join sample s on s.experiment_id = e.experiment_id
)

-- paths from sample directly to file
, sample_file_id_path as (
    select ps.sample_id, ps.processing_id, pf.file_id
    from processing_files pf
    join processing_samples ps on pf.processing_id = ps.processing_id
)

-- paths from sample to file via lane
, lane_file_id_path as (
    select l.sample_id, l.lane_id, pl.processing_id, pf.file_id
    from processing_files pf
    join processing_lanes pl on pf.processing_id = pl.processing_id
    join lane l on pl.lane_id = l.lane_id
)

-- paths from sample to file via ius
, ius_file_id_path as (
    select i.sample_id, i.ius_id, pi.processing_id, pf.file_id
    from processing_files pf
    join processing_ius pi on pf.processing_id = pi.processing_id
    join ius i on pi.ius_id = i.ius_id
)

-- paths from study to file
, file_id_path as (
    select sp.study_id, sp.experiment_id, fp.*
    from  (
        select sample_id, lane_id, null as ius_id, processing_id, file_id from lane_file_id_path
        union all
        select sample_id, null as lane_id, ius_id, processing_id, file_id from ius_file_id_path
        union all
        select sample_id, null as lane_id, null as ius_id, processing_id, file_id from sample_file_id_path) fp
    join sample_id_path sp on sp.sample_id =  fp.sample_id
)

-- concatenated sample/tag values
, study_attrs as (
    select study_id, tag, array_to_string(array_agg(value), ';') as vals
    from study_attribute sta
    where study_id is not null and tag is not null
    group by study_id, tag
)

-- concatenated sample attributes
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
    from sample_ancestor_ids anc
    join sample_attrs attr on attr.sample_id = anc.ancestor_id
    join sample s on s.sample_id = anc.ancestor_id
    group by anc.sample_id
)


select p.update_tstmp as processing_date
     , translate(st.title, ' ', '_') as study_title
     , st.sw_accession as study_swa
     , case when sta.attrs is not null then sta.attrs else '' end as study_attrs
     , translate(case when e.name is not null and e.name <> '' then e.name else e.title end , ' ', '_') as experiment_name
     , e.sw_accession as experiment_swa
     , case when ea.attrs is not null then ea.attrs else '' end as experiment_attrs
     , translate(sp.parent_names, ' ', '_') as sample_parent_names
     , sp.parent_swas as sample_parent_swas
     , sp.parent_attrs as sample_parent_attrs
     , translate(case when s.name is not null and s.name <> '' then s.name else s.title end , ' ', '_') as sample_name
     , s.sw_accession as sample_swa
     , case when sa.attrs is not null then sa.attrs else '' end as sample_attrs
     , translate(sr.name, ' ', '_') as sequencer_run_name
     , sr.sw_accession as sequencer_run_swa
     , case when sra.attrs is not null then sra.attrs else '' end as sequencer_run_attrs
     , translate(l.name, ' ', '_') as lane_name
     , l.lane_index as lane_index
     , l.sw_accession as lane_swa
     , case when la.attrs is not null then la.attrs else '' end as lane_attrs
     , i.tag as ius_tag
     , i.sw_accession as ius_swa
     , case when ia.attrs is not null then ia.attrs else '' end as ius_attrs
     , translate(wf.name, ' ', '_') as workflow_name
     , wf.version as workflow_version
     , wf.sw_accession as workflow_swa
     , translate(wfr.name, ' ', '_') as workflow_run_name
--     , wfr.status as workflow_run_status
     , wfr.sw_accession as workflow_run_swa
     , p.algorithm as processing_algorithm
     , p.sw_accession as processing_swa
     , case when pa.attrs is not null then pa.attrs else '' end as processing_attrs
     , f.meta_type as file_meta_type
     , f.sw_accession as file_swa
     , f.file_path as file_path
from file_id_path fp
join file f on f.file_id = fp.file_id
join study st on st.study_id = fp.study_id
left join study_attrs_str sta on sta.study_id = fp.study_id
join experiment e on e.experiment_id = fp.experiment_id
left join experiment_attrs_str ea on ea.experiment_id = fp.experiment_id
join processing p on p.processing_id = fp.processing_id
left join processing_attrs_str pa on pa.processing_id = fp.processing_id
left join sample_parent_strs sp on sp.sample_id = fp.sample_id
left join sample s on s.sample_id = fp.sample_id
left join sample_attrs_str sa on sa.sample_id = fp.sample_id
left join lane l on l.lane_id = fp.lane_id
left join lane_attrs_str la on la.lane_id = fp.lane_id
left join sequencer_run sr on sr.sequencer_run_id = l.sequencer_run_id
left join sequencer_run_attrs_str sra on sra.sequencer_run_id = sr.sequencer_run_id
left join ius i on i.ius_id = fp.ius_id
left join ius_attrs_str ia on ia.ius_id = fp.ius_id
left join workflow_run wfr on wfr.workflow_run_id = p.workflow_run_id
left join workflow wf on wf.workflow_id = wfr.workflow_id