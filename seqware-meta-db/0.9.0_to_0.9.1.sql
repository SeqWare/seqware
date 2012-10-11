-- Removed lane_workflow_runs and ius_workflow_runs that link to non-existent workflow_runs. This cleanup is necessary
-- before we add constraints (below) that will not permit entries in these tables if the foreign keys do not exist.
delete from lane_workflow_runs where workflow_run_id not in (select workflow_run_id from workflow_run);
delete from ius_workflow_runs where workflow_run_id not in (select workflow_run_id from workflow_run);

ALTER TABLE file ADD COLUMN file_type_id integer;
ALTER TABLE file ADD CONSTRAINT fk_file_file_type_id FOREIGN KEY (file_type_id) REFERENCES file_type (file_type_id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE file ADD CONSTRAINT fk_file_owner_id FOREIGN KEY (owner_id) REFERENCES registration (registration_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ius ADD CONSTRAINT fk_ius_owner_id FOREIGN KEY (owner_id) REFERENCES registration (registration_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ius_link ADD CONSTRAINT pk_ius_link PRIMARY KEY (ius_link_id);
ALTER TABLE ius_link ADD CONSTRAINT fk_ius_link_ius_id FOREIGN KEY (ius_id) REFERENCES ius (ius_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ius_workflow_runs ADD CONSTRAINT pk_ius_workflow_runs PRIMARY KEY (ius_workflow_runs_id);
ALTER TABLE ius_workflow_runs ADD CONSTRAINT fk_ius_workflow_runs_workflow_run_id FOREIGN KEY (workflow_run_id) REFERENCES workflow_run (workflow_run_id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE ius_workflow_runs ADD CONSTRAINT fk_ius_workflow_runs_ius_id FOREIGN KEY (ius_id) REFERENCES ius (ius_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE lane_link ADD CONSTRAINT pk_lane_link PRIMARY KEY (lane_link_id);
ALTER TABLE lane_link ADD CONSTRAINT fk_lane_link_lane_id FOREIGN KEY (lane_id) REFERENCES lane (lane_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE lane_workflow_runs ADD CONSTRAINT pk_lane_workflow_runs PRIMARY KEY (lane_workflow_runs_id);
ALTER TABLE lane_workflow_runs ADD CONSTRAINT fk_lane_workflow_runs_lane_id FOREIGN KEY (lane_id) REFERENCES lane (lane_id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE lane_workflow_runs ADD CONSTRAINT fk_lane_workflow_runs_workflow_run_id FOREIGN KEY (workflow_run_id) REFERENCES workflow_run (workflow_run_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE processing ADD CONSTRAINT fk_processing_owner_id FOREIGN KEY (owner_id) REFERENCES registration (registration_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE sequencer_run_attribute ADD CONSTRAINT fk_sequencer_run_attribute_sample_id FOREIGN KEY (sample_id) REFERENCES sample (sample_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE share_experiment ADD CONSTRAINT pk_share_experiment PRIMARY KEY (share_experiment_id);
ALTER TABLE share_experiment ADD CONSTRAINT fk_share_experiment_experiment_id FOREIGN KEY (experiment_id) REFERENCES experiment (experiment_id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE share_experiment ADD CONSTRAINT fk_share_experiment_registration_id FOREIGN KEY (registration_id) REFERENCES registration (registration_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE share_file ADD COLUMN share_file_id integer NOT NULL;
ALTER TABLE share_file ADD CONSTRAINT pk_share_file PRIMARY KEY (share_file_id);
ALTER TABLE share_file ADD CONSTRAINT fk_share_file_file_id FOREIGN KEY (file_id) REFERENCES file (file_id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE share_file ADD CONSTRAINT fk_share_file_registration_id FOREIGN KEY (registration_id) REFERENCES registration (registration_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE share_lane ADD COLUMN share_lane_id integer NOT NULL;
ALTER TABLE share_lane ADD CONSTRAINT pk_share_lane PRIMARY KEY (share_lane_id);
ALTER TABLE share_lane ADD CONSTRAINT fk_share_lane_lane_id FOREIGN KEY (lane_id) REFERENCES lane (lane_id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE share_lane ADD CONSTRAINT fk_share_lane_registration_id FOREIGN KEY (registration_id) REFERENCES registration (registration_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE share_processing ADD COLUMN share_processing_id integer NOT NULL;
ALTER TABLE share_processing ADD CONSTRAINT pk_share_processing PRIMARY KEY (share_processing_id);
ALTER TABLE share_processing ADD CONSTRAINT fk_share_processing_processing_id FOREIGN KEY (processing_id) REFERENCES processing (processing_id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE share_processing ADD CONSTRAINT fk_share_processing_registration_id FOREIGN KEY (registration_id) REFERENCES registration (registration_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE share_sample ADD COLUMN share_sample_id integer NOT NULL;
ALTER TABLE share_sample ADD CONSTRAINT pk_share_sample PRIMARY KEY (share_sample_id);
ALTER TABLE share_sample ADD CONSTRAINT fk_share_sample_sample_id FOREIGN KEY (sample_id) REFERENCES sample (sample_id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE share_sample ADD CONSTRAINT fk_share_sample_registration_id FOREIGN KEY (registration_id) REFERENCES registration (registration_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE share_study ADD CONSTRAINT fk_share_study_study_id FOREIGN KEY (study_id) REFERENCES study (study_id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE share_study ADD CONSTRAINT fk_share_study_registration_id FOREIGN KEY (registration_id) REFERENCES registration (registration_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE share_workflow_run ADD CONSTRAINT fk_share_workflow_run_registration_id FOREIGN KEY (registration_id) REFERENCES registration (registration_id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE share_workflow_run ADD CONSTRAINT fk_share_workflow_run_workflow_run_id FOREIGN KEY (workflow_run_id) REFERENCES workflow_run (workflow_run_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE workflow ADD CONSTRAINT fk_workflow_registration_id FOREIGN KEY (owner_id) REFERENCES registration (registration_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE workflow_param ADD CONSTRAINT pk_workflow_param PRIMARY KEY (workflow_param_id);
ALTER TABLE workflow_param ADD CONSTRAINT fk_workflow_param_workflow_id FOREIGN KEY (workflow_id) REFERENCES workflow (workflow_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE workflow_param_value ADD CONSTRAINT pk_workflow_param_value PRIMARY KEY (workflow_param_value_id);
ALTER TABLE workflow_param_value ADD CONSTRAINT fk_workflow_param_value_workflow_param_id FOREIGN KEY (workflow_param_id) REFERENCES workflow_param (workflow_param_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE workflow_run ADD CONSTRAINT fk_workflow_run_registration_id FOREIGN KEY (owner_id) REFERENCES registration (registration_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE workflow_run_param ADD CONSTRAINT pk_workflow_run_param PRIMARY KEY (workflow_run_param_id);
ALTER TABLE workflow_run_param ADD CONSTRAINT fk_workflow_run_param_workflow_run_id FOREIGN KEY (workflow_run_id) REFERENCES workflow_run (workflow_run_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE INDEX index_processing_relationship_parent_id
   ON processing_relationship (parent_id ASC NULLS LAST);
CREATE INDEX index_processing_relationship_child_id
   ON processing_relationship (child_id ASC NULLS LAST);

