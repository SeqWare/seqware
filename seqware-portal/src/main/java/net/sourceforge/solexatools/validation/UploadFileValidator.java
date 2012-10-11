package net.sourceforge.solexatools.validation;

import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.FileTypeService;
import net.sourceforge.seqware.common.business.UploadFile;
import net.sourceforge.seqware.common.util.Log;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

public class UploadFileValidator implements Validator {
	private FileService fileService;
	private FileTypeService fileTypeService;
	
	public UploadFileValidator () {
		super();
	}
	
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return UploadFile.class.equals(clazz);
	}
	
	/**
	 * Validates the specified Object.
	 *
	 * @param obj the Object to validate
	 * @param errors Errors object for validation errors
	 */
	public void validate(Object obj, Errors errors) {
		UploadFile uploadFile = (UploadFile) obj;
		
		if(getFileTypeService().list().size() == 0){
			errors.reject("error.upload.file.file.type.empty");
		}
		
		// must be true protocol
		if(!errors.hasErrors()){
			if(uploadFile.getUseURL()){
				String s = uploadFile.getStrStartURL();
				if(s == null || s.isEmpty()){
					errors.reject("error.upload.file.url.emty.protocol");
				}
			}
		}

    	Log.info("URL = " + uploadFile.getFileURL());
    	Log.info("USE URL ? = " + uploadFile.getUseURL());
    	
    	// file is required
		if(!errors.hasErrors()){
			if(uploadFile.getUseURL()){
				if(uploadFile.getFileURL() == null || uploadFile.getFileURL().isEmpty()){
					errors.rejectValue("file", "error.upload.file.required");
				}
			}else{
				if(uploadFile.getFile() == null || uploadFile.getFile().isEmpty()){
					errors.rejectValue("file", "error.upload.file.required");
				}
			}
		}
/*		if(!errors.hasErrors()){
			validateExists(uploadFile.getFile(), uploadFile.getFolderStore(), errors);
		}
*/		
		// validate Protocol
		if(!errors.hasErrors()){
			if(uploadFile.getUseURL()){
				String[] trueProtocols = uploadFile.getStrStartURL().split(",");
				this.validateProtocol(uploadFile.getFileURL(), trueProtocols, errors);
			}
		}
		// test file extension
		if(!errors.hasErrors()){
			String fileName = "";
			if(uploadFile.getUseURL()){
				fileName = uploadFile.getFileURL();
			}else{
				fileName = uploadFile.getFile().getOriginalFilename();
			}
			this.validateExtension(fileName, "fileURL", uploadFile.getFileTypeId(), errors);
		}	
		
		// We don't want upload s3 file. Then not checking for availability. 
		/*
		if(!errors.hasErrors()){
			if (uploadFile.getUseURL()) {
				String url = uploadFile.getFileURL();
				if (url.startsWith("s3://") || url.startsWith("S3://")) {
					this.validateS3ResourceAvailable(url, "fileURL", errors);
				}
			}
		}
		*/
	}
	
	/*
	public boolean validateS3ResourceAvailable(String url, String fieldId, Errors errors) {
		ProvisionFilesUtil filesUtil = new ProvisionFilesUtil();
		BufferedInputStream input = filesUtil.getSourceReader(url, 1024, 0L);
		if (input == null) {
			errors.rejectValue(fieldId, "error.upload.file.url.not.available");
			return false;
		}
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	*/
	
	public boolean validateProtocol(String strUrl, String[] trueProtocols, Errors errors){
		boolean isFound = false;
		for(String protocol : trueProtocols){
			if(strUrl.trim().startsWith(protocol.trim())){
				isFound = true;
			}
		}
		
		if(!isFound){
			errors.reject("error.upload.file.url.false.protocol", 
					new Object[] {getTrueProtocolToString(trueProtocols)}, "Bad protocol.");
		}
		return isFound;
	}
	
	/* TODO: this needs to be made more flexible, there are multiple possible extensions for the same file type */
	public boolean validateExtension(String fileName, String nameField, Integer fileTypeId, Errors errors)
	{		
		boolean isTrueExtension = true;
		String trueFileExtension = getFileTypeService().findByID(fileTypeId).getExtension();
		String fileExtension = getExtension(fileName);

    	Log.info("trueFileExtension = " + trueFileExtension);
    	Log.info("fileExtension = " + fileExtension);
    	
//		Log.info("Ext1 = " + fileExtension);
		if(!trueFileExtension.equals(fileExtension)){
			isTrueExtension = false;
			errors.rejectValue(nameField, "error.upload.file.bad.extension", 
					new Object[] {trueFileExtension}, "Bad extension.");
		}
		
		return isTrueExtension;
	}
	
	protected String getExtension(String name){
		String extension = "";
		int dotPos = name.lastIndexOf(".");
		if(dotPos > -1){
			extension = name.substring(name.lastIndexOf(".") + 1); 
		}
		return extension;
	}
	
	public void validateExists(MultipartFile file, String folderStore, Errors errors){
		String firstFileName = file.getOriginalFilename();
		if(getFileService().isExists(firstFileName, folderStore)){
			errors.rejectValue("fileOne", "error.upload.file.used"); 
		}
	}
	
	private String getTrueProtocolToString(String[] trueProtocols){
		String str = "";
		for (String protocol : trueProtocols) {
			str = str + protocol.trim() + ", ";
		}
		str=str.substring(0, str.length()-2);
		return str;
	}
	
	public FileService getFileService() {
		return fileService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	public FileTypeService getFileTypeService() {
		return fileTypeService;
	}

	public void setFileTypeService(FileTypeService fileTypeService) {
		this.fileTypeService = fileTypeService;
	}
}
