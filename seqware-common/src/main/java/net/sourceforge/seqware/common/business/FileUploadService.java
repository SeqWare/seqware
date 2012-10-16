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

/**
 * <p>FileUploadService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface FileUploadService {
	/** Constant <code>NAME="FileUploadService"</code> */
	public static final String NAME = "FileUploadService";

	/**
	 * <p>uploadFile.</p>
	 *
	 * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
	 * @param uploadFile a {@link net.sourceforge.seqware.common.business.UploadFile} object.
	 * @param fileType a {@link net.sourceforge.seqware.common.model.FileType} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @throws java.lang.Exception if any.
	 */
	public void uploadFile(Study study, UploadFile uploadFile, FileType fileType,
			Registration registration) throws Exception;
	
	/**
	 * <p>uploadFile.</p>
	 *
	 * @param experiment a {@link net.sourceforge.seqware.common.model.Experiment} object.
	 * @param uploadFile a {@link net.sourceforge.seqware.common.business.UploadFile} object.
	 * @param fileType a {@link net.sourceforge.seqware.common.model.FileType} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @throws java.lang.Exception if any.
	 */
	public void uploadFile(Experiment experiment, UploadFile uploadFile, FileType fileType,
			Registration registration) throws Exception;
	
	/**
	 * <p>uploadFile.</p>
	 *
	 * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
	 * @param uploadFile a {@link net.sourceforge.seqware.common.business.UploadFile} object.
	 * @param fileType a {@link net.sourceforge.seqware.common.model.FileType} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @throws java.lang.Exception if any.
	 */
	public void uploadFile(Sample sample, UploadFile uploadFile, FileType fileType,
			Registration registration) throws Exception;
	
	/**
	 * <p>uploadFile.</p>
	 *
	 * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
	 * @param uploadFile a {@link net.sourceforge.seqware.common.business.UploadFile} object.
	 * @param fileType a {@link net.sourceforge.seqware.common.model.FileType} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @throws java.lang.Exception if any.
	 */
	public void uploadFile(Lane lane, UploadFile uploadFile, FileType fileType,
			Registration registration) throws Exception;
	
	/**
	 * <p>uploadFile.</p>
	 *
	 * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
	 * @param uploadFile a {@link net.sourceforge.seqware.common.business.UploadFile} object.
	 * @param fileType a {@link net.sourceforge.seqware.common.model.FileType} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @throws java.lang.Exception if any.
	 */
	public void uploadFile(IUS ius, UploadFile uploadFile, FileType fileType,
			Registration registration) throws Exception;
	
	/**
	 * <p>uploadFile.</p>
	 *
	 * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @param uploadFile a {@link net.sourceforge.seqware.common.business.UploadFile} object.
	 * @param fileType a {@link net.sourceforge.seqware.common.model.FileType} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @throws java.lang.Exception if any.
	 */
	public void uploadFile(Processing processing, UploadFile uploadFile, FileType fileType,
			Registration registration) throws Exception;
	
	/**
	 * <p>uploadFile.</p>
	 *
	 * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
	 * @param uploadFile a {@link net.sourceforge.seqware.common.business.UploadFile} object.
	 * @param fileType a {@link net.sourceforge.seqware.common.model.FileType} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @throws java.lang.Exception if any.
	 */
	public void uploadFile(SequencerRun sequencerRun, UploadFile uploadFile, FileType fileType,
			Registration registration) throws Exception;
	
}
