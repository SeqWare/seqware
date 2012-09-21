--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--



SET search_path = public, pg_catalog;

--
-- Name: FileReportDelete(); Type: FUNCTION; Schema: public; Owner: seqware
--

CREATE FUNCTION "FileReportDelete"() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN 
  DELETE FROM file_report fr
  WHERE fr.file_id = OLD.file_id;

  RETURN NEW;
END
  $$;


ALTER FUNCTION public."FileReportDelete"() OWNER TO seqware;

--
-- Name: FileReportInsert(); Type: FUNCTION; Schema: public; Owner: seqware
--

CREATE FUNCTION "FileReportInsert"() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
  
  $$;


ALTER FUNCTION public."FileReportInsert"() OWNER TO seqware;

--
-- Name: SampleReportDelete(); Type: FUNCTION; Schema: public; Owner: seqware
--

CREATE FUNCTION "SampleReportDelete"() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE 
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
END;$$;


ALTER FUNCTION public."SampleReportDelete"() OWNER TO seqware;

--
-- Name: SampleReportUpdate(); Type: FUNCTION; Schema: public; Owner: seqware
--

CREATE FUNCTION "SampleReportUpdate"() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE 
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
END;$$;


ALTER FUNCTION public."SampleReportUpdate"() OWNER TO seqware;

--
-- Name: current_status_new(integer, integer); Type: FUNCTION; Schema: public; Owner: seqware
--

CREATE FUNCTION current_status_new(_ius_id integer, _workflow_id integer) RETURNS character varying
    LANGUAGE plpgsql
    AS $_$DECLARE
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
END;$_$;


ALTER FUNCTION public.current_status_new(_ius_id integer, _workflow_id integer) OWNER TO seqware;

--
-- Name: fill_file_report(); Type: FUNCTION; Schema: public; Owner: seqware
--

CREATE FUNCTION fill_file_report() RETURNS boolean
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION public.fill_file_report() OWNER TO seqware;

--
-- Name: fill_sample_report(); Type: FUNCTION; Schema: public; Owner: seqware
--

CREATE FUNCTION fill_sample_report() RETURNS boolean
    LANGUAGE plpgsql
    AS $$
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
END;$$;


ALTER FUNCTION public.fill_sample_report() OWNER TO seqware;

--
-- Name: sw_accession_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE sw_accession_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.sw_accession_seq OWNER TO seqware;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: experiment; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE experiment (
    experiment_id integer NOT NULL,
    study_id integer NOT NULL,
    experiment_library_design_id integer,
    experiment_spot_design_id integer,
    platform_id integer,
    name text,
    title text,
    description text,
    alias text,
    accession text,
    status text,
    center_name text,
    sequence_space text,
    base_caller text,
    quality_scorer text,
    quality_number_of_levels integer,
    quality_multiplier integer,
    quality_type text,
    expected_number_runs integer,
    expected_number_spots bigint,
    expected_number_reads bigint,
    owner_id integer,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone,
    CONSTRAINT experiment_quality_type_check CHECK ((quality_type = ANY (ARRAY['phred'::text, 'other'::text]))),
    CONSTRAINT experiment_sequence_space_check CHECK ((sequence_space = ANY (ARRAY['Base Space'::text, 'Color Space'::text])))
);


ALTER TABLE public.experiment OWNER TO seqware;

--
-- Name: experiment_attribute; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE experiment_attribute (
    experiment_attribute_id integer NOT NULL,
    experiment_id integer NOT NULL,
    tag text,
    value text,
    units text
);


ALTER TABLE public.experiment_attribute OWNER TO seqware;

--
-- Name: experiment_attribute_experiment_attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE experiment_attribute_experiment_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.experiment_attribute_experiment_attribute_id_seq OWNER TO seqware;

--
-- Name: experiment_attribute_experiment_attribute_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE experiment_attribute_experiment_attribute_id_seq OWNED BY experiment_attribute.experiment_attribute_id;


--
-- Name: experiment_experiment_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE experiment_experiment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.experiment_experiment_id_seq OWNER TO seqware;

--
-- Name: experiment_experiment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE experiment_experiment_id_seq OWNED BY experiment.experiment_id;


--
-- Name: experiment_library_design; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE experiment_library_design (
    experiment_library_design_id integer NOT NULL,
    name text,
    description text,
    construction_protocol text,
    strategy integer,
    source integer,
    selection integer,
    layout text,
    paired_orientation text,
    nominal_length integer,
    nominal_sdev double precision,
    CONSTRAINT experiment_library_design_layout_check CHECK ((layout = ANY (ARRAY['paired'::text, 'single'::text])))
);


ALTER TABLE public.experiment_library_design OWNER TO seqware;

--
-- Name: experiment_library_design_experiment_library_design_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE experiment_library_design_experiment_library_design_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.experiment_library_design_experiment_library_design_id_seq OWNER TO seqware;

--
-- Name: experiment_library_design_experiment_library_design_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE experiment_library_design_experiment_library_design_id_seq OWNED BY experiment_library_design.experiment_library_design_id;


--
-- Name: experiment_link; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE experiment_link (
    experiment_link_id integer NOT NULL,
    experiment_id integer NOT NULL,
    label text,
    url text,
    db text,
    id text
);


ALTER TABLE public.experiment_link OWNER TO seqware;

--
-- Name: experiment_link_experiment_link_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE experiment_link_experiment_link_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.experiment_link_experiment_link_id_seq OWNER TO seqware;

--
-- Name: experiment_link_experiment_link_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE experiment_link_experiment_link_id_seq OWNED BY experiment_link.experiment_link_id;


--
-- Name: experiment_spot_design; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE experiment_spot_design (
    experiment_spot_design_id integer NOT NULL,
    decode_method integer,
    reads_per_spot integer,
    read_spec text,
    tag_spec text,
    adapter_spec text
);


ALTER TABLE public.experiment_spot_design OWNER TO seqware;

--
-- Name: experiment_spot_design_experiment_spot_design_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE experiment_spot_design_experiment_spot_design_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.experiment_spot_design_experiment_spot_design_id_seq OWNER TO seqware;

--
-- Name: experiment_spot_design_experiment_spot_design_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE experiment_spot_design_experiment_spot_design_id_seq OWNED BY experiment_spot_design.experiment_spot_design_id;


--
-- Name: experiment_spot_design_read_spec; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE experiment_spot_design_read_spec (
    experiment_spot_design_read_spec_id integer NOT NULL,
    experiment_spot_design_id integer,
    read_index integer,
    read_label text,
    read_class text,
    read_type text,
    base_coord integer,
    cycle_coord integer,
    length integer,
    expected_basecall text,
    CONSTRAINT read_class_ck CHECK ((read_class = ANY (ARRAY[('Technical Read'::character varying)::text, ('Application Read'::character varying)::text]))),
    CONSTRAINT read_type_ck CHECK ((read_type = ANY (ARRAY[('Forward'::character varying)::text, ('Reverse'::character varying)::text, ('Adapter'::character varying)::text, ('Primer'::character varying)::text, ('Linker'::character varying)::text, ('BarCode'::character varying)::text, ('Other'::character varying)::text])))
);


ALTER TABLE public.experiment_spot_design_read_spec OWNER TO seqware;

--
-- Name: experiment_spot_design_read_s_experiment_spot_design_read_s_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE experiment_spot_design_read_s_experiment_spot_design_read_s_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.experiment_spot_design_read_s_experiment_spot_design_read_s_seq OWNER TO seqware;

--
-- Name: experiment_spot_design_read_s_experiment_spot_design_read_s_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE experiment_spot_design_read_s_experiment_spot_design_read_s_seq OWNED BY experiment_spot_design_read_spec.experiment_spot_design_read_spec_id;


--
-- Name: file; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE file (
    file_id integer NOT NULL,
    file_path text NOT NULL,
    md5sum text,
    url text,
    url_label text,
    type text,
    meta_type text,
    description text,
    owner_id integer,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    file_type_id integer,
    size bigint
);


ALTER TABLE public.file OWNER TO seqware;

--
-- Name: file_attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE file_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.file_attribute_id_seq OWNER TO seqware;

--
-- Name: file_attribute; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE file_attribute (
    file_attribute_id integer DEFAULT nextval('file_attribute_id_seq'::regclass) NOT NULL,
    file_id integer NOT NULL,
    tag character varying(255) NOT NULL,
    value character varying(255) NOT NULL,
    unit character varying(255)
);


ALTER TABLE public.file_attribute OWNER TO seqware;

--
-- Name: file_file_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE file_file_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.file_file_id_seq OWNER TO seqware;

--
-- Name: file_file_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE file_file_id_seq OWNED BY file.file_id;


--
-- Name: file_report; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE file_report (
    row_id integer NOT NULL,
    study_id integer,
    ius_id integer,
    lane_id integer,
    file_id integer,
    sample_id integer,
    experiment_id integer,
    child_sample_id integer,
    processing_id integer
);


ALTER TABLE public.file_report OWNER TO seqware;

--
-- Name: file_report_row_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE file_report_row_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.file_report_row_id_seq OWNER TO seqware;

--
-- Name: file_report_row_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE file_report_row_id_seq OWNED BY file_report.row_id;


--
-- Name: file_type; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE file_type (
    file_type_id integer NOT NULL,
    display_name text,
    meta_type text,
    extension text
);


ALTER TABLE public.file_type OWNER TO seqware;

--
-- Name: file_type_file_type_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE file_type_file_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.file_type_file_type_id_seq OWNER TO seqware;

--
-- Name: file_type_file_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE file_type_file_type_id_seq OWNED BY file_type.file_type_id;


--
-- Name: ius; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE ius (
    ius_id integer NOT NULL,
    sample_id integer NOT NULL,
    lane_id integer NOT NULL,
    owner_id integer,
    name text,
    alias text,
    description text,
    tag text,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone,
    skip boolean DEFAULT false
);


ALTER TABLE public.ius OWNER TO seqware;

--
-- Name: ius_attribute; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE ius_attribute (
    ius_attribute_id integer NOT NULL,
    ius_id integer NOT NULL,
    tag text,
    value text,
    units text
);


ALTER TABLE public.ius_attribute OWNER TO seqware;

--
-- Name: ius_attribute_ius_attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE ius_attribute_ius_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.ius_attribute_ius_attribute_id_seq OWNER TO seqware;

--
-- Name: ius_attribute_ius_attribute_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE ius_attribute_ius_attribute_id_seq OWNED BY ius_attribute.ius_attribute_id;


--
-- Name: ius_ius_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE ius_ius_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.ius_ius_id_seq OWNER TO seqware;

