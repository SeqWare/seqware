package net.sourceforge.solexatools.validation;

import net.sourceforge.seqware.common.model.Experiment;

import org.springframework.validation.Errors;

public class ExperimentUpdateValidator extends ExperimentValidator {

  @Override
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

      Experiment inDbExperiment = this.getExperimentService().findByTitle(experiment.getTitle());

      if (inDbExperiment != null
          && inDbExperiment.getSwAccession().intValue() != experiment.getSwAccession().intValue()) {
        errors.reject("error.match.title");
      }
    }
  }

}
