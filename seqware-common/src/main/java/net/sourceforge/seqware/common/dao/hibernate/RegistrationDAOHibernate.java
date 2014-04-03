package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.seqware.common.dao.RegistrationDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>RegistrationDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class RegistrationDAOHibernate extends HibernateDaoSupport implements RegistrationDAO {
  
    private Logger logger;
    
    /**
     * <p>Constructor for RegistrationDAOHibernate.</p>
     */
    public RegistrationDAOHibernate() {
    super();
    logger = Logger.getLogger(RegistrationDAOHibernate.class);
  }

  /**
   * {@inheritDoc}
   *
   * Inserts an instance of Registration into the database.
   */
  public void insert(Registration registration) {
    this.getHibernateTemplate().save(registration);
  }

  /**
   * {@inheritDoc}
   *
   * Updates an instance of Registration in the database.
   */
  public void update(Registration registration) {

    this.getHibernateTemplate().update(registration);
  }

  /**
   * {@inheritDoc}
   *
   * Finds an instance of Registration in the database by the Registration
   * emailAddress only.
   */
  public Registration findByEmailAddress(String emailAddress) {
    String query = "from Registration as registration where registration.emailAddress = ?";
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
   * Finds an instance of Registration in the database by the Registration
   * emailAddress and password.
   */
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
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }
}
