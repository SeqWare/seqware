package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ProcessingIUSService;
import net.sourceforge.seqware.common.dao.ProcessingIUSDAO;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingIus;

/**
 * <p>ProcessingIUSServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingIUSServiceImpl implements ProcessingIUSService {

  private ProcessingIUSDAO dao;

  /** {@inheritDoc} */
  @Override
  public void setProcessingIUSDAO(ProcessingIUSDAO dao) {
    this.dao = dao;
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingIus findByProcessingIUS(Processing processing, IUS ius) {
    return dao.findByProcessingIUS(processing, ius);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ProcessingIus processingIus) {
    dao.delete(processingIus);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ProcessingIus processingIus) {
    dao.update(processingIus);
  }

  /** {@inheritDoc} */
  @Override
  public void insert(ProcessingIus processingIus) {
    dao.insert(processingIus);
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingIus updateDetached(ProcessingIus processingIus) {
    return dao.updateDetached(processingIus);
  }

    /** {@inheritDoc} */
    @Override
    public List<ProcessingIus> list() {
        return dao.list();
    }
}
