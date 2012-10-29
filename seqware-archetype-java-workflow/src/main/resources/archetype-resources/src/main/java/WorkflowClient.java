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
package net.sourceforge.seqware;

<<<<<<< HEAD:seqware-archetype-java-workflow/src/main/resources/archetype-resources/src/main/java/WorkflowClient.java
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;
=======
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;

/**
 * <p>WorkflowContext class.</p>
 *
 * @author yongliang
 * @version $Id: $Id
 */
public class WorkflowContext {
	private static WorkflowContext instance;

	private Workflow workflow;

	/**
	 * <p>Getter for the field <code>instance</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.pipeline.workflowV2.pegasus.WorkflowContext} object.
	 */
	public static WorkflowContext getInstance() {
		if(null== instance) {
			instance = new WorkflowContext();
		}
		return instance;
	}
>>>>>>> develop:seqware-pipeline/src/main/java/net/sourceforge/seqware/pipeline/workflowV2/pegasus/WorkflowContext.java

public class WorkflowClient extends AbstractWorkflowDataModel {


<<<<<<< HEAD:seqware-archetype-java-workflow/src/main/resources/archetype-resources/src/main/java/WorkflowClient.java
	
	@Override
	public void buildWorkflow() {
		
	}

=======
	/**
	 * <p>Setter for the field <code>workflow</code>.</p>
	 *
	 * @param wf a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Workflow} object.
	 */
	public void setWorkflow(Workflow wf) {
		this.workflow = wf;
	}

	/**
	 * <p>Getter for the field <code>workflow</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Workflow} object.
	 */
	public Workflow getWorkflow() {
		return this.workflow;
	}
>>>>>>> develop:seqware-pipeline/src/main/java/net/sourceforge/seqware/pipeline/workflowV2/pegasus/WorkflowContext.java
}
