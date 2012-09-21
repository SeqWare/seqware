package net.sourceforge.seqware.common.business;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.FileType;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;

public interface FileUploadService {
	public static final String NAME = "FileUploadService";

	public void uploadFile(Study study, UploadFile uploadFile, FileType fileType,
			Registration registration) throws Exception;
	
	public void uploadFile(Experiment experiment, UploadFile uploadFile, FileType fileType,
			Registration registration) throws Exception;
	
	public void uploadFile(Sample sample, UploadFile uploadFile, FileType fileType,
			Registration registration) throws Exception;
	
	public void uploadFile(Lane lane, UploadFile uploadFile, FileType fileType,
			Registration registration) throws Exception;
	
	public void uploadFile(IUS ius, UploadFile uploadFile, FileType fileType,
			Registration registration) throws Exception;
	
	public void uploadFile(Processing processing, UploadFile uploadFile, FileType fileType,
			Registration registration) throws Exception;
	
	public void uploadFile(SequencerRun sequencerRun, UploadFile uploadFile, FileType fileType,
			Registration registration) throws Exception;
	
}
