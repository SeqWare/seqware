package	net.sourceforge.solexatools;						// -*- tab-width: 4 -*-
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.model.Registration;


public class Security {
	public final static boolean isAuthenticated(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session != null
		   && (Registration)session.getAttribute("registration") != null)
			return true;
		return false;
	}

	public final static Registration getRegistration(HttpServletRequest request) {
		if(request == null
		   || request.getSession(false) == null)
			return null;
		return (Registration)request.getSession(false).getAttribute("registration");
	}

	public final static Registration requireRegistration(
			HttpServletRequest	request,
			HttpServletResponse	response
		) {
		//TODO// use a more specific Exception sub-class?

		Registration registration = getRegistration(request);
		if(registration != null)
			return registration;

		//TODO// Save this URL for coming back to, using a session property
		String requestURI = request.getRequestURI();
		String servletPath = request.getServletPath();
		String applicationRootURI =
			requestURI.substring(0, requestURI.length() - servletPath.length());

		Debug.put(": //TODO// save URI for returning after login");
		Debug.put(": applicationRootURI = " + applicationRootURI);
		Debug.put(": redirecting to login...");

		// Redirect to login...
		if(response != null) {
			response.setStatus(response.SC_FORBIDDEN);
			try {
			  response.sendRedirect("login.htm");
			} catch (Exception e) {
			  // ignore
			}
		}
		return registration;
		//throw new Exception();
	}
}
