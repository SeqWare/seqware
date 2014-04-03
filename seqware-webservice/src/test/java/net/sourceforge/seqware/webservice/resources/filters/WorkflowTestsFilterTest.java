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
package net.sourceforge.seqware.webservice.resources.filters;

import net.sourceforge.seqware.webservice.resources.AbstractResourceTest;

/**
 *
 * @author mtaschuk
 */
public class WorkflowTestsFilterTest extends AbstractResourceTest {

    String workflowId;

    public WorkflowTestsFilterTest() {
        super("/workflows/1/tests");
        workflowId = "1";
    }

    @Override
    public void testGet() {
        System.out.println(getRelativeURI() + " GET");
//        try {
//            Assert.assertTrue(resource.get().getText().contains("GET all tests from " + workflowId));
//        } catch (Exception e) {
//            Assert.fail(e.getMessage());
//            e.printStackTrace();
//        }
    }

    @Override
    public void testPut() {
        System.out.println(getRelativeURI() + " PUT");
//        try {
//            resource.put(null).write(System.out);
//            Assert.fail("No PUT on " + getRelativeURI());
//        } catch (Exception e) {
//            Assert.assertEquals("Method Not Allowed", e.getMessage());
//        }
    }

    @Override
    public void testPost() {
        System.out.println(getRelativeURI() + " POST");
//        try {
//            StringRepresentation myString = new StringRepresentation("Test test test");
//            Assert.assertTrue(resource.post(myString).getText().contains("POST test Test test test"));
//        } catch (Exception e) {
//            Assert.fail(e.getMessage());
//            e.printStackTrace();
//        }
    }

//    @Override
    public void testDelete() {
        System.out.println(getRelativeURI() + " DELETE");
//        try {
//            resource.delete().write(System.out);
//            Assert.fail("No DELETE on " + getRelativeURI());
//        } catch (Exception e) {
//            Assert.assertEquals("Method Not Allowed", e.getMessage());
//        }
    }
}
