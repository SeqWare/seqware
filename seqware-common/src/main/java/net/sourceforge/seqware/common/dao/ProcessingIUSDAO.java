package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingIus;

/**
 * <p>ProcessingIUSDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ProcessingIUSDAO {

  /**
   * <p>findByProcessingIUS.</p>
   *
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
   * @return a {@link net.sourceforge.seqware.common.model.ProcessingIus} object.
   */
  public abstract ProcessingIus findByProcessingIUS(Processing processing, IUS ius);

  /**
   * <p>delete.</p>
   *
   * @param processingIus a {@link net.sourceforge.seqware.common.model.ProcessingIus} object.
   */
  public abstract void delete(ProcessingIus processingIus);

  /**
   * <p>update.</p>
   *
   * @param processingIus a {@link net.sourceforge.seqware.common.model.ProcessingIus} object.
   */
  public abstract void update(ProcessingIus processingIus);

  /**
   * <p>insert.</p>
   *
   * @param processingIus a {@link net.sourceforge.seqware.common.model.ProcessingIus} object.
   */
  public abstract void insert(ProcessingIus processingIus);

  /**
   * <p>updateDetached.</p>
   *
   * @param processingIus a {@link net.sourceforge.seqware.common.model.ProcessingIus} object.
   * @return a {@link net.sourceforge.seqware.common.model.ProcessingIus} object.
   */
  public abstract ProcessingIus updateDetached(ProcessingIus processingIus);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ProcessingIus> list();

}
