package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ProcessingSequencerRunsService;
import net.sourceforge.seqware.common.dao.ProcessingSequencerRunsDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingSequencerRuns;
import net.sourceforge.seqware.common.model.SequencerRun;

public class ProcessingSequencerRunsServiceImpl implements ProcessingSequencerRunsService {

  private ProcessingSequencerRunsDAO dao;

  @Override
  public void setProcessingSequencerRunsDAO(ProcessingSequencerRunsDAO dao) {
    this.dao = dao;
  }

  @Override
  public ProcessingSequencerRuns findByProcessingSequencerRun(Processing processing, SequencerRun sequencerRun) {
    return dao.findByProcessingSequencerRun(processing, sequencerRun);
  }

  @Override
  public void delete(ProcessingSequencerRuns processingSequencerRuns) {
    dao.delete(processingSequencerRuns);
  }

  @Override
  public void update(ProcessingSequencerRuns processingSequencerRuns) {
    dao.update(processingSequencerRuns);
  }

  @Override
  public void insert(ProcessingSequencerRuns processingSequencerRuns) {
    dao.insert(processingSequencerRuns);
  }

  @Override
  public ProcessingSequencerRuns updateDetached(ProcessingSequencerRuns processingSequencerRuns) {
    return dao.updateDetached(processingSequencerRuns);
  }

    @Override
    public List<ProcessingSequencerRuns> list() {
        return dao.list();
    }

}
