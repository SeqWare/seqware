package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.Constant;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class IUSController extends MultiActionController {
	private IUSService iusService;

	public IUSController() {
	    super();
	}

	public IUSController(Object delegate) {
		super(delegate);
	}
  
	public IUSService getIusService() {
		return iusService;
	}

	public void setIusService(IUSService iusService) {
		this.iusService = iusService;
	}
					  
	public IUSService getIUSService() {
		return iusService;
	}

	public void setIUSService(IUSService iusService) {
		this.iusService = iusService;
	}
	  
	/**
	 * Handles the user's request to delete their ius.
	 *
	 * @param command IUS command object
	 */
	public ModelAndView handleDelete(HttpServletRequest		request,
									 HttpServletResponse	response,
									 IUS					command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		IUS						ius				= getRequestedIUS(request);
		
		ServletContext context = this.getServletContext();
		String deleteRealFiles = context.getInitParameter("delete.files.for.node.deletion");

		if (ius != null) {
		    if(registration.equals(ius.getOwner()) || registration.isLIMSAdmin()){
		    	getIUSService().delete(ius, deleteRealFiles);
		    }
		} 
		modelAndView = new ModelAndView(getViewName(request), model);
		return modelAndView;
	}
	
	/**
	 * Handles the user's request to delete their ius.
	 *
	 * @param command IUS command object
	 */
	public ModelAndView handleUpdate(HttpServletRequest		request,
									 HttpServletResponse	response,
									 IUS					command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");
		
		ModelAndView modelAndView = null;
		
		IUS newIus = command;
		IUS oldIus = getCurrentIus(request);
		if (newIus != null && oldIus != null) {
			new ServletRequestDataBinder(oldIus).bind(request);
			getIusService().update(oldIus);
			modelAndView = new ModelAndView("redirect:/search.htm");
		} else {
			modelAndView = new ModelAndView("redirect:/Error.htm");
		}
	
		request.getSession(false).removeAttribute("ius");
		
		return modelAndView;
	}
	
	/**
	 * Handles the user's request to cancel.
	 *
	 * @param command IUS command object
	 *
	 * @return ModelAndView
	 *
	 * @throws Exception
	 */
	public ModelAndView handleCancel(HttpServletRequest request,
									 HttpServletResponse response,
									 IUS command) throws Exception {
		return new ModelAndView("redirect:/search.htm");
	}
	
	/**
	 * Gets the IUS from the session.
	 *
	 * @param request HttpServletRequest
	 *
	 * @return instance of IUS from the session, or a new instance
	 * if the IUS is not in the session (e.g. the user is not logged in)
	 */
	private IUS getCurrentIus(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object ius = session.getAttribute("ius");
			if (ius != null) {
				return (IUS)ius;
			}
		}
		return new IUS();
	}
	  
	private String getViewName(HttpServletRequest request){
		String typeTree = (String)request.getSession(false).getAttribute("typeTree");
		String viewName = Constant.getViewName(typeTree);
		request.getSession(false).removeAttribute("typeTree");
		return viewName;
	}
	
	private IUS getRequestedIUS(HttpServletRequest request) {
		HttpSession	session	= request.getSession(false);
		IUS  	ius	= null;
		String	id	= (String)request.getParameter("objectId");
			
		 if (id != null) {
		  Integer iusID = Integer.parseInt(id);
		  ius = getIUSService().findByID(iusID);
		 }
		 return ius;
	}
}