package net.sourceforge.seqware.queryengine.backend.model;

import java.util.HashMap;

/**
 * <p>ModelInterface interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ModelInterface {
  /**
   * <p>getTags.</p>
   *
   * @return a {@link java.util.HashMap} object.
   */
  public HashMap<String, String> getTags();
  /**
   * <p>setTags.</p>
   *
   * @param tags a {@link java.util.HashMap} object.
   */
  public void setTags(HashMap<String, String> tags);
  /**
   * <p>getId.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getId();
  /**
   * <p>setId.</p>
   *
   * @param id a {@link java.lang.String} object.
   */
  public void setId(String id);
}
