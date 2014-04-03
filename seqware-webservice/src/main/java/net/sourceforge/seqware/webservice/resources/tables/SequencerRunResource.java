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
import net.sourceforge.seqware.common.business.PlatformService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Platform;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.lists.SequencerRunList;
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
 * <p>SequencerRunResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class SequencerRunResource extends DatabaseResource {

  private Logger logger;

  /**
   * <p>Constructor for SequencerRunResource.</p>
   */
  public SequencerRunResource() {
    super("sequencer_run");
    logger = Logger.getLogger(SequencerRun.class);
  }

  /**
   * <p>getXml.</p>
   */
  @Get
  public void getXml() {
    authenticate();
    SequencerRunService ss = BeanFactory.getSequencerRunServiceBean();
    Hibernate3DtoCopier copier = new Hibernate3DtoCopier();

    if (queryValues.get("name") != null) {
      String name = queryValues.get("name");
      SequencerRun study = (SequencerRun) testIfNull(ss.findByName(name));
      JaxbObject<SequencerRun> jaxbTool = new JaxbObject<SequencerRun>();
      SequencerRun dto = copier.hibernate2dto(SequencerRun.class, study);
      Document line = XmlTools.marshalToDocument(jaxbTool, dto);
      getResponse().setEntity(XmlTools.getRepresentation(line));
    } else {
      JaxbObject<SequencerRunList> jaxbTool = new JaxbObject<SequencerRunList>();
      List<SequencerRun> runs = (List<SequencerRun>) testIfNull(ss.list());
      SequencerRunList eList = new SequencerRunList();
      eList.setList(new ArrayList());

      for (SequencerRun sequencerRun : runs) {
        SequencerRun dto = copier.hibernate2dto(SequencerRun.class, sequencerRun);
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
    authenticate();
    try {
      JaxbObject<SequencerRun> jo = new JaxbObject<SequencerRun>();
      String text = entity.getText();
      SequencerRun o = null;
      try {
        o = (SequencerRun) XmlTools.unMarshal(jo, new SequencerRun(), text);
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
      if (o.getPlatform() != null) {
        PlatformService ps = BeanFactory.getPlatformServiceBean();
        Platform platform = (Platform) testIfNull(ps.findByID(o.getPlatform().getPlatformId()));
        o.setPlatform(platform);
      }


      //persist object
      SequencerRunService service = BeanFactory.getSequencerRunServiceBean();
      Integer swAccession = service.insert(registration, o);

      SequencerRun obj = (SequencerRun) testIfNull(service.findBySWAccession(swAccession));
      Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
      SequencerRun detachedSequencerRun = copier.hibernate2dto(SequencerRun.class, obj);

      Document line = XmlTools.marshalToDocument(jo, detachedSequencerRun);
      getResponse().setEntity(XmlTools.getRepresentation(line));
      getResponse().setLocationRef(getRequest().getRootRef() + "/sequencerruns/" + detachedSequencerRun.getSwAccession());
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
