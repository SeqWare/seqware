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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.lists.ProcessingList;
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
 * <p>ProcessResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class ProcessResource extends DatabaseResource {

    /**
     * <p>Constructor for ProcessResource.</p>
     */
    public ProcessResource() {
        super("processing");
    }

    /** {@inheritDoc} */
    @Override
    public Representation post(Representation entity) {
        return super.post(entity);
    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        authenticate();

        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        Document line;
        ProcessingService ss = BeanFactory.getProcessingServiceBean();
        if (queryValues.get("id") != null) {
            JaxbObject<Processing> jaxbTool = new JaxbObject<Processing>();
            Processing p = (Processing) testIfNull(ss.findByID(parseClientInt(queryValues.get("id"))));
            Processing dto = copier.hibernate2dto(Processing.class, p);
            if (fields.contains("files")) {
                Set<File> files = p.getFiles();
                Set<File> copiedFiles = new HashSet<File>();
                if (files != null) {
                    for (File file : files) {
                        copiedFiles.add(copier.hibernate2dto(File.class, file));
                    }
                    dto.setFiles(copiedFiles);
                } else {
                    Log.info("Could not be found :files");
                }
            }
            line = XmlTools.marshalToDocument(jaxbTool, dto);

        } else {
            JaxbObject<ProcessingList> jaxbTool = new JaxbObject<ProcessingList>();
            ProcessingList list = new ProcessingList();
            List<Processing> processings = (List<Processing>) testIfNull(ss.list());
            for (Processing p : processings) {
                list.add(copier.hibernate2dto(Processing.class, p));
            }

            line = XmlTools.marshalToDocument(jaxbTool, list);

        }
        getResponse().setEntity(XmlTools.getRepresentation(line));
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
            JaxbObject<Processing> jo = new JaxbObject<Processing>();
            String text = entity.getText();
            Processing p;
            try {
                p = (Processing) XmlTools.unMarshal(jo, new Processing(), text);
            } catch (SAXException ex) {
                throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, ex);
            }
            if (p.getOwner() != null) {
                p.setOwner(BeanFactory.getRegistrationServiceBean().findByEmailAddress(p.getOwner().getEmailAddress()));
            } else {
                p.setOwner(registration);
            }
            //persist p
            ProcessingService ps = BeanFactory.getProcessingServiceBean();
            Integer id = ps.insert(registration, p);
            Processing newProcessing = (Processing) testIfNull(ps.findBySWAccession(id));
            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            Processing detachedP = copier.hibernate2dto(Processing.class, newProcessing);

            Document line = XmlTools.marshalToDocument(jo, detachedP);
            getResponse().setEntity(XmlTools.getRepresentation(line));
            getResponse().setLocationRef(getRequest().getRootRef() + "/processes/" + detachedP.getSwAccession());
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
}
