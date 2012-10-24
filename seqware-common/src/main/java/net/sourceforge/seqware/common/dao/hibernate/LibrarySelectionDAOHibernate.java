package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.LibrarySelectionDAO;
import net.sourceforge.seqware.common.model.LibrarySelection;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>LibrarySelectionDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LibrarySelectionDAOHibernate extends HibernateDaoSupport implements LibrarySelectionDAO {
  /**
   * <p>Constructor for LibrarySelectionDAOHibernate.</p>
   */
  public LibrarySelectionDAOHibernate() {
    super();
  }

  /** {@inheritDoc} */
  public List<LibrarySelection> list(Registration registration) {
    ArrayList<LibrarySelection> objects = new ArrayList<LibrarySelection>();
    if (registration == null)
      return objects;

    List expmts = this.getHibernateTemplate().find("from LibrarySelection as ls order by ls.librarySelectionId asc" // desc
    );

    for (Object object : expmts) {
      objects.add((LibrarySelection) object);
    }
    return objects;
  }

  /** {@inheritDoc} */
  public LibrarySelection findByID(Integer id) {
    String query = "from LibrarySelection as l where l.librarySelectionId = ?";
    LibrarySelection obj = null;
    Object[] parameters = { id };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (LibrarySelection) list.get(0);
    }
    return obj;
  }

  /** {@inheritDoc} */
  @Override
  public LibrarySelection updateDetached(LibrarySelection librarySelection) {
    LibrarySelection dbObject = findByID(librarySelection.getLibrarySelectionId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, librarySelection);
      return (LibrarySelection) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    /** {@inheritDoc} */
    @Override
    public List<LibrarySelection> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
