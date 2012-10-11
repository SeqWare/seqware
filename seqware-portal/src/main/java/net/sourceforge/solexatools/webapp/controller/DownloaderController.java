package net.sourceforge.solexatools.webapp.controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.filetools.ProvisionFilesUtil;
import net.sourceforge.solexatools.Security;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * RegistrationSetupController
 */

public class DownloaderController extends BaseCommandController {

  private FileService fileService;
  private final int BUFFERSIZE = 500 * 1024; // 512K buffer

  public DownloaderController() {
    super();
    setSupportedMethods(new String[] { METHOD_GET });
  }

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    Registration registration = Security.getRegistration(request);
    if (registration == null)
      return new ModelAndView("redirect:/login.htm");

    File file = getRequestedFile(request);

    if (file != null) {

      if (file.getFilePath() != null && file.getFilePath().startsWith("s3://")) {
    	  
    	  ProvisionFilesUtil fileUtil = new ProvisionFilesUtil();
    	  URL url = fileUtil.getS3Url(file.getFilePath());
    	  if (url != null) {
    		  response.sendRedirect(response.encodeRedirectURL(url.toString()));
    		  return null;
    	  }

        // if it's an S3 URL we will need to generate a temporary URL and
        // redirect the client to that
        ServletContext context = this.getServletContext();
        String accessKey = context.getInitParameter("s3.accesskey");
        String secretKey = context.getInitParameter("s3.secretkey");
        
        url = fileUtil.getS3Url(file.getFilePath(), accessKey, secretKey);
        response.sendRedirect(response.encodeRedirectURL(url.toString()));
        return null;

      } else if (file.getFilePath() != null
          && (file.getFilePath().startsWith("http://") || file.getFilePath().startsWith("https://"))) {

        response.sendRedirect(response.encodeRedirectURL(file.getFilePath()));

      } else if (file.getFilePath() != null && file.getFilePath().length() > 0) {
        java.io.File realFile = new java.io.File(file.getFilePath());
        int fileSize = (int) realFile.length();
        String shortName = file.getFilePath().substring(file.getFilePath().lastIndexOf('/') + 1);
        response.setBufferSize(BUFFERSIZE);
        response.setContentType(this.getServletContext().getMimeType(file.getFilePath()));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + shortName + "\"");
        response.setContentLength(fileSize);

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(realFile));

        FileCopyUtils.copy(in, response.getOutputStream());
        in.close();
        response.getOutputStream().flush();
        response.getOutputStream().close();
      } else {
        return null;
      }

    } else {
      // should go to an error page saying file not found
      return new ModelAndView("redirect:/login.htm");
    }
    return null;
  }

  /**
   * FIXME: need to add check to make sure user owns file!
   * 
   * @param request
   * @return
   */
  private File getRequestedFile(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    File file = null;
    String id = (String) request.getParameter("fileId");
    if (id != null) {
      Integer fileID = Integer.parseInt(id);
      file = getFileService().findByID(fileID);
      // session.setAttribute("file", file);
    }

    return file;
  }

  public FileService getFileService() {
    return fileService;
  }

  public void setFileService(FileService fileService) {
    this.fileService = fileService;
  }

}
