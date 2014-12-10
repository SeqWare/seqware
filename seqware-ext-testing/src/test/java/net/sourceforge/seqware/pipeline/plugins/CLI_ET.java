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
import net.sourceforge.seqware.common.module.ReturnValue;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests support the new simplified command-line tools.
 *
 * Try a few simple commands as a sanity check.
 *
 * @author dyuen
 */
public class CLI_ET {

    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Test
    public void runHelp() throws IOException {
        String listCommand = "--help";
        String listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("output contains Usage", listOutput.contains("Usage:") && listOutput.contains("Commands:"));
        Assert.assertTrue("output contains exception", !listOutput.contains("Exception"));
    }

    @Test
    public void checkVersion() throws IOException {
        String listCommand = "--version";
        String listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("output does not contain version", listOutput.startsWith("SeqWare version"));
    }

    @Test
    public void checkEnvironment() throws IOException {
        String listCommand = "--metadata";
        String listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("output does not contain version", listOutput.contains("version") && listOutput.contains("metadata"));
    }

    @Test
    public void listBundles() throws IOException {
        String listCommand = " workflow list";
        String listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        int countOccurrencesOf = StringUtils.countMatches(listOutput, "RECORD");
        Assert.assertTrue("incorrect number of expected bundles", countOccurrencesOf == 20);
    }

    @Test
    public void workflowRunReport() throws IOException {
        String listCommand = " workflow-run report --accession 6603";
        String listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        int countOccurrencesOf = StringUtils.countMatches(listOutput, "RECORD");
        Assert.assertTrue("incorrect number of expected bundles", countOccurrencesOf == 1);
    }

    @Test
    public void runSeqwareCheck() throws IOException {
        String listCommand = " check";
        String listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        int countOccurrencesOf = StringUtils.countMatches(listOutput, "Crashed and failed check");
        Assert.assertTrue("Crashed and failed checks", countOccurrencesOf == 0);
    }
}
