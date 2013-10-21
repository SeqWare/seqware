/*
 * Copyright (C) 2013 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.WorkflowRunFacadeREST;
import io.seqware.webservice.generated.model.File;
import io.seqware.webservice.generated.model.Ius;
import io.seqware.webservice.generated.model.IusWorkflowRuns;
import io.seqware.webservice.generated.model.Lane;
import io.seqware.webservice.generated.model.Processing;
import io.seqware.webservice.generated.model.ProcessingFiles;
import io.seqware.webservice.generated.model.ProcessingIus;
import io.seqware.webservice.generated.model.ProcessingRelationship;
import io.seqware.webservice.generated.model.SequencerRun;
import io.seqware.webservice.generated.model.WorkflowRun;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author dyuen
 */
@Stateless
@Path("io.seqware.webservice.model.workflowrun")
public class CustomWorkflowRunFacadeREST extends WorkflowRunFacadeREST {

    /**
     * The actual delete method, container managed JTA transactions should
     * handle rollback and atomic operations
     *
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
    @Path("{id}/rdelete/{targetClass}")
    @Consumes({"application/json"})
    public void deleteRecursive(@PathParam("id") Integer id, Set<ModelAccessionIDTuple> victims, @PathParam("targetClass") String targetClass) throws NamingException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException, ClassNotFoundException {
        handleTargetting(targetClass, id, true, victims);
    }

    /**
     * Returns the list of potential victims for a deletion operation
     *
     * @param id
     * @return
     */
    @GET
    @Path("{id}/rdelete/{targetClass}")
    @Produces({"application/json"})
    public Set<ModelAccessionIDTuple> findRecursive(@PathParam("id") Integer id, @PathParam("targetClass") String targetClass) {
        return handleTargetting(targetClass, id, false, null);
    }

    /**
     * Method that starts from a SequencerRun, either deleting appropriate
     * workflow runs or reporting on what would have been deleted
     *
     * @param id
     * @param delete
     * @param matchSet
     * @return
     */
    private Set<ModelAccessionIDTuple> deleteSequencerRunRecursive(Integer id, boolean delete, Set<ModelAccessionIDTuple> matchSet) {
        SequencerRun data = getEntityManager().find(SequencerRun.class, id);
        if (data == null) {
            return null;
        }
        Set<ModelAccessionIDTuple> results = new HashSet<ModelAccessionIDTuple>();
        if (data.getLaneCollection() != null) {
            for (Lane l : data.getLaneCollection()) {
                Set<ModelAccessionIDTuple> recursiveSet = this.deleteLaneRecursive(l.getLaneId(), delete, matchSet);
                if (recursiveSet != null) {
                    results.addAll(recursiveSet);
                }
            }
        }
        return results;
    }

    /**
     * Method that starts from a lane, either deleting appropriate workflow runs
     * or reporting on what would have been deleted
     *
     * @param id
     * @param delete
     * @param matchSet
     * @return
     */
    private Set<ModelAccessionIDTuple> deleteLaneRecursive(Integer id, boolean delete, Set<ModelAccessionIDTuple> matchSet) {
        Lane data = getEntityManager().find(Lane.class, id);
        if (data == null) {
            return null;
        }
        Set<ModelAccessionIDTuple> results = new HashSet<ModelAccessionIDTuple>();
        if (data.getIusCollection() != null) {
            for (Ius i : data.getIusCollection()) {
                Set<ModelAccessionIDTuple> recursiveSet = this.deleteIUSRecursive(i.getIusId(), delete, matchSet);
                if (recursiveSet != null) {
                    results.addAll(recursiveSet);
                }
            }
        }
        return results;
    }

    /**
     * Method that starts from an ius, either deleting appropriate workflow runs
     * or reporting on what would have been deleted
     *
     * @param id
     * @param delete
     * @param matchSet
     * @return
     */
    private Set<ModelAccessionIDTuple> deleteIUSRecursive(Integer id, boolean delete, Set<ModelAccessionIDTuple> matchSet) {
        Ius data = getEntityManager().find(Ius.class, id);
        if (data == null) {
            return null;
        }
        Set<ModelAccessionIDTuple> results = new HashSet<ModelAccessionIDTuple>();
        if (data.getIusWorkflowRunsCollection() != null) {
            for (IusWorkflowRuns iwr : data.getIusWorkflowRunsCollection()) {
                WorkflowRun workflowRun = iwr.getWorkflowRunId();
                Set<ModelAccessionIDTuple> recursiveSet = this.deleteWorkflowRunRecursive(workflowRun.getWorkflowRunId(), delete, matchSet);
                if (recursiveSet != null) {
                    results.addAll(recursiveSet);
                }
            }
        }
        if (data.getProcessingIusCollection() != null) {
            for (ProcessingIus pi : data.getProcessingIusCollection()) {
                Processing childp = pi.getProcessingId();
                handleWorkflowRunGivenProcessing(childp, null, delete, matchSet, results);
            }
        }
        return results;
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
    private Set<ModelAccessionIDTuple> deleteWorkflowRunRecursive(Integer id, boolean delete, Set<ModelAccessionIDTuple> matchSet) {
        EntityManager entityManager = getEntityManager();
        Set<ModelAccessionIDTuple> results = new HashSet<ModelAccessionIDTuple>();
        WorkflowRun data = entityManager.find(WorkflowRun.class, id);
        if (data == null) {
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
            if (p.getProcessingRelationshipCollection() != null) {
                for (ProcessingRelationship childpr : p.getProcessingRelationshipCollection()) {
                    Processing childp = childpr.getChildId();
                    handleWorkflowRunGivenProcessing(childp, data, delete, matchSet, results);
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
                        if (found != null) {
                            entityManager.remove(found);
                        }
                    }
                } else {
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
        if (col != null) {
            for (ProcessingRelationship pr : col) {
                results.add(new ModelAccessionIDTuple(Integer.MAX_VALUE, pr.getProcessingRelationshipId(), pr.getClass().getName()));
            }
        }
    }

    private Set<ModelAccessionIDTuple> handleTargetting(String targetClass, Integer id, boolean delete, Set<ModelAccessionIDTuple> victims) {
        if (targetClass == null) {
            throw new RuntimeException("No targetClass specified");
        }
        if (targetClass.equals(WorkflowRun.class.getSimpleName())) {
            return deleteWorkflowRunRecursive(id, delete, victims);
        } else if (targetClass.equals(Ius.class.getSimpleName())) {
            return deleteIUSRecursive(id, delete, victims);
        } else if (targetClass.equals(SequencerRun.class.getSimpleName())) {
            return deleteSequencerRunRecursive(id, delete, victims);
        } else if (targetClass.equals(Lane.class.getSimpleName())) {
            return deleteLaneRecursive(id, delete, victims);
        } else {
            throw new RuntimeException("Unknown target class");
        }
    }

    private void handleWorkflowRunGivenProcessing(Processing childp, WorkflowRun data, boolean delete, Set<ModelAccessionIDTuple> matchSet, Set<ModelAccessionIDTuple> results) {
        // if the child processing is connected to a workflow run that is not this one, recursive
        WorkflowRun childRun = childp.getAncestorWorkflowRunId();
        if (childRun == null) {
            childRun = childp.getWorkflowRunId();
        }
        if (childRun != null && !childRun.equals(data)) {
            Set<ModelAccessionIDTuple> recursiveSet = this.deleteWorkflowRunRecursive(childRun.getWorkflowRunId(), delete, matchSet);
            if (recursiveSet != null) {
                results.addAll(recursiveSet);
            }
        }
    }
}
