package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.ExpenseFacadeREST;

import javax.ejb.Stateless;
import javax.ws.rs.Path;

import com.wordnik.swagger.annotations.Api;

/**
 * 
 * @author dyuen
 */
@Api(value="/io.seqware.webservice.model.expense")
@Stateless
@Path("io.seqware.webservice.model.expense")
public class CustomExpenseFacadeREST extends ExpenseFacadeREST {
}
