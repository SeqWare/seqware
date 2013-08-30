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
import java.util.ArrayList;
import java.util.List;

import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.lists.FileList;
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
 * <p>WorkflowRunIdFilesResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowRunIdFilesResource extends BasicRestlet {

    /**
     * <p>Constructor for WorkflowRunIdFilesResource.</p>
     *
     * @param context a {@link org.restlet.Context} object.
     */
    public WorkflowRunIdFilesResource(Context context) {
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        try {
            String id = request.getAttributes().get("workflowRunId").toString();

            List<File> files = hello(parseClientInt(id));

            FileList list = new FileList();
            list.setList(files);

            JaxbObject<FileList> jaxbTool = new JaxbObject<FileList>();

            Document doc = XmlTools.marshalToDocument(jaxbTool, list);

            response.setEntity(XmlTools.getRepresentation(doc));
        } catch (SQLException ex) {
            ex.printStackTrace();
            response.setStatus(Status.SERVER_ERROR_INTERNAL, ex);
        }
    }

    /**
     * <p>hello.</p>
     *
     * @param wrSWA a int.
     * @return a {@link java.util.List} object.
     * @throws java.sql.SQLException if any.
     */
    public List<File> hello(int wrSWA) throws SQLException {
        WorkflowRunService wrs = BeanFactory.getWorkflowRunServiceBean();
        List<File> files = (List<File>) testIfNull(wrs.findFiles(wrSWA));
        List<File> dtoFiles = new ArrayList<File>();
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        for (File file : files) {
            dtoFiles.add(copier.hibernate2dto(File.class, file));
        }
        return dtoFiles;
    }
}
