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

import java.util.ArrayList;
import java.util.SortedSet;

import javax.xml.bind.JAXBException;

import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList2;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.*;

/**
 * <p>RunWorkflowResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class RunWorkflowResource
        extends BasicRestlet {

    /**
     * <p>Constructor for RunWorkflowResource.</p>
     *
     * @param context a {@link org.restlet.Context} object.
     */
    public RunWorkflowResource(Context context) {
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        String id = request.getAttributes().get("workflowId").toString(), line = null;
        WorkflowService ws = BeanFactory.getWorkflowServiceBean();
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        if (request.getMethod().compareTo(Method.GET) == 0) {
            try {
                Workflow w = (Workflow) testIfNull(ws.findBySWAccession(parseClientInt(id)));
                WorkflowRunList2 list = new WorkflowRunList2();
                SortedSet<WorkflowRun> wrs = w.getWorkflowRuns();
                if (wrs != null) {
                    for (WorkflowRun run : wrs) {
                        list.add((WorkflowRun) copier.hibernate2dto(run));
                    }
                } else {
                    Log.info("Could not find worklfow runs");
                }
                JaxbObject<WorkflowRunList2> jaxbTool = new JaxbObject<WorkflowRunList2>();
                line = jaxbTool.marshal(list);
            } catch (JAXBException ex) {
                ex.printStackTrace();
                response.setStatus(Status.SERVER_ERROR_INTERNAL);
            }
            response.setEntity(line, MediaType.APPLICATION_XML);

        } else if (request.getMethod().compareTo(Method.POST) == 0) {
            WorkflowInfo workflowInfo = new WorkflowInfo();
            Log.debug("ID: "+id);
            Workflow w = (Workflow) testIfNull(ws.findBySWAccession(parseClientInt(id)));

            workflowInfo.setCommand(w.getCommand());
            workflowInfo.setDescription(w.getDescription());
            workflowInfo.setName(w.getName());
            workflowInfo.setTemplatePath(w.getTemplate());
            workflowInfo.setVersion(w.getVersion());
            workflowInfo.setWorkflowAccession(w.getSwAccession());
            workflowInfo.setConfigPath(w.getBaseIniFile());
            workflowInfo.setWorkflowDir(w.getCwd());
            workflowInfo.setWorkflowAccession(w.getSwAccession());

            Form form = request.getResourceRef().getQueryAsForm();

            String workflowRunAccession = null;
            String iniFilesStr = request.getEntityAsText();
            boolean noMetadata = false;
            String parentAccessionsStr = "";
            ArrayList<String> parentsLinkedToWR = new ArrayList<String>();

            for (Parameter param : form) {
                String name = param.getName();
                if (name.equals("workflow-run-accession")) {
                    workflowRunAccession = param.getValue();
                } else if (name.equals("no-metadata")) {
                    noMetadata = Boolean.parseBoolean(param.getValue());
                } else if (name.equals("parent-accessions")) {
                    parentAccessionsStr = param.getValue();
                } else if (name.equals("link-workflow-run-to-parents")) {
                    String[] parents = param.getValue().split(",");
                    for (String parent : parents) {
                        parentsLinkedToWR.add(parent.trim());
                    }
                } else {
                    Log.error("Unrecognised query: " + param.getName() + "=" + param.getValue());
                }
            }

            WorkflowRunService wrs = BeanFactory.getWorkflowRunServiceBean();
            ReturnValue ret = wrs.runWorkflow(workflowInfo, workflowRunAccession,
                    iniFilesStr, noMetadata, parentAccessionsStr, parentsLinkedToWR, registration);
            if (ret.getExitStatus() != ReturnValue.SUCCESS) {
                Log.error("Exited with non-success status: " + ret.getExitStatus());
                response.setStatus(Status.SERVER_ERROR_INTERNAL);
                return;
            }
            if (ret.getReturnValue() == 0) {
                Log.error("No workflow run ID: " + ret.getReturnValue());
                response.setStatus(Status.SERVER_ERROR_INTERNAL);
                return;
            }
            try {
                JaxbObject<ReturnValue> jaxbTool = new JaxbObject<ReturnValue>();
                line = jaxbTool.marshal(ret);
                response.setEntity(line, MediaType.APPLICATION_XML);
            } catch (JAXBException ex) {
                Log.error("Problems marshalling ReturnValue");
                response.setStatus(Status.SERVER_ERROR_INTERNAL);
            }
        }
    }
}
