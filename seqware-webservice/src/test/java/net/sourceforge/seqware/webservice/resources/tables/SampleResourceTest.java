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
import java.util.Set;
import junit.framework.Assert;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.restlet.representation.Representation;
import org.w3c.dom.Document;

/**
 *
 * @author mtaschuk
 */
public class SampleResourceTest extends DatabaseResourceTest {

    public SampleResourceTest() {
        super("/samples");
    }

  @Override
  public void testPost() {
              System.out.println(getRelativeURI() + " POST");
        testCreateNormalSample();
        testCreateRootSample();
  }

    private void testCreateNormalSample() {
        Representation rep = null;
        JaxbObject<Sample> jbo = new JaxbObject<Sample>();
        try {
            Sample sample = new Sample();
            sample.setName("Normal Sample");
            sample.setCreateTimestamp(new Date());
            sample.setUpdateTimestamp(new Date());
            Set<Sample> samples = sample.getParents();
            //samples.add(null);
            Sample n = new Sample();
            n.setSampleId(1792);
            samples.add(n);
            //Assert.assertEquals(2, samples.size());
            sample.setParents(samples);
            
            Document doc = XmlTools.marshalToDocument(jbo, sample);
            rep = resource.post(XmlTools.getRepresentation(doc));
            Sample s2 = (Sample)XmlTools.unMarshal(jbo,new Sample(), rep.getText());
            Assert.assertEquals("Should have 1 parent! (" + s2.getSampleId() + ")", 1, s2.getParents().size());
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
     private void testCreateRootSample() {
        Representation rep = null;
        JaxbObject<Sample> jbo = new JaxbObject<Sample>();
        try {
            Sample sample = new Sample();
            sample.setName("Intended Root Sample");
            sample.setCreateTimestamp(new Date());
            sample.setUpdateTimestamp(new Date());
            sample.getParents().add(null);
            
            Document doc = XmlTools.marshalToDocument(jbo, sample);
            rep = resource.post(XmlTools.getRepresentation(doc));
            Sample s2 = (Sample)XmlTools.unMarshal(jbo,new Sample(), rep.getText());
            Assert.assertEquals("Should have no parents! (" + s2.getSampleId() + ")", 0, s2.getParents().size());
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
