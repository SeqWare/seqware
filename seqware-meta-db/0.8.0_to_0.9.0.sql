-- Add constraintes to organism table
ALTER TABLE organism ADD UNIQUE (code);
ALTER TABLE organism ALTER COLUMN code SET NOT NULL;
ALTER TABLE organism ADD UNIQUE (ncbi_taxid);

-- Add new  NCBI organisms to organism table
INSERT INTO organism(organism_id, code, "name", ncbi_taxid) VALUES (DEFAULT, 'Escherichia_coli_str._K-12_substr._DH10B', 'Escherichia coli str. K-12 substr. DH10B', 316385);
INSERT INTO organism(organism_id, code, "name", ncbi_taxid) VALUES (DEFAULT, 'Enterobacteria_phage_phiX174', 'Enterobacteria phage phiX174', 10847);
INSERT INTO organism(organism_id, code, "name", ncbi_taxid) VALUES (DEFAULT, 'Chlorocebus_pygerythrus', 'Chlorocebus pygerythrus', 60710);
INSERT INTO organism(organism_id, code, "name", ncbi_taxid) VALUES (DEFAULT, 'Plasmodium_falciparum', 'Plasmodium falciparum', 5833);
INSERT INTO organism(organism_id, code, "name", ncbi_taxid) VALUES (DEFAULT, 'Rhodobacter_sphaeroides', 'Rhodobacter sphaeroides', 1063);
INSERT INTO organism(organism_id, code, "name", ncbi_taxid) VALUES (DEFAULT, 'Staphylococcus_aureus', 'Staphylococcus aureus', 1280);

-- Add OICR specific organisms to organism table
INSERT INTO organism(organism_id, code, "name") VALUES (DEFAULT, 'OICR_Vaccinia_jx-594', 'OICR Vaccinia JX-594');
INSERT INTO organism(organism_id, code, "name") VALUES (DEFAULT, 'OICR_De_novo_assembly', 'OICR De novo assembly');
INSERT INTO organism(organism_id, code, "name") VALUES (DEFAULT, 'OICR_See_comments', 'OICR See Comments');

-- Improve 'other' code in organism table. We want a code without spaces.
UPDATE organism SET  code='Other', "name"='Other (add to description)' WHERE code like 'other (add to description)';

-- Add NCBI id to existing organisms.
UPDATE organism SET ncbi_taxid=9606 WHERE code like 'Homo_sapiens';
UPDATE organism SET ncbi_taxid=10090 WHERE code like 'Mus_musculus';
UPDATE organism SET ncbi_taxid=10116 WHERE code like 'Rattus_norvegicus';
UPDATE organism SET ncbi_taxid=6239 WHERE code like 'Caenorhabditis_elegans';

-- Adding new platforms.
INSERT INTO platform(platform_id, "name", instrument_model, description) VALUES (DEFAULT, 'ILLUMINA', 'Illumina MiSeq', 'Illumina is 4-channel flowgram with 1-to-1 mapping between basecalls and flows');
INSERT INTO platform(platform_id, "name", instrument_model, description) VALUES (DEFAULT, 'ION_TORRENT', 'Ion Torrent PGM', 'Ion Torrent Personal Genome Machine (PGM) from Life Technologies.');
INSERT INTO platform(platform_id, "name", instrument_model, description) VALUES (DEFAULT, 'PACBIO_SMRT', 'PacBio RS', 'PacificBiosciences platform type for the single molecule real time (SMRT) technology.');
