package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.seqware.common.dao.ExperimentSpotDesignDAO;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>ExperimentSpotDesignDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ExperimentSpotDesignDAOHibernate extends HibernateDaoSupport implements ExperimentSpotDesignDAO {

  /**
   * <p>Constructor for ExperimentSpotDesignDAOHibernate.</p>
   */
  public ExperimentSpotDesignDAOHibernate() {
    super();
  }

  /** {@inheritDoc} */
  public void insert(ExperimentSpotDesign obj) {
    this.getHibernateTemplate().save(obj);
  }

  /** {@inheritDoc} */
  public void update(ExperimentSpotDesign obj) {
    this.getHibernateTemplate().update(obj);
  }

  /**
   * {@inheritDoc}
   *
   * Finds an instance of ExperimentSpotDesign in the database by the
   * ExperimentSpotDesign ID.
   */
  public ExperimentSpotDesign findByID(Integer id) {
    String query = "from ExperimentSpotDesign as e where e.experimentSpotDesignId = ?";
    ExperimentSpotDesign obj = null;
    Object[] parameters = { id };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (ExperimentSpotDesign) list.get(0);
    }
    return obj;
  }

  /** {@inheritDoc} */
  @Override
  public ExperimentSpotDesign updateDetached(ExperimentSpotDesign experiment) {
    ExperimentSpotDesign dbObject = findByID(experiment.getExperimentSpotDesignId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, experiment);
      return (ExperimentSpotDesign) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    /** {@inheritDoc} */
    @Override
    public List<ExperimentSpotDesign> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
