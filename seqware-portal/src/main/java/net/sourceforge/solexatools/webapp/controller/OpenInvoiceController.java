package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sourceforge.seqware.common.business.InvoiceService;

import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

public class OpenInvoiceController  extends BaseCommandController {

	public InvoiceService invoiceService;
	
	public OpenInvoiceController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}
        
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest	 request, HttpServletResponse response)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();

		model.put("pendingInvoiceList", getInvoiceService().list(registration, "pending"));
                model.put("openInvoiceList", getInvoiceService().list(registration, "open"));
                model.put("closedInvoiceList", getInvoiceService().list(registration, "closed"));

		modelAndView = new ModelAndView("OpenInvoice", model);
		
		return modelAndView;
	}
        
        

	public InvoiceService getInvoiceService() {
		return invoiceService;
	}

	public void setInvoiceService(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}
}