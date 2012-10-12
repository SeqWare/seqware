package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.seqware.common.dao.ShareProcessingDAO;
import net.sourceforge.seqware.common.model.ShareProcessing;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>ShareProcessingDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ShareProcessingDAOHibernate extends HibernateDaoSupport implements ShareProcessingDAO {

  /** {@inheritDoc} */
  @Override
  public void insert(ShareProcessing shareProcessing) {
    this.getHibernateTemplate().save(shareProcessing);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ShareProcessing shareProcessing) {
    this.getHibernateTemplate().update(shareProcessing);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ShareProcessing shareProcessing) {
    this.getHibernateTemplate().delete(shareProcessing);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("rawtypes")
  public ShareProcessing findByID(Integer shareProcessingID) {
    String query = "from ShareProcessing as s where s.shareProcessingId = ?";
    ShareProcessing obj = null;
    Object[] parameters = { shareProcessingID };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (ShareProcessing) list.get(0);
    }
    return obj;
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public List<ShareProcessing> findByOwnerID(Integer registrationID) {
    String query = "from ShareProcessing as s where s.registration.registrationId = ?";
    Object[] parameters = { registrationID };
    return this.getHibernateTemplate().find(query, parameters);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings({ "unchecked" })
  public ShareProcessing findBySWAccession(Integer swAccession) {
    String query = "from ShareProcessing as s where s.swAccession = ?";
    ShareProcessing shareProcessing = null;
    Object[] parameters = { swAccession };
    List<ShareProcessing> list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      shareProcessing = (ShareProcessing) list.get(0);
    }
    return shareProcessing;
  }

  /** {@inheritDoc} */
  @Override
  public ShareProcessing updateDetached(ShareProcessing shareProcessing) {
    ShareProcessing dbObject = findByID(shareProcessing.getShareProcessingId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, shareProcessing);
      return (ShareProcessing) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    /** {@inheritDoc} */
    @Override
    public List<ShareProcessing> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
