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

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.junit.Assert;
import org.restlet.representation.Representation;

/**
 * Stupid hack, first test fails when tomcat is used after glassfish
 * 
 * @author dyuen
 */
public class DummyExperimentIDResourceTest extends DatabaseResourceIDTest {

    public DummyExperimentIDResourceTest() {
        super("/experiments/6157");
        jo = new JaxbObject<>();
        o = new Experiment();
    }

    @Override
    public void testGet() {
        Representation rep = null;
        try {
            rep = resource.get();
            String text = rep.getText();
            Object obj = XmlTools.unMarshal(jo, o, text);

            int result = testObject(obj);
            if (result == ReturnValue.INVALIDFILE) {
                Assert.fail("IDs are not equal. See System.err for more information.");
            } else if (result == ReturnValue.FILENOTREADABLE) {
                Assert.fail("Returned object is not correct instance. See System.err for more information.");
            }

            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void testPut() {
        /*
         * Representation rep = null; String test =
         * "<Experiment><accession/><baseCaller/><centerName/><createTimestamp>2012-02-04T21:27:06.752-05:00</createTimestamp>" +
         * "<description/><experimentId>14</experimentId><isHasFile>false</isHasFile>" +
         * "<isSelected>false</isSelected><name>ExomeTest1</name><qualityType>phred</qualityType>" +
         * "<sequenceSpace>Base Space</sequenceSpace><swAccession>6157</swAccession>" +
         * "<title>ExomeTest1kkk</title><updateTimestamp>2012-02-04T21:27:06.752-05:00</updateTimestamp>" +
         * "<experimentAttributes><experimentid>14</experimentid><tag>ttk2</tag><value>tttm1</value>" +
         * "</experimentAttributes><experimentAttributes><experimentid>14</experimentid><tag>ttk3</tag>" +
         * "<value>tttm1111</value></experimentAttributes></Experiment>"; try { rep = resource.put(test); String result = rep.getText();
         * rep.exhaust(); rep.release(); System.out.println("id = " + id); System.out.println(result);
         * Assert.assertTrue("ID is not in representation:" + result, result.contains(id)); } catch (Exception e) { e.printStackTrace();
         * Assert.fail(e.getMessage());
         * 
         * }
         */
    }

    @Override
    public void testPost() {
        Representation rep = null;
        try {
            rep = resource.post(null);
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
        }
    }

    @Override
    protected int testObject(Object o) {
        if (o instanceof Experiment) {
            Experiment e = (Experiment) o;
            System.out.println("ExperimentIDResourceTest.testObject: " + e.toString());
            if (e.getSwAccession() != Integer.parseInt(id)) {
                System.err.println("Actual ID: " + e.getSwAccession() + " and expected ID: " + Integer.parseInt(id));
                return ReturnValue.INVALIDFILE;
            }

        } else {
            System.err.println("Object is not an instance of Experiment");
        }
        return ReturnValue.SUCCESS;
    }

    @Override
    public void testDelete() {
        Representation rep = null;
        try {
            rep = resource.delete();
            String result = rep.getText();
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            /** expect an error on the first try */
        }
    }
}
