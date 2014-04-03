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
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.w3c.dom.Document;

/**
 * <p>WorkflowRunIDWorkflowResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowRunIDWorkflowResource extends BasicRestlet {

    /**
     * <p>Constructor for WorkflowRunIDWorkflowResource.</p>
     *
     * @param context a {@link org.restlet.Context} object.
     */
    public WorkflowRunIDWorkflowResource(Context context) {
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        
            String id = request.getAttributes().get("workflowRunId").toString();  
            WorkflowRunService wrs = BeanFactory.getWorkflowRunServiceBean();
            WorkflowRun run = (WorkflowRun)testIfNull(wrs.findBySWAccession(parseClientInt(id)));
            Workflow w = run.getWorkflow();
            JaxbObject<Workflow> jaxbTool = new JaxbObject<Workflow>();
            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            Workflow wdto = copier.hibernate2dto(Workflow.class, w);
            Document doc = XmlTools.marshalToDocument(jaxbTool, wdto);
            response.setEntity(XmlTools.getRepresentation(doc));
        
    }

}
