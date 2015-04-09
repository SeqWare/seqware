/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.SampleFacadeREST;
import io.seqware.webservice.generated.model.Sample;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.sun.jersey.api.ConflictException;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * 
 * @author dyuen
 */
@Stateless
@Api(value="/io.seqware.webservice.model.sample")
@Path("io.seqware.webservice.model.sample")
public class CustomSampleFacadeREST extends SampleFacadeREST {
    
    /**
     * Modify the listed sample to create a sample_hierarchy relation with a
     * null parent
     *
     * @param id
     * @return 
     */
    @ApiOperation(value="Create a null hierarchy of samples")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PUT
    @Path("{id}/createNullHierarchy")
    @Consumes({"application/xml", "application/json"})
    public void createNullHierarchy(@PathParam("id") Integer id) {
        // verify that the entity exists
        Sample find = super.find(id);
        // verify that no null relation already exists
        Query createSelectNullParentCountQuery = Sample.selectNullParentCountQuery(super.getEntityManager(), id);
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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @DELETE
    @Path("{id}/removeNullHierarchy")
    @Consumes({ "application/xml", "application/json" })
    public void removeNullHierarchy(@PathParam("id") Integer id) {
        // verify that the entity exists
        Sample find = super.find(id);
        // verify that a null relation already exists
        Query removeSelectNullParentCountQuery = Sample.selectNullParentCountQuery(super.getEntityManager(), id);
        Long count = (Long) removeSelectNullParentCountQuery.getSingleResult();
        // remove the null relationship
        if (count == 1) {
            int executeUpdate = super.getEntityManager()
                    .createNativeQuery("DELETE FROM sample_hierarchy sh WHERE sh.sample_id=" + id + " AND sh.parent_id IS NULL;")
                    .executeUpdate();
            if (executeUpdate == 1) {
                return;
            }
            throw new ConflictException("could not remove null parent hierarchy relationship");
        }
    }
}
