/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.generated.controller;

import io.seqware.webservice.generated.model.StudyType;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.wordnik.swagger.annotations.ApiOperation;

/**
 * 
 * @author boconnor
 */
@Stateless
@Path("io.seqware.webservice.model.studytype")
public class StudyTypeFacadeREST extends AbstractFacade<StudyType> {
    @PersistenceContext(unitName = "io.seqware_seqware-admin-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public StudyTypeFacadeREST() {
        super(StudyType.class);
    }

    @POST
    @Override
    @Consumes({ "application/xml", "application/json" })
    public void create(StudyType entity) {
        super.create(entity);
    }

    @PUT
    @Override
    @Consumes({ "application/xml", "application/json" })
    public void edit(StudyType entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({ "application/xml", "application/json" })
    @ApiOperation(value="Find an entity by its primary key.",hidden=false, tags={"Generic find by ID"})
    public StudyType find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({ "application/xml", "application/json" })
    public List<StudyType> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({ "application/xml", "application/json" })
    public List<StudyType> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[] { from, to });
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

}
