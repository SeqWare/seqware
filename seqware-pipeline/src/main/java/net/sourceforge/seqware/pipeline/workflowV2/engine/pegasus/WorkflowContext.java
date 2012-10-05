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
package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus;

import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow2;

/**
 *
 * @author yongliang
 */
public class WorkflowContext {
	private static WorkflowContext instance;

	private Workflow2 workflow;

	public static WorkflowContext getInstance() {
		if(null== instance) {
			instance = new WorkflowContext();
		}
		return instance;
	}

	private WorkflowContext() {

	}

	public void setWorkflow(Workflow2 wf) {
		this.workflow = wf;
	}

	public Workflow2 getWorkflow() {
		return this.workflow;
	}
}
