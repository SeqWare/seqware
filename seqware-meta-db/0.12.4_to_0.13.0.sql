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
-- adding payee field
-- 

ALTER TABLE only registration ADD COLUMN payee boolean not null default false;


