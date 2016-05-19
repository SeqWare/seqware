package net.sourceforge.seqware.common.dao.hibernate;

import net.sourceforge.seqware.common.dao.WorkflowParamValueDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowParamValue;
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
 * WorkflowParamValueDAOHibernate class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowParamValueDAOHibernate extends HibernateDaoSupport implements WorkflowParamValueDAO {

    final Logger localLogger = LoggerFactory.getLogger(WorkflowParamValueDAOHibernate.class);

    /**
     * <p>
     * Constructor for WorkflowParamValueDAOHibernate.
     * </p>
     */
    public WorkflowParamValueDAOHibernate() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(WorkflowParamValue workflowParamValue) {
        // this.currentSession().evict(workflowParam.getWorkflow());
        return (Integer) this.getHibernateTemplate().save(workflowParamValue);
    }

    /** {@inheritDoc} */
    @Override
    public void update(WorkflowParamValue workflowParamValue) {
        getHibernateTemplate().update(workflowParamValue);
        this.currentSession().flush();
    }

    /** {@inheritDoc} */
    @Override
    public void delete(WorkflowParamValue workflowParamValue) {
        getHibernateTemplate().delete(workflowParamValue);
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowParamValue findByID(Integer id) {
        String query = "from WorkflowParamValue as workflowParamValue where workflowParamValue.workflowParamValueId = ?";
        WorkflowParamValue workflowParamValue = null;
        Object[] parameters = { id };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            workflowParamValue = (WorkflowParamValue) list.get(0);
        }
        return workflowParamValue;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowParamValue updateDetached(WorkflowParamValue workflowParamValue) {
        WorkflowParamValue dbObject = reattachWorkflowParamValue(workflowParamValue);
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, workflowParamValue);
            return (WorkflowParamValue) this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            localLogger.error("Error updating detached workflowparamvalue", e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<WorkflowParamValue> list() {
        ArrayList<WorkflowParamValue> l = new ArrayList<>();

        String query = "from WorkflowParamValue";

        List list = this.getHibernateTemplate().find(query);

        for (Object e : list) {
            l.add((WorkflowParamValue) e);
        }

        return l;
    }

    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, WorkflowParamValue workflowParamValue) {
        WorkflowParamValue dbObject = reattachWorkflowParamValue(workflowParamValue);
        if (registration == null) {
            localLogger.error("WorkflowParamValueDAOHibernate update: registration is null");
        } else if (registration.isLIMSAdmin()
                || (workflowParamValue.givesPermission(registration) && dbObject.givesPermission(registration))) {
            localLogger.info("Updating workflow param value object");
            update(workflowParamValue);
        } else
            localLogger.error("WorkflowParamValueDAOHibernate update not authorized");
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(Registration registration, WorkflowParamValue workflowParamValue) {
        if (registration == null) {
            localLogger.error("WorkflowParamValueDAOHibernate insert: registration is null");
        } else if (registration.isLIMSAdmin() || workflowParamValue.givesPermission(registration)) {
            localLogger.info("insert workflow param value object");
            return insert(workflowParamValue);
        } else
            localLogger.error("WorkflowParamValueDAOHibernate insert not authorized");
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowParamValue updateDetached(Registration registration, WorkflowParamValue workflowParamValue) {
        WorkflowParamValue dbObject = reattachWorkflowParamValue(workflowParamValue);
        if (registration == null) {
            localLogger.error("WorkflowParamValueDAOHibernate updateDetached: registration is null");
        } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
            localLogger.info("updateDetached workflow param value object");
            return updateDetached(workflowParamValue);
        } else
            localLogger.error("WorkflowParamValueDAOHibernate updateDetached not authorized");
        return null;
    }

    private WorkflowParamValue reattachWorkflowParamValue(WorkflowParamValue workflowParam) throws IllegalStateException,
            DataAccessResourceFailureException {
        WorkflowParamValue dbObject = workflowParam;
        if (!this.currentSession().contains(workflowParam)) {
            dbObject = findByID(workflowParam.getWorkflowParamValueId());
        }
        return dbObject;
    }

}
