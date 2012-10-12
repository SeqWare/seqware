package net.sourceforge.seqware.queryengine.backend;

import net.sourceforge.seqware.queryengine.backend.model.Feature;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * <p>HBaseTest class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
@Test(groups = "hbase")
public class HBaseTest {

	private static final String QE_HBASE_FEATUREID = System
			.getProperty("QE_HBASE_FEATUREID");

	@BeforeTest(enabled = true)
	void setup() {
		// nothing to do here
	}

	@AfterTest(enabled = true)
	void tearDown() {
		// nothing to do here
	}

	/**
	 * <p>testHBaseReading.</p>
	 *
	 * @throws java.lang.Exception if any.
	 */
	@Test(dependsOnMethods = { "testHBaseWriting" }, enabled = true)
	public static void testHBaseReading() throws Exception {

		String featureId = QE_HBASE_FEATUREID;

		HBaseStore store = new HBaseStore();
		SeqWareSettings settings = new SeqWareSettings();
		settings.setReferenceId("hg18");
		settings.setGenomeId("032399");
		store.setup(settings);
		System.out.println("FeatureID " + featureId);
		Feature f = store.getFeature(featureId);
		// modified when moving to hbase-0.90.6, looks like colons are no
		// longer allowed in family names SEQWARE-607
		System.out.println("Feature " + f.getContig());
		System.out.println("FeatureId " + f.getId());
		// store.getFeaturesByTag("frameshift");
		// store.getFeaturesByTag("is_dbSNP129");
		store.close();

	}

	/**
	 * <p>testHBaseWriting.</p>
	 *
	 * @throws java.lang.Exception if any.
	 */
	@Test(enabled = true)
	public static void testHBaseWriting(/* String[] args */) throws Exception {

		HBaseStore store = new HBaseStore();
		SeqWareSettings settings = new SeqWareSettings();
		settings.setReferenceId("hg18");
		settings.setGenomeId("032399");
		store.setup(settings);
		String chr = "chr22";
		int start = 1234;
		int count = 1;

		/*
		 * if (args.length >= 1 && args[0] != null) { chr = args[0]; } if
		 * (args.length >= 2 && args[1] != null) { start =
		 * Integer.parseInt(args[1]); } if (args.length >= 3 && args[2] != null)
		 * { count = Integer.parseInt(args[2]); }
		 */

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
