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
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.lists.IUSList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.restlet.resource.Get;
import org.w3c.dom.Document;

/**
 *
 * @author mtaschuk
 */
public class IusResource extends DatabaseResource {

    public IusResource() {
        super("ius");
    }

    @Get
    public void getXml() throws IOException {
        authenticate();
        IUSService ss = BeanFactory.getIUSServiceBean();
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();

        Document line;
        if (queryValues.get("id") != null) {
            IUS p = (IUS) testIfNull(ss.findByID(Integer.parseInt(queryValues.get("id"))));

            JaxbObject<IUS> jaxbTool = new JaxbObject<IUS>();
            IUS dto = copier.hibernate2dto(IUS.class, p);
            line = XmlTools.marshalToDocument(jaxbTool, dto);

        } else {

            JaxbObject<IUSList> jaxbTool = new JaxbObject<IUSList>();
            IUSList list = new IUSList();
            List<IUS> iuses = (List<IUS>) testIfNull(ss.list());

            for (IUS i : iuses) {
                list.add(copier.hibernate2dto(IUS.class, i));
            }
            line = XmlTools.marshalToDocument(jaxbTool, list);

        }
        getResponse().setEntity(XmlTools.getRepresentation(line));
    }
}
