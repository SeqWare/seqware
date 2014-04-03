package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.InvoiceService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.InvoiceState;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>OpenInvoiceController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class OpenInvoiceController  extends BaseCommandController {

	public InvoiceService invoiceService;
	
	/**
	 * <p>Constructor for OpenInvoiceController.</p>
	 */
	public OpenInvoiceController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}
        
	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest	 request, HttpServletResponse response)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();

		model.put("pendingInvoiceList", getInvoiceService().list(registration, InvoiceState.pending));
                model.put("openInvoiceList", getInvoiceService().list(registration, InvoiceState.open));
                model.put("closedInvoiceList", getInvoiceService().list(registration, InvoiceState.closed));

		modelAndView = new ModelAndView("OpenInvoice", model);
		
		return modelAndView;
	}
        
        

	/**
	 * <p>Getter for the field <code>invoiceService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.InvoiceService} object.
	 */
	public InvoiceService getInvoiceService() {
		return invoiceService;
	}

	/**
	 * <p>Setter for the field <code>invoiceService</code>.</p>
	 *
	 * @param invoiceService a {@link net.sourceforge.seqware.common.business.InvoiceService} object.
	 */
	public void setInvoiceService(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}
}
