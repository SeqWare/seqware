-- Table: workflow_run_files

-- DROP TABLE workflow_run_files;

CREATE TABLE workflow_run_files
(
  workflow_run_id integer,
  file_id integer,
  CONSTRAINT workflow_run_files_file_id_fkey FOREIGN KEY (file_id)
      REFERENCES file (file_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT workflow_run_id FOREIGN KEY (workflow_run_id)
      REFERENCES workflow_run (workflow_run_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE workflow_run_files
  OWNER TO seqware;

-- Index: file_id_workflow_run_files

-- DROP INDEX file_id_workflow_run_files;

CREATE INDEX file_id_workflow_run_files
  ON workflow_run_files
  USING btree
  (file_id);

-- Index: workflow_run_id_workflow_run_files

-- DROP INDEX workflow_run_id_workflow_run_files;

CREATE INDEX workflow_run_id_workflow_run_files
  ON workflow_run_files
  USING btree
  (workflow_run_id);
