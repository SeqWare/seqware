package net.sourceforge.seqware.common.business.impl;

import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.seqware.common.business.FileUploadService;
import net.sourceforge.seqware.common.business.UploadFile;
import net.sourceforge.seqware.common.dao.ExperimentDAO;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.dao.FileTypeDAO;
import net.sourceforge.seqware.common.dao.IUSDAO;
import net.sourceforge.seqware.common.dao.LaneDAO;
import net.sourceforge.seqware.common.dao.ProcessingDAO;
import net.sourceforge.seqware.common.dao.SampleDAO;
import net.sourceforge.seqware.common.dao.SequencerRunDAO;
import net.sourceforge.seqware.common.dao.StudyDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileType;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileUploadServiceImpl implements FileUploadService {
  private StudyDAO studyDAO = null;
  private ExperimentDAO experimentDAO = null;
  private SampleDAO sampleDAO = null;
  private LaneDAO laneDAO = null;
  private ProcessingDAO processingDAO = null;
  private SequencerRunDAO sequencerRunDAO = null;
  private IUSDAO IUSDAO = null;
  private FileTypeDAO fileTypeDAO = null;
  private FileDAO fileDAO = null;
  private static final Log log = LogFactory.getLog(FileTypeServiceImpl.class);

  public FileUploadServiceImpl() {
    super();
  }

  public void setStudyDAO(StudyDAO studyDAO) {
    this.studyDAO = studyDAO;
  }

  public void setExperimentDAO(ExperimentDAO experimentDAO) {
    this.experimentDAO = experimentDAO;
  }

  public void setSampleDAO(SampleDAO sampleDAO) {
    this.sampleDAO = sampleDAO;
  }

  public void setLaneDAO(LaneDAO laneDAO) {
    this.laneDAO = laneDAO;
  }

  public void setProcessingDAO(ProcessingDAO processingDAO) {
    this.processingDAO = processingDAO;
  }

  public void setSequencerRunDAO(SequencerRunDAO sequencerRunDAO) {
    this.sequencerRunDAO = sequencerRunDAO;
  }

  public void setIUSDAO(IUSDAO IUSDAO) {
    this.IUSDAO = IUSDAO;
  }

  public void setFileTypeDAO(FileTypeDAO fileTypeDAO) {
    this.fileTypeDAO = fileTypeDAO;
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * FileDAO. This method is called by the Spring framework at run time.
   * 
   * @param fileDAO
   *          implementation of FileDAO
   * @see fileDAO
   */
  public void setFileDAO(FileDAO fileDAO) {
    this.fileDAO = fileDAO;
  }

  private File createFile(UploadFile uploadFile, String metaType, Registration owner) throws IOException {
    String filePath = "";
    if (uploadFile.getUseURL()) {
      filePath = uploadFile.getFileURL();
    } else {
      java.io.File f = fileDAO.saveFile(uploadFile.getFile(), uploadFile.getFolderStore(), owner);
      filePath = f.getPath();
    }

    File file = new File();
    file.setFilePath(filePath);
    file.setOwner(owner);
    file.setMetaType(metaType);
    fileDAO.insert(file);
    return file;
  }

  private Processing insertProcessing(Registration owner, Set<File> files) {
    Processing newProcessing = new Processing();
    newProcessing.setOwner(owner);
    newProcessing.setFiles(files);
    newProcessing.setStatus("success");
    newProcessing.setExitStatus(0);
    newProcessing.setProcessExitStatus(0);
    newProcessing.setRunStartTimestamp(null);
    newProcessing.setRunStopTimestamp(null);
    newProcessing.setAlgorithm("upload");
    newProcessing.setCreateTimestamp(new Date());
    processingDAO.insert(newProcessing);
    return newProcessing;
  }

  private Processing insert(UploadFile uploadFile, FileType fileType, Registration registration) throws Exception {
    Set<File> files = new TreeSet<File>();

    // if(file!= null && !file.isEmpty()){
    files.add(createFile(uploadFile, fileType.getMetaType(), registration));
    // }

    // create new processing
    Processing newProcessing = insertProcessing(registration, files);

    return newProcessing;
  }

  @Override
  public void uploadFile(Study study, UploadFile uploadFile, FileType fileType, Registration registration)
      throws Exception {
    // create new processing
    Processing newProcessing = insert(uploadFile, fileType, registration);

    // get old processings
    Set<Processing> oldProcessings = study.getProcessings();

    // and add them to new processing
    oldProcessings.add(newProcessing);

    // add study to new processing
    newProcessing.getStudies().add(study);

    // insert new study
    studyDAO.update(study);
  }

  @Override
  public void uploadFile(Experiment experiment, UploadFile uploadFile, FileType fileType, Registration registration)
      throws Exception {
    // create new processing
    Processing newProcessing = insert(uploadFile, fileType, registration);

    // get old processings
    Set<Processing> oldProcessings = experiment.getProcessings();

    // and add them to new processing
    oldProcessings.add(newProcessing);

    // add study to new processing
    newProcessing.getExperiments().add(experiment);

    // insert new study
    experimentDAO.update(experiment);
  }

  @Override
  public void uploadFile(Sample sample, UploadFile uploadFile, FileType fileType, Registration registration)
      throws Exception {
    // create new processing
    Processing newProcessing = insert(uploadFile, fileType, registration);

    // get old processings
    Set<Processing> oldProcessings = sample.getProcessings();

    // and add them to new processing
    oldProcessings.add(newProcessing);

    // add study to new processing
    newProcessing.getSamples().add(sample);

    // insert new study
    sampleDAO.update(sample);
  }

  @Override
  public void uploadFile(Lane lane, UploadFile uploadFile, FileType fileType, Registration registration)
      throws Exception {
    // create new processing
    Processing newProcessing = insert(uploadFile, fileType, registration);

    // get old processings
    Set<Processing> oldProcessings = lane.getProcessings();

    // and add them to new processing
    oldProcessings.add(newProcessing);

    // add study to new processing
    newProcessing.getLanes().add(lane);

    // insert new study
    laneDAO.update(lane);
  }

  @Override
  public void uploadFile(IUS ius, UploadFile uploadFile, FileType fileType, Registration registration) throws Exception {
    // create new processing
    Processing newProcessing = insert(uploadFile, fileType, registration);

    // get old processings
    Set<Processing> oldProcessings = ius.getProcessings();

    // and add them to new processing
    oldProcessings.add(newProcessing);

    // add study to new processing
    newProcessing.getIUS().add(ius);

    // insert new study
    IUSDAO.update(ius);
  }

  @Override
  public void uploadFile(Processing processing, UploadFile uploadFile, FileType fileType, Registration registration)
      throws Exception {
    // create new processing
    Processing newProcessing = insert(uploadFile, fileType, registration);

    // get old processings
    Set<Processing> oldProcessings = processing.getChildren();

    // and add them to new processing
    oldProcessings.add(newProcessing);

    // add study to new processing
    newProcessing.getParents().add(processing);

    // insert new study
    processingDAO.update(processing);
  }

  @Override
  public void uploadFile(SequencerRun sequencerRun, UploadFile uploadFile, FileType fileType, Registration registration)
      throws Exception {
    // create new processing
    Processing newProcessing = insert(uploadFile, fileType, registration);

    // get old processings
    Set<Processing> oldProcessings = sequencerRun.getProcessings();

    // and add them to new processing
    oldProcessings.add(newProcessing);

    // add study to new processing
    newProcessing.getSequencerRuns().add(sequencerRun);

    // insert new study
    sequencerRunDAO.update(sequencerRun);
  }
}
