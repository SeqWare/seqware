package net.sourceforge.seqware.common.business.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileServiceImpl implements FileService {

  private FileDAO fileDAO = null;
  private static final Log log = LogFactory.getLog(FileServiceImpl.class);

  public FileServiceImpl() {
    super();
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * FileDAO. This method is called by the Spring framework at run time.
   * 
   * @param fileDAO
   *          implementation of FileDAO
   * @see FileDAO
   */
  public void setFileDAO(FileDAO dao) {
    this.fileDAO = dao;
  }

  /**
   * Inserts an instance of File into the database.
   * 
   * @param fileDAO
   *          instance of FileDAO
   */
  public void insert(File file) {
    fileDAO.insert(file);
  }

  /**
   * Updates an instance of File in the database.
   * 
   * @param file
   *          instance of File
   */
  public void update(File file) {
    fileDAO.update(file);
  }

  public void delete(File file, String deleteRealFiles) {
    Set<Processing> processings = file.getProcessings();

    for (Processing processing : processings) {
      processing.getFiles().remove(file);
    }

    file.getProcessings().clear();

    fileDAO.delete(file);

    if ("yes".equals(deleteRealFiles)) {
      List<File> deleteFiles = new LinkedList<File>();
      deleteFiles.add(file);
      fileDAO.deleteAllWithFolderStore(deleteFiles);
    }

  }

  public void deleteAll(List<File> files, String deleteRealFiles) {
    fileDAO.deleteAll(files);
    if ("yes".equals(deleteRealFiles)) {
      fileDAO.deleteAllWithFolderStore(files);
    }
  }

  public boolean isExists(String fileName, String folderStore) {
    boolean isExists = false;
    String path = folderStore + fileName;
    if (findByPath(path) != null) {
      isExists = true;
    }
    return isExists;
  }

  public List<File> getFiles(Integer fileId) {
    File file = findByID(fileId);
    List<File> files = new ArrayList<File>();
    files.add(file);
    return files;
  }

  public List<File> getFiles(Integer fileId, String metaType) {
    File file = findByID(fileId);
    List<File> files = new ArrayList<File>();
    if (metaType.equals(file.getMetaType())) {
      files.add(file);
    }
    return files;
  }

  public Set<File> setWithHasFile(Set<File> list, String metaType) {
    Set<File> result = new TreeSet<File>();
    for (File file : list) {
      if (metaType.equals(file.getMetaType())) {
        result.add(file);
      }
    }
    return result;
  }

  public File findByPath(String path) {
    File file = null;
    if (path != null) {
      try {
        file = fileDAO.findByPath(path.trim());
      } catch (Exception exception) {
        log.debug("Cannot find file by path " + path);
      }
    }
    return file;
  }

  public File findByID(Integer fileId) {
    File file = null;
    if (fileId != null) {
      try {
        file = fileDAO.findByID(fileId);
      } catch (Exception exception) {
        log.error("Cannot find File by expID " + fileId);
        log.error(exception.getMessage());
      }
    }
    return file;
  }

  @Override
  public File findBySWAccession(Integer swAccession) {
    File file = null;
    if (swAccession != null) {
      try {
        file = fileDAO.findBySWAccession(swAccession);
      } catch (Exception exception) {
        log.error("Cannot find File by swAccession " + swAccession);
        log.error(exception.getMessage());
      }
    }
    return file;
  }

  @Override
  public List<File> findByCriteria(String criteria, boolean isCaseSens) {
    return fileDAO.findByCriteria(criteria, isCaseSens);
  }

  @Override
  public File updateDetached(File file) {
    return fileDAO.updateDetached(file);
  }

  @Override
  public List<File> findByOwnerId(Integer registrationId) {
    List<File> files = null;
    if (registrationId != null) {
      try {
        files = fileDAO.findByOwnerId(registrationId);
      } catch (Exception exception) {
        log.error("Cannot find Files by registrationId " + registrationId);
        log.error(exception.getMessage());
      }
    }
    return files;
  }

    @Override
    public List<File> list() {
        return fileDAO.list();
    }

    @Override
    public void update(Registration registration, File file) {
        fileDAO.update(registration, file);
    }

    @Override
    public void insert(Registration registration, File file) {
        fileDAO.insert(registration, file);
    }

    @Override
    public File updateDetached(Registration registration, File file) {
        return fileDAO.updateDetached(registration, file);
    }
}
