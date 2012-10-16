package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.StudyType;

/**
 * <p>StudyTypeDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface StudyTypeDAO {
  /**
   * <p>insert.</p>
   *
   * @param sequencerRun a {@link net.sourceforge.seqware.common.model.StudyType} object.
   */
  public void insert(StudyType sequencerRun);

  /**
   * <p>update.</p>
   *
   * @param sequencerRun a {@link net.sourceforge.seqware.common.model.StudyType} object.
   */
  public void update(StudyType sequencerRun);

  /**
   * <p>list.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.util.List} object.
   */
  public List<StudyType> list(Registration registration);

  /**
   * <p>findByName.</p>
   *
   * @param name a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.model.StudyType} object.
   */
  public StudyType findByName(String name);

  /**
   * <p>findByID.</p>
   *
   * @param studyTypeID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.StudyType} object.
   */
  public StudyType findByID(Integer studyTypeID);

  /**
   * <p>updateDetached.</p>
   *
   * @param studyType a {@link net.sourceforge.seqware.common.model.StudyType} object.
   * @return a {@link net.sourceforge.seqware.common.model.StudyType} object.
   */
  public StudyType updateDetached(StudyType studyType);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<StudyType> list();
}
