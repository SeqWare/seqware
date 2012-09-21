package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.FileTypeService;
import net.sourceforge.seqware.common.dao.FileTypeDAO;
import net.sourceforge.seqware.common.model.FileType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileTypeServiceImpl implements FileTypeService {
  private FileTypeDAO fileTypeDAO = null;
  private static final Log log = LogFactory.getLog(FileTypeServiceImpl.class);

  public FileTypeServiceImpl() {
    super();
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * FileTypeDAO. This method is called by the Spring framework at run time.
   * 
   * @param fileTypeDAO
   *          implementation of FileTypeDAO
   * @see FileTypeDAO
   */
  public void setFileTypeDAO(FileTypeDAO fileTypeDAO) {
    this.fileTypeDAO = fileTypeDAO;
  }

  public List<FileType> list() {
    return fileTypeDAO.list();
  }

  public FileType findByID(Integer id) {
    FileType obj = null;
    if (id != null) {
      try {
        obj = fileTypeDAO.findByID(id);
      } catch (Exception exception) {
        log.error("Cannot find FileType by id " + id);
        log.error(exception.getMessage());
      }
    }
    return obj;
  }

  @Override
  public FileType updateDetached(FileType fileType) {
    return fileTypeDAO.updateDetached(fileType);
  }
}
