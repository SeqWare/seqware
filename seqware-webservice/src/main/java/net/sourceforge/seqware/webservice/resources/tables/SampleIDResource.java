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

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import net.sf.beanlib.CollectionPropertyName;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Sample;
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
 * <p>
 * SampleIDResource class.
 * </p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class SampleIDResource extends DatabaseIDResource {

    /**
     * <p>
     * Constructor for SampleIDResource.
     * </p>
     */
    public SampleIDResource() {
        super("sampleId");
    }

    /**
     * <p>
     * getXml.
     * </p>
     */
    @Get
    public void getXml() {
        authenticate();

        JaxbObject<Lane> jaxbTool = new JaxbObject<>();
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();

        SampleService ss = BeanFactory.getSampleServiceBean();
        Sample sample = testIfNull(ss.findBySWAccession(getId()));
        CollectionPropertyName<Sample>[] createCollectionPropertyNames = CollectionPropertyName.createCollectionPropertyNames(Sample.class,
                new String[] { "sampleAttributes" });
        Sample dto = copier.hibernate2dto(Sample.class, sample, new Class<?>[] {}, createCollectionPropertyNames);

        if (fields.contains("lanes")) {
            SortedSet<Lane> lanes = sample.getLanes();
            if (lanes != null) {
                SortedSet<Lane> copiedLanes = new TreeSet<>();
                for (Lane lane : lanes) {
                    copiedLanes.add(copier.hibernate2dto(Lane.class, lane));
                }
                dto.setLanes(copiedLanes);
            } else {
                Log.info("Could not be found: lanes");
            }
        }
        if (fields.contains("ius")) {
            SortedSet<IUS> ius = sample.getIUS();
            if (ius != null) {
                SortedSet<IUS> copiedIUS = new TreeSet<>();
                for (IUS i : ius) {
                    copiedIUS.add(copier.hibernate2dto(IUS.class, i));
                }
                dto.setIUS(copiedIUS);
            }
            {
                Log.info("Could not be found : ius");
            }
        }

        Document line = XmlTools.marshalToDocument(jaxbTool, dto);
        getResponse().setEntity(XmlTools.getRepresentation(line));
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    @Put
    public Representation put(Representation entity) {
        authenticate();
        Representation representation = null;
        try {
            JaxbObject<Sample> jo = new JaxbObject<>();
            String text = entity.getText();
            Sample o = null;
            try {
                o = (Sample) XmlTools.unMarshal(jo, new Sample(), text);
            } catch (SAXException ex) {
                throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, ex);
            }

            SampleService service = BeanFactory.getSampleServiceBean();
            Sample sample = testIfNull(service.findByID(o.getSampleId()));
            sample.givesPermission(registration);

            String anonymizedName = o.getAnonymizedName();
            String individualName = o.getIndividualName();
            Integer swAccession = o.getSwAccession();
            String name = o.getName();
            String title = o.getTitle();
            String alias = o.getAlias();
            String description = o.getDescription();
            String type = o.getType();
            String tags = o.getTags();
            String adapters = o.getAdapters();
            String regions = o.getRegions();
            Integer expectedNumRuns = o.getExpectedNumRuns();
            Integer expectedNumSpots = o.getExpectedNumSpots();
            Integer expectedNumReads = o.getExpectedNumReads();
            Boolean skip = o.getSkip();
            Boolean isSelected = o.getIsSelected();
            Boolean isHasFile = o.getIsHasFile();
            Integer countFile = o.getCountFile();

            if (null != anonymizedName) sample.setAnonymizedName(anonymizedName);
            if (null != individualName) sample.setIndividualName(individualName);
            if (null != swAccession) sample.setSwAccession(swAccession);
            if (null != name) sample.setName(name);
            if (null != title) sample.setTitle(title);
            if (null != alias) sample.setAlias(alias);
            if (null != description) sample.setDescription(description);
            if (null != type) sample.setType(type);
            if (null != tags) sample.setTags(tags);
            if (null != adapters) sample.setAdapters(adapters);
            if (null != regions) sample.setRegions(regions);
            if (null != expectedNumRuns) sample.setExpectedNumRuns(expectedNumRuns);
            if (null != expectedNumSpots) sample.setExpectedNumSpots(expectedNumSpots);
            if (null != expectedNumReads) sample.setExpectedNumReads(expectedNumReads);
            if (null != skip) sample.setSkip(skip);
            if (null != isSelected) sample.setIsSelected(isSelected);
            if (null != isHasFile) sample.setIsHasFile(isHasFile);
            if (null != countFile) sample.setCountFile(countFile);

            if (null != o.getSampleAttributes()) {
                // SEQWARE-1577 - AttributeAnnotator cascades deletes when annotating
                SampleIDResource.mergeAttributes(sample.getSampleAttributes(), o.getSampleAttributes(), sample);
            }
            if (null != o.getParents()) {
                SampleService ss = BeanFactory.getSampleServiceBean();
                Set<Sample> parents = new HashSet<>(sample.getParents());
                for (Sample s : o.getParents()) {
                    parents.add(ss.findByID(s.getSampleId()));
                }
                sample.setParents(parents);
            }
            if (null != o.getChildren()) {
                SampleService ss = BeanFactory.getSampleServiceBean();
                Set<Sample> children = new HashSet<>(sample.getChildren());
                for (Sample s : o.getChildren()) {
                    children.add(ss.findByID(s.getSampleId()));
                }
                sample.setChildren(children);
            }
            service.update(sample);

            // persist object
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

        return representation;
    }

}
