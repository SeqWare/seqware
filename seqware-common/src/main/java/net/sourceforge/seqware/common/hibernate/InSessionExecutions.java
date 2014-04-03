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

/**
 * <p>Abstract InSessionExecutions class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public abstract class InSessionExecutions {

    private static Session session;
    private static SessionFactory sessionFactory;
    private static FlushMode oldMode;

    /**
     * <p>runInSessionCalculations.</p>
     */
    protected void runInSessionCalculations() {
        bindSessionToThread();
        hibernateCalls();
        unBindSessionFromTheThread();
    }

    /**
     * <p>hibernateCalls.</p>
     */
    protected abstract void hibernateCalls();

    /**
     * <p>bindSessionToThread.</p>
     */
    public static void bindSessionToThread() {
        sessionFactory = BeanFactory.getSessionFactoryBean();
        session = SessionFactoryUtils.getSession(sessionFactory, true);
        oldMode = session.getFlushMode();
        TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
    }

    /**
     * <p>bindSessionToThread.</p>
     *
     * @param mode a {@link org.hibernate.FlushMode} object.
     */
    public static void bindSessionToThread(FlushMode mode) {
        bindSessionToThread();
        session.setFlushMode(mode);
    }

    /**
     * <p>unBindSessionFromTheThread.</p>
     */
    public static void unBindSessionFromTheThread() {
//        session.flush();
//        session.setFlushMode(oldMode);
        TransactionSynchronizationManager.unbindResource(sessionFactory);
        SessionFactoryUtils.releaseSession(session, sessionFactory);
    }
    
    /**
     * <p>evict.</p>
     *
     * @param o a {@link java.lang.Object} object.
     */
    public static void evict(Object o)
    {
        session.evict(o);
    }
    
}
