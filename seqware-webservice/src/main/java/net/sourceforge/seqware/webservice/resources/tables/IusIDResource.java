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
import java.util.TreeSet;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <p>IusIDResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class IusIDResource extends DatabaseIDResource {

    private Logger logger;

    /**
     * <p>Constructor for IusIDResource.</p>
     */
    public IusIDResource() {
        super("iusId");
        logger = Logger.getLogger(IusIDResource.class);
    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        authenticate();
        IUSService ss = BeanFactory.getIUSServiceBean();
        IUS ius = (IUS) testIfNull(ss.findBySWAccession(getId()));
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<IUS> jaxbTool = new JaxbObject<IUS>();

        IUS dto = copier.hibernate2dto(IUS.class, ius);

		if(fields.contains("attributes")) {
			Set<IUSAttribute> ias = ius.getIusAttributes();
			if(ias!=null && !ias.isEmpty()) {
				Set<IUSAttribute> newias = new TreeSet<IUSAttribute>();
				for(IUSAttribute ia: ias) {
					newias.add(copier.hibernate2dto(IUSAttribute.class,ia));
				}
				dto.setIusAttributes(newias);
			}
		}
        Document line = XmlTools.marshalToDocument(jaxbTool, dto);
        getResponse().setEntity(XmlTools.getRepresentation(line));
    }

    /** {@inheritDoc} */
    @Override
    public Representation put(Representation entity) {
        authenticate();
        Representation representation = null;
        IUS newIUS = null;
        JaxbObject<IUS> jo = new JaxbObject<IUS>();
        try {
            String text = entity.getText();
            newIUS = (IUS) XmlTools.unMarshal(jo, new IUS(), text);
        } catch (SAXException ex) {
            ex.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ex);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
        }
        try {
            IUSService fs = BeanFactory.getIUSServiceBean();
            IUS ius = (IUS) testIfNull(fs.findByID(newIUS.getIusId()));
            ius.givesPermission(registration);
            //simple types
            String name = newIUS.getName();
            String desc = newIUS.getDescription();
            String tags = newIUS.getTag();
            Boolean skip = newIUS.getSkip();

            //foreign keys
            Sample sample = newIUS.getSample();
            Registration owner = newIUS.getOwner();

            Set<IUSAttribute> newAttributes = newIUS.getIusAttributes();

            ius.setName(name);
            ius.setDescription(desc);
            ius.setSkip(skip);
            ius.setTag(tags);

            if (sample != null) {
                SampleService ss = BeanFactory.getSampleServiceBean();
                Sample newSample = ss.findByID(sample.getSampleId());
                if (newSample != null && newSample.givesPermission(registration)) {
                    ius.setSample(newSample);
                }
                else if (newSample==null)
                {
                    Log.info("Could not be found "+sample);
                }
            }

            if (owner != null) {
                RegistrationService rs = BeanFactory.getRegistrationServiceBean();
                Registration newReg = rs.findByEmailAddress(owner.getEmailAddress());
                if (newReg != null) {
                    ius.setOwner(newReg);
                } else {
                    Log.info(newReg.getClass().getName() + " cannot be found: " + owner.getEmailAddress());
                }
            } else if (ius.getOwner() == null) {
                ius.setOwner(registration);
            }
            if (newAttributes != null) {
                //SEQWARE-1577 - AttributeAnnotator cascades deletes when annotating
                this.mergeAttributes(ius.getIusAttributes(), newAttributes, ius);
            }

            fs.update(registration, ius);

            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            IUS detachedIUS = copier.hibernate2dto(IUS.class, ius);

            Document line = XmlTools.marshalToDocument(jo, detachedIUS);
            representation = XmlTools.getRepresentation(line);
            getResponse().setEntity(representation);
            getResponse().setLocationRef(getRequest().getRootRef() + "/ius/" + detachedIUS.getSwAccession());
            getResponse().setStatus(Status.SUCCESS_CREATED);
        } catch (SecurityException e) {
            getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e);
        } catch (Exception e) {
            e.printStackTrace();
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
        }

        return representation;
    }
}
