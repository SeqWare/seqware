package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.LibrarySelectionService;
import net.sourceforge.seqware.common.dao.LibrarySelectionDAO;
import net.sourceforge.seqware.common.dao.StudyTypeDAO;
import net.sourceforge.seqware.common.model.LibrarySelection;
import net.sourceforge.seqware.common.model.Registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>LibrarySelectionServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LibrarySelectionServiceImpl implements LibrarySelectionService {
  private LibrarySelectionDAO dao = null;
  private static final Log log = LogFactory.getLog(LibrarySelectionServiceImpl.class);

  /**
   * <p>Constructor for LibrarySelectionServiceImpl.</p>
   */
  public LibrarySelectionServiceImpl() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * Sets a private member variable with an instance of an implementation of
   * StudyTypeDAO. This method is called by the Spring framework at run time.
   * @see StudyTypeDAO
   */
  public void setLibrarySelectionDAO(LibrarySelectionDAO dao) {
    this.dao = dao;
  }

  /** {@inheritDoc} */
  public List<LibrarySelection> list(Registration registration) {
    return dao.list(registration);
  }

  /** {@inheritDoc} */
  public LibrarySelection findByID(Integer id) {
    LibrarySelection obj = null;
    if (id != null) {
      try {
        obj = dao.findByID(id);
      } catch (Exception exception) {
        log.error("Cannot find LibrarySelection by id " + id);
        log.error(exception.getMessage());
      }
    }
    return obj;
  }

  /** {@inheritDoc} */
  @Override
  public LibrarySelection updateDetached(LibrarySelection librarySelection) {
    return dao.updateDetached(librarySelection);
  }

    /** {@inheritDoc} */
    @Override
    public List<LibrarySelection> list() {
        return dao.list();
    }
}
