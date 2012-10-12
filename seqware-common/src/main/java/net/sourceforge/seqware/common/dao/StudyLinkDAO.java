package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyLink;

/**
 * <p>StudyLinkDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface StudyLinkDAO {

  /**
   * <p>insert.</p>
   *
   * @param studyLink a {@link net.sourceforge.seqware.common.model.StudyLink} object.
   */
  public abstract void insert(StudyLink studyLink);

  /**
   * <p>update.</p>
   *
   * @param studyLink a {@link net.sourceforge.seqware.common.model.StudyLink} object.
   */
  public abstract void update(StudyLink studyLink);

  /**
   * <p>delete.</p>
   *
   * @param studyLink a {@link net.sourceforge.seqware.common.model.StudyLink} object.
   */
  public abstract void delete(StudyLink studyLink);

  /**
   * <p>findAll.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<StudyLink> findAll(Study study);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<StudyLink> list();

}