--
-- Name: ius_ius_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE ius_ius_id_seq OWNED BY ius.ius_id;


--
-- Name: ius_link; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE ius_link (
    ius_link_id integer NOT NULL,
    ius_id integer NOT NULL,
    label text NOT NULL,
    url text NOT NULL,
    db text,
    id text
);


ALTER TABLE public.ius_link OWNER TO seqware;

--
-- Name: ius_link_ius_link_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE ius_link_ius_link_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.ius_link_ius_link_id_seq OWNER TO seqware;

--
-- Name: ius_link_ius_link_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE ius_link_ius_link_id_seq OWNED BY ius_link.ius_link_id;


--
-- Name: ius_workflow_runs; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE ius_workflow_runs (
    ius_workflow_runs_id integer NOT NULL,
    ius_id integer NOT NULL,
    workflow_run_id integer NOT NULL
);


ALTER TABLE public.ius_workflow_runs OWNER TO seqware;

--
-- Name: ius_workflow_runs_ius_workflow_runs_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE ius_workflow_runs_ius_workflow_runs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.ius_workflow_runs_ius_workflow_runs_id_seq OWNER TO seqware;

--
-- Name: ius_workflow_runs_ius_workflow_runs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE ius_workflow_runs_ius_workflow_runs_id_seq OWNED BY ius_workflow_runs.ius_workflow_runs_id;


--
-- Name: lane; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE lane (
    lane_id integer NOT NULL,
    sequencer_run_id integer,
    sample_id integer,
    organism_id integer,
    name text,
    alias text,
    description text,
    lane_index integer,
    cycle_descriptor text,
    cycle_count integer,
    cycle_sequence text,
    type integer,
    study_type integer,
    library_strategy integer,
    library_selection integer,
    library_source integer,
    skip boolean DEFAULT false,
    tags text,
    regions text,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    owner_id integer,
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone
);


ALTER TABLE public.lane OWNER TO seqware;

--
-- Name: lane_attribute; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE lane_attribute (
    lane_attribute_id integer NOT NULL,
    lane_id integer NOT NULL,
    tag text,
    value text,
    units text
);


ALTER TABLE public.lane_attribute OWNER TO seqware;

--
-- Name: lane_attribute_lane_attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE lane_attribute_lane_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.lane_attribute_lane_attribute_id_seq OWNER TO seqware;

--
-- Name: lane_attribute_lane_attribute_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE lane_attribute_lane_attribute_id_seq OWNED BY lane_attribute.lane_attribute_id;


--
-- Name: lane_lane_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE lane_lane_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.lane_lane_id_seq OWNER TO seqware;

--
-- Name: lane_lane_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE lane_lane_id_seq OWNED BY lane.lane_id;


--
-- Name: lane_link; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE lane_link (
    lane_link_id integer NOT NULL,
    lane_id integer NOT NULL,
    label text NOT NULL,
    url text NOT NULL,
    db text,
    id text
);


ALTER TABLE public.lane_link OWNER TO seqware;

--
-- Name: lane_link_lane_link_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE lane_link_lane_link_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.lane_link_lane_link_id_seq OWNER TO seqware;

--
-- Name: lane_link_lane_link_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE lane_link_lane_link_id_seq OWNED BY lane_link.lane_link_id;


--
-- Name: lane_type; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE lane_type (
    lane_type_id integer NOT NULL,
    code text,
    name text
);


ALTER TABLE public.lane_type OWNER TO seqware;

--
-- Name: lane_type_lane_type_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE lane_type_lane_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.lane_type_lane_type_id_seq OWNER TO seqware;

--
-- Name: lane_type_lane_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE lane_type_lane_type_id_seq OWNED BY lane_type.lane_type_id;


--
-- Name: lane_workflow_runs; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE lane_workflow_runs (
    lane_workflow_runs_id integer NOT NULL,
    lane_id integer NOT NULL,
    workflow_run_id integer NOT NULL
);


ALTER TABLE public.lane_workflow_runs OWNER TO seqware;

--
-- Name: lane_workflow_runs_lane_workflow_runs_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE lane_workflow_runs_lane_workflow_runs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.lane_workflow_runs_lane_workflow_runs_id_seq OWNER TO seqware;

--
-- Name: lane_workflow_runs_lane_workflow_runs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE lane_workflow_runs_lane_workflow_runs_id_seq OWNED BY lane_workflow_runs.lane_workflow_runs_id;


--
-- Name: library_selection; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE library_selection (
    library_selection_id integer NOT NULL,
    name text,
    description text
);


ALTER TABLE public.library_selection OWNER TO seqware;

--
-- Name: library_selection_library_selection_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE library_selection_library_selection_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.library_selection_library_selection_id_seq OWNER TO seqware;

--
-- Name: library_selection_library_selection_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE library_selection_library_selection_id_seq OWNED BY library_selection.library_selection_id;


--
-- Name: library_source; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE library_source (
    library_source_id integer NOT NULL,
    name text,
    description text
);


ALTER TABLE public.library_source OWNER TO seqware;

--
-- Name: library_source_library_source_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE library_source_library_source_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.library_source_library_source_id_seq OWNER TO seqware;

--
-- Name: library_source_library_source_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE library_source_library_source_id_seq OWNED BY library_source.library_source_id;


--
-- Name: library_strategy; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE library_strategy (
    library_strategy_id integer NOT NULL,
    name text,
    description text
);


ALTER TABLE public.library_strategy OWNER TO seqware;

--
-- Name: library_strategy_library_strategy_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE library_strategy_library_strategy_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.library_strategy_library_strategy_id_seq OWNER TO seqware;

--
-- Name: library_strategy_library_strategy_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE library_strategy_library_strategy_id_seq OWNED BY library_strategy.library_strategy_id;


--
-- Name: organism; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE organism (
    organism_id integer NOT NULL,
    code text NOT NULL,
    name text,
    accession text,
    ncbi_taxid integer
);


ALTER TABLE public.organism OWNER TO seqware;

--
-- Name: organism_organism_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE organism_organism_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.organism_organism_id_seq OWNER TO seqware;

--
-- Name: organism_organism_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE organism_organism_id_seq OWNED BY organism.organism_id;


--
-- Name: platform; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE platform (
    platform_id integer NOT NULL,
    name text,
    instrument_model text,
    description text
);


ALTER TABLE public.platform OWNER TO seqware;

--
-- Name: platform_platform_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE platform_platform_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.platform_platform_id_seq OWNER TO seqware;

--
-- Name: platform_platform_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE platform_platform_id_seq OWNED BY platform.platform_id;


--
-- Name: processing; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE processing (
    processing_id integer NOT NULL,
    workflow_run_id integer,
    ancestor_workflow_run_id integer,
    algorithm text,
    status text,
    description text,
    url text,
    url_label text,
    version text,
    parameters text,
    stdout text,
    stderr text,
    exit_status integer,
    process_exit_status integer,
    task_group boolean DEFAULT false,
    owner_id integer,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    run_start_tstmp timestamp without time zone,
    run_stop_tstmp timestamp without time zone,
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone
);


ALTER TABLE public.processing OWNER TO seqware;

--
-- Name: processing_attribute; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE processing_attribute (
    processing_attribute_id integer NOT NULL,
    processing_id integer NOT NULL,
    tag text,
    value text,
    units text
);


ALTER TABLE public.processing_attribute OWNER TO seqware;

--
-- Name: processing_attribute_processing_attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE processing_attribute_processing_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.processing_attribute_processing_attribute_id_seq OWNER TO seqware;

--
-- Name: processing_attribute_processing_attribute_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE processing_attribute_processing_attribute_id_seq OWNED BY processing_attribute.processing_attribute_id;


--
-- Name: processing_experiments; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE processing_experiments (
    processing_experiments_id integer NOT NULL,
    experiment_id integer NOT NULL,
    processing_id integer NOT NULL,
    description text,
    label text,
    url text,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass)
);


ALTER TABLE public.processing_experiments OWNER TO seqware;

--
-- Name: processing_experiments_processing_experiments_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE processing_experiments_processing_experiments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.processing_experiments_processing_experiments_id_seq OWNER TO seqware;

--
-- Name: processing_experiments_processing_experiments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE processing_experiments_processing_experiments_id_seq OWNED BY processing_experiments.processing_experiments_id;


--
-- Name: processing_files; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE processing_files (
    processing_files_id integer NOT NULL,
    processing_id integer NOT NULL,
    file_id integer NOT NULL
);


ALTER TABLE public.processing_files OWNER TO seqware;

--
-- Name: processing_files_processing_files_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE processing_files_processing_files_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.processing_files_processing_files_id_seq OWNER TO seqware;

--
-- Name: processing_files_processing_files_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE processing_files_processing_files_id_seq OWNED BY processing_files.processing_files_id;


--
-- Name: processing_ius; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE processing_ius (
    processing_ius_id integer NOT NULL,
    ius_id integer NOT NULL,
    processing_id integer NOT NULL,
    description text,
    label text,
    url text,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass)
);


ALTER TABLE public.processing_ius OWNER TO seqware;

--
-- Name: processing_ius_processing_ius_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE processing_ius_processing_ius_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.processing_ius_processing_ius_id_seq OWNER TO seqware;

--
-- Name: processing_ius_processing_ius_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE processing_ius_processing_ius_id_seq OWNED BY processing_ius.processing_ius_id;


--
-- Name: processing_lanes; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE processing_lanes (
    processing_lanes_id integer NOT NULL,
    lane_id integer NOT NULL,
    processing_id integer NOT NULL,
    description text,
    label text,
    url text,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass)
);


ALTER TABLE public.processing_lanes OWNER TO seqware;

--
-- Name: processing_lanes_processing_lanes_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE processing_lanes_processing_lanes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.processing_lanes_processing_lanes_id_seq OWNER TO seqware;

--
-- Name: processing_lanes_processing_lanes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE processing_lanes_processing_lanes_id_seq OWNED BY processing_lanes.processing_lanes_id;


--
-- Name: processing_processing_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE processing_processing_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.processing_processing_id_seq OWNER TO seqware;

--
-- Name: processing_processing_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE processing_processing_id_seq OWNED BY processing.processing_id;


--
-- Name: processing_relationship; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE processing_relationship (
    processing_relationship_id integer NOT NULL,
    parent_id integer,
    child_id integer,
    relationship text
);


ALTER TABLE public.processing_relationship OWNER TO seqware;

--
-- Name: processing_relationship_processing_relationship_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE processing_relationship_processing_relationship_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.processing_relationship_processing_relationship_id_seq OWNER TO seqware;

--
-- Name: processing_relationship_processing_relationship_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE processing_relationship_processing_relationship_id_seq OWNED BY processing_relationship.processing_relationship_id;


--
-- Name: processing_samples; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE processing_samples (
    processing_samples_id integer NOT NULL,
    sample_id integer NOT NULL,
    processing_id integer NOT NULL,
    description text,
    label text,
    url text,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass)
);


ALTER TABLE public.processing_samples OWNER TO seqware;

--
-- Name: processing_samples_processing_samples_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE processing_samples_processing_samples_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.processing_samples_processing_samples_id_seq OWNER TO seqware;

--
-- Name: processing_samples_processing_samples_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE processing_samples_processing_samples_id_seq OWNED BY processing_samples.processing_samples_id;


--
-- Name: processing_sequencer_runs; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE processing_sequencer_runs (
    processing_sequencer_runs_id integer NOT NULL,
    sequencer_run_id integer NOT NULL,
    processing_id integer NOT NULL,
    description text,
    label text,
    url text,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass)
);


ALTER TABLE public.processing_sequencer_runs OWNER TO seqware;

--
-- Name: processing_sequencer_runs_processing_sequencer_runs_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE processing_sequencer_runs_processing_sequencer_runs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.processing_sequencer_runs_processing_sequencer_runs_id_seq OWNER TO seqware;

--
-- Name: processing_sequencer_runs_processing_sequencer_runs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE processing_sequencer_runs_processing_sequencer_runs_id_seq OWNED BY processing_sequencer_runs.processing_sequencer_runs_id;


--
-- Name: processing_studies; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE processing_studies (
    processing_studies_id integer NOT NULL,
    study_id integer NOT NULL,
    processing_id integer NOT NULL,
    description text,
    label text,
    url text,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass)
);


ALTER TABLE public.processing_studies OWNER TO seqware;

--
-- Name: processing_studies_processing_studies_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE processing_studies_processing_studies_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.processing_studies_processing_studies_id_seq OWNER TO seqware;

--
-- Name: processing_studies_processing_studies_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE processing_studies_processing_studies_id_seq OWNED BY processing_studies.processing_studies_id;


--
-- Name: registration; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE registration (
    registration_id integer NOT NULL,
    email text NOT NULL,
    password text,
    password_hint text,
    first_name text NOT NULL,
    last_name text NOT NULL,
    institution text,
    invitation_code text,
    lims_admin boolean DEFAULT false NOT NULL,
    payee boolean DEFAULT false NOT NULL,
    create_tstmp timestamp without time zone NOT NULL,
    last_update_tstmp timestamp without time zone NOT NULL,
    developer_ml boolean DEFAULT false NOT NULL,
    user_ml boolean DEFAULT false NOT NULL
);


ALTER TABLE public.registration OWNER TO seqware;

--
-- Name: registration_registration_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE registration_registration_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.registration_registration_id_seq OWNER TO seqware;

--
-- Name: registration_registration_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE registration_registration_id_seq OWNED BY registration.registration_id;


--
-- Name: sample; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE sample (
    sample_id integer NOT NULL,
    experiment_id integer,
    organism_id integer,
    name text,
    title text,
    alias text,
    type text,
    scientific_name text,
    common_name text,
    anonymized_name text,
    individual_name text,
    description text,
    taxon_id integer,
    tags text,
    adapters text,
    regions text,
    expected_number_runs integer,
    expected_number_spots integer,
    expected_number_reads integer,
    skip boolean,
    is_public boolean,
    owner_id integer,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone
);


ALTER TABLE public.sample OWNER TO seqware;

--
-- Name: sample_attribute; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE sample_attribute (
    sample_attribute_id integer NOT NULL,
    sample_id integer NOT NULL,
    tag text NOT NULL,
    value text NOT NULL,
    units text
);


ALTER TABLE public.sample_attribute OWNER TO seqware;

--
-- Name: sample_attribute_sample_attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE sample_attribute_sample_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.sample_attribute_sample_attribute_id_seq OWNER TO seqware;

--
-- Name: sample_attribute_sample_attribute_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE sample_attribute_sample_attribute_id_seq OWNED BY sample_attribute.sample_attribute_id;


--
-- Name: sample_hierarchy; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE sample_hierarchy (
    sample_id integer NOT NULL,
    parent_id integer
);


ALTER TABLE public.sample_hierarchy OWNER TO seqware;

--
-- Name: TABLE sample_hierarchy; Type: COMMENT; Schema: public; Owner: seqware
--

COMMENT ON TABLE sample_hierarchy IS 'Relationship of samples as they pass through the wet lab.';


--
-- Name: sample_link; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE sample_link (
    sample_link_id integer NOT NULL,
    sample_id integer NOT NULL,
    label text NOT NULL,
    url text NOT NULL,
    db text,
    id text
);


ALTER TABLE public.sample_link OWNER TO seqware;

--
-- Name: sample_link_sample_link_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE sample_link_sample_link_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.sample_link_sample_link_id_seq OWNER TO seqware;

--
-- Name: sample_link_sample_link_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE sample_link_sample_link_id_seq OWNED BY sample_link.sample_link_id;


--
-- Name: sample_relationship; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE sample_relationship (
    sample_relationship_id integer NOT NULL,
    parent_id integer,
    child_id integer,
    relationship text
);


ALTER TABLE public.sample_relationship OWNER TO seqware;

--
-- Name: sample_relationship_sample_relationship_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE sample_relationship_sample_relationship_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.sample_relationship_sample_relationship_id_seq OWNER TO seqware;

--
-- Name: sample_relationship_sample_relationship_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE sample_relationship_sample_relationship_id_seq OWNED BY sample_relationship.sample_relationship_id;


--
-- Name: sample_report; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE sample_report (
    study_id integer,
    child_sample_id integer,
    workflow_id integer,
    status character varying(255),
    sequencer_run_id integer,
    lane_id integer,
    ius_id integer,
    row_id integer NOT NULL
);


ALTER TABLE public.sample_report OWNER TO seqware;

--
-- Name: sample_report_row_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE sample_report_row_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.sample_report_row_id_seq OWNER TO seqware;

--
-- Name: sample_report_row_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE sample_report_row_id_seq OWNED BY sample_report.row_id;


--
-- Name: sample_sample_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE sample_sample_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.sample_sample_id_seq OWNER TO seqware;

--
-- Name: sample_sample_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE sample_sample_id_seq OWNED BY sample.sample_id;


--
-- Name: sample_search_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE sample_search_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.sample_search_id_seq OWNER TO seqware;

--
-- Name: sample_search; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE sample_search (
    sample_search_id integer DEFAULT nextval('sample_search_id_seq'::regclass) NOT NULL,
    sample_id integer NOT NULL,
    create_tstmp timestamp without time zone NOT NULL
);


ALTER TABLE public.sample_search OWNER TO seqware;

--
-- Name: sample_search_attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE sample_search_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.sample_search_attribute_id_seq OWNER TO seqware;

--
-- Name: sample_search_attribute; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE sample_search_attribute (
    sample_search_attribute_id integer DEFAULT nextval('sample_search_attribute_id_seq'::regclass) NOT NULL,
    sample_search_id integer NOT NULL,
    tag character varying(255) NOT NULL,
    value character varying(255) NOT NULL
);


ALTER TABLE public.sample_search_attribute OWNER TO seqware;

--
-- Name: sequencer_run; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE sequencer_run (
    sequencer_run_id integer NOT NULL,
    name text,
    description text,
    status text,
    platform_id integer,
    instrument_name text,
    cycle_descriptor text,
    cycle_count integer,
    cycle_sequence text,
    file_path text,
    paired_end boolean,
    process boolean,
    ref_lane integer,
    paired_file_path text,
    use_ipar_intensities boolean,
    color_matrix text,
    color_matrix_code text,
    slide_count integer,
    slide_1_lane_count integer,
    slide_1_file_path text,
    slide_2_lane_count integer,
    slide_2_file_path text,
    flow_sequence text,
    flow_count integer,
    owner_id integer,
    run_center text,
    base_caller text,
    quality_scorer text,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone,
    skip boolean DEFAULT false
);


ALTER TABLE public.sequencer_run OWNER TO seqware;

--
-- Name: sequencer_run_attribute; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE sequencer_run_attribute (
    sequencer_run_attribute_id integer NOT NULL,
    sample_id integer NOT NULL,
    tag text NOT NULL,
    value text NOT NULL,
    units text
);


ALTER TABLE public.sequencer_run_attribute OWNER TO seqware;

--
-- Name: sequencer_run_attribute_sequencer_run_attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE sequencer_run_attribute_sequencer_run_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.sequencer_run_attribute_sequencer_run_attribute_id_seq OWNER TO seqware;

--
-- Name: sequencer_run_attribute_sequencer_run_attribute_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE sequencer_run_attribute_sequencer_run_attribute_id_seq OWNED BY sequencer_run_attribute.sequencer_run_attribute_id;


--
-- Name: sequencer_run_sequencer_run_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE sequencer_run_sequencer_run_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.sequencer_run_sequencer_run_id_seq OWNER TO seqware;

--
-- Name: sequencer_run_sequencer_run_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE sequencer_run_sequencer_run_id_seq OWNED BY sequencer_run.sequencer_run_id;


--
-- Name: share_experiment; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE share_experiment (
    share_experiment_id integer NOT NULL,
    experiment_id integer NOT NULL,
    registration_id integer NOT NULL,
    active boolean DEFAULT true,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone
);


ALTER TABLE public.share_experiment OWNER TO seqware;

--
-- Name: share_file; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE share_file (
    file_id integer NOT NULL,
    registration_id integer NOT NULL,
    active boolean DEFAULT true,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone,
    share_file_id integer NOT NULL
);


ALTER TABLE public.share_file OWNER TO seqware;

--
-- Name: share_lane; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE share_lane (
    lane_id integer NOT NULL,
    registration_id integer NOT NULL,
    active boolean DEFAULT true,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone,
    share_lane_id integer NOT NULL
);


ALTER TABLE public.share_lane OWNER TO seqware;

--
-- Name: share_processing; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE share_processing (
    processing_id integer NOT NULL,
    registration_id integer NOT NULL,
    active boolean DEFAULT true,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone,
    share_processing_id integer NOT NULL
);


ALTER TABLE public.share_processing OWNER TO seqware;

--
-- Name: share_sample; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE share_sample (
    sample_id integer NOT NULL,
    registration_id integer NOT NULL,
    active boolean DEFAULT true,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone,
    share_sample_id integer NOT NULL
);


ALTER TABLE public.share_sample OWNER TO seqware;

--
-- Name: share_study; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE share_study (
    share_study_id integer NOT NULL,
    study_id integer NOT NULL,
    registration_id integer NOT NULL,
    active boolean DEFAULT true,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone
);


ALTER TABLE public.share_study OWNER TO seqware;

--
-- Name: share_study_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE share_study_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.share_study_id_seq OWNER TO seqware;

--
-- Name: share_study_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE share_study_id_seq OWNED BY share_study.share_study_id;


--
-- Name: share_workflow_run; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE share_workflow_run (
    share_workflow_run_id integer NOT NULL,
    workflow_run_id integer NOT NULL,
    registration_id integer NOT NULL,
    active boolean DEFAULT true,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone
);


ALTER TABLE public.share_workflow_run OWNER TO seqware;

--
-- Name: share_workflow_run_share_workflow_run_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE share_workflow_run_share_workflow_run_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.share_workflow_run_share_workflow_run_id_seq OWNER TO seqware;

--
-- Name: share_workflow_run_share_workflow_run_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE share_workflow_run_share_workflow_run_id_seq OWNED BY share_workflow_run.share_workflow_run_id;


--
-- Name: study; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE study (
    study_id integer NOT NULL,
    title text NOT NULL,
    alias text,
    description text,
    accession text,
    abstract text,
    existing_type integer NOT NULL,
    new_type text,
    center_name text NOT NULL,
    center_project_name text NOT NULL,
    project_id integer DEFAULT 0 NOT NULL,
    status text,
    owner_id integer,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone
);


ALTER TABLE public.study OWNER TO seqware;

--
-- Name: study_attribute; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE study_attribute (
    study_attribute_id integer NOT NULL,
    study_id integer NOT NULL,
    tag text NOT NULL,
    value text NOT NULL,
    units text
);


ALTER TABLE public.study_attribute OWNER TO seqware;

--
-- Name: study_attribute_study_attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE study_attribute_study_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.study_attribute_study_attribute_id_seq OWNER TO seqware;

--
-- Name: study_attribute_study_attribute_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE study_attribute_study_attribute_id_seq OWNED BY study_attribute.study_attribute_id;


--
-- Name: study_link; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE study_link (
    study_link_id integer NOT NULL,
    study_id integer NOT NULL,
    label text NOT NULL,
    url text NOT NULL,
    db text,
    id text
);


ALTER TABLE public.study_link OWNER TO seqware;

--
-- Name: study_link_study_link_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE study_link_study_link_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.study_link_study_link_id_seq OWNER TO seqware;

--
-- Name: study_link_study_link_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE study_link_study_link_id_seq OWNED BY study_link.study_link_id;


--
-- Name: study_study_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE study_study_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.study_study_id_seq OWNER TO seqware;

--
-- Name: study_study_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE study_study_id_seq OWNED BY study.study_id;


--
-- Name: study_type; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE study_type (
    study_type_id integer NOT NULL,
    name text NOT NULL,
    description text
);


ALTER TABLE public.study_type OWNER TO seqware;

--
-- Name: study_type_study_type_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE study_type_study_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.study_type_study_type_id_seq OWNER TO seqware;

--
-- Name: study_type_study_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE study_type_study_type_id_seq OWNED BY study_type.study_type_id;


--
-- Name: version; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE version (
    version_id integer NOT NULL,
    name text,
    major integer,
    minor integer,
    bugfix integer,
    type character varying(100),
    CONSTRAINT lims_version_release_type_ck CHECK (((type)::text = ANY (ARRAY[(''::character varying)::text, ('rc'::character varying)::text, ('alpha'::character varying)::text, ('beta'::character varying)::text, ('debug'::character varying)::text])))
);


ALTER TABLE public.version OWNER TO seqware;

--
-- Name: version_version_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE version_version_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.version_version_id_seq OWNER TO seqware;

--
-- Name: version_version_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE version_version_id_seq OWNED BY version.version_id;


--
-- Name: workflow; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE workflow (
    workflow_id integer NOT NULL,
    name text,
    description text,
    input_algorithm text,
    version text,
    seqware_version text,
    owner_id integer,
    base_ini_file text,
    cmd text,
    current_working_dir text,
    host text,
    username text,
    workflow_template text,
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    permanent_bundle_location text
);


ALTER TABLE public.workflow OWNER TO seqware;

--
-- Name: workflow_attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE workflow_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.workflow_attribute_id_seq OWNER TO seqware;

--
-- Name: workflow_attribute; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE workflow_attribute (
    workflow_attribute_id integer DEFAULT nextval('workflow_attribute_id_seq'::regclass) NOT NULL,
    workflow_id integer NOT NULL,
    tag character varying(255) NOT NULL,
    value character varying(255) NOT NULL,
    unit character varying(255)
);


ALTER TABLE public.workflow_attribute OWNER TO seqware;

--
-- Name: workflow_param; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE workflow_param (
    workflow_param_id integer NOT NULL,
    workflow_id integer NOT NULL,
    type text NOT NULL,
    key text NOT NULL,
    display boolean,
    display_name text NOT NULL,
    file_meta_type text,
    default_value text
);


ALTER TABLE public.workflow_param OWNER TO seqware;

--
-- Name: workflow_param_value; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE workflow_param_value (
    workflow_param_value_id integer NOT NULL,
    workflow_param_id integer NOT NULL,
    display_name text NOT NULL,
    value text
);


ALTER TABLE public.workflow_param_value OWNER TO seqware;

--
-- Name: workflow_param_value_workflow_param_value_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE workflow_param_value_workflow_param_value_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.workflow_param_value_workflow_param_value_id_seq OWNER TO seqware;

--
-- Name: workflow_param_value_workflow_param_value_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE workflow_param_value_workflow_param_value_id_seq OWNED BY workflow_param_value.workflow_param_value_id;


--
-- Name: workflow_param_workflow_param_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE workflow_param_workflow_param_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.workflow_param_workflow_param_id_seq OWNER TO seqware;

--
-- Name: workflow_param_workflow_param_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE workflow_param_workflow_param_id_seq OWNED BY workflow_param.workflow_param_id;


--
-- Name: workflow_run; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE workflow_run (
    workflow_run_id integer NOT NULL,
    workflow_id integer NOT NULL,
    owner_id integer,
    name text,
    ini_file text,
    cmd text,
    workflow_template text,
    dax text,
    status text,
    status_cmd text,
    seqware_revision text,
    host text,
    current_working_dir text,
    username text,
    stderr text,
    stdout text,
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass)
);


ALTER TABLE public.workflow_run OWNER TO seqware;

--
-- Name: workflow_run_attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE workflow_run_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.workflow_run_attribute_id_seq OWNER TO seqware;

--
-- Name: workflow_run_attribute; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE workflow_run_attribute (
    workflow_run_attribute_id integer DEFAULT nextval('workflow_run_attribute_id_seq'::regclass) NOT NULL,
    workflow_run_id integer NOT NULL,
    tag character varying(255) NOT NULL,
    value character varying(255) NOT NULL,
    unit character varying(255)
);


ALTER TABLE public.workflow_run_attribute OWNER TO seqware;

--
-- Name: workflow_run_param; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE workflow_run_param (
    workflow_run_param_id integer NOT NULL,
    workflow_run_id integer NOT NULL,
    type text,
    key text,
    parent_processing_accession integer,
    value text
);


ALTER TABLE public.workflow_run_param OWNER TO seqware;

--
-- Name: workflow_run_param_workflow_run_param_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE workflow_run_param_workflow_run_param_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.workflow_run_param_workflow_run_param_id_seq OWNER TO seqware;

--
-- Name: workflow_run_param_workflow_run_param_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE workflow_run_param_workflow_run_param_id_seq OWNED BY workflow_run_param.workflow_run_param_id;


--
-- Name: workflow_run_workflow_run_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE workflow_run_workflow_run_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.workflow_run_workflow_run_id_seq OWNER TO seqware;

--
-- Name: workflow_run_workflow_run_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE workflow_run_workflow_run_id_seq OWNED BY workflow_run.workflow_run_id;


--
-- Name: workflow_workflow_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE workflow_workflow_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.workflow_workflow_id_seq OWNER TO seqware;

--
-- Name: workflow_workflow_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: seqware
--

ALTER SEQUENCE workflow_workflow_id_seq OWNED BY workflow.workflow_id;


--
-- Name: experiment_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment ALTER COLUMN experiment_id SET DEFAULT nextval('experiment_experiment_id_seq'::regclass);


--
-- Name: experiment_attribute_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment_attribute ALTER COLUMN experiment_attribute_id SET DEFAULT nextval('experiment_attribute_experiment_attribute_id_seq'::regclass);


--
-- Name: experiment_library_design_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment_library_design ALTER COLUMN experiment_library_design_id SET DEFAULT nextval('experiment_library_design_experiment_library_design_id_seq'::regclass);


--
-- Name: experiment_link_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment_link ALTER COLUMN experiment_link_id SET DEFAULT nextval('experiment_link_experiment_link_id_seq'::regclass);


--
-- Name: experiment_spot_design_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment_spot_design ALTER COLUMN experiment_spot_design_id SET DEFAULT nextval('experiment_spot_design_experiment_spot_design_id_seq'::regclass);


--
-- Name: experiment_spot_design_read_spec_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment_spot_design_read_spec ALTER COLUMN experiment_spot_design_read_spec_id SET DEFAULT nextval('experiment_spot_design_read_s_experiment_spot_design_read_s_seq'::regclass);


--
-- Name: file_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY file ALTER COLUMN file_id SET DEFAULT nextval('file_file_id_seq'::regclass);


--
-- Name: row_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY file_report ALTER COLUMN row_id SET DEFAULT nextval('file_report_row_id_seq'::regclass);


--
-- Name: file_type_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY file_type ALTER COLUMN file_type_id SET DEFAULT nextval('file_type_file_type_id_seq'::regclass);


--
-- Name: ius_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY ius ALTER COLUMN ius_id SET DEFAULT nextval('ius_ius_id_seq'::regclass);


--
-- Name: ius_attribute_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY ius_attribute ALTER COLUMN ius_attribute_id SET DEFAULT nextval('ius_attribute_ius_attribute_id_seq'::regclass);


--
-- Name: ius_workflow_runs_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY ius_workflow_runs ALTER COLUMN ius_workflow_runs_id SET DEFAULT nextval('ius_workflow_runs_ius_workflow_runs_id_seq'::regclass);


--
-- Name: lane_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane ALTER COLUMN lane_id SET DEFAULT nextval('lane_lane_id_seq'::regclass);


