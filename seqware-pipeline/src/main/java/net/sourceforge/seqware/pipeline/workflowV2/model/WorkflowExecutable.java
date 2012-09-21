package net.sourceforge.seqware.pipeline.workflowV2.model;

public enum WorkflowExecutable {
  JAVA("java", "seqware", "1.6.0"), PERL("perl", "seqware", "5.14.1"), R("R", "seqware", ""), CUSTOM("custom",
      "seqware", "");

  private String name;
  private String version;
  private String namespace;

  private WorkflowExecutable(String name, String namespace, String version) {
    this.name = name;
    this.namespace = namespace;
    this.version = version;
  }

  public String getName() {
    return this.name;
  }

  public String getVersion() {
    return this.version;
  }

  public String getNamespace() {
    return this.namespace;
  }
}
