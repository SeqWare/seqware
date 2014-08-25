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
import java.util.List;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests support command-line tools found in the SeqWare User Tutorial, in this case, AttributeAnnotator
 * 
 * @author dyuen
 */
public class AttributeAnnotatorET {
    public static final String COUNT_DB_SIZE = "SELECT (SELECT COUNT(*) FROM workflow), (SELECT COUNT(*) FROM workflow_run), (SELECT COUNT(*) FROM sequencer_run), (SELECT COUNT(*) FROM experiment), (SELECT COUNT(*) FROM ius), (SELECT COUNT(*) FROM lane), (SELECT COUNT(*) FROM processing), (SELECT COUNT(*) FROM sample), (SELECT COUNT(*) FROM sample_hierarchy), (SELECT COUNT(*) FROM processing_ius), (SELECT COUNT(*) FROM processing_files), (SELECT COUNT(*) FROM processing_relationship), (SELECT COUNT(*) FROM file), (SELECT COUNT(*) FROM study)";
    private final ExtendedTestDatabaseCreator dbCreator = new ExtendedTestDatabaseCreator();

    public enum AttributeType {
        FILE("file", "file", "file", true), SEQUENCER_RUN("sequencer-run", "sequencer_run", "sample", true), LANE("lane", "lane", "lane",
                true), IUS("ius", "ius", "ius", true), EXPERIMENT("experiment", "experiment", "experiment", false), PROCESSING(
                "processing", "processing", "processing", false), SAMPLE("sample", "sample", "sample", true), STUDY("study", "study",
                "study", false), WORKFLOW("workflow", "workflow", "workflow", false), WORKFLOW_RUN("workflow-run", "workflow_run",
                "workflow_run", false);
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
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Test
    public void testFileSkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.FILE, 835);
    }

    @Test
    public void testSequencerRunSkipOnly() throws IOException {
        // need to reset database due to repeated accession
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
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

    @Test
    public void testExperimentSkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.EXPERIMENT, 2587);
    }

    @Test
    public void testProcessingSkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.PROCESSING, 16);
    }

    @Test
    public void testSampleSkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.SAMPLE, 6200);
    }

    @Test
    public void testStudySkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.STUDY, 4758);
    }

    @Test
    public void testWorkflowSkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.WORKFLOW, 2861);
    }

    @Test
    public void testWorkflowRunSkipOnly() throws IOException {
        toggleSkipOnly(AttributeType.WORKFLOW_RUN, 863);
    }

    @Test
    public void testFileSkipValue() throws IOException {
        annotateSkipImplicitly(AttributeType.FILE, 838);
    }

    @Test
    public void testSequencerRunSkipValue() throws IOException {
        // need to reset database due to repeated accession
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
        annotateSkipImplicitly(AttributeType.SEQUENCER_RUN, 4715);
    }

    @Test
    public void testLaneSkipValue() throws IOException {
        annotateSkipImplicitly(AttributeType.LANE, 4709);
    }

    @Test
    public void testIUSSkipValue() throws IOException {
        annotateSkipImplicitly(AttributeType.IUS, 6077);
    }

    @Test
    public void testExperimentSkipValue() throws IOException {
        annotateSkipImplicitly(AttributeType.EXPERIMENT, 4759);
    }

    @Test
    public void testProcessingSkipValue() throws IOException {
        annotateSkipImplicitly(AttributeType.PROCESSING, 2524);
    }

    @Test
    public void testSampleSkipValue() throws IOException {
        annotateSkipImplicitly(AttributeType.SAMPLE, 6207);
    }

    @Test
    public void testStudySkipValue() throws IOException {
        annotateSkipImplicitly(AttributeType.STUDY, 6144);
    }

    @Test
    public void testWorkflowSkipValue() throws IOException {
        annotateSkipImplicitly(AttributeType.WORKFLOW, 4767);
    }

    @Test
    public void testWorkflowRunSkipValue() throws IOException {
        annotateSkipImplicitly(AttributeType.WORKFLOW_RUN, 6654);
    }

    @Test
    public void testFileAnnotateArbitrary() throws IOException {
        annotateAndReannotate(AttributeType.FILE, 6120);
    }

    @Test
    public void testSequencerRunAnnotateArbitrary() throws IOException {
        // need to reset database due to repeated accession
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
        annotateAndReannotate(AttributeType.SEQUENCER_RUN, 47150);
    }

    @Test
    public void testLaneAnnotateArbitrary() throws IOException {
        annotateAndReannotate(AttributeType.LANE, 6123);
    }

    @Test
    public void testIUSAnnotateArbitrary() throws IOException {
        annotateAndReannotate(AttributeType.IUS, 6124);
    }

    @Test
    public void testExperimentAnnotateArbitrary() throws IOException {
        annotateAndReannotate(AttributeType.EXPERIMENT, 4759);
    }

    @Test
    public void testProcessingAnnotateArbitrary() throws IOException {
        annotateAndReannotate(AttributeType.PROCESSING, 6122);
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
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator " + "-- --" + type.parameter_prefix
                + "-accession " + accession + " --skip true";
        int expectedReturnValue = type.skippable ? ReturnValue.SUCCESS : ReturnValue.INVALIDPARAMETERS;
        ITUtility.runSeqWareJar(listCommand, expectedReturnValue, null);
        if (type.skippable) {
            Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "SELECT skip FROM " + type.table_name + " WHERE sw_accession=?",
                    accession);
            Assert.assertTrue("skip value incorrect", runQuery.length == 1 && runQuery[0].equals(true));
        }
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator " + "-- --" + type.parameter_prefix + "-accession "
                + accession + " --skip false";
        ITUtility.runSeqWareJar(listCommand, expectedReturnValue, null);
        if (type.skippable) {
            Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "SELECT skip FROM " + type.table_name + " WHERE sw_accession=?",
                    accession);
            Assert.assertTrue("skip value incorrect", runQuery.length == 1 && runQuery[0].equals(false));
        }
    }

    /**
     * Annotate skip with an implicit key of "skip"
     * 
     * @param type
     * @param accession
     * @throws IOException
     */
    public void annotateSkipImplicitly(AttributeType type, int accession) throws IOException {
        String query = "SELECT t2." + type.table_name + "_attribute_id, t2.tag, t2.value FROM " + type.table_name + "_attribute t2, "
                + type.table_name + " t1 WHERE " + "t1." + type.table_name + "_id=t2." + type.attribute_id_prefix
                + "_id AND t1.sw_accession=? ORDER BY " + type.table_name + "_attribute_id";
        Log.info(query);
        String value = "\"Improperly entered into the LIMS\"";

        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator " + "-- --" + type.parameter_prefix
                + "-accession " + accession + " --skip true --value " + value;
        int expectedReturnValue = type.skippable ? ReturnValue.SUCCESS : ReturnValue.INVALIDPARAMETERS;
        ITUtility.runSeqWareJar(listCommand, expectedReturnValue, null);
        if (type.skippable) {
            Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "SELECT skip FROM " + type.table_name + " WHERE sw_accession=?",
                    accession);
            Assert.assertTrue("skip value incorrect", runQuery.length == 1 && runQuery[0].equals(true));
            List<Object[]> runQuery1 = dbCreator.runQuery(new ArrayListHandler(), query, accession);
            Assert.assertTrue("first annotation incorrect, found " + runQuery1.size(), runQuery1.size() == 1);
            Assert.assertTrue("first tag incorrect, found " + runQuery1.get(0)[1], runQuery1.get(0)[1].equals("skip"));
            Assert.assertTrue("first value incorrect, found " + runQuery1.get(0)[2], runQuery1.get(0)[2].equals(value));
        }
    }

    /**
     * Annotate an attribute with both a key and value. Re-annotate and ensure that no duplicates are formed.
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

        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator " + "-- --" + type.parameter_prefix
                + "-accession " + accession + " --key " + funky_key + " --value " + funky_initial_value;
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        String query = "SELECT t2." + type.table_name + "_attribute_id, t2.tag, t2.value FROM " + type.table_name + "_attribute t2, "
                + type.table_name + " t1 WHERE " + "t1." + type.table_name + "_id=t2." + type.attribute_id_prefix
                + "_id AND t1.sw_accession=? ORDER BY " + type.table_name + "_attribute_id";
        Log.info(query);
        List<Object[]> runQuery = dbCreator.runQuery(new ArrayListHandler(), query, accession);
        Assert.assertTrue("first annotation incorrect", runQuery.size() == 1);
        Assert.assertTrue("first tag incorrect", runQuery.get(0)[1].equals(funky_key));
        Assert.assertTrue("first value incorrect", runQuery.get(0)[2].equals(funky_initial_value));
        // count records in the database to check for cascading deletes
        List<Object[]> count1 = dbCreator.runQuery(new ArrayListHandler(), COUNT_DB_SIZE);
        runAnnotationWithSecondValue(type, accession, funky_key, funky_second_value, query);
        // seqware-1945: test reannotating with the same key and same value
        runAnnotationWithSecondValue(type, accession, funky_key, funky_second_value, query);

        // check against cascading deletes
        List<Object[]> count2 = dbCreator.runQuery(new ArrayListHandler(), COUNT_DB_SIZE);
        compareTwoCounts(count1.get(0), count2.get(0));
        // try unrelated annotation
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator " + "-- --" + type.parameter_prefix + "-accession "
                + accession + " --key " + groovy_key + " --value " + groovy_value;
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        // check results of unrelated annotation
        runQuery = dbCreator.runQuery(new ArrayListHandler(), query, accession);
        Assert.assertTrue("incorrect resulting number of unrelated annotations, found: " + runQuery.size(), runQuery.size() == 2);
        Assert.assertTrue("second tag incorrect", runQuery.get(0)[1].equals(funky_key));
        Assert.assertTrue("second value incorrect", runQuery.get(0)[2].equals(funky_second_value));
        Assert.assertTrue("third tag incorrect", runQuery.get(1)[1].equals(groovy_key));
        Assert.assertTrue("third value incorrect", runQuery.get(1)[2].equals(groovy_value));
        List<Object[]> count3 = dbCreator.runQuery(new ArrayListHandler(), COUNT_DB_SIZE);
        compareTwoCounts(count2.get(0), count3.get(0));
    }

    private void runAnnotationWithSecondValue(AttributeType type, int accession, final String funky_key, final String funky_second_value,
            String query) throws IOException {
        String listCommand;
        List<Object[]> runQuery;
        // reannotate with same key, different value
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator " + "-- --" + type.parameter_prefix + "-accession "
                + accession + " --key " + funky_key + " --value " + funky_second_value;
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        // ensure that duplicates are not formed in the database
        runQuery = dbCreator.runQuery(new ArrayListHandler(), query, accession);
        Assert.assertTrue("incorrect resulting number of duplicate annotations, found " + runQuery.size(), runQuery.size() == 1);
        Assert.assertTrue("second tag incorrect", runQuery.get(0)[1].equals(funky_key));
        Assert.assertTrue("second value incorrect", runQuery.get(0)[2].equals(funky_second_value));
    }

    /**
     * Compare two object arrays element by element and output which element fails
     * 
     * @param count1
     * @param count2
     */
    private void compareTwoCounts(Object[] count1, Object[] count2) {
        Assert.assertTrue("size of arrays is different", count1.length == count2.length);
        for (int i = 0; i < count1.length; i++) {
            Assert.assertTrue("element " + i + " did not match", count1[i].equals(count2[i]));
        }
    }

    /**
     * Test various forms of invalid parameters SEQWARE-1678
     * 
     * @throws java.io.IOException
     */
    @Test
    public void testInvalidParameters() throws IOException {
        // invalid value
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator "
                + "-- --ius 4789 --key funky_key --funky_change_value";
        ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDARGUMENT, null);
        // key with no valid value
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator " + "-- --ius 4789 --key funky_key";
        ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDPARAMETERS, null);
    }

    @Test
    public void testRejectDoubleAnnotation() throws IOException {
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator "
                + "-- --file-accession 6650 --key funky_key --value funky_value";
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator "
                + "-- --file-accession 6650 --key funky_key --value funky_value";
        // seqware-1945: rejecting double annotation with the same key and value seems confusing to users
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
    }

    @Test
    public void testBulkInsert() throws IOException {
        String path = AttributeAnnotatorET.class.getResource("attributeAnnotator.csv").getPath();
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator " + "-- --file " + path;
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
    }

}
