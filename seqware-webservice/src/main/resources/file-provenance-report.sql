drop table if exists file_provenance_report_temp;
create temporary table file_provenance_report_temp as (
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
-- this gathers together all paths that run fully from study to file
, study_report_ids_premerge (study_id, experiment_id, sample_id, sequencer_run_id, lane_id, ius_id, processing_id, file_id) as (
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
-- a bit of a misnomer, this collects all paths that go through the processing_lanes table from sequencer run to file 
-- along with normal paths that stretch from study through to file
, study_report_ids (study_id, experiment_id, sample_id, sequencer_run_id, lane_id, ius_id, processing_id, file_id) as (
    select null as study_id
         , null as experiment_id
         , null as sample_id
         , sr.sequencer_run_id
         , l.lane_id
         , null as ius_id
         , pl.processing_id
         , pf.file_id
    from processing_lanes pl
    join lane l on l.lane_id = pl.lane_id
    join sequencer_run sr on sr.sequencer_run_id = l.sequencer_run_id
    join processing_files pf on pf.processing_id = pl.processing_id
    WHERE NOT EXISTS (select null from study_report_ids_premerge f where pf.file_id = f.file_id)
union all 
    select * from study_report_ids_premerge
)
, sample_ancestors (sample_id, ancestor_id) as (
    select sample_id
         , parent_id as ancestor_id
         , 1 as rank
    from sample_hierarchy
    where parent_id is not null
union all
    select sa.sample_id
         , sh.parent_id as ancestor_id
         , sa.rank + 1 as rank
    from sample_ancestors sa
    join sample_hierarchy sh on sh.sample_id = sa.ancestor_id
    where parent_id is not null
)

, study_attrs as (
    select study_id, tag, array_to_string(array_agg(value order by value), '&') as vals
    from study_attribute
    where study_id is not null and tag is not null
    group by study_id, tag
)


, study_attrs_str as (
    select study_id
         , array_to_string(array_agg('study.'||tag||'='||vals order by tag,vals), ';') as attrs
    from study_attrs attr
    group by study_id
)

, experiment_attrs as (
    select experiment_id, tag, array_to_string(array_agg(value order by value), '&') as vals
    from experiment_attribute
    where experiment_id is not null and tag is not null
    group by experiment_id, tag
)

, experiment_attrs_str as (
    select experiment_id
         , array_to_string(array_agg('experiment.'||tag||'='||vals order by tag,vals), ';') as attrs
    from experiment_attrs attr
    group by experiment_id
)

, sample_attrs as (
    select sample_id, tag, array_to_string(array_agg(value order by value), '&') as vals
    from sample_attribute
    where sample_id is not null and tag is not null
    group by sample_id, tag
)

, sample_attrs_str as (
    select sample_id
         , array_to_string(array_agg('sample.'||tag||'='||vals order by tag,vals), ';') as attrs
    from sample_attrs attr
    group by sample_id
)

, sequencer_run_attrs as (
    -- bug: table has sample_id instead of sequencer_run_id
    select sample_id as sequencer_run_id, tag, array_to_string(array_agg(value order by value), '&') as vals
    from sequencer_run_attribute
    where sample_id is not null and tag is not null
    group by sample_id, tag
)

, sequencer_run_attrs_str as (
    select sequencer_run_id
         , array_to_string(array_agg('sequencerrun.'||tag||'='||vals order by tag,vals), ';') as attrs
    from sequencer_run_attrs attr
    group by sequencer_run_id
)

, lane_attrs as (
    select lane_id, tag, array_to_string(array_agg(value order by value), '&') as vals
    from lane_attribute
    where lane_id is not null and tag is not null
    group by lane_id, tag
)

, lane_attrs_str as (
    select lane_id
         , array_to_string(array_agg('lane.'||tag||'='||vals order by tag,vals), ';') as attrs
    from lane_attrs attr
    group by lane_id
)

, ius_attrs as (
    select ius_id, tag, array_to_string(array_agg(value order by value), '&') as vals
    from ius_attribute
    where ius_id is not null and tag is not null
    group by ius_id, tag
)

, ius_attrs_str as (
    select ius_id
         , array_to_string(array_agg('ius.'||tag||'='||vals order by tag,vals), ';') as attrs
    from ius_attrs attr
    group by ius_id
)

, processing_attrs as (
    select processing_id, tag, array_to_string(array_agg(value order by value), '&') as vals
    from processing_attribute
    where processing_id is not null and tag is not null
    group by processing_id, tag
)

, processing_attrs_str as (
    select processing_id
         , array_to_string(array_agg('processing.'||tag||'='||vals order by tag,vals), ';') as attrs
    from processing_attrs attr
    group by processing_id
)

, file_attrs as (
    select file_id, tag, array_to_string(array_agg(value order by value), '&') as vals
    from file_attribute
    where file_id is not null and tag is not null
    group by file_id, tag
)

, file_attrs_str as (
    select file_id
         , array_to_string(array_agg('file.'||tag||'='||vals order by tag,vals), ';') as attrs
    from file_attrs attr
    group by file_id
)

, workflow_attrs as (
    select workflow_id, tag, array_to_string(array_agg(value order by value), '&') as vals
    from workflow_attribute
    where workflow_id is not null and tag is not null
    group by workflow_id, tag
)

, workflow_attrs_str as (
    select workflow_id
         , array_to_string(array_agg('workflow.'||tag||'='||vals order by tag,vals), ';') as attrs
    from workflow_attrs attr
    group by workflow_id
)

, workflow_run_attrs as (
    select workflow_run_id, tag, array_to_string(array_agg(value order by value), '&') as vals
    from workflow_run_attribute
    where workflow_run_id is not null and tag is not null
    group by workflow_run_id, tag
)

, workflow_run_attrs_str as (
    select workflow_run_id
         , array_to_string(array_agg('workflow_run.'||tag||'='||vals order by tag,vals), ';') as attrs
    from workflow_run_attrs attr
    group by workflow_run_id
)

-- concatenated values from sample parents
, sample_parent_swas_names (sample_id, parent_swas, parent_names, parent_organism_ids) as (
    select anc.sample_id
         , array_to_string(array_agg(sw_accession  order by rank), ':') as parent_swas
         , array_to_string(array_agg(coalesce(nullif(s.name,''),s.title) order by rank), ':') as parent_names
         , array_to_string(array_agg(organism_id order by rank), ':') as parent_organism_ids
    from sample_ancestors anc
    join sample s on s.sample_id = anc.ancestor_id
    group by anc.sample_id
)

, root_sample_swas_names as(
    select anc.sample_id, s.sample_id as root_id, s.name as root_name, s.sw_accession as root_accession from sample s join sample_ancestors anc on 
    s.sample_id = anc.ancestor_id join  
    (select anc.sample_id, max(rank)
    from sample_ancestors anc
    group by anc.sample_id) max_ranks
    on (max_ranks.sample_id = anc.sample_id and anc.rank = max_ranks.max) 
)

, sample_parent_attrs (sample_id, parent_attrs) as (
    select anc.sample_id
         , array_to_string(array_agg('parent_sample.'||tag||'.'||sw_accession||'='||vals order by rank), ';') as parent_attrs
    from sample_ancestors anc
    join sample_attrs attr on attr.sample_id = anc.ancestor_id
    join sample s on s.sample_id = anc.ancestor_id
    group by anc.sample_id
)

, sample_parent_skip (sample_id, skip) as (
    select anc.sample_id
         , (true = any(array_agg(s.skip))) as skip
    from sample_ancestors anc
    join sample s on anc.ancestor_id = s.sample_id
    group by anc.sample_id
)

select p.update_tstmp as last_modified
     , translate(st.title, ' ', '_') as study_title
     , st.sw_accession as study_swa
     , sta.attrs as study_attrs
     , translate(case when e.name is not null and e.name <> '' then e.name else e.title end , ' ', '_') as experiment_name
     , e.sw_accession as experiment_swa
     , ea.attrs as experiment_attrs
     , r.root_name as root_sample_name
     , r.root_accession as root_sample_swa
     , translate(spn.parent_names, ' ', '_') as sample_parent_names
     , spn.parent_swas as sample_parent_swas
     , spn.parent_organism_ids as parent_organism_ids
     , spa.parent_attrs as sample_parent_attrs
     , translate(case when s.name is not null and s.name <> '' then s.name else s.title end , ' ', '_') as sample_name
     , s.sw_accession as sample_swa
     , org.organism_id as organism_id
     , translate(org.code, ' ', '_') as organism_code 
     , sa.attrs as sample_attrs
     , translate(sr.name, ' ', '_') as sequencer_run_name
     , sr.sw_accession as sequencer_run_swa
     , sra.attrs as sequencer_run_attrs
     , pla.platform_id as platform_id
     , translate(pla.name, ' ', '_') as platform_name
     , translate(l.name, ' ', '_') as lane_name
     , coalesce(l.lane_index,0)+1 as lane_number
     , l.sw_accession as lane_swa
     , la.attrs as lane_attrs
     , coalesce(i.tag,'NoIndex') as ius_tag
     , i.sw_accession as ius_swa
     , ia.attrs as ius_attrs
     , translate(wf.name, ' ', '_') as workflow_name
     , wf.version as workflow_version
     , wf.sw_accession as workflow_swa
     , wfa.attrs as workflow_attrs
     , translate(wfr.name, ' ', '_') as workflow_run_name
     , wfr.status as workflow_run_status
     , wfr.sw_accession as workflow_run_swa
     , wfra.attrs as workflow_run_attrs
     ,
     ( -- doing this as a scalar subquery, looks like the query optimizer doesn't properly restrict this query as a CTE
       -- and lateral left join will not be available until production gets postgres 9.3
        select array_to_string(array_agg(f.sw_accession order by f.sw_accession), ';') as file_swas
        from workflow_run_input_files wrif
        join file f on wrif.file_id = f.file_id
        where wrif.workflow_run_id = wfr.workflow_run_id
     ) as workflow_run_input_files_swas
     , p.algorithm as processing_algorithm
     , p.sw_accession as processing_swa
     , pa.attrs as processing_attrs
     , p.status as processing_status
     , f.meta_type as file_meta_type
     , f.sw_accession as file_swa
     , fa.attrs as file_attrs
     , f.file_path as file_path
     , f.md5sum as file_md5sum
     , f.size as file_size
     , f.description as file_description
     -- this determines whether this particular path through the database hierarchy should be skipped
     , (case when (f.skip or sr.skip or l.skip or i.skip or s.skip or sps.skip) = true
             then true else false end) as path_skip
     -- this determines whether there are any paths to a particular file that are skipped
     , coalesce((bool_or(f.skip) over (partition by f.sw_accession)) or (bool_or(sr.skip) over (partition by f.sw_accession)) 
       or (bool_or(l.skip) over (partition by f.sw_accession)) or (bool_or(i.skip) over (partition by f.sw_accession)) 
       or (bool_or(s.skip) over (partition by f.sw_accession)) or (bool_or(sps.skip) over (partition by f.sw_accession))
       , false )as skip
from study_report_ids ids
join file f on f.file_id = ids.file_id
left join file_attrs_str fa on fa.file_id = ids.file_id
left join study st on st.study_id = ids.study_id
left join study_attrs_str sta on sta.study_id = ids.study_id
left join experiment e on e.experiment_id = ids.experiment_id
left join experiment_attrs_str ea on ea.experiment_id = ids.experiment_id
join processing p on p.processing_id = ids.processing_id
left join processing_attrs_str pa on pa.processing_id = ids.processing_id
left join root_sample_swas_names r on r.sample_id = ids.sample_id  
left join sample_parent_swas_names spn on spn.sample_id = ids.sample_id
left join sample_parent_attrs spa on spa.sample_id = ids.sample_id
left join sample_parent_skip sps on sps.sample_id = ids.sample_id
left join sample s on s.sample_id = ids.sample_id
left join organism org on s.organism_id = org.organism_id  
left join sample_attrs_str sa on sa.sample_id = ids.sample_id
left join lane l on l.lane_id = ids.lane_id
left join lane_attrs_str la on la.lane_id = ids.lane_id
left join sequencer_run sr on sr.sequencer_run_id = ids.sequencer_run_id
left join platform pla on sr.platform_id = pla.platform_id
left join sequencer_run_attrs_str sra on sra.sequencer_run_id = ids.sequencer_run_id
left join ius i on i.ius_id = ids.ius_id
left join ius_attrs_str ia on ia.ius_id = ids.ius_id
left join workflow_run wfr on (p.workflow_run_id is not null and wfr.workflow_run_id = p.workflow_run_id)
                           or (p.workflow_run_id is null and wfr.workflow_run_id = p.ancestor_workflow_run_id)
left join workflow_run_attrs_str wfra on wfra.workflow_run_id = wfr.workflow_run_id
left join workflow wf on wf.workflow_id = wfr.workflow_id
left join workflow_attrs_str wfa on wfa.workflow_id = wf.workflow_id
);

begin;
drop table if exists file_provenance_report;
create table file_provenance_report as (select * from file_provenance_report_temp);
commit;

--- Discard temporary tables for sure
discard TEMPORARY;
--- update statistics for query planner for the database
VACUUM ANALYZE file_provenance_report;