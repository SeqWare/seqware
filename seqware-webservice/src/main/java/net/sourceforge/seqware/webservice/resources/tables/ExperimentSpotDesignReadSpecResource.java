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
import net.sourceforge.seqware.common.business.ExperimentSpotDesignReadSpecService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;
import net.sourceforge.seqware.common.model.lists.ExperimentSpotDesignReadSpecList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.apache.log4j.Logger;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;

/**
 * <p>ExperimentSpotDesignReadSpecResource class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class ExperimentSpotDesignReadSpecResource extends DatabaseResource {

    private Logger logger;

    /**
     * <p>Constructor for ExperimentResource.</p>
     */
    public ExperimentSpotDesignReadSpecResource() {
        super("experiment_spot_design_read_spec");
        logger = Logger.getLogger(ExperimentSpotDesignReadSpecResource.class);
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
        ExperimentSpotDesignReadSpecService ss = BeanFactory.getExperimentSpotDesignReadSpecServiceBean();
        logger.debug("registration: " + registration);

        List<ExperimentSpotDesignReadSpec> objects = (List<ExperimentSpotDesignReadSpec>) testIfNull(ss.list());
        logger.debug("experiment spot design read specs: " + objects.size() + " " + objects);
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<ExperimentSpotDesignReadSpecList> jaxbTool = new JaxbObject<ExperimentSpotDesignReadSpecList>();

        ExperimentSpotDesignReadSpecList list = new ExperimentSpotDesignReadSpecList();
        list.setList(new ArrayList());

        for (ExperimentSpotDesignReadSpec obj : objects) {
            ExperimentSpotDesignReadSpec dto = copier.hibernate2dto(ExperimentSpotDesignReadSpec.class, obj);
            list.add(dto);
        }

        Document line = XmlTools.marshalToDocument(jaxbTool, list);

        getResponse().setEntity(XmlTools.getRepresentation(line));
    }

    
}
