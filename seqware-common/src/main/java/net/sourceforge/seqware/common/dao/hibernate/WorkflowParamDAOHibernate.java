package net.sourceforge.seqware.common.dao.hibernate;

import net.sourceforge.seqware.common.dao.WorkflowParamDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * WorkflowParamDAOHibernate class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowParamDAOHibernate extends HibernateDaoSupport implements WorkflowParamDAO {

    final Logger localLogger = LoggerFactory.getLogger(InvoiceDAOHibernate.class);

    /**
     * <p>
     * Constructor for WorkflowParamDAOHibernate.
     * </p>
     */
    public WorkflowParamDAOHibernate() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(WorkflowParam workflowParam) {
        return (Integer) this.getHibernateTemplate().save(workflowParam);
    }

    /** {@inheritDoc} */
    @Override
    public void update(WorkflowParam workflowParam) {
        getHibernateTemplate().update(workflowParam);
        this.currentSession().flush();
    }

    /** {@inheritDoc} */
    @Override
    public void delete(WorkflowParam workflowParam) {
        getHibernateTemplate().delete(workflowParam);
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowParam findByID(Integer id) {
        String query = "from WorkflowParam as workflowParam where workflowParam.workflowParamId = ?";
        WorkflowParam workflowParam = null;
        Object[] parameters = { id };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            workflowParam = (WorkflowParam) list.get(0);
        }
        return workflowParam;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowParam updateDetached(WorkflowParam workflowParam) {
        WorkflowParam dbObject = reattachWorkflowParam(workflowParam);
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, workflowParam);
            return this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            localLogger.error("Error updating detached WorkflowParam", e);
        }
        return null;
    }

    private WorkflowParam reattachWorkflowParam(WorkflowParam workflowParam) throws IllegalStateException,
            DataAccessResourceFailureException {
        WorkflowParam dbObject = workflowParam;
        if (!this.currentSession().contains(workflowParam)) {
            dbObject = findByID(workflowParam.getWorkflowParamId());
        }
        return dbObject;
    }

    /** {@inheritDoc} */
    @Override
    public List<WorkflowParam> list() {
        ArrayList<WorkflowParam> l = new ArrayList<>();

        String query = "from WorkflowParam";

        List list = this.getHibernateTemplate().find(query);

        for (Object e : list) {
            l.add((WorkflowParam) e);
        }

        return l;
    }

    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, WorkflowParam workflowParam) {
        WorkflowParam dbObject = reattachWorkflowParam(workflowParam);
        if (registration == null) {
            localLogger.error("WorkflowParamDAOHibernate update: Registration is null - exiting");
        } else if (registration.isLIMSAdmin() || (workflowParam.givesPermission(registration) && dbObject.givesPermission(registration))) {
            localLogger.info("WorkflowParamDAOHibernate Updating workflow param object");
            update(workflowParam);
        } else {
            localLogger.error("WorkflowParamDAOHibernate update not authorized");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(Registration registration, WorkflowParam workflowParam) {
        if (registration == null) {
            localLogger.error("WorkflowParamDAOHibernate insert: Registration is null - exiting");
        } else if (registration.isLIMSAdmin() || workflowParam.givesPermission(registration)) {
            localLogger.info("insert workflow param object");
            return insert(workflowParam);
        } else {
            localLogger.error("WorkflowParamDAOHibernate insert not authorized");
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowParam updateDetached(Registration registration, WorkflowParam workflowParam) {
        WorkflowParam dbObject = reattachWorkflowParam(workflowParam);
        if (registration == null) {
            localLogger.error("WorkflowParamDAOHibernate updateDetached: Registration is null - exiting");
        } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
            localLogger.info("updateDetached workflow param object");
            return updateDetached(workflowParam);
        } else {
            localLogger.error("WorkflowParamDAOHibernate updateDetached not authorized");
        }
        return null;
    }
}
