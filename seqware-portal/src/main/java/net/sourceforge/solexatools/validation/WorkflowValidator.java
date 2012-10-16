package net.sourceforge.solexatools.validation;

import net.sourceforge.seqware.common.model.Workflow;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * <p>WorkflowValidator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowValidator implements Validator{
	
	/**
	 * <p>Constructor for WorkflowValidator.</p>
	 */
	public WorkflowValidator () {
		super();
	}
	
	/** {@inheritDoc} */
	public boolean supports(Class clazz) {
		return Workflow.class.equals(clazz);
	}
	/**
	 * {@inheritDoc}
	 *
	 * Validates the specified Object.
	 */
	public void validate(Object obj, Errors errors) {
	//	Workflow workflow = (Workflow) obj;
		ValidationUtils.rejectIfEmpty(errors, "name", "workflow.required.name");
			
	}

}
