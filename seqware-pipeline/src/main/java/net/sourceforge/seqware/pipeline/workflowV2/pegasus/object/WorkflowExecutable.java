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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Element;

/**
 * <p>WorkflowExecutable class.</p>
 *
 * @author yongliang
 * @version $Id: $Id
 */
public class WorkflowExecutable extends PegasusAbstract {

  private Pfn pfn;

  private String namespace;
  private String name;
  private String version;
  private String os;
  private String arch;
  private boolean installed;

  /**
   * <p>Getter for the field <code>namespace</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getNamespace() {
    return namespace;
  }

  /**
   * <p>Setter for the field <code>namespace</code>.</p>
   *
   * @param namespace a {@link java.lang.String} object.
   */
  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  /**
   * <p>Getter for the field <code>name</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getName() {
    return name;
  }

  /**
   * <p>Setter for the field <code>name</code>.</p>
   *
   * @param name a {@link java.lang.String} object.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * <p>Getter for the field <code>version</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getVersion() {
    return version;
  }

  /**
   * <p>Setter for the field <code>version</code>.</p>
   *
   * @param version a {@link java.lang.String} object.
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * <p>Getter for the field <code>os</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getOs() {
    return os;
  }

  /**
   * <p>Setter for the field <code>os</code>.</p>
   *
   * @param os a {@link java.lang.String} object.
   */
  public void setOs(String os) {
    this.os = os;
  }

  /**
   * <p>Getter for the field <code>arch</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getArch() {
    return arch;
  }

  /**
   * <p>Setter for the field <code>arch</code>.</p>
   *
   * @param arch a {@link java.lang.String} object.
   */
  public void setArch(String arch) {
    this.arch = arch;
  }

  /**
   * <p>isInstalled.</p>
   *
   * @return a boolean.
   */
  public boolean isInstalled() {
    return installed;
  }

  /**
   * <p>Setter for the field <code>installed</code>.</p>
   *
   * @param installed a boolean.
   */
  public void setInstalled(boolean installed) {
    this.installed = installed;
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof WorkflowExecutable == false)
      return false;
    if (this == obj)
      return true;
    WorkflowExecutable ex = (WorkflowExecutable) obj;

    return new EqualsBuilder().appendSuper(super.equals(obj)).append(name, ex.name).isEquals();
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(name).hashCode();
  }

  /**
   * <p>Getter for the field <code>pfn</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.pipeline.workflowV2.pegasus.object.Pfn} object.
   */
  public Pfn getPfn() {
    return pfn;
  }

  /**
   * <p>Setter for the field <code>pfn</code>.</p>
   *
   * @param pfn a {@link net.sourceforge.seqware.pipeline.workflowV2.pegasus.object.Pfn} object.
   */
  public void setPfn(Pfn pfn) {
    this.pfn = pfn;
  }
}
