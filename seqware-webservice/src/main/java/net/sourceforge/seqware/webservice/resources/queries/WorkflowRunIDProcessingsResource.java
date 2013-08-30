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

import java.sql.SQLException;
import java.util.*;

import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.lists.ProcessingList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.w3c.dom.Document;

/**
 * <p>WorkflowRunIDProcessingsResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowRunIDProcessingsResource extends BasicRestlet {

    /**
     * <p>Constructor for WorkflowRunIDProcessingsResource.</p>
     *
     * @param context a {@link org.restlet.Context} object.
     */
    public WorkflowRunIDProcessingsResource(Context context) {
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        try {
            String id = request.getAttributes().get("workflowRunId").toString();

            List<Processing> procs = collectProcessingList(parseClientInt(id));

            ProcessingList list = new ProcessingList();
            list.setList(procs);

            JaxbObject<ProcessingList> jaxbTool = new JaxbObject<ProcessingList>();

            Document doc = XmlTools.marshalToDocument(jaxbTool, list);

            response.setEntity(XmlTools.getRepresentation(doc));
        } catch (SQLException ex) {
            ex.printStackTrace();
            response.setStatus(Status.SERVER_ERROR_INTERNAL, ex);
        }
    }

    /**
     * <p>collectProcessingList.</p>
     *
     * @param wrSWA a int.
     * @return a {@link java.util.List} object.
     * @throws java.sql.SQLException if any.
     */
    public List<Processing> collectProcessingList(int wrSWA) throws SQLException {
        WorkflowRunService wrs = BeanFactory.getWorkflowRunServiceBean();
        WorkflowRun run = (WorkflowRun)testIfNull(wrs.findBySWAccession(wrSWA));
        SortedSet<Processing> procs = new TreeSet<Processing>(new Comparator<Processing>(){
            @Override
            public int compare(Processing t, Processing t1) {
                return t.getCreateTimestamp().compareTo(t1.getCreateTimestamp());
            }
            
        });
        if (run.getProcessings()!=null)
            procs.addAll(run.getProcessings());
        if (run.getOffspringProcessings()!=null)
            procs.addAll(run.getOffspringProcessings());
        
        List<Processing> procDto = new ArrayList<Processing>();
        
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        for (Processing p : procs) {
            procDto.add(copier.hibernate2dto(Processing.class, p));
        }
        return procDto;
    }
}
