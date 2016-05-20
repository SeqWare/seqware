package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.seqware.common.dao.LibrarySelectionDAO;
import net.sourceforge.seqware.common.model.LibrarySelection;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 * <p>
 * LibrarySelectionDAOHibernate class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class LibrarySelectionDAOHibernate extends HibernateDaoSupport implements LibrarySelectionDAO {

    /**
     * <p>
     * Constructor for LibrarySelectionDAOHibernate.
     * </p>
     */
    public LibrarySelectionDAOHibernate() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LibrarySelection> list(Registration registration) {
        if (registration == null) {
            return null;
        }
        return (list());
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     * {@inheritDoc}
     */
    @Override
    public LibrarySelection updateDetached(LibrarySelection librarySelection) {
        LibrarySelection dbObject = findByID(librarySelection.getLibrarySelectionId());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, librarySelection);
            return this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LibrarySelection> list() {
        ArrayList<LibrarySelection> objects = new ArrayList<>();

        List expmts = this.getHibernateTemplate().find("from LibrarySelection as ls order by ls.librarySelectionId asc" // desc
        );

        for (Object object : expmts) {
            objects.add((LibrarySelection) object);
        }
        return objects;
    }
}
