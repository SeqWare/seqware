package com.github.seqware.queryengine.system.test;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.system.ReferenceCreator;
import com.github.seqware.queryengine.system.TagSetCreator;
import com.github.seqware.queryengine.system.exporters.VCFDumper;
import com.github.seqware.queryengine.system.importers.FeatureImporter;
import com.github.seqware.queryengine.system.importers.OBOImporter;
import com.github.seqware.queryengine.system.importers.SOFeatureImporter;
import com.github.seqware.queryengine.util.SGID;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * System tests for importing and exporting VCF files while being aware of SO
 *
 * @author dyuen
 */
public class SOFeatureImporterTest {

    private static File testVCFFile = null;
    private static File testVCFFile_missingValues = null;
    private static File testVCFFile_invalid = null;
    private static TagSet sequenceOntology = null;
    private static Reference reference = null;
    private static TagSet adHocSet = null;

    @BeforeClass
    public static void setupTests() {
        // we will need a loaded SO, a reference, and an adhoc tag set to test this sucker

        // load SO
        String curDir = System.getProperty("user.dir");
        File file = new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/so.obo");
        SGID tagSetID = OBOImporter.mainMethod(new String[]{file.getAbsolutePath()});
        sequenceOntology = SWQEFactory.getQueryInterface().getAtomBySGID(TagSet.class, tagSetID);

        // setup reference
        SGID refID = ReferenceCreator.mainMethod(new String[]{"hg_19"});
        reference = SWQEFactory.getQueryInterface().getAtomBySGID(Reference.class, refID);

        // setup ad hoc tag set
        SGID aSetID = TagSetCreator.mainMethod(new String[]{"ad_hoc_tagSet"});
        adHocSet = SWQEFactory.getQueryInterface().getAtomBySGID(TagSet.class, aSetID);

        testVCFFile = new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/consequences_annotated.vcf");
        testVCFFile_missingValues = new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/test_missingValues.vcf");
        testVCFFile_invalid = new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/test_invalid.vcf");
    }

    @Test
    public void testVCFImportParam() throws IOException {
        BufferedReader in = null;
        try {
            File createTempFile = File.createTempFile("output", "txt");
            Assert.assertTrue("Cannot read VCF file for test", testVCFFile.exists() && testVCFFile.canRead());
            SOFeatureImporter.runMain(new String[]{"-w", "VCFVariantImportWorker", "-a", adHocSet.getSGID().getRowKey(),
                        "-i", testVCFFile.getAbsolutePath(), "-o", createTempFile.getAbsolutePath(),
                        "-r", reference.getSGID().getRowKey(), "-s", sequenceOntology.getSGID().getRowKey()
                    });
            // check key value output file created
            in = new BufferedReader(new FileReader(createTempFile));
            String[] output = in.readLine().split("\t");
            boolean twoItems = output.length == 2;
            boolean idStart = output[0].equals(FeatureImporter.FEATURE_SET_ID);
            Assert.assertTrue("output file not populated with correct output", twoItems && idStart);
            createTempFile.delete();
            // run again without unnecessary parameters
            SGID main = SOFeatureImporter.runMain(new String[]{"-w", "VCFVariantImportWorker",
                        "-i", testVCFFile.getAbsolutePath(),
                        "-r", reference.getSGID().getRowKey(),});
            FeatureSet fSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(main, FeatureSet.class);
            long count = fSet.getCount();
            Assert.assertTrue("incorrect number of features in imported VCF, found " + count + " expected 173", count == 173);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    @Test
    public void testNormalVCFImport() {
        testFile(testVCFFile, true);
    }

    @Test
    public void testMissingValueVCFImport() {
        // should handle this normally, all columns but POS look like they could use the missing value (".") according to the VCF specification
        testFile(testVCFFile_missingValues, true);
    }

    @Test
    public void testInvalidVCFImport() {
        // should bug out
        testFile(testVCFFile_invalid, false);
    }

    private void testFile(File testFile, boolean shouldSucceed) {
        BufferedReader in = null;
        BufferedReader controlIn = null;
        try {
            File createTempFile = File.createTempFile("output", "txt");
            Assert.assertTrue("Cannot read VCF file for test", testFile.exists() && testFile.canRead());
            SGID main = SOFeatureImporter.runMain(new String[]{"-w", "VCFVariantImportWorker", "-a", adHocSet.getSGID().getRowKey(),
                        "-i", testFile.getAbsolutePath(), "-o", createTempFile.getAbsolutePath(),
                        "-r", reference.getSGID().getRowKey(), "-s", sequenceOntology.getSGID().getRowKey()
                    });
            if (!shouldSucceed) {
                // would be an error code on the command-line
                Assert.assertTrue(main == null);
                return;
            }

            // do some output comparisons, we may need to sort the results
            VCFDumper.main(new String[]{main.getUuid().toString(), createTempFile.getAbsolutePath()});
            in = new BufferedReader(new FileReader(createTempFile));
            List<String> output = new ArrayList<String>();
            while (in.ready()) {
                String inLine = in.readLine();
                output.add(inLine);
            }
            Collections.sort(output);
            // compare against original VCF file
            controlIn = new BufferedReader(new FileReader(testFile));
            List<String> control = new ArrayList<String>();
            while (controlIn.ready()) {
                control.add(controlIn.readLine());
            }
            Collections.sort(control);
            for (int i = 0; i < output.size(); i++) {
                String[] cLine = control.get(i).split("\t");
                String[] eLine = output.get(i).split("\t");
                Assert.assertTrue("VCF position does not match", cLine[1].equals(eLine[1]));
                Assert.assertTrue("VCF ID does not match", cLine[2].equals(eLine[2]));
                Assert.assertTrue("VCF REF does not match", cLine[3].equals(eLine[3]));
                Assert.assertTrue("VCF ALT does not match", cLine[4].equals(eLine[4]));
                Assert.assertTrue("VCF FILTER does not match", cLine[6].equals(eLine[6]));
                Assert.assertTrue("VCF INFO does not match", cLine[7].equals(eLine[7]));
            }
        } catch (IOException ex) {
            Logger.getLogger(SOFeatureImporterTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.assertTrue("IO Exception", false);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (controlIn != null) {
                    controlIn.close();
                }
            } catch (IOException ex) {
                /**
                 * we don't really care if the tests file closing fails
                 */
                return;
            }
        }
    }
}
