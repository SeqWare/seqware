package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.InvoiceAttributeFacadeREST;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 * 
 * @author dyuen
 */
@Stateless
@Path("io.seqware.webservice.model.invoiceattribute")
public class CustomInvoiceAttributeFacadeREST extends InvoiceAttributeFacadeREST {
}
