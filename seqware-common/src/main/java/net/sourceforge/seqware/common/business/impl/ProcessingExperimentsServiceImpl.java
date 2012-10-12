package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ProcessingExperimentsService;
import net.sourceforge.seqware.common.dao.ProcessingExperimentsDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingExperiments;

/**
 * <p>ProcessingExperimentsServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingExperimentsServiceImpl implements ProcessingExperimentsService {

  private ProcessingExperimentsDAO dao;

  /** {@inheritDoc} */
  @Override
  public void setProcessingExperimentsDAO(ProcessingExperimentsDAO dao) {
    this.dao = dao;
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ProcessingExperiments processingExperiments) {
    dao.delete(processingExperiments);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ProcessingExperiments processingExperiments) {
    dao.update(processingExperiments);
  }

  /** {@inheritDoc} */
  @Override
  public void insert(ProcessingExperiments processingExperiments) {
    dao.insert(processingExperiments);
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingExperiments findByProcessingExperiment(Processing processing, Experiment experiment) {
    return dao.findByProcessingExperiment(processing, experiment);
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingExperiments updateDetached(ProcessingExperiments processingExperiments) {
    return dao.updateDetached(processingExperiments);
  }

    /** {@inheritDoc} */
    @Override
    public List<ProcessingExperiments> list() {
        return dao.list();
    }
}
