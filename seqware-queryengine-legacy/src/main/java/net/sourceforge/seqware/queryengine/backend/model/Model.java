package net.sourceforge.seqware.queryengine.backend.model;

import java.util.HashMap;

/**
 * <p>Model class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Model implements ModelInterface {
  
  // These are byte IDs used to detect the object type from a byte stream.
  // They encode both the type and version so the correct model object and
  // tuplebinder can be used
  /** Constant <code>VARIENT=0</code> */
  public static final byte VARIENT = 0;
  /** Constant <code>TAG=10</code> */
  public static final byte TAG = 10;
  /** Constant <code>STRINGID=20</code> */
  public static final byte STRINGID = 20;
  /** Constant <code>FEATURE=30</code> */
  public static final byte FEATURE = 30;
  /** Constant <code>COVERAGE=40</code> */
  public static final byte COVERAGE = 40;
  /** Constant <code>CONTIGPOSITION=50</code> */
  public static final byte CONTIGPOSITION = 50;
  /** Constant <code>CONSEQUENCE=60</code> */
  public static final byte CONSEQUENCE = 60;
  
  protected HashMap<String, String> tags = new HashMap<String,String>();
  protected String id = null;
  protected String keyvalues = null;
  
  
  
  /**
   * <p>Getter for the field <code>keyvalues</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getKeyvalues() {
    return keyvalues;
  }

  /**
   * <p>Setter for the field <code>keyvalues</code>.</p>
   *
   * @param keyvalues a {@link java.lang.String} object.
   */
  public void setKeyvalues(String keyvalues) {
    this.keyvalues = keyvalues;
  }

  /**
   * <p>Getter for the field <code>tags</code>.</p>
   *
   * @return a {@link java.util.HashMap} object.
   */
  public HashMap<String, String> getTags() {
    return(tags);
  }
  
  /** {@inheritDoc} */
  public void setTags(HashMap<String, String> tags) {
    this.tags = tags;
  }
  
  /**
   * <p>Getter for the field <code>id</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getId() {
    return(id);
  }
  
  /** {@inheritDoc} */
  public void setId(String id) {
    this.id = id;
  }
  
}
