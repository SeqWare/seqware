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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.seqware.common.model.lists.IntegerList;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList2;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import static net.sourceforge.seqware.webservice.resources.BasicResource.testIfNull;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This resource will pull back the workflow runs that are generated from a
 * particular file.
 *
 * This version can limit results by particular workflow runs and take parameters by JSON
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class FileChildLimitedWorkflowRunsResource extends DatabaseResource {


    /**
     * <p>Constructor for FileChildWorkflowRunsResource.</p>
     */
    public FileChildLimitedWorkflowRunsResource() {
        super("file");
    }

    @Post("xml")
    public void getJson(Representation entity) {
        authenticate();
        try {
            Log.debug("Dealing with FileChildWorkflowRunsResource with Json input");
            JaxbObject jaxbTool;
            Log.info("Using direct json search");
            List<Integer> workflows = new ArrayList<Integer>();
            for (String key : queryValues.keySet()) {
                Log.debug("key: " + key + " -> " + queryValues.get(key));
                if (key.equals("workflows")) {
                    String value = queryValues.get(key);
                    String[] workflowSWIDs = value.split(",");
                    for (String swid : workflowSWIDs) {
                        workflows.add(Integer.valueOf(swid));
                    }
                }
            }
            // try to deserialize json file list
            JaxbObject<IntegerList> jo = new JaxbObject<IntegerList>();
            String text = entity.getText();
            Log.debug(text);
            List<Integer> o = null;
            try {
                o = ((IntegerList) XmlTools.unMarshal(jo, new IntegerList(), text)).getList();
            } catch (SAXException ex) {
                throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, ex);
            }
            o = testIfNull(o);
                
            Log.debug("Working with " + o.size() + " files");
            Log.debug("Working with " + workflows.size() + " workflows");
            WorkflowRunList2 runs = FileChildWorkflowRunsResource.directRetrieveWorkflowRuns(o, workflows);
            // these variables will be used to return information
            Log.debug("Returning " + runs.getList().size() + " workflow runs");
            jaxbTool = new JaxbObject<WorkflowRunList>();
            Log.debug("JaxbObjects started");
            assert runs.getList().isEmpty();
            final Document line = XmlTools.marshalToDocument(jaxbTool, runs);
            getResponse().setEntity(XmlTools.getRepresentation(line));
            getResponse().setStatus(Status.SUCCESS_CREATED);
        } catch (IOException e) {
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        }
    }
    
    
}
