package net.sourceforge.solexatools.validation;

import net.sourceforge.seqware.common.model.File;

import org.springframework.validation.Errors;

public class FileValidator extends LoginValidator{
	
	public FileValidator () {
		super();
	}

	/**
	 * Validates the specified Object.
	 *
	 * @param obj the Object to validate
	 * @param errors Errors object for validation errors
	 */
	public void validate(Object obj, Errors errors) {
		File file = (File) obj;
	}
}
