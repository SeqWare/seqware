package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.ExpenseFacadeREST;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 * 
 * @author dyuen
 */
@Stateless
@Path("io.seqware.webservice.model.expense")
public class CustomExpenseFacadeREST extends ExpenseFacadeREST {
}
