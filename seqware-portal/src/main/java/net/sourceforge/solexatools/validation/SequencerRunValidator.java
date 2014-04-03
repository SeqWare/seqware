package	net.sourceforge.solexatools.validation;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.model.SequencerRun;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * <p>SequencerRunValidator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SequencerRunValidator implements Validator {
	private SequencerRunService sequencerRunService;

	/**
	 * <p>Constructor for SequencerRunValidator.</p>
	 */
	public SequencerRunValidator () {
		super();
	}
	
	/** {@inheritDoc} */
	public boolean supports(Class clazz) {
		return SequencerRun.class.equals(clazz);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Validates the specified Object.
	 */
	public void validate(Object obj, Errors errors) {
		SequencerRun sequencerRun = (SequencerRun) obj;
		this.validateName(sequencerRun.getName(), errors);
		ValidationUtils.rejectIfEmpty(errors, "name", "sequencerRun.required.name");
		this.validateRefLane(sequencerRun, errors);
	}

	/**
	 * Determines if the experiment's email address and confirm
	 * email address match.
	 *
	 * @param errors Errors object for validation errors
	 * @param name a {@link java.lang.String} object.
	 */
	public void validateName(String name, Errors errors) {

		if (errors.getFieldError("name") == null) {
			/* The individual fields have passed validation. */
			if (getSequencerRunService().findByName(name) != null) {
				errors.reject("error.sequencerRun.match.email");
			}
		}
	}
	
	/**
	 * <p>validateRefLane.</p>
	 *
	 * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
	 * @param errors a {@link org.springframework.validation.Errors} object.
	 */
	public void validateRefLane(SequencerRun sequencerRun, Errors errors) {
		if (errors.getFieldError("strRefLane") == null) {
			boolean isHasError = false;
			String strRefLane = sequencerRun.getStrRefLane();
			if(strRefLane != null && !strRefLane.equals("")){
				Integer refLane = null;
				try{
					refLane = Integer.parseInt(strRefLane);
				}catch (Exception e) {
					isHasError = true;
					errors.reject("sequencerRun.error.type.refLane");
				}
				if(!isHasError){
					if(refLane < 1){
						isHasError = true;
						errors.reject("sequencerRun.error.value.refLane");
					}
				}
				if(!isHasError){
					sequencerRun.setRefLane(refLane);
				}
			}
		}
	}

	/**
	 * <p>Getter for the field <code>sequencerRunService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.SequencerRunService} object.
	 */
	public SequencerRunService getSequencerRunService() {
		return sequencerRunService;
	}

	/**
	 * <p>Setter for the field <code>sequencerRunService</code>.</p>
	 *
	 * @param sequencerRunService a {@link net.sourceforge.seqware.common.business.SequencerRunService} object.
	 */
	public void setSequencerRunService(SequencerRunService sequencerRunService) {
		this.sequencerRunService = sequencerRunService;
	}
}

// ex:sw=4:ts=4:
