package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.ExperimentLibraryDesignDAO;
import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>ExperimentLibraryDesignDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ExperimentLibraryDesignDAOHibernate extends HibernateDaoSupport implements ExperimentLibraryDesignDAO {

  /**
   * <p>Constructor for ExperimentLibraryDesignDAOHibernate.</p>
   */
  public ExperimentLibraryDesignDAOHibernate() {
    super();
  }

  /** {@inheritDoc} */
  public void insert(ExperimentLibraryDesign obj) {
    this.getHibernateTemplate().save(obj);
  }

  /** {@inheritDoc} */
  public void update(ExperimentLibraryDesign obj) {
    this.getHibernateTemplate().update(obj);
  }

  /** {@inheritDoc} */
  public List<ExperimentLibraryDesign> list(Registration registration) {

    ArrayList<ExperimentLibraryDesign> objs = new ArrayList<ExperimentLibraryDesign>();
    if (registration == null)
      return objs;

    List temp = this.getHibernateTemplate().find("from ExperimentLibraryDesign as e order by e.name desc");

    for (Object obj : temp) {
      objs.add((ExperimentLibraryDesign) obj);
    }

    return objs;
  }

  /**
   * {@inheritDoc}
   *
   * Finds an instance of ExperimentLibraryDesign in the database by the
   * ExperimentLibraryDesign ID.
   */
  public ExperimentLibraryDesign findByID(Integer id) {
    String query = "from ExperimentLibraryDesign as e where e.experimentLibraryDesignId = ?";
    ExperimentLibraryDesign obj = null;
    Object[] parameters = { id };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (ExperimentLibraryDesign) list.get(0);
    }
    return obj;
  }

  /** {@inheritDoc} */
  @Override
  public ExperimentLibraryDesign updateDetached(ExperimentLibraryDesign eld) {
    ExperimentLibraryDesign dbObject = findByID(eld.getExperimentLibraryDesignId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, eld);
      return (ExperimentLibraryDesign) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    /** {@inheritDoc} */
    @Override
    public List<ExperimentLibraryDesign> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
