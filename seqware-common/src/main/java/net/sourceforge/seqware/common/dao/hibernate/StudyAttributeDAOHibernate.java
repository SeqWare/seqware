package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.dao.StudyAttributeDAO;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyAttribute;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>StudyAttributeDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StudyAttributeDAOHibernate extends HibernateDaoSupport implements StudyAttributeDAO {

  /** {@inheritDoc} */
  @Override
  public void insert(StudyAttribute studyAttribute) {
    this.getHibernateTemplate().save(studyAttribute);
  }

  /** {@inheritDoc} */
  @Override
  public void update(StudyAttribute studyAttribute) {
    this.getHibernateTemplate().update(studyAttribute);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(StudyAttribute studyAttribute) {
    this.getHibernateTemplate().delete(studyAttribute);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public List<StudyAttribute> findAll(Study study) {
    String query = "from StudyAttribute as sa where sa.study.studyId = ?";
    Object[] parameters = { study.getStudyId() };
    return this.getHibernateTemplate().find(query, parameters);
  }

    /** {@inheritDoc} */
    @Override
    public List<StudyAttribute> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
