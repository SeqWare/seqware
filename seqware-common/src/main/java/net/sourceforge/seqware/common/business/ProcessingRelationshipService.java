package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingRelationshipDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingRelationship;

/**
 * <p>ProcessingRelationshipService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ProcessingRelationshipService {

  /**
   * <p>setProcessingRelationshipDAO.</p>
   *
   * @param dao a {@link net.sourceforge.seqware.common.dao.ProcessingRelationshipDAO} object.
   */
  public abstract void setProcessingRelationshipDAO(ProcessingRelationshipDAO dao);

  /**
   * <p>findByProcessings.</p>
   *
   * @param processingParent a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @param processingChild a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @return a {@link net.sourceforge.seqware.common.model.ProcessingRelationship} object.
   */
  public abstract ProcessingRelationship findByProcessings(Processing processingParent, Processing processingChild);

  /**
   * <p>delete.</p>
   *
   * @param processingRelationship a {@link net.sourceforge.seqware.common.model.ProcessingRelationship} object.
   */
  public abstract void delete(ProcessingRelationship processingRelationship);

  /**
   * <p>update.</p>
   *
   * @param processingRelationship a {@link net.sourceforge.seqware.common.model.ProcessingRelationship} object.
   */
  public abstract void update(ProcessingRelationship processingRelationship);

  /**
   * <p>insert.</p>
   *
   * @param processingRelationship a {@link net.sourceforge.seqware.common.model.ProcessingRelationship} object.
   */
  public abstract void insert(ProcessingRelationship processingRelationship);

  /**
   * <p>updateDetached.</p>
   *
   * @param processingRelationship a {@link net.sourceforge.seqware.common.model.ProcessingRelationship} object.
   * @return a {@link net.sourceforge.seqware.common.model.ProcessingRelationship} object.
   */
  public abstract ProcessingRelationship updateDetached(ProcessingRelationship processingRelationship);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ProcessingRelationship> list();
  
  public List<ProcessingRelationship> listByParentProcessingId(int parentId);

}
