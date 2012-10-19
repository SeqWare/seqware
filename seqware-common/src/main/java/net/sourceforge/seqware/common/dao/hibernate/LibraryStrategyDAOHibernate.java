package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.LibraryStrategyDAO;
import net.sourceforge.seqware.common.model.LibraryStrategy;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>LibraryStrategyDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LibraryStrategyDAOHibernate extends HibernateDaoSupport implements LibraryStrategyDAO {

  /**
   * <p>Constructor for LibraryStrategyDAOHibernate.</p>
   */
  public LibraryStrategyDAOHibernate() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  public List<LibraryStrategy> list(Registration registration) {
    if (registration == null) { return(null); }
    return(list());
  }

  /**
   * {@inheritDoc}
   */
  public LibraryStrategy findByID(Integer id) {
    String query = "from LibraryStrategy as l where l.libraryStrategyId = ?";
    LibraryStrategy obj = null;
    Object[] parameters = {id};
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (LibraryStrategy) list.get(0);
    }
    return obj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LibraryStrategy updateDetached(LibraryStrategy strategy) {
    LibraryStrategy dbObject = findByID(strategy.getLibraryStrategyId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, strategy);
      return (LibraryStrategy) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<LibraryStrategy> list() {
    ArrayList<LibraryStrategy> objects = new ArrayList<LibraryStrategy>();

    List expmts = this.getHibernateTemplate().find("from LibraryStrategy as ls order by ls.libraryStrategyId asc" // desc
            );

    for (Object object : expmts) {
      objects.add((LibraryStrategy) object);
    }
    return objects;
  }
}
