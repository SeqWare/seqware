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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.StringRepresentation;

/**
 *
 * @author mtaschuk
 */
public class StudyIdFilesTSVResource extends BasicRestlet {

    public StudyIdFilesTSVResource(Context context) {
        super(context);
    }

    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        init(request);
		if (null!=request.getAttributes().get("studyId") ) {
			this.handleStudyFile(request, response);
		} else {
			this.handleAllStudiesFiles(request, response);
		}
    }

	private void handleAllStudiesFiles(Request request, Response response) {
		StudyService ss = BeanFactory.getStudyServiceBean();
        final FindAllTheFiles fatf = new FindAllTheFiles();
		final List<Study> studies = ss.list();
		OutputRepresentation output = new OutputRepresentation(MediaType.TEXT_TSV) {

			@Override
			public void write(OutputStream out) throws IOException {

				for(Study study: studies) {
					List<ReturnValue> returnValues = fatf.filesFromStudy(study);

					boolean duplicates = false, showFailedAndRunning = false, showStatus = false;
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

					try {
						returnValues = FindAllTheFiles.filterReturnValues(returnValues, study.getTitle(), fileType, duplicates, showFailedAndRunning, showStatus);
						FindAllTheFiles.printTSVFile(new PrintWriter(out), showStatus, returnValues, study.getTitle());
					} catch (IOException ex) {
						Log.error("Error writing to StringWriter.", ex);
						//response.setStatus(Status.SERVER_ERROR_INTERNAL, "Error writing to "
						//		+ "StringWriter. The TSV file could not be created. Please "
						//		+ "contact the SeqWare Helpdesk for assistance: seqware.jira@oicr.on.ca");
						return;
					}
				}
			}

		};


		response.setEntity(output);
	}

	private void handleStudyFile(Request request, Response response) {
		String studySWA = request.getAttributes().get("studyId").toString();

        StudyService ss = BeanFactory.getStudyServiceBean();
        FindAllTheFiles fatf = new FindAllTheFiles();
        Study study = (Study) testIfNull(ss.findBySWAccession(Integer.parseInt(studySWA)));
        List<ReturnValue> returnValues = fatf.filesFromStudy(study);



        boolean duplicates = false, showFailedAndRunning = false, showStatus = false;
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

        StringWriter writer = new StringWriter();

        try {
            returnValues = FindAllTheFiles.filterReturnValues(returnValues, study.getTitle(), fileType, duplicates, showFailedAndRunning, showStatus);
            FindAllTheFiles.printTSVFile(writer, showStatus, returnValues, study.getTitle());
        } catch (IOException ex) {
            Log.error("Error writing to StringWriter.", ex);
            response.setStatus(Status.SERVER_ERROR_INTERNAL, "Error writing to "
                    + "StringWriter. The TSV file could not be created. Please "
                    + "contact the SeqWare Helpdesk for assistance: seqware.jira@oicr.on.ca");
            return;
        }
        response.setEntity(new StringRepresentation(writer.toString(), MediaType.TEXT_TSV));
	}
}
