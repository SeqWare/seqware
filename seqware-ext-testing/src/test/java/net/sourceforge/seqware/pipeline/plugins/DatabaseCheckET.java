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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests support the database check utility.
 * 
 * @author dyuen
 */
public class DatabaseCheckET {

    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Test
    public void runDatabaseCheck() throws IOException {
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDB";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        Log.info(listOutput);
        String[] split = listOutput.split("Printed report to ");
        File report = new File(split[split.length - 1].trim());
        String readFileToString = FileUtils.readFileToString(report, (Charset) null);
        Assert.assertTrue("report empty", readFileToString.length() > 0);
        Assert.assertTrue("report does not contain report", readFileToString.contains("CheckDB Report"));
        Assert.assertTrue("report contains exceptions", !readFileToString.contains("Exception"));
        FileUtils.deleteQuietly(report);
    }

}
