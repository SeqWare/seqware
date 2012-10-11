package net.sourceforge.solexatools.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

public class SearchResultsController extends BaseCommandController {

	@Override
	protected ModelAndView handleRequestInternal(
			HttpServletRequest httpservletrequest,
			HttpServletResponse httpservletresponse) throws Exception {
		
		ModelAndView mav = new ModelAndView("SearchResults");
		mav.addObject("type", httpservletrequest.getParameter("type"));
		mav.addObject("criteria", httpservletrequest.getParameter("criteria"));
		mav.addObject("mode", httpservletrequest.getParameter("mode"));
		HttpSession session = httpservletrequest.getSession(false);
		String caseSens = httpservletrequest.getParameter("casesens");
		if (caseSens != null) {
			session.setAttribute(SearchController.SEARCH_CASE_SENSITIVE, caseSens);
		} else {
			session.removeAttribute(SearchController.SEARCH_CASE_SENSITIVE);
		}
		
		return mav;
	}

}
