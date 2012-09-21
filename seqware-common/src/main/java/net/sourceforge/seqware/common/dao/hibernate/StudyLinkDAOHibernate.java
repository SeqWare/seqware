package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.dao.StudyLinkDAO;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyLink;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class StudyLinkDAOHibernate extends HibernateDaoSupport implements StudyLinkDAO {

  @Override
  public void insert(StudyLink studyLink) {
    this.getHibernateTemplate().save(studyLink);
  }

  @Override
  public void update(StudyLink studyLink) {
    this.getHibernateTemplate().update(studyLink);
  }

  @Override
  public void delete(StudyLink studyLink) {
    this.getHibernateTemplate().delete(studyLink);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<StudyLink> findAll(Study study) {
    String query = "from StudyLink as sa where sa.study.studyId = ?";
    Object[] parameters = { study.getStudyId() };
    return this.getHibernateTemplate().find(query, parameters);
  }

    @Override
    public List<StudyLink> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
