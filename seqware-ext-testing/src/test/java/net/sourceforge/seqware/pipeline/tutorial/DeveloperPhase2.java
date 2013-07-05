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

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import net.sourceforge.seqware.pipeline.plugins.PluginRunnerET;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Build and install a bundle, used by both the User tutorial and the Developer tutorial
 * @author dyuen
 */
public class DeveloperPhase2 {
    
    @Test
    public void testTestingTheWorkflow() throws IOException{
       String tOutput = ITUtility.runSeqWareJar("-p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -b " + DeveloperPhase1.BundleDir.getAbsolutePath() + 
               " -t --workflow seqware-archetype-java-workflow --version 1.0-SNAPSHOT", ReturnValue.SUCCESS, null);
    }
    
    @Test 
    public void testPackagingWorkflow()throws IOException{
        File tempPackageDir = Files.createTempDir();
        String tOutput = ITUtility.runSeqWareJar("-p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -b " + tempPackageDir.getAbsolutePath() + 
               " -p " + DeveloperPhase1.BundleDir.getAbsolutePath() , ReturnValue.SUCCESS, null);
        FileUtils.deleteDirectory(tempPackageDir);
    }
    
    @AfterClass
    public static void cleanup() throws IOException{
        FileUtils.deleteDirectory(DeveloperPhase1.BundleDir);
        PluginRunnerET.monitorAndClean(false);
    }
}
