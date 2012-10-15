package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.seqware.common.dao.ShareFileDAO;
import net.sourceforge.seqware.common.model.ShareFile;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>ShareFileDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ShareFileDAOHibernate extends HibernateDaoSupport implements ShareFileDAO {

  /** {@inheritDoc} */
  @Override
  public void insert(ShareFile shareFile) {
    this.getHibernateTemplate().save(shareFile);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ShareFile shareFile) {
    this.getHibernateTemplate().update(shareFile);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ShareFile shareFile) {
    this.getHibernateTemplate().delete(shareFile);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("rawtypes")
  public ShareFile findByID(Integer shareFileID) {
    String query = "from ShareFile as s where s.shareFileId = ?";
    ShareFile obj = null;
    Object[] parameters = { shareFileID };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (ShareFile) list.get(0);
    }
    return obj;
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public List<ShareFile> findByOwnerID(Integer registrationID) {
    String query = "from ShareFile as s where s.registration.registrationId = ?";
    Object[] parameters = { registrationID };
    return this.getHibernateTemplate().find(query, parameters);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({ "unchecked" })
  public ShareFile findBySWAccession(Integer swAccession) {
    String query = "from ShareFile as s where s.swAccession = ?";
    ShareFile shareFile = null;
    Object[] parameters = { swAccession };
    List<ShareFile> list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      shareFile = (ShareFile) list.get(0);
    }
    return shareFile;
  }

  /** {@inheritDoc} */
  @Override
  public ShareFile updateDetached(ShareFile shareFile) {
    ShareFile dbObject = findByID(shareFile.getShareFileId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, shareFile);
      return (ShareFile) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    /** {@inheritDoc} */
    @Override
    public List<ShareFile> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
