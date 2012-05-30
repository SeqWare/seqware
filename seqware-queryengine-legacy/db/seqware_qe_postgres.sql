--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


SET search_path = public, pg_catalog;

--
-- Name: feature_feature_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE feature_feature_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.feature_feature_id_seq OWNER TO seqware;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: feature; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE feature (
    feature_id integer DEFAULT nextval('feature_feature_id_seq'::regclass) NOT NULL,
    "type" text,
    contig text NOT NULL,
    "start" integer NOT NULL,
    stop integer NOT NULL,
    bdata bytea,
    oiddata oid,
    keyvalues text
);


ALTER TABLE public.feature OWNER TO seqware;

--
-- Name: feature_tag_feature_tag_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE feature_tag_feature_tag_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.feature_tag_feature_tag_id_seq OWNER TO seqware;

--
-- Name: feature_tag; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE feature_tag (
    feature_tag_id integer DEFAULT nextval('feature_tag_feature_tag_id_seq'::regclass) NOT NULL,
    feature_id integer NOT NULL,
    tag_id integer NOT NULL
);


ALTER TABLE public.feature_tag OWNER TO seqware;

--
-- Name: tag_tag_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE tag_tag_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.tag_tag_id_seq OWNER TO seqware;

--
-- Name: tag; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE tag (
    tag_id integer DEFAULT nextval('tag_tag_id_seq'::regclass) NOT NULL,
    "key" text NOT NULL,
    value text
);


ALTER TABLE public.tag OWNER TO seqware;

--
-- Name: variant_variant_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE variant_variant_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.variant_variant_id_seq OWNER TO seqware;

--
-- Name: variant; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE variant (
    variant_id integer DEFAULT nextval('variant_variant_id_seq'::regclass) NOT NULL,
    "type" integer,
    contig text NOT NULL,
    "start" integer NOT NULL,
    stop integer NOT NULL,
    fuzzystartpositionmax integer,
    fuzzystoppositionmin integer,
    referencebase text,
    consensusbase text,
    calledbase text,
    referencecallquality double precision,
    consensuscallquality double precision,
    maximummappingquality double precision,
    readcount integer,
    readbases text,
    basequalities text,
    calledbasecount integer,
    calledbasecountforward integer,
    calledbasecountreverse integer,
    zygosity integer,
    referencemaxseqquality double precision,
    referenceaveseqquality double precision,
    consensusmaxseqquality double precision,
    consensusaveseqquality double precision,
    callone text,
    calltwo text,
    readssupportingcallone integer,
    readssupportingcalltwo integer,
    readssupportingcallthree integer,
    svtype integer,
    relativelocation integer,
    translocationtype integer,
    translocationdestinationcontig text,
    translocationdestinationstartposition integer,
    translocationdestinationstopposition integer,
    keyvalues text
);


ALTER TABLE public.variant OWNER TO seqware;

--
-- Name: variant_tag_variant_tag_id_seq; Type: SEQUENCE; Schema: public; Owner: seqware
--

CREATE SEQUENCE variant_tag_variant_tag_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.variant_tag_variant_tag_id_seq OWNER TO seqware;

--
-- Name: variant_tag; Type: TABLE; Schema: public; Owner: seqware; Tablespace: 
--

CREATE TABLE variant_tag (
    variant_tag_id integer DEFAULT nextval('variant_tag_variant_tag_id_seq'::regclass) NOT NULL,
    variant_id integer NOT NULL,
    tag_id integer NOT NULL
);


ALTER TABLE public.variant_tag OWNER TO seqware;

--
-- Name: feature_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY feature
    ADD CONSTRAINT feature_pkey PRIMARY KEY (feature_id);


--
-- Name: feature_tag_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY feature_tag
    ADD CONSTRAINT feature_tag_pkey PRIMARY KEY (feature_tag_id);


--
-- Name: tag_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (tag_id);


--
-- Name: variant_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY variant
    ADD CONSTRAINT variant_pkey PRIMARY KEY (variant_id);


--
-- Name: variant_tag_pkey; Type: CONSTRAINT; Schema: public; Owner: seqware; Tablespace: 
--

ALTER TABLE ONLY variant_tag
    ADD CONSTRAINT variant_tag_pkey PRIMARY KEY (variant_tag_id);


--
-- Name: feature_contig_index; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX feature_contig_index ON feature USING btree (contig);


--
-- Name: feature_contig_start_stop_index; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX feature_contig_start_stop_index ON feature USING btree (contig, "start", stop);


--
-- Name: feature_start_index; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX feature_start_index ON feature USING btree ("start");


--
-- Name: feature_stop_index; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX feature_stop_index ON feature USING btree (stop);


--
-- Name: feature_tag_feature_id_index; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX feature_tag_feature_id_index ON feature_tag USING btree (feature_id);


--
-- Name: feature_tag_tag_id_index; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX feature_tag_tag_id_index ON feature_tag USING btree (tag_id);


--
-- Name: tag_key_index; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX tag_key_index ON tag USING btree ("key");


--
-- Name: tag_value_index; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX tag_value_index ON tag USING btree (value);


--
-- Name: variant_contig_index; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX variant_contig_index ON variant USING btree (contig);


--
-- Name: variant_contig_start_stop_index; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX variant_contig_start_stop_index ON variant USING btree (contig, "start", stop);


--
-- Name: variant_start_index; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX variant_start_index ON variant USING btree ("start");


--
-- Name: variant_stop_index; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX variant_stop_index ON variant USING btree (stop);


--
-- Name: variant_tag_tag_id_index; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX variant_tag_tag_id_index ON variant_tag USING btree (tag_id);


--
-- Name: variant_tag_variant_id_index; Type: INDEX; Schema: public; Owner: seqware; Tablespace: 
--

CREATE INDEX variant_tag_variant_id_index ON variant_tag USING btree (variant_id);


--
-- Name: feature_feature_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY feature_tag
    ADD CONSTRAINT feature_feature_id_fkey FOREIGN KEY (feature_id) REFERENCES feature(feature_id);


--
-- Name: tag_tag_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY feature_tag
    ADD CONSTRAINT tag_tag_id_fkey FOREIGN KEY (tag_id) REFERENCES tag(tag_id);


--
-- Name: tag_tag_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY variant_tag
    ADD CONSTRAINT tag_tag_id_fkey FOREIGN KEY (tag_id) REFERENCES tag(tag_id);


--
-- Name: variant_variant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: seqware
--

ALTER TABLE ONLY variant_tag
    ADD CONSTRAINT variant_variant_id_fkey FOREIGN KEY (variant_id) REFERENCES variant(variant_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

