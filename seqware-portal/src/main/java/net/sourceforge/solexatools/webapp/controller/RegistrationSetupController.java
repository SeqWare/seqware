package	net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.RegistrationDTO;
import net.sourceforge.solexatools.Debug;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * RegistrationSetupController
 * This is invoked upon entry to Registration.jsp or RegistrationUpdate.jsp
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class RegistrationSetupController extends BaseCommandController {
	
  private RegistrationService registrationService = null;
  
  /**
   * <p>Constructor for RegistrationSetupController.</p>
   */
  public RegistrationSetupController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}

	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
												 HttpServletResponse response)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		ModelAndView modelAndView;
		HashMap<String,Object>  model     = new HashMap<String,Object>();

		Debug.put(": request.requestURI = " + request.getRequestURI());
		
    // used to specify the authentication module if not using the default
    ServletContext context = this.getServletContext();
    String authStr = context.getInitParameter("authenticator");

		/* If they are already logged-in, use the request URI to figure out what to do  */
		if (registration != null && request.getRequestURI().contains("Edit")) {
			/* assume they are updating their info */
			RegistrationDTO		dto	= registrationService.findByEmailAddress(registration.getEmailAddress());
			dto.setDomainObject(registration);
			model.put("strategy", "update");
			request.setAttribute(getCommandName(), dto);
			modelAndView = new ModelAndView("RegistrationUpdate", model);
		} else {
			/* else they are creating a new registration */
			request.setAttribute(getCommandName(), new RegistrationDTO());
			model.put("strategy", "submit");
			modelAndView = new ModelAndView("Registration", model);
		}
		return modelAndView;
	}

  /**
   * <p>Getter for the field <code>registrationService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.RegistrationService} object.
   */
  public RegistrationService getRegistrationService() {
    return registrationService;
  }

  /**
   * <p>Setter for the field <code>registrationService</code>.</p>
   *
   * @param registrationService a {@link net.sourceforge.seqware.common.business.RegistrationService} object.
   */
  public void setRegistrationService(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }
}
