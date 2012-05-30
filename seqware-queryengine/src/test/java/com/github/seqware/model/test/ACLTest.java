package com.github.seqware.model.test;

import com.github.seqware.factory.Factory;
import com.github.seqware.model.*;
import com.github.seqware.util.SeqWareIterable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests of {@link ACL} and {@link ACLable}.
 *
 * @author dyuen
 */
public class ACLTest {

    private static FeatureSet fSet;
    private static Feature f1;
    private static TagSet tSet1;
    private static ReferenceSet rSet;
    private static Reference r1;
    private static Group group, viewerGroup;
    private static User marshmallowUser, titanicUser;
    private static AnalysisSet aSet;

    @BeforeClass
    public static void setupTests() {
        // test ACL on every possible class that can be ACLed
        fSet = Factory.buildFeatureSet(Factory.buildReference("testing dummy reference"));
        Set<Feature> testFeatures = new HashSet<Feature>();
        f1 = new Feature(fSet, 1000000, 1000100);
        testFeatures.add(f1);      
        fSet.add(testFeatures);
        tSet1 = Factory.buildTagSet("Funky tags");
        rSet = Factory.buildReferenceSet("Minbar", "Minbari");
        aSet = Factory.buildAnalysisSet("FP", "Funky program");
        r1 = Factory.buildReference("ref1");
        rSet.add(r1);
        group = new Group("Developers", "Users that are working on new stuff");
        viewerGroup = new Group("Viewers", "Users that are just looking at stuff");
        marshmallowUser = new User(group, "Joe", "Smith", "joe.smith@googly.com", "password");
        titanicUser = new User(group, "Deanna", "Troi", "deanna.troi@googly.com", "password");
        group.add(marshmallowUser);
        viewerGroup.add(titanicUser);
        
        // set groups and owners on some stuff
        fSet.getPermissions().setOwner(marshmallowUser);
        tSet1.getPermissions().setOwner(marshmallowUser);
        tSet1.getPermissions().setGroup(group);
        rSet.getPermissions().setGroup(group);
        // persist everything
        fSet.store();
        f1.store();
        tSet1.store();
        rSet.store();
        aSet.store();
        r1.store();
        group.store();
        marshmallowUser.store();
        // change stuff up 
        fSet.getPermissions().setOwner(titanicUser);
        boolean[] rights = fSet.getPermissions().getAccess();
        Arrays.fill(rights, false);
        fSet.update();
        // change stuff up 
        fSet.getPermissions().setOwner(marshmallowUser);
        rights = fSet.getPermissions().getAccess();
        Arrays.fill(rights, true);
        fSet.update();
    }

    @Test
    public void testACL() {
        boolean[] access = new boolean[6];
        Arrays.fill(access,true);
        // check that everything looks ok
        FeatureSet targetSet = (FeatureSet) Factory.getFeatureStoreInterface().getParticleByUUID(fSet.getUUID());
        // check some versioning while we are at it
        Assert.assertTrue("wrong owner",targetSet.getPermissions().getOwner().equals(marshmallowUser));
        Assert.assertTrue("wrong owner for old version",targetSet.getPrecedingVersion().getPermissions().getOwner().equals(titanicUser));
        Assert.assertTrue("wrong owner for old old version",targetSet.getPrecedingVersion().getPrecedingVersion().getPermissions().getOwner().equals(marshmallowUser));
        boolean[] userpermissions = targetSet.getPermissions().getAccess();
        // why oh why doesn't JUnit assertArrayEquals support boolean?
        Assert.assertTrue("permissions", userpermissions[0] == true && userpermissions [1] == true &&  userpermissions[2] == true && userpermissions [3] == true && userpermissions[4] == true && userpermissions [5] == true );
        userpermissions = targetSet.getPrecedingVersion().getPermissions().getAccess();
        Assert.assertTrue("permissions", userpermissions[0] == false && userpermissions [1] == false &&  userpermissions[2] == false && userpermissions [3] == false && userpermissions[4] == false && userpermissions [5] == false );
    }


}
