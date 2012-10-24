package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.dao.FileAttributeDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileAttribute;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
/**
 * <p>FileAttributeDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class FileAttributeDAOHibernate implements FileAttributeDAO {

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  /** {@inheritDoc} */
  @Override
  public List<FileAttribute> getAll() {
    Query query = currentSession().createQuery("from FileAttribute");
    @SuppressWarnings("unchecked")
    List<FileAttribute> records = query.list();
    return records;
  }

  /** {@inheritDoc} */
  @Override
  public List<FileAttribute> get(File file) {
    Query query = currentSession().createQuery("from FileAttribute as f where f.file = :file");
    query.setEntity("file", file);
    @SuppressWarnings("unchecked")
    List<FileAttribute> records = query.list();
    return records;
  }

  /** {@inheritDoc} */
  @Override
  public FileAttribute get(Integer id) {
    return (FileAttribute) currentSession().get(FileAttribute.class, id);
  }

  /** {@inheritDoc} */
  @Override
  public Integer add(FileAttribute fileAttribute) {
    return (Integer) currentSession().save(fileAttribute);
  }

  /** {@inheritDoc} */
  @Override
  public void update(FileAttribute fileAttribute) {
    currentSession().merge(fileAttribute);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(FileAttribute fileAttribute) {
    // Detach from file before deleting attribute.
    File file = fileAttribute.getFile();
    currentSession().evict(file);
    fileAttribute.setFile(null);

    currentSession().delete(fileAttribute);
  }

}
