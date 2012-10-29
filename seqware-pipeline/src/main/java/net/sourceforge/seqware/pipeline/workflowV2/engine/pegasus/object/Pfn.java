package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object;

import org.jdom.Element;

public class Pfn  {

  private String url;
  private String site;


  /** {@inheritDoc} */
  public Element serializeXML() {
    Element element = new Element("pfn", Adag.NAMESPACE);
    element.setAttribute("url", this.url);
    element.setAttribute("site", this.site);
    return element;

  }

  /**
   * <p>Getter for the field <code>url</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getUrl() {
    return this.url;
  }

  /**
   * <p>Setter for the field <code>url</code>.</p>
   *
   * @param url a {@link java.lang.String} object.
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * <p>Getter for the field <code>site</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getSite() {
    return site;
  }

  /**
   * <p>Setter for the field <code>site</code>.</p>
   *
   * @param site a {@link java.lang.String} object.
   */
  public void setSite(String site) {
    this.site = site;
  }
}
