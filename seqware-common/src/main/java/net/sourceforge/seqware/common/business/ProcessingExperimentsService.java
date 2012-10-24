package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingExperimentsDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingExperiments;

/**
 * <p>ProcessingExperimentsService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ProcessingExperimentsService {

  /**
   * <p>setProcessingExperimentsDAO.</p>
   *
   * @param dao a {@link net.sourceforge.seqware.common.dao.ProcessingExperimentsDAO} object.
   */
  public abstract void setProcessingExperimentsDAO(ProcessingExperimentsDAO dao);

  /**
   * <p>delete.</p>
   *
   * @param processingExperiments a {@link net.sourceforge.seqware.common.model.ProcessingExperiments} object.
   */
  public abstract void delete(ProcessingExperiments processingExperiments);

  /**
   * <p>update.</p>
   *
   * @param processingExperiments a {@link net.sourceforge.seqware.common.model.ProcessingExperiments} object.
   */
  public abstract void update(ProcessingExperiments processingExperiments);

  /**
   * <p>insert.</p>
   *
   * @param processingExperiments a {@link net.sourceforge.seqware.common.model.ProcessingExperiments} object.
   */
  public abstract void insert(ProcessingExperiments processingExperiments);

  /**
   * <p>findByProcessingExperiment.</p>
   *
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @param experiment a {@link net.sourceforge.seqware.common.model.Experiment} object.
   * @return a {@link net.sourceforge.seqware.common.model.ProcessingExperiments} object.
   */
  public abstract ProcessingExperiments findByProcessingExperiment(Processing processing, Experiment experiment);

  /**
   * <p>updateDetached.</p>
   *
   * @param processingExperiments a {@link net.sourceforge.seqware.common.model.ProcessingExperiments} object.
   * @return a {@link net.sourceforge.seqware.common.model.ProcessingExperiments} object.
   */
  public abstract ProcessingExperiments updateDetached(ProcessingExperiments processingExperiments);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ProcessingExperiments> list();

}
