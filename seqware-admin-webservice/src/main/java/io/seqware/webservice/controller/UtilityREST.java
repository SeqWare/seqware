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

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.client.UniformInterfaceException;
import io.seqware.webservice.generated.model.Experiment;
import io.seqware.webservice.generated.model.Ius;
import io.seqware.webservice.generated.model.Lane;
import io.seqware.webservice.generated.model.Processing;
import io.seqware.webservice.generated.model.Sample;
import io.seqware.webservice.generated.model.SequencerRun;
import io.seqware.webservice.generated.model.Study;
import io.seqware.webservice.generated.model.WorkflowRun;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author dyuen
 */
@Stateless
@Path("utility")
public class UtilityREST {

    @PersistenceContext(unitName = "io.seqware_seqware-admin-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    /**
     * Returns a tuple describing the class and accession given only an
     * accession
     *
     * @param accession
     * @return
     */
    @GET
    @Path("{accession}")
    @Produces({"application/json"})
    public ModelAccessionIDTuple find(@PathParam("accession") Integer accession) {
        Object target;
        try {
            target = em.createQuery("select wr from WorkflowRun wr WHERE wr.swAccession = " + accession, WorkflowRun.class).getSingleResult();
            if (target != null) {
                return new ModelAccessionIDTuple(accession, ((WorkflowRun) target).getWorkflowRunId(), target.getClass().getName());
            }
        } catch (NoResultException ex) {
            /**
             * ignore, does the JPA API really have no way of checking whether a
             * result is available except by exception?
             */
        }
        try {
            target = em.createQuery("select e from Experiment e WHERE e.swAccession = " + accession, Experiment.class).getSingleResult();
            if (target != null) {
                return new ModelAccessionIDTuple(accession, ((Experiment) target).getExperimentId(), target.getClass().getName());
            }
        } catch (NoResultException ex) {
        }
        try {
            target = em.createQuery("select i from Ius i WHERE i.swAccession = " + accession, Ius.class).getSingleResult();
            if (target != null) {
                return new ModelAccessionIDTuple(accession, ((Ius) target).getIusId(), target.getClass().getName());
            }
        } catch (NoResultException ex) {
        }
        try {
            target = em.createQuery("select l from Lane l WHERE l.swAccession = " + accession, Lane.class).getSingleResult();
            if (target != null) {
                return new ModelAccessionIDTuple(accession, ((Lane) target).getLaneId(), target.getClass().getName());
            }
        } catch (NoResultException ex) {
        }
        try {
            target = em.createQuery("select p from Processing p WHERE p.swAccession = " + accession, Processing.class).getSingleResult();
            if (target != null) {
                return new ModelAccessionIDTuple(accession, ((Processing) target).getProcessingId(), target.getClass().getName());
            }
        } catch (NoResultException ex) {
        }
        try {
            target = em.createQuery("select s from Sample s WHERE s.swAccession = " + accession, Sample.class).getSingleResult();
            if (target != null) {
                return new ModelAccessionIDTuple(accession, ((Sample) target).getSampleId(), target.getClass().getName());
            }
        } catch (NoResultException ex) {
        }
        try {
            target = em.createQuery("select sr from SequencerRun sr WHERE sr.swAccession = " + accession, SequencerRun.class).getSingleResult();
            if (target != null) {
                return new ModelAccessionIDTuple(accession, ((SequencerRun) target).getSequencerRunId(), target.getClass().getName());
            }
        } catch (NoResultException ex) {
        }
        try {
            target = em.createQuery("select s from Study s WHERE s.swAccession = " + accession, Study.class).getSingleResult();
            if (target != null) {
                return new ModelAccessionIDTuple(accession, ((Study) target).getStudyId(), target.getClass().getName());
            }
        } catch (NoResultException ex) {
        }

        throw new NotFoundException();
    }
}
