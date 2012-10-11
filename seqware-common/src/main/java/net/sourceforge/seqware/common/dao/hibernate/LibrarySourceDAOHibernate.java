package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.LibrarySourceDAO;
import net.sourceforge.seqware.common.model.LibrarySource;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class LibrarySourceDAOHibernate extends HibernateDaoSupport implements LibrarySourceDAO {
  public LibrarySourceDAOHibernate() {
    super();
  }

  public List<LibrarySource> list(Registration registration) {
    ArrayList<LibrarySource> objects = new ArrayList<LibrarySource>();
    if (registration == null)
      return objects;

    List expmts = this.getHibernateTemplate().find("from LibrarySource as ls order by ls.librarySourceId asc" // desc
    );

    for (Object object : expmts) {
      objects.add((LibrarySource) object);
    }
    return objects;
  }

  public LibrarySource findByID(Integer id) {
    String query = "from LibrarySource as l where l.librarySourceId = ?";
    LibrarySource obj = null;
    Object[] parameters = { id };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (LibrarySource) list.get(0);
    }
    return obj;
  }

  @Override
  public LibrarySource updateDetached(LibrarySource librarySource) {
    LibrarySource dbObject = findByID(librarySource.getLibrarySourceId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, librarySource);
      return (LibrarySource) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    @Override
    public List<LibrarySource> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
