package net.sourceforge.solexatools.validation;

import net.sourceforge.seqware.common.model.File;

import org.springframework.validation.Errors;

/**
 * <p>FileValidator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class FileValidator extends LoginValidator{
	
	/**
	 * <p>Constructor for FileValidator.</p>
	 */
	public FileValidator () {
		super();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Validates the specified Object.
	 */
	public void validate(Object obj, Errors errors) {
		File file = (File) obj;
	}
}
