package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.sourceforge.seqware.common.dao.RegistrationDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 * <p>
 * RegistrationDAOHibernate class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class RegistrationDAOHibernate extends HibernateDaoSupport implements RegistrationDAO {

    final Logger localLogger = LoggerFactory.getLogger(RegistrationDAOHibernate.class);

    /**
     * <p>
     * Constructor for RegistrationDAOHibernate.
     * </p>
     */
    public RegistrationDAOHibernate() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * Inserts an instance of Registration into the database.
     */
    @Override
    public void insert(Registration registration) {
        this.getHibernateTemplate().save(registration);
    }

    /**
     * {@inheritDoc}
     * 
     * Updates an instance of Registration in the database.
     */
    @Override
    public void update(Registration registration) {

        this.getHibernateTemplate().update(registration);
    }

    /**
     * {@inheritDoc}
     * 
     * Finds an instance of Registration in the database by the Registration emailAddress only.
     */
    @Override
    public Registration findByEmailAddress(String emailAddress) {
        String query = "from Registration as registration where lower(registration.emailAddress) = ?";
        Registration registration = null;
        Object[] parameters = { emailAddress };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            registration = (Registration) list.get(0);
        }
        return registration;
    }

    /**
     * {@inheritDoc}
     * 
     * Finds an instance of Registration in the database by the Registration emailAddress and password.
     */
    @Override
    public Registration findByEmailAddressAndPassword(String emailAddress, String password) {
        String query = "from Registration as registration where registration.emailAddress = ? and registration.password = ?";
        Registration registration = null;
        Object[] parameters = { emailAddress, password };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            registration = (Registration) list.get(0);
        }
        return registration;
    }

    /** {@inheritDoc} */
    @Override
    public Registration updateDetached(Registration registration) {
        Registration dbObject = findByEmailAddress(registration.getEmailAddress());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, registration);
            return (Registration) this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            localLogger.error("Error updating detached registration", e);
        }
        return null;
    }
}
