package net.sourceforge.solexatools.validation;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.model.Experiment;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ExperimentValidator implements Validator {
  private ExperimentService experimentService;

  public ExperimentValidator() {
    super();
  }

  @SuppressWarnings("rawtypes")
  public boolean supports(Class clazz) {
    return Experiment.class.equals(clazz);
  }

  /**
   * Validates the specified Object.
   * 
   * @param obj
   *          the Object to validate
   * @param errors
   *          Errors object for validation errors
   */
  public void validate(Object obj, Errors errors) {
    Experiment experiment = (Experiment) obj;
    ValidationUtils.rejectIfEmpty(errors, "title", "experiment.required.title");
    this.validateTitle(experiment, errors);

    ValidationUtils.rejectIfEmpty(errors, "platformInt", "experiment.required.platformInt");
    ValidationUtils.rejectIfEmpty(errors, "sequenceSpace", "experiment.required.sequenceSpace");
    ValidationUtils.rejectIfEmpty(errors, "qualityType", "experiment.required.qualityType");
    ValidationUtils.rejectIfEmpty(errors, "expLibDesignName", "experiment.required.expLibDesignName");
    ValidationUtils.rejectIfEmpty(errors, "expLibDesignStrategy", "experiment.required.expLibDesignStrategy");
    ValidationUtils.rejectIfEmpty(errors, "expLibDesignSource", "experiment.required.expLibDesignSource");
    ValidationUtils.rejectIfEmpty(errors, "expLibDesignSelection", "experiment.required.expLibDesignSelection");

    this.validateExpectedNumberRuns(experiment, errors);
    this.validateExpectedNumberReads(experiment, errors);
  }

  public void validateExpectedNumberRuns(Experiment experiment, Errors errors) {
    if (errors.getFieldError("strExpectedNumberRuns") == null) {
      boolean isHasError = false;
      String strRuns = experiment.getStrExpectedNumberRuns();
      if (strRuns != null && !strRuns.equals("")) {
        Integer runs = null;
        try {
          runs = Integer.parseInt(strRuns);
        } catch (Exception e) {
          isHasError = true;
          errors.reject("experiment.error.type.expectedRuns");
        }
        if (!isHasError) {
          if (runs < 1) {
            isHasError = true;
            errors.reject("experiment.error.value.expectedRuns");
          }
        }
        if (!isHasError) {
          experiment.setExpectedNumberRuns(runs);
        }
      }
    }
  }

  public void validateExpectedNumberReads(Experiment experiment, Errors errors) {
    if (errors.getFieldError("strExpectedNumberReads") == null) {
      boolean isHasError = false;
      String strReads = experiment.getStrExpectedNumberReads();
      if (strReads != null && !strReads.equals("")) {
        Long reads = null;
        try {
          reads = Long.parseLong(strReads);
        } catch (Exception e) {
          isHasError = true;
          errors.reject("experiment.error.type.expectedReds");
        }
        if (!isHasError) {
          if (reads < 1) {
            isHasError = true;
            errors.reject("experiment.error.value.expectedReds");
          }
        }
        if (!isHasError) {
          experiment.setExpectedNumberReads(reads);
        }
      }
    }
  }

  /**
   * Determines if the experiment's email address and confirm email address
   * match.
   * 
   * @param errors
   *          Errors object for validation errors
   */
  public void validateTitle(Experiment experiment, Errors errors) {

    if (errors.getFieldError("title") == null) {
      /* The individual fields have passed validation. */
      if (this.getExperimentService().findByTitle(experiment.getTitle()) != null) {
        errors.reject("error.match.title");
      }
    }
  }

  public ExperimentService getExperimentService() {
    return experimentService;
  }

  public void setExperimentService(ExperimentService experimentService) {
    this.experimentService = experimentService;
  }

}

// ex:sw=4:ts=4:
