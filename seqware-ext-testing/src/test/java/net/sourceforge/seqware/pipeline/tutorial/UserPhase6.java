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

import java.io.IOException;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import org.junit.Test;

/**
 * Tests downloading results in the tutorial
 * 
 * @author dyuen
 */
public class UserPhase6 {

    @Test
    public void testMonitorWorkflowRuns() throws IOException {
        String output = ITUtility.runSeqWareJar(" -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- -wa " + AccessionMap.accessionMap.get(UserAndDeveloperPhase5.WORKFLOW), ReturnValue.SUCCESS);
    }

    
    //TODO: test wra instead of wa, but the previous step we needed to use --wait to ensure we have results to report and --wait does not output a SWID to the command-line
    @Test
    public void testMonitorWorkflowRunStdOut() throws IOException {
        String output = ITUtility.runSeqWareJar(" -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- --wr-stdout -wa " + AccessionMap.accessionMap.get(UserAndDeveloperPhase5.WORKFLOW), ReturnValue.SUCCESS);
    }

    @Test
    public void testMonitorWorkflowRunStdErr() throws IOException {
        String output = ITUtility.runSeqWareJar(" -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- --wr-stderr -wa " + AccessionMap.accessionMap.get(UserAndDeveloperPhase5.WORKFLOW), ReturnValue.SUCCESS);
    }
    
    @Test
    public void testdownloadWorkflowResults() throws IOException {
        String output = ITUtility.runSeqWareJar(" -p net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter -- "
                + " --no-links --output-filename study_report "
                + "--workflow-accession "
                + AccessionMap.accessionMap.get(UserAndDeveloperPhase5.WORKFLOW) + " "
                + "--study 'New Test Study'" 
                , ReturnValue.SUCCESS);
    }
}
