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

import java.util.Date;
import junit.framework.Assert;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.w3c.dom.Document;

/**
 *
 * @author mtaschuk
 */
public class IusResourceTest extends DatabaseResourceTest {

    public IusResourceTest() {
        super("/ius");
    }
    
    @Override
    public void testPost() {
        System.out.println(getRelativeURI() + " POST");
        Representation rep = null;
        try {
            IUS ius = new IUS();
            ius.setCreateTimestamp(new Date());
            ius.setIusId(27);
            ius.setSwAccession(6213);
            ius.setUpdateTimestamp(new Date());
            Sample s = new Sample();
            Lane l = new Lane();
            l.setLaneId(1);
            s.setSampleId(10);
            ius.setSample(s);
            ius.setLane(l);
            
            Document doc = XmlTools.marshalToDocument(new JaxbObject<IUS>(), ius);
            rep = resource.post(XmlTools.getRepresentation(doc));
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
