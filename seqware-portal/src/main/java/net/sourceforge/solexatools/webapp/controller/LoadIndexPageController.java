package net.sourceforge.solexatools.webapp.controller;

import java.io.BufferedInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.filetools.ProvisionFilesUtil;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.HTMLTagValidator;
import net.sourceforge.solexatools.util.ModelUtil;

import org.apache.commons.io.FileUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;
import net.sourceforge.seqware.common.util.Log;

public class LoadIndexPageController extends BaseCommandController {
	private FileService fileService;

	private final static String SEPARATOR = java.io.File.separator;
	private static final long MEGABYTE = 1024L * 1024L;
	private final static String MAIN_FILE = "index.html";
	private final static long WARNING_SIZE = 1024L * 3;

	private HTMLTagValidator htmlTagValidator = new HTMLTagValidator();

	public LoadIndexPageController() {
		super();
		setSupportedMethods(new String[] { METHOD_GET });
	}

	private long bytesToMeg(long bytes) {
		return bytes / MEGABYTE;
	}

	private void removeNavigationAttributeFromSession(HttpServletRequest request) {
		request.getSession(false).removeAttribute("numberCurrentPage");
		request.getSession(false).removeAttribute("listSavedPage");
	}

	private void setNumberCurrentPage(HttpServletRequest request,
			Integer numberCurrentPage) {
		request.getSession(false).setAttribute("numberCurrentPage",
				numberCurrentPage);
	}

	private Integer getNumberCurrentPage(HttpServletRequest request) {
		Integer numberCurrentPage = (Integer) request.getSession(false)
				.getAttribute("numberCurrentPage");
		if (numberCurrentPage == null) {
			numberCurrentPage = 0;
		}
		return numberCurrentPage;
	}

	private String incrementNumberCurrentPage(HttpServletRequest request) {
		Log.info("Call increment ..");
		Integer numberCurrentPage = getNumberCurrentPage(request);
		List<String> listSavedPage = getListSavedPage(request);
		Log.info("Start. Number page = " + numberCurrentPage
				+ "; List page size = " + listSavedPage.size());
		if (numberCurrentPage < listSavedPage.size()) {
			Log.info("Do ++ ");
			numberCurrentPage++;
			setNumberCurrentPage(request, numberCurrentPage);
		}
		Log.info("Start. Number page = " + numberCurrentPage
				+ "; List page size = " + listSavedPage.size());
		return getCurrentPage(request);
	}

	private String decreaseNumberCurrentPage(HttpServletRequest request) {
		Integer numberCurrentPage = getNumberCurrentPage(request);
		Log.info("Call decrease ..");
		Log.info("Start. Number curr page = " + numberCurrentPage);
		if (numberCurrentPage > 1) {
			Log.info("Do -- ");
			numberCurrentPage--;
			setNumberCurrentPage(request, numberCurrentPage);
		}
		Log.info("End. Number curr page = " + numberCurrentPage);
		return getCurrentPage(request);
	}

	private void setListSavedPage(HttpServletRequest request,
			List<String> listSavedPage) {
		request.getSession(false).setAttribute("listSavedPage", listSavedPage);
	}

	private List<String> getListSavedPage(HttpServletRequest request) {
		List<String> listSavedPage = (List<String>) request.getSession(false)
				.getAttribute("listSavedPage");
		if (listSavedPage == null) {
			listSavedPage = new LinkedList<String>();
		}
		return listSavedPage;
	}

	private void addNewPageToTop(HttpServletRequest request, String newPage) {
		Integer numberCurrentPage = getNumberCurrentPage(request);
		List<String> listSavedPage = getListSavedPage(request);

		Log.info("Add new Page to TOP ...");
		Log.info("Start. Number curr page = " + numberCurrentPage
				+ "; List page size = " + listSavedPage.size());

		listSavedPage = listSavedPage.subList(0, numberCurrentPage);
		numberCurrentPage++;
		setNumberCurrentPage(request, numberCurrentPage);
		listSavedPage.add(newPage);
		setListSavedPage(request, listSavedPage);

		Log.info("End. Number curr page = " + numberCurrentPage
				+ "; List page size = " + listSavedPage.size());
	}

