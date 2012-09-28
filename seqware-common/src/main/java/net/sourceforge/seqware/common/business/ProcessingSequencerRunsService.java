package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingSequencerRunsDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingSequencerRuns;
import net.sourceforge.seqware.common.model.SequencerRun;

public interface ProcessingSequencerRunsService {

  public abstract void setProcessingSequencerRunsDAO(ProcessingSequencerRunsDAO dao);

  public abstract ProcessingSequencerRuns findByProcessingSequencerRun(Processing processing, SequencerRun sequencerRun);

  public abstract void delete(ProcessingSequencerRuns processingSequencerRuns);

  public abstract void update(ProcessingSequencerRuns processingSequencerRuns);

  public abstract void insert(ProcessingSequencerRuns processingSequencerRuns);

  public abstract ProcessingSequencerRuns updateDetached(ProcessingSequencerRuns processingSequencerRuns);
  
  public List<ProcessingSequencerRuns> list();

}