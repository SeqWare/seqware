package net.sourceforge.seqware.pipeline.workflowV2.model;

/**
 * <p>WorkflowExecutable class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
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

  /**
   * <p>Getter for the field <code>name</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getName() {
    return this.name;
  }

  /**
   * <p>Getter for the field <code>version</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getVersion() {
    return this.version;
  }

  /**
   * <p>Getter for the field <code>namespace</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getNamespace() {
    return this.namespace;
  }
}
