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

import static net.sourceforge.seqware.webservice.resources.BasicResource.parseClientInt;
import static net.sourceforge.seqware.webservice.resources.BasicResource.testIfNull;

import java.util.List;

import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.lists.ReturnValueList;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.w3c.dom.Document;

/**
 * <p>StudyIdFilesResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class StudyIdFilesResource extends BasicRestlet {

    /**
     * <p>Constructor for StudyIdFilesResource.</p>
     *
     * @param context a {@link org.restlet.Context} object.
     */
    public StudyIdFilesResource(Context context) {
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        init(request);
        String id = request.getAttributes().get("studyId").toString();

        List<ReturnValue> returnValues = hello(parseClientInt(id));

        ReturnValueList list = new ReturnValueList();
        list.setList(returnValues);

        JaxbObject<ReturnValueList> jaxbTool = new JaxbObject<ReturnValueList>();
        Document line = XmlTools.marshalToDocument(jaxbTool, list);
        response.setEntity(XmlTools.getRepresentation(line));
    }

    /**
     * <p>hello.</p>
     *
     * @param studySWA a int.
     * @return a {@link java.util.List} object.
     */
    public List<ReturnValue> hello(int studySWA) {

        StudyService ss = BeanFactory.getStudyServiceBean();
        FindAllTheFiles fatf = new FindAllTheFiles();
        if (this.getQueryValue("requireFiles") != null){
            boolean requireFiles = Boolean.valueOf(this.getQueryValue("requireFiles"));
            fatf.setRequireFiles(requireFiles);
        }
        Study study = (Study) testIfNull(ss.findBySWAccession(studySWA));
        List<ReturnValue> returnValues = fatf.filesFromStudy(study);
        return returnValues;
    }
}