	private String getCurrentPage(HttpServletRequest request) {
		Integer numberCurrentPage = getNumberCurrentPage(request);
		List<String> listSavedPage = getListSavedPage(request);
		return listSavedPage.get(numberCurrentPage - 1);
	}

	private String getStartPage(HttpServletRequest request) {
		setNumberCurrentPage(request, 1);
		return getCurrentPage(request);
	}

	private boolean isStartPage(HttpServletRequest request) {
		return (getNumberCurrentPage(request) == 1) ? true : false;
	}

	private boolean isEndPage(HttpServletRequest request) {
		return (getNumberCurrentPage(request) == getListSavedPage(request)
				.size()) ? true : false;
	}

	private String getCurrentPage(HttpServletRequest request, String action) {
		String currentPage = "";
		if ("next".equals(action)) {
			currentPage = incrementNumberCurrentPage(request);
		}
		if ("previous".equals(action)) {
			currentPage = decreaseNumberCurrentPage(request);
		}
		if ("reload".equals(action)) {
			currentPage = getCurrentPage(request);
		}
		if ("home".equals(action)) {
			currentPage = getStartPage(request);
		}
		return currentPage;
	}

	/*
	 * private long getTime(){ return System.currentTimeMillis(); }
	 * 
	 * private boolean isTimeOut(long startTime, long currTime, long maxTime){
	 * boolean isTimeOut = false; if( (currTime-startTime) > maxTime) isTimeOut
	 * = true; return isTimeOut; }
	 */

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// Registration registration = Security.requireRegistration(request,
		// response);
		Registration registration = Security.getRegistration(request);
		if (registration == null)
			return new ModelAndView("redirect:/login.htm");

		/**
		 * Pass registration so that we can filter the list if its appropriate
		 * to do so.
		 */

		request.getSession(false).removeAttribute("isUserAbortedViewIndexPage");

		// long maxTime = 100000;
		// long startTime = System.currentTimeMillis();
		// long currTime = startTime;

		Integer fileId = getRequestedFileId(request);
		File file = getFileService().findByID(fileId);

		String html = "";
		java.io.File indexFile = null;

		// Log.info("IS OWNER");

		String contextPath = this.getServletContext().getContextPath();

		String pathToTempStore = "webapps" + contextPath +
		/* SEPARATOR +"WEB-INF" + */SEPARATOR + "temp";

		// Log.info("Context path = " + contextPath);
		// Log.info("pathToTempStore = " + pathToTempStore);

		java.io.File tempFolder = new java.io.File(pathToTempStore);
		if (!tempFolder.exists()) {
			tempFolder.mkdir();
		}

		String pathToUserTempStore = pathToTempStore + SEPARATOR
				+ registration.getEmailAddress() + SEPARATOR;
		java.io.File userTempFolder = new java.io.File(pathToUserTempStore);

		Date dateNow = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		StringBuilder strNow = new StringBuilder(dateFormat.format(dateNow));

		if (!userTempFolder.exists()) {
			userTempFolder.mkdir();
		} else {
			if (isZipFile(request)) {
				FileUtils.deleteDirectory(userTempFolder);
				userTempFolder.mkdir();
			}
		}

		pathToUserTempStore = pathToUserTempStore + strNow + SEPARATOR;
		userTempFolder = new java.io.File(pathToUserTempStore);

		if (!userTempFolder.exists()) {
			userTempFolder.mkdir();
		}

		String action = getRequestedAction(request);

