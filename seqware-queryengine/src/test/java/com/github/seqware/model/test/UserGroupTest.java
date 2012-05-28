package com.github.seqware.model.test;

import com.github.seqware.factory.Factory;
import com.github.seqware.model.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests of User.
 *
 * @author dyuen
 */
public class UserGroupTest {

    private static User a1, a2, a3, a4;
    private static Group g1; 

    @BeforeClass
    public static void setupTests() {
        
        g1 = new Group("Developers", "Group for Developers");
        a1 = new User(g1, "Joe", "Smith", "smith@googly.com", "password" );
        a2 = new User(g1, "bev", "Smith", "bev@googly.com", "password" );
        a3 = new User(g1, "Tim", "Smith", "tim@googly.com", "password" );
        a4 = new User(g1, "Mao", "Smith", "mao@googly.com", "password" );
        g1.add(a1, a2, a3, a4);
        // persisting users and group to back-end
        a1.add();
        a2.add();
        a3.add();
        a4.add();
        g1.add();
    }

    @Test
    public void testUserCreation() {
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
        User n1 = new User(g1, "Cheung", "Man-Yuk", "cmy@googly.com", "ITMfL" );
        n1.add();
        // check current User's password
        Assert.assertTrue(n1.checkPassword("ITMfL"));  
        n1.setPassword("2046");
        User n1_v1 = n1.update();
        // check current User's password
        Assert.assertTrue(n1_v1.checkPassword("2046"));  
    }
}
