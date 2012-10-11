package net.sourceforge.seqware.common.hibernate;

import net.sourceforge.seqware.common.ContextImpl;
import net.sourceforge.seqware.common.factory.BeanFactory;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public abstract class InSessionExecutions {

    private static Session session;
    private static SessionFactory sessionFactory;
    private static FlushMode oldMode;

    protected void runInSessionCalculations() {
        bindSessionToThread();
        hibernateCalls();
        unBindSessionFromTheThread();
    }

    protected abstract void hibernateCalls();

    public static void bindSessionToThread() {
        sessionFactory = BeanFactory.getSessionFactoryBean();
        session = SessionFactoryUtils.getSession(sessionFactory, true);
        oldMode = session.getFlushMode();
        TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
    }

    public static void bindSessionToThread(FlushMode mode) {
        bindSessionToThread();
        session.setFlushMode(mode);
    }

    public static void unBindSessionFromTheThread() {
//        session.flush();
//        session.setFlushMode(oldMode);
        TransactionSynchronizationManager.unbindResource(sessionFactory);
        SessionFactoryUtils.releaseSession(session, sessionFactory);
    }
    
    public static void evict(Object o)
    {
        session.evict(o);
    }
    
}
