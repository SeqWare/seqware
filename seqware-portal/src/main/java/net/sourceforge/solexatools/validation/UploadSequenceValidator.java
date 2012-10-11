package net.sourceforge.solexatools.validation;

import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.FileTypeService;
import net.sourceforge.seqware.common.model.UploadSequence;
import net.sourceforge.seqware.common.util.Log;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

public class UploadSequenceValidator extends UploadFileValidator implements Validator {
	private FileService fileService;
	private FileTypeService fileTypeService;
	
	public UploadSequenceValidator () {
		super();
	}
	
	public boolean supports(Class clazz) {
		return UploadSequence.class.equals(clazz);
	}
	
	/**
	 * Validates the specified Object.
	 *
	 * @param obj the Object to validate
	 * @param errors Errors object for validation errors
	 */
	public void validate(Object obj, Errors errors) {
		UploadSequence comm = (UploadSequence) obj;
		
		if(comm.getSample()==null){
			errors.reject("upload.required.sample");
		}
		
		if(!errors.hasErrors()){
			if(getFileTypeService().list().size() == 0){
				errors.reject("error.upload.file.file.type.empty");
			}
		}
		

		// must be list true protocol if used URL
		if(!errors.hasErrors() && (comm.getUseOneURL() ||  (comm.getUseTwoURL() && comm.isUsePairedFile()) ) ){
			String s = comm.getStrStartURL();
			if(s == null || s.isEmpty()){
				errors.reject("error.upload.file.url.emty.protocol");
			}
		}
		
		Log.info("!!    comm.getUseURL() = " + comm.getUseOneURL());
		Log.info("!!    comm.getUseTwoURL() = " + comm.getUseTwoURL());
		
		if(!errors.hasErrors()){
			boolean isUsedTrueProtocol = true;
			boolean isUsedTrueExtension = true;
			
			// for first file ...
			if(comm.getUseOneURL()){
				// check URL ...
				Log.info("!!!!!!!!!    check URL 1");
				// required URL if selected
				if(comm.getFileURL() == null || comm.getFileURL().isEmpty()){
					errors.rejectValue("fileURL", "error.upload.required.url");
				}
				
				// validate Protocol
				if(!errors.hasFieldErrors("fileURL")){
					String[] trueProtocols = comm.getStrStartURL().split(",");
					isUsedTrueProtocol = this.validateProtocol(comm.getFileURL(), trueProtocols, errors);
				}
				
				// test file extension
				if(!errors.hasFieldErrors("fileURL")){
					String fileName = comm.getFileURL();
					isUsedTrueExtension = this.validateExtension(fileName, "fileURL", comm.getFileTypeId(), errors);
				}	
			}else{
				// check file...
				Log.info("!!!!!!!!!!!! check file 1");
				// file is required
				if(comm.getFileOne() == null || comm.getFileOne().isEmpty()){
					errors.rejectValue("fileOne", "upload.required.file");
				}
				
				// test file extension
				if(!errors.hasFieldErrors("fileOne")){
					String fileName = comm.getFileOne().getOriginalFilename();
					isUsedTrueExtension = this.validateExtension(fileName, "fileOne", comm.getFileTypeId(), errors);
				}	
			}
			
			if(comm.isUsePairedFile()){
				
				// for second file ...
				if(comm.getUseTwoURL()){
					// check URL ...
					
					// required URL if selected
					if(comm.getFileTwoURL() == null || comm.getFileTwoURL().isEmpty()){
						errors.rejectValue("fileTwoURL", "error.upload.required.two.url");
					}
					
					// validate Protocol
					if(!errors.hasFieldErrors("fileTwoURL") && isUsedTrueProtocol){
						String[] trueProtocols = comm.getStrStartURL().split(",");
						this.validateProtocol(comm.getFileTwoURL(), trueProtocols, errors);
					}
					
					// test file extension
					if(!errors.hasFieldErrors("fileTwoURL") && isUsedTrueExtension){
						String fileName = comm.getFileTwoURL();
						this.validateExtension(fileName, "fileTwoURL", comm.getFileTypeId(), errors);
					}	
				}else{
					// check file...
					
					// file is required
					if(comm.getFileTwo() == null || comm.getFileTwo().isEmpty()){
						errors.rejectValue("fileTwo", "error.upload.file.two.required");
					}
					
					// test file extension
					if(!errors.hasFieldErrors("fileTwo") && isUsedTrueExtension){
						String fileName = comm.getFileTwo().getOriginalFilename();
						this.validateExtension(fileName, "fileTwo", comm.getFileTypeId(), errors);
					}	
				}
			}
		}
		
	}
	
	/* TODO: this needs to be made more flexible, there are multiple possible extensions for the same file type */
	public void validateExtension(MultipartFile fileOne, MultipartFile fileTwo, 
			Integer fileTypeId, Errors errors)
	{
		String  trueExtension = getFileTypeService().findByID(fileTypeId).getExtension();

		if(!errors.hasErrors()){
			String firstFileExtension = this.getExtension(fileOne.getOriginalFilename());
			
			Log.info("Ext1 = " + firstFileExtension);
			if(!trueExtension.equals(firstFileExtension)){
				errors.rejectValue("fileOne", "error.upload.bad.first.file.extension", 
						new Object[] {trueExtension}, "bad extension");
			}
			
			if(fileTwo != null && !fileTwo.isEmpty()){
				String secondFileExtension = getExtension(fileTwo.getOriginalFilename());
				if(!trueExtension.equals(secondFileExtension)){
					errors.rejectValue("fileTwo", "error.upload.bad.second.file.extension", 
							new Object[] {trueExtension}, "bad extension");
				}
			}
		}
	}
/*	
	public void validateExists(MultipartFile fileOne, MultipartFile fileTwo, String folderStore, Errors errors){
		
		String firstFileName = fileOne.getOriginalFilename();
		if(getFileService().isExists(firstFileName, folderStore)){
			errors.rejectValue("fileOne", "error.upload.first.file.used"); 
		}
		
		if(fileTwo != null && !fileTwo.isEmpty()){
			String secondFileName = fileTwo.getOriginalFilename();
			if(getFileService().isExists(secondFileName, folderStore)){
				errors.rejectValue("fileTwo", "error.upload.second.file.used");
			}
		}
	}
*/	
	
/*	public void validateFileNameDontMatch(MultipartFile fileOne, MultipartFile fileTwo, Errors errors){
		
		if(fileOne != null && !fileOne.isEmpty() &&	fileTwo != null && !fileTwo.isEmpty()){
		
			String firstFileName = fileOne.getOriginalFilename();
			String secondFileName = fileTwo.getOriginalFilename();
			
			if (errors.getFieldError("fileOne") == null && errors.getFieldError("fileTwo") == null) {
				// DONT END
				if(firstFileName.trim().equals(secondFileName.trim())){
					errors.reject("error.file.names.dont.match");
				}
			}
		}
	}
*/	
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
