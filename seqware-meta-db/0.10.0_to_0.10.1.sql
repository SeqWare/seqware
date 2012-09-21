ALTER TABLE workflow ADD COLUMN permanent_bundle_location text;
INSERT INTO workflow (name, description, version, seqware_version, create_tstmp) VALUES ('FileImport', 'Imports files into the database, links them to IUSs or Lanes and creates intermediate Processings. Initially used to import files from the LIMS and attach them to IUSes.', '0.1.0', '0.10.0', '2012-01-04 13:51:00');
