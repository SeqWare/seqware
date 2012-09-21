/*
 * Copyright (C) 2012 SeqWare
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
package net.sourceforge.seqware.webservice.resources.queries;

import java.util.List;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.lists.ReturnValueList;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.w3c.dom.Document;

/**
 *
 * @author mtaschuk
 */
public class SampleIdFilesResource extends BasicRestlet {

    public SampleIdFilesResource(Context context) {
        super(context);
    }

    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        String id = request.getAttributes().get("sampleId").toString();
        ReturnValueList returnValues = new ReturnValueList();

        SampleService ss = BeanFactory.getSampleServiceBean();
        Sample sample = (Sample) testIfNull(ss.findBySWAccession(Integer.parseInt(id)));
        FindAllTheFiles fatf = new FindAllTheFiles();
        List<ReturnValue> list = fatf.filesFromSample(sample, null, null);
        returnValues.setList(list);

        JaxbObject<ReturnValueList> jaxbTool = new JaxbObject<ReturnValueList>();

        Document line = XmlTools.marshalToDocument(jaxbTool, returnValues);

        response.setEntity(XmlTools.getRepresentation(line));
    }
}
