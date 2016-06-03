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
package io.seqware.pipeline.api;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;

/**
 * <p>
 * WorkflowEngine interface.
 * </p>
 * 
 * This interface will eventually encompass all the methods that a workflow engine should have to implement including preparation of
 * workflows and watching workflows. Currently, workflow status checker code still needs to be modified engine-by-engine.
 * 
 * @author yongliang
 * @since 1.1
 */
public interface WorkflowEngine {

    /**
     * Prepare the workflow according to the info filled in the input objectModel.
     * 
     * @param objectModel
     *            model of the workflow to prepare to run
     */
    void prepareWorkflow(AbstractWorkflowDataModel objectModel);

    /**
     * Run the prepared workflow.
     * 
     * @return
     */
    ReturnValue runWorkflow();

    /**
     * An engine-specific token for this workflow run that can be used to lookup relevant runtime data.
     * 
     * @return the token
     */
    String getLookupToken();

    /**
     * The working directory for the prepared workflow run, or null if not yet prepared or if not applicable for the concrete engine.
     * 
     * @return the working directory, or null
     */
    String getWorkingDirectory();

    /**
     * Watch a workflow and return running information until it completes.
     * 
     * The exact nature of the output is arbitrary and likely dependent on the workflow engine.
     * 
     * @param jobToken
     * @return
     */
    ReturnValue watchWorkflow(String jobToken);

}
