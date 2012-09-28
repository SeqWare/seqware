package net.sourceforge.seqware.pipeline.workflowV2.model;

public class JobProfile {
  private String ns;
  private String key;
  private String value;

  public JobProfile(String ns, String key, String value) {
    this.ns = ns;
    this.key = key;
    this.value = value;
  }

  public String getNamespace() {
    return ns;
  }

  public void setNamespace(String ns) {
    this.ns = ns;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
