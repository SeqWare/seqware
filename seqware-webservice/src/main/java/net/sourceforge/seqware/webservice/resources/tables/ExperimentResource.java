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
import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.lists.ExperimentList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <p>ExperimentResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class ExperimentResource extends DatabaseResource {

    private Logger logger;

    /**
     * <p>Constructor for ExperimentResource.</p>
     */
    public ExperimentResource() {
        super("experiment");
        logger = Logger.getLogger(ExperimentResource.class);
    }

    /** {@inheritDoc} */
    @Override
    protected void doInit() throws ResourceException {
        super.doInit();
        authenticate();
    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        ExperimentService ss = BeanFactory.getExperimentServiceBean();
        logger.debug("registration: " + registration);

        List<Experiment> experiments = (List<Experiment>) testIfNull(ss.list());
        logger.debug("experiments: " + experiments.size() + " " + experiments);
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<ExperimentList> jaxbTool = new JaxbObject<ExperimentList>();

        ExperimentList eList = new ExperimentList();
        eList.setList(new ArrayList());

        for (Experiment experiment : experiments) {
            Experiment dto = copier.hibernate2dto(Experiment.class, experiment);
            eList.add(dto);
        }

        Document line = XmlTools.marshalToDocument(jaxbTool, eList);

        getResponse().setEntity(XmlTools.getRepresentation(line));
    }

    /**
     * <p>postJaxb.</p>
     *
     * @param entity a {@link org.restlet.representation.Representation} object.
     * @throws org.restlet.resource.ResourceException if any.
     */
    @Post("xml")
    public void postJaxb(Representation entity) throws ResourceException {
        authenticate();
        try {
            JaxbObject<Experiment> jo = new JaxbObject<Experiment>();
            String text = entity.getText();
            Experiment o = null;
            try {
                o = (Experiment) XmlTools.unMarshal(jo, new Experiment(), text);
            } catch (SAXException ex) {
                throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, ex);
            }
            // SEQWARE-1548
            o = testIfNull(o);
            if (o.getOwner() == null) {
                o.setOwner(registration);
            } else {
                Registration reg = BeanFactory.getRegistrationServiceBean().findByEmailAddress(testIfNull(o.getOwner().getEmailAddress()));
                if (reg != null) {
                    o.setOwner(reg);
                } else
                {
                    logger.info("Could not be found: "+o.getOwner());
                }
            }

            if (o.getStudy() != null) {
                StudyService ss = BeanFactory.getStudyServiceBean();
                Study newStudy = (Study) testIfNull(ss.findBySWAccession(o.getStudy().getSwAccession()));
                o.setStudy(newStudy);
            }


            //persist object
            ExperimentService service = BeanFactory.getExperimentServiceBean();
            Integer swAccession = service.insert(registration, o);

            Experiment experiment = (Experiment) testIfNull(service.findBySWAccession(swAccession));
            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            Experiment detachedExperiment = copier.hibernate2dto(Experiment.class, experiment);

            Document line = XmlTools.marshalToDocument(jo, detachedExperiment);
            getResponse().setEntity(XmlTools.getRepresentation(line));
            getResponse().setLocationRef(getRequest().getRootRef() + "/experiments/" + detachedExperiment.getSwAccession());
            getResponse().setStatus(Status.SUCCESS_CREATED);
        } catch (SecurityException e) {
            getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
        }

    }
}
