-- Add id auto increment to sample_search. (Missing in previous migration script.)
ALTER TABLE sample_search ALTER COLUMN sample_search_id SET DEFAULT nextval('sample_search_id_seq'::regclass);
ALTER TABLE sample_search_attribute ALTER COLUMN sample_search_attribute_id SET DEFAULT nextval('sample_search_attribute_id_seq'::regclass);

-- Add attributes for file, workflow and workflow run.
CREATE TABLE file_attribute
(
  file_attribute_id integer NOT NULL,
  file_id integer NOT NULL,
  tag character varying(255) NOT NULL,
  "value" character varying(255) NOT NULL,
  unit character varying(255),
  CONSTRAINT file_attribute_pkey PRIMARY KEY (file_attribute_id),
  CONSTRAINT fk7750ef99bb4f9efd FOREIGN KEY (file_id)
      REFERENCES file (file_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT file_attribute_file_id_tag_value_key UNIQUE (file_id, tag, value)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE file_attribute OWNER TO seqware;

CREATE SEQUENCE file_attribute_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE file_attribute_id_seq OWNER TO seqware;

ALTER TABLE file_attribute ALTER COLUMN file_attribute_id SET DEFAULT nextval('file_attribute_id_seq'::regclass);

CREATE TABLE workflow_attribute
(
  workflow_attribute_id integer NOT NULL,
  workflow_id integer NOT NULL,
  tag character varying(255) NOT NULL,
  "value" character varying(255) NOT NULL,
  unit character varying(255),
  CONSTRAINT workflow_attribute_pkey PRIMARY KEY (workflow_attribute_id),
  CONSTRAINT fk2d251a1c685fba9d FOREIGN KEY (workflow_id)
      REFERENCES workflow (workflow_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT workflow_attribute_workflow_id_tag_value_key UNIQUE (workflow_id, tag, value)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE workflow_attribute OWNER TO seqware;

CREATE SEQUENCE workflow_attribute_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE workflow_attribute_id_seq OWNER TO seqware;

ALTER TABLE workflow_attribute ALTER COLUMN workflow_attribute_id SET DEFAULT nextval('workflow_attribute_id_seq'::regclass);

CREATE TABLE workflow_run_attribute
(
  workflow_run_attribute_id integer NOT NULL,
  workflow_run_id integer NOT NULL,
  tag character varying(255) NOT NULL,
  "value" character varying(255) NOT NULL,
  unit character varying(255),
  CONSTRAINT workflow_run_attribute_pkey PRIMARY KEY (workflow_run_attribute_id),
  CONSTRAINT fk35f09d686071c5f8 FOREIGN KEY (workflow_run_id)
      REFERENCES workflow_run (workflow_run_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT workflow_run_attribute_workflow_run_id_tag_value_key UNIQUE (workflow_run_id, tag, value)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE workflow_run_attribute OWNER TO seqware;

CREATE SEQUENCE workflow_run_attribute_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE workflow_run_attribute_id_seq OWNER TO seqware;

ALTER TABLE workflow_run_attribute ALTER COLUMN workflow_run_attribute_id SET DEFAULT nextval('workflow_run_attribute_id_seq'::regclass);

-- Add size field to file to store file size in bytes.
ALTER TABLE file ADD COLUMN size bigint;
