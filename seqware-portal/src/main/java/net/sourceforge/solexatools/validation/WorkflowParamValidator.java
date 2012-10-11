package net.sourceforge.solexatools.validation;

import net.sourceforge.seqware.common.model.WorkflowParam;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class WorkflowParamValidator implements Validator{
	
	public WorkflowParamValidator () {
		super();
	}
	
	public boolean supports(Class clazz) {
		return WorkflowParam.class.equals(clazz);
	}

	/**
	 * Validates the specified Object.
	 *
	 * @param obj the Object to validate
	 * @param errors Errors object for validation errors
	 */
	public void validate(Object obj, Errors errors) {
	//	Workflow workflow = (Workflow) obj;
		ValidationUtils.rejectIfEmpty(errors, "type", "error.workflowParam.required.type");
		ValidationUtils.rejectIfEmpty(errors, "key", "error.workflowParam.required.key");
		ValidationUtils.rejectIfEmpty(errors, "displayName", "error.workflowParam.required.displayName");
	}

}

