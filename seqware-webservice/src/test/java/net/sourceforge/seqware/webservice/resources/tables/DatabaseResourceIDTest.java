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

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.AbstractResourceTest;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.restlet.representation.Representation;

/**
 * 
 * @author mtaschuk
 */
public abstract class DatabaseResourceIDTest extends AbstractResourceTest {

    protected String id;
    protected JaxbObject jo;
    protected Object o;
    private Logger logger = Logger.getLogger(DatabaseResourceIDTest.class);

    public DatabaseResourceIDTest(String relativeURI) {
        super(relativeURI);
        String[] array = relativeURI.split("/");
        id = array[array.length - 1];
        logger.debug("ID is " + id);
    }

    @Override
    public void testPut() {
        logger.debug(getRelativeURI() + " PUT");
        Representation rep = null;
        try {
            rep = resource.put(null);
            String result = rep.getText();
            rep.exhaust();
            rep.release();
            Assert.assertTrue("ID is not in representation:" + result, result.contains(id));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());

        }
    }

    @Override
    public void testGet() {
        logger.debug(getRelativeURI() + " GET");
        Representation rep = null;
        try {
            rep = resource.get();
            String text = rep.getText();
            logger.debug(text);
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
            Assert.fail(e.getMessage());

        }
    }

    @Override
    public void testPost() {
        logger.debug(getRelativeURI() + " POST");
        Representation rep = null;
        try {
            rep = resource.post(null);
            rep.exhaust();
            rep.release();
            Assert.fail("No Post on " + getRelativeURI());
        } catch (Exception e) {
            Assert.assertTrue("Method Not Allowed", e.getMessage().contains("Method Not Allowed"));
        }
    }

    @Override
    public void testDelete() {
        logger.debug(getRelativeURI() + " DELETE");
        Representation rep = null;
        try {
            rep = resource.delete();
            String result = rep.getText();
            rep.exhaust();
            rep.release();
            Assert.assertTrue("ID is not in representation:" + result, result.contains(id));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    protected abstract int testObject(Object o);
}
