package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.seqware.common.dao.ExperimentSpotDesignReadSpecDAO;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>ExperimentSpotDesignReadSpecDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ExperimentSpotDesignReadSpecDAOHibernate extends HibernateDaoSupport implements
    ExperimentSpotDesignReadSpecDAO {

  /**
   * <p>Constructor for ExperimentSpotDesignReadSpecDAOHibernate.</p>
   */
  public ExperimentSpotDesignReadSpecDAOHibernate() {
    super();
  }

  /** {@inheritDoc} */
  public void insert(ExperimentSpotDesignReadSpec obj) {
    this.getHibernateTemplate().save(obj);
  }

  /** {@inheritDoc} */
  public void update(ExperimentSpotDesignReadSpec obj) {
    this.getHibernateTemplate().update(obj);
  }

  /** {@inheritDoc} */
  public void delete(ExperimentSpotDesignReadSpec obj) {
    this.getHibernateTemplate().delete(obj);
  }

  /**
   * {@inheritDoc}
   *
   * Finds an instance of ExperimentSpotDesignReadSpec in the database by the
   * ExperimentSpotDesignReadSpec ID.
   */
  public ExperimentSpotDesignReadSpec findByID(Integer id) {
    String query = "from ExperimentSpotDesignReadSpec as e where e.experimentSpotDesignReadSpecId = ?";
    ExperimentSpotDesignReadSpec obj = null;
    Object[] parameters = { id };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (ExperimentSpotDesignReadSpec) list.get(0);
    }
    return obj;
  }

  /** {@inheritDoc} */
  public ExperimentSpotDesignReadSpec updateDetached(ExperimentSpotDesignReadSpec experiment) {
    ExperimentSpotDesignReadSpec dbObject = findByID(experiment.getExperimentSpotDesignReadSpecId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, experiment);
      return (ExperimentSpotDesignReadSpec) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    /** {@inheritDoc} */
    @Override
    public List<ExperimentSpotDesignReadSpec> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
