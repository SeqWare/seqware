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
import net.sourceforge.seqware.common.business.WorkflowParamService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.lists.WorkflowParamList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <p>WorkflowParamResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowParamResource extends DatabaseResource {

    /**
     * <p>Constructor for WorkflowParamResource.</p>
     */
    public WorkflowParamResource() {
        super("WorkflowParams");
    }

    /**
     * <p>postJaxb.</p>
     *
     * @param entity a {@link org.restlet.representation.Representation} object.
     */
    @Post
    public void postJaxb(Representation entity) {
        authenticate();
        try {
            JaxbObject<WorkflowParam> jo = new JaxbObject<WorkflowParam>();
            String text = entity.getText();
            WorkflowParam p;
            try {
                p = (WorkflowParam) XmlTools.unMarshal(jo, new WorkflowParam(), text);
            } catch (SAXException ex) {
                throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, ex);
            }

            WorkflowService wos = BeanFactory.getWorkflowServiceBean();
            Workflow w = (Workflow) testIfNull(wos.findByID(p.getWorkflow().getWorkflowId()));
            w.givesPermission(registration);
            p.setWorkflow(w);

            //persist p
            WorkflowParamService ws = BeanFactory.getWorkflowParamServiceBean();
            Integer id = ws.insert(registration, p);
            WorkflowParam wp = (WorkflowParam) testIfNull(ws.findByID(id));
            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            WorkflowParam detachedWP = copier.hibernate2dto(WorkflowParam.class, wp);

            Document line = XmlTools.marshalToDocument(jo, detachedWP);
            getResponse().setEntity(XmlTools.getRepresentation(line));
            getResponse().setLocationRef(getRequest().getRootRef() + "/workflowparams?id=" + detachedWP.getWorkflowParamId());
            getResponse().setStatus(Status.SUCCESS_CREATED);
        } catch (SecurityException e) {
            getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e);
        } catch (IOException e) {
            e.printStackTrace();
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e);
        } catch (Exception e) {
            e.printStackTrace();
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
        }

    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        authenticate();
        WorkflowParamService ss = BeanFactory.getWorkflowParamServiceBean();
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        Document line;
        if (queryValues.get("id") != null) {

            JaxbObject<WorkflowParam> jaxbTool = new JaxbObject<WorkflowParam>();

            WorkflowParam wp = ((WorkflowParam) testIfNull(ss.findByID(parseClientInt(queryValues.get("id")))));
            WorkflowParam dto = copier.hibernate2dto(WorkflowParam.class, wp);
            Workflow w = wp.getWorkflow();
            Workflow detachedW = copier.hibernate2dto(Workflow.class, w);
            dto.setWorkflow(detachedW);

            line = XmlTools.marshalToDocument(jaxbTool, dto);

        } else {
            JaxbObject<WorkflowParam> jaxbTool = new JaxbObject<WorkflowParam>();
            List<WorkflowParam> wps = (List<WorkflowParam>) testIfNull(ss.list());
            WorkflowParamList list = new WorkflowParamList();
            for (WorkflowParam wp : wps ) {
                list.add(copier.hibernate2dto(WorkflowParam.class, wp));
            }

            line = XmlTools.marshalToDocument(jaxbTool, list);
        }
        getResponse().setEntity(XmlTools.getRepresentation(line));

    }
}
