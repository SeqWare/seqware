package net.sourceforge.solexatools.webapp.controller;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.StudyTypeService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.SetNodeIdInSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * RegistrationSetupController
 */

public class StudySetupController extends BaseCommandController {
    
    private StudyService studyService;
    private StudyTypeService studyTypeService;

	public StudySetupController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest	 request,
												 HttpServletResponse response)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		Study				study		= getRequestedStudy(request);
		boolean isReport = request.getParameter("report") != null;
		
		model.put("studyTypeList", getStudyTypeService().list(registration));
		if (study != null) {
		    study.setExistingTypeInt(study.getExistingType().getStudyTypeId());
			request.setAttribute(getCommandName(), study);
			request.setAttribute("swid",study.getSwAccession());
			model.put("strategy", "update");
			if (!isReport) {
				modelAndView = new ModelAndView("Study", model);
			} else {
				modelAndView = new ModelAndView("StudyReport", model);
			}
		} else {
			request.setAttribute(getCommandName(), new Study());
			model.put("strategy", "submit");
			modelAndView = new ModelAndView("Study", model);
		}
		// remove node id
		SetNodeIdInSession.removeStudy(request);
		
		return modelAndView;
	}

	private Study getRequestedStudy(HttpServletRequest request) {
		HttpSession	session	= request.getSession(false);
		Study study = null;
		String id = (String)request.getParameter("studyID");
		session.removeAttribute("study");

		if (id != null) {
			Integer expID = Integer.parseInt(id);
			study = getStudyService().findByID(expID);
			session.setAttribute("study", study);
		}

		return study;
	}

	public StudyService getStudyService() {
		return studyService;
	}

	public void setStudyService(StudyService studyService) {
		this.studyService = studyService;
	}

    public StudyTypeService getStudyTypeService() {
        return studyTypeService;
    }

    public void setStudyTypeService(StudyTypeService studyTypeService) {
        this.studyTypeService = studyTypeService;
    }

	
}
