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

import java.io.IOException;
import java.util.List;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.lists.LaneList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.restlet.resource.Get;
import org.w3c.dom.Document;

/**
 * <p>LaneResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class LaneResource extends DatabaseResource {

    /**
     * <p>Constructor for LaneResource.</p>
     */
    public LaneResource() {
        super("lane");
    }

    /**
     * <p>getXml.</p>
     *
     * @throws java.io.IOException if any.
     */
    @Get
    public void getXml() throws IOException {
        authenticate();
        LaneService ss = BeanFactory.getLaneServiceBean();
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        Document line;
        
        if (queryValues.get("id") != null) {
            JaxbObject<Lane> jaxbTool = new JaxbObject<Lane>();
            Lane p = (Lane) testIfNull(ss.findByID(Integer.parseInt(queryValues.get("id"))));
            
            Lane dto = copier.hibernate2dto(Lane.class, p);
            line = XmlTools.marshalToDocument(jaxbTool, dto);
            
        }
        else
        {
            JaxbObject<LaneList> jaxbTool = new JaxbObject<LaneList>();
            LaneList list = new LaneList();
            List<Lane> lanes = (List<Lane>)testIfNull(ss.list());
            for (Lane l: lanes)
            {
                list.add(copier.hibernate2dto(Lane.class, l));
            }
            line = XmlTools.marshalToDocument(jaxbTool, list);
            
        }
        
        getResponse().setEntity(XmlTools.getRepresentation(line));
        
    }
}
