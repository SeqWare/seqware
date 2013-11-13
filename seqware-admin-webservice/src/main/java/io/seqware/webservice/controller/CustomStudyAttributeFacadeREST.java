/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.StudyAttributeFacadeREST;
import io.seqware.webservice.generated.controller.StudyFacadeREST;
import io.seqware.webservice.generated.model.StudyAttribute;

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
@Path("io.seqware.webservice.model.studyattribute")
public class CustomStudyAttributeFacadeREST extends StudyAttributeFacadeREST {

   @PersistenceContext(unitName = "io.seqware_seqware-admin-webservice_war_1.0-SNAPSHOTPU")
   private EntityManager em;

   @EJB
   private StudyFacadeREST studyFacadeRest;

   /**
    * Create a new study and a new study attribute to associate with it in a
    * single step. Both steps occur in a single transaction.
    * 
    * @param entity
    *           The study attribute with nested study.
    */
   @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
   @POST
   @Path("withstudy")
   @Consumes({ "application/json", "application/xml" })
   public void createWithStudy(StudyAttribute entity) {
      System.out.println("Study id should be null " + entity.getStudyId().getStudyId());
      studyFacadeRest.create(entity.getStudyId());
      em.flush();
      System.out.println("Study id should have value " + entity.getStudyId().getStudyId());
      super.create(entity);
   }
}
