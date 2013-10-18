/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.controller;

import io.seqware.webservice.model.File;
import io.seqware.webservice.model.Processing;
import io.seqware.webservice.model.ProcessingFiles;
import io.seqware.webservice.model.ProcessingRelationship;
import io.seqware.webservice.model.WorkflowRun;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
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
  
  /**
   * The actual delete method, container managed JTA transactions should handle rollback and atomic operations
   * @param id
   * @param victims
   * @throws NamingException
   * @throws NotSupportedException
   * @throws SystemException
   * @throws RollbackException
   * @throws HeuristicMixedException
   * @throws HeuristicRollbackException
   * @throws ClassNotFoundException 
   */
  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  @DELETE
  @Path("{id}/rdelete")
  @Consumes({"application/json"})
  public void deleteRecursive(@PathParam("id") Integer id, Set<ModelAccessionIDTuple> victims) throws NamingException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException, ClassNotFoundException {
      deleteWorkflowRunRecursive(id, true, victims);
  }

  
  /**
   * Returns the list of potential victims for a deletion operation
   * @param id
   * @return 
   */
    @GET
    @Path("{id}/rdelete")
    @Produces({"application/json"})
    public Set<ModelAccessionIDTuple> findRecursive(@PathParam("id") Integer id) {
        return deleteWorkflowRunRecursive(id, false, null);
    }

    /**
     * Method that starts from a workflow run, either deleting appropriate
     * entities or reporting on what would have been deleted
     *
     * @param id primary key id for the workflow run
     * @param delete whether to actually go through with deletion
     * @param matchSet when actually doing deletion, this set should be a
     * superset of what we find
     * @return
     */
    protected Set<ModelAccessionIDTuple> deleteWorkflowRunRecursive(Integer id, boolean delete, Set<ModelAccessionIDTuple> matchSet) {
        EntityManager entityManager = getEntityManager();
        Set<ModelAccessionIDTuple> results = new HashSet<ModelAccessionIDTuple>();
        WorkflowRun data = entityManager.find(WorkflowRun.class, id);
        if (data == null){
            return null;
        }
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
            // look for child workflow runs and handle them recursively
            if (p.getProcessingRelationshipCollection() != null){
                for(ProcessingRelationship childpr : p.getProcessingRelationshipCollection()){
                    Processing childp = childpr.getChildId();
                    // if the child processing is connected to a workflow run that is not this one, recursive
                    WorkflowRun childRun = childp.getAncestorWorkflowRunId();
                    if (childRun == null){ childRun = childp.getWorkflowRunId(); }
                    if (!childRun.equals(data)){
                        Set<ModelAccessionIDTuple> recursiveSet = this.deleteWorkflowRunRecursive(childRun.getWorkflowRunId(), delete, matchSet);
                        if (recursiveSet != null) {
                            results.addAll(recursiveSet);
                        }
                    }
                }
                handleProcessingRelationshipCollection(p.getProcessingRelationshipCollection(), results); 
                handleProcessingRelationshipCollection(p.getProcessingRelationshipCollection1(), results);          
            }
            
            Collection<ProcessingFiles> processingFilesCollection = p.getProcessingFilesCollection();
            if (processingFilesCollection == null) {
                continue;
            }
            for (ProcessingFiles pf : processingFilesCollection) {
                affectedFile.add(pf.getFileId());
            }            
        }
        // list all affected resources (and delete them if required)
        for (Processing p : affectedProcessing) {
            results.add(new ModelAccessionIDTuple(p.getSwAccession(), p.getProcessingId(), p.getClass().getName()));
        }
        for (File f : affectedFile) {
            results.add(new ModelAccessionIDTuple(f.getSwAccession(), f.getFileId(), f.getClass().getName()));
        }
        // handle the workflow run itself
        results.add(new ModelAccessionIDTuple(data.getSwAccession(), data.getWorkflowRunId(), data.getClass().getName()));
        // if we are doing deletion, check that everything is ok
        try {
            if (delete) {
                // check that the results are a subset of the matchset
                if (matchSet.containsAll(results)) {
                    for (ModelAccessionIDTuple t : results) {
                        Object found = entityManager.find(Class.forName(t.getAdminModelClass()), t.getId());
                        // entity may already have been removed by recursive call
                        if (found != null){
                            entityManager.remove(found);
                        }
                    }
                } else{
                    results.removeAll(matchSet);
                    throw new RuntimeException("Found " + results.size() + " elements that were not in the match set");
                }
            }
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("ClassNotFound", ex);
        }
        return results;
    }

    private void handleProcessingRelationshipCollection(Collection<ProcessingRelationship> col, Set<ModelAccessionIDTuple> results) {
        // add the actual relationships themselves to the results
        if (col != null){
            for(ProcessingRelationship pr : col){
                results.add(new ModelAccessionIDTuple(-1, pr.getProcessingRelationshipId(), pr.getClass().getName()));
            }
        }
    }
  
}
