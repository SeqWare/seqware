/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.iterators.processors;

/**
 * @author boconnor
 *
 * A simple interface that defines the methods an iterator processor needs to support.
 *
 */
public interface ProcessorInterface {

  public Object process (Object obj);
  
  public String report (Object obj);
  
}
