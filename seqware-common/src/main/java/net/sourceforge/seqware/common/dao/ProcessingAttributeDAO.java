package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingAttribute;

/**
 * <p>ProcessingAttributeDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ProcessingAttributeDAO {

  /**
   * <p>insert.</p>
   *
   * @param processingAttribute a {@link net.sourceforge.seqware.common.model.ProcessingAttribute} object.
   */
  public abstract void insert(ProcessingAttribute processingAttribute);

  /**
   * <p>update.</p>
   *
   * @param processingAttribute a {@link net.sourceforge.seqware.common.model.ProcessingAttribute} object.
   */
  public abstract void update(ProcessingAttribute processingAttribute);

  /**
   * <p>delete.</p>
   *
   * @param processingAttribute a {@link net.sourceforge.seqware.common.model.ProcessingAttribute} object.
   */
  public abstract void delete(ProcessingAttribute processingAttribute);

  /**
   * <p>findAll.</p>
   *
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<ProcessingAttribute> findAll(Processing processing);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ProcessingAttribute> list();

}
