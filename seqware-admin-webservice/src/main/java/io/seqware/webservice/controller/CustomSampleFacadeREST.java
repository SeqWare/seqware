/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.controller;

import com.sun.jersey.api.ConflictException;
import com.sun.jersey.api.NotFoundException;
import io.seqware.webservice.generated.controller.*;
import io.seqware.webservice.generated.model.Sample;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author dyuen
 */
@Stateless
@Path("io.seqware.webservice.model.sample")
public class CustomSampleFacadeREST extends SampleFacadeREST {
    
    /**
     * Modify the listed sample to create a sample_hierarchy relation with a
     * null parent
     *
     * @param id
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PUT
    @Path("{id}/createNullHierarchy")
    @Consumes({"application/xml", "application/json"})
    public void createNullHierarchy(@PathParam("id") Integer id) {
        // verify that the entity exists
        Sample find = super.find(id);
        // verify that no null relation already exists
        Query createSelectNullParentCountQuery = Sample.createSelectNullParentCountQuery(super.getEntityManager(), id);
        Long count = (Long)createSelectNullParentCountQuery.getSingleResult();
        // create a null relationship
        if (count == 0){
            int executeUpdate = super.getEntityManager().createNativeQuery("insert into sample_hierarchy values ("+id+", null);").executeUpdate();
            if (executeUpdate == 1){
                return;
            }
            throw new ConflictException("could not create null parent hierarchy relationship");
        } 
    }
}
