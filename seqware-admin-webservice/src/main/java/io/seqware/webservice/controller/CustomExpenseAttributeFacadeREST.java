package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.*;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 *
 * @author dyuen
 */
@Stateless
@Path("io.seqware.webservice.model.expenseattribute")
public class CustomExpenseAttributeFacadeREST extends ExpenseAttributeFacadeREST {
}
