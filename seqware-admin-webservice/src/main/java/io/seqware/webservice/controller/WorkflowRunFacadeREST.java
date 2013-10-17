/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.controller;

import io.seqware.webservice.model.File;
import io.seqware.webservice.model.Processing;
import io.seqware.webservice.model.ProcessingFiles;
import io.seqware.webservice.model.WorkflowRun;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author
 * boconnor
 */
@Stateless
@Path("io.seqware.webservice.model.workflowrun")
public class WorkflowRunFacadeREST extends AbstractFacade<WorkflowRun> {
  @PersistenceContext(unitName = "io.seqware_seqware-admin-webservice_war_1.0-SNAPSHOTPU")
  private EntityManager em;

  public WorkflowRunFacadeREST() {
    super(WorkflowRun.class);
  }

  @POST
  @Override
  @Consumes({"application/xml", "application/json"})
  public void create(WorkflowRun entity) {
    super.create(entity);
  }

  @PUT
  @Override
  @Consumes({"application/xml", "application/json"})
  public void edit(WorkflowRun entity) {
    super.edit(entity);
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") Integer id) {
    super.remove(super.find(id));
  }

  @GET
  @Path("{id}")
  @Produces({"application/xml", "application/json"})
  public WorkflowRun find(@PathParam("id") Integer id) {
    return super.find(id);
  }

  @GET
  @Override
  @Produces({"application/xml", "application/json"})
  public List<WorkflowRun> findAll() {
    return super.findAll();
  }

  @GET
  @Path("{from}/{to}")
  @Produces({"application/xml", "application/json"})
  public List<WorkflowRun> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
    return super.findRange(new int[]{from, to});
  }

  @GET
  @Path("count")
  @Produces("text/plain")
  public String countREST() {
    return String.valueOf(super.count());
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }
  
  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  @DELETE
  @Path("{id}/rdelete")
  @Consumes({"application/json"})
  public void deleteRecursive(@PathParam("id") Integer id, List<ModelAccessionIDTuple> victims) throws NamingException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException, ClassNotFoundException {
      EntityManager entityManager = getEntityManager();
      for(ModelAccessionIDTuple t : victims){
          Object found = entityManager.find(Class.forName(t.getAdminModelClass()), t.getId());
          entityManager.remove(found);
      }
      WorkflowRun find = entityManager.find(WorkflowRun.class, id);
      entityManager.remove(find);
  }

  
  /**
   * Returns the list of potential victims for a deletion operation
   * @param id
   * @return 
   */
    @GET
    @Path("{id}/rdelete")
    @Produces({"application/json"})
    public List<ModelAccessionIDTuple> findRecursive(@PathParam("id") Integer id) {
        List<ModelAccessionIDTuple> results = new ArrayList<ModelAccessionIDTuple>();
        WorkflowRun data = getEntityManager().find(WorkflowRun.class, id);
        Set<Processing> affectedProcessing = new HashSet<Processing>();
        // workflow_run
        if (data.getProcessingCollection() != null) {
            affectedProcessing.addAll(data.getProcessingCollection());
        }
        // ancestor_workflow_run
        if (data.getProcessingCollection1() != null) {
            affectedProcessing.addAll(data.getProcessingCollection1());
        }
        Set<File> affectedFile = new HashSet<File>();
        for (Processing p : affectedProcessing) {
            Collection<ProcessingFiles> processingFilesCollection = p.getProcessingFilesCollection();
            if (processingFilesCollection == null) {
                continue;
            }
            for (ProcessingFiles pf : processingFilesCollection) {
                affectedFile.add(pf.getFileId());
            }
        }
        // list all affected resources
        for (Processing p : affectedProcessing) {
            results.add(new ModelAccessionIDTuple(p.getSwAccession(), p.getProcessingId(), p.getClass().getName()));
        }
        for (File f : affectedFile) {
            results.add(new ModelAccessionIDTuple(f.getSwAccession(), f.getFileId(), f.getClass().getName()));
        }
        return results;
    }
  
}
