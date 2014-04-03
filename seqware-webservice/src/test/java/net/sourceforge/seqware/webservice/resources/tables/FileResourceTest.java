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

import junit.framework.Assert;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.restlet.representation.Representation;
import org.w3c.dom.Document;

/**
 *
 * @author mtaschuk
 */
public class FileResourceTest extends DatabaseResourceTest
{
    public FileResourceTest() {
        super("/files");
    }
    
     public void testPost() {
        System.out.println(getRelativeURI() + " POST");
        Representation rep = null;
        try {
            File file = new File();
            file.setDescription("test test");
            file.setFilePath("http://testing.my.file.txt");
            file.setMetaType("txt");
            
            Document doc = XmlTools.marshalToDocument(new JaxbObject<File>(), file);
            rep = resource.post(XmlTools.getRepresentation(doc));
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
}
