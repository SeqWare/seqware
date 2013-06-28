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
import net.sourceforge.seqware.metadb.util.TestDatabaseCreator;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests support command-line tools
 * in this case, BundleManager
 *
 * @author dyuen
 */
public class BundleManagerET {
    
    @BeforeClass
    public static void resetDatabase() {
         ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }
    

    @Test
    public void runInvalidParameters() throws IOException {
        // cannot use install and install-dir-only at the same time SEQWARE-1632
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.BundleManager "
                + "-- --install --install-dir-only --bundle does_not_matter";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDPARAMETERS, null);
        Assert.assertTrue("output contains exception", !listOutput.contains("Exception"));
        
    }
    
    
}
