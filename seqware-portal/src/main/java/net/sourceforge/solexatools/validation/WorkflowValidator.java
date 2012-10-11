package net.sourceforge.solexatools.validation;

import net.sourceforge.seqware.common.model.Workflow;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class WorkflowValidator implements Validator{
	
	public WorkflowValidator () {
		super();
	}
	
	public boolean supports(Class clazz) {
		return Workflow.class.equals(clazz);
	}
	/**
	 * Validates the specified Object.
	 *
	 * @param obj the Object to validate
	 * @param errors Errors object for validation errors
	 */
	public void validate(Object obj, Errors errors) {
	//	Workflow workflow = (Workflow) obj;
		ValidationUtils.rejectIfEmpty(errors, "name", "workflow.required.name");
			
	}

}
