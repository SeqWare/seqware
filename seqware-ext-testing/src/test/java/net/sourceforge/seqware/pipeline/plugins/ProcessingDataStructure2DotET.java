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
import org.junit.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests support the processing data structure2dot utility.
 * 
 * @author dyuen
 */
public class ProcessingDataStructure2DotET {

    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Test
    public void getHelp() throws IOException {
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.ProcessingDataStructure2Dot";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDARGUMENT, null);
        Assert.assertTrue("did not report help message", listOutput.length() > 0);
    }

    @Test
    public void getInvalidAccession() throws IOException {
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.ProcessingDataStructure2Dot -- --parent-accession 1";
        ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDPARAMETERS, null);
    }

    @Test
    public void getStandardOutput() throws IOException {
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.ProcessingDataStructure2Dot -- --parent-accession 5174";
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
    }

}
