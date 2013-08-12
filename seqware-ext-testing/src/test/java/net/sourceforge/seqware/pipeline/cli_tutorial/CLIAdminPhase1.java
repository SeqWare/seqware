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

import java.io.File;
import java.io.IOException;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import net.sourceforge.seqware.pipeline.tutorial.AdminPhase1;

/**
 * Do all tests that can be concurrently done in the admin tutorial
 *
 * @author dyuen
 */
public class CLIAdminPhase1 extends AdminPhase1{
    
    

    @Override
    protected String installBundle(File zippedBundle) throws IOException {
        String installCommand = " bundle install --zip " + zippedBundle;
        String installOutput = ITUtility.runSeqwareCLI(installCommand, ReturnValue.SUCCESS, null);
        return installOutput;
    }

    @Override
    protected String launchScheduled() throws IOException {
        // launch-scheduled
        String schedCommand = " workflow-run launch-scheduled";
        String schedOutput = ITUtility.runSeqwareCLI(schedCommand, ReturnValue.SUCCESS, null);
        return schedOutput;
    }

    @Override
    protected String statusCheck() throws IOException {
        String listCommand = " workflow-run propagate-statuses --threads 1000";
        String listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        return listOutput;
    }
}
