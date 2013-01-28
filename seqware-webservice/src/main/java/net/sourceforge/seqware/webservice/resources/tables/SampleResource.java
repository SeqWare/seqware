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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.lists.SampleList;
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
 * <p>SampleResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class SampleResource extends DatabaseResource {

    /**
     * <p>Constructor for SampleResource.</p>
     */
    public SampleResource() {
        super("sample");
    }

    /**
     * {@inheritDoc}
     */
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

        if (queryValues.get("title") != null) {
            jaxbTool = new JaxbObject<Sample>();
            Sample sample = (Sample) testIfNull(ss.findByTitle(queryValues.get("title")));
            Sample dto = copier.hibernate2dto(Sample.class, sample);
            Document line = XmlTools.marshalToDocument(jaxbTool, dto);
            getResponse().setEntity(XmlTools.getRepresentation(line));

        } else if (queryValues.get("name") != null) {

            jaxbTool = new JaxbObject<Sample>();
            Sample sample = (Sample) testIfNull(ss.findByName(queryValues.get("name")));
            Sample dto = copier.hibernate2dto(Sample.class, sample);
            Document line = XmlTools.marshalToDocument(jaxbTool, dto);
            getResponse().setEntity(XmlTools.getRepresentation(line));

        } else if (queryValues.get("matches") != null) {
            jaxbTool = new JaxbObject<SampleList>();
            String name = queryValues.get("matches");

            List<Sample> samples = (List<Sample>) testIfNull(ss.matchName(name));
            SampleList eList = new SampleList();
            eList.setList(new ArrayList());

            for (Sample sample : samples) {
                Sample dto = copier.hibernate2dto(Sample.class, sample);
                eList.add(dto);
            }
            Document line = XmlTools.marshalToDocument(jaxbTool, eList);
            getResponse().setEntity(XmlTools.getRepresentation(line));
        } else {
            jaxbTool = new JaxbObject<SampleList>();
            List<Sample> samples = (List<Sample>) testIfNull(ss.list());
            SampleList eList = new SampleList();
            eList.setList(new ArrayList());

            for (Sample sample : samples) {
                Sample dto = copier.hibernate2dto(Sample.class, sample);
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
     * @throws org.restlet.resource.ResourceException if any.
     */
    @Post("xml")
    public void postJaxb(Representation entity) throws ResourceException {
        try {

            authenticate();

            JaxbObject<Sample> jo = new JaxbObject<Sample>();
            String text = entity.getText();
            Sample o = null;
            try {
                o = (Sample) XmlTools.unMarshal(jo, new Sample(), text);
            } catch (SAXException ex) {
                throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, ex);
            }

            if (o.getOwner() == null) {
                o.setOwner(registration);
            }

            if (o.getExperiment() != null) {
                ExperimentService es = BeanFactory.getExperimentServiceBean();
                Experiment newExp = es.findBySWAccession(o.getExperiment().getSwAccession());
                if (newExp != null) {
                    o.setExperiment(newExp);
                } else {
                    Log.info("Could not be found " + o.getExperiment());
                }
            }

            if (null != o.getParents()) {
                SampleService ss = BeanFactory.getSampleServiceBean();
                HashSet<Sample> parents = new HashSet<Sample>();
                for (Sample s : o.getParents()) {
                    parents.add(ss.findByID(s.getSampleId()));
                }
                o.setParents(parents);
            }
            if (null != o.getChildren()) {
                SampleService ss = BeanFactory.getSampleServiceBean();
                HashSet<Sample> children = new HashSet<Sample>();
                for (Sample s : o.getChildren()) {
                    children.add(ss.findByID(s.getSampleId()));
                }
                o.setChildren(children);
            }


            //persist object
            SampleService service = BeanFactory.getSampleServiceBean();
            Integer swAccession = service.insert(registration, o);

            Sample sample = (Sample) testIfNull(service.findBySWAccession(swAccession));
            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            Sample detachedSample = copier.hibernate2dto(Sample.class, sample);

            Document line = XmlTools.marshalToDocument(jo, detachedSample);
            getResponse().setEntity(XmlTools.getRepresentation(line));
            getResponse().setLocationRef(getRequest().getRootRef() + "/samples/" + detachedSample.getSwAccession());
            getResponse().setStatus(Status.SUCCESS_CREATED);
        } catch (SecurityException e) {
            getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e);
        } catch (Exception e) {
            e.printStackTrace();
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
        }

    }
}