--
-- Name: lane_attribute_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane_attribute ALTER COLUMN lane_attribute_id SET DEFAULT nextval('lane_attribute_lane_attribute_id_seq'::regclass);


--
-- Name: lane_type_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane_type ALTER COLUMN lane_type_id SET DEFAULT nextval('lane_type_lane_type_id_seq'::regclass);


--
-- Name: lane_workflow_runs_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane_workflow_runs ALTER COLUMN lane_workflow_runs_id SET DEFAULT nextval('lane_workflow_runs_lane_workflow_runs_id_seq'::regclass);


--
-- Name: library_selection_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY library_selection ALTER COLUMN library_selection_id SET DEFAULT nextval('library_selection_library_selection_id_seq'::regclass);


--
-- Name: library_source_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY library_source ALTER COLUMN library_source_id SET DEFAULT nextval('library_source_library_source_id_seq'::regclass);


--
-- Name: library_strategy_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY library_strategy ALTER COLUMN library_strategy_id SET DEFAULT nextval('library_strategy_library_strategy_id_seq'::regclass);


--
-- Name: organism_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY organism ALTER COLUMN organism_id SET DEFAULT nextval('organism_organism_id_seq'::regclass);


--
-- Name: platform_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY platform ALTER COLUMN platform_id SET DEFAULT nextval('platform_platform_id_seq'::regclass);


--
-- Name: processing_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing ALTER COLUMN processing_id SET DEFAULT nextval('processing_processing_id_seq'::regclass);


--
-- Name: processing_attribute_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_attribute ALTER COLUMN processing_attribute_id SET DEFAULT nextval('processing_attribute_processing_attribute_id_seq'::regclass);


--
-- Name: processing_experiments_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_experiments ALTER COLUMN processing_experiments_id SET DEFAULT nextval('processing_experiments_processing_experiments_id_seq'::regclass);


--
-- Name: processing_files_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_files ALTER COLUMN processing_files_id SET DEFAULT nextval('processing_files_processing_files_id_seq'::regclass);


--
-- Name: processing_ius_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_ius ALTER COLUMN processing_ius_id SET DEFAULT nextval('processing_ius_processing_ius_id_seq'::regclass);


--
-- Name: processing_lanes_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_lanes ALTER COLUMN processing_lanes_id SET DEFAULT nextval('processing_lanes_processing_lanes_id_seq'::regclass);


--
-- Name: processing_relationship_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_relationship ALTER COLUMN processing_relationship_id SET DEFAULT nextval('processing_relationship_processing_relationship_id_seq'::regclass);


--
-- Name: processing_samples_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_samples ALTER COLUMN processing_samples_id SET DEFAULT nextval('processing_samples_processing_samples_id_seq'::regclass);


--
-- Name: processing_sequencer_runs_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_sequencer_runs ALTER COLUMN processing_sequencer_runs_id SET DEFAULT nextval('processing_sequencer_runs_processing_sequencer_runs_id_seq'::regclass);


--
-- Name: processing_studies_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_studies ALTER COLUMN processing_studies_id SET DEFAULT nextval('processing_studies_processing_studies_id_seq'::regclass);


--
-- Name: registration_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY registration ALTER COLUMN registration_id SET DEFAULT nextval('registration_registration_id_seq'::regclass);


--
-- Name: sample_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample ALTER COLUMN sample_id SET DEFAULT nextval('sample_sample_id_seq'::regclass);


--
-- Name: sample_attribute_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample_attribute ALTER COLUMN sample_attribute_id SET DEFAULT nextval('sample_attribute_sample_attribute_id_seq'::regclass);


--
-- Name: sample_link_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample_link ALTER COLUMN sample_link_id SET DEFAULT nextval('sample_link_sample_link_id_seq'::regclass);


--
-- Name: sample_relationship_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample_relationship ALTER COLUMN sample_relationship_id SET DEFAULT nextval('sample_relationship_sample_relationship_id_seq'::regclass);


--
-- Name: row_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample_report ALTER COLUMN row_id SET DEFAULT nextval('sample_report_row_id_seq'::regclass);


--
-- Name: sequencer_run_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sequencer_run ALTER COLUMN sequencer_run_id SET DEFAULT nextval('sequencer_run_sequencer_run_id_seq'::regclass);


--
-- Name: sequencer_run_attribute_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sequencer_run_attribute ALTER COLUMN sequencer_run_attribute_id SET DEFAULT nextval('sequencer_run_attribute_sequencer_run_attribute_id_seq'::regclass);


--
-- Name: share_study_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_study ALTER COLUMN share_study_id SET DEFAULT nextval('share_study_id_seq'::regclass);


--
-- Name: share_workflow_run_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_workflow_run ALTER COLUMN share_workflow_run_id SET DEFAULT nextval('share_workflow_run_share_workflow_run_id_seq'::regclass);


--
-- Name: study_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY study ALTER COLUMN study_id SET DEFAULT nextval('study_study_id_seq'::regclass);


--
-- Name: study_attribute_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY study_attribute ALTER COLUMN study_attribute_id SET DEFAULT nextval('study_attribute_study_attribute_id_seq'::regclass);


--
-- Name: study_link_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY study_link ALTER COLUMN study_link_id SET DEFAULT nextval('study_link_study_link_id_seq'::regclass);


--
-- Name: study_type_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY study_type ALTER COLUMN study_type_id SET DEFAULT nextval('study_type_study_type_id_seq'::regclass);


--
-- Name: version_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY version ALTER COLUMN version_id SET DEFAULT nextval('version_version_id_seq'::regclass);


--
-- Name: workflow_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY workflow ALTER COLUMN workflow_id SET DEFAULT nextval('workflow_workflow_id_seq'::regclass);


--
-- Name: workflow_param_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY workflow_param ALTER COLUMN workflow_param_id SET DEFAULT nextval('workflow_param_workflow_param_id_seq'::regclass);


--
-- Name: workflow_param_value_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY workflow_param_value ALTER COLUMN workflow_param_value_id SET DEFAULT nextval('workflow_param_value_workflow_param_value_id_seq'::regclass);


--
-- Name: workflow_run_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY workflow_run ALTER COLUMN workflow_run_id SET DEFAULT nextval('workflow_run_workflow_run_id_seq'::regclass);


--
-- Name: workflow_run_param_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY workflow_run_param ALTER COLUMN workflow_run_param_id SET DEFAULT nextval('workflow_run_param_workflow_run_param_id_seq'::regclass);


--
-- Name: experiment_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY experiment_attribute
    ADD CONSTRAINT experiment_attribute_pkey PRIMARY KEY (experiment_attribute_id);


--
-- Name: experiment_library_design_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY experiment_library_design
    ADD CONSTRAINT experiment_library_design_pkey PRIMARY KEY (experiment_library_design_id);


--
-- Name: experiment_link_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY experiment_link
    ADD CONSTRAINT experiment_link_pkey PRIMARY KEY (experiment_link_id);


--
-- Name: experiment_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY experiment
    ADD CONSTRAINT experiment_pkey PRIMARY KEY (experiment_id);


--
-- Name: experiment_spot_design_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY experiment_spot_design
    ADD CONSTRAINT experiment_spot_design_pkey PRIMARY KEY (experiment_spot_design_id);


--
-- Name: experiment_spot_design_read_spec_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY experiment_spot_design_read_spec
    ADD CONSTRAINT experiment_spot_design_read_spec_pkey PRIMARY KEY (experiment_spot_design_read_spec_id);


--
-- Name: file_attribute_file_id_tag_value_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY file_attribute
    ADD CONSTRAINT file_attribute_file_id_tag_value_key UNIQUE (file_id, tag, value);


--
-- Name: file_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY file_attribute
    ADD CONSTRAINT file_attribute_pkey PRIMARY KEY (file_attribute_id);


--
-- Name: file_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY file
    ADD CONSTRAINT file_pkey PRIMARY KEY (file_id);


--
-- Name: file_report_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY file_report
    ADD CONSTRAINT file_report_pkey PRIMARY KEY (row_id);


--
-- Name: file_type_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY file_type
    ADD CONSTRAINT file_type_pkey PRIMARY KEY (file_type_id);


--
-- Name: ius_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY ius_attribute
    ADD CONSTRAINT ius_attribute_pkey PRIMARY KEY (ius_attribute_id);


--
-- Name: ius_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY ius
    ADD CONSTRAINT ius_pkey PRIMARY KEY (ius_id);


--
-- Name: lane_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY lane_attribute
    ADD CONSTRAINT lane_attribute_pkey PRIMARY KEY (lane_attribute_id);


--
-- Name: lane_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY lane
    ADD CONSTRAINT lane_pkey PRIMARY KEY (lane_id);


--
-- Name: lane_type_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY lane_type
    ADD CONSTRAINT lane_type_pkey PRIMARY KEY (lane_type_id);


--
-- Name: library_selection_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY library_selection
    ADD CONSTRAINT library_selection_pkey PRIMARY KEY (library_selection_id);


--
-- Name: library_source_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY library_source
    ADD CONSTRAINT library_source_pkey PRIMARY KEY (library_source_id);


--
-- Name: library_strategy_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY library_strategy
    ADD CONSTRAINT library_strategy_pkey PRIMARY KEY (library_strategy_id);


--
-- Name: organism_code_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY organism
    ADD CONSTRAINT organism_code_key UNIQUE (code);


--
-- Name: organism_ncbi_taxid_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY organism
    ADD CONSTRAINT organism_ncbi_taxid_key UNIQUE (ncbi_taxid);


--
-- Name: organism_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY organism
    ADD CONSTRAINT organism_pkey PRIMARY KEY (organism_id);


--
-- Name: pk_ius_link; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY ius_link
    ADD CONSTRAINT pk_ius_link PRIMARY KEY (ius_link_id);


--
-- Name: pk_ius_workflow_runs; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY ius_workflow_runs
    ADD CONSTRAINT pk_ius_workflow_runs PRIMARY KEY (ius_workflow_runs_id);


--
-- Name: pk_lane_link; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY lane_link
    ADD CONSTRAINT pk_lane_link PRIMARY KEY (lane_link_id);


--
-- Name: pk_lane_workflow_runs; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY lane_workflow_runs
    ADD CONSTRAINT pk_lane_workflow_runs PRIMARY KEY (lane_workflow_runs_id);


--
-- Name: pk_share_experiment; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY share_experiment
    ADD CONSTRAINT pk_share_experiment PRIMARY KEY (share_experiment_id);


--
-- Name: pk_share_file; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY share_file
    ADD CONSTRAINT pk_share_file PRIMARY KEY (share_file_id);


--
-- Name: pk_share_lane; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY share_lane
    ADD CONSTRAINT pk_share_lane PRIMARY KEY (share_lane_id);


--
-- Name: pk_share_processing; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY share_processing
    ADD CONSTRAINT pk_share_processing PRIMARY KEY (share_processing_id);


--
-- Name: pk_share_sample; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY share_sample
    ADD CONSTRAINT pk_share_sample PRIMARY KEY (share_sample_id);


--
-- Name: pk_workflow_param; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY workflow_param
    ADD CONSTRAINT pk_workflow_param PRIMARY KEY (workflow_param_id);


--
-- Name: pk_workflow_param_value; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY workflow_param_value
    ADD CONSTRAINT pk_workflow_param_value PRIMARY KEY (workflow_param_value_id);


--
-- Name: pk_workflow_run_param; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY workflow_run_param
    ADD CONSTRAINT pk_workflow_run_param PRIMARY KEY (workflow_run_param_id);


--
-- Name: platform_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY platform
    ADD CONSTRAINT platform_pkey PRIMARY KEY (platform_id);


--
-- Name: processing_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_attribute
    ADD CONSTRAINT processing_attribute_pkey PRIMARY KEY (processing_attribute_id);


--
-- Name: processing_experiments_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_experiments
    ADD CONSTRAINT processing_experiments_pkey PRIMARY KEY (processing_experiments_id);


--
-- Name: processing_experiments_unique_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_experiments
    ADD CONSTRAINT processing_experiments_unique_key UNIQUE (experiment_id, processing_id);


--
-- Name: processing_files_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_files
    ADD CONSTRAINT processing_files_pkey PRIMARY KEY (processing_files_id);


--
-- Name: processing_ius_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_ius
    ADD CONSTRAINT processing_ius_pkey PRIMARY KEY (processing_ius_id);


--
-- Name: processing_ius_unique_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_ius
    ADD CONSTRAINT processing_ius_unique_key UNIQUE (ius_id, processing_id);


--
-- Name: processing_lanes_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_lanes
    ADD CONSTRAINT processing_lanes_pkey PRIMARY KEY (processing_lanes_id);


--
-- Name: processing_lanes_unique_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_lanes
    ADD CONSTRAINT processing_lanes_unique_key UNIQUE (lane_id, processing_id);


--
-- Name: processing_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing
    ADD CONSTRAINT processing_pkey PRIMARY KEY (processing_id);


--
-- Name: processing_relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_relationship
    ADD CONSTRAINT processing_relationship_pkey PRIMARY KEY (processing_relationship_id);


--
-- Name: processing_samples_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_samples
    ADD CONSTRAINT processing_samples_pkey PRIMARY KEY (processing_samples_id);


--
-- Name: processing_samples_unique_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_samples
    ADD CONSTRAINT processing_samples_unique_key UNIQUE (sample_id, processing_id);


--
-- Name: processing_sequencer_runs_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_sequencer_runs
    ADD CONSTRAINT processing_sequencer_runs_pkey PRIMARY KEY (processing_sequencer_runs_id);


--
-- Name: processing_sequencer_runs_unique_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_sequencer_runs
    ADD CONSTRAINT processing_sequencer_runs_unique_key UNIQUE (sequencer_run_id, processing_id);


--
-- Name: processing_studies_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_studies
    ADD CONSTRAINT processing_studies_pkey PRIMARY KEY (processing_studies_id);


--
-- Name: processing_studies_unique_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY processing_studies
    ADD CONSTRAINT processing_studies_unique_key UNIQUE (study_id, processing_id);


--
-- Name: registration_email_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY registration
    ADD CONSTRAINT registration_email_key UNIQUE (email);


--
-- Name: registration_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY registration
    ADD CONSTRAINT registration_pkey PRIMARY KEY (registration_id);


--
-- Name: sample_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY sample_attribute
    ADD CONSTRAINT sample_attribute_pkey PRIMARY KEY (sample_attribute_id);


--
-- Name: sample_hierarchy_sample_id_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY sample_hierarchy
    ADD CONSTRAINT sample_hierarchy_sample_id_key UNIQUE (sample_id, parent_id);


--
-- Name: sample_link_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY sample_link
    ADD CONSTRAINT sample_link_pkey PRIMARY KEY (sample_link_id);


--
-- Name: sample_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_pkey PRIMARY KEY (sample_id);


--
-- Name: sample_relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY sample_relationship
    ADD CONSTRAINT sample_relationship_pkey PRIMARY KEY (sample_relationship_id);


--
-- Name: sample_report_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY sample_report
    ADD CONSTRAINT sample_report_pkey PRIMARY KEY (row_id);


--
-- Name: sample_search_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY sample_search_attribute
    ADD CONSTRAINT sample_search_attribute_pkey PRIMARY KEY (sample_search_attribute_id);


--
-- Name: sample_search_attribute_sample_search_id_tag_value_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY sample_search_attribute
    ADD CONSTRAINT sample_search_attribute_sample_search_id_tag_value_key UNIQUE (sample_search_id, tag, value);


--
-- Name: sample_search_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY sample_search
    ADD CONSTRAINT sample_search_pkey PRIMARY KEY (sample_search_id);


--
-- Name: sequencer_run_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY sequencer_run_attribute
    ADD CONSTRAINT sequencer_run_attribute_pkey PRIMARY KEY (sequencer_run_attribute_id);


--
-- Name: sequencer_run_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY sequencer_run
    ADD CONSTRAINT sequencer_run_pkey PRIMARY KEY (sequencer_run_id);


--
-- Name: share_study_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY share_study
    ADD CONSTRAINT share_study_pkey PRIMARY KEY (share_study_id);


--
-- Name: share_workflow_run_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY share_workflow_run
    ADD CONSTRAINT share_workflow_run_pkey PRIMARY KEY (share_workflow_run_id);


--
-- Name: study_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY study_attribute
    ADD CONSTRAINT study_attribute_pkey PRIMARY KEY (study_attribute_id);


--
-- Name: study_link_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY study_link
    ADD CONSTRAINT study_link_pkey PRIMARY KEY (study_link_id);


--
-- Name: study_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY study
    ADD CONSTRAINT study_pkey PRIMARY KEY (study_id);


--
-- Name: study_type_name_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY study_type
    ADD CONSTRAINT study_type_name_key UNIQUE (name);


--
-- Name: study_type_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY study_type
    ADD CONSTRAINT study_type_pkey PRIMARY KEY (study_type_id);


--
-- Name: workflow_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY workflow_attribute
    ADD CONSTRAINT workflow_attribute_pkey PRIMARY KEY (workflow_attribute_id);


--
-- Name: workflow_attribute_workflow_id_tag_value_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY workflow_attribute
    ADD CONSTRAINT workflow_attribute_workflow_id_tag_value_key UNIQUE (workflow_id, tag, value);


--
-- Name: workflow_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY workflow
    ADD CONSTRAINT workflow_pkey PRIMARY KEY (workflow_id);


--
-- Name: workflow_run_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY workflow_run_attribute
    ADD CONSTRAINT workflow_run_attribute_pkey PRIMARY KEY (workflow_run_attribute_id);


--
-- Name: workflow_run_attribute_workflow_run_id_tag_value_key; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY workflow_run_attribute
    ADD CONSTRAINT workflow_run_attribute_workflow_run_id_tag_value_key UNIQUE (workflow_run_id, tag, value);


--
-- Name: workflow_run_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY workflow_run
    ADD CONSTRAINT workflow_run_pkey PRIMARY KEY (workflow_run_id);


--
-- Name: file_report_study_id_idx; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX file_report_study_id_idx ON file_report USING btree (study_id);


--
-- Name: index_processing_relationship_child_id; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX index_processing_relationship_child_id ON processing_relationship USING btree (child_id);


--
-- Name: index_processing_relationship_parent_id; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX index_processing_relationship_parent_id ON processing_relationship USING btree (parent_id);


--
-- Name: sample_report_study_id_child_sample_id_workflow_id_idx; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX sample_report_study_id_child_sample_id_workflow_id_idx ON sample_report USING btree (study_id, child_sample_id, workflow_id);


--
-- Name: FileReportTrigger; Type: TRIGGER; Schema: public; Owner: seqware
--

-- CREATE TRIGGER "FileReportTrigger"
--    AFTER INSERT ON processing_files
--    FOR EACH ROW
--    EXECUTE PROCEDURE "FileReportInsert"();


--
-- Name: removeFileReport; Type: TRIGGER; Schema: public; Owner: seqware
--

-- CREATE TRIGGER "removeFileReport"
--    AFTER DELETE ON processing_files
--    FOR EACH ROW
--    EXECUTE PROCEDURE "FileReportDelete"();


--
-- Name: sampleReportDelete; Type: TRIGGER; Schema: public; Owner: seqware
--

-- CREATE TRIGGER "sampleReportDelete"
--    AFTER DELETE ON workflow_run
--    FOR EACH ROW
--    EXECUTE PROCEDURE "SampleReportDelete"();


--
-- Name: sample_report_update; Type: TRIGGER; Schema: public; Owner: seqware
--

-- CREATE TRIGGER sample_report_update
--    AFTER INSERT OR UPDATE ON workflow_run
--    FOR EACH ROW
--    EXECUTE PROCEDURE "SampleReportUpdate"();


--
-- Name: experiment__owner_fk; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment
    ADD CONSTRAINT experiment__owner_fk FOREIGN KEY (owner_id) REFERENCES registration(registration_id);


--
-- Name: experiment_attribute_experiment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment_attribute
    ADD CONSTRAINT experiment_attribute_experiment_id_fkey FOREIGN KEY (experiment_id) REFERENCES experiment(experiment_id);


--
-- Name: experiment_experiment_library_design_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment
    ADD CONSTRAINT experiment_experiment_library_design_id_fkey FOREIGN KEY (experiment_library_design_id) REFERENCES experiment_library_design(experiment_library_design_id);


--
-- Name: experiment_experiment_spot_design_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment
    ADD CONSTRAINT experiment_experiment_spot_design_id_fkey FOREIGN KEY (experiment_spot_design_id) REFERENCES experiment_spot_design(experiment_spot_design_id);


--
-- Name: experiment_library_design_selection_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment_library_design
    ADD CONSTRAINT experiment_library_design_selection_fkey FOREIGN KEY (selection) REFERENCES library_selection(library_selection_id);


