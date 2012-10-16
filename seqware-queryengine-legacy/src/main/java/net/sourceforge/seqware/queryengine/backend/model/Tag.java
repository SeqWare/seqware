/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Tag class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Tag extends Model {
    
  private String tag = null;
  private String value = null;
  
  /**
   * <p>Getter for the field <code>tag</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getTag() {
    return tag;
  }
  /**
   * <p>Setter for the field <code>tag</code>.</p>
   *
   * @param tag a {@link java.lang.String} object.
   */
  public void setTag(String tag) {
    this.tag = tag;
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
