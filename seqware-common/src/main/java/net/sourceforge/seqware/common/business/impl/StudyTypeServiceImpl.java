package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.StudyTypeService;
import net.sourceforge.seqware.common.dao.StudyTypeDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.StudyType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>StudyTypeServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StudyTypeServiceImpl implements StudyTypeService {
  private StudyTypeDAO studyTypeDAO = null;
  private static final Log log = LogFactory.getLog(StudyTypeServiceImpl.class);

  /**
   * <p>Constructor for StudyTypeServiceImpl.</p>
   */
  public StudyTypeServiceImpl() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * Sets a private member variable with an instance of an implementation of
   * StudyTypeDAO. This method is called by the Spring framework at run time.
   * @see StudyTypeDAO
   */
  public void setStudyTypeDAO(StudyTypeDAO studyTypeDAO) {
    this.studyTypeDAO = studyTypeDAO;
  }

  /**
   * {@inheritDoc}
   *
   * Inserts an instance of StudyType into the database.
   */
  public void insert(StudyType studyType) {

    studyTypeDAO.insert(studyType);
  }

  /**
   * {@inheritDoc}
   *
   * Updates an instance of StudyType in the database.
   */
  public void update(StudyType studyType) {

    studyTypeDAO.update(studyType);

  }

  /** {@inheritDoc} */
  public List<StudyType> list(Registration registration) {
    return studyTypeDAO.list(registration);
  }

  /**
   * {@inheritDoc}
   *
   * Finds an instance of StudyType in the database by the StudyType
   * emailAddress, and copies the StudyType properties to an instance of
   * StudyType.
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

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override
  public StudyType updateDetached(StudyType studyType) {
    return studyTypeDAO.updateDetached(studyType);
  }

    /** {@inheritDoc} */
    @Override
    public List<StudyType> list() {
        return studyTypeDAO.list();
    }

}

// ex:sw=4:ts=4:
