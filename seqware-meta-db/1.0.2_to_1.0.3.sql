update processing set status='running' where status like '%running%';
update processing set status='failed' where status like '%error%';
update processing set status='success' where status = 'processed';

update workflow_run set status='completed' where status = 'success';
update workflow_run set status='completed' where status = 'complete';
