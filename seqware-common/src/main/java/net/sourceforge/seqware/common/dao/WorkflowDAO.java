package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Workflow;

public interface WorkflowDAO {

  /**
   * Inserts a new Workflow and returns its sw_accession number.
   * 
   * @param workflow
   *          Workflow to be inserted.
   * @return The SeqWare Accession number for the newly inserted workflow.
   */
  public Integer insert(Workflow workflow);

  public Integer insert(Registration registration, Workflow workflow);

  public void update(Workflow workflow);

  public void update(Registration registration, Workflow workflow);

  public void delete(Workflow workflow);

  public List<Workflow> list();

  public List<Workflow> list(Registration registration);

  public List<Workflow> listMyShared(Registration registration);

  public List<Workflow> listSharedWithMe(Registration registration);

  public List<Workflow> findByName(String name);

  public Workflow findByID(Integer wfID);

  public Workflow findBySWAccession(Integer swAccession);

  public Workflow updateDetached(Workflow workflow);
    
    public Workflow updateDetached(Registration registration, Workflow workflow);

  public List<Workflow> findByCriteria(String criteria, boolean isCaseSens);

  public List<Workflow> listWorkflows(SequencerRun sr);
}
