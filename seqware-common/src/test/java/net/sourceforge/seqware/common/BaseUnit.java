package net.sourceforge.seqware.common;

import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.factory.BeanFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

public class BaseUnit {
  protected RegistrationService registrationService;

  public BaseUnit() throws Exception {
    registrationService = BeanFactory.getRegistrationServiceBean();
  }

  protected Session getSession() {
    SessionFactory sessionFactory = BeanFactory.getSessionFactoryBean();
    Session session = SessionFactoryUtils.getSession(sessionFactory, true);
    return session;
  }

  protected void removeSession(Session session) {
    SessionFactoryUtils.closeSession(session);
  }
}
