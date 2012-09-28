package	net.sourceforge.solexatools.validation;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.model.Lane;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class LaneValidator implements Validator {
	private LaneService laneService;

	public LaneValidator () {
		super();
	}
	
	public boolean supports(Class clazz) {
		return Lane.class.equals(clazz);
	}

	/**
	 * Validates the specified Object.
	 *
	 * @param obj the Object to validate
	 * @param errors Errors object for validation errors
	 */
	public void validate(Object obj, Errors errors) {
		Lane lane = (Lane) obj;
		ValidationUtils.rejectIfEmpty(errors, "name", "lane.required.name");
	//	this.validateName(lane.getName(), errors);
		
		ValidationUtils.rejectIfEmpty(errors, "description", "lane.required.description");
		ValidationUtils.rejectIfEmpty(errors, "cycleDescriptor", "lane.required.cycleDescriptor");
	}

	/**
	 * Determines if the Lane's email address and confirm
	 * email address match.
	 *
	 * @param errors Errors object for validation errors
	 */
	public void validateName(String name, Errors errors) {

		if (errors.getFieldError("name") == null) {
			/* The individual fields have passed validation. */
			if (this.getLaneService().findByName(name) != null) {
				errors.reject("error.match.name");
			}
		}
	}

	public LaneService getLaneService() {
		return laneService;
	}

	public void setLaneService(LaneService LaneService) {
		this.laneService = LaneService;
	}
}

// ex:sw=4:ts=4:
