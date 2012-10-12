package net.sourceforge.seqware.pipeline.workflowV2.model;

/**
 * <p>JobProfile class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class JobProfile {
  private String ns;
  private String key;
  private String value;

  /**
   * <p>Constructor for JobProfile.</p>
   *
   * @param ns a {@link java.lang.String} object.
   * @param key a {@link java.lang.String} object.
   * @param value a {@link java.lang.String} object.
   */
  public JobProfile(String ns, String key, String value) {
    this.ns = ns;
    this.key = key;
    this.value = value;
  }

  /**
   * <p>getNamespace.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getNamespace() {
    return ns;
  }

  /**
   * <p>setNamespace.</p>
   *
   * @param ns a {@link java.lang.String} object.
   */
  public void setNamespace(String ns) {
    this.ns = ns;
  }

  /**
   * <p>Getter for the field <code>key</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getKey() {
    return key;
  }

  /**
   * <p>Setter for the field <code>key</code>.</p>
   *
   * @param key a {@link java.lang.String} object.
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * <p>Getter for the field <code>value</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getValue() {
    return value;
  }

  /**
   * <p>Setter for the field <code>value</code>.</p>
   *
   * @param value a {@link java.lang.String} object.
   */
  public void setValue(String value) {
    this.value = value;
  }
}
