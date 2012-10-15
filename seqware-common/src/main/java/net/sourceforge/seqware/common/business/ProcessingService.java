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

/**
 * <p>ProcessingService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ProcessingService {

  /** Constant <code>NAME="processingService"</code> */
  public static final String NAME = "processingService";

  /**
   * <p>setProcessingDAO.</p>
   *
   * @param processingDAO a {@link net.sourceforge.seqware.common.dao.ProcessingDAO} object.
   */
  public void setProcessingDAO(ProcessingDAO processingDAO);

  // public void insert(Processing processing);
  /**
   * <p>insert.</p>
   *
   * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   */
  public void insert(SequencerRun sequencerRun, Processing processing);

  /**
   * <p>insert.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   */
  public void insert(Registration registration, SequencerRun sequencerRun, Processing processing);

  /**
   * Inserts a new Processing and returns its sw_accession number.
   *
   * @param processing
   *          Processing to be inserted.
   * @return The SeqWare Accession number for the newly inserted Processing.
   */
  public Integer insert(Processing processing);

  /**
   * <p>insert.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @return a {@link java.lang.Integer} object.
   */
  public Integer insert(Registration registration, Processing processing);

  /**
   * <p>update.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   */
  public void update(Registration registration, Processing processing);

  /**
   * <p>update.</p>
   *
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   */
  public void update(Processing processing);

  /**
   * <p>delete.</p>
   *
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @param deleteRealFiles a {@link java.lang.String} object.
   */
  public void delete(Processing processing, String deleteRealFiles);

  // public List<Processing> list();
  // public Processing findByExperiment(Integer expID);
  /**
   * <p>findByID.</p>
   *
   * @param processingID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
   */
  public Processing findByID(Integer processingID);

  /**
   * <p>findByIDOnlyWithRunningWR.</p>
   *
   * @param processingID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
   */
  public Processing findByIDOnlyWithRunningWR(Integer processingID);

  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
   */
  public Processing findBySWAccession(Integer swAccession);

  /**
   * <p>getFiles.</p>
   *
   * @param processingId a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public List<File> getFiles(Integer processingId);

  /**
   * <p>getFiles.</p>
   *
   * @param processingId a {@link java.lang.Integer} object.
   * @param metaType a {@link java.lang.String} object.
   * @return a {@link java.util.List} object.
   */
  public List<File> getFiles(Integer processingId, String metaType);

  /**
   * <p>isHasFile.</p>
   *
   * @param processingId a {@link java.lang.Integer} object.
   * @return a boolean.
   */
  public boolean isHasFile(Integer processingId);

  /**
   * <p>setWithHasFile.</p>
   *
   * @param list a {@link java.util.Set} object.
   * @return a {@link java.util.Set} object.
   */
  public Set<Processing> setWithHasFile(Set<Processing> list);

  /**
   * <p>setWithHasFile.</p>
   *
   * @param list a {@link java.util.Set} object.
   * @param metaType a {@link java.lang.String} object.
   * @return a {@link java.util.Set} object.
   */
  public Set<Processing> setWithHasFile(Set<Processing> list, String metaType);

  /**
   * <p>updateDetached.</p>
   *
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
   */
  public Processing updateDetached(Processing processing);

  /**
   * <p>updateDetached.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
   */
  public Processing updateDetached(Registration registration, Processing processing);

  /**
   * <p>findByOwnerID.</p>
   *
   * @param registrationId a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public List<Processing> findByOwnerID(Integer registrationId);

  /**
   * <p>findByCriteria.</p>
   *
   * @param criteria a {@link java.lang.String} object.
   * @param isCaseSens a boolean.
   * @return a {@link java.util.List} object.
   */
  public List<Processing> findByCriteria(String criteria, boolean isCaseSens);

  /**
   * <p>findFor.</p>
   *
   * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @return a {@link java.util.Set} object.
   */
  public Set<Processing> findFor(Sample sample, WorkflowRun workflowRun);

  /**
   * <p>findFor.</p>
   *
   * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
   * @return a {@link java.util.Set} object.
   */
  public Set<Processing> findFor(Sample sample);

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<Processing> list();

}

// ex:sw=4:ts=4:
