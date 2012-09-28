package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.dao.ExperimentLinkDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentLink;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ExperimentLinkDAOHibernate extends HibernateDaoSupport implements ExperimentLinkDAO {

  @Override
  public void insert(ExperimentLink experimentLink) {
    this.getHibernateTemplate().save(experimentLink);

  }

  @Override
  public void update(ExperimentLink experimentLink) {
    this.getHibernateTemplate().update(experimentLink);
  }

  @Override
  public void delete(ExperimentLink experimentLink) {
    this.getHibernateTemplate().delete(experimentLink);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ExperimentLink> findAll(Experiment experiment) {
    String query = "from ExperimentLink as ea where ea.experiment.experimentId = ?";
    Object[] parameters = { experiment.getExperimentId() };
    return this.getHibernateTemplate().find(query, parameters);
  }

    @Override
    public List<ExperimentLink> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
