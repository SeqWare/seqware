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
 * Build and install a bundle, used by both the User tutorial and the Developer tutorial
 * @author dyuen
 */
public class CLIDeveloperPhase2 extends DeveloperPhase2{
    
    @Override
    protected void testWorkflow() throws IOException {
        ITUtility.runSeqwareCLI(" bundle test  --dir " + DeveloperPhase1.BundleDir.getAbsolutePath(), ReturnValue.SUCCESS, null);
    }

    @Override
    protected void packageBundle(File tempPackageDir) throws IOException {
        ITUtility.runSeqwareCLI(" bundle package --to  " + tempPackageDir.getAbsolutePath() + 
               " --dir " + DeveloperPhase1.BundleDir.getAbsolutePath() , ReturnValue.SUCCESS, null);
    }
}
