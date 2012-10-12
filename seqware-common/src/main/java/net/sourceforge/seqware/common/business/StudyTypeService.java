package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.StudyTypeDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.StudyType;

/**
 * <p>StudyTypeService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface StudyTypeService {
  /** Constant <code>NAME="StudyTypeService"</code> */
  public static final String NAME = "StudyTypeService";

  /**
   * <p>setStudyTypeDAO.</p>
   *
   * @param studyTypeDAO a {@link net.sourceforge.seqware.common.dao.StudyTypeDAO} object.
   */
  public void setStudyTypeDAO(StudyTypeDAO studyTypeDAO);

  /**
   * <p>insert.</p>
   *
   * @param studyType a {@link net.sourceforge.seqware.common.model.StudyType} object.
   */
  public void insert(StudyType studyType);

  /**
   * <p>update.</p>
   *
   * @param studyType a {@link net.sourceforge.seqware.common.model.StudyType} object.
   */
  public void update(StudyType studyType);

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<StudyType> list();
  
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
   * @param expID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.StudyType} object.
   */
  public StudyType findByID(Integer expID);

  /**
   * <p>updateDetached.</p>
   *
   * @param studyType a {@link net.sourceforge.seqware.common.model.StudyType} object.
   * @return a {@link net.sourceforge.seqware.common.model.StudyType} object.
   */
  StudyType updateDetached(StudyType studyType);
}

// ex:sw=4:ts=4:
