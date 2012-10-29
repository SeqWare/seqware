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
import net.sourceforge.seqware.common.business.OrganismService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.lists.ExperimentList;
import net.sourceforge.seqware.common.model.lists.OrganismList;
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
 * @author boconnor
 * @version $Id: $Id
 */
public class OrganismResource extends DatabaseResource {

    private Logger logger;

    /**
     * <p>Constructor for ExperimentResource.</p>
     */
    public OrganismResource() {
        super("organism");
        logger = Logger.getLogger(OrganismResource.class);
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
        OrganismService ss = BeanFactory.getOrganismServiceBean();
        logger.debug("registration: " + registration);

        List<Organism> objects = (List<Organism>) testIfNull(ss.list());
        logger.debug("organisms: " + objects.size() + " " + objects);
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<OrganismList> jaxbTool = new JaxbObject<OrganismList>();

        OrganismList list = new OrganismList();
        list.setList(new ArrayList());

        for (Organism obj : objects) {
            Organism dto = copier.hibernate2dto(Organism.class, obj);
            list.add(dto);
        }

        Document line = XmlTools.marshalToDocument(jaxbTool, list);

        getResponse().setEntity(XmlTools.getRepresentation(line));
    }

    
}
