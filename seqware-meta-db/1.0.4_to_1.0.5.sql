update workflow_run set status='failed' where status = 'unknown';
update workflow_run set status='failed' where status IS NULL;
