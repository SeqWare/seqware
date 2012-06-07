package com.github.seqware.model.test;

import com.github.seqware.factory.Factory;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.Group;
import com.github.seqware.model.User;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests of User.
 *
 * @author dyuen
 */
public class UserGroupTest {

    private static User a1, a2, a3;
    private static Group g1,g2; 

    @BeforeClass
    public static void setupTests() {
//        Logger.getLogger(UserGroupTest.class.getName()).log(Level.INFO, "@BeforeClass");
        ModelManager mManager = Factory.getModelManager();
        g1 = mManager.buildGroup().setName("Developers").setDescription("Group for Developers").build();
        g2 = mManager.buildGroup().setName("Variant-Developers").setDescription("Group for Developers").build();
        a1 = mManager.buildUser().setFirstName("Joe").setLastName("Smith").setEmailAddress("smith@googly.com").setPassword("password").build();
        a2 = mManager.buildUser().setFirstName("bev").setLastName("Smith").setEmailAddress("bev@googly.com").setPassword("password").build();
        a3 = mManager.buildUser().setFirstName("Tim").setLastName("Smith").setEmailAddress("tim@googly.com").setPassword("password").build();
        g1.add(a1, a2, a3);
        // persisting users and group to back-end
        mManager.flush();
        mManager.close();
    }

    @Test
    public void testUserCreation() {
//        Logger.getLogger(UserGroupTest.class.getName()).log(Level.INFO, "@Test");
        // check that Users are present match
        boolean b1 = false;
        boolean b2 = false;
        boolean b3 = false;
        for (User u : Factory.getFeatureStoreInterface().getUsers()) {
            if (u.equals(a1)) {
                b1 = true;
            } else if (u.equals(a2)) {
                b2 = true;
            } else if (u.equals(a3)) {
                b3 = true;
            }
        }
        Assert.assertTrue(b1 && b2 && b3);
    }
    
    @Test
    public void testGroupCreation() {
//       Logger.getLogger(UserGroupTest.class.getName()).log(Level.INFO, "@Test");
        // check that Group are present match
        boolean b1 = false;
        for (Group u : Factory.getFeatureStoreInterface().getGroups()) {
            if (u.equals(g1)) {
                b1 = true;
            } 
        }
        Assert.assertTrue(b1);
    }

    @Test
    public void testUserPasswordChanging(){
//        Logger.getLogger(UserGroupTest.class.getName()).log(Level.INFO, "@Test");
        ModelManager mManager = Factory.getModelManager();
        String password1 = "ITMfL";
        User n1 = mManager.buildUser().setFirstName("Cheung").setLastName("Man-Yuk").setEmailAddress("cmy@googly.com").setPassword(password1).build();
        mManager.flush();
        User oldUser = n1;
        // check current User's password
        Assert.assertTrue(n1.checkPassword(password1));  
        String password2 = "2046";
        n1 = n1.toBuilder().setPassword(password2).build();
        n1.setPrecedingVersion(oldUser);
        mManager.flush();
        // check new current User's password
        Assert.assertTrue(n1.checkPassword(password2));  
        // check old User's password via Versionable interface
        Assert.assertTrue(n1.getPrecedingVersion().checkPassword(password1));  
        // check old User's password by re-retrieving it
        User oldN1 = (User) Factory.getFeatureStoreInterface().getParticleBySGID(oldUser.getSGID());
        Assert.assertTrue(oldN1.checkPassword(password1));  
    }
}
