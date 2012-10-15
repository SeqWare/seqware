package net.sourceforge.solexatools.webapp.controller;

import java.io.BufferedInputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.util.filetools.ProvisionFilesUtil;
import net.sourceforge.solexatools.util.BulkUtil;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>BulkDownloaderSizeValidatorController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class BulkDownloaderSizeValidatorController extends
		BaseCommandController {

	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView("Popup");
		try {

			List<File> filesToZip = BulkUtil.getFiles(request, "unknow");
			ProvisionFilesUtil util = new ProvisionFilesUtil();
			long size = determineSize(util, filesToZip);
			long maxSize = getMaxBulkDownloadFilesSize();
			if (size > maxSize) {
				throw new Exception(
						"The files you have chosen to download total > "
								+ maxSize
								+ " and cannot be zipped on this web server.  Please download individually.");
			}

		} catch (Exception e) {
			modelAndView.addObject("res", e.getMessage());
		}
		return modelAndView;
	}

	private long determineSize(ProvisionFilesUtil util, List<File> filesToZip) throws Exception {
		long size = 0;
		for (File file : filesToZip) {
			size += ProvisionFilesUtil.getFileSize(file.getFilePath());
		}
		return size;
	}

	private long getMaxBulkDownloadFilesSize() {
		ServletContext context = this.getServletContext();
		String maxSizeStr = context
				.getInitParameter("bulk.download.max.file.size");
		long maxSize = 200 * 1024 * 1024;
		try {
			maxSize = Integer.parseInt(maxSizeStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return maxSize;
	}
}
