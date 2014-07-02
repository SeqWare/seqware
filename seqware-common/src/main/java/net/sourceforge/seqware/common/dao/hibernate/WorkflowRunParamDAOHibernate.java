package net.sourceforge.seqware.common.dao.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.common.dao.WorkflowRunParamDAO;
import net.sourceforge.seqware.common.model.*;

import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>
 * WorkflowRunParamDAOHibernate class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowRunParamDAOHibernate extends HibernateDaoSupport implements WorkflowRunParamDAO {

    final Logger localLogger = LoggerFactory.getLogger(WorkflowRunParamDAOHibernate.class);

    /**
     * <p>
     * Constructor for WorkflowRunParamDAOHibernate.
     * </p>
     */
    public WorkflowRunParamDAOHibernate() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public void insert(WorkflowRunParam workflowRunParam) {
        this.getHibernateTemplate().save(workflowRunParam);
        getSession().flush();
    }

    /** {@inheritDoc} */
    @Override
    public void update(WorkflowRunParam workflowRunParam) {
        getHibernateTemplate().update(workflowRunParam);
        getSession().flush();
    }

    /** {@inheritDoc} */
    @Override
    public void delete(WorkflowRunParam workflowRunParam) {
        getHibernateTemplate().delete(workflowRunParam);
    }

    private Integer getProcessingSWID(Integer fileId) {
        Integer swid = null;
        String query = "select sw_accession from processing pr inner join processing_files pf on pr.processing_id=pf.processing_id where pf.file_id = ?";
        List list = this.getSession().createSQLQuery(query).setInteger(0, fileId).list();
        if (list.get(0) != null) {
            swid = Integer.parseInt(list.get(0).toString());
        }
        return swid;
    }

    /**
     * {@inheritDoc}
     * 
     * @param paramNameFileHash
     */
    @Override
    public void insertFilesAsWorkflowRunParam(WorkflowRun workflowRun, Map<String, List<File>> paramNameFileHash) {
        localLogger.debug("Start insert files ...");
        for (String paramName : paramNameFileHash.keySet()) {

            List<File> files = paramNameFileHash.get(paramName);

            localLogger.debug("Param Name = " + paramName);

            if (files != null && files.size() > 0) {

                String paramQuery = "";
                for (int i = 0; i < files.size() - 1; i++) {
                    paramQuery = paramQuery + "(?,?,?,?,?),";
                }
                paramQuery = paramQuery + "(?,?,?,?,?)";

                String query = "INSERT INTO workflow_run_param (workflow_run_id, key, value, parent_processing_accession, type) VALUES"
                        + paramQuery;

                SQLQuery sql = this.getSession().createSQLQuery(query);

                Integer workflowRunId = workflowRun.getWorkflowRunId();

                int countParam = 5;
                // 0,1,2,3,4 5,6,7,8,9
                localLogger.debug("Insert Files :");
                for (int i = 0; i < files.size(); i++) {
                    int startPos = countParam * i;
                    sql.setInteger(startPos, workflowRunId);
                    sql.setString(startPos + 1, paramName);
                    sql.setString(startPos + 2, files.get(i).getFilePath());
                    // NOTE: in our DB files generally don't have multiple processing
                    // events but since it
                    // uses a linker table many-to-many is possible. Keep this in mind for
                    // the code below,
                    // it just records the sw_accession for the first linked processing
                    // event
                    sql.setInteger(startPos + 3, getProcessingSWID(files.get(i).getFileId()));
                    sql.setString(startPos + 4, "file");
                    localLogger.debug("Key = " + paramName + "; Value = " + files.get(i).getFilePath() + "; parent swAccession = "
                            + getProcessingSWID(files.get(i).getFileId()) + "; type = file");

                }

                localLogger.debug("INSERT STATEMENT: " + query);

                sql.executeUpdate();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowRunParam updateDetached(WorkflowRunParam workflowRunParam) {
        return (WorkflowRunParam) this.getHibernateTemplate().merge(workflowRunParam);
    }

    /** {@inheritDoc} */
    @Override
    public List<WorkflowRunParam> list() {
        ArrayList<WorkflowRunParam> l = new ArrayList<>();

        String query = "from WorkflowRunParam";

        List list = this.getHibernateTemplate().find(query);

        for (Object e : list) {
            l.add((WorkflowRunParam) e);
        }

        return l;
    }

    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, WorkflowRunParam workflowRunParam) {
        if (registration == null) {
            localLogger.error("WorkflowRunParamDAOHibernate update registration is null");
        } else if (registration.isLIMSAdmin() || workflowRunParam.givesPermission(registration)) {
            localLogger.info("update workflow run param object");
            update(workflowRunParam);
        } else {
            localLogger.error("WorkflowRunParamDAOHibernate update is not authorized");
        }

    }

    /** {@inheritDoc} */
    @Override
    public void insert(Registration registration, WorkflowRunParam workflowRunParam) {
        if (registration == null) {
            localLogger.error("WorkflowRunParamDAOHibernate insert registration is null");
        } else if (registration.isLIMSAdmin() || workflowRunParam.givesPermission(registration)) {
            localLogger.info("insert workflow run param object");
            insert(workflowRunParam);
        } else {
            localLogger.error("WorkflowRunParamDAOHibernate insert is not authorized");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void insertFilesAsWorkflowRunParam(Registration registration, WorkflowRun workflowRun, Map<String, List<File>> files) {
        if (registration == null) {
            localLogger.error("WorkflowRunParamDAOHibernate insertFilesAsWorkflowRunParam registration is null");
        } else if (registration.isLIMSAdmin() || workflowRun.givesPermission(registration)) {
            localLogger.info("insertFilesAsWorkflowRunParam workflow run param object");
            insertFilesAsWorkflowRunParam(workflowRun, files);
        } else {
            localLogger.error("WorkflowRunParamDAOHibernate insertFilesAsWorkflowRunParam is not authorized");
        }
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowRunParam updateDetached(Registration registration, WorkflowRunParam workflowRunParam) {
        // Should probably grab an instance of this object from the database, but
        // clearly this method isn't being used at the moment
        if (registration == null) {
            localLogger.error("WorkflowRunParamDAOHibernate updateDetached registration is null");
        } else if (registration.isLIMSAdmin() || workflowRunParam.givesPermission(registration)) {
            localLogger.info("updateDetached workflow run param object");
            return updateDetached(workflowRunParam);
        } else {
            localLogger.error("WorkflowRunParamDAOHibernate updateDetached is not authorized");
        }
        return null;
    }
}
