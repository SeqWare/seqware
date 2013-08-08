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

import java.io.IOException;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import net.sourceforge.seqware.pipeline.tutorial.UserPhase1;

/**
 *
 * @author dyuen
 */
public class CLIUserPhase1 extends UserPhase1 {

    @Override
    protected String runListTables() throws IOException {
        String output = ITUtility.runSeqwareCLI(" create --help", ReturnValue.SUCCESS, null);
        return output;
    }

    @Override
    protected String runListFields() throws IOException {
        String output = ITUtility.runSeqwareCLI(" create study --help", ReturnValue.SUCCESS, null);
        return output;
    }

    @Override
    protected String runStudyCreation() throws IOException {
        String output = ITUtility.runSeqwareCLI("  create study --title 'New Test Study' --description 'This is a test description' --accession 'InternalID123' --center-name 'SeqWare' --center-project-name 'SeqWare Test Project' --study-type 4", ReturnValue.SUCCESS, null);
        return output;
    }
    
}
