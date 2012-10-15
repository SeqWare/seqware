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

import java.util.ArrayList;
import java.util.List;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.lists.StudyList;
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
 * <p>StudyResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class StudyResource extends DatabaseResource {

    /**
     * <p>Constructor for StudyResource.</p>
     */
    public StudyResource() {
        super("study");
    }

    /** {@inheritDoc} */
    @Override
    protected void doInit() throws ResourceException {
        super.doInit();
        authenticate();
    }

    /** {@inheritDoc} */
    @Override
    public Representation get() {
        getXML();
        return null;
    }

    /**
     * <p>getXML.</p>
     */
    @Get
    public void getXML() {
        authenticate();
        StudyService ss = BeanFactory.getStudyServiceBean();
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();

        if (queryValues.get("title") != null) {
            Study study = (Study) testIfNull(ss.findByTitle(queryValues.get("title")));
            JaxbObject jaxbTool = new JaxbObject<Study>();
            Study dto = copier.hibernate2dto(Study.class, study);
            Document line = XmlTools.marshalToDocument(jaxbTool, dto);
            getResponse().setEntity(XmlTools.getRepresentation(line));
        } else {
            JaxbObject jaxbTool = new JaxbObject<StudyList>();
            List<Study> studies = (List<Study>) testIfNull(ss.list());
            StudyList eList = new StudyList();
            eList.setList(new ArrayList());

            for (Study study : studies) {
                Study dto = copier.hibernate2dto(Study.class, study);
                eList.add(dto);
            }
            Document line = XmlTools.marshalToDocument(jaxbTool, eList);
            getResponse().setEntity(XmlTools.getRepresentation(line));
        }

    }

    /**
     * <p>postJaxb.</p>
     *
     * @param entity a {@link org.restlet.representation.Representation} object.
     * @throws org.restlet.resource.ResourceException if any.
     */
    @Post("xml")
    public void postJaxb(Representation entity) throws ResourceException {
        try {
            JaxbObject<Study> jo = new JaxbObject<Study>();
            String text = entity.getText();
            Study p = null;
            try {
                p = (Study) XmlTools.unMarshal(jo, new Study(), text);
            } catch (SAXException ex) {
                throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, ex);
            }

            if (p.getOwner() == null) {
                p.setOwner(registration);
            }

            //persist object
            StudyService service = BeanFactory.getStudyServiceBean();
            Integer swAccession = service.insert(registration, p);
            Study study = (Study) testIfNull(service.findBySWAccession(swAccession));
            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            Study detachedStudy = copier.hibernate2dto(Study.class, study);

            Document line = XmlTools.marshalToDocument(jo, detachedStudy);
            getResponse().setEntity(XmlTools.getRepresentation(line));
            getResponse().setLocationRef(getRequest().getRootRef() + "/studies/" + detachedStudy.getSwAccession());
            getResponse().setStatus(Status.SUCCESS_CREATED);
        } catch (SecurityException e) {
            getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e);
        } catch (Exception e) {
            e.printStackTrace();
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
        }

    }
}
