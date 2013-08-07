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
