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
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.system.ReferenceCreator;
import com.github.seqware.queryengine.system.TagSetCreator;
import com.github.seqware.queryengine.util.SGID;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Simple tests for some simple utilities for the back-end
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class CommandLineUtilsTest {

    private static String randomRef1 = null;
    private static String randomRef2 = null;
    private static String randomRef3 = null;
    private static String randomRef4 = null;

    /**
     * <p>setupTests.</p>
     */
    @BeforeClass
    public static void setupTests() {
        randomRef1 = "Random_ref_" + new BigInteger(20, new SecureRandom()).toString(32);
        randomRef2 = "Random_ref_" + new BigInteger(20, new SecureRandom()).toString(32);
        randomRef3 = "Random_ref_" + new BigInteger(20, new SecureRandom()).toString(32);
        randomRef4 = "Random_ref_" + new BigInteger(20, new SecureRandom()).toString(32);
    }

    /**
     * <p>createtagSetViaCommandLine.</p>
     */
    @Test
    public void createtagSetViaCommandLine() {
        try {
            File outputFile = File.createTempFile("keyValue", "out");
            SGID tagSetID = TagSetCreator.mainMethod(new String[]{randomRef1, outputFile.getAbsolutePath()});
            TagSet tagSet = SWQEFactory.getQueryInterface().getAtomBySGID(TagSet.class, tagSetID);
            Assert.assertTrue("command-line util should create empty set " + tagSet.getCount(), tagSet.getCount() == 0);
        } catch (IOException ex) {
            Logger.getLogger(CommandLineUtilsTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.assertTrue("IOException", false);
        }
    }

    /**
     * <p>createReferenceViaCommandLine.</p>
     */
    @Test
    public void createReferenceViaCommandLine() {
        try {
            File outputFile = File.createTempFile("keyValue", "out");
            SGID refID = ReferenceCreator.mainMethod(new String[]{randomRef2, outputFile.getAbsolutePath()});
            Reference reference = SWQEFactory.getQueryInterface().getAtomBySGID(Reference.class, refID);
            Assert.assertTrue("command-line util should create empty reference ", reference.getCount() == 0);
        } catch (IOException ex) {
            Logger.getLogger(CommandLineUtilsTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.assertTrue("IOException", false);
        }
    }

    /**
     * <p>checkExistingTagSetViaCommandLine.</p>
     */
    @Test
    public void checkExistingTagSetViaCommandLine() {
        try {
            File outputFile = File.createTempFile("keyValue", "out");
            SGID tagSetID = TagSetCreator.mainMethod(new String[]{randomRef3, outputFile.getAbsolutePath()});
            TagSet tagSet = SWQEFactory.getQueryInterface().getAtomBySGID(TagSet.class, tagSetID);
            Assert.assertTrue("command-line util should create empty set " + tagSet.getCount(), tagSet.getCount() == 0);

            // check that re-creation of the same key fails
            try {
                SGID tagSetID2 = TagSetCreator.mainMethod(new String[]{randomRef3, outputFile.getAbsolutePath()});
            } catch (IllegalArgumentException ex) {
                /** we actually want an exception here */
                return;
            }

            Assert.assertTrue("creation of repeated key did not fail", false);
        } catch (IOException ex) {
            Logger.getLogger(CommandLineUtilsTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.assertTrue("IOException", false);
        }
    }

    /**
     * <p>checkExistingReferenceViaCommandLine.</p>
     */
    @Test
    public void checkExistingReferenceViaCommandLine() {
        try {
            File outputFile = File.createTempFile("keyValue", "out");
            SGID refID = ReferenceCreator.mainMethod(new String[]{randomRef4, outputFile.getAbsolutePath()});
            Reference reference = SWQEFactory.getQueryInterface().getAtomBySGID(Reference.class, refID);
            Assert.assertTrue("command-line util should create empty reference ", reference.getCount() == 0);
            
            // check that re-creation of the same key fails
            try {
                SGID tagSetID2 = ReferenceCreator.mainMethod(new String[]{randomRef4, outputFile.getAbsolutePath()});
            } catch (IllegalArgumentException ex) {
                /** we actually want an exception here */
                return;
            }

            Assert.assertTrue("creation of repeated key did not fail", false);
        } catch (IOException ex) {
            Logger.getLogger(CommandLineUtilsTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.assertTrue("IOException", false);
        }
    }
}
