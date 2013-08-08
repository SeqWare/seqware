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
package net.sourceforge.seqware.pipeline.tutorial;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import net.sourceforge.seqware.pipeline.plugins.PluginRunnerET;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests downloading results in the tutorial
 *
 * @author dyuen
 */
public class UserPhase6 {

    @Test
    public void testMonitorWorkflowRuns() throws IOException {
        monitorAndReturnWorkflowRun();
    }

    //TODO: test wra instead of wa, but the previous step we needed to use --wait to ensure we have results to report and --wait does not output a SWID to the command-line
    @Test
    public void testMonitorWorkflowRunStdOut() throws IOException {
        File workingDir = Files.createTempDir();
        String swid = monitorAndReturnWorkflowRun();      
         // check on existence and contents of output
        // delete files that would interfere
        WorkFlowRunReporterFilterStdOut filter = new WorkFlowRunReporterFilterStdOut(swid);
        runWorkflowRunReporterStdOut(swid, workingDir);
        
        Collection<File> listFiles = FileUtils.listFiles(workingDir, filter, filter);
        Assert.assertTrue("wrong number of csv files found, found " + listFiles.size(), listFiles.size() == 1);
        File foundFile = listFiles.iterator().next();
        foundFile.delete();
    }

    @Test
    public void testMonitorWorkflowRunStdErr() throws IOException {
        File workingDir = Files.createTempDir();
        String swid = monitorAndReturnWorkflowRun();
         // check on existence and contents of output
        WorkFlowRunReporterFilterStdErr filter = new WorkFlowRunReporterFilterStdErr(swid);     
        runWorkflowRunReporterStdErr(swid, workingDir);
        Collection<File> listFiles = FileUtils.listFiles(workingDir, filter, filter);
        Assert.assertTrue("wrong number of csv files found, found " + listFiles.size(), listFiles.size() == 1);
        File foundFile = listFiles.iterator().next();
        foundFile.delete();
    }

    @Test
    public void testdownloadWorkflowResults() throws IOException {
        exportStudyResults();
    }
    
    @AfterClass
    public static void cleanup() throws IOException{
        PluginRunnerET.monitorAndClean(false);
    }

    private String monitorAndReturnWorkflowRun() throws IOException {
        File workingDir = Files.createTempDir();
        // delete files that would interfere
        WorkFlowRunReporterFilter filter = new WorkFlowRunReporterFilter();
        runWorkflowRunReporter(workingDir);

        Collection<File> listFiles = FileUtils.listFiles(workingDir, filter, filter);
        // ensure that we only have one csv file
        Assert.assertTrue("too many csv files found", listFiles.size() == 1);
        File foundFile = listFiles.iterator().next();
        // check that we have at least one run in the output
        boolean runFound = false;
        String workflowRunSWID = null;
        for (String line : FileUtils.readLines(foundFile)) {
            if (line.contains("seqware-archetype-java-workflow")) {
                runFound = true;
                String[] parts = line.split("\t");
                workflowRunSWID = parts[1];
            }
        }
        foundFile.delete();
        Assert.assertTrue("invalid workflow run SWID", runFound && workflowRunSWID != null);
        return workflowRunSWID;
    }

    protected void runWorkflowRunReporter(File workingDir) throws IOException {
        String output = ITUtility.runSeqWareJar(" -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- -wa " + AccessionMap.accessionMap.get(UserPhase5.WORKFLOW)
                , ReturnValue.SUCCESS
                , workingDir);
    }

    protected void runWorkflowRunReporterStdErr(String swid, File workingDir) throws IOException {
        String output = ITUtility.runSeqWareJar(" -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- --wr-stderr -wra " + swid
                , ReturnValue.SUCCESS
                , workingDir);
    }

    protected void runWorkflowRunReporterStdOut(String swid, File workingDir) throws IOException {
        String output = ITUtility.runSeqWareJar(" -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- --wr-stdout -wra " + swid
                , ReturnValue.SUCCESS
                , workingDir);
    }

    protected void exportStudyResults() throws IOException {
        String output = ITUtility.runSeqWareJar(" -p net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter -- "
                + " --no-links --output-filename study_report "
                + "--workflow-accession "
                + AccessionMap.accessionMap.get(UserPhase5.WORKFLOW) + " "
                + "--study 'New Test Study'", ReturnValue.SUCCESS, null);
    }

    public static class WorkFlowRunReporterFilter implements IOFileFilter {

        @Override
        public boolean accept(File file) {
            return check(file);
        }

        @Override
        public boolean accept(File file, String string) {
            return check(file);
        }
        
        private boolean check(File file){
             return file.getName().contains("_workflow_" + AccessionMap.accessionMap.get(UserPhase5.WORKFLOW) + ".csv");
        }
    }

    public static class WorkFlowRunReporterFilterStdOut implements IOFileFilter {

        private String swid;
        
        public WorkFlowRunReporterFilterStdOut(String swid){
            this.swid = swid;
        }
        
       @Override
        public boolean accept(File file) {
            return check(file);
        }

        @Override
        public boolean accept(File file, String string) {
            return check(file);
        }
        
        private boolean check(File file){
             return file.getName().contains("_workflowrun_" + swid + "_STDOUT.csv");
        }
    }

    public static class WorkFlowRunReporterFilterStdErr implements IOFileFilter {
        
        private String swid;
        
        public WorkFlowRunReporterFilterStdErr(String swid){
            this.swid = swid;
        }

        @Override
        public boolean accept(File file) {
            return check(file);
        }

        @Override
        public boolean accept(File file, String string) {
            return check(file);
        }
        
        private boolean check(File file){
             return file.getName().contains("_workflowrun_" + swid + "_STDERR.csv");
        }
    }
}
