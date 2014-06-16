package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.ExperimentAttributeFacadeREST;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 * 
 * @author dyuen
 */
@Stateless
@Path("io.seqware.webservice.model.experimentattribute")
public class CustomExperimentAttributeFacadeREST extends ExperimentAttributeFacadeREST {
}
