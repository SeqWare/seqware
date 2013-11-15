/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.LaneAttributeFacadeREST;
import io.seqware.webservice.generated.controller.LaneFacadeREST;
import io.seqware.webservice.generated.model.LaneAttribute;

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
@Path("io.seqware.webservice.model.laneattribute")
public class CustomLaneAttributeFacadeREST extends LaneAttributeFacadeREST {

   @PersistenceContext(unitName = "io.seqware_seqware-admin-webservice_war_1.0-SNAPSHOTPU")
   private EntityManager em;

   @EJB
   private LaneFacadeREST laneFacadeRest;

   /**
    * Create a new lane and a new lane attribute to associate with it in a
    * single step. Both steps occur in a single transaction.
    * 
    * @param entity
    *           The lane attribute with nested lane.
    */
   @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
   @POST
   @Path("withbaseentity")
   @Consumes({ "application/json", "application/xml" })
   public void createWithBaseEntity(LaneAttribute entity) {
      laneFacadeRest.create(entity.getLaneId());
      em.flush();
      super.create(entity);
   }
}
