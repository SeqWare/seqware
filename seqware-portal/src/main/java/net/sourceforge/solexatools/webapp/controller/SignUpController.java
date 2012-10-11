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
 */
public class SignUpController extends BaseCommandController {
	
  private RegistrationService registrationService = null;
  
  public SignUpController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}

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
//		String authStr = context.getInitParameter("authenticator");
		 
		String isInvitationCode = context.getInitParameter("invitation.code");

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
			RegistrationDTO registationDTO = null;
			
			/* if invitation code id on then find registration*/			
			if("true".equals(isInvitationCode)){
				String email = request.getParameter("email");
				String code = request.getParameter("code");
			//	Log.info("Sign Up email = " + email);
				if(email != null && !"".equals(email)){
					registationDTO = getRegistrationService().findByEmailAddress(email);
					// check invitation code with link
					if(code == null || registationDTO == null || !code.equals(registationDTO.getInvitationCode())){
						registationDTO = new RegistrationDTO();
					}
		//			request.getSession(false).setAttribute("registration", registationDTO);
				}
			}
			if("false".equals(isInvitationCode)){
				String email = request.getParameter("email");
				if(email != null && !"".equals(email)){
					registationDTO = getRegistrationService().findByEmailAddress(email);
				}
			}
			
			// if reg not found then creating a new registration 
			if(registationDTO == null){
				registationDTO = new RegistrationDTO();
			}

			request.setAttribute(getCommandName(), registationDTO);			
			model.put("strategy", "submit");
			model.put("isInvitationCode", isInvitationCode);
			modelAndView = new ModelAndView("SignUp", model);
		}
		return modelAndView;
	}

  public RegistrationService getRegistrationService() {
    return registrationService;
  }

  public void setRegistrationService(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }
}
