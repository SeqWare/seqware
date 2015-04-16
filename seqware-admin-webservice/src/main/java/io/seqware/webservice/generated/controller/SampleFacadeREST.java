/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.generated.controller;

import io.seqware.webservice.generated.model.Sample;

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

import org.apache.log4j.Logger;

import com.wordnik.swagger.annotations.ApiOperation;

/**
 * 
 * @author boconnor
 */
@Stateless
@Path("io.seqware.webservice.model.sample")
public class SampleFacadeREST extends AbstractFacade<Sample> {
    @PersistenceContext(unitName = "io.seqware_seqware-admin-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public SampleFacadeREST() {
        super(Sample.class);
    }

    @POST
    @Override
    @Consumes({ "application/xml", "application/json" })
    public void create(Sample entity) {
        super.create(entity);
    }

    @PUT
    @Override
    @Consumes({ "application/xml", "application/json" })
    public void edit(Sample entity) {
    Logger.getLogger(Sample.class).info("XML output for @Put");
    if (Logger.getLogger(Sample.class).isDebugEnabled()){
        entity.toXml();
    }
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
    public Sample find(@PathParam("id") Integer id) {
      Sample find = super.find(id);
      if (Logger.getLogger(Sample.class).isDebugEnabled()){
        Logger.getLogger(Sample.class).info("XML output for @Get");
        find.toXml();
      }
      return find;
    }

    @GET
    @Override
    @Produces({ "application/xml", "application/json" })
    public List<Sample> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({ "application/xml", "application/json" })
    public List<Sample> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