--
-- Name: experiment_library_design_source_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment_library_design
    ADD CONSTRAINT experiment_library_design_source_fkey FOREIGN KEY (source) REFERENCES library_source(library_source_id);


--
-- Name: experiment_library_design_strategy_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment_library_design
    ADD CONSTRAINT experiment_library_design_strategy_fkey FOREIGN KEY (strategy) REFERENCES library_strategy(library_strategy_id);


--
-- Name: experiment_link_experiment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment_link
    ADD CONSTRAINT experiment_link_experiment_id_fkey FOREIGN KEY (experiment_id) REFERENCES experiment(experiment_id);


--
-- Name: experiment_platform_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment
    ADD CONSTRAINT experiment_platform_id_fkey FOREIGN KEY (platform_id) REFERENCES platform(platform_id);


--
-- Name: experiment_spot_design_read_spec_experiment_spot_design_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment_spot_design_read_spec
    ADD CONSTRAINT experiment_spot_design_read_spec_experiment_spot_design_id_fkey FOREIGN KEY (experiment_spot_design_id) REFERENCES experiment_spot_design(experiment_spot_design_id);


--
-- Name: experiment_study_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY experiment
    ADD CONSTRAINT experiment_study_id_fkey FOREIGN KEY (study_id) REFERENCES study(study_id);


--
-- Name: fk2d251a1c685fba9d; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY workflow_attribute
    ADD CONSTRAINT fk2d251a1c685fba9d FOREIGN KEY (workflow_id) REFERENCES workflow(workflow_id);


--
-- Name: fk35f09d686071c5f8; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY workflow_run_attribute
    ADD CONSTRAINT fk35f09d686071c5f8 FOREIGN KEY (workflow_run_id) REFERENCES workflow_run(workflow_run_id);


--
-- Name: fk48cd19ddcc73e37d; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample_search
    ADD CONSTRAINT fk48cd19ddcc73e37d FOREIGN KEY (sample_id) REFERENCES sample(sample_id);


--
-- Name: fk7750ef99bb4f9efd; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY file_attribute
    ADD CONSTRAINT fk7750ef99bb4f9efd FOREIGN KEY (file_id) REFERENCES file(file_id);


--
-- Name: fk_file_file_type_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY file
    ADD CONSTRAINT fk_file_file_type_id FOREIGN KEY (file_type_id) REFERENCES file_type(file_type_id);


--
-- Name: fk_file_owner_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY file
    ADD CONSTRAINT fk_file_owner_id FOREIGN KEY (owner_id) REFERENCES registration(registration_id);


--
-- Name: fk_ius_link_ius_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY ius_link
    ADD CONSTRAINT fk_ius_link_ius_id FOREIGN KEY (ius_id) REFERENCES ius(ius_id);


--
-- Name: fk_ius_owner_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY ius
    ADD CONSTRAINT fk_ius_owner_id FOREIGN KEY (owner_id) REFERENCES registration(registration_id);


--
-- Name: fk_ius_workflow_runs_ius_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY ius_workflow_runs
    ADD CONSTRAINT fk_ius_workflow_runs_ius_id FOREIGN KEY (ius_id) REFERENCES ius(ius_id);


--
-- Name: fk_ius_workflow_runs_workflow_run_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY ius_workflow_runs
    ADD CONSTRAINT fk_ius_workflow_runs_workflow_run_id FOREIGN KEY (workflow_run_id) REFERENCES workflow_run(workflow_run_id);


--
-- Name: fk_lane_link_lane_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane_link
    ADD CONSTRAINT fk_lane_link_lane_id FOREIGN KEY (lane_id) REFERENCES lane(lane_id);


--
-- Name: fk_lane_workflow_runs_lane_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane_workflow_runs
    ADD CONSTRAINT fk_lane_workflow_runs_lane_id FOREIGN KEY (lane_id) REFERENCES lane(lane_id);


--
-- Name: fk_lane_workflow_runs_workflow_run_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane_workflow_runs
    ADD CONSTRAINT fk_lane_workflow_runs_workflow_run_id FOREIGN KEY (workflow_run_id) REFERENCES workflow_run(workflow_run_id);


--
-- Name: fk_processing_owner_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing
    ADD CONSTRAINT fk_processing_owner_id FOREIGN KEY (owner_id) REFERENCES registration(registration_id);


--
-- Name: fk_sequencer_run_attribute_sequencer_run_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sequencer_run_attribute
    ADD CONSTRAINT fk_sequencer_run_attribute_sequencer_run_id FOREIGN KEY (sample_id) REFERENCES sequencer_run(sequencer_run_id);


--
-- Name: fk_share_experiment_experiment_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_experiment
    ADD CONSTRAINT fk_share_experiment_experiment_id FOREIGN KEY (experiment_id) REFERENCES experiment(experiment_id);


--
-- Name: fk_share_experiment_registration_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_experiment
    ADD CONSTRAINT fk_share_experiment_registration_id FOREIGN KEY (registration_id) REFERENCES registration(registration_id);


--
-- Name: fk_share_file_file_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_file
    ADD CONSTRAINT fk_share_file_file_id FOREIGN KEY (file_id) REFERENCES file(file_id);


--
-- Name: fk_share_file_registration_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_file
    ADD CONSTRAINT fk_share_file_registration_id FOREIGN KEY (registration_id) REFERENCES registration(registration_id);


--
-- Name: fk_share_lane_lane_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_lane
    ADD CONSTRAINT fk_share_lane_lane_id FOREIGN KEY (lane_id) REFERENCES lane(lane_id);


--
-- Name: fk_share_lane_registration_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_lane
    ADD CONSTRAINT fk_share_lane_registration_id FOREIGN KEY (registration_id) REFERENCES registration(registration_id);


--
-- Name: fk_share_processing_processing_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_processing
    ADD CONSTRAINT fk_share_processing_processing_id FOREIGN KEY (processing_id) REFERENCES processing(processing_id);


--
-- Name: fk_share_processing_registration_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_processing
    ADD CONSTRAINT fk_share_processing_registration_id FOREIGN KEY (registration_id) REFERENCES registration(registration_id);


--
-- Name: fk_share_sample_registration_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_sample
    ADD CONSTRAINT fk_share_sample_registration_id FOREIGN KEY (registration_id) REFERENCES registration(registration_id);


--
-- Name: fk_share_sample_sample_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_sample
    ADD CONSTRAINT fk_share_sample_sample_id FOREIGN KEY (sample_id) REFERENCES sample(sample_id);


--
-- Name: fk_share_study_registration_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_study
    ADD CONSTRAINT fk_share_study_registration_id FOREIGN KEY (registration_id) REFERENCES registration(registration_id);


--
-- Name: fk_share_study_study_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_study
    ADD CONSTRAINT fk_share_study_study_id FOREIGN KEY (study_id) REFERENCES study(study_id);


--
-- Name: fk_share_workflow_run_registration_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_workflow_run
    ADD CONSTRAINT fk_share_workflow_run_registration_id FOREIGN KEY (registration_id) REFERENCES registration(registration_id);


--
-- Name: fk_share_workflow_run_workflow_run_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY share_workflow_run
    ADD CONSTRAINT fk_share_workflow_run_workflow_run_id FOREIGN KEY (workflow_run_id) REFERENCES workflow_run(workflow_run_id);


--
-- Name: fk_workflow_param_value_workflow_param_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY workflow_param_value
    ADD CONSTRAINT fk_workflow_param_value_workflow_param_id FOREIGN KEY (workflow_param_id) REFERENCES workflow_param(workflow_param_id);


--
-- Name: fk_workflow_param_workflow_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY workflow_param
    ADD CONSTRAINT fk_workflow_param_workflow_id FOREIGN KEY (workflow_id) REFERENCES workflow(workflow_id);


--
-- Name: fk_workflow_registration_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY workflow
    ADD CONSTRAINT fk_workflow_registration_id FOREIGN KEY (owner_id) REFERENCES registration(registration_id);


--
-- Name: fk_workflow_run_param_workflow_run_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY workflow_run_param
    ADD CONSTRAINT fk_workflow_run_param_workflow_run_id FOREIGN KEY (workflow_run_id) REFERENCES workflow_run(workflow_run_id);


--
-- Name: fk_workflow_run_registration_id; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY workflow_run
    ADD CONSTRAINT fk_workflow_run_registration_id FOREIGN KEY (owner_id) REFERENCES registration(registration_id);


--
-- Name: fkf378ceba921d0a72; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample_search_attribute
    ADD CONSTRAINT fkf378ceba921d0a72 FOREIGN KEY (sample_search_id) REFERENCES sample_search(sample_search_id);


--
-- Name: ius__lane_fk; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY ius
    ADD CONSTRAINT ius__lane_fk FOREIGN KEY (lane_id) REFERENCES lane(lane_id);


--
-- Name: ius__sample_fk; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY ius
    ADD CONSTRAINT ius__sample_fk FOREIGN KEY (sample_id) REFERENCES sample(sample_id);


--
-- Name: ius_attribute_ius_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY ius_attribute
    ADD CONSTRAINT ius_attribute_ius_id_fkey FOREIGN KEY (ius_id) REFERENCES ius(ius_id);


--
-- Name: lane_attribute_lane_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane_attribute
    ADD CONSTRAINT lane_attribute_lane_id_fkey FOREIGN KEY (lane_id) REFERENCES lane(lane_id);


--
-- Name: lane_library_selection_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane
    ADD CONSTRAINT lane_library_selection_fkey FOREIGN KEY (library_selection) REFERENCES library_selection(library_selection_id);


--
-- Name: lane_library_source_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane
    ADD CONSTRAINT lane_library_source_fkey FOREIGN KEY (library_source) REFERENCES library_source(library_source_id);


--
-- Name: lane_library_strategy_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane
    ADD CONSTRAINT lane_library_strategy_fkey FOREIGN KEY (library_strategy) REFERENCES library_strategy(library_strategy_id);


--
-- Name: lane_organism_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane
    ADD CONSTRAINT lane_organism_id_fkey FOREIGN KEY (organism_id) REFERENCES organism(organism_id);


--
-- Name: lane_owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane
    ADD CONSTRAINT lane_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES registration(registration_id);


--
-- Name: lane_sample_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane
    ADD CONSTRAINT lane_sample_id_fkey FOREIGN KEY (sample_id) REFERENCES sample(sample_id);


