package net.sourceforge.seqware.common.model;

/**
 * <p>Attribute interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface Attribute {

  /**
   * <p>getTag.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getTag();

  /**
   * <p>setTag.</p>
   *
   * @param tag a {@link java.lang.String} object.
   */
  public void setTag(String tag);

  /**
   * <p>getValue.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getValue();

  /**
   * <p>setValue.</p>
   *
   * @param value a {@link java.lang.String} object.
   */
  public void setValue(String value);

  /**
   * <p>getUnit.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getUnit();

  /**
   * <p>setUnit.</p>
   *
   * @param unit a {@link java.lang.String} object.
   */
  public void setUnit(String unit);

}
