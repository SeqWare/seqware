package net.sourceforge.solexatools.webapp.controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>ChangeNumberPopupPageController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ChangeNumberPopupPageController  extends BaseCommandController {
	
	/** Constant <code>SEPARATOR="java.io.File.separator"</code> */
	public final static String SEPARATOR = java.io.File.separator;
	
	/**
	 * <p>Constructor for ChangeNumberPopupPageController.</p>
	 */
	public ChangeNumberPopupPageController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}
	
	private void setNumberCurrentPage(HttpServletRequest request, Integer numberCurrentPage){
		request.getSession(false).setAttribute("numberCurrentPage", numberCurrentPage);
	}
	
	private Integer getNumberCurrentPage(HttpServletRequest request){
		Integer numberCurrentPage =(Integer)request.getSession(false).getAttribute("numberCurrentPage");
		if(numberCurrentPage == null){
			numberCurrentPage = 0;
		}
		return numberCurrentPage;
	}
	
	private String incrementNumberCurrentPage(HttpServletRequest request){
		Log.info("Call increment ..");
		Integer numberCurrentPage = getNumberCurrentPage(request);
		List<String> listSavedPage = getListSavedPage(request);
		Log.info("Start. Number page = " + numberCurrentPage + "; List page size = " + listSavedPage.size());
		if(numberCurrentPage < listSavedPage.size()){
			Log.info("Do ++ ");
			numberCurrentPage++;
			setNumberCurrentPage(request, numberCurrentPage);
		}
		Log.info("Start. Number page = " + numberCurrentPage + "; List page size = " + listSavedPage.size());
		return getCurrentPage(request);
	}
	
	private String decreaseNumberCurrentPage(HttpServletRequest request){
		Integer numberCurrentPage = getNumberCurrentPage(request);
		Log.info("Call decrease ..");
		Log.info("Start. Number curr page = " + numberCurrentPage);
		if(numberCurrentPage > 1){
			Log.info("Do -- ");
			numberCurrentPage--;
			setNumberCurrentPage(request, numberCurrentPage);
		}
		Log.info("End. Number curr page = " + numberCurrentPage);
		return getCurrentPage(request);
	}
	
	private void setListSavedPage(HttpServletRequest request, List<String> listSavedPage){
		request.getSession(false).setAttribute("listSavedPage", listSavedPage);
	}
	
	private List<String> getListSavedPage(HttpServletRequest request){
		List<String> listSavedPage = (List <String>)request.getSession(false).getAttribute("listSavedPage");
		if(listSavedPage == null){
			listSavedPage = new LinkedList<String>();
		}
		return listSavedPage;
	}
	
	private String getCurrentPage(HttpServletRequest request){
		Integer numberCurrentPage = getNumberCurrentPage(request);
		List<String> listSavedPage = getListSavedPage(request);
		return listSavedPage.get(numberCurrentPage - 1);
	}
	
	private String getStartPage(HttpServletRequest request){
		setNumberCurrentPage(request, 1);
		return getCurrentPage(request);
	}
	
	private String getCurrentPage(HttpServletRequest request, String action){
		String currentPage = "";
		if("next".equals(action)){
			currentPage = incrementNumberCurrentPage(request);
		}
		if("previous".equals(action)){
			currentPage = decreaseNumberCurrentPage(request);
		}
		if("reload".equals(action)){
			currentPage = getCurrentPage(request);
		}
		if("home".equals(action)){
			currentPage = getStartPage(request);
		}
		return currentPage;
	}

	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
												 HttpServletResponse response)
		throws Exception {

		//Registration registration = Security.requireRegistration(request, response);
		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		/**
		 * Pass registration so that we can filter the list if its appropriate
		 * to do so.
		 */
		String action = getRequestedAction(request);
		getCurrentPage(request, action);
		
		ModelAndView modelAndView	=  null;//new ModelAndView("ResultUnpack");
		
		request.getSession(false).setAttribute("isUserAbortedViewIndexPage", false);
		
		return modelAndView;
	}
	
	private String getRequestedAction(HttpServletRequest request){
		return request.getParameter("action");
	}
}

