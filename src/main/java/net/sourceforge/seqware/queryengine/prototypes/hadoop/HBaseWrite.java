package net.sourceforge.seqware.queryengine.prototypes.hadoop;

import net.sourceforge.seqware.queryengine.backend.model.Feature;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;

public class HBaseWrite {

	public static void main(String[] args) throws Exception {

		HBaseStore store = new HBaseStore();
		SeqWareSettings settings = new SeqWareSettings();
		settings.setReferenceId("hg18");
		settings.setGenomeId("032399");
		store.setup(settings);
		String chr = "chr22";
		int start = 1234;
		int count = 1;

		if (args.length >= 1 && args[0] != null) {
			chr = args[0];
		}
		if (args.length >= 2 && args[1] != null) {
			start = Integer.parseInt(args[1]);
		}
		if (args.length >= 3 && args[2] != null) {
			count = Integer.parseInt(args[2]);
		}

		int update = 0;
		for (int i = 0; i < count; i++) {

			update++;
			int currPos = start + i;
			if (update >= 1000) {
				update = 0;
				System.out.print("Putting " + chr + ":" + currPos + "\r");
			}

			Feature feature = new Feature();
			feature.setContig(chr);
			feature.setStartPosition(currPos);
			feature.getTags().put("is_dbSNP129", "rs2129121");
			feature.getTags().put("frameshift", "");
			feature.getTags().put("nonsynonyous", "");
			feature.getTags().put("GO:12323121", "cell_membrane");

			String id = store.putFeature(feature);

			System.out.println("ID: " + id);

			/*
			 * feature.setStartPosition(123243235);
			 * feature.getTags().put("intron-splice-site", "");
			 * feature.getTags().remove("frameshift");
			 * 
			 * store.putFeature(feature);
			 * 
			 * feature.setStartPosition(123243236);
			 * feature.getTags().put("omim:323131", "");
			 * 
			 * store.putFeature(feature);
			 */
		}
		store.close();
	}

}
