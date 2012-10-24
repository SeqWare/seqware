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
import net.sourceforge.seqware.queryengine.backend.store.impl.PostgreSQLStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.util.iterators.CursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.LocatableSecondaryCursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.SecondaryCursorIterator;

/**
 * <p>PostgreSQLTest class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
@Test(groups="postgresql")
public class PostgreSQLTest {

    private static final String DB_HOST = System.getProperty("QE_POSTGRESQL_HOST");
    private static final String DB_PORT = System.getProperty("QE_POSTGRESQL_PORT");
    private static final String DB_USER = System.getProperty("QE_POSTGRESQL_USER");
    private static final String DB_PASSWORD = System.getProperty("QE_POSTGRESQL_PASS");
    private static final String DB = System.getProperty("QE_POSTGRESQL_DB");

    @BeforeTest(enabled = true)
    void setup() {
      // nothing to do here
    }

    @AfterTest(enabled = true)
    void tearDown() {
      // nothing to do here
    }
    
    /**
     * <p>testMismatchDBReadingAndWriting.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test(enabled = true)
    public void testMismatchDBReadingAndWriting() throws Exception {

        String varId1 = null;
        String varId2 = null;
        String varId3 = null;

        // settings object
        SeqWareSettings settings = new SeqWareSettings();
        settings.setPostgresqlPersistenceStrategy(SeqWareSettings.FIELDS);
        settings.setStoreType("postgresql-mismatch-store");
        // FIXME: need to make these params at some point
        settings.setDatabase(DB);
        settings.setUsername(DB_USER);
        settings.setPassword(DB_PASSWORD);
        settings.setServer(DB_HOST);
        settings.setGenomeId("genomeId");
        settings.setReferenceId("referenceId");
        settings.setReturnIds(true);
        PostgreSQLStore store = new PostgreSQLStore();
        store.setSettings(settings);
        store.setup(settings);
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

        // now try to save some tags!
        m.getTags().put("chr22", null);
        m.addTag("nonsynonymous", null);
        m.addTag("isDbSNP131", "rs2121203");


        // now save in the db
        store.putMismatch(m);
        System.out.println("Variant ID: " + m.getId());
        varId1 = m.getId();

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
        varId2 = m.getId();

        // create a mismatch deletion entry and add it to the database
        /* http://genome.ucsc.edu/cgi-bin/hgGene?hgg_gene=uc001kfb.1&hgg_prot=P60484&hgg_chrom=chr10&hgg_start=89613174&hgg_end=89718512&hgg_type=knownGene&db=hg18&hgsid=140022007
         * pten deletion in third codon
         * GACA-G-CCAT
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
        varId3 = m.getId();

        // close the store
        store.close();

        // now reopen the database and check that the round trip works
        settings = new SeqWareSettings();
        settings.setPostgresqlPersistenceStrategy(SeqWareSettings.FIELDS);
        settings.setStoreType("postgresql-mismatch-store");
        // FIXME: need to make these params at some point
        settings.setDatabase(DB);
        settings.setUsername(DB_USER);
        settings.setPassword(DB_PASSWORD);
        settings.setServer(DB_HOST);
        settings.setGenomeId("genomeId");
        settings.setReferenceId("referenceId");
        settings.setReturnIds(true);
        store = new PostgreSQLStore();
        store.setSettings(settings);
        store.setup(settings);
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
            if (varId1.equals(m.getId())) {
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
                // now try to get some tags!
                Assert.assertNotNull(m.getTags());
                Assert.assertTrue(m.getTags().containsKey("chr22"));
                Assert.assertTrue(m.getTags().containsKey("nonsynonymous"));
                Assert.assertTrue(m.getTags().containsKey("isDbSNP131"));
                Assert.assertEquals(m.getTags().get("isDbSNP131"), "rs2121203");

            } else if (varId2.equals(m.getId())) {
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
            } else if (varId3.equals(m.getId())) {
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
                // just going to ignore other rows since I don't really have control over what else might be in this DB
                System.out.println("Found another mismatch in the DB not created by this test: " + m.getId());
                //Assert.fail("Got back a mismatch with a MismatchID outside the excpected range!");
            }
        }
        // close the iterator
        mismatchIt.close();

        // close the store
        store.close();


    }
}
