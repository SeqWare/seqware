/**
 * TODO: need to add tag testing here too
 */
package net.sourceforge.seqware.queryengine.backend;

import org.testng.annotations.*;
import org.testng.Assert;

import java.io.*;
import java.util.HashMap;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Consequence;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Feature;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.util.iterators.CursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.LocatableSecondaryCursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.SecondaryCursorIterator;

/**
 * <p>BerkeleyDBTest class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
@Test(groups="berkeleydb")
public class BerkeleyDBTest {

    private static final String TEST_DB_PATH = System.getProperty("QE_BERKELEYDB_PATH");
    private static final int BIN_SIZE = Integer.parseInt(System.getProperty("QE_BERKELEYDB_COVERAGE_BIN_SIZE"));
    private static final int CACHE_SIZE = Integer.parseInt(System.getProperty("QE_BERKELEYDB_CACHE_SIZE"));

    @BeforeTest(enabled = true)
    void setup() {
        // Clear out any existing files if the directory exists
        System.out.println("Cleaning out: " + TEST_DB_PATH);
        File tmpDir = new File(TEST_DB_PATH);
        if (tmpDir.exists()) {
            for (final File f : tmpDir.listFiles()) {
                f.delete();
            }
        } else {
            tmpDir.mkdir();
        }
    }

    @AfterTest(enabled = true)
    void tearDown() {
        File tmpDir = new File(TEST_DB_PATH);
        for (final File f : tmpDir.listFiles()) {
            f.delete();
        }
    }

    /**
     * <p>testFeatureDBReadingAndWriting.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test(enabled = true)
    public void testFeatureDBReadingAndWriting() throws Exception {

        // settings object
        SeqWareSettings settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(TEST_DB_PATH);
        settings.setCacheSize(CACHE_SIZE);
        settings.setCreateMismatchDB(true);
        settings.setCreateConsequenceAnnotationDB(true);
        settings.setCreateDbSNPAnnotationDB(true);
        settings.setCreateCoverageDB(true);
        settings.setReadOnly(false);

        // create factory and use it to create store object
        BerkeleyDBFactory factory = new BerkeleyDBFactory();
        Assert.assertNotNull(factory);
        BerkeleyDBStore store = factory.getStore(settings);
        Assert.assertNotNull(store);

        // IUPAC nucleotide codes: http://www.bioinformatics.org/sms/iupac.html

        // create mismatch SNV entry and add it to the database
        // this sample is based on the 5' UTR and first exon of of ZNF74
        // http://genome.ucsc.edu/cgi-bin/hgGene?hgg_gene=uc010gsm.1
        // http://genome.ucsc.edu/cgi-bin/hgc?hgsid=140022007&o=19078479&t=19092752&g=refGene&i=NM_003426&c=chr22&l=19078916&r=19078949&db=hg18&pix=800
        Feature f = new Feature();
        f.setContig("chr22");
        f.setStartPosition(19078852);
        f.setStopPosition(19078952);
        f.setName("ZNF74");
        f.setScore(33);
        f.setStrand('+');
        // BED-specific
        f.setThickStart(19078918);
        f.setThickEnd(19078951);
        f.setItemRgb("204,51,51");
        f.setBlockCount(2);
        int[] blockSizes = new int[2];
        int[] blockStarts = new int[2];
        blockSizes[0] = 3;
        blockSizes[1] = 4;
        blockStarts[0] = 5;
        blockStarts[1] = 10;
        f.setBlockSizes(blockSizes);
        f.setBlockStarts(blockStarts);
        // GFF-specific
        f.setFeature("CDS");
        f.setFrame((byte) 1);
        f.setGroup("uc010gsm.1");
        // GTF-specific
        f.setGeneId("ZNF74");
        f.setTranscriptId("uc010gsm.1");

        // now save to db
        store.putFeature(f);
        System.out.println("Feature ID: " + f.getId());

        // close the store
        store.close();

        // now reopen the database and check that the round trip works
        settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(TEST_DB_PATH);
        settings.setCacheSize(CACHE_SIZE);//commented out above
        settings.setCreateMismatchDB(false);
        settings.setCreateConsequenceAnnotationDB(false);
        settings.setCreateDbSNPAnnotationDB(false);
        settings.setCreateCoverageDB(false);
        settings.setReadOnly(true);

        // create factory and use it to open store object
        factory = new BerkeleyDBFactory();
        Assert.assertNotNull(factory);
        store = factory.getStore(settings);
        Assert.assertNotNull(store);

        // get iterator of mismatches
        SeqWareIterator featureIt = store.getFeatures();
        Assert.assertNotNull(featureIt);

        // iterate over contents
        while (featureIt.hasNext()) {

            f = null;
            f = (Feature) featureIt.next();
            Assert.assertNotNull(f, "It's null!");
            System.out.println("Feature ID from ITERATOR: " + f.getId());
            if ("1".equals(f.getId())) {
                System.out.println("Validating Feature");

                Assert.assertEquals(f.getContig(), "chr22");
                Assert.assertEquals(f.getStartPosition(), 19078852);
                Assert.assertEquals(f.getStopPosition(), 19078952);
                Assert.assertEquals(f.getName(), "ZNF74");
                Assert.assertEquals(f.getScore(), 33);
                Assert.assertEquals(f.getStrand(), '+');
                Assert.assertEquals(f.getThickStart(), 19078918);
                Assert.assertEquals(f.getThickEnd(), 19078951);
                Assert.assertEquals(f.getItemRgb(), "204,51,51");
                Assert.assertEquals(f.getBlockCount(), 2);
                Assert.assertEquals(f.getBlockSizes()[0], 3);
                Assert.assertEquals(f.getBlockSizes()[1], 4);
                Assert.assertEquals(f.getBlockStarts()[0], 5);
                Assert.assertEquals(f.getBlockStarts()[1], 10);
                Assert.assertEquals(f.getFeature(), "CDS");
                Assert.assertEquals(f.getFrame(), (byte) 1);
                Assert.assertEquals(f.getGroup(), "uc010gsm.1");
                Assert.assertEquals(f.getGeneId(), "ZNF74");
                Assert.assertEquals(f.getTranscriptId(), "uc010gsm.1");

            } else {
                Assert.fail("Got back a feature with a featureID outside the excpected range!");
            }
        }
        // close the iterator
        featureIt.close();

        // close the store
        store.close();

    }

    //
    /**
     * <p>testMismatchDBReadingAndWriting.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test(dependsOnMethods = {"testFeatureDBReadingAndWriting"}, enabled = true)
    public void testMismatchDBReadingAndWriting() throws Exception {

        // settings object
        SeqWareSettings settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(TEST_DB_PATH);
        settings.setCacheSize(CACHE_SIZE);//commented out above
        settings.setCreateMismatchDB(true);
        settings.setCreateConsequenceAnnotationDB(true);
        settings.setCreateDbSNPAnnotationDB(true);
        settings.setCreateCoverageDB(true);
        settings.setReadOnly(false);

        // create factory and use it to create store object
        BerkeleyDBFactory factory = new BerkeleyDBFactory();
        Assert.assertNotNull(factory);
        BerkeleyDBStore store = factory.getStore(settings);
        Assert.assertNotNull(store);

        // IUPAC nucleotide codes: http://www.bioinformatics.org/sms/iupac.html

        // create mismatch SNV entry and add it to the database
        // this sample is based on the first codon (M) of ZNF74
        // http://genome.ucsc.edu/cgi-bin/hgGene?hgg_gene=uc010gsm.1
        // http://genome.ucsc.edu/cgi-bin/hgc?hgsid=140022007&o=19078479&t=19092752&g=refGene&i=NM_003426&c=chr22&l=19078916&r=19078949&db=hg18&pix=800
        Variant m = new Variant();
        m.setContig("chr22");
        m.setStartPosition(19078919);
        m.setStopPosition(19078920);
        m.setReferenceBase("T");
        m.setCalledBase("C");
        m.setConsensusBase("Y");
        m.setReadCount(23);
        m.setReadBases(",,,...CCCCCCCCCCccccccc");
        // this is bogus, doesn't really match up with average qualities below
        m.setBaseQualities("<<<<<<<<<<<<<<<<<<<<<<<");
        m.setCalledBaseCount(17);
        m.setCalledBaseCountForward(10);
        m.setCalledBaseCountReverse(7);
        m.setReferenceCallQuality((float) 15);
        m.setConsensusCallQuality((float) 16);
        m.setMaximumMappingQuality((float) 120);
        m.setReferenceMaxSeqQuality((float) 121);
        m.setReferenceAveSeqQuality((float) 122);
        m.setConsensusMaxSeqQuality((float) 123);
        m.setConsensusAveSeqQuality((float) 124);
        m.setType(Variant.SNV);
        m.setZygosity(Variant.HETEROZYGOUS);

        // now save in the db
        store.putMismatch(m);
        System.out.println("Variant ID: " + m.getId());

        // create a mismatch insertion entry and add it to the database
        // this is an insertion in the same gene as above, first position
        // of the 5th codon 
        // wt CTGC, mut CT-C-GC
        // TODO: did I get 
        m = new Variant();
        m.setContig("chr22");
        m.setStartPosition(19078929);
        m.setStopPosition(19078930);
        m.setReferenceBase("-");
        m.setCalledBase("C");
        m.setConsensusBase("*/+C");
        m.setReadCount(13);
        m.setReadBases(",,,...+C+C+C+c+c+c+c");
        // this is bogus, doesn't really match up with average qualities below
        m.setBaseQualities("<<<<<<<<<<<<<");
        m.setCallOne("*");
        m.setCallTwo("+C");
        m.setReadsSupportingCallOne(6);
        m.setReadsSupportingCallTwo(7);
        m.setReadsSupportingCallThree(0);
        m.setCalledBaseCount(7);
        m.setCalledBaseCountForward(3);
        m.setCalledBaseCountReverse(4);
        m.setReferenceCallQuality((float) 16);
        m.setConsensusCallQuality((float) 17);
        m.setMaximumMappingQuality((float) 121);
        m.setReferenceMaxSeqQuality((float) 122);
        m.setReferenceAveSeqQuality((float) 123);
        m.setConsensusMaxSeqQuality((float) 124);
        m.setConsensusAveSeqQuality((float) 125);
        m.setType(Variant.INSERTION);
        m.setZygosity(Variant.HETEROZYGOUS);

        // now save in the db
        store.putMismatch(m);
        System.out.println("Variant ID: " + m.getId());

        // create a mismatch deletion entry and add it to the database
        /*
         * http://genome.ucsc.edu/cgi-bin/hgGene?hgg_gene=uc001kfb.1&hgg_prot=P60484&hgg_chrom=chr10&hgg_start=89613174&hgg_end=89718512&hgg_type=knownGene&db=hg18&hgsid=140022007
         * pten deletion in third codon GACA-G-CCAT
         */
        m = new Variant();
        m.setContig("chr10");
        m.setStartPosition(89614212);
        m.setStopPosition(89614213);
        m.setReferenceBase("G");
        m.setCalledBase("-");
        m.setConsensusBase("-G/-G");
        m.setReadCount(13);
        m.setReadBases(",,,...-G-G-G-g-g-g-g");
        // this is bogus, doesn't really match up with average qualities below
        m.setBaseQualities("<<<<<<<<<<<<<");
        m.setCallOne("*");
        m.setCallTwo("-G");
        m.setReadsSupportingCallOne(6);
        m.setReadsSupportingCallTwo(7);
        m.setReadsSupportingCallThree(0);
        m.setCalledBaseCount(7);
        m.setCalledBaseCountForward(3);
        m.setCalledBaseCountReverse(4);
        m.setReferenceCallQuality((float) 18);
        m.setConsensusCallQuality((float) 19);
        m.setMaximumMappingQuality((float) 122);
        m.setReferenceMaxSeqQuality((float) 123);
        m.setReferenceAveSeqQuality((float) 124);
        m.setConsensusMaxSeqQuality((float) 125);
        m.setConsensusAveSeqQuality((float) 126);
        m.setType(Variant.DELETION);
        m.setZygosity(Variant.HOMOZYGOUS);

        // now save in the db
        store.putMismatch(m);
        System.out.println("Variant ID: " + m.getId());

        // close the store
        store.close();

        // now reopen the database and check that the round trip works
        settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(TEST_DB_PATH);
        settings.setCacheSize(CACHE_SIZE);//commented out above
        settings.setCreateMismatchDB(false);
        settings.setCreateConsequenceAnnotationDB(false);
        settings.setCreateDbSNPAnnotationDB(false);
        settings.setCreateCoverageDB(false);
        settings.setReadOnly(true);

        // create factory and use it to open store object
        factory = new BerkeleyDBFactory();
        Assert.assertNotNull(factory);
        store = factory.getStore(settings);
        Assert.assertNotNull(store);

        // get iterator of mismatches
        SeqWareIterator mismatchIt = store.getMismatches();
        Assert.assertNotNull(mismatchIt);

        // iterate over contents
        while (mismatchIt.hasNext()) {

            m = null;
            m = (Variant) mismatchIt.next();
            System.out.println("Variant ID from ITERATOR: " + m.getId());
            Assert.assertNotNull(m, "It's null!");
            if ("1".equals(m.getId())) {
                System.out.println("Validating SNV");
                Assert.assertEquals(m.getContig(), "chr22");
                Assert.assertEquals(m.getStartPosition(), 19078919);
                Assert.assertEquals(m.getStopPosition(), 19078920);
                Assert.assertEquals(m.getReferenceBase(), "T");
                Assert.assertEquals(m.getCalledBase(), "C");
                Assert.assertEquals(m.getConsensusBase(), "Y");
                Assert.assertEquals(m.getReadCount(), 23);
                Assert.assertEquals(m.getReadBases(), ",,,...CCCCCCCCCCccccccc");
                Assert.assertEquals(m.getBaseQualities(), "<<<<<<<<<<<<<<<<<<<<<<<");
                Assert.assertEquals(m.getCalledBaseCount(), 17);
                Assert.assertEquals(m.getCalledBaseCountForward(), 10);
                Assert.assertEquals(m.getCalledBaseCountReverse(), 7);
                Assert.assertEquals(m.getReferenceCallQuality(), (float) 15);
                Assert.assertEquals(m.getConsensusCallQuality(), (float) 16);
                Assert.assertEquals(m.getMaximumMappingQuality(), (float) 120);
                Assert.assertEquals(m.getReferenceMaxSeqQuality(), (float) 121);
                Assert.assertEquals(m.getReferenceAveSeqQuality(), (float) 122);
                Assert.assertEquals(m.getConsensusMaxSeqQuality(), (float) 123);
                Assert.assertEquals(m.getConsensusAveSeqQuality(), (float) 124);
                Assert.assertEquals(m.getType(), Variant.SNV);
                Assert.assertEquals(m.getZygosity(), Variant.HETEROZYGOUS);
            } else if ("2".equals(m.getId())) {
                System.out.println("Validating Insertion");
                Assert.assertEquals(m.getContig(), "chr22");
                Assert.assertEquals(m.getStartPosition(), 19078929);
                Assert.assertEquals(m.getStopPosition(), 19078930);
                Assert.assertEquals(m.getReferenceBase(), "-");
                Assert.assertEquals(m.getCalledBase(), "C");
                Assert.assertEquals(m.getConsensusBase(), "*/+C");
                Assert.assertEquals(m.getReadCount(), 13);
                Assert.assertEquals(m.getReadBases(), ",,,...+C+C+C+c+c+c+c");
                Assert.assertEquals(m.getBaseQualities(), "<<<<<<<<<<<<<");
                Assert.assertEquals(m.getCallOne(), "*");
                Assert.assertEquals(m.getCallTwo(), "+C");
                Assert.assertEquals(m.getReadsSupportingCallOne(), 6);
                Assert.assertEquals(m.getReadsSupportingCallTwo(), 7);
                Assert.assertEquals(m.getReadsSupportingCallThree(), 0);
                Assert.assertEquals(m.getCalledBaseCount(), 7);
                Assert.assertEquals(m.getCalledBaseCountForward(), 3);
                Assert.assertEquals(m.getCalledBaseCountReverse(), 4);
                Assert.assertEquals(m.getReferenceCallQuality(), (float) 16);
                Assert.assertEquals(m.getConsensusCallQuality(), (float) 17);
                Assert.assertEquals(m.getMaximumMappingQuality(), (float) 121);
                Assert.assertEquals(m.getReferenceMaxSeqQuality(), (float) 122);
                Assert.assertEquals(m.getReferenceAveSeqQuality(), (float) 123);
                Assert.assertEquals(m.getConsensusMaxSeqQuality(), (float) 124);
                Assert.assertEquals(m.getConsensusAveSeqQuality(), (float) 125);
                Assert.assertEquals(m.getType(), Variant.INSERTION);
                Assert.assertEquals(m.getZygosity(), Variant.HETEROZYGOUS);
            } else if ("3".equals(m.getId())) {
                System.out.println("Validating Deletion");
                Assert.assertEquals(m.getContig(), "chr10");
                Assert.assertEquals(m.getStartPosition(), 89614212);
                Assert.assertEquals(m.getStopPosition(), 89614213);
                Assert.assertEquals(m.getReferenceBase(), "G");
                Assert.assertEquals(m.getCalledBase(), "-");
                Assert.assertEquals(m.getConsensusBase(), "-G/-G");
                Assert.assertEquals(m.getReadCount(), 13);
                Assert.assertEquals(m.getReadBases(), ",,,...-G-G-G-g-g-g-g");
                Assert.assertEquals(m.getBaseQualities(), "<<<<<<<<<<<<<");
                Assert.assertEquals(m.getCallOne(), "*");
                Assert.assertEquals(m.getCallTwo(), "-G");
                Assert.assertEquals(m.getReadsSupportingCallOne(), 6);
                Assert.assertEquals(m.getReadsSupportingCallTwo(), 7);
                Assert.assertEquals(m.getReadsSupportingCallThree(), 0);
                Assert.assertEquals(m.getCalledBaseCount(), 7);
                Assert.assertEquals(m.getCalledBaseCountForward(), 3);
                Assert.assertEquals(m.getCalledBaseCountReverse(), 4);
                Assert.assertEquals(m.getReferenceCallQuality(), (float) 18);
                Assert.assertEquals(m.getConsensusCallQuality(), (float) 19);
                Assert.assertEquals(m.getMaximumMappingQuality(), (float) 122);
                Assert.assertEquals(m.getReferenceMaxSeqQuality(), (float) 123);
                Assert.assertEquals(m.getReferenceAveSeqQuality(), (float) 124);
                Assert.assertEquals(m.getConsensusMaxSeqQuality(), (float) 125);
                Assert.assertEquals(m.getConsensusAveSeqQuality(), (float) 126);
                Assert.assertEquals(m.getType(), Variant.DELETION);
                Assert.assertEquals(m.getZygosity(), Variant.HOMOZYGOUS);
            } else {
                Assert.fail("Got back a mismatch with a MismatchID outside the excpected range!");
            }
        }
        // close the iterator
        mismatchIt.close();

        // close the store
        store.close();

    }

    /**
     * <p>testMismatchAnnotationTagDBReadingAndWriting.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test(dependsOnMethods = {"testMismatchDBReadingAndWriting"}, enabled = true)
    public void testMismatchAnnotationTagDBReadingAndWriting() throws Exception {
        // settings object
        SeqWareSettings settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(TEST_DB_PATH);
        settings.setCacheSize(CACHE_SIZE);
        settings.setCreateMismatchDB(false);
        settings.setCreateConsequenceAnnotationDB(false);
        settings.setCreateDbSNPAnnotationDB(false);
        settings.setCreateCoverageDB(false);
        settings.setReadOnly(false);

        // create factory and use it to create store object
        BerkeleyDBFactory factory = new BerkeleyDBFactory();
        Assert.assertNotNull(factory);
        BerkeleyDBStore store = factory.getStore(settings);
        Assert.assertNotNull(store);

        // get a mismatch
        Variant m = store.getMismatch("1");
        Assert.assertNotNull(m);

        // add an annotation
        m.addTag("is_dbSNP", "rs2306737");

        // save it back to the db
        store.putMismatch(m);

        // close
        store.close();
        store = null;

        // change settings to readonly
        settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(TEST_DB_PATH);
        settings.setCacheSize(CACHE_SIZE);//commented out above
        settings.setCreateMismatchDB(false);
        settings.setCreateConsequenceAnnotationDB(false);
        settings.setCreateDbSNPAnnotationDB(false);
        settings.setCreateCoverageDB(false);
        settings.setReadOnly(true);

        // open store
        store = factory.getStore(settings);
        Assert.assertNotNull(store);

        // get mismatch
        m = store.getMismatch("1");
        Assert.assertNotNull(m);

        // pull back tags
        HashMap<String, String> tags = m.getTags();
        Assert.assertEquals(tags.size(), 1);
        Assert.assertEquals(m.getTagValue("is_dbSNP"), "rs2306737");
        //System.out.println("TAG: "+m.getTagValue("is_dbSNP"));

        // get mismatch that doesn't have a tag
        m = store.getMismatch("2");
        Assert.assertNotNull(m);
        tags = m.getTags();
        Assert.assertEquals(tags.size(), 0);
        Assert.assertNull(m.getTagValue("is_dbSNP"));
        //System.out.println("TAG: "+m.getTagValue("is_dbSNP"));

        // now can I lookup mismatches based on tags?
        SecondaryCursorIterator mIt = store.getMismatchesByTag("is_dbSNP");
        while (mIt.hasNext()) {
            Variant currM = (Variant) mIt.next();
            Assert.assertEquals(currM.getId(), "1");
            Assert.assertEquals(currM.getTagValue("is_dbSNP"), "rs2306737");
            //System.out.println("Variant: "+currM.getId()+" tag "+currM.getTagValue("is_dbSNP"));
        }

        store.close();

        // now open as readwrite and add a bunch of new tags
        settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(TEST_DB_PATH);
        settings.setCacheSize(CACHE_SIZE);//commented out above
        settings.setCreateMismatchDB(false);
        settings.setCreateConsequenceAnnotationDB(false);
        settings.setCreateDbSNPAnnotationDB(false);
        settings.setCreateCoverageDB(false);
        settings.setReadOnly(false);

        // open store
        store = factory.getStore(settings);
        Assert.assertNotNull(store);

        // add two tags
        m = store.getMismatch("2");
        Assert.assertNotNull(m);
        Assert.assertEquals(m.getTags().size(), 0);
        m.addTag("is_dbSNP", "rs1281732");
        m.addTag("nonsynonymous", null);
        store.putMismatch(m);

        // add one tag
        m = store.getMismatch("3");
        Assert.assertNotNull(m);
        Assert.assertEquals(m.getTags().size(), 0);
        m.addTag("frameshift", "testing");
        store.putMismatch(m);

        store.close();

        // open readonly
        settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(TEST_DB_PATH);
        settings.setCacheSize(CACHE_SIZE);//commented out above
        settings.setCreateMismatchDB(false);
        settings.setCreateConsequenceAnnotationDB(false);
        settings.setCreateDbSNPAnnotationDB(false);
        settings.setCreateCoverageDB(false);
        settings.setReadOnly(true);

        // open store
        store = factory.getStore(settings);
        Assert.assertNotNull(store);

        // now try to read these back
        mIt = store.getMismatchesByTag("is_dbSNP");
        int count = 0;
        System.out.println("Testing tag retrieval: is_dbSNP");
        while (mIt.hasNext()) {
            Variant currM = (Variant) mIt.next();
            Assert.assertNotNull(currM);
            if (currM != null) {
                count++;
            }
            System.out.println(" Variant ID " + currM.getId() + " snp " + currM.getTagValue("is_dbSNP"));
            for (String key : currM.getTags().keySet()) {
                System.out.println("  my key: " + key + " value: " + currM.getTagValue(key));
            }
            Assert.assertTrue("1".equals(currM.getId()) || "2".equals(currM.getId()));
            Assert.assertFalse("3".equals(currM.getId()));
            Assert.assertNotNull(currM.getTagValue("is_dbSNP"));

        }
        Assert.assertEquals(count, 2);

        // test another tag
        mIt = store.getMismatchesByTag("frameshift");
        count = 0;
        System.out.println("Testing tag retrieval: frameshift");
        while (mIt.hasNext()) {
            Variant currM = (Variant) mIt.next();
            Assert.assertNotNull(currM);
            if (currM != null) {
                count++;
            }
            System.out.println(" Variant ID " + currM.getId() + " snp " + currM.getTagValue("frameshift"));
            for (String key : currM.getTags().keySet()) {
                System.out.println("  my key: " + key + " value: " + currM.getTagValue(key));
            }
            Assert.assertTrue("3".equals(currM.getId()));
            Assert.assertNotNull(currM.getTagValue("frameshift"));
        }
        Assert.assertEquals(count, 1);

        // test another tag
        mIt = store.getMismatchesByTag("nonsynonymous");
        count = 0;
        System.out.println("Testing tag retrieval: nonsynonymous");
        while (mIt.hasNext()) {
            Variant currM = (Variant) mIt.next();
            Assert.assertNotNull(currM);
            if (currM != null) {
                count++;
            }
            System.out.println(" Variant ID " + currM.getId() + " snp " + currM.getTagValue("nonsynonymous"));
            for (String key : currM.getTags().keySet()) {
                System.out.println("  my key: " + key + " value: " + currM.getTagValue(key));
            }
            Assert.assertTrue("2".equals(currM.getId()));
            Assert.assertNull(currM.getTagValue("nonsynonymous"));
        }
        Assert.assertEquals(count, 1);

        store.close();

    }

    /**
     * <p>testMismatchContigPositionDBReadingAndWriting.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test(dependsOnMethods = {"testMismatchAnnotationTagDBReadingAndWriting"}, enabled = true)
    public void testMismatchContigPositionDBReadingAndWriting() throws Exception {

        // change settings to readonly
        SeqWareSettings settings = new SeqWareSettings();
        settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(TEST_DB_PATH);
        settings.setCacheSize(CACHE_SIZE);//commented out above
        settings.setCreateMismatchDB(false);
        settings.setCreateConsequenceAnnotationDB(false);
        settings.setCreateDbSNPAnnotationDB(false);
        settings.setCreateCoverageDB(false);
        settings.setReadOnly(true);

        // create factory and use it to create store object
        BerkeleyDBFactory factory = new BerkeleyDBFactory();
        Assert.assertNotNull(factory);
        BerkeleyDBStore store = factory.getStore(settings);
        Assert.assertNotNull(store);

        // FIXME: why do I get a null mismatch here!?!?!?!?!?!?!?!?!?!!
        LocatableSecondaryCursorIterator cci = store.getMismatches("chr22", 19078919, 19078920);
        int count = 0;
        while (cci.hasNext()) {
            Variant m = (Variant) cci.next();
            if (m != null) {
                count++;
                Assert.assertEquals(m.getId(), "1");
            }
        }
        Assert.assertEquals(count, 1);

        // FIXME: this doesn't work (but should!)
        //cci = store.getMismatches("chr22", 19078919, 19078930);
        // this works
        cci = store.getMismatches("chr22", 19078918, 19078930);
        count = 0;
        while (cci.hasNext()) {
            count++;
            Variant m = (Variant) cci.next();
            if (m != null) {
                Assert.assertTrue("1".equals(m.getId()) || "2".equals(m.getId()));
            }
        }
        Assert.assertEquals(count, 2);

        CursorIterator sci = store.getMismatches();
        count = 0;
        while (sci.hasNext()) {
            count++;
            Variant m = (Variant) sci.next();
            // this should be ordered by chromosome and position, so should see in this order
            if (count == 1) {
                Assert.assertTrue("3".equals(m.getId()));
            }
            if (count == 2) {
                Assert.assertTrue("1".equals(m.getId()));
            }
            if (count == 3) {
                Assert.assertTrue("2".equals(m.getId()));
            }
        }
        Assert.assertEquals(count, 3);

        // close
        store.close();

    }

    /**
     * <p>testCoverageDBReadingAndWriting.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test(dependsOnMethods = {"testMismatchAnnotationTagDBReadingAndWriting"}, enabled = true)
    public void testCoverageDBReadingAndWriting() throws Exception {

        // settings object
        SeqWareSettings settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(TEST_DB_PATH);
        settings.setCacheSize(CACHE_SIZE);//commented out above
        settings.setCreateMismatchDB(false);
        settings.setCreateConsequenceAnnotationDB(false);
        settings.setCreateDbSNPAnnotationDB(false);
        settings.setCreateCoverageDB(false);
        settings.setReadOnly(false);

        // create factory and use it to create store object
        BerkeleyDBFactory factory = new BerkeleyDBFactory();
        Assert.assertNotNull(factory);
        BerkeleyDBStore store = factory.getStore(settings);
        Assert.assertNotNull(store);

        // bin size
        int binSize = BIN_SIZE;//commented out above

        // Now create coverage data and perform roundtrip
        // adding coverage for chr22
        Coverage c = null;
        int currBin = 0;

        // used to track the correct coverage at a given position
        HashMap<Integer, Integer> correctCov = new HashMap<Integer, Integer>();

        // starting same location as SNV above
        String contig = "chr22";
        Integer startPos = 19078919;
        Integer stopPos = 19078920;
        Integer count = 23;

        // create 1K coverage entries starting at above position
        for (int i = 0; i < 10; i++) {
            // loop and add a coverage for each
            Integer currStartPos = startPos + i;
            Integer currStopPos = stopPos + i;
            int currCount = count + i;
            // figure out the bin
            int bin = currStartPos / binSize;

            if (c == null || bin != currBin) {
                if (c != null) {
                    // save the previous cov obj
                    store.putCoverage(c);
                }
                c = new Coverage();
                currBin = bin;
                c.setContig(contig);
                c.setStartPosition(bin * binSize);
                c.setStopPosition((bin * binSize) + (binSize - 1));
            }
            c.putCoverage(currStartPos, currCount);
            // keep track of correct answer
            correctCov.put(currStartPos, currCount);
        }
        // save the last one
        if (c != null) {
            store.putCoverage(c);
        }
        // close the store
        store.close();

        // now open the store and make sure the coverage comes back correctly
        settings.setReadOnly(true);
        store = factory.getStore(settings);
        Assert.assertNotNull(store);

        // FIXME: The key is you have to request the coverage objects at or outside the range defined by the bin size.
        // A request for a range contained within a bin will produce 0 results.  This is unfortunate, the 
        // client should just set the bin size once and then the system should properly handle ranges
        // and trim the results as needed.
        //ContigCursorIterator cit = store.getCoverages("chr22", 19078919, 19088919); // doesn't work
        //ContigCursorIterator cit = store.getCoverages("chr22", 1, 20088919); // works
        //ContigCursorIterator cit = store.getCoverages("chr22", 19078001, 19078999); // doesn't work
        //ContigCursorIterator cit = store.getCoverages("chr22", 19077000, 19079999); //works
        //ContigCursorIterator cit = store.getCoverages("chr22", 19078000, 19078999); //works
        LocatableSecondaryCursorIterator cit = store.getCoverages("chr22", 19078000, 19079999); //works, returns two coverage objects

        System.out.println("Validating Coverage");
        while (cit.hasNext()) {
            Coverage currCov = (Coverage) cit.next();
            // FIXME: this really shouldn't happen but does :-(
            if (currCov != null) {
                HashMap<Integer, Integer> currCovMap = currCov.getCoverage();
                for (Integer pos : currCovMap.keySet()) {
                    Assert.assertEquals(currCovMap.get(pos), correctCov.get(pos));
                    //System.out.println("Stored answer: "+pos+":"+currCovMap.get(pos)+" Real answer: "+pos+":"+correctCov.get(pos));
                }
            }
        }
        cit.close();
        store.close();

    }

    /**
     * <p>testConsequenceAnnotationDBReadingAndWriting.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test(dependsOnMethods = {"testCoverageDBReadingAndWriting"}, enabled = true)
    public void testConsequenceAnnotationDBReadingAndWriting() throws Exception {
        System.out.println("Validating Consequence Annotation DB");
        // settings object
        SeqWareSettings settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(TEST_DB_PATH);
        settings.setCacheSize(CACHE_SIZE);
        settings.setCreateMismatchDB(false);
        settings.setCreateConsequenceAnnotationDB(false);
        settings.setCreateDbSNPAnnotationDB(false);
        settings.setCreateCoverageDB(false);
        settings.setReadOnly(false);

        // create factory and use it to create store object
        BerkeleyDBFactory factory = new BerkeleyDBFactory();
        Assert.assertNotNull(factory);
        BerkeleyDBStore store = factory.getStore(settings);
        Assert.assertNotNull(store);

        // look up the mismatch
        Variant m = store.getMismatch("1");

        // now create a new consequence object
        Consequence c = new Consequence();

        // now fill in with details
        c.setStrand(Consequence.PLUS_STRAND);
        c.setMismatchId("1");
        c.setMismatchType(Variant.SNV);
        c.setMismatchChr("chr22");
        c.setMismatchStart(19078919);
        c.setMismatchStop(19078920);
        c.setMismatchCodonPosition(2);
        c.setMismatchCodonChange("ATG->ACG");
        c.setMismatchAminoAcidChange("M->T");
        c.getTags().put("start-codon-loss", null);
        c.getTags().put("coding-nonsynonymous", null);
        c.setMismatchAAChangeBlosumScore(3);
        // NOTE: these aren't really the sequences but I did modify the first codon
        c.setGenomicSequence("ATGGTGACTGAATTCATTTTTCTGGGTCTCTCTGATTCTCAGGAACTCCAGACCTTCCTATTTATGTTGTTTTTTGTATTCTATGGAGGAATCGTGTTTGGAAACCTTCTTATTGTCATAACAGTGGTATCTGACTCCCACCTTCACTCTCCCATGTACTTCCTGCTAGCCAACCTCTCACTCATTGATCTGTCTCTGTCTTCAGTCACAGCCCCCAAGATGATTACTGACTTTTTCAGCCAGCGCAAAGTCATCTCTTTCAAGGGCTGCCTTGTTCAGATATTTCTCCTTCACTTCTTTGGTGGGAGTGAGATGGTGATCCTCATAGCCATGGGCTTTGACAGATATATAGCAATATGCAAGCCCCTACACTACACTACAATTATGTGTGGCAACGCATGTGTCGGCATTATGGCTGTCACATGGGGAATTGGCTTTCTCCATTCGGTGAGCCAGTTGGCGTTTGCCGTGCACTTACTCTTCTGTGGTCCCAATGAGGTCGATAGTTTTTATTGTGACCTTCCTAGGGTAATCAAACTTGCCTGTACAGATACCTACAGGCTAGATATTATGGTCATTGCTAACAGTGGTGTGCTCACTGTGTGTTCTTTTGTTCTTCTAATCATCTCATACACTATCATCCTAATGACCATCCAGCATCGCCCTTTAGATAAGTCGTCCAAAGCTCTGTCCACTTTGACTGCTCACATTACAGTAGTTCTTTTGTTCTTTGGACCA");
        c.setMutatedGenomicSequence("ACGGTGACTGAATTCATTTTTCTGGGTCTCTCTGATTCTCAGGAACTCCAGACCTTCCTATTTATGTTGTTTTTTGTATTCTATGGAGGAATCGTGTTTGGAAACCTTCTTATTGTCATAACAGTGGTATCTGACTCCCACCTTCACTCTCCCATGTACTTCCTGCTAGCCAACCTCTCACTCATTGATCTGTCTCTGTCTTCAGTCACAGCCCCCAAGATGATTACTGACTTTTTCAGCCAGCGCAAAGTCATCTCTTTCAAGGGCTGCCTTGTTCAGATATTTCTCCTTCACTTCTTTGGTGGGAGTGAGATGGTGATCCTCATAGCCATGGGCTTTGACAGATATATAGCAATATGCAAGCCCCTACACTACACTACAATTATGTGTGGCAACGCATGTGTCGGCATTATGGCTGTCACATGGGGAATTGGCTTTCTCCATTCGGTGAGCCAGTTGGCGTTTGCCGTGCACTTACTCTTCTGTGGTCCCAATGAGGTCGATAGTTTTTATTGTGACCTTCCTAGGGTAATCAAACTTGCCTGTACAGATACCTACAGGCTAGATATTATGGTCATTGCTAACAGTGGTGTGCTCACTGTGTGTTCTTTTGTTCTTCTAATCATCTCATACACTATCATCCTAATGACCATCCAGCATCGCCCTTTAGATAAGTCGTCCAAAGCTCTGTCCACTTTGACTGCTCACATTACAGTAGTTCTTTTGTTCTTTGGACCA");
        c.setTranslatedSequence("MVTEFIFLGLSDSQELQTFLFMLFFVFYGGIVFGNLLIVITVVSDSHLHSPMYFLLANLSLIDLSLSSVTAPKMITDFFSQRKVISFKGCLVQIFLLHFFGGSEMVILIAMGFDRYIAICKPLHYTTIMCGNACVGIMAVTWGIGFLHSVSQLAFAVHLLFCGPNEVDSFYCDLPRVIKLACTDTYRLDIMVIANSGVLTVCSFVLLIISYTIILMTIQHRPLDKSSKALSTLTAHITVVLLFFGP");
        c.setMutatedTranslatedSequence("TVTEFIFLGLSDSQELQTFLFMLFFVFYGGIVFGNLLIVITVVSDSHLHSPMYFLLANLSLIDLSLSSVTAPKMITDFFSQRKVISFKGCLVQIFLLHFFGGSEMVILIAMGFDRYIAICKPLHYTTIMCGNACVGIMAVTWGIGFLHSVSQLAFAVHLLFCGPNEVDSFYCDLPRVIKLACTDTYRLDIMVIANSGVLTVCSFVLLIISYTIILMTIQHRPLDKSSKALSTLTAHITVVLLFFGP");
        c.setGeneId("ZNF74");

        // add tags to mismatch object too
        m.addTag("coding-nonsynonymous", null);
        m.addTag("start-codon-loss", null);

        // now store these
        store.putMismatch(m);
        store.putConsequence(c, true);
        c = null;
        m = null;

        // close
        store.close();

        // settings object
        settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(TEST_DB_PATH);
        settings.setCacheSize(CACHE_SIZE);//commented out above
        settings.setCreateMismatchDB(false);
        settings.setCreateConsequenceAnnotationDB(false);
        settings.setCreateDbSNPAnnotationDB(false);
        settings.setCreateCoverageDB(false);
        settings.setReadOnly(true);

        // create store object
        store = factory.getStore(settings);
        Assert.assertNotNull(store);

        // now get the consequence back
        c = store.getConsequence("1");
        Assert.assertNotNull(c);
        Assert.assertEquals(c.getStrand(), Consequence.PLUS_STRAND);
        Assert.assertEquals(c.getMismatchId(), "1");
        Assert.assertEquals(c.getMismatchType(), Variant.SNV);
        Assert.assertEquals(c.getMismatchChr(), "chr22");
        Assert.assertEquals(c.getMismatchStart(), 19078919);
        Assert.assertEquals(c.getMismatchStop(), 19078920);
        Assert.assertEquals(c.getMismatchCodonPosition(), 2);
        Assert.assertEquals(c.getMismatchCodonChange(), "ATG->ACG");
        Assert.assertEquals(c.getMismatchAminoAcidChange(), "M->T");
        Assert.assertTrue(c.getTags().containsKey("start-codon-loss"));
        Assert.assertTrue(c.getTags().containsKey("coding-nonsynonymous"));
        Assert.assertEquals(c.getTags().size(), 2);
        Assert.assertEquals(c.getMismatchAAChangeBlosumScore(), (float) 3.0);
        // NOTE: these aren't really the sequences but I did modify the first codon
        Assert.assertEquals(c.getGenomicSequence(), "ATGGTGACTGAATTCATTTTTCTGGGTCTCTCTGATTCTCAGGAACTCCAGACCTTCCTATTTATGTTGTTTTTTGTATTCTATGGAGGAATCGTGTTTGGAAACCTTCTTATTGTCATAACAGTGGTATCTGACTCCCACCTTCACTCTCCCATGTACTTCCTGCTAGCCAACCTCTCACTCATTGATCTGTCTCTGTCTTCAGTCACAGCCCCCAAGATGATTACTGACTTTTTCAGCCAGCGCAAAGTCATCTCTTTCAAGGGCTGCCTTGTTCAGATATTTCTCCTTCACTTCTTTGGTGGGAGTGAGATGGTGATCCTCATAGCCATGGGCTTTGACAGATATATAGCAATATGCAAGCCCCTACACTACACTACAATTATGTGTGGCAACGCATGTGTCGGCATTATGGCTGTCACATGGGGAATTGGCTTTCTCCATTCGGTGAGCCAGTTGGCGTTTGCCGTGCACTTACTCTTCTGTGGTCCCAATGAGGTCGATAGTTTTTATTGTGACCTTCCTAGGGTAATCAAACTTGCCTGTACAGATACCTACAGGCTAGATATTATGGTCATTGCTAACAGTGGTGTGCTCACTGTGTGTTCTTTTGTTCTTCTAATCATCTCATACACTATCATCCTAATGACCATCCAGCATCGCCCTTTAGATAAGTCGTCCAAAGCTCTGTCCACTTTGACTGCTCACATTACAGTAGTTCTTTTGTTCTTTGGACCA");
        Assert.assertEquals(c.getMutatedGenomicSequence(), "ACGGTGACTGAATTCATTTTTCTGGGTCTCTCTGATTCTCAGGAACTCCAGACCTTCCTATTTATGTTGTTTTTTGTATTCTATGGAGGAATCGTGTTTGGAAACCTTCTTATTGTCATAACAGTGGTATCTGACTCCCACCTTCACTCTCCCATGTACTTCCTGCTAGCCAACCTCTCACTCATTGATCTGTCTCTGTCTTCAGTCACAGCCCCCAAGATGATTACTGACTTTTTCAGCCAGCGCAAAGTCATCTCTTTCAAGGGCTGCCTTGTTCAGATATTTCTCCTTCACTTCTTTGGTGGGAGTGAGATGGTGATCCTCATAGCCATGGGCTTTGACAGATATATAGCAATATGCAAGCCCCTACACTACACTACAATTATGTGTGGCAACGCATGTGTCGGCATTATGGCTGTCACATGGGGAATTGGCTTTCTCCATTCGGTGAGCCAGTTGGCGTTTGCCGTGCACTTACTCTTCTGTGGTCCCAATGAGGTCGATAGTTTTTATTGTGACCTTCCTAGGGTAATCAAACTTGCCTGTACAGATACCTACAGGCTAGATATTATGGTCATTGCTAACAGTGGTGTGCTCACTGTGTGTTCTTTTGTTCTTCTAATCATCTCATACACTATCATCCTAATGACCATCCAGCATCGCCCTTTAGATAAGTCGTCCAAAGCTCTGTCCACTTTGACTGCTCACATTACAGTAGTTCTTTTGTTCTTTGGACCA");
        Assert.assertEquals(c.getTranslatedSequence(), "MVTEFIFLGLSDSQELQTFLFMLFFVFYGGIVFGNLLIVITVVSDSHLHSPMYFLLANLSLIDLSLSSVTAPKMITDFFSQRKVISFKGCLVQIFLLHFFGGSEMVILIAMGFDRYIAICKPLHYTTIMCGNACVGIMAVTWGIGFLHSVSQLAFAVHLLFCGPNEVDSFYCDLPRVIKLACTDTYRLDIMVIANSGVLTVCSFVLLIISYTIILMTIQHRPLDKSSKALSTLTAHITVVLLFFGP");
        Assert.assertEquals(c.getMutatedTranslatedSequence(), "TVTEFIFLGLSDSQELQTFLFMLFFVFYGGIVFGNLLIVITVVSDSHLHSPMYFLLANLSLIDLSLSSVTAPKMITDFFSQRKVISFKGCLVQIFLLHFFGGSEMVILIAMGFDRYIAICKPLHYTTIMCGNACVGIMAVTWGIGFLHSVSQLAFAVHLLFCGPNEVDSFYCDLPRVIKLACTDTYRLDIMVIANSGVLTVCSFVLLIISYTIILMTIQHRPLDKSSKALSTLTAHITVVLLFFGP");
        Assert.assertEquals(c.getGeneId(), "ZNF74");

        m = store.getMismatch("1");
        Assert.assertNotNull(m);
        Assert.assertTrue(m.getTags().containsKey("start-codon-loss"));
        Assert.assertTrue(m.getTags().containsKey("coding-nonsynonymous"));
        Assert.assertEquals(m.getTags().size(), 3);

        // close
        c = null;

        // now can I lookup consequence by tag
        SecondaryCursorIterator mIt = store.getConsequencesByTag("start-codon-loss");
        System.out.println("Testing tag retrieval: start-codon-loss");
        int count = 0;
        while (mIt.hasNext()) {
            Consequence currC = (Consequence) mIt.next();
            if (currC != null) {
                count++;
            }
            System.out.println(" Consequence ID " + currC.getId() + " tag " + currC.getTagValue("start-codon-loss"));
            Assert.assertNotNull(currC);
            Assert.assertEquals(currC.getId(), "1");
            Assert.assertEquals(currC.getTags().get("start-codon-loss"), null);
            for (String key : currC.getTags().keySet()) {
                System.out.println("  my tag key: " + key + " value: " + currC.getTagValue(key));
            }
        }
        Assert.assertEquals(count, 1);
        mIt.close();

        // lookup mismatch by tag
        mIt = store.getMismatchesByTag("start-codon-loss");
        count = 0;
        System.out.println("Testing tag retrieval: start-codon-loss");
        while (mIt.hasNext()) {
            Variant currM = (Variant) mIt.next();
            Assert.assertNotNull(currM);
            if (currM != null) {
                count++;
            }
            System.out.println(" Variant ID " + currM.getId() + " snp " + currM.getTagValue("start-codon-loss"));
            for (String key : currM.getTags().keySet()) {
                System.out.println("  my key: " + key + " value: " + currM.getTagValue(key));
            }
            Assert.assertTrue("1".equals(currM.getId()));
        }
        Assert.assertEquals(count, 1);
        mIt.close();

        // now test retrieving consequence by mismatchId
        mIt = store.getConsequencesByMismatch("1");
        count = 0;
        System.out.println("Testing consequence retrieval by mismatch id");
        while (mIt.hasNext()) {
            Consequence curr = (Consequence) mIt.next();
            Assert.assertNotNull(curr);
            if (curr != null) {
                count++;
            }
            System.out.println(" Consequence ID " + curr.getId() + " geneId " + curr.getGeneId());
            for (String key : curr.getTags().keySet()) {
                System.out.println("  my key: " + key + " value: " + curr.getTagValue(key));
            }
            Assert.assertTrue("1".equals(curr.getId()));
        }
        Assert.assertEquals(count, 1);
        mIt.close();

        // close 
        store.close();


    }
}
