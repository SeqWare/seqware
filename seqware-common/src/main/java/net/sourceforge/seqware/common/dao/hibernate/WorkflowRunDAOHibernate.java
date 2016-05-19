package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sourceforge.seqware.common.dao.WorkflowRunDAO;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 * <p>
 * WorkflowRunDAOHibernate class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowRunDAOHibernate extends HibernateDaoSupport implements WorkflowRunDAO {

    final Logger localLogger = LoggerFactory.getLogger(WorkflowRunDAOHibernate.class);

    /**
     * <p>
     * Constructor for WorkflowRunDAOHibernate.
     * </p>
     */
    public WorkflowRunDAOHibernate() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(WorkflowRun workflowRun) {
        this.getHibernateTemplate().save(workflowRun);
        this.currentSession().flush();
        return workflowRun.getSwAccession();
    }

    /** {@inheritDoc} */
    @Override
    public void update(WorkflowRun workflowRun) {
        getHibernateTemplate().update(workflowRun);
        currentSession().flush();
    }

    /** {@inheritDoc} */
    @Override
    public void delete(WorkflowRun workflowRun) {
        getHibernateTemplate().delete(workflowRun);
    }

    /**
     * <p>
     * update.
     * </p>
     *
     * @param workflowRun
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     * @param laneIds
     *            a {@link java.util.List} object.
     */
    @Override
    public void update(WorkflowRun workflowRun, List<Integer> laneIds) {
        String paramQuery = "";
        for (int i = 0; i < laneIds.size() - 1; i++) {
            paramQuery = paramQuery + "?,";
        }
        paramQuery = paramQuery + "?";

        String query = "update processing set  workflow_id=? where processing_id in "
                + "(select  processing_id from processing_lanes where lane_id in (" + paramQuery + ") )";

        SQLQuery sql = this.currentSession().createSQLQuery(query);

        sql.setInteger(0, workflowRun.getWorkflowRunId());
        for (int i = 0; i < laneIds.size(); i++) {
            sql.setInteger(i + 1, laneIds.get(i));
        }

        sql.executeUpdate();
        currentSession().flush();
    }

    /**
     * <p>
     * list.
     * </p>
     *
     * @return a {@link java.util.List} object.
     */
    @Override
    public List<WorkflowRun> list() {
        ArrayList<WorkflowRun> workflowRuns = new ArrayList<>();
        // Limit the workflows to those owned by the user

        String query = "from WorkflowRun as workflowRun order by workflowRun.createTimestamp desc";
        // Object[] parameters = { registration.getRegistrationId() };
        Session s = getSessionFactory().getCurrentSession();
        SQLQuery sqlquery = s.createSQLQuery("select workflow_run_id, workflow_id,  owner_id,  name,  ini_file,  cmd, "
                + " workflow_template,  status,  status_cmd,  seqware_revision,  host,  current_working_dir, "
                + "username,  create_tstmp,  update_tstmp,  sw_accession, stderr, stdout, workflow_engine from workflow_run");
        sqlquery.addEntity(WorkflowRun.class);
        List list = sqlquery.list();
        for (Object obj : list) {
            WorkflowRun run = (WorkflowRun) obj;
            workflowRuns.add(run);
            s.clear();
        }

        return workflowRuns;
    }

    /** {@inheritDoc} */
    @Override
    public List<WorkflowRun> list(Registration registration, Boolean isAsc) {
        ArrayList<WorkflowRun> workflowRuns = new ArrayList<>();
        localLogger.debug("Get WFR LIST. " + registration.getEmailAddress());
        /*
         * Criteria criteria = this.currentSession().createCriteria(Study.class); criteria.add(Expression.eq("owner_id",
         * registration.getRegistrationId())); criteria.addOrder(Order.asc("create_tstmp")); criteria.setFirstResult(100);
         * criteria.setMaxResults(50); List pageResults=criteria.list();
         */
        String query;
        Object[] parameters = { registration.getRegistrationId() };
        String sortValue = (!isAsc) ? "asc" : "desc";
        // Limit the workflows to those owned by the user
        if (registration.isLIMSAdmin()) {
            query = "from WorkflowRun as workflowRun " + "where workflowRun.status='completed' " + "order by workflowRun.createTimestamp "
                    + sortValue;
            parameters = null;
        } else {
            query = "from WorkflowRun as workflowRun " + "where workflowRun.owner.registrationId=? and workflowRun.status='completed' "
                    + "order by workflowRun.createTimestamp " + sortValue;
        }

        List list = this.getHibernateTemplate().find(query, parameters);

        for (Object workflowRun : list) {
            workflowRuns.add((WorkflowRun) workflowRun);
        }
        return workflowRuns;
    }

    /** {@inheritDoc} */
    @Override
    public List<Workflow> listRelatedWorkflows(Registration registration) {
        String querySQL = "select distinct wr.workflow from WorkflowRun as wr where wr.owner.registrationId = ?";
        Object[] parameters = { registration.getRegistrationId() };
        List list = this.getHibernateTemplate().find(querySQL, parameters);
        List<Workflow> workflows = new ArrayList<>();
        for (Object workflow : list) {
            workflows.add((Workflow) workflow);
        }
        return workflows;
    }

    /** {@inheritDoc} */
    @Override
    public List<WorkflowRun> listMyShared(Registration registration, Boolean isAsc) {
        List<WorkflowRun> sharedWorkflowRuns = new ArrayList<>();

        String sortValue = (!isAsc) ? "asc" : "desc";
        String query = "from WorkflowRun as workflowRun where workflowRun.owner.registrationId=? "
                + "and workflowRun.sharedWorkflowRuns.size > 0 " + "order by workflowRun.createTimestamp " + sortValue;
        Object[] parameters = { registration.getRegistrationId() };
        List list = this.getHibernateTemplate().find(query, parameters);

        for (Object workflowRun : list) {
            sharedWorkflowRuns.add((WorkflowRun) workflowRun);
        }

        return sharedWorkflowRuns;
    }

    /** {@inheritDoc} */
    @Override
    public List<WorkflowRun> listSharedWithMe(Registration registration, Boolean isAsc) {
        List<WorkflowRun> sharedWithMeWorkflowRuns = new ArrayList<>();
        if (registration == null) {
            return sharedWithMeWorkflowRuns;
        }

        String sortValue = (!isAsc) ? "asc" : "desc";
        String query = "select workflowRun from WorkflowRun as workflowRun " + "inner join workflowRun.sharedWorkflowRuns as runs "
                + "where runs.registration.registrationId = ? " + "order by workflowRun.createTimestamp " + sortValue;
        Object[] parameters = { registration.getRegistrationId() };
        List list = this.getHibernateTemplate().find(query, parameters);

        for (Object workflowRun : list) {
            sharedWithMeWorkflowRuns.add((WorkflowRun) workflowRun);
        }
        return sharedWithMeWorkflowRuns;
    }

    /** {@inheritDoc} */
    @Override
    public List<WorkflowRun> listRunning(Registration registration, Boolean isAsc) {
        List<WorkflowRun> runningWorkflowRuns = new ArrayList<>();
        String query;
        Object[] parameters = { registration.getRegistrationId() };
        String sortValue = (!isAsc) ? "asc" : "desc";
        if (registration.isLIMSAdmin()) {
            query = "from WorkflowRun as workflowRun " + "where workflowRun.status<>'completed' " + "order by workflowRun.createTimestamp "
                    + sortValue;
            parameters = null;
        } else {
            query = "from WorkflowRun as workflowRun " + "where workflowRun.owner.registrationId=? and workflowRun.status<>'completed' "
                    + "order by workflowRun.createTimestamp " + sortValue;
        }
        List list = this.getHibernateTemplate().find(query, parameters);

        for (Object workflowRun : list) {
            runningWorkflowRuns.add((WorkflowRun) workflowRun);
        }
        return runningWorkflowRuns;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowRun findByName(String name) {
        String query = "from WorkflowRun as workflowRun where workflowRun.name = ?";
        WorkflowRun workflowRun = null;
        Object[] parameters = { name };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            workflowRun = (WorkflowRun) list.get(0);
        }
        return workflowRun;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowRun findByID(Integer wfrID) {
        String query = "from WorkflowRun as workflowRun where workflowRun.workflowRunId = ?";
        WorkflowRun workflowRun = null;
        Object[] parameters = { wfrID };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            workflowRun = (WorkflowRun) list.get(0);
        }
        return workflowRun;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public WorkflowRun findBySWAccession(Integer swAccession) {
        String query = "from WorkflowRun as workflowRun where workflowRun.swAccession = ?";
        WorkflowRun workflowRun = null;
        Object[] parameters = { swAccession };
        List<WorkflowRun> list = (List<WorkflowRun>) this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            workflowRun = list.get(0);
        }
        return workflowRun;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<WorkflowRun> findByOwnerID(Integer registrationID) {
        String query = "from WorkflowRun as workflowRun where workflowRun.owner.registrationId = ?";
        Object[] parameters = { registrationID };
        return (List<WorkflowRun>) this.getHibernateTemplate().find(query, parameters);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<WorkflowRun> findByCriteria(String criteria, boolean isCaseSens) {
        String queryStringCase = "from WorkflowRun as wr where cast(wr.swAccession as string) like :sw "
                + " or wr.name like :name order by wr.name";
        String queryStringICase = "from WorkflowRun as wr where cast(wr.swAccession as string) like :sw "
                + " or lower(wr.name) like :name order by wr.name";
        Query query = isCaseSens ? this.currentSession().createQuery(queryStringCase) : this.currentSession().createQuery(queryStringICase);
        if (!isCaseSens) {
            criteria = criteria.toLowerCase();
        }
        criteria = "%" + criteria + "%";
        query.setString("sw", criteria);
        query.setString("name", criteria);

        return query.list();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    public Set<WorkflowRun> findRunsForIUS(IUS ius) {
        Set<WorkflowRun> wfRuns = new HashSet<>();

        // wfRuns.addAll(ius.getWorkflowRuns());
        // Set<Processing> processings = ius.getProcessings();
        //
        // for (Processing processing : processings) {
        // wfRuns.addAll(workflowRunFromProcessing(processing));
        // }

        String query = "WITH RECURSIVE Rec(pid, id) AS (" + " SELECT pr.parent_id, pr.child_id" + " FROM processing_relationship pr"
                + " JOIN processing_ius pi" + " ON (pr.parent_id = pi.processing_id)" + " WHERE pi.ius_id = ?" + " UNION"
                + " SELECT null, pi.processing_id" + " FROM processing_ius pi" + " WHERE pi.ius_id = ?" + " UNION"
                + " SELECT r.pid, sr.child_id FROM Rec r" + " JOIN processing_relationship sr" + " ON (r.id = sr.parent_id)" + ")"
                + " SELECT wr.* FROM Rec r" + " JOIN processing p" + " ON (p.processing_id = r.id)"
                + " JOIN workflow_run wr ON (p.workflow_run_id = wr.workflow_run_id)";

        List list = this.currentSession().createSQLQuery(query).addEntity(WorkflowRun.class).setInteger(0, ius.getIusId())
                .setInteger(1, ius.getIusId()).list();

        for (Object wfRunObj : list) {
            WorkflowRun wfRun = (WorkflowRun) wfRunObj;
            wfRuns.add(wfRun);
        }

        wfRuns.addAll(ius.getWorkflowRuns());
        return wfRuns;
    }

    private Collection<? extends WorkflowRun> workflowRunFromProcessing(Processing processing) {
        Set<WorkflowRun> wfRuns = new HashSet<>();
        if (processing.getWorkflowRun() != null) {
            wfRuns.add(processing.getWorkflowRun());
        }

        for (Processing child : processing.getChildren()) {
            wfRuns.addAll(workflowRunFromProcessing(child));
        }
        return wfRuns;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowRun updateDetached(WorkflowRun workflowRun) {
        WorkflowRun dbWf = reattachWorkflowRun(workflowRun);
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbWf, workflowRun);
            return (WorkflowRun) this.getHibernateTemplate().merge(dbWf);
        } catch (IllegalAccessException | InvocationTargetException e) {
            localLogger.error("Error updating detached WorkflowRun", e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<WorkflowRun> findByCriteria(String criteria) {

        /*
         * Not sure why this doesn't work but I think the :sw sub is only good for a value and not an HQL phrase String queryStringCase =
         * "from WorkflowRun as wr where :sw"; Query query = this.currentSession().createQuery(queryStringCase); query.setString("sw",
         * criteria);
         */

        String queryStringCase = "from WorkflowRun as wr where ";
        Query query = this.currentSession().createQuery(queryStringCase + " " + criteria);

        return query.list();
    }

    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, WorkflowRun workflowRun) {
        WorkflowRun dbWf = reattachWorkflowRun(workflowRun);
        if (registration == null) {
            localLogger.error("WorkflowRunDAOHibernate update registration is null");
        } else if (registration.isLIMSAdmin() || (dbWf.givesPermission(registration) && workflowRun.givesPermission(registration))) {
            localLogger.info("Updating workflow run object");
            update(workflowRun);
        } else {
            localLogger.error("WorkflowRunDAOHibernate update not authorized");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(Registration registration, WorkflowRun workflowRun) {
        if (registration == null) {
            localLogger.error("WorkflowRunDAOHibernate insert registration is null");
        } else if (registration.isLIMSAdmin() || workflowRun.givesPermission(registration)) {
            localLogger.info("insert workflow run object");
            return insert(workflowRun);
        } else {
            localLogger.error("WorkflowRunDAOHibernate insert not authorized");
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowRun updateDetached(Registration registration, WorkflowRun workflowRun) {
        WorkflowRun dbWf = reattachWorkflowRun(workflowRun);
        if (registration == null) {
            localLogger.error("WorkflowRunDAOHibernate updateDetached registration is null");
        } else if (registration.isLIMSAdmin() || dbWf.givesPermission(registration)) {
            localLogger.info("updateDetached workflow run object");
            return updateDetached(workflowRun);
        } else {
            localLogger.error("WorkflowRunDAOHibernate updateDetached not authorized");
        }
        return null;
    }

    private WorkflowRun reattachWorkflowRun(WorkflowRun workflowRun) throws IllegalStateException, DataAccessResourceFailureException {
        WorkflowRun dbObject = workflowRun;
        if (!currentSession().contains(workflowRun)) {
            dbObject = findByID(workflowRun.getWorkflowRunId());
        }
        return dbObject;
    }
}
