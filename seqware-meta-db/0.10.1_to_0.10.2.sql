ALTER TABLE sequencer_run_attribute DROP CONSTRAINT fk_sequencer_run_attribute_sample_id;
ALTER TABLE sequencer_run_attribute ADD CONSTRAINT fk_sequencer_run_attribute_sequencer_run_id FOREIGN KEY (sample_id) REFERENCES sequencer_run (sequencer_run_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

