/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.SequencerRunAttributeFacadeREST;
import io.seqware.webservice.generated.controller.SequencerRunFacadeREST;
import io.seqware.webservice.generated.model.SequencerRunAttribute;
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
@Path("io.seqware.webservice.model.sequencerrunattribute")
public class CustomSequencerRunAttributeFacadeREST extends SequencerRunAttributeFacadeREST {

    @PersistenceContext(unitName = "io.seqware_seqware-admin-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @EJB
    private SequencerRunFacadeREST sequencerRunFacadeRest;

    /**
     * Create a new sequencer run and a new sequencer run attribute to associate with it in a single step. Both steps occur in a single
     * transaction.
     * 
     * @param entity
     *            The sequencer run attribute with nested sequencer run.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @POST
    @Path("withbaseentity")
    @Consumes({ "application/json", "application/xml" })
    public void createWithBaseEntity(SequencerRunAttribute entity) {
        sequencerRunFacadeRest.create(entity.getSampleId());
        em.flush();
        super.create(entity);
    }
}
