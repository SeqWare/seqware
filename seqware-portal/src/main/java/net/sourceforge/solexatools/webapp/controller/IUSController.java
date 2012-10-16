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

/**
 * <p>IUSController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class IUSController extends MultiActionController {
	private IUSService iusService;

	/**
	 * <p>Constructor for IUSController.</p>
	 */
	public IUSController() {
	    super();
	}

	/**
	 * <p>Constructor for IUSController.</p>
	 *
	 * @param delegate a {@link java.lang.Object} object.
	 */
	public IUSController(Object delegate) {
		super(delegate);
	}
  
	/**
	 * <p>Getter for the field <code>iusService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.IUSService} object.
	 */
	public IUSService getIusService() {
		return iusService;
	}

	/**
	 * <p>Setter for the field <code>iusService</code>.</p>
	 *
	 * @param iusService a {@link net.sourceforge.seqware.common.business.IUSService} object.
	 */
	public void setIusService(IUSService iusService) {
		this.iusService = iusService;
	}
					  
	/**
	 * <p>getIUSService.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.IUSService} object.
	 */
	public IUSService getIUSService() {
		return iusService;
	}

	/**
	 * <p>setIUSService.</p>
	 *
	 * @param iusService a {@link net.sourceforge.seqware.common.business.IUSService} object.
	 */
	public void setIUSService(IUSService iusService) {
		this.iusService = iusService;
	}
	  
	/**
	 * Handles the user's request to delete their ius.
	 *
	 * @param command IUS command object
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @return a {@link org.springframework.web.servlet.ModelAndView} object.
	 * @throws java.lang.Exception if any.
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
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @return a {@link org.springframework.web.servlet.ModelAndView} object.
	 * @throws java.lang.Exception if any.
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
	 * @return ModelAndView
	 * @throws java.lang.Exception if any.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
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
