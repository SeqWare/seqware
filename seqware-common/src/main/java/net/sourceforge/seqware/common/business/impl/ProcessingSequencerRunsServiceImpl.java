package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ProcessingSequencerRunsService;
import net.sourceforge.seqware.common.dao.ProcessingSequencerRunsDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingSequencerRuns;
import net.sourceforge.seqware.common.model.SequencerRun;

/**
 * <p>ProcessingSequencerRunsServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingSequencerRunsServiceImpl implements ProcessingSequencerRunsService {

  private ProcessingSequencerRunsDAO dao;

  /** {@inheritDoc} */
  @Override
  public void setProcessingSequencerRunsDAO(ProcessingSequencerRunsDAO dao) {
    this.dao = dao;
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingSequencerRuns findByProcessingSequencerRun(Processing processing, SequencerRun sequencerRun) {
    return dao.findByProcessingSequencerRun(processing, sequencerRun);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ProcessingSequencerRuns processingSequencerRuns) {
    dao.delete(processingSequencerRuns);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ProcessingSequencerRuns processingSequencerRuns) {
    dao.update(processingSequencerRuns);
  }

  /** {@inheritDoc} */
  @Override
  public void insert(ProcessingSequencerRuns processingSequencerRuns) {
    dao.insert(processingSequencerRuns);
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingSequencerRuns updateDetached(ProcessingSequencerRuns processingSequencerRuns) {
    return dao.updateDetached(processingSequencerRuns);
  }

    /** {@inheritDoc} */
    @Override
    public List<ProcessingSequencerRuns> list() {
        return dao.list();
    }

}
