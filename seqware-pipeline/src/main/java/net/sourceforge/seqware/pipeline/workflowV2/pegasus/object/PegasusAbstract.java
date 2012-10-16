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
package net.sourceforge.seqware.pipeline.workflowV2.pegasus.object;

import java.util.Map;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;
import net.sourceforge.seqware.pipeline.workflowV2.pegasus.WorkflowContext;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * <p>Abstract PegasusAbstract class.</p>
 *
 * @author yongliang
 * @version $Id: $Id
 */
public abstract class PegasusAbstract {
  protected Namespace NAMESPACE = Namespace.getNamespace("http://pegasus.isi.edu/schema/DAX");
  protected Namespace XSI = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");


  /**
   * <p>serializeXML.</p>
   *
   * @return a {@link org.jdom.Element} object.
   */
  public abstract Element serializeXML();

  /**
   * <p>getWorkflow.</p>
   *
   * @return a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Workflow} object.
   */
  public Workflow getWorkflow() {
	  return WorkflowContext.getInstance().getWorkflow();
  }

  /**
   * <p>setWorkflow.</p>
   *
   * @param wf a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Workflow} object.
   */
  public void setWorkflow(Workflow wf) {
	  WorkflowContext.getInstance().setWorkflow(wf);
  }

  /**
   * <p>getWorkflowProperty.</p>
   *
   * @param key a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public String getWorkflowProperty(String key) {
	  return WorkflowContext.getInstance().getWorkflow().getProperty(key);
  }
}
