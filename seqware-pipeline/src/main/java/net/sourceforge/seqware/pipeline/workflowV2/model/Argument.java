package net.sourceforge.seqware.pipeline.workflowV2.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Argument {
  private String key;
  private String value;

  public Argument(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

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

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(this.key).append(this.value).toHashCode();
  }
}
