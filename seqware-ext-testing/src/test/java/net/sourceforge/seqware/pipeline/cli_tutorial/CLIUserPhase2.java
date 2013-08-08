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
import net.sourceforge.seqware.pipeline.tutorial.AccessionMap;
import net.sourceforge.seqware.pipeline.tutorial.UserPhase1;
import net.sourceforge.seqware.pipeline.tutorial.UserPhase2;

/**
 *
 * @author dyuen
 */
public class CLIUserPhase2 extends UserPhase2 {

    @Override
    protected String runCreateExperimentAndLinkStudy() throws IOException {
        String output = ITUtility.runSeqwareCLI(" create experiment --title 'New Test Experiment' --description 'This is a test description' --platform-id 26 --study-accession " + AccessionMap.accessionMap.get(UserPhase1.STUDY),
                ReturnValue.SUCCESS, null);
        return output;
    }
}
