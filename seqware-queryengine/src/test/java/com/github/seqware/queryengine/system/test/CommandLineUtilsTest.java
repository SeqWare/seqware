/*
 * Copyright (C) 2012 SeqWare
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
package com.github.seqware.queryengine.system.test;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.TagSpecSet;
import com.github.seqware.queryengine.system.ReferenceCreator;
import com.github.seqware.queryengine.system.TagSpecSetCreator;
import com.github.seqware.queryengine.util.SGID;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Simple tests for some simple utilities for the back-end
 *
 * @author dyuen
 */
public class CommandLineUtilsTest {

    @Test
    public void createtagSetViaCommandLine() {
        try {
            File outputFile = File.createTempFile("keyValue", "out");
            SGID tagSetID = TagSpecSetCreator.mainMethod(new String[]{"ad_hoc_tagSet", outputFile.getAbsolutePath()});
            TagSpecSet tagSet = SWQEFactory.getQueryInterface().getAtomBySGID(TagSpecSet.class, tagSetID);
            Assert.assertTrue("command-line util should create empty set " + tagSet.getCount(), tagSet.getCount() == 0);
        } catch (IOException ex) {
            Logger.getLogger(CommandLineUtilsTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.assertTrue("IOException", false);
        }   
    }
    
    @Test
    public void createReferenceViaCommandLine() {
        try {
            File outputFile = File.createTempFile("keyValue", "out");
            SGID refID = ReferenceCreator.mainMethod(new String[]{"hg_19", outputFile.getAbsolutePath()});
            Reference reference = SWQEFactory.getQueryInterface().getAtomBySGID(Reference.class, refID);
            Assert.assertTrue("command-line util should create empty reference ", reference.getCount() == 0);
        } catch (IOException ex) {
            Logger.getLogger(CommandLineUtilsTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.assertTrue("IOException", false);
        }   
    }
}
