package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.LibrarySourceService;
import net.sourceforge.seqware.common.dao.LibrarySourceDAO;
import net.sourceforge.seqware.common.dao.StudyTypeDAO;
import net.sourceforge.seqware.common.model.LibrarySource;
import net.sourceforge.seqware.common.model.Registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LibrarySourceServiceImpl implements LibrarySourceService {
  private LibrarySourceDAO dao = null;
  private static final Log log = LogFactory.getLog(LibrarySourceServiceImpl.class);

  public LibrarySourceServiceImpl() {
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
  public void setLibrarySourceDAO(LibrarySourceDAO dao) {
    this.dao = dao;
  }

  public List<LibrarySource> list(Registration registration) {
    return dao.list(registration);
  }

  public LibrarySource findByID(Integer id) {
    LibrarySource obj = null;
    if (id != null) {
      try {
        obj = dao.findByID(id);
      } catch (Exception exception) {
        log.error("Cannot find LibrarySource by id " + id);
        log.error(exception.getMessage());
      }
    }
    return obj;
  }

  @Override
  public LibrarySource updateDetached(LibrarySource librarySource) {
    return dao.updateDetached(librarySource);
  }

    @Override
    public List<LibrarySource> list() {
        return dao.list();
    }

}
