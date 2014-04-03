package net.sourceforge.seqware.pipeline.workflowV2.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * <p>Argument class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Argument {
  private String key;
  private String value;

  /**
   * <p>Constructor for Argument.</p>
   *
   * @param key a {@link java.lang.String} object.
   * @param value a {@link java.lang.String} object.
   */
  public Argument(String key, String value) {
    this.key = key;
    this.value = value;
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

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Argument == false) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    Argument rhs = (Argument) obj;
    return new EqualsBuilder().append(this.key, rhs.key).append(this.value, rhs.value).isEquals();
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(this.key).append(this.value).toHashCode();
  }
}
