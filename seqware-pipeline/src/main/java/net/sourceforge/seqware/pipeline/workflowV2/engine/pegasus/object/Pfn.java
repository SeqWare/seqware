package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object;

import org.jdom.Element;

public class Pfn  {

  private String url;
  private String site;


  public Element serializeXML() {
    Element element = new Element("pfn", Adag.NAMESPACE);
    element.setAttribute("url", this.url);
    element.setAttribute("site", this.site);
    return element;

  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }
}