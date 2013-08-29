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
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.lists.FileList;
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
 * <p>FileResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class FileResource extends DatabaseResource {

    /**
     * <p>Constructor for FileResource.</p>
     */
    public FileResource() {
        super("file");
    }

    /**
     * <p>getXml.</p>
     *
     * @throws java.io.IOException if any.
     */
    @Get
    public void getXml() throws IOException {
        authenticate();
        FileService ss = BeanFactory.getFileServiceBean();
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();

        if (queryValues.get("id") != null) {
            JaxbObject<File> jaxbTool = new JaxbObject<File>();
            File p = (File) testIfNull(ss.findByID(parseClientInt(queryValues.get("id"))));

            File dto = copier.hibernate2dto(File.class, p);
            Document line = XmlTools.marshalToDocument(jaxbTool, dto);
            getResponse().setEntity(XmlTools.getRepresentation(line));
        } else {
            JaxbObject<FileList> jaxbTool = new JaxbObject<FileList>();
            List<File> files = (List<File>) testIfNull(ss.findByOwnerId(registration.getRegistrationId()));
            FileList eList = new FileList();
            eList.setList(new ArrayList());

            for (File file : files) {
                File dto = copier.hibernate2dto(File.class, file);
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
     */
    @Post("xml")
    public void postJaxb(Representation entity) {
        authenticate();
        try {
            JaxbObject<File> jo = new JaxbObject<File>();

            File p = null;
            try {
                String text = entity.getText();
                p = (File) XmlTools.unMarshal(jo, new File(), text);
            } catch (SAXException ex) {
                throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, ex);
            }

            if (p.getOwner() == null) {
                p.setOwner(registration);
            } else {
                Registration reg = BeanFactory.getRegistrationServiceBean().findByEmailAddress(p.getOwner().getEmailAddress());
                if (reg != null) {
                    p.setOwner(reg);
                } else {
                    Log.info("Could not be found: " + p.getOwner());
                }
            }
            //persist p
            FileService fileService = BeanFactory.getFileServiceBean();
            fileService.insert(registration, p);
            File file = (File) testIfNull(fileService.findByPath(p.getFilePath()));
            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            File detachedFile = copier.hibernate2dto(File.class, file);

            Document line = XmlTools.marshalToDocument(jo, detachedFile);
            getResponse().setEntity(XmlTools.getRepresentation(line));
            getResponse().setLocationRef(getRequest().getRootRef() + "/files/" + detachedFile.getSwAccession());
            getResponse().setStatus(Status.SUCCESS_CREATED);
        } catch (SecurityException e) {
            getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e);
        } catch (IOException e) {
            e.printStackTrace();
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e);
        }
//        catch (Exception e) {
//            e.printStackTrace();
//            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
//        }

    }
}
