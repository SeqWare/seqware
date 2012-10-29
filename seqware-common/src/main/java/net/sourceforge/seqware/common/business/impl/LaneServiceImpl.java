package net.sourceforge.seqware.common.business.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.dao.IUSDAO;
import net.sourceforge.seqware.common.dao.LaneDAO;
import net.sourceforge.seqware.common.dao.ProcessingDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileType;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.UploadSequence;
import net.sourceforge.seqware.common.module.ReturnValue;

import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>LaneServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LaneServiceImpl implements LaneService {

  private LaneDAO laneDAO = null;
  private ProcessingDAO processingDAO = null;
  private FileDAO fileDAO = null;
  private IUSDAO IUSDAO = null;
  private Logger log = Logger.getLogger(LaneServiceImpl.class);

  /**
   * <p>Constructor for LaneServiceImpl.</p>
   */
  public LaneServiceImpl() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * Sets a private member variable with an instance of an implementation of
   * LaneDAO. This method is called by the Spring framework at run time.
   * @see LaneDAO
   */
  public void setLaneDAO(LaneDAO laneDAO) {
    this.laneDAO = laneDAO;
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * ProcessingDAO. This method is called by the Spring framework at run time.
   *
   * @param processingDAO
   *          implementation of ProcessingDAO
   * @see ProcessingDAO
   */
  public void setProcessingDAO(ProcessingDAO processingDAO) {
    this.processingDAO = processingDAO;
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

  /**
   * Sets a private member variable with an instance of an implementation of
   * IUSDAO. This method is called by the Spring framework at run time.
   *
   * @see iusDAO
   * @param IUSDAO a {@link net.sourceforge.seqware.common.dao.IUSDAO} object.
   */
  public void setIUSDAO(IUSDAO IUSDAO) {
    this.IUSDAO = IUSDAO;
  }

  /** {@inheritDoc} */
  public List<File> getFiles(Integer laneId) {
    return laneDAO.getFiles(laneId);
  }

  /** {@inheritDoc} */
  public List<File> getFiles(Integer studyId, String metaType) {
    return laneDAO.getFiles(studyId, metaType);
  }

  /** {@inheritDoc} */
  public boolean isHasFile(Integer studyId, String metaType) {
    return laneDAO.isHasFile(studyId, metaType);
  }

  /** {@inheritDoc} */
  public boolean isHasFile(Integer laneId) {
    return laneDAO.isHasFile(laneId);
  }

  /** {@inheritDoc} */
  public SortedSet<Lane> setWithHasFile(SortedSet<Lane> list) {
    for (Lane lane : list) {
      lane.setIsHasFile(isHasFile(lane.getLaneId()));
    }
    return list;
  }

  /** {@inheritDoc} */
  public SortedSet<Lane> listWithHasFile(SortedSet<Lane> list, String metaType) {
    SortedSet<Lane> result = new TreeSet<Lane>();
    for (Lane lane : list) {
      boolean isHasFile = isHasFile(lane.getLaneId(), metaType);
      if (isHasFile) {
        // log.debug("ADD LANES ZZZ");
        lane.setIsHasFile(true);
        result.add(lane);
      }
      // lane.setIsHasFile();
    }
    // log.debug("Lanes RESZ ZZZ = " + result.size());
    return result;
  }

  /** {@inheritDoc} */
  public Integer insertLane(Registration registration, Sample sample, UploadSequence uploadSeqence, FileType fileType)
      throws Exception {
    Set<File> files = new TreeSet<File>();
    String folderStore = uploadSeqence.getFolderStore();

    if (uploadSeqence.getUseOneURL()) {
      files.add(createFile(registration, uploadSeqence.getFileURL(), fileType.getMetaType(), folderStore));
    } else {
      MultipartFile fileOne = uploadSeqence.getFileOne();
      if (fileOne != null && !fileOne.isEmpty()) {
        java.io.File uploadFile = fileDAO.saveFile(fileOne, folderStore, registration);
        files.add(createFile(registration, uploadFile.getPath(), fileType.getMetaType(), folderStore));
      }
    }

    if (uploadSeqence.isUsePairedFile()) {
      if (uploadSeqence.getUseTwoURL()) {
        files.add(createFile(registration, uploadSeqence.getFileTwoURL(), fileType.getMetaType(), folderStore));
      } else {
        MultipartFile fileTwo = uploadSeqence.getFileTwo();
        if (fileTwo != null && !fileTwo.isEmpty()) {
          java.io.File uploadFile = fileDAO.saveFile(fileTwo, folderStore, registration);
          files.add(createFile(registration, uploadFile.getPath(), fileType.getMetaType(), folderStore));
        }
      }
    }

    /*
     * if(uploadSeqence.getUseURL()){
     * files.add(createFile(uploadSeqence.getFileURL(), fileType.getMetaType(),
     * folderStore, registration));
     * 
     * if(uploadSeqence.isUsePairedFile()){
     * files.add(createFile(uploadSeqence.getFileTwoURL(),
     * fileType.getMetaType(), folderStore, registration)); } } else{
     * MultipartFile fileOne = uploadSeqence.getFileOne(); MultipartFile fileTwo
     * = uploadSeqence.getFileTwo(); if(fileOne!= null && !fileOne.isEmpty()){
     * java.io.File uploadFile = fileDAO.saveFile(fileOne, folderStore,
     * registration); files.add(createFile(uploadFile.getPath(),
     * fileType.getMetaType(), folderStore, registration)); }
     * 
     * if(fileTwo!=null && !fileTwo.isEmpty()){ java.io.File uploadFile =
     * fileDAO.saveFile(fileTwo, folderStore, registration);
     * files.add(createFile(uploadFile.getPath(), fileType.getMetaType(),
     * folderStore, registration)); } }
     */
    // create new IUS
    IUS ius = new IUS();

    // create new processing
    Processing newProcessing = insertProcessing(registration, files);

    // create new Processing
    Set<Processing> processings = new TreeSet<Processing>();
    processings.add(newProcessing);

    // set new Processing
    ius.setProcessings(processings);

    // create new lane
    Lane lane = new Lane();
    lane.setOwner(sample.getOwner());
    insert(registration, lane);

    // set new Lane
    ius.setLane(lane);
    SortedSet<IUS> newIUS = new TreeSet<IUS>();
    newIUS.add(ius);
    lane.setIUS(newIUS);

    // set IUS into new processing
    newProcessing.setIUS(newIUS);

    // get old IUD
    Set<IUS> oldIUS = sample.getIUS();
    // and add them to new processing
    newIUS.addAll(oldIUS);

    // clear old IUD
    oldIUS.clear();
    // and set new IUD as current
    oldIUS.addAll(newIUS);

    // set exixst Sample
    ius.setSample(sample);
    // sample.setIUS(newIUS);

    // insert new IUS
    ius.setCreateTimestamp(new Date());
    IUSDAO.insert(registration, ius);

    // create new Lane
    // Lane lane = new Lane();
    // lane.setSample(sample);
    // lane.setOwner(sample.getOwner());
    // lane.setProcessings(processings);

    // set lanes
    // newProcessing.getLanes().add(lane);

    // insert(lane);

    return newProcessing.getProcessingId();
  }

  /**
   * <p>updateLane.</p>
   *
   * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
   * @param fileOne a {@link org.springframework.web.multipart.MultipartFile} object.
   * @param fileTwo a {@link org.springframework.web.multipart.MultipartFile} object.
   * @param typeFile a {@link java.lang.String} object.
   * @param folderStore a {@link java.lang.String} object.
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.lang.Integer} object.
   * @throws java.lang.Exception if any.
   */
  public Integer updateLane(Sample sample, MultipartFile fileOne, MultipartFile fileTwo, String typeFile,
      String folderStore, Registration registration) throws Exception {
    /*
     * Set<File> files = new TreeSet<File>();
     * 
     * if(fileOne!= null && !fileOne.isEmpty()){ files.add(createFile(fileOne,
     * typeFile, folderStore, registration)); }
     * 
     * if(fileTwo!=null && !fileTwo.isEmpty()){ files.add(createFile(fileTwo,
     * typeFile, folderStore, registration)); }
     * 
     * IUS ius = getCurrentIUS(sample);
     * 
     * // create new processing Processing newProcessing =
     * insertProcessing(sample.getOwner(), files);
     * 
     * // get old processings Set<Processing> oldProcessings =
     * ius.getProcessings(); // and add them to new processing
     * newProcessing.getChildren().addAll(oldProcessings);
     * 
     * // clear old processings oldProcessings.clear(); // and set new
     * processing as current lane's child oldProcessings.add(newProcessing);
     * 
     * // set lanes newProcessing.getIUS().add(ius);
     * 
     * // update current Lane IUSDAO.update(ius);
     * 
     * // get first Lane // Lane lane = getLane(sample);
     * 
     * // create new processing // Processing newProcessing =
     * insertProcessing(sample.getOwner(), files); // get old processings //
     * Set<Processing> oldProcessings = lane.getProcessings(); // and add them
     * to new processing // newProcessing.getChildren().addAll(oldProcessings);
     * 
     * // clear old processings // oldProcessings.clear(); // and set new
     * processing as current lane's child // oldProcessings.add(newProcessing);
     * 
     * // set lanes // newProcessing.getLanes().add(lane);
     * 
     * // update current Lane // update(lane);
     * 
     * return newProcessing.getProcessingId();
     */
    return null;
  }

  private Processing insertProcessing(Registration owner, Set<File> files) {
    Processing newProcessing = new Processing();
    newProcessing.setOwner(owner);
    newProcessing.setFiles(files);
    // if (newProcessing.getStatus() == null) {
    // newProcessing.setStatus("pending"); }
    newProcessing.setStatus("success");
    newProcessing.setExitStatus(0);
    newProcessing.setProcessExitStatus(0);
    newProcessing.setRunStartTimestamp(null);
    newProcessing.setRunStopTimestamp(null);
    newProcessing.setAlgorithm("upload");
    newProcessing.setCreateTimestamp(new Date());
    processingDAO.insert(owner, newProcessing);

    // status pending, exit_status and process_exit_status null,
    // run_start/stop_tstmp null, algorithm null

    return newProcessing;
  }

  private IUS getCurrentIUS(Sample sample) {
    IUS currentUIS = sample.getIUS().first();
    return currentUIS;
  }

  private Lane getLane(Sample sample) {
    Lane lane = null;
    Set<Lane> lanes = sample.getLanes();

    if (lanes != null && !lanes.isEmpty())
      lane = lanes.iterator().next();
    return lane;
  }

  /** {@inheritDoc} */
  public List<Lane> list(List<Integer> laneIds) {
    if (laneIds == null || laneIds.size() == 0)
      return new ArrayList<Lane>();

    return laneDAO.list(laneIds);
  }

  /**
   * {@inheritDoc}
   *
   * Inserts an instance of Lane into the database.
   */
  public void insert(Lane lane) {
    // lane.setExperimentId(exp.getExperimentId());
    lane.setCreateTimestamp(new Date());
    laneDAO.insert(lane);
  }

  /**
   * {@inheritDoc}
   *
   * Updates an instance of Lane in the database.
   */
  public void update(Lane lane) {
    laneDAO.update(lane);
  }

  /**
   * {@inheritDoc}
   *
   * Deletes an instance of Lane in the database.
   */
  public void delete(Lane lane, String deleteRealFiles) {
    List<File> deleteFiles = null;
    if ("yes".equals(deleteRealFiles)) {
      deleteFiles = laneDAO.getFiles(lane.getLaneId());
    }

    laneDAO.delete(lane);

    if ("yes".equals(deleteRealFiles)) {
      fileDAO.deleteAllWithFolderStore(deleteFiles);
    }
  }

  /**
   * {@inheritDoc}
   *
   * Finds an instance of Lane in the database by the Lane emailAddress, and
   * copies the Lane properties to an instance of Lane.
   */
  public Lane findByName(String name) {
    Lane lane = null;
    if (name != null) {
      try {
        lane = laneDAO.findByName(name.trim().toLowerCase());
      } catch (Exception exception) {
        log.debug("Cannot find Lane by name " + name);
      }
    }
    return lane;
  }

  /** {@inheritDoc} */
  public Lane findByID(Integer laneID) {
    Lane lane = null;
    if (laneID != null) {
      try {
        lane = laneDAO.findByID(laneID);
        // fillInLanes(lane);
      } catch (Exception exception) {
        log.error("Cannot find Lane by expID " + laneID);
        log.error(exception.getMessage());
      }
    }
    return lane;
  }

  /** {@inheritDoc} */
  @Override
  public Lane findBySWAccession(Integer swAccession) {
    Lane lane = null;
    if (swAccession != null) {
      try {
        lane = laneDAO.findBySWAccession(swAccession);
        // fillInLanes(lane);
      } catch (Exception exception) {
        log.error("Cannot find Lane by swAccession " + swAccession);
        log.error(exception.getMessage());
      }
    }
    return lane;
  }

  /** {@inheritDoc} */
  @Override
  public List<Lane> findByOwnerID(Integer registrationId) {
    List<Lane> lanes = null;
    if (registrationId != null) {
      try {
        lanes = laneDAO.findByOwnerID(registrationId);
      } catch (Exception exception) {
        log.error("Cannot find Lane by registrationId " + registrationId);
        log.error(exception.getMessage());
      }
    }
    return lanes;
  }

  /**
   * {@inheritDoc}
   *
   * Determines if an email address has already been used.
   */
  public boolean hasNameBeenUsed(String oldName, String newName) {
    boolean nameUsed = false;
    boolean checkName = true;

    if (newName != null) {

      if (oldName != null) {
        /*
         * We do not want to check if an name address has been used if the user
         * is updating an existing lane and has not changed the nameAddress.
         */
        checkName = !newName.trim().equalsIgnoreCase(oldName.trim());
      }

      if (checkName) {
        Lane lane = this.findByName(newName.trim().toLowerCase());
        if (lane != null) {
          nameUsed = true;
        }
      }
    }
    return nameUsed;
  }

  /**
   * <p>listFile.</p>
   *
   * @param reqistration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param typeNode a {@link java.lang.String} object.
   * @param list a {@link java.util.List} object.
   * @param ids an array of {@link java.lang.String} objects.
   * @param statuses an array of {@link java.lang.String} objects.
   * @return a {@link java.util.List} object.
   */
  public List<File> listFile(Registration reqistration, String typeNode, List<File> list, String[] ids,
      String[] statuses) {
    for (int i = 0; i < ids.length; i++) {

      Integer id = Integer.parseInt(ids[i]);
      Integer status = Integer.parseInt(statuses[i]);
      List<File> files = new ArrayList<File>();

      if (typeNode.equals("lane")) {
        Lane lane = findByID(Integer.parseInt(ids[i]));
        files = getListFile(lane);
      }
      if (typeNode.equals("file")) {
        File file = fileDAO.findByID(id);
        files.add(file);
      }

      if (status == 0) {
        log.debug("Remove files");
        // list.removeAll(getListFile(lane));
        list = removeFiles(list, files);
      } else {
        log.debug("Add files");
        // list.addAll(getListFile(lane));
        list = addFiles(list, files);

        // add elements, including duplicates
        /*
         * Set set = new TreeSet(); set.addAll(list); list.clear();
         * list.addAll(set);
         */
      }

      /*
       * log.debug("Lane id:" + ids[i] + "; Files size:" +
       * list.size()); for(File file : list){
       * log.debug(file.getFileName()); }
       */
    }
    return list;
  }

  private List<File> addFiles(List<File> list, List<File> addList) {
    for (File addFile : addList) {
      boolean isAdd = true;
      for (File file : list) {
        if (file.getFileId().equals(addFile.getFileId())) {
          isAdd = false;
        }
      }
      if (isAdd) {
        list.add(addFile);
      }
    }
    return list;
  }

  private List<File> removeFiles(List<File> list, List<File> removeList) {
    for (File removefile : removeList) {
      boolean isRemove = false;
      for (File file : list) {
        if (file.getFileId().equals(removefile.getFileId())) {
          isRemove = true;
        }
      }

      if (isRemove) {
        list.remove(removefile);
      }
      /*
       * int size = list.size(); for (int i = 0; i < size; i++) { File file =
       * list.get(i); if(file.getFileId().equals(removefile.getFileId())){
       * list.remove(i); size = size--; } }
       */
    }
    return list;
  }

  private List<File> getListFile(Lane lane) {
    List<File> list = new ArrayList<File>();

    Processing processing = new Processing();
    Set<Processing> processings = lane.getProcessings();

    do {
      list.addAll(getFiles(processings));

      Iterator<Processing> it = processings.iterator();
      if (it.hasNext()) {
        processing = it.next();
      }

      processings = processing.getChildren();
    } while (!processings.isEmpty());

    return list;
  }

  private List<File> getFiles(Set<Processing> processings) {
    List<File> list = new ArrayList<File>();
    for (Processing processing : processings) {
      list.addAll(processing.getFiles());
    }
    return list;
  }

  private File createFile(Registration owner, String filePath, String metaType, String folderStore) throws IOException {
    File file = new File();
    file.setFilePath(filePath);
    file.setOwner(owner);
    file.setMetaType(metaType);
    fileDAO.insert(owner, file);
    return file;
  }

  /** {@inheritDoc} */
  @Override
  public List<Lane> findByCriteria(String criteria, boolean isCaseSens) {
    return laneDAO.findByCriteria(criteria, isCaseSens);
  }

  /** {@inheritDoc} */
  @Override
  public Lane updateDetached(Lane lane) {
    return laneDAO.updateDetached(lane);
  }

    /** {@inheritDoc} */
    @Override
    public List<ReturnValue> findFiles(Integer swAccession) {
        return laneDAO.findFiles(swAccession);
    }

    /** {@inheritDoc} */
    @Override
    public List<Lane> list() {
        return laneDAO.list();
    }

    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, Lane lane) {
        laneDAO.update(registration, lane);
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(Registration registration, Lane lane) {
        lane.setCreateTimestamp(new Date());
	return(laneDAO.insert(registration, lane));
    }

    /** {@inheritDoc} */
    @Override
    public Lane updateDetached(Registration registration, Lane lane) {
        return laneDAO.updateDetached(registration, lane);
    }
}

// ex:sw=4:ts=4:
