package net.sourceforge.solexatools.validation;

/**
 * {@inheritDoc}
 *
 *package net.sourceforge.solexatools.validation;
 *
 *import net.sourceforge.seqware.common.model.Study;
 *
 *import org.springframework.validation.Errors;
 *
 *public class StudyUpdateValidator extends StudyValidator {
 */

import java.util.List;
import java.util.Objects;
import net.sourceforge.seqware.common.model.Study;
import org.springframework.validation.Errors;

public class StudyUpdateValidator extends StudyValidator {
    @Override
    /**
     * We don't want to validate if title exist for the update study.
     */
    public void validateTitle(Study study, Errors errors) {
        if (errors.getFieldError("title") == null) {
            /* The individual fields have passed validation. */
            List<Study> inDbStudy = this.getStudyService().findByTitle(study.getTitle());

            // if inDbStudy has the same ID that current Study has, then this is the
            // same study
            // Title can be the same, otherwise title should not be matched to the
            // different study.
            // FIXME: assumes one study per title
            if (inDbStudy != null && !Objects.equals(inDbStudy.get(0).getSwAccession(), study.getSwAccession())) {
                errors.reject("error.match.title");
            }
        }
    }
}
