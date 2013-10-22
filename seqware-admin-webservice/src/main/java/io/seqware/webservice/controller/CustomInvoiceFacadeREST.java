package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.InvoiceFacadeREST;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 *
 * @author dyuen
 */
@Stateless
@Path("io.seqware.webservice.model.invoice")
public class CustomInvoiceFacadeREST extends InvoiceFacadeREST {
}
