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

import junit.framework.Assert;
import net.sourceforge.seqware.webservice.resources.AbstractResourceTest;
import org.restlet.representation.Representation;

/**
 *
 * @author mtaschuk
 */
public class WorkflowRunsFilterIDTest extends AbstractResourceTest {

    private String workflowId;
    private String runId;

    public WorkflowRunsFilterIDTest() {
        super("/workflows/1/runs/2");
        workflowId = "1";
        runId = "2";
    }

    @Override
    public void testPut() {
        System.out.println(getRelativeURI() + " PUT");
        try {
            Representation rep = resource.put(null);
            rep.write(System.out);
            rep.exhaust();
            rep.release();
            Assert.fail("No PUT on " + getRelativeURI());
        } catch (Exception e) {
            Assert.assertEquals("Method Not Allowed", e.getMessage());
        }
        System.out.println();
    }

    @Override
    public void testGet() {
        System.out.println(getRelativeURI() + " GET");
        try {
            Representation rep = resource.get();
            String result = rep.getText();
            rep.exhaust();
            rep.release();
            Assert.assertTrue("runId is not in representation:" + result, result.contains(runId));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void testPost() {
        System.out.println(getRelativeURI() + " POST");
        try {
            Representation rep = resource.post(null);
            rep.write(System.out);
            rep.exhaust();
            rep.release();
            Assert.fail("No POST on " + getRelativeURI());
        } catch (Exception e) {
            Assert.assertEquals("Method Not Allowed", e.getMessage());
        }
    }

    @Override
    public void testDelete() {
        System.out.println(getRelativeURI() + " DELETE");
        try {
            Representation rep = resource.delete();
            String result = rep.getText();
            rep.exhaust();
            rep.release();
//            Assert.assertTrue("workflowId is not in representation:" + result, result.contains(workflowId));
            Assert.assertTrue("runId is not in representation:" + result, result.contains(runId));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
