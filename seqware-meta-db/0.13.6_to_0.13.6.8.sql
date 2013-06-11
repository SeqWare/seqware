--
-- Name: ancestor_workflow_run_processing; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX ancestor_workflow_run_processing ON processing USING btree (ancestor_workflow_run_id);


--
-- Name: email_registration; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX email_registration ON registration USING btree (email);


--
-- Name: experiment_id_sample; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX experiment_id_sample ON sample USING btree (experiment_id);


--
-- Name: file_id_processing_files; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX file_id_processing_files ON processing_files USING btree (file_id);

--
-- Name: ius_id_ius_workflow_runs; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX ius_id_ius_workflow_runs ON ius_workflow_runs USING btree (ius_id);


--
-- Name: parent_sample_hierarchy; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX parent_sample_hierarchy ON sample_hierarchy USING btree (parent_id);


--
-- Name: processing_id_processing_files; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX processing_id_processing_files ON processing_files USING btree (processing_id);


--
-- Name: sample_id_ius; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX sample_id_ius ON ius USING btree (sample_id);


--
-- Name: sample_id_processing_samples; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX sample_id_processing_samples ON processing_samples USING btree (sample_id);


--
-- Name: sample_id_sample_attribute; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX sample_id_sample_attribute ON sample_attribute USING btree (sample_id);


--
-- Name: status_workflow_run; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX status_workflow_run ON workflow_run USING btree (status);


--
-- Name: sw_accession_file; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX sw_accession_file ON file USING btree (sw_accession);


--
-- Name: sw_accession_processing; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE UNIQUE INDEX sw_accession_processing ON processing USING btree (sw_accession);


--
-- Name: sw_accession_workflow_run; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE UNIQUE INDEX sw_accession_workflow_run ON workflow_run USING btree (sw_accession);


--
-- Name: workflow_run_id_ius_workflow_runs; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX workflow_run_id_ius_workflow_runs ON ius_workflow_runs USING btree (workflow_run_id);


--
-- Name: workflow_run_processing; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX workflow_run_processing ON processing USING btree (workflow_run_id);

