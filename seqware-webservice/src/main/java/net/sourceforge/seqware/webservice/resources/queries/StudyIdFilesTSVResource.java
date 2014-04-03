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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.StringRepresentation;

/**
 * <p>StudyIdFilesTSVResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class StudyIdFilesTSVResource extends BasicRestlet {

    /**
     * <p>Constructor for StudyIdFilesTSVResource.</p>
     *
     * @param context a {@link org.restlet.Context} object.
     */
    public StudyIdFilesTSVResource(Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        init(request);
        if (null != request.getAttributes().get("studyId")) {
            this.handleStudyFile(request, response);
        } else {
            this.handleAllStudiesFiles(request, response);
        }
    }

    private void handleAllStudiesFiles(Request request, Response response) {
        StudyService ss = BeanFactory.getStudyServiceBean();
        final FindAllTheFiles fatf = new FindAllTheFiles();
        if (queryValues.containsKey("show-input-files")) {
            fatf.setReportInputFiles(true);
        }

        final List<Study> studies = ss.list();
        OutputRepresentation output = new OutputRepresentation(MediaType.TEXT_TSV) {
            @Override
            public void write(OutputStream out) throws IOException {
                PrintWriter writer = new PrintWriter(out);
                try{
                for (Study study : studies) {
                    if (handleStudy(study, writer, fatf, null)) {
                        return;
                    }
                } 
                } finally{
                    writer.close();
                }
            }
        };
        response.setEntity(output);
    }

    private void handleStudyFile(Request request, Response response) {
        String studySWA = request.getAttributes().get("studyId").toString();

        StudyService ss = BeanFactory.getStudyServiceBean();
        FindAllTheFiles fatf = new FindAllTheFiles();
        if (queryValues.containsKey("show-input-files")) {
            fatf.setReportInputFiles(true);
        }
        
        Study study = (Study) testIfNull(ss.findBySWAccession(parseClientInt(studySWA)));
        StringWriter writer = new StringWriter();
        if (handleStudy(study, writer , fatf, null)) {
            return;
        }
        response.setEntity(new StringRepresentation(writer.toString(), MediaType.TEXT_TSV));
    }

    private boolean handleStudy(Study study, Writer out, FindAllTheFiles fatf, Response response) {
        List<ReturnValue> returnValues = fatf.filesFromStudy(study);
        boolean duplicates = false, showFailedAndRunning = false, showStatus = false, reportInputFiles = false;
        String fileType = FindAllTheFiles.FILETYPE_ALL;
        if (queryValues.containsKey("duplicates")) {
            duplicates = true;
        }
        if (queryValues.containsKey("show-failed-and-running")) {
            showFailedAndRunning = true;
        }
        if (queryValues.containsKey("show-status")) {
            showStatus = true;
        }
        if (queryValues.containsKey("file-type")) {
            fileType = queryValues.get("file-type");
        }
        if (queryValues.containsKey("show-input-files")) {
            reportInputFiles = true;
        }
        try {
            returnValues = FindAllTheFiles.filterReturnValues(returnValues, study.getTitle(), fileType, duplicates, showFailedAndRunning, showStatus);
            FindAllTheFiles.printTSVFile(out, showStatus, returnValues, study.getTitle(), reportInputFiles);
        } catch (IOException ex) {
            if (response != null){
                Log.error("Error writing to StringWriter.", ex);
                response.setStatus(Status.SERVER_ERROR_INTERNAL, "Error writing to "
                    + "StringWriter. The TSV file could not be created. Please "
                    + "contact the SeqWare Helpdesk for assistance: seqware.jira@oicr.on.ca");
            } else{
                Log.error("Error writing to StringWriter.", ex);
                //response.setStatus(Status.SERVER_ERROR_INTERNAL, "Error writing to "
                //		+ "StringWriter. The TSV file could not be created. Please "
                //		+ "contact the SeqWare Helpdesk for assistance: seqware.jira@oicr.on.ca");
                return true;
            }
        }
        return false;
    }
}
