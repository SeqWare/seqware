-- Table: workflow_run_input_files

-- DROP TABLE workflow_run_input_files;

CREATE TABLE workflow_run_input_files
(
  workflow_run_id integer,
  file_id integer,
  CONSTRAINT workflow_run_id FOREIGN KEY (workflow_run_id)
      REFERENCES workflow_run (workflow_run_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT file_id FOREIGN KEY (file_id)
      REFERENCES file (file_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT workflow_run_input_files_unique UNIQUE (workflow_run_id, file_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE workflow_run_input_files
  OWNER TO seqware;

-- Index: file_id_workflow_run_input_files

-- DROP INDEX file_id_workflow_run_input_files;

CREATE INDEX file_id_workflow_run_input_files
  ON workflow_run_input_files
  USING btree
  (file_id);

-- Index: workflow_run_id_workflow_run_input_files

-- DROP INDEX workflow_run_id_workflow_run_input_files;

CREATE INDEX workflow_run_id_workflow_run_input_files
  ON workflow_run_input_files
  USING btree
  (workflow_run_id);

-- Note: This constraint is already in our test database but should also be in the production database
-- Name: sample_hierarchy_sample_id_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY sample_hierarchy
    ADD CONSTRAINT sample_hierarchy_sample_id_key UNIQUE (sample_id, parent_id);


-- Partial indices enforce uniqueness for both cases when parent_id is null and when it is not null

CREATE UNIQUE INDEX sample_hierarchy_not_null
ON sample_hierarchy (sample_id, parent_id)
WHERE parent_id IS NOT NULL;

CREATE UNIQUE INDEX sample_hierarchy_null
ON sample_hierarchy (sample_id)
WHERE parent_id IS NULL;

-- Fix old statuses that do not conform to the new enums
update processing set status='running' where status like '%running%';
update processing set status='failed' where status like '%error%';
update processing set status='success' where status = 'processed';

update workflow_run set status='completed' where status = 'success';
update workflow_run set status='completed' where status = 'complete';
