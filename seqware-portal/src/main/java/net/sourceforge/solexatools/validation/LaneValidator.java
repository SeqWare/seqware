package	net.sourceforge.solexatools.validation;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.model.Lane;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * <p>LaneValidator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LaneValidator implements Validator {
	private LaneService laneService;

	/**
	 * <p>Constructor for LaneValidator.</p>
	 */
	public LaneValidator () {
		super();
	}
	
	/** {@inheritDoc} */
	public boolean supports(Class clazz) {
		return Lane.class.equals(clazz);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Validates the specified Object.
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
	 * @param name a {@link java.lang.String} object.
	 */
	public void validateName(String name, Errors errors) {

		if (errors.getFieldError("name") == null) {
			/* The individual fields have passed validation. */
			if (this.getLaneService().findByName(name) != null) {
				errors.reject("error.match.name");
			}
		}
	}

	/**
	 * <p>Getter for the field <code>laneService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.LaneService} object.
	 */
	public LaneService getLaneService() {
		return laneService;
	}

	/**
	 * <p>Setter for the field <code>laneService</code>.</p>
	 *
	 * @param LaneService a {@link net.sourceforge.seqware.common.business.LaneService} object.
	 */
	public void setLaneService(LaneService LaneService) {
		this.laneService = LaneService;
	}
}

// ex:sw=4:ts=4:
