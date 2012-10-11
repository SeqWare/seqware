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
package net.sourceforge.seqware.common.hibernate;

import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 *
 * @author mtaschuk
 */
public class WorkflowReport {

    public String fromWorkflow(Integer workflowSWA) {
        StringBuilder result = new StringBuilder();
        WorkflowService ws = BeanFactory.getWorkflowServiceBean();
        Workflow workflow = ws.findBySWAccession(workflowSWA);

        if (workflow == null)
        {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "The workflow does not exist: "+workflowSWA);
        }
        
        result.append("Workflow: ").append(workflow.getName()).append("\tVersion ").append(workflow.getVersion()).append("\tSWID: ").append(workflow.getSwAccession()).append("\n");

        for (WorkflowRun run : workflow.getWorkflowRuns()) {

            long time = run.getUpdateTimestamp().getTime() - run.getCreateTimestamp().getTime();
            result.append("\tRun: ").append(run.getStatus()).append("\tStart Date: ").append(run.getCreateTimestamp()).append("\tRunning time: ").append(getTime(time)).append("\tSWID: ").append(run.getSwAccession()).append("\n");
            for (Processing processing : run.getProcessings()) {
                calcProcessing(processing, result);
            }
            for (Processing processing : run.getOffspringProcessings()) {
                calcProcessing(processing, result);
            }
        }

        return result.toString();

    }

    private String getTime(long millis) {
        long hours = millis / 3600000;
        millis = millis - (3600000 * hours);
        long minutes = millis / 60000;
        millis = millis - (60000 * minutes);
        long seconds = millis / 1000;
        millis = millis - (1000 * seconds);
        String strings = hours + ":" + minutes + ":" + seconds + "." + millis;
        return strings;
    }

    private void calcProcessing(Processing processing, StringBuilder result) {
        long time = processing.getUpdateTimestamp().getTime() - processing.getCreateTimestamp().getTime();
        result.append("\t\tProcessing: ").append(processing.getAlgorithm()).append("\n\t\t\tRunning time: ").append(getTime(time)).append("\n\t\t\tSWID: ").append(processing.getSwAccession()).append("\n\t\t\tNumber of files: ").append(processing.getFiles().size()).append("\n");
    }
}
