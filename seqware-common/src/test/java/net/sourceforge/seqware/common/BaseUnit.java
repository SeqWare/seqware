package net.sourceforge.seqware.common;

import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.SessionFactoryUtils;

/**
 * <p>
 * BaseUnit class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class BaseUnit {
    protected RegistrationService registrationService;

    /**
     * <p>
     * Constructor for BaseUnit.
     * </p>
     * 
     * @throws java.lang.Exception
     *             if any.
     */
    public BaseUnit() throws Exception {
        registrationService = BeanFactory.getRegistrationServiceBean();
    }

    /**
     * <p>
     * getSession.
     * </p>
     * 
     * @return a {@link org.hibernate.Session} object.
     */
    protected Session getSession() {
        SessionFactory sessionFactory = BeanFactory.getSessionFactoryBean();
        return sessionFactory.getCurrentSession();
    }

    /**
     * <p>
     * removeSession.
     * </p>
     * 
     * @param session
     *            a {@link org.hibernate.Session} object.
     */
    protected void removeSession(Session session) {
        SessionFactoryUtils.closeSession(session);
    }
}
