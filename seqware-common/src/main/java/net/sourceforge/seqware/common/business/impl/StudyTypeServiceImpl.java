package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.StudyTypeService;
import net.sourceforge.seqware.common.dao.StudyTypeDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.StudyType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StudyTypeServiceImpl implements StudyTypeService {
  private StudyTypeDAO studyTypeDAO = null;
  private static final Log log = LogFactory.getLog(StudyTypeServiceImpl.class);

  public StudyTypeServiceImpl() {
    super();
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * StudyTypeDAO. This method is called by the Spring framework at run time.
   * 
   * @param studyTypeDAO
   *          implementation of StudyTypeDAO
   * @see StudyTypeDAO
   */
  public void setStudyTypeDAO(StudyTypeDAO studyTypeDAO) {
    this.studyTypeDAO = studyTypeDAO;
  }

  /**
   * Inserts an instance of StudyType into the database.
   * 
   * @param studyTypeDAO
   *          instance of StudyTypeDAO
   */
  public void insert(StudyType studyType) {

    studyTypeDAO.insert(studyType);
  }

  /**
   * Updates an instance of StudyType in the database.
   * 
   * @param studyType
   *          instance of StudyType
   */
  public void update(StudyType studyType) {

    studyTypeDAO.update(studyType);

  }

  public List<StudyType> list(Registration registration) {
    return studyTypeDAO.list(registration);
  }

  /**
   * Finds an instance of StudyType in the database by the StudyType
   * emailAddress, and copies the StudyType properties to an instance of
   * StudyType.
   * 
   * @return instance of StudyType, or null if a StudyType cannot be found
   */
  public StudyType findByName(String name) {
    StudyType studyType = null;
    if (name != null) {
      try {
        studyType = studyTypeDAO.findByName(name.trim().toLowerCase());
      } catch (Exception exception) {
        log.debug("Cannot find StudyType by title " + name);
      }
    }
    return studyType;
  }

  public StudyType findByID(Integer studyTypeID) {
    StudyType studyType = null;
    if (studyTypeID != null) {
      try {
        studyType = studyTypeDAO.findByID(studyTypeID);
      } catch (Exception exception) {
        log.error("Cannot find StudyType by studyTypeID " + studyTypeID);
        log.error(exception.getMessage());
      }
    }
    return studyType;
  }

  @Override
  public StudyType updateDetached(StudyType studyType) {
    return studyTypeDAO.updateDetached(studyType);
  }

    @Override
    public List<StudyType> list() {
        return studyTypeDAO.list();
    }

}

// ex:sw=4:ts=4:
