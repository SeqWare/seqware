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
import java.util.List;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.LibrarySelectionService;
import net.sourceforge.seqware.common.business.LibrarySourceService;
import net.sourceforge.seqware.common.business.LibraryStrategyService;
import net.sourceforge.seqware.common.business.PlatformService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LibrarySelection;
import net.sourceforge.seqware.common.model.LibrarySource;
import net.sourceforge.seqware.common.model.LibraryStrategy;
import net.sourceforge.seqware.common.model.Platform;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.lists.LaneList;
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
 * <p>LaneResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class LaneResource extends DatabaseResource {

  private Logger logger;

  /**
   * <p>Constructor for LaneResource.</p>
   */
  public LaneResource() {
    super("lane");
    logger = Logger.getLogger(Lane.class);
  }

  /**
   * <p>getXml.</p>
   *
   * @throws java.io.IOException if any.
   */
  @Get
  public void getXml() throws IOException {
    authenticate();
    LaneService ss = BeanFactory.getLaneServiceBean();
    Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
    Document line;

    if (queryValues.get("id") != null) {
      JaxbObject<Lane> jaxbTool = new JaxbObject<Lane>();
      Lane p = (Lane) testIfNull(ss.findByID(parseClientInt(queryValues.get("id"))));

      Lane dto = copier.hibernate2dto(Lane.class, p);
      line = XmlTools.marshalToDocument(jaxbTool, dto);

    } else {
      JaxbObject<LaneList> jaxbTool = new JaxbObject<LaneList>();
      LaneList list = new LaneList();
      List<Lane> lanes = (List<Lane>) testIfNull(ss.list());
      for (Lane l : lanes) {
        list.add(copier.hibernate2dto(Lane.class, l));
      }
      line = XmlTools.marshalToDocument(jaxbTool, list);

    }

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
      JaxbObject<Lane> jo = new JaxbObject<Lane>();
      String text = entity.getText();
      Lane o = null;
      try {
        o = (Lane) XmlTools.unMarshal(jo, new Lane(), text);
      } catch (SAXException ex) {
        throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, ex);
      }

      if (o.getOwner() == null) {
        o.setOwner(registration);
      } else {
        Registration reg = BeanFactory.getRegistrationServiceBean().findByEmailAddress(o.getOwner().getEmailAddress());
        if (reg != null) {
          o.setOwner(reg);
        } else {
          logger.info("Could not be found: " + o.getOwner());
        }
      }
      
      // attempt to save foreign keys, I guess this is replacing an empty object with a fully populated one?
      
      if (o.getSequencerRun() != null) {
        SequencerRunService ps = BeanFactory.getSequencerRunServiceBean();
        SequencerRun sequencerRun = (SequencerRun) testIfNull(ps.findByID(o.getSequencerRun().getSequencerRunId()));
        o.setSequencerRun(sequencerRun);
      }
      
      if (o.getLibraryStrategy() != null) {
        LibraryStrategyService ps = BeanFactory.getLibraryStrategyServiceBean();
        LibraryStrategy obj = (LibraryStrategy) testIfNull(ps.findByID(o.getLibraryStrategy().getLibraryStrategyId()));
        o.setLibraryStrategy(obj);
      }
      
      if (o.getLibrarySelection() != null) {
        LibrarySelectionService ps = BeanFactory.getLibrarySelectionServiceBean();
        LibrarySelection obj = (LibrarySelection) testIfNull(ps.findByID(o.getLibrarySelection().getLibrarySelectionId()));
        o.setLibrarySelection(obj);
      }
      
      if (o.getLibrarySource() != null) {
        LibrarySourceService ps = BeanFactory.getLibrarySourceServiceBean();
        LibrarySource obj = (LibrarySource) testIfNull(ps.findByID(o.getLibrarySource().getLibrarySourceId()));
        o.setLibrarySource(obj);
      }

      //persist object
      LaneService service = BeanFactory.getLaneServiceBean();
      Integer swAccession = service.insert(registration, o);

      Lane obj = (Lane) testIfNull(service.findBySWAccession(swAccession));
      Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
      Lane detachedLane = copier.hibernate2dto(Lane.class, obj);

      Document line = XmlTools.marshalToDocument(jo, detachedLane);
      getResponse().setEntity(XmlTools.getRepresentation(line));
      getResponse().setLocationRef(getRequest().getRootRef() + "/lanes/" + detachedLane.getSwAccession());
      getResponse().setStatus(Status.SUCCESS_CREATED);
    } catch (SecurityException e) {
      getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
      getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    } catch (Exception e) {
      e.printStackTrace();
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
    }

  }
}
