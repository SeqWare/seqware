/*
 * Copyright (C) 2013 SeqWare
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

import java.util.List;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.lists.IUSList;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.AbstractResourceTest;
import net.sourceforge.seqware.webservice.resources.ClientResourceInstance;
import org.junit.*;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

/**
 *
 * @author mtaschuk
 */
public class IUSIDFilterTest extends AbstractResourceTest {

    public IUSIDFilterTest() {
        super("");
    }

    @Override
    public void testGet() {
    }

    @Test
    public void testGetFromLane() throws Exception {
        List<IUS> lanes = getIUSs("/lanes/4764/ius");
        Assert.assertFalse(lanes.isEmpty());
    }

    @Test
    public void testGetFromLaneNotFound() throws Exception {
        try {
            getIUSs("/lanes/47151/ius");
            Assert.fail("This lane should not exist, should not have IUSes, and should show a 404 Not Found");
        } catch (ResourceException ex) {
            if (!ex.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
                throw ex;
            }
        }
    }

    @Test
    public void testGetFromLaneNoIUSes() throws Exception {
        try {
            getIUSs("/lanes/4707/ius");
            Assert.fail("This lane should not have IUSes, and should show a 404 Not Found");
        } catch (ResourceException ex) {
            if (!ex.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
                throw ex;
            }
        }
    }

    @Test
    public void testGetFromSample() throws Exception {
        List<IUS> lanes = getIUSs("/samples/4783/ius");
        Assert.assertFalse(lanes.isEmpty());
    }

    @Test
    public void testGetFromSampleNotFound() throws Exception {
        try {
            getIUSs("/samples/47151/ius");
            Assert.fail("This sample should not exist, should not have IUSes, and should show a 404 Not Found");
        } catch (ResourceException ex) {
            if (!ex.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
                throw ex;
            }
        }
    }

        @Test
    public void testGetFromSampleNoIUSes() throws Exception {
        try {
            getIUSs("/samples/6560/ius");
            Assert.fail("This lane should not have IUSes, and should show a 404 Not Found");
        } catch (ResourceException ex) {
            if (!ex.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
                throw ex;
            }
        }
    }
    
    private List<IUS> getIUSs(String relativeURI) throws ResourceException, Exception {
        resource = ClientResourceInstance.getChild(relativeURI);
        Log.stdout(getRelativeURI() + " GET");
        IUSList parent = new IUSList();
        JaxbObject<IUSList> jaxb = new JaxbObject<IUSList>();
        try {
            Representation rep = resource.get();
            String text = rep.getText();
            parent = (IUSList) XmlTools.unMarshal(jaxb, parent, text);
            rep.exhaust();
            rep.release();
        } catch (ResourceException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return parent.getList();
    }

    @Override
    public void testPut() {
    }

    @Override
    public void testPost() {
    }

    @Override
    public void testDelete() {
    }
}
