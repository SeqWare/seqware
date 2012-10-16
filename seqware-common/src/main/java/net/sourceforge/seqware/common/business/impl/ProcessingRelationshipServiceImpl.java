package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ProcessingRelationshipService;
import net.sourceforge.seqware.common.dao.ProcessingRelationshipDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingRelationship;

/**
 * <p>ProcessingRelationshipServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingRelationshipServiceImpl implements ProcessingRelationshipService {

  private ProcessingRelationshipDAO dao;

  /** {@inheritDoc} */
  @Override
  public void setProcessingRelationshipDAO(ProcessingRelationshipDAO dao) {
    this.dao = dao;
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingRelationship findByProcessings(Processing processingParent, Processing processingChild) {
    return dao.findByProcessings(processingParent, processingChild);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ProcessingRelationship processingRelationship) {
    dao.delete(processingRelationship);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ProcessingRelationship processingRelationship) {
    dao.update(processingRelationship);
  }

  /** {@inheritDoc} */
  @Override
  public void insert(ProcessingRelationship processingRelationship) {
    dao.insert(processingRelationship);
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingRelationship updateDetached(ProcessingRelationship processingRelationship) {
    return dao.updateDetached(processingRelationship);
  }

    /** {@inheritDoc} */
    @Override
    public List<ProcessingRelationship> list() {
        return dao.list();
    }

}
