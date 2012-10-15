package net.sourceforge.solexatools.webapp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>StudyReportSetupController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StudyReportSetupController extends BaseCommandController {
	
	private StudyService studyService;
	
	/**
	 * <p>Setter for the field <code>studyService</code>.</p>
	 *
	 * @param studyService a {@link net.sourceforge.seqware.common.business.StudyService} object.
	 */
	public void setStudyService(StudyService studyService) {
		this.studyService = studyService;
	}

	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Registration registration = Security.getRegistration(request);
		if (registration == null)
		{
			return new ModelAndView("redirect:/login.htm");
		}
		
		List<Study> studies = studyService.list(registration);
		ModelAndView modelAndView = new ModelAndView("ReportStudySetup");
		modelAndView.addObject("studies", studies);
		return modelAndView;
	}

}
