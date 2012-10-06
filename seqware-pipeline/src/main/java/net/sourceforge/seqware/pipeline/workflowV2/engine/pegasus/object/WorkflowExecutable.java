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
package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Element;

/**
 * 
 * @author yongliang
 */
public class WorkflowExecutable extends PegasusAbstract {

  private Pfn pfn;

  private String namespace;
  private String name;
  private String version;
  private String os;
  private String arch;
  private boolean installed;

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public String getArch() {
    return arch;
  }

  public void setArch(String arch) {
    this.arch = arch;
  }

  public boolean isInstalled() {
    return installed;
  }

  public void setInstalled(boolean installed) {
    this.installed = installed;
  }

  @Override
  public Element serializeXML() {
    Element element = new Element("executable", NAMESPACE);
    element.setAttribute("namespace", this.namespace);
    element.setAttribute("name", this.name);
    element.setAttribute("version", this.version);
    element.setAttribute("arch", this.arch);
    element.setAttribute("os", this.os);
    element.setAttribute("installed", Boolean.toString(this.installed));
    if (this.pfn != null)
      element.addContent(this.pfn.serializeXML());
    return element;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof WorkflowExecutable == false)
      return false;
    if (this == obj)
      return true;
    WorkflowExecutable ex = (WorkflowExecutable) obj;

    return new EqualsBuilder().appendSuper(super.equals(obj)).append(name, ex.name).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(name).hashCode();
  }

  public Pfn getPfn() {
    return pfn;
  }

  public void setPfn(Pfn pfn) {
    this.pfn = pfn;
  }
}
