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
import java.io.IOException;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;

/**
 *
 * @author dyuen
 */
public class CLIUserPhase3 extends UserPhase3{
        
     @Override
     protected String createSampleAndLinkToExperiment() throws IOException {
        String output = ITUtility.runSeqwareCLI("  create sample --title 'New Test Sample' --description 'This is a test description' --organism-id 26 --experiment-accession "+AccessionMap.accessionMap.get(UserPhase2.EXPERIMENT)
                , ReturnValue.SUCCESS
                , null);
        return output;
    }
}
