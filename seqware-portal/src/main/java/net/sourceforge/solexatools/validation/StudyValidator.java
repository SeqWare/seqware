package net.sourceforge.solexatools.validation;

import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.model.Study;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * <p>StudyValidator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StudyValidator implements Validator {
  private StudyService studyService;

  /**
   * <p>Constructor for StudyValidator.</p>
   */
  public StudyValidator() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  public boolean supports(Class clazz) {
    return Study.class.equals(clazz);
  }

  /**
   * {@inheritDoc}
   *
   * Validates the specified Object.
   */
  public void validate(Object obj, Errors errors) {
    Study study = (Study) obj;
    ValidationUtils.rejectIfEmpty(errors, "title", "study.required.title");
    this.validateTitle(study, errors);

    ValidationUtils.rejectIfEmpty(errors, "existingTypeInt", "study.required.existingTypeInt");
    ValidationUtils.rejectIfEmpty(errors, "centerName", "study.required.centerName");
    ValidationUtils.rejectIfEmpty(errors, "centerProjectName", "study.required.centerProjectName");
  }

  /**
   * Determines if the experiment's email address and confirm email address
   * match.
   *
   * @param errors
   *          Errors object for validation errors
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   */
  public void validateTitle(Study study, Errors errors) {
    if (errors.getFieldError("title") == null) {
      /* The individual fields have passed validation. */
      if (this.getStudyService().findByTitle(study.getTitle()) != null) {
        errors.reject("error.match.title");
      }
    }
  }

  /**
   * <p>Getter for the field <code>studyService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.StudyService} object.
   */
  public StudyService getStudyService() {
    return studyService;
  }

  /**
   * <p>Setter for the field <code>studyService</code>.</p>
   *
   * @param studyService a {@link net.sourceforge.seqware.common.business.StudyService} object.
   */
  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }
}

// ex:sw=4:ts=4:
