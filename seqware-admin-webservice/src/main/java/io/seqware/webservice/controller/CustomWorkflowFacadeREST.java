/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.WorkflowFacadeREST;

import javax.ejb.Stateless;
import javax.ws.rs.Path;

import com.wordnik.swagger.annotations.Api;

/**
 * 
 * @author dyuen
 */
@Stateless
@Api(value = "/io.seqware.webservice.model.workflow", description="Operations on Workflows")
@Path("io.seqware.webservice.model.workflow")
public class CustomWorkflowFacadeREST extends WorkflowFacadeREST {
}
