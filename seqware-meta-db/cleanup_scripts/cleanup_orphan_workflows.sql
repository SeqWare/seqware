WITH orphan_workflows AS (SELECT workflow_id FROM workflow WHERE workflow_id NOT IN (select workflow_id from workflow_run)),
orphan_params AS (select workflow_param_id from workflow_param wp, workflow w WHERE wp.workflow_id = w.workflow_id AND w.workflow_id IN (select * from orphan_workflows))
DELETE from workflow_param_value WHERE workflow_param_id IN (select * from orphan_params);
WITH orphan_workflows AS (SELECT workflow_id FROM workflow WHERE workflow_id NOT IN (select workflow_id from workflow_run))
DELETE from workflow_param WHERE workflow_id IN (select * from orphan_workflows);
DELETE from workflow WHERE workflow_id NOT IN (select workflow_id from workflow_run);
