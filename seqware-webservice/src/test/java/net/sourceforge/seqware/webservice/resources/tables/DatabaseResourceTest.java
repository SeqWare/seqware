/*
 * Copyright (C) 2011 SeqWare
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
package net.sourceforge.seqware.webservice.resources.tables;

import java.io.IOException;
import junit.framework.Assert;
import net.sourceforge.seqware.webservice.resources.AbstractResourceTest;
import org.junit.Test;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

/**
 *
 * @author mtaschuk
 */
public abstract class DatabaseResourceTest extends AbstractResourceTest {

    public DatabaseResourceTest(String relativeURI) {
        super(relativeURI);
    }

    /**
     * Test of get method, of class WorkflowResource.
     */
    @Override
    public void testGet() {
        System.out.println(getRelativeURI() + " GET");
        Representation rep = null;
        try {
            rep = resource.get();
            String result = rep.getText();
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    
//    /**
//     * Test of post method, of class WorkflowResource.
//     */
//    @Override
//    public void testPost() {
//        System.out.println(getRelativeURI() + " POST");
//        Representation rep = null;
//        try {
//            StringRepresentation myString = new StringRepresentation("Test test test");
//            rep = resource.post(myString);
//            String result = rep.getText();
//            rep.exhaust();
//            rep.release();
//            Assert.assertTrue(result.contains("Test test test"));
//        } catch (Exception e) {
//            Assert.fail(e.getMessage());
//            e.printStackTrace();
//        }
//    }

        public void testPost() {
        System.out.println(getRelativeURI() + " POST");
        Representation rep = null;
        try {
            StringRepresentation myString = new StringRepresentation("Test test test");
            rep = resource.post(myString);
            rep.exhaust();
            rep.release();
            Assert.fail("No POST on " + getRelativeURI());
        } catch (Exception e) {
            Assert.assertEquals("Method Not Allowed", e.getMessage());
        }
    }
    
    
    @Override
    public void testPut() {
        System.out.println(getRelativeURI() + " PUT");
        Representation rep = null;
        try {
            rep = resource.put(null);
            rep.exhaust();
            rep.release();
            Assert.fail("No PUT on " + getRelativeURI());
        } catch (Exception e) {
            Assert.assertEquals("Method Not Allowed", e.getMessage());
        }
    }

    @Override
    public void testDelete() {
        System.out.println(getRelativeURI() + " DELETE");
        Representation rep = null;
        try {
            rep = resource.delete();
            rep.exhaust();
            rep.release();
            Assert.fail("No DELETE on " + getRelativeURI());
        } catch (Exception e) {
            Assert.assertEquals("Method Not Allowed", e.getMessage());
        }
    }
}
