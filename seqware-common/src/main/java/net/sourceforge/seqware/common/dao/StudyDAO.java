package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowRunStatus;
import net.sourceforge.seqware.common.module.ReturnValue;

/**
 * <p>StudyDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface StudyDAO {

  /**
   * <p>insert.</p>
   *
   * @param sequencerRun a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link java.lang.Integer} object.
   */
  public Integer insert(Study sequencerRun);

  /**
   * <p>insert.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param sequencerRun a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link java.lang.Integer} object.
   */
  public Integer insert(Registration registration, Study sequencerRun);

  /**
   * <p>update.</p>
   *
   * @param sequencerRun a {@link net.sourceforge.seqware.common.model.Study} object.
   */
  public void update(Study sequencerRun);

  /**
   * <p>update.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   */
  public void update(Registration registration, Study study);

  /**
   * <p>delete.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   */
  public void delete(Study study);

  /**
   * <p>merge.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   */
  void merge(Study study);

  /**
   * <p>list.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param isAcs a {@link java.lang.Boolean} object.
   * @return a {@link java.util.List} object.
   */
  public List<Study> list(Registration registration, Boolean isAcs);

  /**
   * <p>listMyShared.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param isAcs a {@link java.lang.Boolean} object.
   * @return a {@link java.util.List} object.
   */
  public List<Study> listMyShared(Registration registration, Boolean isAcs);

  /**
   * <p>listSharedWithMe.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param isAcs a {@link java.lang.Boolean} object.
   * @return a {@link java.util.List} object.
   */
  public List<Study> listSharedWithMe(Registration registration, Boolean isAcs);

  /**
   * <p>findByTitle.</p>
   *
   * @param title a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.model.Study} object.
   */
  public Study findByTitle(String title);

  /**
   * <p>findByID.</p>
   *
   * @param studyID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.Study} object.
   */
  public Study findByID(Integer studyID);

  /**
   * <p>getFiles.</p>
   *
   * @param studyId a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public List<File> getFiles(Integer studyId);

  /**
   * <p>isHasFile.</p>
   *
   * @param studyId a {@link java.lang.Integer} object.
   * @return a boolean.
   */
  public boolean isHasFile(Integer studyId);

  /**
   * <p>getFiles.</p>
   *
   * @param studyId a {@link java.lang.Integer} object.
   * @param metaType a {@link java.lang.String} object.
   * @return a {@link java.util.List} object.
   */
  public List<File> getFiles(Integer studyId, String metaType);

  /**
   * <p>findFiles.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public List<ReturnValue> findFiles(Integer swAccession);

  /**
   * <p>isHasFile.</p>
   *
   * @param studyId a {@link java.lang.Integer} object.
   * @param metaType a {@link java.lang.String} object.
   * @return a boolean.
   */
  public boolean isHasFile(Integer studyId, String metaType);

  /**
   * <p>listStudyHasFile.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param metaType a {@link java.lang.String} object.
   * @param iaAsc a {@link java.lang.Boolean} object.
   * @return a {@link java.util.List} object.
   */
  public List<Study> listStudyHasFile(Registration registration, String metaType, Boolean iaAsc);

  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.Study} object.
   */
  public Study findBySWAccession(Integer swAccession);

  /**
   * <p>findByCriteria.</p>
   *
   * @param criteria a {@link java.lang.String} object.
   * @param isCaseSens a boolean.
   * @return a {@link java.util.List} object.
   */
  public List<Study> findByCriteria(String criteria, boolean isCaseSens);
  
  /**
   * <p>findByCriteria.</p>
   *
   * @param criteria a {@link java.lang.String} object.
   * @return a {@link java.util.List} object.
   */
  public List<Study> findByCriteria(String criteria);

  /**
   * <p>updateDetached.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link net.sourceforge.seqware.common.model.Study} object.
   */
  public Study updateDetached(Study study);

  /**
   * <p>updateDetached.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link net.sourceforge.seqware.common.model.Study} object.
   */
  public Study updateDetached(Registration registration, Study study);

  /**
   * <p>findByOwnerID.</p>
   *
   * @param registrationId a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public List<Study> findByOwnerID(Integer registrationId);

  /**
   * <p>updateOwners.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   */
  public void updateOwners(Integer swAccession);

  //
  // List search();
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<Study> list();

  /**
   * <p>Count the number of workflow runs with the given status, under the given study</p>
   *
   * @param study the study
   * @param status the status of the workflow runs to count
   * @return the count
   */
  public int getStatusCount(Study study, WorkflowRunStatus status);

}
