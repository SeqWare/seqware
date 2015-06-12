/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.ExperimentFacadeREST;
import io.seqware.webservice.generated.model.Experiment;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.wordnik.swagger.annotations.Api;

/**
 * 
 * @author dyuen
 */
@Api(value="/io.seqware.webservice.model.experiment")
@Stateless
@Path("io.seqware.webservice.model.experiment")
public class CustomExperimentFacadeREST extends ExperimentFacadeREST {
    
    @POST
    @Path("/createExperiment")
    @Consumes({ "application/xml", "application/json" })
    public Experiment createExperiment(Experiment entity) {
        super.create(entity);
        return entity;
    }
}
