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
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.PlatformService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Platform;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.lists.IUSList;
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
 * <p>IusResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class IusResource extends DatabaseResource {

  private Logger logger;

  /**
   * <p>Constructor for IusResource.</p>
   */
  public IusResource() {
    super("ius");
    logger = Logger.getLogger(IUS.class);
  }

  /**
   * <p>getXml.</p>
   *
   * @throws java.io.IOException if any.
   */
  @Get
  public void getXml() throws IOException {
    authenticate();
    IUSService ss = BeanFactory.getIUSServiceBean();
    Hibernate3DtoCopier copier = new Hibernate3DtoCopier();

    Document line;
    if (queryValues.get("id") != null) {
      IUS p = (IUS) testIfNull(ss.findByID(parseClientInt(queryValues.get("id"))));

      JaxbObject<IUS> jaxbTool = new JaxbObject<IUS>();
      IUS dto = copier.hibernate2dto(IUS.class, p);
      line = XmlTools.marshalToDocument(jaxbTool, dto);

    } else {

      JaxbObject<IUSList> jaxbTool = new JaxbObject<IUSList>();
      IUSList list = new IUSList();
      List<IUS> iuses = (List<IUS>) testIfNull(ss.list());

      for (IUS i : iuses) {
        list.add(copier.hibernate2dto(IUS.class, i));
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
      JaxbObject<IUS> jo = new JaxbObject<IUS>();
      String text = entity.getText();
      IUS o = null;
      try {
        o = (IUS) XmlTools.unMarshal(jo, new IUS(), text);
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
      if (o.getLane() != null) {
        LaneService ps = BeanFactory.getLaneServiceBean();
        Lane lane = (Lane) testIfNull(ps.findByID(o.getLane().getLaneId()));
        o.setLane(lane);
      }

      // attempt to save foreign keys, I guess this is replacing an empty object with a fully populated one?
      if (o.getSample() != null) {
        SampleService ps = BeanFactory.getSampleServiceBean();
        Sample sample = (Sample) testIfNull(ps.findByID(o.getSample().getSampleId()));
        o.setSample(sample);
      }

      //persist object
      IUSService service = BeanFactory.getIUSServiceBean();
      Integer swAccession = service.insert(registration, o);

      IUS obj = (IUS) testIfNull(service.findBySWAccession(swAccession));
      Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
      IUS detachedIUS = copier.hibernate2dto(IUS.class, obj);

      Document line = XmlTools.marshalToDocument(jo, detachedIUS);
      getResponse().setEntity(XmlTools.getRepresentation(line));
      getResponse().setLocationRef(getRequest().getRootRef() + "/ius/" + detachedIUS.getSwAccession());
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
