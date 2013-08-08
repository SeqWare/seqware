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
import junit.framework.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests support the new simplified command-line tools
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
        String listCommand = "--help ";
        String listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("output contains Usage", listOutput.contains("Usage:") && listOutput.contains("Commands:"));
        Assert.assertTrue("output contains exception", !listOutput.contains("Exception"));
    }
    
    
}
