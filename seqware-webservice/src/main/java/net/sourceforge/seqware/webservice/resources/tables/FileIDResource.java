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
import java.util.Set;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <p>FileIDResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class FileIDResource extends DatabaseIDResource {

    /**
     * <p>Constructor for FileIDResource.</p>
     */
    public FileIDResource() {
        super("fileId");
    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        FileService ss = BeanFactory.getFileServiceBean();

        File file = (File) testIfNull(ss.findBySWAccession(getId()));
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<File> jaxbTool = new JaxbObject<File>();

        File dto = copier.hibernate2dto(File.class, file);
        Document line = XmlTools.marshalToDocument(jaxbTool, dto);

        getResponse().setEntity(XmlTools.getRepresentation(line));
    }

    /** {@inheritDoc} */
    @Override
    @Put
    public Representation put(Representation entity) {
        authenticate();
        Representation representation = null;
        File newFile = null;
        JaxbObject<File> jo = new JaxbObject<File>();
        try {
            String text = entity.getText();
            newFile = (File) XmlTools.unMarshal(jo, new File(), text);
        } catch (SAXException ex) {
            ex.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, ex);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
        }
        try {
            // SEQWARE-1548 
            newFile = testIfNull(newFile); 
            // persist
            FileService fs = BeanFactory.getFileServiceBean();
            File file = (File) testIfNull(fs.findByID(newFile.getFileId()));
            file.setDescription(newFile.getDescription());
            file.setFilePath(newFile.getFilePath());
            file.setFileType(newFile.getFileType());
            file.setIsSelected(newFile.getIsSelected());
            file.setMd5sum(newFile.getMd5sum());
            file.setMetaType(newFile.getMetaType());
            if (newFile.getOwner() != null) {
                Registration reg = BeanFactory.getRegistrationServiceBean().findByEmailAddress(newFile.getOwner().getEmailAddress());
                if (reg != null) {
                    file.setOwner(reg);
                } else {
                    Log.info("Could not be found: " + newFile.getOwner());
                }
            } else if (file.getOwner() == null) {
                file.setOwner(registration);
            }
            file.setSwAccession(newFile.getSwAccession());
            file.setType(newFile.getType());
            file.setUrl(newFile.getUrl());
            file.setUrlLabel(newFile.getUrlLabel());
            file.setSkip(newFile.getSkip());
            
            Set<FileAttribute> newAttributes = newFile.getFileAttributes();
            if (newAttributes != null) {
                //SEQWARE-1577 - AttributeAnnotator cascades deletes when annotating
                this.mergeAttributes(file.getFileAttributes(), newAttributes, file);
            }
			
            fs.update(registration, file);
            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            File detachedFile = copier.hibernate2dto(File.class, file);

            Document line = XmlTools.marshalToDocument(jo, detachedFile);
            representation = XmlTools.getRepresentation(line);
            getResponse().setEntity(representation);
            getResponse().setLocationRef(getRequest().getRootRef() + "/files/" + detachedFile.getSwAccession());
            getResponse().setStatus(Status.SUCCESS_CREATED);
        } catch (SecurityException e) {
            getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e);
        }
        catch (Exception e) {
            e.printStackTrace();
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
        }

        return representation;
    }
}
