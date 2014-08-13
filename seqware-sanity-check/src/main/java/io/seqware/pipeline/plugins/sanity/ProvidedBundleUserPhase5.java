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
package io.seqware.pipeline.plugins.sanity;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import net.sourceforge.seqware.pipeline.plugins.PluginRunnerET;
import net.sourceforge.seqware.pipeline.tutorial.AccessionMap;
import net.sourceforge.seqware.pipeline.tutorial.UserPhase4;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Build and install a bundle, used by both the User tutorial and the Developer tutorial
 * 
 * @author dyuen
 */
public class ProvidedBundleUserPhase5 {

    public static final String WORKFLOW = "Workflow";

    @Test
    public void installHandyProvidedBundleTest() throws IOException {
        Collection<File> listFiles = FileUtils.listFiles(new File(System.getProperty("user.dir")), new String[] { "zip" }, false);
        Assert.assertTrue("could not find appropriate bundle for testing", listFiles.size() > 0);

        File bundleUsed = listFiles.iterator().next();
        Log.info("Using bundle " + bundleUsed.getAbsolutePath() + " for testing");

        // for all tests, we're going to need to create and install our basic archetypes
        String installCommand = "-p net.sourceforge.seqware.pipeline.plugins.BundleManager -verbose -- -i -b "
                + bundleUsed.getAbsolutePath();
        String installOutput = ITUtility.runSeqWareJar(installCommand, ReturnValue.SUCCESS, null);
        Log.info(installOutput);
        int workflowAccession = ITUtility.extractSwid(installOutput);
        AccessionMap.accessionMap.put(WORKFLOW, String.valueOf(workflowAccession));
        File exportINIFile = exportINI(new PluginRunnerET(), Lists.newArrayList(workflowAccession));

        String localhost = ITUtility.getLocalhost();
        Log.info("Attempting to launch with wait on host: " + localhost);
        // launch, slightly unlike the tutorial, I'm going to wait to ensure that we have results to export in the next phase
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files "
                + exportINIFile.getAbsolutePath() + " --workflow-accession " + workflowAccession + " --parent-accessions "
                + AccessionMap.accessionMap.get(UserPhase4.FILE) + " --wait --host " + localhost;
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        Log.info(listOutput);
    }

    protected File exportINI(PluginRunnerET pit, List<Integer> accessions) throws IOException {
        // launch our specific workflow and get store its workflow run accession
        File exportINIFile = pit.exportINIFile("Java workflow", accessions.get(0), false);
        return exportINIFile;
    }
}
