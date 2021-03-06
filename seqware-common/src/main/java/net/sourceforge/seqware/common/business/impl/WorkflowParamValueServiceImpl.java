package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.WorkflowParamValueService;
import net.sourceforge.seqware.common.dao.WorkflowParamValueDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowParamValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * WorkflowParamValueServiceImpl class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowParamValueServiceImpl implements WorkflowParamValueService {
    private WorkflowParamValueDAO workflowParamValueDAO = null;
    private static final Log LOG = LogFactory.getLog(WorkflowParamValueServiceImpl.class);

    /**
     * <p>
     * Constructor for WorkflowParamValueServiceImpl.
     * </p>
     */
    public WorkflowParamValueServiceImpl() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public void setWorkflowParamValueDAO(WorkflowParamValueDAO workflowParamValueDAO) {
        this.workflowParamValueDAO = workflowParamValueDAO;
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(WorkflowParamValue workflowParamValue) {
        return workflowParamValueDAO.insert(workflowParamValue);
    }

    /** {@inheritDoc} */
    @Override
    public void update(WorkflowParamValue workflowParamValue) {
        workflowParamValueDAO.update(workflowParamValue);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(WorkflowParamValue workflowParamValue) {
        workflowParamValueDAO.delete(workflowParamValue);
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowParamValue findByID(Integer id) {
        WorkflowParamValue workflowParamValue = null;
        if (id != null) {
            try {
                workflowParamValue = workflowParamValueDAO.findByID(id);
            } catch (Exception exception) {
                LOG.error("Cannot find Lane by Workflow Param ID " + id);
                LOG.error(exception.getMessage());
            }
        }
        return workflowParamValue;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowParamValue updateDetached(WorkflowParamValue workflowParamValue) {
        return workflowParamValueDAO.updateDetached(workflowParamValue);
    }

    /** {@inheritDoc} */
    @Override
    public List<WorkflowParamValue> list() {
        return workflowParamValueDAO.list();
    }

    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, WorkflowParamValue workflowParamValue) {
        workflowParamValueDAO.update(registration, workflowParamValue);
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(Registration registration, WorkflowParamValue workflowParamValue) {
        return workflowParamValueDAO.insert(registration, workflowParamValue);
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowParamValue updateDetached(Registration registration, WorkflowParamValue workflowParamValue) {
        return workflowParamValueDAO.updateDetached(registration, workflowParamValue);
    }
}
