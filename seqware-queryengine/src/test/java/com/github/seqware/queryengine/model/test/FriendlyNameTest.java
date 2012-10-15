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
package com.github.seqware.queryengine.model.test;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Group;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.ReferenceSet;
import com.github.seqware.queryengine.model.TagSet;
import java.math.BigInteger;
import java.security.SecureRandom;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit Tests for how a user would interact with friendly row keys.
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class FriendlyNameTest {

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
     * <p>createFriendlyKeysTest.</p>
     */
    @Test
    public void createFriendlyKeysTest(){
        // create friendly keys for all applicable objects
        CreateUpdateManager modelManager = SWQEFactory.getModelManager();
        Group group = modelManager.buildGroup().setFriendlyRowKey(randomRef1).build();
        Reference reference = modelManager.buildReference().setName(randomRef2).setFriendlyRowKey(randomRef2).build();
        ReferenceSet referenceSet = modelManager.buildReferenceSet().setFriendlyRowKey(randomRef3).build();
        TagSet tagSet = modelManager.buildTagSet().setFriendlyRowKey(randomRef4).build();
        modelManager.flush();
        
        // check that objects have been persisted properly by SGID
        Group qGroup = SWQEFactory.getQueryInterface().getAtomBySGID(Group.class , group.getSGID());
        Assert.assertTrue("Group persist and retrieval failed", qGroup.equals(group));
        // check that objects have been persisted properly by rowKey
        Group rGroup = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(randomRef1, Group.class);
        Reference rRef = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(randomRef2, Reference.class);
        ReferenceSet rRefSet = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(randomRef3, ReferenceSet.class);
        TagSet rTagSet = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(randomRef4, TagSet.class);
        Assert.assertTrue("Group persist and retrieval failed", rGroup.equals(group));
        Assert.assertTrue("Reference persist and retrieval failed", rRef.equals(reference));
        Assert.assertTrue("ReferenceSet persist and retrieval failed", rRefSet.equals(referenceSet));
        Assert.assertTrue("TagSet persist and retrieval failed", rTagSet.equals(tagSet));        
    }
}
