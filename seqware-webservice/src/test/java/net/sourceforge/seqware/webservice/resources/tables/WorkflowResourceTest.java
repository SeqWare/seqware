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

/**
 *
 * @author mtaschuk
 */
public class WorkflowResourceTest extends DatabaseResourceTest {

    public WorkflowResourceTest() {
        super("/workflows");
    }

    
    
//    @Override
//    public void testPost() {
//        Representation rep = null;
//        try {
//            System.out.println(getRelativeURI() + " POST  zip");
//            String zipFile = WorkflowResourceTest.class.getResource("test_bundle.zip").getPath();
//            FileRepresentation myString = new FileRepresentation(new File(zipFile), MediaType.APPLICATION_ZIP);
//
//            Disposition d = new Disposition();
//            d.setFilename(myString.getFile().getName());
//            myString.setDisposition(d);
//            rep = resource.post(myString);
//            rep.write(System.out);
//            rep.exhaust();
//            rep.release();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Assert.fail(e.getMessage());
//        }
//    }

//    @Test
//    public void testURIPost() {
//        Representation rep = null;
//        try {
//            System.out.println(getRelativeURI() + " POST uri");
//            URL uriFile = WorkflowResourceTest.class.getResource("test_bundle.zip").toURI().toURL();
//            String uri = uriFile.toString();
//            StringRepresentation myString = new StringRepresentation(uri, MediaType.TEXT_PLAIN);
//            rep = resource.post(myString);
//            rep.write(System.out);
//            rep.exhaust();
//            rep.release();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Assert.fail(e.getMessage());
//        }
//    }

    @Override
    public void testPost() {
        
    }
}
