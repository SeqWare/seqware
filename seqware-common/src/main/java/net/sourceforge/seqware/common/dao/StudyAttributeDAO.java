package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyAttribute;

/**
 * <p>StudyAttributeDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface StudyAttributeDAO {

  /**
   * <p>insert.</p>
   *
   * @param studyAttribute a {@link net.sourceforge.seqware.common.model.StudyAttribute} object.
   */
  public abstract void insert(StudyAttribute studyAttribute);

  /**
   * <p>update.</p>
   *
   * @param studyAttribute a {@link net.sourceforge.seqware.common.model.StudyAttribute} object.
   */
  public abstract void update(StudyAttribute studyAttribute);

  /**
   * <p>delete.</p>
   *
   * @param studyAttribute a {@link net.sourceforge.seqware.common.model.StudyAttribute} object.
   */
  public abstract void delete(StudyAttribute studyAttribute);

  /**
   * <p>findAll.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<StudyAttribute> findAll(Study study);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<StudyAttribute> list();

}
