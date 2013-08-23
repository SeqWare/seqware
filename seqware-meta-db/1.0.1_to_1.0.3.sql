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

