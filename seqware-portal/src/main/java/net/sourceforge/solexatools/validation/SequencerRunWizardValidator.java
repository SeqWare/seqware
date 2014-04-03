package	net.sourceforge.solexatools.validation;						// -*- tab-width: 4 -*-
import net.sourceforge.seqware.common.model.SequencerRunWizardDTO;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * <p>SequencerRunWizardValidator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SequencerRunWizardValidator extends SequencerRunValidator {
	
	/**
	 * <p>Constructor for SequencerRunWizardValidator.</p>
	 */
	public SequencerRunWizardValidator () {
		super();
	}
	
	/** {@inheritDoc} */
	public boolean supports(Class clazz) {
		return SequencerRunWizardDTO.class.equals(clazz);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Validates the specified Object.
	 */
	public void validate(Object obj, Errors errors) {
		SequencerRunWizardDTO sequencerRunWizardDTO = (SequencerRunWizardDTO) obj;
		this.validateName(sequencerRunWizardDTO.getName(), errors);
		ValidationUtils.rejectIfEmpty(errors, "name", "sequencerRun.required.name");
		this.validateRefLane(sequencerRunWizardDTO, errors);
		this.validateLaneCount(sequencerRunWizardDTO, errors);
	}

	/**
	 * <p>validateLaneCount.</p>
	 *
	 * @param sequencerRunWizardDTO a {@link net.sourceforge.seqware.common.model.SequencerRunWizardDTO} object.
	 * @param errors a {@link org.springframework.validation.Errors} object.
	 */
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
