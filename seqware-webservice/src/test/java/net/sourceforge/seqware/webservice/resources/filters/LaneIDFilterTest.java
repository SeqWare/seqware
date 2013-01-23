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
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.lists.LaneList;
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
public class LaneIDFilterTest extends AbstractResourceTest {

    public LaneIDFilterTest() {
        super("");
    }

    @Override
    public void testGet() {
    }

    @Test
    public void testGetFromSequencerRun() throws Exception {
        List<Lane> lanes = getLanes("/sequencerruns/4715/lanes");
        Assert.assertFalse(lanes.isEmpty());
    }

    @Test
    public void testGetFromSequencerRunNotFound() throws Exception {
        try {
            getLanes("/sequencerruns/47151/lanes");
            Assert.fail("This sequencer run should not exist, should not have lanes, and should show a 404 Not Found");
        } catch (ResourceException ex) {
            if (!ex.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
                throw ex;
            }
        }
    }

    @Test
    public void testGetFromIUS() throws Exception {
        List<Lane> lanes = getLanes("/ius/4765/lane");
        Assert.assertFalse(lanes.isEmpty());
        Assert.assertTrue(lanes.size()==1);
    }

    @Test
    public void testGetFromIUSNotFound() throws Exception {
        try {
            getLanes("/ius/47151/lanes");
            Assert.fail("This sequencer run should not exist, should not have lanes, and should show a 404 Not Found");
        } catch (ResourceException ex) {
            if (!ex.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
                throw ex;
            }
        }
    }

//    @Test
//    public void testGetFromSequencerRunNoLanes() throws Exception {
//        try {
//            getLanes("/sequencerruns/6052/lanes");
//            Assert.fail("This sequencer run should not have lanes, and should show a 404 Not Found");
//        } catch (ResourceException ex) {
//            if (!ex.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
//                throw ex;
//            }
//        }
//    }
    
    private List<Lane> getLanes(String relativeURI) throws ResourceException, Exception {
        resource = ClientResourceInstance.getChild(relativeURI);
        Log.stdout(getRelativeURI() + " GET");
        LaneList parent = new LaneList();
        JaxbObject<LaneList> jaxb = new JaxbObject<LaneList>();
        try {
            Representation rep = resource.get();
            String text = rep.getText();
            parent = (LaneList) XmlTools.unMarshal(jaxb, parent, text);
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
