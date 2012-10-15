package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.dao.StudyLinkDAO;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyLink;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>StudyLinkDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StudyLinkDAOHibernate extends HibernateDaoSupport implements StudyLinkDAO {

  /** {@inheritDoc} */
  @Override
  public void insert(StudyLink studyLink) {
    this.getHibernateTemplate().save(studyLink);
  }

  /** {@inheritDoc} */
  @Override
  public void update(StudyLink studyLink) {
    this.getHibernateTemplate().update(studyLink);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(StudyLink studyLink) {
    this.getHibernateTemplate().delete(studyLink);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public List<StudyLink> findAll(Study study) {
    String query = "from StudyLink as sa where sa.study.studyId = ?";
    Object[] parameters = { study.getStudyId() };
    return this.getHibernateTemplate().find(query, parameters);
  }

    /** {@inheritDoc} */
    @Override
    public List<StudyLink> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
