package	net.sourceforge.solexatools.validation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.seqware.common.model.RegistrationDTO;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class LoginValidator implements Validator {
	protected static final String CONST_AT_SIGN = "@";

	public LoginValidator() {
		super();
	}

	/**
	 * Returns true if this Validator supports the
	 * specified Class, and false otherwise.
	 *
	 * @param clazz java.lang.Class
	 *
	 * @return true if this Validator supports the
	 * specified Class, and false otherwise
	 */
	public boolean supports(Class clazz) {
		return RegistrationDTO.class.equals(clazz);
	}

	/**
	 * Validates the specified Object.
	 *
	 * @param obj the Object to validate
	 * @param errors Errors object for validation
	 * errors
	 */
	public void validate(Object obj, Errors errors) {
		RegistrationDTO registration = (RegistrationDTO) obj;
		this.validateEmail("emailAddress", registration.getEmailAddress(), errors);
		ValidationUtils.rejectIfEmpty(errors, "password", "required.password");
	}

	/**
	 * Validates an email address.
	 *
	 * @param emailProperty email property name such as "emailAddress"
	 * @param emailValue value of the email property
	 * @param errors Errors object for validation errors
	 */
	public void validateEmail(String emailProperty, String emailValue, Errors errors) {
		//if (emailValue == null || emailValue.indexOf(CONST_AT_SIGN) == -1) {
		//	errors.rejectValue(emailProperty, "required." + emailProperty);
		//}
		
		if (emailValue == null || !isCheckEmail(emailValue)) {
			errors.rejectValue(emailProperty, "required." + emailProperty);
		}
	}
	
	public static boolean isCheckEmail(String emailValue){
		boolean isValid = false;
		if (emailValue != null ) {
		    Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		    Matcher m = p.matcher(emailValue);
		    boolean matchFound = m.matches();

		    if(matchFound){
		    	isValid = true;
		    }else{
		    	isValid = false;
		    }
		}
		return isValid;
	}
		
}

// ex:sw=4:ts=4:
