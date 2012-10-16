package net.sourceforge.solexatools.validation;

import net.sourceforge.seqware.common.model.WorkflowParam;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * <p>WorkflowParamValidator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowParamValidator implements Validator{
	
	/**
	 * <p>Constructor for WorkflowParamValidator.</p>
	 */
	public WorkflowParamValidator () {
		super();
	}
	
	/** {@inheritDoc} */
	public boolean supports(Class clazz) {
		return WorkflowParam.class.equals(clazz);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Validates the specified Object.
	 */
	public void validate(Object obj, Errors errors) {
	//	Workflow workflow = (Workflow) obj;
		ValidationUtils.rejectIfEmpty(errors, "type", "error.workflowParam.required.type");
		ValidationUtils.rejectIfEmpty(errors, "key", "error.workflowParam.required.key");
		ValidationUtils.rejectIfEmpty(errors, "displayName", "error.workflowParam.required.displayName");
	}

}

