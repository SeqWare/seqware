package	net.sourceforge.solexatools.validation;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.model.Sample;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class SampleValidator implements Validator {
	private SampleService sampleService;

	public SampleValidator () {
		super();
	}
	
	public boolean supports(Class clazz) {
		return Sample.class.equals(clazz);
	}

	/**
	 * Validates the specified Object.
	 *
	 * @param obj the Object to validate
	 * @param errors Errors object for validation errors
	 */
	public void validate(Object obj, Errors errors) {
		Sample sample = (Sample) obj;
		ValidationUtils.rejectIfEmpty(errors, "title", "sample.required.title");
	//	this.validateTitle(sample.getTitle(), errors);
		
		ValidationUtils.rejectIfEmpty(errors, "organismId", "sample.required.organism");
		this.validateExpectedNumberRuns(sample, errors);	
		this.validateExpectedNumberReads(sample, errors);
	}
	
	public void validateExpectedNumberRuns(Sample sample, Errors errors) {
		if (errors.getFieldError("strExpectedNumRuns") == null) {
			boolean isHasError = false;
			String strRuns = sample.getStrExpectedNumRuns();
			if(strRuns != null && !strRuns.equals("")){
				Integer runs = null;
				try{
					runs = Integer.parseInt(strRuns);
				}catch (Exception e) {
					isHasError = true;
					errors.reject("sample.error.type.expectedRuns");
				}
				if(!isHasError){
					if(runs < 1){
						isHasError = true;
						errors.reject("sample.error.value.expectedRuns");
					}
				}
				if(!isHasError){
					sample.setExpectedNumRuns(runs);
				}
			}
		}
	}
	
	public void validateExpectedNumberReads(Sample sample, Errors errors) {
		if (errors.getFieldError("strExpectedNumReads") == null) {
			boolean isHasError = false;
			String strReads = sample.getStrExpectedNumReads();
			if(strReads != null && !strReads.equals("")){
				Integer reads = null;
				try{
					reads = Integer.parseInt(strReads);
				}catch (Exception e) {
					isHasError = true;
					errors.reject("sample.error.type.expectedReds");
				}
				if(!isHasError){
					if(reads < 1){
						isHasError = true;
						errors.reject("sample.error.value.expectedReds");
					}
				}
				if(!isHasError){
					sample.setExpectedNumReads(reads);
				}
			}
		}
	}

	/**
	 * Determines if the sample's email address and confirm
	 * email address match.
	 *
	 * @param errors Errors object for validation errors
	 */
	public void validateTitle(String title, Errors errors) {

		if (errors.getFieldError("title") == null) {
			/* The individual fields have passed validation. */
			if (this.getSampleService().findByTitle(title) != null) {
				errors.reject("error.match.title");
			}
		}
	}

	public SampleService getSampleService() {
		return sampleService;
	}

	public void setSampleService(SampleService sampleService) {
		this.sampleService = sampleService;
	}
}

// ex:sw=4:ts=4:
