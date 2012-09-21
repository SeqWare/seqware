CREATE TABLE file_report
(
  row_id serial NOT NULL,
  study_id integer,
  ius_id integer,
  lane_id integer,
  file_id integer,
  sample_id integer,
  experiment_id integer,
  child_sample_id integer,
  processing_id integer,
  CONSTRAINT file_report_pkey PRIMARY KEY (row_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE file_report OWNER TO seqware;

-- Index: file_report_study_id_idx

-- DROP INDEX file_report_study_id_idx;

CREATE INDEX file_report_study_id_idx
  ON file_report
  USING btree
  (study_id);

CREATE TABLE sample_report
(
  study_id integer,
  child_sample_id integer,
  workflow_id integer,
  status character varying(255),
  row_id serial NOT NULL,
  CONSTRAINT sample_report_pkey PRIMARY KEY (row_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sample_report OWNER TO seqware;

-- Index: sample_report_study_id_child_sample_id_workflow_id_idx

-- DROP INDEX sample_report_study_id_child_sample_id_workflow_id_idx;

CREATE INDEX sample_report_study_id_child_sample_id_workflow_id_idx
  ON sample_report
  USING btree
  (study_id, child_sample_id, workflow_id);

-- Function: fill_file_report()

-- DROP FUNCTION fill_file_report();

CREATE OR REPLACE FUNCTION fill_file_report()
  RETURNS boolean AS
$BODY$
DECLARE
  _study_id INTEGER;
  _root_sample INTEGER;
  _sample_id INTEGER;
  _ius_id INTEGER;
  _lane_id INTEGER;
  _file_id INTEGER;
  _experiment_id INTEGER;

-- temp data
  _parent_sample_id INTEGER;
  _child_sample_id INTEGER;
  _processing_id INTEGER;
  _ius_data ius%ROWTYPE;
  _sample_record RECORD;

 -- _child_samples_id INTEGER ARRAY[4];

BEGIN
  FOR _study_id IN 
	SELECT study_id FROM study
  LOOP
	--RAISE NOTICE 'STUDY %', _study_id;
	FOR _sample_record IN -- SAMPLE_ID is Root Sample here
		WITH RECURSIVE root_to_leaf(root_sample, child_id) AS (
		SELECT sample_id, sample_id FROM sample s
		JOIN experiment e ON (s.experiment_id = e.experiment_id)
		JOIN study st ON (st.study_id = e.study_id)
		WHERE st.study_id = _study_id
		UNION
		SELECT rl.root_sample, sr.child_id FROM sample_relationship sr 
		JOIN root_to_leaf rl ON (sr.parent_id = rl.child_id) )
		select * from root_to_leaf
	LOOP
		_parent_sample_id := _sample_record.root_sample;
		-- FIND parent sample
		WITH RECURSIVE rec(parent_id) AS (
		SELECT parent_id FROM sample_hierarchy
		WHERE sample_id = _parent_sample_id
		UNION
		SELECT sh.parent_id FROM sample_hierarchy sh, rec r
		WHERE sh.sample_id = r.parent_id )
		SELECT r.parent_id INTO _parent_sample_id FROM rec r, sample_hierarchy sh
		WHERE r.parent_id = sh.sample_id
		AND sh.parent_id IS NULL;

		IF _parent_sample_id IS NULL THEN
		  _parent_sample_id := _sample_record.root_sample;
		END IF;
		
		_sample_id := _sample_record.child_id;
		SELECT experiment_id INTO _experiment_id FROM sample 
		WHERE sample_id = _parent_sample_id;
		--RAISE NOTICE 'SAMPLE ROOT %', _parent_sample_id;
		--RAISE NOTICE 'SAMPLE %', _sample_id;
		-- Let's get all IUSs here
		FOR _ius_data IN 
			SELECT * FROM ius i
			WHERE i.sample_id = _sample_id
		LOOP
			_ius_id := _ius_data.ius_id;
			_lane_id := _ius_data.lane_id;
			--RAISE NOTICE 'IUS %, LANE %', _ius_id, _lane_id;
			
			FOR _processing_id IN 
				WITH RECURSIVE proces_to_leaf(child_id) AS (
					SELECT pi.processing_id FROM processing_ius pi
					WHERE pi.ius_id = _ius_id
					UNION
					SELECT pr.child_id FROM processing_relationship pr, proces_to_leaf pl
					WHERE pr.parent_id = pl.child_id )
				SELECT * FROM proces_to_leaf
			LOOP
--				RAISE NOTICE 'PROCESSING %', _processing_id;
				IF EXISTS (SELECT file_id FROM processing_files
				WHERE processing_id = _processing_id) THEN
					FOR _file_id IN SELECT file_id FROM processing_files
						WHERE processing_id = _processing_id	
					LOOP
						--RAISE NOTICE 'FILE %', _file_id;
						INSERT INTO file_report (study_id, experiment_id, sample_id, child_sample_id, ius_id, lane_id, processing_id, file_id)
						VALUES (_study_id, _experiment_id, _parent_sample_id, _sample_id, _ius_id, _lane_id, _processing_id, _file_id);
					END LOOP;
				END IF;
			END LOOP;
		END LOOP;
	END LOOP;
  END LOOP;

  RETURN TRUE;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fill_file_report() OWNER TO seqware;

-- Function: fill_sample_report()

-- DROP FUNCTION fill_sample_report();

CREATE OR REPLACE FUNCTION fill_sample_report()
  RETURNS boolean AS
$BODY$
DECLARE
  _study_id INTEGER;
  _root_sample INTEGER;
  _sample_id INTEGER;
  _ius_id INTEGER;
  _lane_id INTEGER;
  _file_id INTEGER;
  _workflow_id INTEGER;
  _sequencer_run_id INTEGER;

-- temp data
  _parent_sample_id INTEGER;
  _child_sample_id INTEGER;
  _processing_id INTEGER;
  _ius_data ius%ROWTYPE;
  _sample_record RECORD;
  _old_status VARCHAR(255);
  _new_status VARCHAR(255);
  _update_needed BOOLEAN;

BEGIN
  FOR _study_id IN 
	SELECT study_id FROM study
  LOOP
	--RAISE NOTICE 'STUDY %', _study_id;
	FOR _sample_record IN -- SAMPLE_ID is Root Sample here
		WITH RECURSIVE root_to_leaf(root_sample, child_id) AS (
		SELECT sample_id, sample_id FROM sample s
		JOIN experiment e ON (s.experiment_id = e.experiment_id)
		JOIN study st ON (st.study_id = e.study_id)
		WHERE st.study_id = _study_id
		UNION
		SELECT rl.root_sample, sr.child_id FROM sample_relationship sr 
		JOIN root_to_leaf rl ON (sr.parent_id = rl.child_id) )
		select * from root_to_leaf
	LOOP
		_parent_sample_id := _sample_record.root_sample;
		_sample_id := _sample_record.child_id;
		--RAISE NOTICE 'SAMPLE ROOT %', _parent_sample_id;
		--RAISE NOTICE 'SAMPLE %', _sample_id;
		-- Let's get all IUSs here
		FOR _ius_data IN 
			SELECT * FROM ius i
			WHERE i.sample_id = _sample_id
		LOOP
			_ius_id := _ius_data.ius_id;
			_lane_id := _ius_data.lane_id;
			SELECT sequencer_run_id INTO _sequencer_run_id FROM lane
			WHERE lane_id = _lane_id;

			--RAISE NOTICE 'IUS %, LANE %', _ius_id, _lane_id;
			FOR _processing_id IN 
				WITH RECURSIVE proces_to_leaf(child_id) AS (
					SELECT pi.processing_id FROM processing_ius pi
					WHERE pi.ius_id = _ius_id
					UNION
					SELECT pr.child_id FROM processing_relationship pr, proces_to_leaf pl
					WHERE pr.parent_id = pl.child_id )
				SELECT * FROM proces_to_leaf
			LOOP
				SELECT wr.workflow_id, wr.status INTO _workflow_id, _new_status FROM processing p
				JOIN workflow_run wr ON (p.workflow_run_id = wr.workflow_run_id)
				WHERE p.processing_id = _processing_id;

				IF _new_status IS NULL THEN
					-- check ancestor run for status
					SELECT wr.status, wr.workflow_id INTO _new_status, _workflow_id FROM processing p
					JOIN workflow_run wr ON (p.ancestor_workflow_run_id = wr.workflow_run_id)
					WHERE p.processing_id = _processing_id;
				END IF;

				IF _workflow_id IS NOT NULL THEN
					IF EXISTS (SELECT status FROM sample_report 
						WHERE study_id = _study_id
						AND child_sample_id = _sample_id
						AND workflow_id = _workflow_id
						AND lane_id = _lane_id
						AND ius_id = _ius_id
						AND sequencer_run_id = _sequencer_run_id)
					THEN
						SELECT status INTO _old_status FROM sample_report 
						WHERE study_id = _study_id
						AND child_sample_id = _sample_id
						AND workflow_id = _workflow_id
						AND lane_id = _lane_id
						AND ius_id = _ius_id
						AND sequencer_run_id = _sequencer_run_id;
							
						_update_needed := false;
						IF ( _old_status != 'completed'
							AND _old_status != 'pending'
							AND _old_status != 'running'
							AND _old_status != 'failed') THEN
							_update_needed := true;
						ELSIF (_old_status = 'failed' 
							AND (_new_status = 'completed' or _new_status = 'pending' or _new_status = 'running')) THEN
							_update_needed := true;
						ELSIF ((_old_status = 'running' or _old_status = 'pending') and (_new_status = 'completed')) THEN
							_update_needed := true;
						END IF;
						
						IF _update_needed THEN
							UPDATE sample_report SET status = _new_status
							WHERE study_id = _study_id
							AND child_sample_id = _sample_id
							AND workflow_id = _workflow_id
							AND lane_id = _lane_id
							AND ius_id = _ius_id
							AND sequencer_run_id = _sequencer_run_id;
						END IF;
					ELSE 
						INSERT INTO sample_report(study_id, child_sample_id, workflow_id, status, sequencer_run_id, lane_id, ius_id)
						VALUES (_study_id, _sample_id, _workflow_id, _new_status, _sequencer_run_id, _lane_id, _ius_id);
					END IF;
				END IF;
			END LOOP;
		END LOOP;
	END LOOP;
  END LOOP;
  RETURN TRUE;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fill_sample_report()
  OWNER TO seqware;

-- Function: current_status_new(integer, integer)

-- DROP FUNCTION current_status_new(integer, integer);

CREATE OR REPLACE FUNCTION current_status_new(_ius_id integer, _workflow_id integer)
  RETURNS character varying AS
$BODY$DECLARE
  _study_id INTEGER;
  _root_sample INTEGER;
  _sample_id INTEGER;
  _ius_id INTEGER;
  _lane_id INTEGER;
  _file_id INTEGER;
  _workflow_id INTEGER;

-- temp data
  _parent_sample_id INTEGER;
  _child_sample_id INTEGER;
  _processing_id INTEGER;
  _ius_data ius%ROWTYPE;
  _sample_record RECORD;
  _old_status VARCHAR(255);
  _new_status VARCHAR(255);
  _update_needed BOOLEAN;
  _workflow_run_id INTEGER;

BEGIN
--_study_id = $1;
--_sample_id = $2;
_ius_id = $1;
_workflow_id = $2;
--_old_status = 'notstarted';
FOR _processing_id IN 
	WITH RECURSIVE proces_to_leaf(child_id) AS (
		SELECT pi.processing_id FROM processing_ius pi
		WHERE pi.ius_id = _ius_id
		UNION
		SELECT pr.child_id FROM processing_relationship pr, proces_to_leaf pl
		WHERE pr.parent_id = pl.child_id )
	SELECT * FROM proces_to_leaf
LOOP
	SELECT wr.status, wr.workflow_run_id INTO _new_status, _workflow_run_id FROM processing p
	JOIN workflow_run wr ON (p.workflow_run_id = wr.workflow_run_id)
	WHERE p.processing_id = _processing_id
	AND wr.workflow_id = _workflow_id;

	IF _new_status IS NULL THEN
		-- check ancestor run for status
		SELECT wr.status, wr.workflow_run_id INTO _new_status, _workflow_run_id FROM processing p
		JOIN workflow_run wr ON (p.ancestor_workflow_run_id = wr.workflow_run_id)
		WHERE p.processing_id = _processing_id
		AND wr.workflow_id = _workflow_id;
	END IF;

	IF _new_status IS NOT NULL THEN
		-- now we have status for current workflow
		IF _old_status IS NULL THEN
		  _old_status := _new_status;
		ELSIF _new_status = 'completed' THEN
		  RETURN _new_status;
		ELSIF (_old_status = 'failed' 
			AND (_new_status = 'pending' or _new_status = 'running')) THEN
		  _old_status := _new_status;
		END IF;			
		--RAISE NOTICE 'PROCESSING %, STATUS %, WORKFLOW_RUN %', _processing_id, _old_status, _workflow_run_id;
	END IF;
END LOOP;
RETURN _old_status;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION current_status_new(integer, integer) OWNER TO seqware;


-- Function: "FileReportInsert"()

-- DROP FUNCTION "FileReportInsert"();

CREATE OR REPLACE FUNCTION "FileReportInsert"()
  RETURNS trigger AS
$BODY$
DECLARE 
  _study_id INTEGER;
  _root_sample_id INTEGER;
  _child_sample_id INTEGER;
  _parent_sample_id INTEGER;
  _tissue_source VARCHAR(255);
  _library_type VARCHAR(255);
  _template_type VARCHAR(255);
  _ius_swid INTEGER;
  _lane_id INTEGER;
  _file_path TEXT;
  _processing_id INTEGER;
  _experiment_id INTEGER;
  _file_id INTEGER;

  -- tmp
  _parent_processing_id INTEGER;
  _child_processing_id INTEGER;
  _bottom_processing_id INTEGER;
  _bottom_sample_id INTEGER;
  _ius_id INTEGER;

BEGIN
  --PERFORM lock_ns_tree(NEW.tree);
  RAISE NOTICE 'START TRIGGER';
  SELECT file_path INTO _file_path FROM file 
	WHERE file.file_id = NEW.file_id;
  _processing_id := NEW.processing_id;
  _file_id := NEW.file_id;
	
  RAISE NOTICE 'FILE PATH %', _file_path;

  FOR _ius_id IN 
		WITH RECURSIVE rec (parent_id, child_id) AS (
		select _processing_id, null
		union 
		select pr.parent_id, r.child_id from processing_relationship pr, rec r
		where r.parent_id = pr.child_id )

		select pi.ius_id from rec r
		join processing_ius pi on (r.parent_id = pi.processing_id)
		--union 
		--select ius_id from ius_workflow_runs where workflow_run_id = _workflow_run_id
		  --SELECT pi.ius_id FROM processing_ius pi
				 --WHERE pi.processing_id = _processing_id
  LOOP
    SELECT i.sw_accession, i.lane_id, i.sample_id INTO _ius_swid, _lane_id, _child_sample_id 
    FROM ius i
    WHERE i.ius_id = _ius_id;

    -- Determine Root Sample
    FOR _root_sample_id IN
		WITH RECURSIVE rec (parent_id, child_id) AS (
		SELECT _child_sample_id, null
		UNION
		SELECT sr.parent_id, r.child_id FROM sample_relationship sr, rec r
		WHERE r.parent_id = sr.child_id )
		SELECT parent_id FROM rec
    LOOP
      SELECT experiment_id INTO _experiment_id FROM sample
      WHERE sample_id = _root_sample_id;
      IF _experiment_id IS NOT NULL THEN
        SELECT s.study_id INTO _study_id FROM study s
	JOIN experiment e ON (s.study_id = e.study_id)
	WHERE e.experiment_id = _experiment_id;

	-- FIND parent sample
	WITH RECURSIVE rec(parent_id) AS (
	SELECT parent_id FROM sample_hierarchy
	WHERE sample_id = _root_sample_id
	UNION
	SELECT sh.parent_id FROM sample_hierarchy sh, rec r
	WHERE sh.sample_id = r.parent_id )
	SELECT r.parent_id INTO _parent_sample_id FROM rec r, sample_hierarchy sh
	WHERE r.parent_id = sh.sample_id
	AND sh.parent_id IS NULL;

	IF _parent_sample_id IS NULL THEN
	  _parent_sample_id := _root_sample_id;
	END IF;

	INSERT INTO file_report(study_id, experiment_id, sample_id, child_sample_id, ius_id, lane_id, processing_id, file_id) 
	VALUES (_study_id, _experiment_id, _parent_sample_id, _child_sample_id, _ius_id, _lane_id, _processing_id, _file_id);
      END IF;
    END LOOP;
		
  END LOOP;
  
  RETURN NEW;
END
  
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "FileReportInsert"() OWNER TO seqware;


-- Function: "FileReportDelete"()

-- DROP FUNCTION "FileReportDelete"();

CREATE OR REPLACE FUNCTION "FileReportDelete"()
  RETURNS trigger AS
$BODY$
BEGIN 
  DELETE FROM file_report fr
  WHERE fr.file_id = OLD.file_id;

  RETURN NEW;
END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "FileReportDelete"() OWNER TO seqware;


-- Function: "SampleReportUpdate"()

-- DROP FUNCTION "SampleReportUpdate"();

CREATE OR REPLACE FUNCTION "SampleReportUpdate"()
  RETURNS trigger AS
$BODY$DECLARE 
  _old_status VARCHAR(255);
  _new_status VARCHAR(255);
  _current_status VARCHAR(255);

  _processing_id INTEGER;
  _child_processing_id INTEGER;
  _child_sample_id INTEGER;
  _root_sample_id INTEGER;
  _ius_id INTEGER;
  _study_id INTEGER;
  _workflow_id INTEGER;
  _workflow_run_id INTEGER;

BEGIN
  _workflow_id := NEW.workflow_id;
  _workflow_run_id := NEW.workflow_run_id;
  _new_status := NEW.status;
  RAISE NOTICE 'Workflow %', _workflow_id;
 /* FOR _processing_id IN SELECT processing_id FROM processing
			WHERE workflow_run_id = NEW.workflow_run_id
  LOOP

	  RAISE NOTICE 'Processing %', _processing_id;
	  
	  IF _processing_id IS NOT NULL THEN
	  
		  -- check if this processing_id belongs to right to the sample
		  SELECT ps.sample_id INTO _child_sample_id FROM processing_samples ps
		  WHERE ps.processing_id = _processing_id;

		  IF _child_sample_id IS NULL THEN
		  -- this is nested processing, look for the parent processing
		    _child_processing_id = _processing_id;
		    LOOP

		      SELECT pi.ius_id INTO _ius_id FROM processing_ius pi
				 WHERE pi.processing_id = _child_processing_id;
		      RAISE NOTICE 'IUS %', _ius_id;
		      PERFORM update_sample_report_processing( _child_processing_id, _workflow_id );

		      SELECT pr.parent_id INTO _processing_id FROM processing_relationship pr
		      WHERE pr.child_id = _child_processing_id;
			
		      IF _processing_id IS NULL THEN -- this is the top processing element
				_processing_id := _child_processing_id;
				EXIT;
		      END IF;
		      
		      _child_processing_id := _processing_id;

		    END LOOP;
		    SELECT ps.sample_id INTO _child_sample_id FROM processing_samples ps
		    WHERE ps.processing_id = _processing_id;
		  END IF;      

		  RAISE NOTICE 'Root Processing %', _processing_id;
		*/  
		  -- find appropriate IUS_ID
		  FOR _ius_id IN --SELECT ius_id from ius_workflow_runs 
				 --where workflow_run_id = _workflow_run_id
				 -- More reliable solution
				WITH RECURSIVE rec (parent_id, child_id) AS (
				select processing_id, null from processing where workflow_run_id = _workflow_run_id
				union 
				select pr.parent_id, r.child_id from processing_relationship pr, rec r
				where r.parent_id = pr.child_id )

				select pi.ius_id from rec r
				join processing_ius pi on (r.parent_id = pi.processing_id)
				union 
				select ius_id from ius_workflow_runs where workflow_run_id = _workflow_run_id
		  --SELECT pi.ius_id FROM processing_ius pi
				 --WHERE pi.processing_id = _processing_id
		  LOOP

			  RAISE NOTICE 'IUS %', _ius_id;
			  
			  IF _ius_id IS NOT NULL THEN
			 
				  SELECT sample_id INTO _child_sample_id FROM ius
				  WHERE ius_id = _ius_id;

				  --RAISE NOTICE 'Child Sample %', _child_sample_id;
				  
				  -- look for the root sample
				  LOOP
					SELECT sr.parent_id INTO _root_sample_id FROM sample_relationship sr
						WHERE sr.child_id = _child_sample_id;
						
					IF _root_sample_id IS NULL THEN -- this is the top processing element
						_root_sample_id := _child_sample_id;
						EXIT;
					END IF;
				  
					_child_sample_id := _root_sample_id;
					
				  END LOOP;

				  --RAISE NOTICE 'Root Sample %', _root_sample_id;

				  -- get relevant study_id
				  SELECT s.study_id INTO _study_id FROM study s, experiment e, sample sa
				  WHERE s.study_id = e.study_id
				  AND e.experiment_id = sa.experiment_id
				  AND sa.sample_id = _root_sample_id;

				  -- Update 
				  SELECT current_status_new(_ius_id, _workflow_id) INTO _current_status;
				  SELECT status INTO _old_status FROM sample_report
				  WHERE study_id = _study_id
				  AND child_sample_id = _child_sample_id
				  AND workflow_id = _workflow_id;

				  RAISE NOTICE 'Current Status %', _current_status;
				  RAISE NOTICE 'New Status %', _new_status;
				  RAISE NOTICE 'Old Status %', _old_status;

				  IF _old_status IS NULL THEN
				    INSERT INTO sample_report(study_id, child_sample_id, workflow_id, status)
				    VALUES (_study_id, _child_sample_id, _workflow_id, _new_status);
				  ELSE
				      UPDATE sample_report SET status = _current_status
				      WHERE study_id = _study_id
				      AND child_sample_id = _child_sample_id
				      AND workflow_id = _workflow_id;
				  END IF;  
				 
			END IF;
		  END LOOP;
/*	END IF;
  END LOOP; */
   RETURN NEW;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "SampleReportUpdate"() OWNER TO seqware;

-- Function: "SampleReportDelete"()

-- DROP FUNCTION "SampleReportDelete"();

CREATE OR REPLACE FUNCTION "SampleReportDelete"()
  RETURNS trigger AS
$BODY$DECLARE 
  _old_status VARCHAR(255);
  _new_status VARCHAR(255);
  _current_status VARCHAR(255);

  _processing_id INTEGER;
  _child_processing_id INTEGER;
  _child_sample_id INTEGER;
  _root_sample_id INTEGER;
  _ius_id INTEGER;
  _study_id INTEGER;
  _workflow_id INTEGER;
  _workflow_run_id INTEGER;

BEGIN
  _workflow_id := OLD.workflow_id;
  _workflow_run_id := OLD.workflow_run_id;
  _new_status := OLD.status;
  RAISE NOTICE 'Workflow %', _workflow_id;
   
  -- find appropriate IUS_ID
  FOR _ius_id IN 
		 -- More reliable solution
		WITH RECURSIVE rec (parent_id, child_id) AS (
		select processing_id, null from processing where workflow_run_id = _workflow_run_id
		union 
		select pr.parent_id, r.child_id from processing_relationship pr, rec r
		where r.parent_id = pr.child_id )

		select pi.ius_id from rec r
		join processing_ius pi on (r.parent_id = pi.processing_id)
		union 
		select ius_id from ius_workflow_runs where workflow_run_id = _workflow_run_id

  LOOP

	  RAISE NOTICE 'IUS %', _ius_id;
	  
	  IF _ius_id IS NOT NULL THEN
	 
		  SELECT sample_id INTO _child_sample_id FROM ius
		  WHERE ius_id = _ius_id;

		  --RAISE NOTICE 'Child Sample %', _child_sample_id;
		  
		  -- look for the root sample
		  LOOP
			SELECT sr.parent_id INTO _root_sample_id FROM sample_relationship sr
				WHERE sr.child_id = _child_sample_id;
				
			IF _root_sample_id IS NULL THEN -- this is the top processing element
				_root_sample_id := _child_sample_id;
				EXIT;
			END IF;
		  
			_child_sample_id := _root_sample_id;
			
		  END LOOP;

		  --RAISE NOTICE 'Root Sample %', _root_sample_id;

		  -- get relevant study_id
		  SELECT s.study_id INTO _study_id FROM study s, experiment e, sample sa
		  WHERE s.study_id = e.study_id
		  AND e.experiment_id = sa.experiment_id
		  AND sa.sample_id = _root_sample_id;

		  -- Update 
		  SELECT current_status_new(_ius_id, _workflow_id) INTO _current_status;
		  SELECT status INTO _old_status FROM sample_report
		  WHERE study_id = _study_id
		  AND child_sample_id = _child_sample_id
		  AND workflow_id = _workflow_id;

		  RAISE NOTICE 'Current Status %', _current_status;
		  RAISE NOTICE 'New Status %', _new_status;
		  RAISE NOTICE 'Old Status %', _old_status;

		  IF _current_status IS NULL THEN
		    DELETE FROM sample_report
		    WHERE study_id = _study_id 
		    AND child_sample_id = _child_sample_id
		    AND workflow_id = _workflow_id;
		  ELSE
		      UPDATE sample_report SET status = _current_status
		      WHERE study_id = _study_id
		      AND child_sample_id = _child_sample_id
		      AND workflow_id = _workflow_id;
		  END IF;  
		 
	END IF;
  END LOOP;

  RETURN NEW;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "SampleReportDelete"() OWNER TO seqware;


-- Trigger: FileReportTrigger on processing_files

-- DROP TRIGGER "FileReportTrigger" ON processing_files;

CREATE TRIGGER "FileReportTrigger"
  AFTER INSERT
  ON processing_files
  FOR EACH ROW
  EXECUTE PROCEDURE "FileReportInsert"();

-- Trigger: removeFileReport on processing_files

-- DROP TRIGGER "removeFileReport" ON processing_files;

CREATE TRIGGER "removeFileReport"
  AFTER DELETE
  ON processing_files
  FOR EACH ROW
  EXECUTE PROCEDURE "FileReportDelete"();

-- Trigger: sampleReportDelete on workflow_run

-- DROP TRIGGER "sampleReportDelete" ON workflow_run;

CREATE TRIGGER "sampleReportDelete"
  AFTER DELETE
  ON workflow_run
  FOR EACH ROW
  EXECUTE PROCEDURE "SampleReportDelete"();

-- Trigger: sample_report_update on workflow_run

-- DROP TRIGGER sample_report_update ON workflow_run;

CREATE TRIGGER sample_report_update
  AFTER INSERT OR UPDATE
  ON workflow_run
  FOR EACH ROW
  EXECUTE PROCEDURE "SampleReportUpdate"();
