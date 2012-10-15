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
package net.sourceforge.seqware.webservice.resources.filters;

import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.tables.SampleIDResource;
import org.restlet.data.MediaType;
import org.restlet.resource.Get;
import org.w3c.dom.Document;

/**
 * <p>SampleIDFilter class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class SampleIDFilter extends SampleIDResource {

    /** {@inheritDoc} */
    @Override
    public void doInit() {
        super.doInit();

    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        SampleService ss = BeanFactory.getSampleServiceBean();
        StringBuilder builder = new StringBuilder();

        String path = getRequest().getResourceRef().getPath();
        Sample sample = null;
        if (getId() != null) {
            sample = ss.findByID(Integer.parseInt(getId()));
        }

        if (path.contains("lanes")) {
            System.out.println("add lanes to Sample here");
        }

        if (path.contains("ius")) {
            System.out.println("add iuses to Sample here");
        }

        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<Sample> jaxbTool = new JaxbObject<Sample>();

        Sample dto = copier.hibernate2dto(Sample.class, sample);
        Document line = XmlTools.marshalToDocument(jaxbTool, dto);

        getResponse().setEntity(XmlTools.getRepresentation(line));
    }
}
