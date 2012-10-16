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
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.lists.SampleList;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;

/**
 * This resource should pull back only samples without parents
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class RootSampleResource extends DatabaseResource {

    /**
     * <p>Constructor for RootSampleResource.</p>
     */
    public RootSampleResource() {
        super("sample");
    }

    /** {@inheritDoc} */
    @Override
    protected void doInit() throws ResourceException {
        super.doInit();
    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        authenticate();
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject jaxbTool;
        for (String key : queryValues.keySet()) {
            Log.debug("key: " + key + " -> " + queryValues.get(key));
        }

        SampleService ss = BeanFactory.getSampleServiceBean();

        jaxbTool = new JaxbObject<SampleList>();
        List<Sample> samples = (List<Sample>) testIfNull(ss.list());
        SampleList eList = new SampleList();
        eList.setList(new ArrayList());

        for (Sample sample : samples) {
            if (sample.getParents().isEmpty()) {
                Sample dto = copier.hibernate2dto(Sample.class, sample);
                eList.add(dto);
            }
        }
        Document line = XmlTools.marshalToDocument(jaxbTool, eList);
        getResponse().setEntity(XmlTools.getRepresentation(line));
    }
}
