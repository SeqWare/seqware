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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import junit.framework.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.modules.utilities.ExternalTestingUtility;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests support command-line tools found in the SeqWare User Tutorial,
 * in this case, SequencerRunReporter
 *
 * @author dyuen
 */
public class WorkflowRunReporterIT {
    
    @BeforeClass
    public static void resetDatabase() {
        ExternalTestingUtility.resetDatabaseWithUsers();
    }

    @Test
    public void runWorkflowRunReporter() throws IOException {
        String randomString = UUID.randomUUID().toString();
        File testOutFile = new File(randomString + ".txt");
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter "
                + "-- --output-filename " + testOutFile.getName() + " --workflow-run-accession 6698"; 
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS);
        Log.info(listOutput);
        File retrievedFile = new File(testOutFile.getName());
        Assert.assertTrue("output file does not exist", retrievedFile.exists());
        List<String> readLines = FileUtils.readLines(testOutFile);
        Assert.assertTrue("incorrect number of lines", readLines.size() == 2);
        long checksumCRC32 = FileUtils.checksumCRC32(testOutFile);
        Assert.assertTrue("incorrect output checksum", checksumCRC32 == 3905672450L);
    }

    
    
}
