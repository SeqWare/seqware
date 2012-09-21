package net.sourceforge.seqware.common.business;

import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.dao.ProcessingDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.WorkflowRun;

public interface ProcessingService {

  public static final String NAME = "processingService";

  public void setProcessingDAO(ProcessingDAO processingDAO);

  // public void insert(Processing processing);
  public void insert(SequencerRun sequencerRun, Processing processing);

  public void insert(Registration registration, SequencerRun sequencerRun, Processing processing);

  /**
   * Inserts a new Processing and returns its sw_accession number.
   * 
   * @param processing
   *          Processing to be inserted.
   * @return The SeqWare Accession number for the newly inserted Processing.
   */
  public Integer insert(Processing processing);

  public Integer insert(Registration registration, Processing processing);

  public void update(Registration registration, Processing processing);

  public void update(Processing processing);

  public void delete(Processing processing, String deleteRealFiles);

  // public List<Processing> list();
  // public Processing findByExperiment(Integer expID);
  public Processing findByID(Integer processingID);

  public Processing findByIDOnlyWithRunningWR(Integer processingID);

  public Processing findBySWAccession(Integer swAccession);

  public List<File> getFiles(Integer processingId);

  public List<File> getFiles(Integer processingId, String metaType);

  public boolean isHasFile(Integer processingId);

  public Set<Processing> setWithHasFile(Set<Processing> list);

  public Set<Processing> setWithHasFile(Set<Processing> list, String metaType);

  public Processing updateDetached(Processing processing);

  public Processing updateDetached(Registration registration, Processing processing);

  public List<Processing> findByOwnerID(Integer registrationId);

  public List<Processing> findByCriteria(String criteria, boolean isCaseSens);

  public Set<Processing> findFor(Sample sample, WorkflowRun workflowRun);

  public Set<Processing> findFor(Sample sample);

  public List<Processing> list();

}

// ex:sw=4:ts=4:
