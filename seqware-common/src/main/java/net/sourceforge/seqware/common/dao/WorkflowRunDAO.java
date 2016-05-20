package net.sourceforge.seqware.common.dao;

import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * WorkflowRunDAO interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface WorkflowRunDAO {

    /**
     * Inserts a new WorkflowRun and returns its sw_accession number.
     * 
     * @param workflowRun
     *            Workflow run to be inserted.
     * @return The SeqWare Accession number for the newly inserted workflow.
     */
    Integer insert(WorkflowRun workflowRun);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowRun
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     * @return a {@link java.lang.Integer} object.
     */
    Integer insert(Registration registration, WorkflowRun workflowRun);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param workflowRun
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    void update(WorkflowRun workflowRun);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowRun
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    void update(Registration registration, WorkflowRun workflowRun);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param workflowRun
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    void delete(WorkflowRun workflowRun);

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
    void update(WorkflowRun workflowRun, List<Integer> laneIds);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<WorkflowRun> list();

    // public List<WorkflowRun> list(Registration registration);
    // public List<WorkflowRun> listMyShared(Registration registration);
    // public List<WorkflowRun> listSharedWithMe(Registration registration);
    // public List<WorkflowRun> listRunning(Registration registration);
    /**
     * <p>
     * list.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param isAsc
     *            a {@link java.lang.Boolean} object.
     * @return a {@link java.util.List} object.
     */
    List<WorkflowRun> list(Registration registration, Boolean isAsc);

    /**
     * <p>
     * listMyShared.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param isAsc
     *            a {@link java.lang.Boolean} object.
     * @return a {@link java.util.List} object.
     */
    List<WorkflowRun> listMyShared(Registration registration, Boolean isAsc);

    /**
     * <p>
     * listSharedWithMe.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param isAsc
     *            a {@link java.lang.Boolean} object.
     * @return a {@link java.util.List} object.
     */
    List<WorkflowRun> listSharedWithMe(Registration registration, Boolean isAsc);

    /**
     * <p>
     * listRunning.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param isAsc
     *            a {@link java.lang.Boolean} object.
     * @return a {@link java.util.List} object.
     */
    List<WorkflowRun> listRunning(Registration registration, Boolean isAsc);

    /**
     * <p>
     * findByName.
     * </p>
     * 
     * @param name
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    WorkflowRun findByName(String name);

    /**
     * <p>
     * findByID.
     * </p>
     * 
     * @param wfrID
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    WorkflowRun findByID(Integer wfrID);

    /**
     * <p>
     * findBySWAccession.
     * </p>
     * 
     * @param swAccession
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    WorkflowRun findBySWAccession(Integer swAccession);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param workflowRun
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    WorkflowRun updateDetached(WorkflowRun workflowRun);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowRun
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    WorkflowRun updateDetached(Registration registration, WorkflowRun workflowRun);

    /**
     * <p>
     * findByOwnerID.
     * </p>
     * 
     * @param registrationID
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    List<WorkflowRun> findByOwnerID(Integer registrationID);

    /**
     * <p>
     * findByCriteria.
     * </p>
     * 
     * @param criteria
     *            a {@link java.lang.String} object.
     * @param isCaseSens
     *            a boolean.
     * @return a {@link java.util.List} object.
     */
    List<WorkflowRun> findByCriteria(String criteria, boolean isCaseSens);

    /**
     * <p>
     * findByCriteria.
     * </p>
     * 
     * @param criteria
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    List<WorkflowRun> findByCriteria(String criteria);

    /**
     * <p>
     * listRelatedWorkflows.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link java.util.List} object.
     */
    List<Workflow> listRelatedWorkflows(Registration registration);

    /**
     * <p>
     * findRunsForIUS.
     * </p>
     * 
     * @param ius
     *            a {@link net.sourceforge.seqware.common.model.IUS} object.
     * @return a {@link java.util.Set} object.
     */
    Set<WorkflowRun> findRunsForIUS(IUS ius);
}
