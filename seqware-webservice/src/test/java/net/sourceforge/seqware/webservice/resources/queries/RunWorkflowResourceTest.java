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
package net.sourceforge.seqware.webservice.resources.queries;

import junit.framework.Assert;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList2;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.tables.DatabaseResourceIDTest;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

/**
 *
 * @author mtaschuk
 */
public class RunWorkflowResourceTest extends DatabaseResourceIDTest {

    public RunWorkflowResourceTest() {
        super("/workflows/2860/runs");
    }

    @Override
    public void testGet() {
        System.out.println(getRelativeURI() + " GET");
        jo = new JaxbObject<WorkflowRunList2>();
        o = new WorkflowRunList2();
        Representation rep = null;
        try {
            rep = resource.get();
            String text = rep.getText();
            System.out.println(text);
            o = XmlTools.unMarshal(jo, o, text);

            int result = testObject(o);
            if (result == ReturnValue.INVALIDFILE) {
                junit.framework.Assert.fail("IDs are not equal. See System.err for more information.");
            } else if (result == ReturnValue.FILENOTREADABLE) {
                junit.framework.Assert.fail("Returned object is not correct instance. See System.err for more information.");
            }

            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            e.printStackTrace();
            junit.framework.Assert.fail(e.getMessage());

        }
    }

    @Override
    public void testPost() {
        System.out.println(getRelativeURI() + " POST");
        jo = new JaxbObject<ReturnValue>();
        o = new ReturnValue();
        Representation rep = null;
        try {
            String iniFile = "one=true\ntwo=my_shoe\nthree=yippee";

            ClientResource childResource = resource.getChild("?parent-accessions=4765,4707&link-workflow-run-to-parents=4765,4707&wait=false");
            rep = childResource.post(iniFile, MediaType.TEXT_ALL);
            String text = rep.getText();
            System.out.println(text);
            o = XmlTools.unMarshal(jo, o, text);

            ReturnValue ret = (ReturnValue) o;
            if (ret.getReturnValue() == 0) {
                Assert.fail("Return Value is 0 - should be workflow run ID");
            }

            int result = ret.getExitStatus();
            if (result == ReturnValue.INVALIDFILE) {
                junit.framework.Assert.fail("IDs are not equal. See System.err for more information.");
            } else if (result == ReturnValue.FILENOTREADABLE) {
                junit.framework.Assert.fail("Returned object is not correct instance. See System.err for more information.");
            }

            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            e.printStackTrace();
            junit.framework.Assert.fail(e.getMessage());

        }
    }

    @Test
    public void testPostNoMetadata() {
        System.out.println(getRelativeURI() + " POST");
        jo = new JaxbObject<ReturnValue>();
        o = new ReturnValue();
        Representation rep = null;
        try {
            String iniFile = "one=true\ntwo=my_shoe\nthree=yippee";

            ClientResource childResource = resource.getChild("?no-metadata=true&parent-accessions=4765,4707&link-workflow-run-to-parents=4765,4707&wait=false");
            rep = childResource.post(iniFile, MediaType.TEXT_ALL);
            String text = rep.getText();
            System.out.println(text);
            o = XmlTools.unMarshal(jo, o, text);

            ReturnValue ret = (ReturnValue) o;
            if (ret.getReturnValue() == 0) {
                Assert.fail("Return Value is 0 - should be workflow run ID");
            }

            int result = ret.getExitStatus();
            if (result == ReturnValue.INVALIDFILE) {
                junit.framework.Assert.fail("IDs are not equal. See System.err for more information.");
            } else if (result == ReturnValue.FILENOTREADABLE) {
                junit.framework.Assert.fail("Returned object is not correct instance. See System.err for more information.");
            }

            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            e.printStackTrace();
            junit.framework.Assert.fail(e.getMessage());

        }
    }
    
    
    @Override
    public void testDelete() {
//        super.testDelete();
    }

    @Override
    public void testPut() {
//        super.testPut();
    }

    @Override
    protected int testObject(Object o) {
        return -1;
    }
}
