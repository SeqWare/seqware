package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingExperiments;

public interface ProcessingExperimentsDAO {

  public abstract ProcessingExperiments findByProcessingExperiment(Processing processing, Experiment experiment);

  public abstract void delete(ProcessingExperiments processingExperiments);

  public abstract void update(ProcessingExperiments processingExperiments);

  public abstract void insert(ProcessingExperiments processingExperiments);

  public abstract ProcessingExperiments updateDetached(ProcessingExperiments processingExperiments);
  
  public List<ProcessingExperiments> list();

}