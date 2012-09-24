package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.seqware.common.dao.ShareLaneDAO;
import net.sourceforge.seqware.common.model.ShareLane;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ShareLaneDAOHibernate extends HibernateDaoSupport implements ShareLaneDAO {
  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#insert(
   * net.sourceforge.seqware.common.model.ShareLane)
   */
  @Override
  public void insert(ShareLane shareLane) {
    this.getHibernateTemplate().save(shareLane);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#update(
   * net.sourceforge.seqware.common.model.ShareLane)
   */
  @Override
  public void update(ShareLane shareLane) {
    this.getHibernateTemplate().update(shareLane);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#delete(
   * net.sourceforge.seqware.common.model.ShareLane)
   */
  @Override
  public void delete(ShareLane shareLane) {
    this.getHibernateTemplate().delete(shareLane);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#findByID
   * (java.lang.Integer)
   */
  @Override
  @SuppressWarnings("rawtypes")
  public ShareLane findByID(Integer shareLaneID) {
    String query = "from ShareLane as s where s.shareLaneId = ?";
    ShareLane obj = null;
    Object[] parameters = { shareLaneID };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (ShareLane) list.get(0);
    }
    return obj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#findByOwnerID
   * (java.lang.Integer)
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<ShareLane> findByOwnerID(Integer registrationID) {
    String query = "from ShareLane as s where s.registration.registrationId = ?";
    Object[] parameters = { registrationID };
    return this.getHibernateTemplate().find(query, parameters);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#
   * findBySWAccession(java.lang.Integer)
   */
  @Override
  @SuppressWarnings({ "unchecked" })
  public ShareLane findBySWAccession(Integer swAccession) {
    String query = "from ShareLane as s where s.swAccession = ?";
    ShareLane shareLane = null;
    Object[] parameters = { swAccession };
    List<ShareLane> list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      shareLane = (ShareLane) list.get(0);
    }
    return shareLane;
  }

  @Override
  public ShareLane updateDetached(ShareLane shareLane) {
    ShareLane dbObject = findByID(shareLane.getShareLaneId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, shareLane);
      return (ShareLane) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    @Override
    public List<ShareLane> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
