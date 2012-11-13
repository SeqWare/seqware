package com.github.seqware.queryengine.model.test;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.*;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.interfaces.ACL;
import com.github.seqware.queryengine.model.interfaces.ACLable;
import com.github.seqware.queryengine.plugins.inmemory.InMemoryFeaturesAllPlugin;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests of {@link com.github.seqware.queryengine.model.interfaces.ACLable}.
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class ACLTest {

    private static FeatureSet fSet;
    private static Feature f1;
    private static TagSet tSet1;
    private static ReferenceSet rSet;
    private static Reference r1;
    private static Group group, viewerGroup;
    private static User marshmallowUser, titanicUser;
    private static AnalysisType aSet;
    private static AnalysisRun a1;

    /**
     * <p>setupTests.</p>
     */
    @BeforeClass
    public static void setupTests() {
//        Logger.getLogger(ACLTest.class.getName()).info( "@BeforeClass");
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        // test ACL on every possible class that can be ACLed
        fSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("testing_dummy_reference").build()).build();
        Set<Feature> testFeatures = new HashSet<Feature>();
        f1 = mManager.buildFeature().setSeqid("chr16").setStart(1000000).setStop(1000100).build();
        testFeatures.add(f1);
        fSet.add(testFeatures);
        tSet1 = mManager.buildTagSet().setName("Funky tags").build();
        rSet = mManager.buildReferenceSet().setName("Minbar").setOrganism("Minbari").build();
        aSet = mManager.buildAnalysisType().setName("FP").setDescription("Funky program").build();
        r1 = mManager.buildReference().setName("ref1").build();
        rSet.add(r1);
        group = mManager.buildGroup().setName("Developers").setDescription("Users that are working on new stuff").build();
        viewerGroup = mManager.buildGroup().setName("Viewers").setDescription("Users that are just looking at stuff").build();
        marshmallowUser = mManager.buildUser().setFirstName("Joe").setLastName("Smith").setEmailAddress("joe.smith@googly.com").setPassword("password").build();
        titanicUser = mManager.buildUser().setFirstName("Deanna").setLastName("Troi").setEmailAddress("deanna.troi@googly.com").setPassword("password").build();
        a1 = mManager.buildAnalysis().setParameters(new ArrayList()).setPlugin(new InMemoryFeaturesAllPlugin()).build();
        group.add(marshmallowUser);
        viewerGroup.add(titanicUser);

        // set groups and owners on some stuff
        fSet.setPermissions(fSet.getPermissions().toBuilder().setOwner(marshmallowUser).build());
        tSet1.setPermissions(tSet1.getPermissions().toBuilder().setOwner(marshmallowUser).setGroup(group).build());
        rSet.setPermissions(rSet.getPermissions().toBuilder().setGroup(group).build());
        mManager.flush();
        // change stuff up 
        fSet.setPermissions(fSet.getPermissions().toBuilder().setOwner(titanicUser).setRights(new boolean[]{false, false, false, false, false, false}).build());
        mManager.flush();
        // change stuff up 
        fSet.setPermissions(fSet.getPermissions().toBuilder().setOwner(marshmallowUser).setRights(new boolean[]{true, true, true, true, true, true}).build());
        mManager.close();
    }

    /**
     * <p>testACLWithVersions.</p>
     */
    @Test
    public void testACLWithVersions() {
//        Logger.getLogger(ACLTest.class.getName()).info( "@Test");
        // check that everything looks ok
        FeatureSet targetSet = (FeatureSet) SWQEFactory.getQueryInterface().getAtomBySGID(FeatureSet.class, fSet.getSGID());
        // check some versioning while we are at it
        Assert.assertTrue("wrong owner", targetSet.getPermissions().getOwner().equals(marshmallowUser));
        Assert.assertTrue("wrong owner for old version", targetSet.getPrecedingVersion().getPermissions().getOwner().equals(titanicUser));
        Assert.assertTrue("wrong owner for old old version", targetSet.getPrecedingVersion().getPrecedingVersion().getPermissions().getOwner().equals(marshmallowUser));
        List<Boolean> userpermissions = targetSet.getPermissions().getAccess();
        // why oh why doesn't JUnit assertArrayEquals support boolean?
        Assert.assertTrue("permissions", userpermissions.get(0) == true && userpermissions.get(1) == true && userpermissions.get(2) == true && userpermissions.get(3) == true && userpermissions.get(4) == true && userpermissions.get(5) == true);
        userpermissions = targetSet.getPrecedingVersion().getPermissions().getAccess();
        Assert.assertTrue("permissions", userpermissions.get(0) == false && userpermissions.get(1) == false && userpermissions.get(2) == false && userpermissions.get(3) == false && userpermissions.get(4) == false && userpermissions.get(5) == false);
        
    }

    /**
     * <p>testACLWithAllObjects.</p>
     */
    @Test
    public void testACLWithAllObjects() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        Group newGroup = mManager.buildGroup().setName("Oversight").setDescription("Users that are monitoring everyone").build();
        User newUser = mManager.buildUser().setFirstName("Madeline").setLastName("Pierce").setEmailAddress("madeline.pierce@googly.com").setPassword("password").build();
        
        ACL acl = fSet.getPermissions().toBuilder().setOwner(newUser).setGroup(newGroup).build();
        Assert.assertTrue(!(f1 instanceof ACLable));
        Molecule[] mols = {a1, fSet, tSet1, rSet, r1, r1, group, viewerGroup, marshmallowUser, titanicUser, aSet, newGroup, newUser};
        for(Molecule mol : mols){
            mManager.persist(mol);
            mol.setPermissions(acl);
        }
        mManager.flush();
        for(Molecule mol : mols){
            Molecule molFromBackEnd = (Molecule) SWQEFactory.getQueryInterface().getAtomBySGID(((MoleculeImpl)mol).getHBaseClass(), mol.getSGID());
            try{
                Assert.assertTrue(molFromBackEnd.getPermissions().getOwner().equals(newUser));
                Assert.assertTrue(molFromBackEnd.getPermissions().getGroup().equals(newGroup));
            }catch(Exception e){
              System.out.println();  
            }
        }
        User newUser2 = mManager.buildUser().setFirstName("M").setLastName("").setEmailAddress("M@googly.com").setPassword("password").build();
        acl = acl.toBuilder().setOwner(newUser2).setGroup(newGroup).build();
        for(Molecule mol : mols){
            mol.setPermissions(acl);
        }
        mManager.flush();
        for(Molecule mol : mols){
            Molecule molFromBackEnd = (Molecule) SWQEFactory.getQueryInterface().getAtomBySGID(((MoleculeImpl)mol).getHBaseClass(), mol.getSGID());
            Assert.assertTrue(molFromBackEnd.getPermissions().getOwner().equals(newUser2));
            // TODO: not sure why we need the cast here when it works for the subclasses, something has gone awry in template classes land?
            Assert.assertTrue(((Molecule)molFromBackEnd.getPrecedingVersion()).getPermissions().getOwner().equals(newUser));
            Assert.assertTrue(molFromBackEnd.getPermissions().getGroup().equals(newGroup));
        }
        mManager.close();
    }
}
