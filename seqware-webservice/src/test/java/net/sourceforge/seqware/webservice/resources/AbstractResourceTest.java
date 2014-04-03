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
package net.sourceforge.seqware.webservice.resources;

import java.io.IOException;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.resource.ClientResource;

/**
 *
 * @author mtaschuk
 */
public abstract class AbstractResourceTest {

    private String relativeURI;
    protected ClientResource resource;
    private Logger logger = Logger.getLogger(AbstractResourceTest.class);

    public AbstractResourceTest(String relativeURI) {
        this.relativeURI = relativeURI;
    }

//    @BeforeClass
//    public static void setUpClass() throws Exception {
//        SeqWareWebServiceMain.main(null);
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//        SeqWareWebServiceMain.stop();
//    }
    @Before
    public void setUp() {
        resource = ClientResourceInstance.getChild(relativeURI);
        logger.debug("REQUESTING!!!!" + resource.toString());
    }

    @After
    public void tearDown() {
        try {
            if (resource.getResponse() != null && resource.getResponse().getEntity() != null) {
                resource.getResponse().getEntity().exhaust();
                resource.getResponse().getEntity().release();
            }
            else
            {
                logger.debug("No response or entity to exhaust");
            }
        } catch (IOException ex) {
            logger.debug("No entity to exhaust");
        }
        resource.release();
        System.out.println();
    }

    @Test
    public abstract void testGet();

    @Test
    public abstract void testPut();

    @Test
    public abstract void testPost();

    @Test
    public abstract void testDelete();

    public String getRelativeURI() {
        return relativeURI;
    }
}
