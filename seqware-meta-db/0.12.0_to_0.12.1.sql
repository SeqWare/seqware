-- Search against samples. The search uses sample attributes as search criteria.
-- Each patient (Identity) has it's own set of unique searches.
CREATE TABLE sample_search
(
  sample_search_id integer NOT NULL,
  sample_id integer NOT NULL,
  create_tstmp timestamp without time zone NOT NULL,
  CONSTRAINT sample_search_pkey PRIMARY KEY (sample_search_id),
  CONSTRAINT fk48cd19ddcc73e37d FOREIGN KEY (sample_id)
      REFERENCES sample (sample_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sample_search OWNER TO seqware;


-- Attributes that make up the sample search criteria.
CREATE TABLE sample_search_attribute
(
  sample_search_attribute_id integer NOT NULL,
  sample_search_id integer NOT NULL,
  tag character varying(255) NOT NULL,
  "value" character varying(255) NOT NULL,
  CONSTRAINT sample_search_attribute_pkey PRIMARY KEY (sample_search_attribute_id),
  CONSTRAINT fkf378ceba921d0a72 FOREIGN KEY (sample_search_id)
      REFERENCES sample_search (sample_search_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT sample_search_attribute_sample_search_id_tag_value_key UNIQUE (sample_search_id, tag, value)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sample_search_attribute OWNER TO seqware;

CREATE SEQUENCE sample_search_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.sample_search_attribute_id_seq OWNER TO seqware;


CREATE SEQUENCE sample_search_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.sample_search_id_seq OWNER TO seqware;
