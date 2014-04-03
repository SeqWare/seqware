package net.sourceforge.seqware.common.dao.hibernate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import net.sourceforge.seqware.common.util.filetools.ProvisionFilesUtil;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>FileDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class FileDAOHibernate extends HibernateDaoSupport implements FileDAO {

  /**
   * <p>Constructor for FileDAOHibernate.</p>
   */
  public FileDAOHibernate() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * Inserts an instance of File into the database.
   */
  public void insert(File file) {

    this.getHibernateTemplate().save(file);
  }

  /**
   * {@inheritDoc}
   *
   * Updates an instance of File in the database.
   */
  public void update(File file) {

    this.getHibernateTemplate().update(file);
  }

  /**
   * {@inheritDoc}
   *
   * Updates an instance of File in the database.
   */
  public void delete(File file) {

    this.getHibernateTemplate().delete(file);
  }

  /** {@inheritDoc} */
  public void deleteAll(List<File> files) {

    this.getHibernateTemplate().deleteAll(files);
  }

  /** {@inheritDoc} */
  public void deleteAllWithFolderStore(List<File> list) {
    for (File file : list) {
      String fileName = file.getFilePath();
      java.io.File f = new java.io.File(fileName);

      // Make sure the file or directory exists and isn't write protected
      if (!f.exists())
        Log.stderr("Delete: no such file or directory: " + fileName);

      if (!f.canWrite())
        Log.stderr("Delete: write protected: " + fileName);

      // If it is a directory, make sure it is empty
      if (f.isDirectory()) {
        String[] files = f.list();
        if (files.length > 0)
          Log.stderr("Delete: directory not empty: " + fileName);
      }

      // Attempt to delete it
      boolean success = f.delete();

      if (!success)
        Log.stderr("Delete: deletion failed");
    }
  }

  /**
   * {@inheritDoc}
   *
   * Finds an instance of File in the database by the File Path.
   */
  public File findByPath(String path) {
    String query = "from File as file where file.filePath = ?";
    File file = null;
    Object[] parameters = { path };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      file = (File) list.get(0);
    }
    return file;
  }

  /**
   * {@inheritDoc}
   *
   * Finds an instance of File in the database by the File ID.
   */
  public File findByID(Integer id) {
    String query = "from File as file where file.fileId = ?";
    File file = null;
    Object[] parameters = { id };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      file = (File) list.get(0);
    }
    return file;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public File findBySWAccession(Integer swAccession) {
    String query = "from File as file where file.swAccession = ?";
    File file = null;
    Object[] parameters = { swAccession };
    List<File> list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      file = (File) list.get(0);
    }
    return file;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public List<File> findByOwnerId(Integer registrationId) {
    String query = "from File as file where file.owner.registrationId = ?";
    Object[] parameters = { registrationId };
    return this.getHibernateTemplate().find(query, parameters);
  }

  /** {@inheritDoc} */
  public java.io.File saveFile(MultipartFile uploadFile, String folderStore, Registration owner) throws IOException {
    String targetPath = ProvisionFilesUtil.createTargetPath(folderStore, owner.getEmailAddress(), uploadFile
        .getOriginalFilename().replace(" ", "_"));

    // logger.debug("File name = " + fileDownlodName);
    logger.debug("Full Path = " + targetPath);

    InputStream inStream = uploadFile.getInputStream();

    java.io.File file = new java.io.File(targetPath);
    FileOutputStream outStream = new FileOutputStream(file);

    int c;
    while ((c = inStream.read()) != -1) {
      outStream.write(c);
    }
    return file;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public List<File> findByCriteria(String criteria, boolean isCaseSens) {
    String queryStringCase = "from File as f where f.description like :description "
        + " or cast(f.swAccession as string) like :sw or f.filePath like :path order by f.filePath, f.description";
    String queryStringICase = "from File as f where lower(f.description) like :description "
        + " or cast(f.swAccession as string) like :sw or lower(f.filePath) like :path order by f.filePath, f.description";
    Query query = isCaseSens ? this.getSession().createQuery(queryStringCase) : this.getSession().createQuery(
        queryStringICase);
    if (!isCaseSens) {
      criteria = criteria.toLowerCase();
    }
    String criteriaLike = "%" + criteria + "%";
    query.setString("description", criteriaLike);
    query.setString("sw", criteriaLike);
    query.setString("path", criteriaLike);
    List<File> res = query.list();
    removePathResults(res, criteria, isCaseSens);
    return res;
  }

  /**
   * Removes results matching by filePath, but not FileName.
   * 
   * @param files
   * @param crit
   * @param isCaseSens
   */
  private void removePathResults(List<File> files, String crit, boolean isCaseSens) {
    if (files == null || crit == null) {
      return;
    }

    Iterator<File> iter = files.iterator();
    while (iter.hasNext()) {
      File file = iter.next();
      String fileName = null;
      String description = null;
      String criteria = null;
      if (!isCaseSens) {
        if (file.getFileName() != null) {
          fileName = file.getFileName().toLowerCase();
        }
        criteria = crit.toLowerCase();
        if (file.getDescription() != null) {
          description = file.getDescription().toLowerCase();
        }
      } else {
        fileName = file.getFileName();
        criteria = crit;
        description = file.getDescription();
      }
      int matchesResults = 0;
      if (fileName != null && fileName.indexOf(criteria) >= 0) {
        matchesResults++;
      }
      if (description != null && description.indexOf(criteria) >= 0) {
        matchesResults++;
      }
      if (file.getSwAccession().toString().indexOf(criteria) >= 0) {
        matchesResults++;
      }
      if (matchesResults == 0) {
        iter.remove();
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public File updateDetached(File file) {
    File dbObject = findByID(file.getFileId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, file);
      return (File) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public List<File> list() {
    ArrayList<File> l = new ArrayList<File>();

    String query = "from File";

    @SuppressWarnings("unchecked")
    List<File> list = this.getHibernateTemplate().find(query);

    for (File e : list) {
      l.add(e);
    }

    return l;
  }

  /** {@inheritDoc} */
  @Override
  public void update(Registration registration, File file) {
    File dbObject = reattachFile(file);
    Logger logger = Logger.getLogger(FileDAOHibernate.class);
    if (registration == null) {
      logger.error("FileDAOHibernate update registration is null");
    } else if (registration.isLIMSAdmin()
        || (file.givesPermission(registration) && dbObject.givesPermission(registration))) {
      logger.info("updating file object");
      update(file);
    } else {
      logger.error("FileDAOHibernate update not authorized");
    }
  }

  /** {@inheritDoc} */
  @Override
  public void insert(Registration registration, File file) {
    Logger logger = Logger.getLogger(FileDAOHibernate.class);
    if (registration == null) {
      logger.error("FileDAOHibernate insert registration is null");
    } else if (registration.isLIMSAdmin() || file.givesPermission(registration)) {
      logger.info("insert file object");
      insert(file);
    } else {
      logger.error("FileDAOHibernate insert not authorized");
    }
  }

  /** {@inheritDoc} */
  @Override
  public File updateDetached(Registration registration, File file) {
    File dbObject = reattachFile(file);
    Logger logger = Logger.getLogger(FileDAOHibernate.class);
    if (registration == null) {
      logger.error("FileDAOHibernate updateDetached registration is null");
    } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
      logger.info("updateDetached file object");
      return updateDetached(file);
    } else {
      logger.error("FileDAOHibernate updateDetached not authorized");
    }
    return null;
  }

  private File reattachFile(File file) throws IllegalStateException, DataAccessResourceFailureException {
    File dbObject = file;
    if (!getSession().contains(file)) {
      dbObject = findByID(file.getFileId());
    }
    return dbObject;
  }

}
