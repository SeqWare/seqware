/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.ExperimentSpotDesignReadSpecFacadeREST;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 * 
 * @author dyuen
 */
@Stateless
@Path("io.seqware.webservice.model.experimentspotdesignreadspec")
public class CustomExperimentSpotDesignReadSpecFacadeREST extends ExperimentSpotDesignReadSpecFacadeREST {
}
