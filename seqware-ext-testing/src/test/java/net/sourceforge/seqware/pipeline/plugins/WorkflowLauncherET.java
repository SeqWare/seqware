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
import junit.framework.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.metadb.util.TestDatabaseCreator;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests support command-line tools
 * in this case, WorkflowLauncher
 *
 * @author dyuen
 */
public class WorkflowLauncherET {
    
    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }
    

    @Test
    public void runInvalidParameters() throws IOException {
        // this should not NullPointerException SEQWARE-1646
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher "
                + "-- --no-metadata --provisioned-bundle-dir /.mounts/labs/PDE/private/mtaschuk/tmp/Test/target/Workflow_Bundle_Test_1.0-SNAPSHOT_SeqWare_0.13.6.6 --workflow Test --version 1.0-SNAPSHOT --ini-files /.mounts/labs/PDE/private/mtaschuk/tmp/Test/target/Workflow_Bundle_Test_1.0-SNAPSHOT_SeqWare_0.13.6.6/Workflow_Bundle_Test/1.0-SNAPSHOT/config/workflow.ini --metadata-output-dir /u/mtaschuk --metadata-file-output-prefix TestTest";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDARGUMENT, null);
        Assert.assertTrue("output contains exception", !listOutput.contains("Exception"));
        
        // this should not NullPointerException SEQWARE-1516
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher "
                + "-- --wa 1 --test ";
        listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDARGUMENT, null);
        Assert.assertTrue("output contains exception", !listOutput.contains("Exception"));
        
    }
    
    
}
