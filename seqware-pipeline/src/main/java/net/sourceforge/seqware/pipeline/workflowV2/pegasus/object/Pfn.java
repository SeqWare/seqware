package net.sourceforge.seqware.pipeline.workflowV2.pegasus.object;

import org.jdom.Element;

public class Pfn extends PegasusAbstract {

  private String url;
  private String site;

  @Override
  public Element serializeXML() {
    Element element = new Element("pfn", NAMESPACE);
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