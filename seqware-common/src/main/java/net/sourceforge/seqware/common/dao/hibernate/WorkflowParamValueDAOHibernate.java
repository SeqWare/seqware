package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.WorkflowParamValueDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowParamValue;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class WorkflowParamValueDAOHibernate extends HibernateDaoSupport implements WorkflowParamValueDAO {

    public WorkflowParamValueDAOHibernate() {
        super();
    }

    public Integer insert(WorkflowParamValue workflowParamValue) {
        // this.getSession().evict(workflowParam.getWorkflow());
        return (Integer) this.getHibernateTemplate().save(workflowParamValue);
    }

    public void update(WorkflowParamValue workflowParamValue) {
        getHibernateTemplate().update(workflowParamValue);
        getSession().flush();
    }

    public void delete(WorkflowParamValue workflowParamValue) {
        getHibernateTemplate().delete(workflowParamValue);
    }

    public WorkflowParamValue findByID(Integer id) {
        String query = "from WorkflowParamValue as workflowParamValue where workflowParamValue.workflowParamValueId = ?";
        WorkflowParamValue workflowParamValue = null;
        Object[] parameters = {id};
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            workflowParamValue = (WorkflowParamValue) list.get(0);
        }
        return workflowParamValue;
    }

    @Override
    public WorkflowParamValue updateDetached(WorkflowParamValue workflowParamValue) {
        WorkflowParamValue dbObject = reattachWorkflowParamValue(workflowParamValue);
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, workflowParamValue);
            return (WorkflowParamValue) this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<WorkflowParamValue> list() {
        ArrayList<WorkflowParamValue> l = new ArrayList<WorkflowParamValue>();

        String query = "from WorkflowParamValue";

        List list = this.getHibernateTemplate().find(query);

        for (Object e : list) {
            l.add((WorkflowParamValue) e);
        }

        return l;
    }

    @Override
    public void update(Registration registration, WorkflowParamValue workflowParamValue) {
        WorkflowParamValue dbObject = reattachWorkflowParamValue(workflowParamValue);
        Logger logger = Logger.getLogger(WorkflowParamValueDAOHibernate.class);
        if (registration == null) {
            logger.error("WorkflowParamValueDAOHibernate update: registration is null");
        } else if (registration.isLIMSAdmin() || 
                (workflowParamValue.givesPermission(registration) && dbObject.givesPermission(registration))) {
            logger.info("Updating workflow param value object");
            update(workflowParamValue);
        }
        else
             logger.error("WorkflowParamValueDAOHibernate update not authorized");
    }

    @Override
    public Integer insert(Registration registration, WorkflowParamValue workflowParamValue) {
        Logger logger = Logger.getLogger(WorkflowParamValueDAOHibernate.class);
        if (registration == null) {
            logger.error("WorkflowParamValueDAOHibernate insert: registration is null");
        } else if (registration.isLIMSAdmin() || workflowParamValue.givesPermission(registration)) {
            logger.info("insert workflow param value object");
            return insert(workflowParamValue);
        }
        else
             logger.error("WorkflowParamValueDAOHibernate insert not authorized");
        return null;
    }

    @Override
    public WorkflowParamValue updateDetached(Registration registration, WorkflowParamValue workflowParamValue) {
        WorkflowParamValue dbObject = reattachWorkflowParamValue(workflowParamValue);
        Logger logger = Logger.getLogger(WorkflowParamValueDAOHibernate.class);
        if (registration == null) {
            logger.error("WorkflowParamValueDAOHibernate updateDetached: registration is null");
        } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
            logger.info("updateDetached workflow param value object");
            return updateDetached(workflowParamValue);
        }
        else
             logger.error("WorkflowParamValueDAOHibernate updateDetached not authorized");
        return null;
    }
    
        private WorkflowParamValue reattachWorkflowParamValue(WorkflowParamValue workflowParam) throws IllegalStateException, DataAccessResourceFailureException {
        WorkflowParamValue dbObject = workflowParam;
        if (!getSession().contains(workflowParam)) {
            dbObject = findByID(workflowParam.getWorkflowParamValueId());
        }
        return dbObject;
    }
    
}
