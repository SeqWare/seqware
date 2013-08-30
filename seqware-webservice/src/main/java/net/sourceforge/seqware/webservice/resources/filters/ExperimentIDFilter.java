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
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.lists.ExperimentList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;

/**
 * <p>ExperimentIDFilter class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class ExperimentIDFilter extends BasicResource {

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        //String path = getRequest().getResourceRef().getPath();getAttribute();
        Collection<Experiment> experiments = null;
        Map<String, Object> requestAttributes = getRequestAttributes();
        if (requestAttributes.containsKey("studyId")) {
            Object val = requestAttributes.get("studyId");
            if (val != null) {
                StudyService ss = BeanFactory.getStudyServiceBean();
                Study s = (Study)testIfNull(ss.findBySWAccession(parseClientInt(val.toString())));
                experiments = (SortedSet<Experiment>) testIfNull(s.getExperiments());
            }
        } else {
            StringBuilder sb = new StringBuilder();
            for (String key : requestAttributes.keySet()) {
                sb.append(key);
            }
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "This resource cannot handle these data types: " + sb.toString());
        }

        if (experiments.isEmpty()) {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "There are no experiments for this resource");
        }

        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<ExperimentList> jaxbTool = new JaxbObject<ExperimentList>();

        ExperimentList eList = new ExperimentList();
        eList.setList(new ArrayList());

        for (Experiment experiment : experiments) {
            Experiment dto = copier.hibernate2dto(Experiment.class, experiment);
            eList.add(dto);
        }

        Document line = XmlTools.marshalToDocument(jaxbTool, eList);

        getResponse().setEntity(XmlTools.getRepresentation(line));
    }
}
