/*
 * Copyright (C) 2012 SeqWare
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
package net.sourceforge.seqware.webservice.resources.queries;

import static net.sourceforge.seqware.webservice.resources.BasicResource.parseClientInt;
import static net.sourceforge.seqware.webservice.resources.BasicResource.testIfNull;

import java.util.Set;

import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.lists.ProcessingList;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.w3c.dom.Document;

/**
 * <p>ProcessIdProcessResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class ProcessIdProcessResource extends BasicRestlet {

    /**
     * <p>Constructor for ProcessIdProcessResource.</p>
     *
     * @param context a {@link org.restlet.Context} object.
     */
    public ProcessIdProcessResource(Context context) {
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    public void handle(Request request, Response response) {

        authenticate(request.getChallengeResponse().getIdentifier());
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();

        String id = request.getAttributes().get("processId").toString();

        ProcessingService s = BeanFactory.getProcessingServiceBean();
        Processing p = (Processing) testIfNull(s.findBySWAccession(parseClientInt(id)));

        JaxbObject<ProcessingList> jaxbTool = new JaxbObject<ProcessingList>();

        Set<Processing> parents = p.getParents();
        ProcessingList list = new ProcessingList();
        if (parents != null) {

            for (Processing parent : parents) {
                list.add(copier.hibernate2dto(Processing.class, parent));
            }
        } else {
            Log.info("Could not find parents");
        }
        Document line = XmlTools.marshalToDocument(jaxbTool, list);

        response.setEntity(XmlTools.getRepresentation(line));
    }
}
