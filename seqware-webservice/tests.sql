<!--testAdd_empty_processing_event()-->

select max(create_tstmp) from processing where processing_id in (select distinct child_id from processing_relationship where parent_id IN (1,4));


<!--testAdd_empty_processing_event_by_parent_accession()-->

select max(create_tstmp) from processing where processing_id in (select distinct processing_id from processing_lanes pl, lane l where l.sw_accession = 24823);
select max(create_tstmp) from processing where processing_id in (select distinct processing_id from processing_ius pl, ius l where l.sw_accession = 24824);



<!--testAdd_task_group-->

select max(create_tstmp, task_group from processing where processing_id in (select distinct child_id from processing_relationship where parent_id IN (5,7)) AND processing_id in (select distinct parent_id from processing_relationship where child_id IN (8,9));


<!--testAdd_workflow_run()-->
select max(create_tstmp) from workflow_run where workflow_id = 27;


<!--testAdd_workflow_run_ancestor()-->
select update_tstmp from processing where ancestor_workflow_run_id = 27 and processing_id=902;



<!--testAssociate_processing_event_with_parents_and_child()-->
select count(*) from processing_relationship where (parent_id=12 AND child_id IN (9,13)) OR (child_id=12 AND parent_id IN (10,11));

<!--should be 4-->

<!--testLinkWorkflowRunAndParent()-->
select count(*) from ius_workflow_runs where ius_id = 31 and workflow_run_id = 30;


<!--testProcessing_event_to_task_group()-->
select count(*) from processing_relationship where (parent_id=14 AND child_id IN (17,18)) OR (child_id=14 AND parent_id IN (15,16));

<!--//should be 4-->

<!--testUpdate_processing_event()-->
select update_tstmp from processing where processing_id=19 and process_exit_status=81 and algorithm='algo testUpdate_processing_event()' and version = '0.7.0';


<!--testUpdate_processing_status()-->
select update_tstmp from processing where processing_id=20 and status='testUpdate_processing_status()';

<!--testUpdate_processing_workflow_run()-->
select update_tstmp from processing where processing_id=21 and workflow_run_id=44;

<!--testUpdate_workflow_run()-->
<!--DOESN'T WORK YET -->
select update_tstmp from workflow_run where workflow_run_id=14;

