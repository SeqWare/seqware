package net.sourceforge.seqware.common.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.ExperimentAttributeDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.Study;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ExperimentAttributeDAOHibernate extends HibernateDaoSupport implements ExperimentAttributeDAO {

  @Override
  public void insert(ExperimentAttribute experimentAttribute) {
    this.getHibernateTemplate().save(experimentAttribute);

  }

  @Override
  public void update(ExperimentAttribute experimentAttribute) {
    this.getHibernateTemplate().saveOrUpdate(experimentAttribute);
  }

  @Override
  public void delete(ExperimentAttribute experimentAttribute) {
    this.getHibernateTemplate().delete(experimentAttribute);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ExperimentAttribute> findAll(Experiment experiment) {
    String query = "from ExperimentAttribute as ea where ea.experiment.experimentId = ?";
    Object[] parameters = { experiment.getExperimentId() };
    return this.getHibernateTemplate().find(query, parameters);
  }

    @Override
    public List<ExperimentAttribute> list() {
        ArrayList<ExperimentAttribute> eAtts = new ArrayList<ExperimentAttribute>();

        String query = "from ExperimentAttribute";

        List list = this.getHibernateTemplate().find(query);

        for (Object e : list) {
            eAtts.add((ExperimentAttribute) e);
        }

        return eAtts;
    }
}
