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
import org.junit.Ignore;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

/**
 *
 * @author mtaschuk
 */
public class WorkflowRunsFilterTest extends AbstractResourceTest {

    String workflowId;

    public WorkflowRunsFilterTest() {
        super("/workflows/1/runs");
        workflowId = "1";
    }

    @Override
    public void testGet() {
        System.out.println(getRelativeURI() + " GET");
        try {
            Representation rep = resource.get();
            Assert.assertTrue(rep.getText().contains("GET all runs from " + workflowId));
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
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
    }

    @Ignore
    @Override
    public void testPost() {
        System.out.println(getRelativeURI() + " POST");
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("# key=input_file:type=file:display=F:file_meta_type=text/plain\n");
            builder.append("input_file=${workflow_bundle_dir}/bundle_hello_world/0.9.1/data/input.txt\n");
            builder.append("# key=greeting:type=text:display=T:display_name=Greeting\n");
            builder.append("greeting=Testing\n");
            builder.append("# this is just a comment, the output directory is a conventions and used in many workflows to specify a relative output path\n");
            builder.append("output_dir=seqware-results\n");
            builder.append("# the output_prefix is a convension and used to specify the root of the absolute output path or an S3 bucket name\n");
            builder.append("# you should pick a path that is available on all custer nodes and can be written by your user\n");
            builder.append("output_prefix=./\n");

            StringRepresentation myString = new StringRepresentation(builder.toString());
            Representation rep = resource.post(myString);
            Assert.assertTrue(rep.getText().contains("POST run Test test test"));
            myString.write(System.out);
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void testDelete() {
        System.out.println(getRelativeURI() + " DELETE");
        try {
            Representation rep = resource.delete();
            rep.write(System.out);
            rep.exhaust();
            rep.release();
            Assert.fail("No DELETE on " + getRelativeURI());
        } catch (Exception e) {
            Assert.assertEquals("Method Not Allowed", e.getMessage());
        }
    }
}
