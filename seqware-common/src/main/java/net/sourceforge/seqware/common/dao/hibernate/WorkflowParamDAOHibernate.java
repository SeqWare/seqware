package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.WorkflowParamDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class WorkflowParamDAOHibernate extends HibernateDaoSupport implements WorkflowParamDAO {

    public WorkflowParamDAOHibernate() {
        super();
    }

    @Override
    public Integer insert(WorkflowParam workflowParam) {
        return (Integer) this.getHibernateTemplate().save(workflowParam);
    }

    public void update(WorkflowParam workflowParam) {
        getHibernateTemplate().update(workflowParam);
        getSession().flush();
    }

    public void delete(WorkflowParam workflowParam) {
        getHibernateTemplate().delete(workflowParam);
    }

    public WorkflowParam findByID(Integer id) {
        String query = "from WorkflowParam as workflowParam where workflowParam.workflowParamId = ?";
        WorkflowParam workflowParam = null;
        Object[] parameters = {id};
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            workflowParam = (WorkflowParam) list.get(0);
        }
        return workflowParam;
    }

    @Override
    public WorkflowParam updateDetached(WorkflowParam workflowParam) {
        WorkflowParam dbObject = reattachWorkflowParam(workflowParam);
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, workflowParam);
            return (WorkflowParam) this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private WorkflowParam reattachWorkflowParam(WorkflowParam workflowParam) throws IllegalStateException, DataAccessResourceFailureException {
        WorkflowParam dbObject = workflowParam;
        if (!getSession().contains(workflowParam)) {
            dbObject = findByID(workflowParam.getWorkflowParamId());
        }
        return dbObject;
    }

    @Override
    public List<WorkflowParam> list() {
        ArrayList<WorkflowParam> l = new ArrayList<WorkflowParam>();

        String query = "from WorkflowParam";

        List list = this.getHibernateTemplate().find(query);

        for (Object e : list) {
            l.add((WorkflowParam) e);
        }

        return l;
    }

    @Override
    public void update(Registration registration, WorkflowParam workflowParam) {
        WorkflowParam dbObject = reattachWorkflowParam(workflowParam);
        Logger logger = Logger.getLogger(WorkflowParamDAOHibernate.class);
        if (registration == null) {
            logger.error("WorkflowParamDAOHibernate update: Registration is null - exiting");
        } else if (registration.isLIMSAdmin()
                || (workflowParam.givesPermission(registration) && dbObject.givesPermission(registration))) {
            logger.info("WorkflowParamDAOHibernate Updating workflow param object");
            update(workflowParam);
        } else {
            logger.error("WorkflowParamDAOHibernate update not authorized");
        }
    }

    @Override
    public Integer insert(Registration registration, WorkflowParam workflowParam) {
        Logger logger = Logger.getLogger(WorkflowParamDAOHibernate.class);
        if (registration == null) {
            logger.error("WorkflowParamDAOHibernate insert: Registration is null - exiting");
        } else if (registration.isLIMSAdmin() || workflowParam.givesPermission(registration)) {
            logger.info("insert workflow param object");
            return insert(workflowParam);
        } else {
            logger.error("WorkflowParamDAOHibernate insert not authorized");
        }
        return null;
    }

    @Override
    public WorkflowParam updateDetached(Registration registration, WorkflowParam workflowParam) {
        WorkflowParam dbObject = reattachWorkflowParam(workflowParam);
        Logger logger = Logger.getLogger(WorkflowParamDAOHibernate.class);
        if (registration == null) {
            logger.error("WorkflowParamDAOHibernate updateDetached: Registration is null - exiting");
        } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
            logger.info("updateDetached workflow param object");
            return updateDetached(workflowParam);
        } else {
            logger.error("WorkflowParamDAOHibernate updateDetached not authorized");
        }
        return null;
    }
}