		if (action != null && !action.equals("")) {
			Log.info("Do OPERATION -> " + action);
			String currPage = getCurrentPage(request, action);
			if (!currPage.endsWith(".html")) {
				// Log.info("FOTO");
				html = "<img src='" + currPage + "'/>";
			} else {
				// Log.info("HTML PAGE");
				indexFile = new java.io.File("webapps" + contextPath
						+ SEPARATOR + currPage);
			}

			// indexFile = new java.io.File("webapps" + contextPath + SEPARATOR
			// + getCurrentPage(request, action));
		} else if (isZipFile(request)
				&& (registration.equals(file.getOwner()) || registration
						.isLIMSAdmin())) {
			removeNavigationAttributeFromSession(request);

			String filePath = file.getFilePath();
			if (filePath.startsWith("s3://")) {
				ProvisionFilesUtil filesUtil = new ProvisionFilesUtil();
				int buffSize = 5000 * 1024; // 5MB
				BufferedInputStream inputStream = filesUtil.getS3InputStream(
						filePath, buffSize, 0);
				if (inputStream == null) {
					// try get access key from context
					ServletContext context = this.getServletContext();
					String accessKey = context.getInitParameter("s3.accesskey");
					String secretKey = context.getInitParameter("s3.secretkey");
					inputStream = filesUtil.getS3InputStream(filePath,
							buffSize, 0, accessKey, secretKey);
				}
				String targetPath = ProvisionFilesUtil.createTargetDirectory(
						userTempFolder.getAbsolutePath(), file.getOwner()
								.getEmailAddress());
				java.io.File f = filesUtil.copyToFile(inputStream, targetPath,
						buffSize, filePath);
				filePath = f.getPath();
			}

			if (filePath.startsWith("http://")) {
				ProvisionFilesUtil filesUtil = new ProvisionFilesUtil();
				int buffSize = 5000 * 1024; // 5MB
				BufferedInputStream inputStream = filesUtil.getHttpInputStream(
						filePath, buffSize, 0);
				String targetPath = ProvisionFilesUtil.createTargetDirectory(
						userTempFolder.getAbsolutePath(), file.getOwner()
								.getEmailAddress());
				java.io.File f = filesUtil.copyToFile(inputStream, targetPath,
						buffSize, filePath);
				filePath = f.getPath();
			}
			java.io.File zip = new java.io.File(filePath);

			ServletContext context = this.getServletContext();
			long warningSize = WARNING_SIZE;
			try {
				warningSize = Long
						.parseLong(context
								.getInitParameter("report.bundle.slow.display.warning.size"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			long sizeMeg = bytesToMeg(zip.length());
			Zip64File zipFile = new Zip64File(zip, true);
			if (sizeMeg > warningSize && isCanAborted(request)) {
				// exit
				ModelAndView modelAndView = new ModelAndView("ResultUnpack");

				modelAndView.addObject("isAborted", true);
				modelAndView.addObject("zipFileName", file.getFileName());
				modelAndView.addObject("isHasError", false);
				modelAndView.addObject("errorMessage", "");
				modelAndView.addObject("pathToIndexPage", "");
				modelAndView.addObject("html", getHtmlListEntity(zipFile));
				modelAndView.addObject("isStartPage", false);
				modelAndView.addObject("isEndPage", false);

				return modelAndView;

			} else {

				FileEntry indexEntity = getIndexEntity(zipFile);

				Log.info("indexEntity != null ?");
				if (indexEntity != null) {
					Log.info("	YES!!!!");
					FileTools.unzipFile(zip, userTempFolder);

					// seach index.html in unpacked directory
					// FileFinder finder = new FileFinder();
					// List<java.io.File> fs =
					// finder.findFiles(pathToUserTempStore, MAIN_FILE);

					// if(fs.size() > 0){
					// indexFile = fs.get(0);
					// }
					String pathIndexFile = pathToUserTempStore
							+ indexEntity.getName();
					Log.info("	pathToUserTempStore = "
							+ pathIndexFile);
					indexFile = new java.io.File(pathIndexFile);
				}
			}
		} else {
			String href = getRequestedHref(request);
			if (href != null) {
				href = href.trim();

				Log.info("HREF = " + href);

				if (!href.endsWith(".html")) {
					Log.info("FOTO");
					html = "<img src='" + href + "'/>";

					addNewPageToTop(request, href);
				} else {
					Log.info("HTML PAGE");
					indexFile = new java.io.File("webapps" + contextPath
							+ SEPARATOR + href);
				}
			}
		}

		if (indexFile == null && isZipFile(request)/* html.equals("") */) {
			// exit
			ModelAndView modelAndView = new ModelAndView("ResultUnpack");

			modelAndView.addObject("isAborted", false);
			modelAndView.addObject("zipFileName", file.getFileName());
			modelAndView.addObject("isHasError", true);
			modelAndView.addObject("errorMessage", "");
			modelAndView.addObject("pathToIndexPage", "");
			modelAndView.addObject("html", "");
			modelAndView.addObject("isStartPage", true);
			modelAndView.addObject("isEndPage", true);

			return modelAndView;
		}

		String pathToIndexPage = "";
		if (html.equals("")) {

			pathToIndexPage = indexFile.getPath().substring(
					indexFile.getPath().lastIndexOf(contextPath)
							+ contextPath.length() + 1);

			String root = "temp/" + registration.getEmailAddress() + "/";
			String page = pathToIndexPage.substring(pathToIndexPage
					.indexOf(root) + root.length());

			// read file to string
			html = FileUtils.readFileToString(indexFile);
			int startPosBody = html.indexOf("<body>");
			int endPosBody = html.lastIndexOf("</body>");
			if (startPosBody != -1 && endPosBody != -1
					&& startPosBody < endPosBody) {
				html = html.substring(startPosBody + "<body>".length(),
						html.lastIndexOf("</body>"));
			}

			html = changeAttr(html, root, page, "img", "src");

			html = changeAttr(html, root, page, "a", "href");

			// html = changeAttr(html, root, page, "link", "href");
			// html = changeAttr(html, root, page, "script", "src");

			if (action == null || action.equals("")) {
				addNewPageToTop(request, pathToIndexPage);
			}

		}
		Boolean isHasError = false;
		String errorMessage = "";

		ModelAndView modelAndView = new ModelAndView("ResultUnpack");

		modelAndView.addObject("isAborted", false);
		modelAndView.addObject("isHasError", isHasError);
		modelAndView.addObject("errorMessage", errorMessage);
		modelAndView.addObject("pathToIndexPage",
				ModelUtil.forJSON(pathToIndexPage));
		modelAndView.addObject("html", ModelUtil.forJSON(html));
		modelAndView.addObject("isStartPage", isStartPage(request));
		modelAndView.addObject("isEndPage", isEndPage(request));

		request.getSession(false).setAttribute("isUserAbortedViewIndexPage",
				false);

		return modelAndView;
	}

	private String changeAttr(String html, String root, String page,
			String tagName, String attrName) {
		int startPos = 0;
		int endPos = 0;
		do {
			startPos = html.indexOf("<", startPos);
			endPos = html.indexOf(">", startPos) + 1;
			if (startPos > -1 && endPos > -1) {
				String tag = html.substring(startPos, endPos);
				String name = "";
				StringTokenizer st = new StringTokenizer(tag, "<> ");
				if (st.hasMoreTokens()) {
					name = st.nextToken();
				}

				if (tagName.equals(name)/* tag.indexOf("a") > -1 */) {
					Log.info("It is foto!!");

					try {
						if (htmlTagValidator.validate(tag)) {
							tag = changeTag(tag, tagName, attrName, root, page);
							html = html.substring(0, startPos) + tag
									+ html.substring(endPos);
						} else {
							System.err.println("Error formay tag:" + tag);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				startPos++;
			}
		} while (startPos > -1);
		// Log.info(html);
		return html;
	}

	private String changeTag(String tag, String tagName, String attrName,
			String root, String page) throws Exception {
		// 1. get attributes and trim
		// tag = tag.substring(tag.indexOf(tagName) + tagName.length()).trim();
		// Log.info("1. attribites:" + tag);

		int startAttrName = tag.indexOf(tagName) + tagName.length();
		int endAttrName = tag.indexOf("=", startAttrName);

		do {
			// 2. get Attr name
			Log.info("startAttrName = " + startAttrName
					+ "; endArrtName = " + endAttrName);
			String currAttrName = tag.substring(startAttrName, endAttrName)
					.trim();
			Log.info("2. Curr attribite name:" + currAttrName);

			if (currAttrName.lastIndexOf(" ") != -1) {
				currAttrName = currAttrName.substring(currAttrName
						.lastIndexOf(" ") + 1);
				Log.info("2.1 Curr attribite name:" + currAttrName);
			}

			int startAttrPos = endAttrName;// tag.indexOf(attrName, 0);

			String bracketSymbol = "\"";
			int i1 = tag.indexOf("\"", startAttrPos);
			int i2 = tag.indexOf("'", startAttrPos);

			Log.info("i1 = " + i1 + "; i2 = " + i2);

			if (i2 < i1 && i1 != -1 && i2 != -1 || i1 == -1 && i2 > 0) {
				Log.info("Small br");
				bracketSymbol = "'";
			} else {
				Log.info("Big br");
			}
			int startImgPath = tag.indexOf(bracketSymbol, startAttrPos);
			int endImgPath = tag.indexOf(bracketSymbol, startImgPath + 1);

			if (startImgPath == -1 || endImgPath == -1) {
				throw new Exception("Error. Bad parce Attr in tag: " + tag);
			}

			System.out
					.println("2.2 '"
							+ tag.substring(endAttrName + 1, startImgPath)
									.trim() + "'");
			if (tag.substring(endAttrName + 1, startImgPath).trim().length() > 0) {
				startAttrName = endAttrName + 1;
				endAttrName = tag.indexOf("=", startAttrName);
				Log.info("2.3");
				continue;
			}

			if (currAttrName.equals(attrName)) {
				String oldPath = tag.substring(startImgPath + 1, endImgPath);

				Log.info("OLD HREF = " + oldPath);
				String newPathToImg = change(root, page, oldPath);

				Log.info("NEW HREF = " + newPathToImg);
				tag = tag.substring(0, startImgPath + 1) + newPathToImg
						+ tag.substring(endImgPath);
				Log.info("NEW TAG = " + tag);

				// return tag;
				endImgPath = endImgPath
						+ (newPathToImg.length() - oldPath.length());
				// html = html.substring(0, startPos) + tag +
				// html.substring(endPos);
				// Log.info(html);
			}
			startAttrName = endImgPath + 1;
			endAttrName = tag.indexOf("=", startAttrName);
		} while (endAttrName != -1);
		return tag;
	}

	private String change(String root, String index, String img) {
		if (index.lastIndexOf("/") != -1) {
			index = index.substring(0, index.lastIndexOf("/"));
		} else {
			index = "";
			root = root.substring(0, root.lastIndexOf("/"));
		}
		String path = "";
		String upDir = "../";

		int startPos = 0;

		int countUpDir = 0;

		do {
			startPos = img.indexOf(upDir, startPos);
			if (startPos > -1) {
				countUpDir++;
				// Log.info("img1 = " + img);
				// img = img.substring(startPos + upDir.length());
				// Log.info("img2 = " + img);
			}
			startPos++;
			// Log.info("startPos = " + startPos);
		} while (startPos > 0);

		// Log.info("img1 = " + img);
		img = img.replace(upDir, "");
		// Log.info("img2 = " + img);

		// Log.info("countDir = " + countUpDir);

		while (countUpDir > 0) {
			// Log.info("index1 = " + index);
			int lastPos = index.lastIndexOf("/");

			if ("".equals(index)) {
				System.err.println("Bad html path");
				return "bad path";
			}

			if (lastPos > -1)
				index = index.substring(0, lastPos);
			else {
				System.err.println("Bad html path");
				return "bad path";
			}
			// Log.info("index2 = " + index);
			countUpDir--;
		}
		path = root + index + "/" + img;
		return path;
	}

	/*
	 * private String changeAttr(String html, String root, String page, String
	 * tagName, String attrName){ int startPos = 0; int endPos = 0; do{ startPos
	 * = html.indexOf("<", startPos); endPos = html.indexOf(">", startPos) + 1;
	 * if(startPos > -1 && endPos > -1) { String tag = html.substring(startPos,
	 * endPos); String name = ""; StringTokenizer st = new StringTokenizer(tag,
	 * "< "); if (st.hasMoreTokens()) { name = st.nextToken(); }
	 * if(tagName.equals(name)){ int startAttrPos = html.indexOf(attrName,
	 * startPos); int startImgPath = html.indexOf("\"", startAttrPos); int
	 * endImgPath = html.indexOf("\"", startImgPath + 1);
	 * 
	 * String oldPath = html.substring(startImgPath + 1, endImgPath); String
	 * newPathToImg = change(root, page, oldPath); html = html.substring(0,
	 * startImgPath + 1) + newPathToImg + html.substring(endImgPath); }
	 * startPos++; } }while(startPos > -1); return html; }
	 * 
	 * private String change(String root, String index, String img){ index =
	 * index.substring(0, index.lastIndexOf("/")); String path = ""; String
	 * upDir = "../";
	 * 
	 * int startPos = 0;
	 * 
	 * int countUpDir = 0;
	 * 
	 * do{ startPos = img.indexOf(upDir, startPos); if(startPos>-1) {
	 * countUpDir++; } startPos++; }while(startPos > 0);
	 * 
	 * img = img.replace(upDir, "");
	 * 
	 * while(countUpDir > 0){ int lastPos = index.lastIndexOf("/"); if(lastPos >
	 * -1) index = index.substring(0, lastPos); else{
	 * System.err.println("Bad path"); return img; } countUpDir--; } path = root
	 * + index + "/" +img; return path; }
	 */
	private String convertDate(Date date, String format) {
		SimpleDateFormat dateFormatDate = new SimpleDateFormat(format);
		StringBuilder strDate = new StringBuilder(dateFormatDate.format(date));
		return strDate.toString();
	}

	public String getFileName(String path) {
		return (path.substring(path.lastIndexOf('/') + 1));
	}

	private FileEntry getIndexEntity(Zip64File zipFile) {
		FileEntry fileEntity = null;
		List<FileEntry> listEntity = zipFile.getListFileEntries();
		for (FileEntry entity : listEntity) {
			if (MAIN_FILE.equals(getFileName(entity.getName()))) {
				fileEntity = entity;
				break;
			}
		}
		return fileEntity;
	}

	private String getHtmlListEntity(Zip64File zipFile) {
		String html = "";
		String header = "<tr> <th>Length</th> <th>Date</th> <th>Time</th> <th>Name</th> </tr>";

		List<FileEntry> listEntity = zipFile.getListFileEntries();
		String lines = "";
		for (FileEntry entity : listEntity) {
			lines = lines + "<tr>" + "<td>" + entity.getSize() + "</td>"
					+ "<td>" + convertDate(entity.getTimestamp(), "MM dd yy")
					+ "</td>" + "<td>"
					+ convertDate(entity.getTimestamp(), "HH mm") + "</td>"
					+ "<td>" + entity.getName() + "</td></tr>";
		}
		html = "<table>" + header + lines + "</table>";
		return html;
	}

	private Integer getRequestedFileId(HttpServletRequest request) {
		Integer fileId = null;
		String strFileId = request.getParameter("fileId");
		if (strFileId != null && !strFileId.equals("")) {
			fileId = Integer.parseInt(strFileId);
		}
		return fileId;
	}

	private String getRequestedHref(HttpServletRequest request) {
		return request.getParameter("href");
	}

	private String getRequestedAction(HttpServletRequest request) {
		return request.getParameter("action");
	}

	private Boolean isZipFile(HttpServletRequest request) {
		return Boolean.parseBoolean(request.getParameter("isZipFile"));
	}

	private Boolean isCanAborted(HttpServletRequest request) {
		return Boolean.parseBoolean(request.getParameter("isCanAborted"));
	}

	public FileService getFileService() {
		return fileService;
	}

	public void setFileService(FileService FileService) {
		this.fileService = FileService;
	}
}
