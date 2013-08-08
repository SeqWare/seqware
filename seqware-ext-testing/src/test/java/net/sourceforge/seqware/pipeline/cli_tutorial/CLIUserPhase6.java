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
package net.sourceforge.seqware.pipeline.cli_tutorial;

import net.sourceforge.seqware.pipeline.tutorial.*;
import java.io.File;
import java.io.IOException;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;

/**
 * Tests downloading results in the tutorial
 *
 * @author dyuen
 */
public class CLIUserPhase6 extends UserPhase6{

  

    @Override
    protected void runWorkflowRunReporter(File workingDir) throws IOException {
        String output = ITUtility.runSeqwareCLI(" workflow report --accession " + AccessionMap.accessionMap.get(UserPhase5.WORKFLOW)
                , ReturnValue.SUCCESS
                , workingDir);
    }

    @Override
    protected void runWorkflowRunReporterStdErr(String swid, File workingDir) throws IOException {
        String output = ITUtility.runSeqwareCLI(" workflow-run stderr --accession " + swid
                , ReturnValue.SUCCESS
                , workingDir);
    }

    @Override
    protected void runWorkflowRunReporterStdOut(String swid, File workingDir) throws IOException {
        String output = ITUtility.runSeqwareCLI(" workflow-run stdout --accession " + swid
                , ReturnValue.SUCCESS
                , workingDir);
    }

    @Override
    protected void exportStudyResults() throws IOException {
        String output = ITUtility.runSeqwareCLI(" files report --study 'New Test Study'", ReturnValue.SUCCESS, null);
    }

    
}
