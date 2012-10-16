package net.sourceforge.solexatools.webapp.controller; // -*- tab-width: 4 -*-

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.RegistrationDTO;
import net.sourceforge.solexatools.authentication.Authentication;

import org.apache.commons.io.FileUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * LoginController
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LoginController extends SimpleFormController {
  private RegistrationService registrationService;

  /**
   * <p>Constructor for LoginController.</p>
   */
  public LoginController() {
    super();
    setSupportedMethods(new String[] { METHOD_GET, METHOD_POST });
    setCommandClass(RegistrationDTO.class);
  }

  /** {@inheritDoc} */
  @Override
  protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
      BindException errors) throws Exception {
    ModelAndView modelAndView = null;
    RegistrationDTO cmdLogin = (RegistrationDTO) command;
    String emailAddress = cmdLogin.getEmailAddress();
    String password = cmdLogin.getPassword();
    RegistrationDTO registration = this.getRegistrationService().findByEmailAddress(emailAddress);

    // used to specify the authentication module if not using the default
    ServletContext context = this.getServletContext();
    String authStr = context.getInitParameter("authenticator");

    // only set these if defined in the context
    if (context.getInitParameter("java.security.krb5.realm") != null) {
      System.setProperty("java.security.krb5.realm", context.getInitParameter("java.security.krb5.realm"));
    }
    if (context.getInitParameter("java.security.krb5.kdc") != null) {
      System.setProperty("java.security.krb5.kdc", context.getInitParameter("java.security.krb5.kdc"));
    }
    System.err.println("REALM: " + System.getProperty("java.security.krb5.realm"));
    System.err.println("KDC: " + System.getProperty("java.security.krb5.kdc"));

    if (emailAddress.equals("admin@admin.com")) {
      // Set the custom authenticator to null to enable default authentication
      // when the administrator is logging in.
      authStr = null;
    }

    if (registration != null && authStr != null && !"".equals(authStr)) {
      boolean error = true;
      Authentication auth = (Authentication) Class.forName(authStr).newInstance();
      if (auth != null) {
        if (auth.loginSuccess(emailAddress, password)) {
          error = false;
          request.getSession(true).setAttribute("registration", registration);
          modelAndView = new ModelAndView(getSuccessView());
          DeleteTempFile(registration);
        }
      }
      if (error) {
        errors.reject("error.login.incorrect");
        modelAndView = showForm(request, response, errors);
      }
    } else if (registration != null && registration.getPassword().equals(password)) {
      request.getSession(true).setAttribute("registration", registration);
      modelAndView = new ModelAndView(getSuccessView());
    } else {
      errors.reject("error.login.incorrect");
      modelAndView = showForm(request, response, errors);
    }
    return modelAndView;
  }

  /**
   * <p>DeleteTempFile.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @throws java.lang.Exception if any.
   */
  public void DeleteTempFile(Registration registration) throws Exception {
    String contextPath = this.getServletContext().getContextPath();
    String pathToTempStore = "webapps" + contextPath + java.io.File.separator + "temp" + java.io.File.separator
        + registration.getEmailAddress();

    java.io.File tempFolder = new java.io.File(pathToTempStore);
    if (tempFolder.exists()) {
      FileUtils.deleteDirectory(tempFolder);
    }
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
