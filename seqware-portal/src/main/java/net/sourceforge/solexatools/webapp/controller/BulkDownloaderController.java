package net.sourceforge.solexatools.webapp.controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.filetools.ProvisionFilesUtil;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.BulkUtil;

import org.apache.commons.io.FileUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>BulkDownloaderController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class BulkDownloaderController extends BaseCommandController {
    
	private FileService fileService;
	/** Constant <code>BUFFERSIZE=500*1024</code> */
	public final static int BUFFERSIZE = 500*1024; // 512K buffer
	private final static String SEPARATOR = java.io.File.separator;


	/**
	 * <p>Constructor for BulkDownloaderController.</p>
	 */
	public BulkDownloaderController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}

	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest	 request,
												 HttpServletResponse response)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");
		
		// These are the files to include in the ZIP file
		List<File> filesToZip = BulkUtil.getFiles(request, "unknow");
		
		if(filesToZip.isEmpty()){
			return new ModelAndView("redirect:/myStudyList.htm");
		}

	    // Create the ZIP file
		ServletContext context = this.getServletContext();
    	String folderStore = context.getInitParameter("path.to.upload.directory");
    	
	    String outFilename = folderStore + "outfile-" + new Date() + ".zip";

	    String contextPath = this.getServletContext().getContextPath();
	    
	    Date dateNow = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		StringBuilder strNow = new StringBuilder(dateFormat.format(dateNow));
		
		String pathToTempStore = "webapps" + contextPath +
		SEPARATOR + "temp" + SEPARATOR + registration.getEmailAddress() 
		+ SEPARATOR + strNow + SEPARATOR;
		
		java.io.File tempStore = new java.io.File(pathToTempStore);
		
		if (!tempStore.exists()) {
			tempStore.mkdir();
		} else {
			FileUtils.deleteDirectory(tempStore);
			tempStore.mkdir();
		}
		
	    ProvisionFilesUtil filesUtil = new ProvisionFilesUtil();
	   	downloadRemoteFiles(filesUtil, filesToZip, pathToTempStore);
	    
	    Collection<java.io.File> filesInTemp = FileUtils.listFiles(tempStore, null, false);
	    java.io.File out = new java.io.File(outFilename);
	    FileTools.zipListFileRecursiveOld(new ArrayList<java.io.File>(filesInTemp), out, pathToTempStore, null, false);

	    Log.info("OUTNAME=" + outFilename);
	    java.io.File realFile = new java.io.File(outFilename);
	    int fileSize = (int) realFile.length();
	    String shortName = outFilename.substring(outFilename.lastIndexOf('/')+1);
	    response.setBufferSize(BUFFERSIZE);
	    response.setContentType(this.getServletContext().getMimeType(outFilename));
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + shortName +"\"");
	    response.setContentLength(fileSize);
	  
	    BufferedInputStream in = new BufferedInputStream(new FileInputStream(realFile));
	  
	    FileCopyUtils.copy(in, response.getOutputStream());
	    in.close();
	    response.getOutputStream().flush();
	    response.getOutputStream().close();
	  
	    // delete file
	    realFile.delete();
	    FileUtils.deleteDirectory(tempStore);
	    
		return null;
	}

	private void downloadRemoteFiles(ProvisionFilesUtil util, List<File> filesToZip, String targetPath) throws Exception {
		long size = determineSize(util, filesToZip);
		long maxSize = getMaxBulkDownloadFilesSize();
		if (size > maxSize) {
			throw new Exception("The files you have chosen to download total > " + maxSize + " and cannot be zipped on this web server.  Please download individually.");
		}
		
		for (File file: filesToZip){
			BufferedInputStream input = util.getSourceReader(file.getFilePath(), BUFFERSIZE, 0);
			util.copyToFile(input, targetPath, BUFFERSIZE, file.getFilePath());
		}
	}

	private long determineSize(ProvisionFilesUtil util,
			List<File> filesToZip) throws Exception {
		long size = 0;
		for (File file: filesToZip) {
			size += ProvisionFilesUtil.getFileSize(file.getFilePath());
		}
		return size;
	}

	private long getMaxBulkDownloadFilesSize() {
		ServletContext context = this.getServletContext();
		String maxSizeStr = context.getInitParameter("bulk.download.max.file.size");
    	long maxSize = 200 * 1024 * 1024;
    	try {
    		maxSize = Integer.parseInt(maxSizeStr);
    	} catch (NumberFormatException e) {
    		e.printStackTrace();
    	}
    	return maxSize;
	}

  /**
   * <p>Getter for the field <code>fileService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.FileService} object.
   */
  public FileService getFileService() {
    return fileService;
  }

  /**
   * <p>Setter for the field <code>fileService</code>.</p>
   *
   * @param fileService a {@link net.sourceforge.seqware.common.business.FileService} object.
   */
  public void setFileService(FileService fileService) {
    this.fileService = fileService;
  }
}
