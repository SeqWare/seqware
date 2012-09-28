package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

public class FileSetupController extends BaseCommandController {

	private FileService fileService;

	public FileService getFileService() {
		return fileService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}
	
	@Override
	protected ModelAndView handleRequestInternal(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView modelAndView = null;
		HashMap <String,String> model = new HashMap<String,String>();

		File file = figureOutFile(request);

		if (file != null) {
			request.setAttribute(getCommandName(), file);
			model.put("strategy", "update");
			modelAndView = new ModelAndView("File", model);
		} 

		return modelAndView;
	}
	
	private File figureOutFile(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		File file = null;

		String id = (String)request.getParameter("fileID");
		if (id != null) {
			file	= fileService.findByID(Integer.parseInt(id));
			session.setAttribute("file", file);
		}

		return file;
	}


}
