/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.SampleAttributeFacadeREST;
import io.seqware.webservice.generated.controller.SampleFacadeREST;
import io.seqware.webservice.generated.model.SampleAttribute;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * 
 * @author dyuen
 */
@Stateless
@Path("io.seqware.webservice.model.sampleattribute")
public class CustomSampleAttributeFacadeREST extends SampleAttributeFacadeREST {

    @PersistenceContext(unitName = "io.seqware_seqware-admin-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @EJB
    private SampleFacadeREST sampleFacadeRest;

    /**
     * Create a new sample and a new sample attribute to associate with it in a single step. Both steps occur in a single transaction.
     * 
     * @param entity
     *            The sample attribute with nested sample.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @POST
    @Path("withbaseentity")
    @Consumes({ "application/json", "application/xml" })
    public void createWithBaseEntity(SampleAttribute entity) {
        sampleFacadeRest.create(entity.getSampleId());
        em.flush();
        super.create(entity);
    }
}
