package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingSequencerRuns;
import net.sourceforge.seqware.common.model.SequencerRun;

public interface ProcessingSequencerRunsDAO {

  @SuppressWarnings("rawtypes")
  public abstract ProcessingSequencerRuns findByProcessingSequencerRun(Processing processing, SequencerRun sequencerRun);

  public abstract void delete(ProcessingSequencerRuns processingSequencerRuns);

  public abstract void update(ProcessingSequencerRuns processingSequencerRuns);

  public abstract void insert(ProcessingSequencerRuns processingSequencerRuns);

  public abstract ProcessingSequencerRuns updateDetached(ProcessingSequencerRuns processingSequencerRuns);
  
  public List<ProcessingSequencerRuns> list();

}