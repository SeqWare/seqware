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
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.tables.StudyIDResource;
import org.restlet.resource.Get;
import org.w3c.dom.Document;

/**
 * <p>StudyIDFilter class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class StudyIDFilter extends StudyIDResource {

  /** {@inheritDoc} */
  @Override
  public void doInit() {
    super.doInit();

  }

  /**
   * <p>getXml.</p>
   */
  @Get
  @Override
  public void getXml() {
    StudyService ss = BeanFactory.getStudyServiceBean();

    String path = getRequest().getResourceRef().getPath();
    Study study = null;
    if (getId() != null) {
      study = ss.findByID(convertIDWithResourceException());
    }

    if (path.contains("experiments")) {
      System.out.println("add experiments to Study here");
    }

    Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
    JaxbObject<Study> jaxbTool = new JaxbObject<Study>();

    Study dto = copier.hibernate2dto(Study.class, study);
    Document line = XmlTools.marshalToDocument(jaxbTool, dto);

    getResponse().setEntity(XmlTools.getRepresentation(line));
  }
}
