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
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.lists.SequencerRunList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.restlet.resource.Get;
import org.w3c.dom.Document;

/**
 *
 * @author mtaschuk
 */
public class SequencerRunResource extends DatabaseResource {

    public SequencerRunResource() {
        super("sequencer_run");
    }

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
}
