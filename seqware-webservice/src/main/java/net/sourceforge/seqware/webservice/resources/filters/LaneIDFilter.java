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
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.lists.LaneList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;

/**
 * <p>LaneIDFilter class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class LaneIDFilter extends BasicResource {

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        //String path = getRequest().getResourceRef().getPath();getAttribute();
        Collection<Lane> lanes = null;
        Map<String, Object> requestAttributes = getRequestAttributes();
        if (requestAttributes.containsKey("sequencerRunId")) {
            Object val = requestAttributes.get("sequencerRunId");
            if (val != null) {
                SequencerRunService ss = BeanFactory.getSequencerRunServiceBean();
                SequencerRun s = (SequencerRun)testIfNull(ss.findBySWAccession(parseClientInt(val.toString())));
                lanes = (SortedSet<Lane>) testIfNull(s.getLanes());
            }
        } else if (requestAttributes.containsKey("iusId")) {
            Object val = requestAttributes.get("iusId");
            if (val != null) {
                IUSService ss = BeanFactory.getIUSServiceBean();
                IUS s = (IUS)testIfNull(ss.findBySWAccession(parseClientInt(val.toString())));
                Lane lane = (Lane)testIfNull(s.getLane());
                lanes = new ArrayList<Lane>();
                lanes.add(lane);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            for (String key : requestAttributes.keySet()) {
                sb.append(key);
            }
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "This resource cannot handle these data types: " + sb.toString());
        }

        if (lanes.isEmpty()) {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "There are no lanes for this resource");
        }

        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<LaneList> jaxbTool = new JaxbObject<LaneList>();

        LaneList eList = new LaneList();
        eList.setList(new ArrayList());

        for (Lane lane : lanes) {
            Lane dto = copier.hibernate2dto(Lane.class, lane);
            eList.add(dto);
        }

        Document line = XmlTools.marshalToDocument(jaxbTool, eList);

        getResponse().setEntity(XmlTools.getRepresentation(line));
    }
}
