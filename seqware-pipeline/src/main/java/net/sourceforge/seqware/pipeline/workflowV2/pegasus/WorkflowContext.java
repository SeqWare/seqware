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
package net.sourceforge.seqware.pipeline.workflowV2.pegasus;

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

	private WorkflowContext() {

	}

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
}
