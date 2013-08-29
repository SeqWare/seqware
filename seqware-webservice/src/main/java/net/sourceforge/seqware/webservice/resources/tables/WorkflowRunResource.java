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
import java.util.ArrayList;
import java.util.List;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.RegistrationDTO;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList2;
import net.sourceforge.seqware.common.util.Log;
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
 * <p>WorkflowRunResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowRunResource extends DatabaseResource {

    /**
     * <p>Constructor for WorkflowRunResource.</p>
     */
    public WorkflowRunResource() {
        super("workflowRun_id");
    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        authenticate();
        WorkflowRunService ss = BeanFactory.getWorkflowRunServiceBean();
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();


        if (queryValues.get("id") != null) {
            JaxbObject<WorkflowRun> jaxbTool = new JaxbObject<WorkflowRun>();
            WorkflowRun wr = (WorkflowRun) testIfNull(ss.findByID(parseClientInt(queryValues.get("id"))));
            WorkflowRun dto = copier.hibernate2dto(WorkflowRun.class, wr);

            Document line = XmlTools.marshalToDocument(jaxbTool, dto);
            getResponse().setEntity(XmlTools.getRepresentation(line));
        } else if (queryValues.get("email") != null) {
            // SEQWARE-1134
            RegistrationService rs = BeanFactory.getRegistrationServiceBean();
            RegistrationDTO regDTO = (RegistrationDTO)testIfNull(rs.findByEmailAddress(queryValues.get("email")));
            Integer registrationId = regDTO.getRegistrationId();
            List<WorkflowRun> runs = ss.findByOwnerID(registrationId);
            respondWithList(runs, copier);
        } 
        else { 
            
            List<WorkflowRun> runs = null;
            if (queryValues.get("status") != null) {
                runs = ss.findByCriteria("wr.status='"+queryValues.get("status")+"'");
            } else {
                runs = (List<WorkflowRun>) testIfNull(ss.list());
            }
            respondWithList(runs, copier);
        }
    }

    /**
     * <p>postJaxb.</p>
     *
     * @param entity a {@link org.restlet.representation.Representation} object.
     */
    @Post("xml")
    public void postJaxb(Representation entity) {
        authenticate();

        WorkflowRun p = null;
        JaxbObject<WorkflowRun> jo = new JaxbObject<WorkflowRun>();
        try {
            String text = entity.getText();
            p = (WorkflowRun) XmlTools.unMarshal(jo, new WorkflowRun(), text);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
        } catch (SAXException ex) {
            throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, ex);
        }
        try {
            if (p.getOwner() != null) {
                Registration reg = BeanFactory.getRegistrationServiceBean().findByEmailAddress(p.getOwner().getEmailAddress());
                if (reg != null) {
                    p.setOwner(reg);
                } else {
                Log.info("Could not be found: owner " + p.getOwner());
            }
            } else {
                p.setOwner(registration);
            }
            WorkflowRun wr = insertWorkflowRun(p);

            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            WorkflowRun detachedWR = copier.hibernate2dto(WorkflowRun.class, wr);

            Document line = XmlTools.marshalToDocument(jo, detachedWR);
            getResponse().setEntity(XmlTools.getRepresentation(line));
            getResponse().setLocationRef(getRequest().getRootRef() + "/workflowruns/" + detachedWR.getSwAccession());
            getResponse().setStatus(Status.SUCCESS_CREATED);
        } catch (SecurityException e) {
            getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e);
        }

    }

    /**
     * <p>insertWorkflowRun.</p>
     *
     * @param p a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    public WorkflowRun insertWorkflowRun(WorkflowRun p) {
        //persist p
        WorkflowRunService wrs = BeanFactory.getWorkflowRunServiceBean();

        Workflow workflow = p.getWorkflow();
        if (workflow != null) {
            Workflow w = (Workflow) testIfNull(BeanFactory.getWorkflowServiceBean().findByID(workflow.getWorkflowId()));
            p.setWorkflow(w);
        }
        Integer id = wrs.insert(registration, p);
        WorkflowRun wr = wrs.findBySWAccession(id);
        return wr;
    }

    private void respondWithList(List<WorkflowRun> runs, Hibernate3DtoCopier copier) {
        WorkflowRunList2 eList = new WorkflowRunList2();
        ArrayList<WorkflowRun> list = new ArrayList<WorkflowRun>();
        JaxbObject<WorkflowRunList2> jaxbTool = new JaxbObject<WorkflowRunList2>();

        for (WorkflowRun run : runs) {
            WorkflowRun dto = copier.hibernate2dto(WorkflowRun.class, run);
            list.add(dto);
        }
        eList.setList(list);
        Document line = XmlTools.marshalToDocument(jaxbTool, eList);
        getResponse().setEntity(XmlTools.getRepresentation(line));
    }
}