--
-- Name: lane_sequencer_run_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane
    ADD CONSTRAINT lane_sequencer_run_id_fkey FOREIGN KEY (sequencer_run_id) REFERENCES sequencer_run(sequencer_run_id);


--
-- Name: lane_study_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane
    ADD CONSTRAINT lane_study_type_fkey FOREIGN KEY (study_type) REFERENCES study_type(study_type_id);


--
-- Name: lane_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY lane
    ADD CONSTRAINT lane_type_fkey FOREIGN KEY (type) REFERENCES lane_type(lane_type_id);


--
-- Name: processing_ancestor_workflow_run_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing
    ADD CONSTRAINT processing_ancestor_workflow_run_id_fkey FOREIGN KEY (ancestor_workflow_run_id) REFERENCES workflow_run(workflow_run_id);


--
-- Name: processing_attribute_processing_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_attribute
    ADD CONSTRAINT processing_attribute_processing_id_fkey FOREIGN KEY (processing_id) REFERENCES processing(processing_id);


--
-- Name: processing_experiments_experiment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_experiments
    ADD CONSTRAINT processing_experiments_experiment_id_fkey FOREIGN KEY (experiment_id) REFERENCES experiment(experiment_id);


--
-- Name: processing_experiments_processing_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_experiments
    ADD CONSTRAINT processing_experiments_processing_id_fkey FOREIGN KEY (processing_id) REFERENCES processing(processing_id);


--
-- Name: processing_files_file_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_files
    ADD CONSTRAINT processing_files_file_id_fkey FOREIGN KEY (file_id) REFERENCES file(file_id);


--
-- Name: processing_files_processing_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_files
    ADD CONSTRAINT processing_files_processing_id_fkey FOREIGN KEY (processing_id) REFERENCES processing(processing_id);


--
-- Name: processing_ius_ius_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_ius
    ADD CONSTRAINT processing_ius_ius_id_fkey FOREIGN KEY (ius_id) REFERENCES ius(ius_id);


--
-- Name: processing_ius_processing_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_ius
    ADD CONSTRAINT processing_ius_processing_id_fkey FOREIGN KEY (processing_id) REFERENCES processing(processing_id);


--
-- Name: processing_lanes_lane_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_lanes
    ADD CONSTRAINT processing_lanes_lane_id_fkey FOREIGN KEY (lane_id) REFERENCES lane(lane_id);


--
-- Name: processing_lanes_processing_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_lanes
    ADD CONSTRAINT processing_lanes_processing_id_fkey FOREIGN KEY (processing_id) REFERENCES processing(processing_id);


--
-- Name: processing_relationship_child_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_relationship
    ADD CONSTRAINT processing_relationship_child_id_fkey FOREIGN KEY (child_id) REFERENCES processing(processing_id) ON DELETE CASCADE;


--
-- Name: processing_relationship_parent_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_relationship
    ADD CONSTRAINT processing_relationship_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES processing(processing_id);


--
-- Name: processing_samples_processing_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_samples
    ADD CONSTRAINT processing_samples_processing_id_fkey FOREIGN KEY (processing_id) REFERENCES processing(processing_id);


--
-- Name: processing_samples_sample_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_samples
    ADD CONSTRAINT processing_samples_sample_id_fkey FOREIGN KEY (sample_id) REFERENCES sample(sample_id);


--
-- Name: processing_sequencer_runs_processing_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_sequencer_runs
    ADD CONSTRAINT processing_sequencer_runs_processing_id_fkey FOREIGN KEY (processing_id) REFERENCES processing(processing_id);


--
-- Name: processing_sequencer_runs_sequencer_run_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_sequencer_runs
    ADD CONSTRAINT processing_sequencer_runs_sequencer_run_id_fkey FOREIGN KEY (sequencer_run_id) REFERENCES sequencer_run(sequencer_run_id);


--
-- Name: processing_studies_processing_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_studies
    ADD CONSTRAINT processing_studies_processing_id_fkey FOREIGN KEY (processing_id) REFERENCES processing(processing_id);


--
-- Name: processing_studies_study_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing_studies
    ADD CONSTRAINT processing_studies_study_id_fkey FOREIGN KEY (study_id) REFERENCES study(study_id);


--
-- Name: processing_workflow_run_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY processing
    ADD CONSTRAINT processing_workflow_run_id_fkey FOREIGN KEY (workflow_run_id) REFERENCES workflow_run(workflow_run_id);


--
-- Name: sample__organism_fk; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT sample__organism_fk FOREIGN KEY (organism_id) REFERENCES organism(organism_id);


--
-- Name: sample__owner_fk; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT sample__owner_fk FOREIGN KEY (owner_id) REFERENCES registration(registration_id);


--
-- Name: sample_attribute_sample_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample_attribute
    ADD CONSTRAINT sample_attribute_sample_id_fkey FOREIGN KEY (sample_id) REFERENCES sample(sample_id);


--
-- Name: sample_experiment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_experiment_id_fkey FOREIGN KEY (experiment_id) REFERENCES experiment(experiment_id);


--
-- Name: sample_hierarchy_parent_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample_hierarchy
    ADD CONSTRAINT sample_hierarchy_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES sample(sample_id);


--
-- Name: sample_hierarchy_sample_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample_hierarchy
    ADD CONSTRAINT sample_hierarchy_sample_id_fkey FOREIGN KEY (sample_id) REFERENCES sample(sample_id);


--
-- Name: sample_link_sample_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample_link
    ADD CONSTRAINT sample_link_sample_id_fkey FOREIGN KEY (sample_id) REFERENCES sample(sample_id);


--
-- Name: sample_relationship_child_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample_relationship
    ADD CONSTRAINT sample_relationship_child_id_fkey FOREIGN KEY (child_id) REFERENCES sample(sample_id) ON DELETE CASCADE;


--
-- Name: sample_relationship_parent_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sample_relationship
    ADD CONSTRAINT sample_relationship_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES sample(sample_id);


--
-- Name: sequencer_run_owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sequencer_run
    ADD CONSTRAINT sequencer_run_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES registration(registration_id);


--
-- Name: sequencer_run_platform_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY sequencer_run
    ADD CONSTRAINT sequencer_run_platform_id_fkey FOREIGN KEY (platform_id) REFERENCES platform(platform_id);


--
-- Name: study_attribute_study_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY study_attribute
    ADD CONSTRAINT study_attribute_study_id_fkey FOREIGN KEY (study_id) REFERENCES study(study_id);


--
-- Name: study_existing_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY study
    ADD CONSTRAINT study_existing_type_fkey FOREIGN KEY (existing_type) REFERENCES study_type(study_type_id);


--
-- Name: study_link_study_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY study_link
    ADD CONSTRAINT study_link_study_id_fkey FOREIGN KEY (study_id) REFERENCES study(study_id);


--
-- Name: study_owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY study
    ADD CONSTRAINT study_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES registration(registration_id);


--
-- Name: workflow_run_workflow_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY workflow_run
    ADD CONSTRAINT workflow_run_workflow_id_fkey FOREIGN KEY (workflow_id) REFERENCES workflow(workflow_id);

--
-- Name: invoice_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE invoice_invoice_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: expense_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE expense_expense_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: invoice_attribute; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE invoice_attribute (
    invoice_attribute_id integer NOT NULL,
    invoice_id integer NOT NULL,
    tag text,
    value text,
    units text
);

--
-- Name: invoice_attribute_invoice_attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE invoice_attribute_invoice_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: invoice_attribute_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY invoice_attribute ALTER COLUMN invoice_attribute_id SET DEFAULT nextval('invoice_attribute_invoice_attribute_id_seq'::regclass);

--
-- Name: invoice_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY invoice_attribute
    ADD CONSTRAINT invoice_attribute_pkey PRIMARY KEY (invoice_attribute_id);


--
-- Name: expense_attribute; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE expense_attribute (
    expense_attribute_id integer NOT NULL,
    expense_id integer NOT NULL,
    tag text,
    value text,
    units text
);

--
-- Name: expense_attribute_expense_attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE expense_attribute_expense_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: expense_attribute_id; Type: DEFAULT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY expense_attribute ALTER COLUMN expense_attribute_id SET DEFAULT nextval('expense_attribute_expense_attribute_id_seq'::regclass);

--
-- Name: expense_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY expense_attribute
    ADD CONSTRAINT expense_attribute_pkey PRIMARY KEY (expense_attribute_id);

--
-- Name: invoice; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE invoice (
    invoice_id integer NOT NULL,
    owner_id integer NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    state text,
    finalized boolean,
    fully_paid boolean,
    paid_amount numeric,
    days_until_due integer,
    external_id text,
    client_notes text,
    notes text,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    create_tstmp timestamp without time zone NOT NULL
    );


ALTER TABLE ONLY invoice ALTER COLUMN invoice_id SET DEFAULT nextval('invoice_invoice_id_seq'::regclass);

ALTER TABLE ONLY invoice
    ADD CONSTRAINT invoice_pkey PRIMARY KEY (invoice_id);

ALTER TABLE ONLY invoice
    ADD CONSTRAINT invoice_owner_id_fk FOREIGN KEY (owner_id) REFERENCES registration(registration_id);

--
-- Name: expense; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE expense (
    expense_id integer NOT NULL,
    invoice_id integer NOT NULL,
    workflow_run_id integer,
    agent text,
    expense_type text,
    description text,
    price_per_unit numeric,
    total_units numeric,
    total_price numeric,
    added_surcharge numeric,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    expense_finished_tstmp timestamp without time zone
    );

ALTER TABLE ONLY expense ALTER COLUMN expense_id SET DEFAULT nextval('expense_expense_id_seq'::regclass);

ALTER TABLE ONLY expense
    ADD CONSTRAINT expense_pkey PRIMARY KEY (expense_id);

ALTER TABLE ONLY expense
    ADD CONSTRAINT invoice_id_fk FOREIGN KEY (invoice_id) REFERENCES invoice(invoice_id);

--
-- Name: expense_attribute_expense_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY expense_attribute
    ADD CONSTRAINT expense_attribute_expense_id_fkey FOREIGN KEY (expense_id) REFERENCES expense(expense_id);

--
-- Name: invoice_attribute_invoice_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY invoice_attribute
    ADD CONSTRAINT invoice_attribute_invoice_id_fkey FOREIGN KEY (invoice_id) REFERENCES invoice(invoice_id);



--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

-- REVOKE ALL ON SCHEMA public FROM PUBLIC;
-- REVOKE ALL ON SCHEMA public FROM postgres;
-- GRANT ALL ON SCHEMA public TO postgres;
-- GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

