package	net.sourceforge.solexatools.validation;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.model.SequencerRun;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class SequencerRunValidator implements Validator {
	private SequencerRunService sequencerRunService;

	public SequencerRunValidator () {
		super();
	}
	
	public boolean supports(Class clazz) {
		return SequencerRun.class.equals(clazz);
	}

	/**
	 * Validates the specified Object.
	 *
	 * @param obj the Object to validate
	 * @param errors Errors object for validation errors
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
	 */
	public void validateName(String name, Errors errors) {

		if (errors.getFieldError("name") == null) {
			/* The individual fields have passed validation. */
			if (getSequencerRunService().findByName(name) != null) {
				errors.reject("error.sequencerRun.match.email");
			}
		}
	}
	
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

	public SequencerRunService getSequencerRunService() {
		return sequencerRunService;
	}

	public void setSequencerRunService(SequencerRunService sequencerRunService) {
		this.sequencerRunService = sequencerRunService;
	}
}

// ex:sw=4:ts=4:
