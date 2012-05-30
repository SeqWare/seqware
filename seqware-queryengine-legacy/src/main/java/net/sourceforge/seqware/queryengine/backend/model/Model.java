package net.sourceforge.seqware.queryengine.backend.model;

import java.util.HashMap;

public class Model implements ModelInterface {
  
  // These are byte IDs used to detect the object type from a byte stream.
  // They encode both the type and version so the correct model object and
  // tuplebinder can be used
  public static final byte VARIENT = 0;
  public static final byte TAG = 10;
  public static final byte STRINGID = 20;
  public static final byte FEATURE = 30;
  public static final byte COVERAGE = 40;
  public static final byte CONTIGPOSITION = 50;
  public static final byte CONSEQUENCE = 60;
  
  protected HashMap<String, String> tags = new HashMap<String,String>();
  protected String id = null;
  protected String keyvalues = null;
  
  
  
  public String getKeyvalues() {
    return keyvalues;
  }

  public void setKeyvalues(String keyvalues) {
    this.keyvalues = keyvalues;
  }

  public HashMap<String, String> getTags() {
    return(tags);
  }
  
  public void setTags(HashMap<String, String> tags) {
    this.tags = tags;
  }
  
  public String getId() {
    return(id);
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
}