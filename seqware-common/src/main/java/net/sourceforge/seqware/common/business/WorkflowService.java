package net.sourceforge.seqware.common.business;

import net.sourceforge.seqware.common.dao.WorkflowDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Workflow;

import java.util.List;

/**
 * <p>
 * WorkflowService interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface WorkflowService {

    /** Constant <code>NAME="WorkflowService"</code> */
    String NAME = "WorkflowService";

    /**
     * <p>
     * setWorkflowDAO.
     * </p>
     * 
     * @param workflowDAO
     *            a {@link net.sourceforge.seqware.common.dao.WorkflowDAO} object.
     */
    void setWorkflowDAO(WorkflowDAO workflowDAO);

    /**
     * Inserts a new Workflow and returns its sw_accession number.
     * 
     * @param workflow
     *            Workflow to be inserted.
     * @return The SeqWare Accession number for the newly inserted workflow.
     */
    Integer insert(Workflow workflow);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflow
     *            a {@link net.sourceforge.seqware.common.model.Workflow} object.
     * @return a {@link java.lang.Integer} object.
     */
    Integer insert(Registration registration, Workflow workflow);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param workflow
     *            a {@link net.sourceforge.seqware.common.model.Workflow} object.
     */
    void update(Workflow workflow);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflow
     *            a {@link net.sourceforge.seqware.common.model.Workflow} object.
     */
    void update(Registration registration, Workflow workflow);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param workflow
     *            a {@link net.sourceforge.seqware.common.model.Workflow} object.
     */
    void delete(Workflow workflow);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<Workflow> list();

    /**
     * <p>
     * list.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link java.util.List} object.
     */
    List<Workflow> list(Registration registration);

    /**
     * <p>
     * listMyShared.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link java.util.List} object.
     */
    List<Workflow> listMyShared(Registration registration);

    /**
     * <p>
     * listSharedWithMe.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link java.util.List} object.
     */
    List<Workflow> listSharedWithMe(Registration registration);

    /**
     * <p>
     * findByName.
     * </p>
     * 
     * @param name
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    List<Workflow> findByName(String name);

    /**
     * <p>
     * findByID.
     * </p>
     * 
     * @param wfID
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Workflow} object.
     */
    Workflow findByID(Integer wfID);

    /**
     * <p>
     * findByIDWithParams.
     * </p>
     * 
     * @param wfID
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Workflow} object.
     */
    Workflow findByIDWithParams(Integer wfID);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflow
     *            a {@link net.sourceforge.seqware.common.model.Workflow} object.
     * @return a {@link net.sourceforge.seqware.common.model.Workflow} object.
     */
    Workflow updateDetached(Registration registration, Workflow workflow);

    /**
     * <p>
     * findBySWAccession.
     * </p>
     * 
     * @param swAccession
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Workflow} object.
     */
    Workflow findBySWAccession(Integer swAccession);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param workflow
     *            a {@link net.sourceforge.seqware.common.model.Workflow} object.
     * @return a {@link net.sourceforge.seqware.common.model.Workflow} object.
     */
    Workflow updateDetached(Workflow workflow);

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
    List<Workflow> findByCriteria(String criteria, boolean isCaseSens);

    /**
     * <p>
     * listSequencerRunsWorkflows.
     * </p>
     * 
     * @param sr
     *            a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     * @return a {@link java.util.List} object.
     */
    List<Workflow> listSequencerRunsWorkflows(SequencerRun sr);
}

// ex:sw=4:ts=4:
