/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.controller;

import java.util.Collection;

import io.seqware.webservice.generated.controller.LaneFacadeREST;
import io.seqware.webservice.generated.model.Lane;
import io.seqware.webservice.generated.model.LaneAttribute;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * 
 * @author dyuen
 */
@Stateless
@Path("io.seqware.webservice.model.lane")
public class CustomLaneFacadeREST extends LaneFacadeREST {

    @Path("/createLane")
    @POST
    @Consumes({ javax.ws.rs.core.MediaType.APPLICATION_JSON, javax.ws.rs.core.MediaType.APPLICATION_XML })
    @Produces({ javax.ws.rs.core.MediaType.APPLICATION_XML })
    public Lane createLane(Lane lane) {
        try {
            super.create(lane);
            persistAttributes(lane.getLaneAttributeCollection(), lane, this.getEntityManager());
        } catch (ConstraintViolationException e) {
            handleConstraintViolation(e);
        }
        return lane;
    }

    private void persistAttributes(Collection<LaneAttribute> attributes, Lane parent, EntityManager em) {
        for (LaneAttribute attribute : attributes) {
            attribute.setLaneId(parent);
            em.persist(attribute);
        }
    }

    private void handleConstraintViolation(ConstraintViolationException e) {
        System.out.println("ContraintViolations detected: " + e.getMessage());
        for (ConstraintViolation<?> e1 : e.getConstraintViolations()) {
            System.out.println(e1.getInvalidValue());
            System.out.println(e1.getMessage());
            System.out.println(e1);
            System.out.println(e1.getConstraintDescriptor());
            System.out.println(e1.getPropertyPath());
        }
    }
}
