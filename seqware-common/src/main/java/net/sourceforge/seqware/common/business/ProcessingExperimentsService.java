package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingExperimentsDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingExperiments;

public interface ProcessingExperimentsService {

  public abstract void setProcessingExperimentsDAO(ProcessingExperimentsDAO dao);

  public abstract void delete(ProcessingExperiments processingExperiments);

  public abstract void update(ProcessingExperiments processingExperiments);

  public abstract void insert(ProcessingExperiments processingExperiments);

  public abstract ProcessingExperiments findByProcessingExperiment(Processing processing, Experiment experiment);

  public abstract ProcessingExperiments updateDetached(ProcessingExperiments processingExperiments);
  
  public List<ProcessingExperiments> list();

}