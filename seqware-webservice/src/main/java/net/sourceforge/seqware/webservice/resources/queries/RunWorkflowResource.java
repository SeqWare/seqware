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

import java.util.SortedSet;
import javax.xml.bind.JAXBException;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList2;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import static net.sourceforge.seqware.webservice.resources.BasicResource.parseClientInt;
import static net.sourceforge.seqware.webservice.resources.BasicResource.testIfNull;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;

/**
 * <p>
 * RunWorkflowResource class.
 * </p>
 * 
 * @author mtaschuk
 * @version $Id: $Id
 */
public class RunWorkflowResource extends BasicRestlet {

    /**
     * <p>
     * Constructor for RunWorkflowResource.
     * </p>
     * 
     * @param context
     *            a {@link org.restlet.Context} object.
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
                    Log.info("Could not find workflow runs");
                }
                JaxbObject<WorkflowRunList2> jaxbTool = new JaxbObject<>();
                line = jaxbTool.marshal(list);
            } catch (JAXBException ex) {
                ex.printStackTrace();
                response.setStatus(Status.SERVER_ERROR_INTERNAL);
            }
            response.setEntity(line, MediaType.APPLICATION_XML);

        }
    }
}
