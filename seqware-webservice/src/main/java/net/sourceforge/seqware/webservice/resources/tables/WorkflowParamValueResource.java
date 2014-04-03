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
import net.sourceforge.seqware.common.business.WorkflowParamValueService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.WorkflowParamValue;
import net.sourceforge.seqware.common.model.lists.WorkflowParamValueList;
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
 * <p>WorkflowParamValueResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowParamValueResource extends DatabaseResource {

    /**
     * <p>Constructor for WorkflowParamValueResource.</p>
     */
    public WorkflowParamValueResource() {
        super("WorkflowParamValue");
    }

    /**
     * <p>postJaxb.</p>
     *
     * @param entity a {@link org.restlet.representation.Representation} object.
     */
    @Post("xml")
    public void postJaxb(Representation entity) {
        authenticate();
        try {
            JaxbObject<WorkflowParamValue> jo = new JaxbObject<WorkflowParamValue>();
            String text = entity.getText();
            WorkflowParamValue p;
            try {
                p = (WorkflowParamValue) XmlTools.unMarshal(jo, new WorkflowParamValue(), text);
            } catch (SAXException ex) {
                throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, ex);
            }

            //persist p
            WorkflowParamValueService wpvs = BeanFactory.getWorkflowParamValueServiceBean();
            Integer id = wpvs.insert(registration, p);
            WorkflowParamValue wpv = (WorkflowParamValue) testIfNull(wpvs.findByID(id));
            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            WorkflowParamValue detachWPV = copier.hibernate2dto(WorkflowParamValue.class, wpv);

            Document line = XmlTools.marshalToDocument(jo, detachWPV);
            getResponse().setEntity(XmlTools.getRepresentation(line));
            getResponse().setLocationRef(getRequest().getRootRef() + "/workflowparamvalues?id=" + detachWPV.getWorkflowParamValueId());
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
        WorkflowParamValueService ss = BeanFactory.getWorkflowParamValueServiceBean();
        Document line;
        if (queryValues.get("id") != null) {
            WorkflowParamValue wp = ((WorkflowParamValue) testIfNull(ss.findByID(parseClientInt(queryValues.get("id")))));
            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            JaxbObject<WorkflowParamValue> jaxbTool = new JaxbObject<WorkflowParamValue>();

            WorkflowParamValue dto = copier.hibernate2dto(WorkflowParamValue.class, wp);

            line = XmlTools.marshalToDocument(jaxbTool, dto);

        } else {
            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            JaxbObject<WorkflowParamValueList> jaxbTool = new JaxbObject<WorkflowParamValueList>();

            WorkflowParamValueList list = new WorkflowParamValueList();
            List<WorkflowParamValue> wpvs = (List<WorkflowParamValue>) testIfNull(ss.list());

            for (WorkflowParamValue wpv : wpvs) {
                list.add(copier.hibernate2dto(WorkflowParamValue.class, wpv));
            }

            line = XmlTools.marshalToDocument(jaxbTool, list);
        }
        getResponse().setEntity(XmlTools.getRepresentation(line));

    }
}
