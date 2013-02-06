package net.sourceforge.seqware.pipeline.plugins;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import junit.framework.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

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


/**
 *
 * @author dyuen
 */
public class PluginRunnerIT {
   
    @Test
    public void testBasicPluginRunner() throws IOException{
        String output = runSeqWareJar("", ReturnValue.INVALIDARGUMENT);
        Assert.assertTrue("output should include usage and no Exceptions", output.contains("Syntax:") && !output.contains("Exception"));
    }
    
    @Test
    public void testBasicMetadataRetrieval() throws IOException{
        String output = runSeqWareJar(" -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --list-tables", ReturnValue.SUCCESS);
        Assert.assertTrue("output should include table names", output.contains("TableName") && output.contains("study") && output.contains("experiment"));
    }
    
    @Test
    public void testTestLatestWorkflows() throws IOException{
        String output = runSeqWareJar("-p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-installed", ReturnValue.SUCCESS);
        Assert.assertTrue("output should include installed workflows", output.contains("INSTALLED WORKFLOWS"));
        Map<String, WorkflowInfo> latestWorkflows = new HashMap<String, WorkflowInfo>();
        String[] lines = output.split(System.getProperty("line.separator"));
        for(String line : lines){
            String[] lineParts = line.split("\t");
            try{
                int workflow_accession = Integer.valueOf(lineParts[3]);
                String workflowName = lineParts[0];
                String path = lineParts[4];
                if (path.equals("null")){
                    continue;
                }
                WorkflowInfo wi = new WorkflowInfo(workflow_accession, path, workflowName, lineParts[1]);
                if (!latestWorkflows.containsKey(workflowName)){
                     latestWorkflows.put(workflowName, wi);
                } else {
                    // contained
                    int old = latestWorkflows.get(workflowName).sw_accession;
                    if (workflow_accession > old){
                        latestWorkflows.put(workflowName, wi);
                    }
                }
            } catch(Exception e){
                /** do nothing */
            }
        }
        // list the newest workflows that we encountered
        // go ahead and test them 
        for(Entry<String, WorkflowInfo> e : latestWorkflows.entrySet()){
            System.out.println("Testing " + e.getKey() + " " + e.getValue().sw_accession);
            StringBuilder params = new StringBuilder();
            params.append("--bundle ").append(e.getValue().path).append(" ");
            params.append("--version ").append(e.getValue().version).append(" ");
            params.append("--test ");
            String tOutput = runSeqWareJar("-p net.sourceforge.seqware.pipeline.plugins.BundleManager -- " + params.toString(), ReturnValue.SUCCESS);
            System.out.println(tOutput);
        }
    }
    
    private class WorkflowInfo{
        public int sw_accession;
        public String path;
        public String name;
        public String version;
        
        public WorkflowInfo(int sw_accession, String path, String name, String version){
            this.sw_accession = sw_accession; 
            this.path = path;
            this.name = name;
            this.version = version;
        }
    }

    private static boolean isRootOfSeqWare(File workingDirectory) {
        String[] list = workingDirectory.list();
        for(String file : list){
            if (file.contains("seqware-distribution")){
                return true;
            }
        }
        return false;
    }
    
    public static String runSeqWareJar(String parameters, int expectedReturnValue) throws IOException {
        File jar = retrieveFullAssembledJar();
        String line = "java -jar " + jar.getAbsolutePath() + " " + parameters;
        Log.info("Running " + line);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CommandLine commandline = CommandLine.parse(line);
        DefaultExecutor exec = new DefaultExecutor();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        exec.setStreamHandler(streamHandler);
        exec.setExitValue(expectedReturnValue);
        int exitValue = exec.execute(commandline);
        Assert.assertTrue("exit value for full jar with no params should be " + expectedReturnValue + " was " + exitValue, exitValue == expectedReturnValue);
        String output = outputStream.toString();
        return output;
    }

    /**
     * This is the hackiest hack in the universe of hacks for getting the final assembly jar so I can run tests. 
     * 
     * There has got to be a better way of getting the path of the assembled jar via maven properties or some such.
     * But I really need something to help me run tests on production bundles
     */
    public static File retrieveFullAssembledJar() {
        String workingDir = System.getProperty("user.dir");
        File workingDirectory;
        workingDirectory = new File(workingDir);
        while(!isRootOfSeqWare(workingDirectory)){
            workingDirectory = workingDirectory.getParentFile();
        }
        File seqDistDir = new File(workingDirectory, "seqware-distribution");
        File seqTargetDir = new File(seqDistDir, "target");
        File targetFullJar = null;
        for(File files : seqTargetDir.listFiles()){
            if (files.getName().contains("full") && !files.getName().contains("-qe-")){
                targetFullJar = files;
            }
        }
        return targetFullJar;
    }
}
