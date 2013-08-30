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
package net.sourceforge.seqware.webservice.resources.filters;

import java.util.*;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.lists.IUSList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;

/**
 * <p>IUSIDFilter class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class IUSIDFilter extends BasicResource {

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        //String path = getRequest().getResourceRef().getPath();getAttribute();
        Collection<IUS> iuss = null;
        Map<String, Object> requestAttributes = getRequestAttributes();
        if (requestAttributes.containsKey("laneId")) {
            Object val = requestAttributes.get("laneId");
            if (val != null) {
                LaneService ss = BeanFactory.getLaneServiceBean();
                Lane s = (Lane)testIfNull(ss.findBySWAccession(parseClientInt(val.toString())));
                iuss = (SortedSet<IUS>) testIfNull(s.getIUS());
            }
        } else if (requestAttributes.containsKey("sampleId")) {
            Object val = requestAttributes.get("sampleId");
            if (val != null) {
                SampleService ss = BeanFactory.getSampleServiceBean();
                Sample s = (Sample)testIfNull(ss.findBySWAccession(parseClientInt(val.toString())));
                iuss = (SortedSet<IUS>)testIfNull(s.getIUS());
            }
        } else {
            StringBuilder sb = new StringBuilder();
            for (String key : requestAttributes.keySet()) {
                sb.append(key);
            }
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "This resource cannot handle these data types: " + sb.toString());
        }

        if (iuss.isEmpty()) {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "There are no IUSes for this resource");
        }

        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<IUSList> jaxbTool = new JaxbObject<IUSList>();

        IUSList eList = new IUSList();
        eList.setList(new ArrayList());

        for (IUS ius : iuss) {
            IUS dto = copier.hibernate2dto(IUS.class, ius);
            eList.add(dto);
        }

        Document line = XmlTools.marshalToDocument(jaxbTool, eList);

        getResponse().setEntity(XmlTools.getRepresentation(line));
    }
}
