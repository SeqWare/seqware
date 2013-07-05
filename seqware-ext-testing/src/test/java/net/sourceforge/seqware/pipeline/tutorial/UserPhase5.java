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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import net.sourceforge.seqware.pipeline.plugins.PluginRunnerET;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import org.junit.Test;

/**
 * Build and install a bundle, used by both the User tutorial and the Developer tutorial
 * @author dyuen
 */
public class UserPhase5 {
    
    public static final String WORKFLOW = "Workflow";
    
    @Test
    public void testListAvailableWorkflowsAndTheirParameters() throws IOException {
        PluginRunnerET pit = new PluginRunnerET();
        PluginRunnerET.clearStaticVariables();
        PluginRunner it = new PluginRunner();
        String SEQWARE_VERSION = it.getClass().getPackage().getImplementationVersion();
        Assert.assertTrue("unable to detect seqware version", SEQWARE_VERSION != null);
        Log.info("SeqWare version detected as: " + SEQWARE_VERSION);

        // for all tests, we're going to need to create and install our basic archetypes
        String[] archetypes = {"java-workflow"};
        PluginRunnerET.buildAndInstallArchetypes(archetypes, SEQWARE_VERSION);

        //list workflows and ensure that the workflow is installed
        List<Integer> accessions = new ArrayList<Integer>();
        accessions.addAll(PluginRunnerET.getInstalledWorkflows().values());
        Assert.assertTrue("one accession expected", accessions.size() == 1);
        AccessionMap.accessionMap.put(WORKFLOW, accessions.get(0).toString());

        // launch our specific workflow and get store its workflow run accession
        File exportINIFile = pit.exportINIFile("Java workflow", accessions.get(0));
        
        String localhost = ITUtility.getLocalhost();
        Log.info("Attempting to launch with wait on host: " + localhost);
        // launch, slightly unlike the tutorial, I'm going to wait to ensure that we have results to export in the next phase
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files " + exportINIFile.getAbsolutePath() + " --workflow-accession " + accessions.get(0)
                + " --parent-accessions " + AccessionMap.accessionMap.get(UserPhase4.FILE) + " --wait --host " + localhost;
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        Log.info(listOutput);
    }
}
