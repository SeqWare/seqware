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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.lists.SampleList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;

/**
 * <p>SampleIDFilter class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class SampleIDFilter extends BasicResource {

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        //String path = getRequest().getResourceRef().getPath();getAttribute();
        Collection<Sample> samples = null;
        Map<String, Object> requestAttributes = getRequestAttributes();
        if (requestAttributes.containsKey("experimentId")) {
            Object val = requestAttributes.get("experimentId");
            if (val != null) {
                ExperimentService es = BeanFactory.getExperimentServiceBean();
                Experiment s = (Experiment)testIfNull(es.findBySWAccession(parseClientInt(val.toString())));
                samples = (SortedSet<Sample>) testIfNull(s.getSamples());
            }
        } else if (requestAttributes.containsKey("parentId")) {
            Object val = requestAttributes.get("parentId");
            if (val != null) {
                SampleService es = BeanFactory.getSampleServiceBean();
                Sample s = (Sample)testIfNull(es.findBySWAccession(parseClientInt(val.toString())));
                samples = (Set<Sample>) testIfNull(s.getChildren());
            }
        } else if (requestAttributes.containsKey("childId")) {
            Object val = requestAttributes.get("childId");
            if (val != null) {
                SampleService es = BeanFactory.getSampleServiceBean();
                Sample s = (Sample)testIfNull(es.findBySWAccession(parseClientInt(val.toString())));
                samples = (Set<Sample>) testIfNull(s.getParents());
            }
        } else {
            StringBuilder sb = new StringBuilder();
            for (String key : requestAttributes.keySet()) {
                sb.append(key);
            }
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "This resource cannot handle these data types: " + sb.toString());
        }
        
        if (samples.isEmpty()) {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "There are no samples for this resource");
        }
        
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<SampleList> jaxbTool = new JaxbObject<SampleList>();

        SampleList eList = new SampleList();
        eList.setList(new ArrayList());

        for (Sample sample : samples) {
            Sample dto = copier.hibernate2dto(Sample.class, sample);
            eList.add(dto);
        }

        Document line = XmlTools.marshalToDocument(jaxbTool, eList);

        getResponse().setEntity(XmlTools.getRepresentation(line));
    }
}
