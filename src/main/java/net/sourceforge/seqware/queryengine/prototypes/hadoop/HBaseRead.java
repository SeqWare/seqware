package net.sourceforge.seqware.queryengine.prototypes.hadoop;

import net.sourceforge.seqware.queryengine.backend.model.Feature;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;

public class HBaseRead {

	public static void main(String[] args) throws Exception {

		String featureId = args[0];

		HBaseStore store = new HBaseStore();
		SeqWareSettings settings = new SeqWareSettings();
		settings.setReferenceId("hg18");
		settings.setGenomeId("032399");
		store.setup(settings);
		Feature f = store.getFeature(featureId);
		// modified when moving to hbase-0.90.6, looks like colons are no
		// longer allowed in family names SEQWARE-607
		System.out.println("Feature " + f.getContig());
		System.out.println("FeatureId " + f.getId());
		// store.getFeaturesByTag("frameshift");
		// store.getFeaturesByTag("is_dbSNP129");
		store.close();

	}

}
