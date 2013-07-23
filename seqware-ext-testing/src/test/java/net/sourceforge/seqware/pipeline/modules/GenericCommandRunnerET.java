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
package net.sourceforge.seqware.pipeline.modules;

import com.google.common.base.Splitter;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import junit.framework.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugins.ExtendedTestDatabaseCreator;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests support command-line tools found in the SeqWare User Tutorial,
 * in this case, GenericCommandRunner
 *
 * @author dyuen
 */
public class GenericCommandRunnerET {
    
    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Test
    public void testGenericCommandRunner() throws IOException {
        File createTempDir = Files.createTempDir();

        Random generator = new Random();
        String random = String.valueOf(generator.nextInt());
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.ModuleRunner "
                + "-- --module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner  "
                + "--  --gcr-algorithm test --gcr-command ls";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, createTempDir);
        Splitter splitter = Splitter.on(System.getProperty("line.separator")).trimResults();
        for(String line : splitter.split(listOutput)){
            if (line.equals("bash -lc ls")){
                /** success, found the command */
                return;
            }
        }
        Assert.assertTrue("did not find command in output", false);
    }
    
    @Test
    public void testGCRStdout() throws IOException {
        File createTempDir = Files.createTempDir();

        Random generator = new Random();
        String random = String.valueOf(generator.nextInt());
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.ModuleRunner "
                + "-- --module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner  "
                + "--  --gcr-algorithm test --gcr-command echo Hello World --gcr-stdout";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, createTempDir);
        Splitter splitter = Splitter.on(System.getProperty("line.separator")).trimResults();
        for(String line : splitter.split(listOutput)){
            if (line.equals("Hello World")){
                /** success, found the expected output */
                return;
            }
        }
        
        Assert.assertTrue("did not find expected output in output", false);
        
    }
     
    @Test
    public void testGCRStderr() throws IOException {
        File createTempDir = Files.createTempDir();

        Random generator = new Random();
        String random = String.valueOf(generator.nextInt());
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.ModuleRunner "
                + "-- --module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner  "
                + "--  --gcr-algorithm test --gcr-command ls /home/abcdef --gcr-stderr";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.PROGRAMFAILED, createTempDir);
        Splitter splitter = Splitter.on(System.getProperty("line.separator")).trimResults();
        for(String line : splitter.split(listOutput)){
            if (line.contains("cannot access")){
                /** success, found the expected error output */
                return;
            }
        }
        
        Assert.assertTrue("did not find expected error in output", false);
        
    }

   
    
    
}
