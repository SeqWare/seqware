package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.FileTypeDAO;
import net.sourceforge.seqware.common.model.FileType;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>FileTypeDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class FileTypeDAOHibernate extends HibernateDaoSupport implements FileTypeDAO {
  /**
   * <p>Constructor for FileTypeDAOHibernate.</p>
   */
  public FileTypeDAOHibernate() {
    super();
  }

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<FileType> list() {
    ArrayList<FileType> fileTypes = new ArrayList<FileType>();

    List expmts = this.getHibernateTemplate().find("from FileType as fileType order by fileType.fileTypeId asc" // desc
    );

    for (Object fileType : expmts) {
      fileTypes.add((FileType) fileType);
    }
    return fileTypes;
  }

  /** {@inheritDoc} */
  public FileType findByID(Integer id) {
    String query = "from FileType as fileType where fileType.fileTypeId = ?";
    FileType obj = null;
    Object[] parameters = { id };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (FileType) list.get(0);
    }
    return obj;
  }

  /** {@inheritDoc} */
  @Override
  public FileType updateDetached(FileType fileType) {
    FileType dbObject = findByID(fileType.getFileTypeId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, fileType);
      return (FileType) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }
}
