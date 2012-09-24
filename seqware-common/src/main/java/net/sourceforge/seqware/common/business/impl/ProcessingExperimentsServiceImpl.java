package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ProcessingExperimentsService;
import net.sourceforge.seqware.common.dao.ProcessingExperimentsDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingExperiments;

public class ProcessingExperimentsServiceImpl implements ProcessingExperimentsService {

  private ProcessingExperimentsDAO dao;

  @Override
  public void setProcessingExperimentsDAO(ProcessingExperimentsDAO dao) {
    this.dao = dao;
  }

  @Override
  public void delete(ProcessingExperiments processingExperiments) {
    dao.delete(processingExperiments);
  }

  @Override
  public void update(ProcessingExperiments processingExperiments) {
    dao.update(processingExperiments);
  }

  @Override
  public void insert(ProcessingExperiments processingExperiments) {
    dao.insert(processingExperiments);
  }

  @Override
  public ProcessingExperiments findByProcessingExperiment(Processing processing, Experiment experiment) {
    return dao.findByProcessingExperiment(processing, experiment);
  }

  @Override
  public ProcessingExperiments updateDetached(ProcessingExperiments processingExperiments) {
    return dao.updateDetached(processingExperiments);
  }

    @Override
    public List<ProcessingExperiments> list() {
        return dao.list();
    }
}
