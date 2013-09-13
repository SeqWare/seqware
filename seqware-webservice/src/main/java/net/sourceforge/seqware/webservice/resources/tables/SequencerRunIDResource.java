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
import java.util.SortedSet;
import java.util.TreeSet;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.*;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <p>SequencerRunIDResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class SequencerRunIDResource extends DatabaseIDResource {

    private Logger logger;

    /**
     * <p>Constructor for SequencerRunIDResource.</p>
     */
    public SequencerRunIDResource() {
        super("sequencerRunId");
        logger = Logger.getLogger(SequencerRunIDResource.class);
    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        authenticate();
        SequencerRunService ss = BeanFactory.getSequencerRunServiceBean();
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<SequencerRun> jaxbTool = new JaxbObject<SequencerRun>();

        SequencerRun run = (SequencerRun) testIfNull(ss.findBySWAccession(getId()));
        SequencerRun dto = copier.hibernate2dto(SequencerRun.class, run);


        if (fields.contains("lanes")) {
            SortedSet<Lane> laneList = new TreeSet<Lane>();
            SortedSet<Lane> lanes = run.getLanes();
            if (lanes != null) {
                for (Lane lane : lanes) {
                    laneList.add(copier.hibernate2dto(Lane.class, lane));
                }
                dto.setLanes(laneList);
            } else if (lanes == null) {
                Log.info("Could not be found: lanes");
            }
        }
		if(fields.contains("attributes")) {
			Set<SequencerRunAttribute> sras = run.getSequencerRunAttributes();
			if(sras!=null && !sras.isEmpty()) {
				Set<SequencerRunAttribute> newsras = new TreeSet<SequencerRunAttribute>();
				for(SequencerRunAttribute sra: sras) {
					newsras.add(copier.hibernate2dto(SequencerRunAttribute.class, sra));
				}
				dto.setSequencerRunAttributes(newsras);
			}
		}

        Document line = XmlTools.marshalToDocument(jaxbTool, dto);
        getResponse().setEntity(XmlTools.getRepresentation(line));

    }

    /** {@inheritDoc} */
    @Override
	@Put
    public Representation put(Representation entity) {
        authenticate();
        Representation representation = null;
        JaxbObject<SequencerRun> jo = new JaxbObject<SequencerRun>();
        SequencerRun newSequencerRun = null;
        try {
            String text = entity.getText();
            newSequencerRun = (SequencerRun) XmlTools.unMarshal(jo, new SequencerRun(), text);
        } catch (SAXException ex) {
            ex.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        }
        try {
            SequencerRunService srs = BeanFactory.getSequencerRunServiceBean();
            SequencerRunWizardDTO sequencerRun = (SequencerRunWizardDTO) testIfNull(srs.findByID(newSequencerRun.getSequencerRunId()));
            sequencerRun.givesPermission(registration);

            //simple types
            String name = newSequencerRun.getName();
            String desc = newSequencerRun.getDescription();
            Boolean skip = newSequencerRun.getSkip();

            //foreign keys
            Registration owner = newSequencerRun.getOwner();
            Platform platform = newSequencerRun.getPlatform();
            Set<SequencerRunAttribute> newAttributes = newSequencerRun.getSequencerRunAttributes();

            sequencerRun.setName(name);
            sequencerRun.setDescription(desc);
            sequencerRun.setSkip(skip);

            if (owner != null) {
                RegistrationService rs = BeanFactory.getRegistrationServiceBean();
                Registration newReg = rs.findByEmailAddress(owner.getEmailAddress());
                if (newReg != null) {
                    sequencerRun.setOwner(newReg);
                } else if (newReg == null) {
                    Log.info("Could not be found " + owner);
                }
            } else if (sequencerRun.getOwner() == null) {
                sequencerRun.setOwner(registration);
            }

            if (platform != null) {
                Platform p = BeanFactory.getPlatformServiceBean().findByID(platform.getPlatformId());
                if (p != null) {
                    sequencerRun.setPlatform(p);
                } else if (p == null) {
                    Log.info("Could not be found " + platform);
                }
            }

            if (newAttributes != null) {
                //SEQWARE-1577 - AttributeAnnotator cascades deletes when annotating
                this.mergeAttributes(sequencerRun.getSequencerRunAttributes(), newAttributes, sequencerRun);
            }
            srs.update(registration, sequencerRun);

            Log.debug("Skip is " + sequencerRun.getSkip());


            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            SequencerRun detachedSequencerRun = copier.hibernate2dto(SequencerRun.class, sequencerRun);

            Document line = XmlTools.marshalToDocument(jo, detachedSequencerRun);
            representation = XmlTools.getRepresentation(line);
            getResponse().setEntity(representation);
            getResponse().setLocationRef(getRequest().getRootRef() + "/sequencerruns/" + detachedSequencerRun.getSwAccession());
            getResponse().setStatus(Status.SUCCESS_CREATED);
        } catch (SecurityException e) {
            getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
        }

        return representation;
    }
}
