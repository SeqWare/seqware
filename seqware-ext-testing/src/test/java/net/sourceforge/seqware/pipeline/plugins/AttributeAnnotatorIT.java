/*
 * Copyright (C) 2013 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.pipeline.plugins;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.metadb.util.TestDatabaseCreator;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.log.Log;

/**
 * These tests support command-line tools found in the SeqWare User Tutorial, in
 * this case, AttributeAnnotator
 *
 * @author dyuen
 */
public class AttributeAnnotatorIT {
    public static final String COUNT_DB_SIZE = "SELECT (SELECT COUNT(*) FROM workflow), (SELECT COUNT(*) FROM workflow_run), (SELECT COUNT(*) FROM sequencer_run), (SELECT COUNT(*) FROM experiment), (SELECT COUNT(*) FROM ius), (SELECT COUNT(*) FROM lane), (SELECT COUNT(*) FROM processing), (SELECT COUNT(*) FROM sample), (SELECT COUNT(*) FROM sample_hierarchy), (SELECT COUNT(*) FROM processing_ius), (SELECT COUNT(*) FROM processing_files), (SELECT COUNT(*) FROM processing_relationship), (SELECT COUNT(*) FROM file), (SELECT COUNT(*) FROM study)";

    public enum AttributeType {

        SEQUENCER_RUN("sequencer-run", "sequencer_run", "sample", true),
        LANE("lane", "lane", "lane", true),
        IUS("ius", "ius", "ius", true),
        EXPERIMENT("experiment", "experiment", "experiment", false),
        PROCESSING("processing", "processing", "processing", false),
        SAMPLE("sample", "sample", "sample", true),
        STUDY("study", "study", "study", false),
        WORKFLOW("workflow", "workflow", "workflow", false),
        WORKFLOW_RUN("workflow-run", "workflow_run", "workflow_run", false);
        protected final String parameter_prefix;
        protected final String table_name;
        protected final String attribute_id_prefix;
        
        /**
         * SEQWARE-1676
         */
        protected final boolean skippable;

        AttributeType(String prefix, String table_name, String attribute_id_prefix, boolean skippable) {
            this.parameter_prefix = prefix;
            this.table_name = table_name;
            this.skippable = skippable;
            this.attribute_id_prefix = attribute_id_prefix;
        }
    }

    @BeforeClass
    public static void resetDatabase() {
        TestDatabaseCreator.resetDatabaseWithUsers();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        DBAccess.close();
    }

    @Test
    public void testSequencerRunSkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.SEQUENCER_RUN, 47150);
    }

    @Test
    public void testLaneSkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.LANE, 4708);
    }

    @Test
    public void testIUSSkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.IUS, 4789);
    }

    /**
     * experiment has no skip column, but this doesn't fail?
     *
     * @throws IOException
     */
    @Test
    public void testExperimentSkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.EXPERIMENT, 2587);
    }

    /**
     * processing has no skip column, but this doesn't fail?
     *
     * @throws IOException
     */
    @Test
    public void testProcessingSkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.PROCESSING, 16);
    }

    @Test
    public void testSampleSkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.SAMPLE, 6200);
    }

    /**
     * study has no skip column, but this doesn't fail?
     *
     * @throws IOException
     */
    @Test
    public void testStudySkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.STUDY, 4758);
    }

    /**
     * workflow has no skip column, but this doesn't fail?
     *
     * @throws IOException
     */
    @Test
    public void testWorkflowSkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.WORKFLOW, 2861);
    }

    /**
     * workflow_run has no skip column, but this doesn't fail?
     *
     * @throws IOException
     */
    @Test
    public void testWorkflowRunSkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.WORKFLOW_RUN, 863);
    }

    @Test
    public void testSequencerRunAnnotateArbitrary() throws IOException {
        annotateAndReannotate(AttributeType.SEQUENCER_RUN, 47150);
    }

    @Test
    public void testLaneAnnotateArbitrary() throws IOException {
        annotateAndReannotate(AttributeType.LANE, 4708);
    }

    @Test
    public void testIUSAnnotateArbitrary() throws IOException {
        annotateAndReannotate(AttributeType.IUS, 4789);
    }

    @Test
    public void testExperimentAnnotateArbitrary() throws IOException {
        annotateAndReannotate(AttributeType.EXPERIMENT, 2587);
    }

    @Test
    public void testProcessingAnnotateArbitrary() throws IOException {
        annotateAndReannotate(AttributeType.PROCESSING, 6676);
    }

    @Test
    public void testSampleAnnotateArbitrary() throws IOException {
        annotateAndReannotate(AttributeType.SAMPLE, 6200);
    }

    @Test
    public void testStudyAnnotateArbitrary() throws IOException {
        annotateAndReannotate(AttributeType.STUDY, 4758);
    }

    @Test
    public void testWorkflowAnnotateArbitrary() throws IOException {
        annotateAndReannotate(AttributeType.WORKFLOW, 2861);
    }

    @Test
    public void testWorkflowRunAnnotateArbitrary() throws IOException {
        annotateAndReannotate(AttributeType.WORKFLOW_RUN, 863);
    }

    /**
     * Toggle just the skip column on a selected table type
     *
     * @param type
     * @param accession
     * @throws IOException
     */
    public void toggleSkipOnly(AttributeType type, int accession) throws IOException {
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator "
                + "-- --" + type.parameter_prefix + "-accession " + accession + " --skip true";
        int expectedReturnValue = type.skippable ? ReturnValue.SUCCESS : ReturnValue.INVALIDPARAMETERS;
        ITUtility.runSeqWareJar(listCommand, expectedReturnValue, null);
        if (type.skippable) {
            Object[] runQuery = TestDatabaseCreator.runQuery(new ArrayHandler(), "SELECT skip FROM " + type.table_name + " WHERE sw_accession=?", accession);
            Assert.assertTrue("skip value incorrect", runQuery.length == 1 && runQuery[0].equals(true));
        }
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator "
                + "-- --" + type.parameter_prefix + "-accession " + accession + " --skip false";
        ITUtility.runSeqWareJar(listCommand, expectedReturnValue, null);
        if (type.skippable) {
            Object[] runQuery = TestDatabaseCreator.runQuery(new ArrayHandler(), "SELECT skip FROM " + type.table_name + " WHERE sw_accession=?", accession);
            Assert.assertTrue("skip value incorrect", runQuery.length == 1 && runQuery[0].equals(false));
        }
    }

    /**
     * Annotate an attribute with both a key and value. Re-annotate and ensure
     * that no duplicates are formed.
     *
     * @param type
     * @param accession
     * @throws IOException
     */
    public void annotateAndReannotate(AttributeType type, int accession) throws IOException {
        final String funky_key = "funky_key";
        final String funky_second_value = "funky_second_value";
        final String funky_initial_value = "funky_initial_value";
        final String groovy_key = "groovy_key";
        final String groovy_value = "groovy_value";
        
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator "
                + "-- --" + type.parameter_prefix + "-accession " + accession + " --key "+funky_key+" --value " + funky_initial_value;
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        String query = "SELECT t2."+type.table_name+"_attribute_id, t2.tag, t2.value FROM " + type.table_name 
                + "_attribute t2, "+type.table_name+" t1 WHERE "
                + "t1."+type.table_name+"_id=t2."+type.attribute_id_prefix+"_id AND t1.sw_accession=?";
        Log.info(query);
        List<Object[]> runQuery = TestDatabaseCreator.runQuery(new ArrayListHandler(), query , accession);
        Assert.assertTrue("first annotation incorrect", runQuery.size() == 1);
        Assert.assertTrue("first tag incorrect", runQuery.get(0)[1].equals(funky_key));
        Assert.assertTrue("first value incorrect", runQuery.get(0)[2].equals(funky_initial_value));
        // count records in the database to check for cascading deletes
        List<Object[]> count1 = TestDatabaseCreator.runQuery(new ArrayListHandler(), COUNT_DB_SIZE);        
        // reannotate with same key, different value
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator "
                + "-- --" + type.parameter_prefix + "-accession " + accession + " --key "+funky_key+" --value " + funky_second_value;
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        // ensure that duplicates are not formed in the database
        runQuery = TestDatabaseCreator.runQuery(new ArrayListHandler(), query , accession);
        Assert.assertTrue("incorrect resulting number of duplicate annotations, found " + runQuery.size(), runQuery.size() == 1);
        Assert.assertTrue("second tag incorrect", runQuery.get(0)[1].equals(funky_key));
        Assert.assertTrue("second value incorrect", runQuery.get(0)[2].equals(funky_second_value));
        // check against cascading deletes
        List<Object[]> count2 = TestDatabaseCreator.runQuery(new ArrayListHandler(), COUNT_DB_SIZE);
        compareTwoCounts(count1.get(0), count2.get(0));
        // try unrelated annotation
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator "
                + "-- --" + type.parameter_prefix + "-accession " + accession + " --key "+groovy_key+" --value " + groovy_value;
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        // check results of unrelated annotation
        runQuery = TestDatabaseCreator.runQuery(new ArrayListHandler(), query , accession);
        Assert.assertTrue("incorrect resulting number of unrelated annotations, found: " + runQuery.size(), runQuery.size() == 2);
        List<Object[]> count3 = TestDatabaseCreator.runQuery(new ArrayListHandler(), COUNT_DB_SIZE);
        compareTwoCounts(count2.get(0), count3.get(0));
    }
    
    /**
     * Compare two object arrays element by element and 
     * output which element fails
     * @param count1
     * @param count2 
     */
    private void compareTwoCounts(Object[] count1, Object[] count2){
        Assert.assertTrue("size of arrays is different", count1.length == count2.length);
        for(int i = 0; i < count1.length; i++){
            Assert.assertTrue("element " + i + " did not match", count1[i].equals(count2[i]));
        }
    }
}
