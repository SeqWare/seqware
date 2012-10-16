/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.iterators.processors;

/**
 * <p>ProcessorInterface interface.</p>
 *
 * @author boconnor
 *
 * A simple interface that defines the methods an iterator processor needs to support.
 * @version $Id: $Id
 */
public interface ProcessorInterface {

  /**
   * <p>process.</p>
   *
   * @param obj a {@link java.lang.Object} object.
   * @return a {@link java.lang.Object} object.
   */
  public Object process (Object obj);
  
  /**
   * <p>report.</p>
   *
   * @param obj a {@link java.lang.Object} object.
   * @return a {@link java.lang.String} object.
   */
  public String report (Object obj);
  
}
