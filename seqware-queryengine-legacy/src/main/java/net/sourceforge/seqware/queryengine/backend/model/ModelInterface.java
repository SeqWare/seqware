package net.sourceforge.seqware.queryengine.backend.model;

import java.util.HashMap;

public interface ModelInterface {
  public HashMap<String, String> getTags();
  public void setTags(HashMap<String, String> tags);
  public String getId();
  public void setId(String id);
}
