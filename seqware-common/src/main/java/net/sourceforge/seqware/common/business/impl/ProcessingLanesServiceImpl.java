package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ProcessingLanesService;
import net.sourceforge.seqware.common.dao.ProcessingLanesDAO;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingLanes;

/**
 * <p>ProcessingLanesServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingLanesServiceImpl implements ProcessingLanesService {

  private ProcessingLanesDAO dao;

  /** {@inheritDoc} */
  @Override
  public void setProcessingLanesDAO(ProcessingLanesDAO dao) {
    this.dao = dao;
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingLanes findByProcessingLane(Processing processing, Lane lane) {
    return dao.findByProcessingLane(processing, lane);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ProcessingLanes processingLanes) {
    dao.delete(processingLanes);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ProcessingLanes processingLanes) {
    dao.update(processingLanes);
  }

  /** {@inheritDoc} */
  @Override
  public void insert(ProcessingLanes processingLanes) {
    dao.insert(processingLanes);
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingLanes updateDetached(ProcessingLanes processingLanes) {
    return dao.updateDetached(processingLanes);
  }

    /** {@inheritDoc} */
    @Override
    public List<ProcessingLanes> list() {
        return dao.list();
    }
}
