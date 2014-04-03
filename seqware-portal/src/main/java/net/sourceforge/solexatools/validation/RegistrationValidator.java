package	net.sourceforge.solexatools.validation;						// -*- tab-width: 4 -*-
import net.sourceforge.seqware.common.model.RegistrationDTO;
import net.sourceforge.solexatools.Debug;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * <p>RegistrationValidator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class RegistrationValidator extends LoginValidator {
	
	private static String trueInvitationCode = "19dks1-12i393-12991-2219k";
	
	/**
	 * <p>Constructor for RegistrationValidator.</p>
	 */
	public RegistrationValidator () {
		super();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Validates the specified RegistrationDTO instance.
	 */
	public void validate(Object reg, Errors errors) {
		RegistrationDTO registration = (RegistrationDTO)reg;
		Debug.put(": validating registration.id = " + registration.getRegistrationId());

		this.validateEmail("emailAddress",
						   registration.getEmailAddress(), errors);
//		this.validateEmail("confirmEmailAddress",
//						   registration.getConfirmEmailAddress(), errors);
		
		ValidationUtils.rejectIfEmpty(errors, "password", "required.password");
		
		
/*		if(registration.getConfirmPassword().equals("")){
			errors.reject("required.confirmPassword");
		}
*/		
		ValidationUtils.rejectIfEmpty(errors, "confirmPassword",
									  "required.confirmPassword");
		
//		ValidationUtils.rejectIfEmpty(errors, "invitationCode", "required.invitationCode");
		
/*		ValidationUtils.rejectIfEmpty(errors, "passwordHint", "required.passwordHint");
		ValidationUtils.rejectIfEmpty(errors, "firstName", "required.firstName");
		ValidationUtils.rejectIfEmpty(errors, "lastName", "required.lastName");
		this.validateEmailAddressesMatch(registration.getEmailAddress(),
										 registration.getConfirmEmailAddress(), errors);
*/										 
		
		
		this.validatePasswordsMatch(registration.getPassword(),
									registration.getConfirmPassword(), errors);

		/* if this is a new-instance, make sure email is not already in use */
		this.validateEmailNotInUse(registration.getEmailAddress(), errors);

//		this.validateInvitationCode(registration.getInvitationCode(), errors);
		/* //TODO// make sure the user has the required privileges to create/update */
	}
	
	/**
	 * <p>validateInvitationCode.</p>
	 *
	 * @param invitationCode a {@link java.lang.String} object.
	 * @param errors a {@link org.springframework.validation.Errors} object.
	 */
	public void validateInvitationCode(String invitationCode, Errors errors){
		if (errors.getFieldError("invitationCode") == null){
			if(!invitationCode.trim().equals(trueInvitationCode)){
				errors.reject("error.registration.invitationCode.false");
			}
		}
	}

	/**
	 * Determines if the registration's email address and confirm
	 * email address match.
	 *
	 * @param emailAddress a {@link java.lang.String} object.
	 * @param confirmEmailAddress a {@link java.lang.String} object.
	 * @param errors a {@link org.springframework.validation.Errors} object.
	 */
	public static void validateEmailAddressesMatch(String emailAddress,
											String confirmEmailAddress, Errors errors) {
		if (errors.getFieldError("emailAddress") == null &&
			errors.getFieldError("confirmEmailAddress") == null) {
			/* The individual fields have passed validation. */
			if (!emailAddress.trim()
				.equalsIgnoreCase(confirmEmailAddress.trim())) {
				errors.reject("error.registration.match.email");
			}
		}
	}

	/**
	 * Determines if the registration's password and confirm
	 * password match.
	 *
	 * @param password a {@link java.lang.String} object.
	 * @param confirmPassword a {@link java.lang.String} object.
	 * @param errors a {@link org.springframework.validation.Errors} object.
	 */
	public static void validatePasswordsMatch(String password,
									   String confirmPassword, Errors errors) {
		if (errors.getFieldError("password") == null &&
			errors.getFieldError("confirmPassword") == null) {
			/* The individual fields have passed validation. */
			if (!password.trim().equals(confirmPassword.trim())) {
				errors.reject("error.registration.match.password");
			}
		}
	}

	/**
	 * <p>validateEmailNotInUse.</p>
	 *
	 * @param emailAddress a {@link java.lang.String} object.
	 * @param errors a {@link org.springframework.validation.Errors} object.
	 */
	public void validateEmailNotInUse(String emailAddress, Errors errors) {
		if (errors.getFieldError("emailAddress") == null) {
			//TODO//
			Debug.put(": email-in-use check");
		}
	}
}

// ex:sw=4:ts=4:
