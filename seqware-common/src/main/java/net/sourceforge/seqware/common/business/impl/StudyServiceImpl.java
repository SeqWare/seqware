package net.sourceforge.seqware.common.business.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.dao.StudyDAO;
import net.sourceforge.seqware.common.dao.StudyTypeDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StudyServiceImpl implements StudyService {

  private StudyDAO studyDAO = null;
  private StudyTypeDAO studyTypeDAO = null;
  private FileDAO fileDAO = null;
  private static final Log log = LogFactory.getLog(StudyServiceImpl.class);

  public StudyServiceImpl() {
    super();
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * StudyDAO. This method is called by the Spring framework at run time.
   * 
   * @param studyDAO
   *          implementation of StudyDAO
   * @see StudyDAO
   */
  public void setStudyDAO(StudyDAO studyDAO) {
    this.studyDAO = studyDAO;
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * FileDAO. This method is called by the Spring framework at run time.
   * 
   * @param fileDAO
   *          implementation of FileDAO
   * @see FileDAO
   */
  public void setFileDAO(FileDAO fileDAO) {
    this.fileDAO = fileDAO;
  }

  /**
   * Inserts an instance of Study into the database.
   * 
   * @param studyDAO
   *          instance of StudyDAO
   */
  public Integer insert(Study study) {

    study.setCreateTimestamp(new Date());

    return (studyDAO.insert(study));
  }

  /**
   * Updates an instance of Study in the database.
   * 
   * @param study
   *          instance of Study
   */
  public void update(Study study) {

    studyDAO.update(study);

  }

  @Override
  public void merge(Study study) {
    studyDAO.merge(study);
  }

  /**
   * Updates an instance of Study in the database.
   * 
   * @param study
   *          instance of Study
   */
  public void delete(Study study, String deleteRealFiles) {
    List<File> deleteFiles = null;
    if ("yes".equals(deleteRealFiles)) {
      deleteFiles = studyDAO.getFiles(study.getStudyId());
    }

    studyDAO.delete(study);

    if ("yes".equals(deleteRealFiles)) {
      fileDAO.deleteAllWithFolderStore(deleteFiles);
    }
  }

  public List<File> getFiles(Integer studyId) {
    return studyDAO.getFiles(studyId);
  }

  public boolean isHasFile(Integer studyId) {
    return studyDAO.isHasFile(studyId);
  }

  public List<File> getFiles(Integer studyId, String metaType) {
    return studyDAO.getFiles(studyId, metaType);
  }

  public boolean isHasFile(Integer studyId, String metaType) {
    return studyDAO.isHasFile(studyId, metaType);
  }

  public List<Study> list(Registration registration) {
    return list(registration, true);
  }

  public List<Study> list(Registration registration, Boolean isAcs) {
    return studyDAO.list(registration, isAcs);
  }

  public List<Study> listMyShared(Registration registration, Boolean isAcs) {
    return studyDAO.listMyShared(registration, isAcs);
  }

  public List<Study> listSharedWithMe(Registration registration) {
    return listSharedWithMe(registration, true);
  }

  public List<Study> listSharedWithMe(Registration registration, Boolean isAcs) {
    return studyDAO.listSharedWithMe(registration, isAcs);
  }

  /**
   * Finds an instance of Study in the database by the Study emailAddress, and
   * copies the Study properties to an instance of Study.
   * 
   * @return instance of Study, or null if a Study cannot be found
   */
  public Study findByTitle(String title) {
    Study study = null;
    if (title != null) {
      try {
        study = studyDAO.findByTitle(title.trim());
      } catch (Exception exception) {
        log.debug("Cannot find Study by title " + title);
      }
    }
    return study;
  }

  public Study findByID(Integer studyID) {
    Study study = null;
    if (studyID != null) {
      try {
        study = studyDAO.findByID(studyID);
      } catch (Exception exception) {
        log.error("Cannot find Study by studyID " + studyID);
        log.error(exception.getMessage());
      }
    }
    return study;
  }

  @Override
  public Study findBySWAccession(Integer swAccession) {
    Study study = null;
    if (swAccession != null) {
      try {
        study = studyDAO.findBySWAccession(swAccession);
      } catch (Exception exception) {
        log.error("Cannot find Study by swAccession " + swAccession);
        log.error(exception.getMessage());
      }
    }
    return study;
  }

  @Override
  public List<Study> findByOwnerID(Integer registrationId) {
    List<Study> studies = null;
    if (registrationId != null) {
      try {
        studies = studyDAO.findByOwnerID(registrationId);
      } catch (Exception exception) {
        log.error("Cannot find Study by registrationId " + registrationId);
        log.error(exception.getMessage());
      }
    }
    return studies;
  }

  @Override
  public List<Study> findByCriteria(String criteria, boolean isCaseSens) {
    return studyDAO.findByCriteria(criteria, isCaseSens);
  }

  /**
   * Determines if an email address has already been used.
   * 
   * @param oldEmail
   *          The previous email address, or null if this method is being called
   *          for a new email address
   * 
   * @param newEmail
   *          The email address that is being checked
   * 
   * @return true if the newEmail has already been used, and false otherwise
   */
  public boolean hasTitleBeenUsed(String oldTitle, String newTitle) {
    boolean titleUsed = false;
    boolean checkTitle = true;

    if (newTitle != null) {
      if (oldTitle != null) {
        /*
         * We do not want to check if an title address has been used if the user
         * is updating an existing sequencer run and has not changed the
         * titleAddress.
         */
        checkTitle = !newTitle.trim().equalsIgnoreCase(oldTitle.trim());
      }

      if (checkTitle) {
        Study study = this.findByTitle(newTitle.trim());
        if (study != null) {
          titleUsed = true;
        }
      }
    }
    return titleUsed;
  }

  public List<Study> listWithHasFile(List<Study> list) {
    for (Study study : list) {
      study.setIsHasFile(isHasFile(study.getStudyId()));
    }
    return list;
  }

  public List<Study> listWithHasFile(List<Study> list, String metaType) {
    List<Study> result = new LinkedList<Study>();
    for (Study study : list) {
      if (isHasFile(study.getStudyId(), metaType)) {
        study.setIsHasFile(true);
        result.add(study);
      }
    }
    return result;
  }

  @Override
  public List<Study> list() {
    return studyDAO.list();
  }

  public List<Study> listStudyHasFile(Registration registration, String metaType, Boolean isAsc) {
    return studyDAO.listStudyHasFile(registration, metaType, isAsc);
  }

  public StudyTypeDAO getStudyTypeDAO() {
    return studyTypeDAO;
  }

  public void setStudyTypeDAO(StudyTypeDAO studyTypeDAO) {
    this.studyTypeDAO = studyTypeDAO;
  }

  public StudyDAO getStudyDAO() {
    return studyDAO;
  }

  @Override
  public Study updateDetached(Study study) {
    return studyDAO.updateDetached(study);
  }

  @Override
  public List<ReturnValue> findFiles(Integer swAccession) {
    return studyDAO.findFiles(swAccession);
  }

  @Override
  public void updateOwners(Integer swAccession) {
    studyDAO.updateOwners(swAccession);
  }

  @Override
  public void update(Registration registration, Study study) {
    studyDAO.update(registration, study);
  }

  @Override
  public Integer insert(Registration registration, Study study) {
    study.setCreateTimestamp(new Date());
    return (studyDAO.insert(registration, study));
  }

  @Override
  public Study updateDetached(Registration registration, Study study) {
    return studyDAO.updateDetached(registration, study);
  }

  @Override
  public int getRunningCount(Study study) {
    return studyDAO.getStatusCount(study, WorkflowRun.RUNNING);
  }

  @Override
  public int getFinishedCount(Study study) {
    return studyDAO.getStatusCount(study, WorkflowRun.FINISHED);
  }

  @Override
  public int getFailedCount(Study study) {
    return studyDAO.getStatusCount(study, WorkflowRun.FAILED);
  }
}

// ex:sw=4:ts=4:
