package com.github.seqware.queryengine.system.test;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.system.ReferenceCreator;
import com.github.seqware.queryengine.system.TagSetCreator;
import com.github.seqware.queryengine.system.exporters.QueryVCFDumper;
import com.github.seqware.queryengine.system.importers.OBOImporter;
import com.github.seqware.queryengine.system.importers.SOFeatureImporter;
import com.github.seqware.queryengine.util.SGID;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * System tests for importing and exporting VCF files while using a simple
 * interface to do a few queries
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class QueryVCFDumperTest {

    private static SGID originalSet = null;
    private static TagSet sequenceOntology = null;
    private static Reference reference = null;
    private static TagSet adHocSet = null;
    /** Constant <code>REFERENCE="hg_42"</code> */
    public static final String REFERENCE = "hg_42";
    private static File outputFile;
    /** Constant <code>AD_HOC_TAG_SET="ad_hoc_tagSet"</code> */
    public static final String AD_HOC_TAG_SET = "ad_hoc_tagSet";

    /**
     * <p>setupTests.</p>
     */
    @BeforeClass
    public static void setupTests() {
        // we will need a loaded SO, a reference, and an adhoc tag set to test this sucker

        // load SO
        String curDir = System.getProperty("user.dir");
        File file = new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/so.obo");
        SGID tagSetID = OBOImporter.mainMethod(new String[]{file.getAbsolutePath()});
        sequenceOntology = SWQEFactory.getQueryInterface().getAtomBySGID(TagSet.class, tagSetID);

        try {
            // setup reference
            SGID refID = ReferenceCreator.mainMethod(new String[]{REFERENCE});
            reference = SWQEFactory.getQueryInterface().getAtomBySGID(Reference.class, refID);
        } catch (IllegalArgumentException e) {
            // proceed if this is already created 
            reference = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(REFERENCE, Reference.class);
        }

        try {
            // setup ad hoc tag set
            SGID aSetID = TagSetCreator.mainMethod(new String[]{AD_HOC_TAG_SET});
            adHocSet = SWQEFactory.getQueryInterface().getAtomBySGID(TagSet.class, aSetID);
        } catch (IllegalArgumentException e) {
            // proceed if this is already created 
            adHocSet = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(AD_HOC_TAG_SET, TagSet.class);
        }

        File testVCFFile = new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/consequences_annotated.vcf");

        outputFile = null;
        try {
            outputFile = File.createTempFile("output", "txt");
        } catch (IOException ex) {
            Logger.getLogger(QueryVCFDumperTest.class.getName()).fatal(null, ex);
            Assert.fail("Could not create output for test");
        }
        Assert.assertTrue("Cannot read VCF file for test", testVCFFile.exists() && testVCFFile.canRead());
        List<String> argList = new ArrayList<String>();
        argList.addAll(Arrays.asList(new String[]{"-w", "VCFVariantImportWorker", "-a", adHocSet.getSGID().getRowKey(),
                    "-i", testVCFFile.getAbsolutePath(), "-o", outputFile.getAbsolutePath(),
                    "-r", reference.getSGID().getRowKey(), "-s", sequenceOntology.getSGID().getRowKey()}));
        originalSet = SOFeatureImporter.runMain(argList.toArray(new String[argList.size()]));
        Assert.assertTrue("Could not import VCF for test", originalSet != null);
    }

// We won't be supporting this anymore with the advent of a proper query language     
//    /**
//     * <p>testQueryVCFDumperByClass.</p>
//     */
//    @Test
//    public void testQueryVCFDumperByClass() {
//        File keyValueFile = null;
//        try {
//            keyValueFile = File.createTempFile("keyValue", "txt");
//        } catch (IOException ex) {
//            Logger.getLogger(QueryVCFDumperTest.class.getName()).fatal(null, ex);
//            Assert.fail("Could not create output for test");
//        }
//
//        List<String> argList = new ArrayList<String>();
//        argList.addAll(Arrays.asList(new String[]{"-f", originalSet.getRowKey(),
//                    "-k", keyValueFile.getAbsolutePath(), "-p", "com.github.seqware.queryengine.system.test.queryDumper.VCFDumperParameterExample",
//                    "-o", outputFile.getAbsolutePath()}));
//        Stack<SGID> runMain = QueryVCFDumper.runMain(argList.toArray(new String[argList.size()]));
//
//        Assert.assertTrue("should have four resulting feature sets, had " + runMain.size(), runMain.size() == 4);
//        Assert.assertTrue("starting feature set was incorrect", SWQEFactory.getQueryInterface().getLatestAtomBySGID(runMain.pop(), FeatureSet.class).getCount() == 173);
//        Assert.assertTrue("first query was incorrect", SWQEFactory.getQueryInterface().getLatestAtomBySGID(runMain.pop(), FeatureSet.class).getCount() == 37);
//        Assert.assertTrue("second query was incorrect", SWQEFactory.getQueryInterface().getLatestAtomBySGID(runMain.pop(), FeatureSet.class).getCount() == 10);
//        // tagset will now vary by execution
//        Assert.assertTrue("third query was incorrect", SWQEFactory.getQueryInterface().getLatestAtomBySGID(runMain.pop(), FeatureSet.class).getCount() == 3);
//
//        // test comparison
//        String curDir = System.getProperty("user.dir");
//        SOFeatureImporterTest.matchOutputToControl(outputFile, false, new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/consequences_annotated_dumperControl.vcf"));
//    }

    /**
     * <p>testFirstVCFQueryDumper.</p>
     */
    @Test
    public void testFirstVCFQueryDumper() {
        File keyValueFile = null;
        try {
            keyValueFile = File.createTempFile("keyValue", "txt");
        } catch (IOException ex) {
            Logger.getLogger(QueryVCFDumperTest.class.getName()).fatal(null, ex);
            Assert.fail("Could not create output for test");
        }

        List<String> argList = new ArrayList<String>();
        argList.addAll(Arrays.asList(new String[]{"-f", originalSet.getRowKey(),
                    "-k", keyValueFile.getAbsolutePath(), "-s", "seqid==\"21\" ",
                    "-o", outputFile.getAbsolutePath()}));
        Stack<SGID> runMain = QueryVCFDumper.runMain(argList.toArray(new String[argList.size()]));

        Assert.assertTrue("should have 1 resulting feature set, had " + runMain.size(), runMain.size() == 2);
        Assert.assertTrue("starting feature set was incorrect", SWQEFactory.getQueryInterface().getLatestAtomBySGID(runMain.pop(), FeatureSet.class).getCount() == 173);
        long count = SWQEFactory.getQueryInterface().getLatestAtomBySGID(runMain.pop(), FeatureSet.class).getCount();
        Assert.assertTrue("first query was incorrect, should have 37, found " + count, count == 37);

        // test comparison
        //String curDir = System.getProperty("user.dir");
        //SOFeatureImporterTest.matchOutputToControl(outputFile, false, new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/consequences_annotated_dumperControl.vcf"));
    }
    
    /**
     * <p>testSecondVCFQueryDumper.</p>
     */
    @Test
    public void testSecondVCFQueryDumper() {
        File keyValueFile = null;
        try {
            keyValueFile = File.createTempFile("keyValue", "txt");
        } catch (IOException ex) {
            Logger.getLogger(QueryVCFDumperTest.class.getName()).fatal(null, ex);
            Assert.fail("Could not create output for test");
        }

        List<String> argList = new ArrayList<String>();
        // strange, we have to leave a space in at the end of the query here
        argList.addAll(Arrays.asList(new String[]{"-f", originalSet.getRowKey(),
                    "-k", keyValueFile.getAbsolutePath(), "-s", "seqid==\"21\" && start >= 20000000 && stop <= 30000000",
                    "-o", outputFile.getAbsolutePath()}));
        Stack<SGID> runMain = QueryVCFDumper.runMain(argList.toArray(new String[argList.size()]));

        Assert.assertTrue("should have 1 resulting feature set, had " + runMain.size(), runMain.size() == 2);
        Assert.assertTrue("starting feature set was incorrect", SWQEFactory.getQueryInterface().getLatestAtomBySGID(runMain.pop(), FeatureSet.class).getCount() == 173);
        long count = SWQEFactory.getQueryInterface().getLatestAtomBySGID(runMain.pop(), FeatureSet.class).getCount();
        Assert.assertTrue("second query was incorrect, should have 10, found " + count, count == 10);

        // test comparison
        //String curDir = System.getProperty("user.dir");
        //SOFeatureImporterTest.matchOutputToControl(outputFile, false, new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/consequences_annotated_dumperControl.vcf"));
    }
    
    /**
     * <p>testThirdVCFQueryDumper.</p>
     */
    @Test
    public void testThirdVCFQueryDumper() {
        File keyValueFile = null;
        try {
            keyValueFile = File.createTempFile("keyValue", "txt");
        } catch (IOException ex) {
            Logger.getLogger(QueryVCFDumperTest.class.getName()).fatal(null, ex);
            Assert.fail("Could not create output for test");
        }

        List<String> argList = new ArrayList<String>();
        // strange, we have to leave a space in at the end of the query here
        argList.addAll(Arrays.asList(new String[]{"-f", originalSet.getRowKey(),
                    "-k", keyValueFile.getAbsolutePath(), "-s", "seqid==\"21\" && start >= 20000000 && stop <= 30000000 && tagOccurrence(\""+QueryVCFDumperTest.AD_HOC_TAG_SET+"\",\"non_synonymous_codon\")",
                    "-o", outputFile.getAbsolutePath()}));
        Stack<SGID> runMain = QueryVCFDumper.runMain(argList.toArray(new String[argList.size()]));

        Assert.assertTrue("should have 1 resulting feature set, had " + runMain.size(), runMain.size() == 2);
        Assert.assertTrue("starting feature set was incorrect", SWQEFactory.getQueryInterface().getLatestAtomBySGID(runMain.pop(), FeatureSet.class).getCount() == 173);
        long count = SWQEFactory.getQueryInterface().getLatestAtomBySGID(runMain.pop(), FeatureSet.class).getCount();
        Assert.assertTrue("third query was incorrect, should have 3, found " + count, count == 3);

        // test comparison
        String curDir = System.getProperty("user.dir");
        SOFeatureImporterTest.matchOutputToControl(outputFile, false, new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/consequences_annotated_dumperControl.vcf"));
    }
}
