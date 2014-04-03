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
import net.sourceforge.seqware.common.business.LibraryStrategyService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.LibraryStrategy;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.lists.ExperimentList;
import net.sourceforge.seqware.common.model.lists.LibraryStrategyList;
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
public class LibraryStrategyResource extends DatabaseResource {

    private Logger logger;

    /**
     * <p>Constructor for ExperimentResource.</p>
     */
    public LibraryStrategyResource() {
        super("library_strategy");
        logger = Logger.getLogger(LibraryStrategyResource.class);
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
        LibraryStrategyService ss = BeanFactory.getLibraryStrategyServiceBean();
        logger.debug("registration: " + registration);

        List<LibraryStrategy> objects = (List<LibraryStrategy>) testIfNull(ss.list());
        logger.debug("library_strategies: " + objects.size() + " " + objects);
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<LibraryStrategyList> jaxbTool = new JaxbObject<LibraryStrategyList>();

        LibraryStrategyList list = new LibraryStrategyList();
        list.setList(new ArrayList());

        for (LibraryStrategy obj : objects) {
            LibraryStrategy dto = copier.hibernate2dto(LibraryStrategy.class, obj);
            list.add(dto);
        }

        Document line = XmlTools.marshalToDocument(jaxbTool, list);

        getResponse().setEntity(XmlTools.getRepresentation(line));
    }

    
}
