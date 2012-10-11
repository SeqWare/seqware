ALTER TABLE sequencer_run ADD COLUMN skip boolean DEFAULT false;
ALTER TABLE ius ADD COLUMN skip boolean DEFAULT false;
ALTER TABLE lane
   ALTER COLUMN skip SET DEFAULT false;
