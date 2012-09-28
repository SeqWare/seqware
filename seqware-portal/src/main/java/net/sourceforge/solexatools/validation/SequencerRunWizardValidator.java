package	net.sourceforge.solexatools.validation;						// -*- tab-width: 4 -*-
import net.sourceforge.seqware.common.model.SequencerRunWizardDTO;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

public class SequencerRunWizardValidator extends SequencerRunValidator {
	
	public SequencerRunWizardValidator () {
		super();
	}
	
	public boolean supports(Class clazz) {
		return SequencerRunWizardDTO.class.equals(clazz);
	}

	/**
	 * Validates the specified Object.
	 *
	 * @param obj the Object to validate
	 * @param errors Errors object for validation errors
	 */
	public void validate(Object obj, Errors errors) {
		SequencerRunWizardDTO sequencerRunWizardDTO = (SequencerRunWizardDTO) obj;
		this.validateName(sequencerRunWizardDTO.getName(), errors);
		ValidationUtils.rejectIfEmpty(errors, "name", "sequencerRun.required.name");
		this.validateRefLane(sequencerRunWizardDTO, errors);
		this.validateLaneCount(sequencerRunWizardDTO, errors);
	}

	public void validateLaneCount(SequencerRunWizardDTO sequencerRunWizardDTO, Errors errors) {
		if (errors.getFieldError("strLaneCount") == null) {
			boolean isHasError = false;
			String strLaneCount = sequencerRunWizardDTO.getStrLaneCount();
			if(strLaneCount != null && !strLaneCount.equals("")){
				Integer laneCount = null;
				try{
					laneCount = Integer.parseInt(strLaneCount);
				}catch (Exception e) {
					isHasError = true;
					errors.reject("sequencerRunWizardDTO.error.type.laneCont");
				}
				if(!isHasError){
					if(laneCount < 1){
						isHasError = true;
						errors.reject("sequencerRunWizardDTO.error.value.laneCont");
					}
				}
				if(!isHasError){
					sequencerRunWizardDTO.setLaneCount(laneCount);
				}
			}
		}
	}
}

// ex:sw=4:ts=4:
