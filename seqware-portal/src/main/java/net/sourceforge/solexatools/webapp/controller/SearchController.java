package net.sourceforge.solexatools.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

public class SearchController extends BaseCommandController {
	
	public final static String SEARCH_CASE_SENSITIVE = "search.checked"; 
	
	private String criteria;
	private String type;

	public SearchController(){
		super();
		setSupportedMethods(new String[] {METHOD_GET, METHOD_POST});
	}
		
	public String getCriteria() {
		return criteria;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Override
//	protected Map referenceData(HttpServletRequest request) throws Exception {
//	Map referenceData = new HashMap();
//	referenceData.put("types", getTypes());
//	return referenceData;
//	}
	 
	private List<String> getTypes() {
		List<String> types = new ArrayList<String>();
		types.add("All");
		types.add("Study");
		types.add("Experiment");
		types.add("Sample");
		types.add("IUS");
		types.add("SequencerRun");
		types.add("Lane");
		types.add("Processing");
		types.add("File");
		types.add("Workflow");
		types.add("WorkflowRun");
		return types;
	}

	@Override
	protected ModelAndView handleRequestInternal(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");
		
		ModelAndView modelAndView = new ModelAndView("Search");
		modelAndView.addObject("types", getTypes());
		String checked = getChecked(request);
		if ("true".equals(checked)) {
			modelAndView.addObject("checked", "checked=\"yes\"");
		} else {
			modelAndView.addObject("checked", "");
		}
		return modelAndView;
	}

	private String getChecked(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String checked = (String) session.getAttribute(SEARCH_CASE_SENSITIVE);
		if (checked == null){
			return null;
		}
		return checked;
	}
	
}
