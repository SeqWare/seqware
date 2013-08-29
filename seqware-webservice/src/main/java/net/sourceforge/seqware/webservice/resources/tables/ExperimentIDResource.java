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
import net.sf.beanlib.CollectionPropertyName;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.*;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.*;
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
 * <p>ExperimentIDResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class ExperimentIDResource extends DatabaseIDResource {

    /**
     * <p>Constructor for ExperimentIDResource.</p>
     */
    public ExperimentIDResource() {
        super("experimentId");
    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        ExperimentService ss = BeanFactory.getExperimentServiceBean();
        
        Experiment experiment = (Experiment) testIfNull(ss.findBySWAccession(getId()));
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<Experiment> jaxbTool = new JaxbObject<Experiment>();

        Experiment dto = copier.hibernate2dto(Experiment.class, experiment, new Class<?>[]{ExperimentSpotDesign.class, ExperimentLibraryDesign.class}, new CollectionPropertyName<?>[]{});
        
		if (fields.contains("attributes")) {
			Set<ExperimentAttribute> eas = experiment.getExperimentAttributes();
			if(eas!=null && !eas.isEmpty()) {
				Set<ExperimentAttribute> newEas = new TreeSet<ExperimentAttribute>();
				for(ExperimentAttribute ea: eas) {
					newEas.add(copier.hibernate2dto(ExperimentAttribute.class, ea));
				}
				dto.setExperimentAttributes(newEas);
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
        Experiment newObj = null;
        JaxbObject<Experiment> jo = new JaxbObject<Experiment>();
        try {
            String text = entity.getText();
            newObj = (Experiment) XmlTools.unMarshal(jo, new Experiment(), text);
        } catch (SAXException ex) {
            ex.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ex);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
        }
        try {
            ExperimentService service = BeanFactory.getExperimentServiceBean();
            Experiment exp = (Experiment) testIfNull(service.findByID(testIfNull(newObj).getExperimentId()));
            exp.givesPermission(registration);

            //simple types
            String title = newObj.getTitle();
            String name = newObj.getName();
            String desc = newObj.getDescription();
            String alias = newObj.getAlias();
            String accession = newObj.getAccession();
            String status = newObj.getStatus();
            String centerName = newObj.getCenterName();
            String sequencerSpace = newObj.getSequenceSpace();
            String baseCaller = newObj.getBaseCaller();
            String qualityScorer = newObj.getQualityScorer();
            Integer qualityNumberLevels = newObj.getQualityNumberOfLevels();
            Integer qualityMultiplier = newObj.getQualityMultiplier();
            Long expectedNumberSpots = newObj.getExpectedNumberSpots();
            Long expectedNumberReads = newObj.getExpectedNumberReads();

            //foreign keys
            Study study = newObj.getStudy();
            Registration owner = newObj.getOwner();

            // sets
            Set<ExperimentAttribute> expAttributes = newObj.getExperimentAttributes();

			if (title != null) {
				exp.setTitle(title);
			}
            if (name != null) {
                exp.setName(name);
            }
            if (desc != null) {
                exp.setDescription(desc);
            }
            if (alias != null) {
                exp.setAlias(alias);
            }
            if (accession != null) {
                exp.setAccession(accession);
            }
            if (status != null) {
                exp.setStatus(status);
            }
            if (centerName != null) {
                exp.setCenterName(centerName);
            }
            if (sequencerSpace != null) {
                exp.setSequenceSpace(sequencerSpace);
            }
			if (baseCaller != null) {
				exp.setBaseCaller(baseCaller);
			}
			if (qualityScorer !=null) {
				exp.setQualityScorer(qualityScorer);
			}
			if (qualityNumberLevels !=null) {
				exp.setQualityNumberOfLevels(qualityNumberLevels);
			}
			if (qualityMultiplier != null) {
				exp.setQualityMultiplier(qualityMultiplier);
			}
			if (expectedNumberSpots != null) {
				exp.setExpectedNumberSpots(expectedNumberSpots);
			}
			if (expectedNumberReads !=null) {
				exp.setExpectedNumberReads(expectedNumberReads);
			}

            if (study != null) {
                StudyService ss = BeanFactory.getStudyServiceBean();
                Study newStudy = ss.findByID(study.getStudyId());
                if (newStudy != null && newStudy.givesPermission(registration)) {
                    exp.setStudy(newStudy);
                } else if (newStudy == null) {
                    Log.info("Could not be found " + study);
                }
            }

            if (owner != null) {
                RegistrationService rs = BeanFactory.getRegistrationServiceBean();
                Registration newReg = rs.findByEmailAddress(owner.getEmailAddress());
                if (newReg != null) {
                    exp.setOwner(newReg);
                } else {
                    Log.info("Could not be found " + owner);
                }
            } else if (exp.getOwner() == null) {
                exp.setOwner(registration);
            }

            if (null != expAttributes) {
                //SEQWARE-1577 - AttributeAnnotator cascades deletes when annotating
                this.mergeAttributes(exp.getExperimentAttributes(), expAttributes, exp);
            }

	    service.update(registration,exp);


            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            Experiment detachedLane = copier.hibernate2dto(Experiment.class, exp);

            Document line = XmlTools.marshalToDocument(jo, detachedLane);
            representation = XmlTools.getRepresentation(line);
            getResponse().setEntity(representation);
            getResponse().setLocationRef(getRequest().getRootRef() + "/experiments/" + detachedLane.getSwAccession());
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
