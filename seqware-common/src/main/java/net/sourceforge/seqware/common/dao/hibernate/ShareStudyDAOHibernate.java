package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.seqware.common.dao.ShareStudyDAO;
import net.sourceforge.seqware.common.model.ShareStudy;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ShareStudyDAOHibernate extends HibernateDaoSupport implements ShareStudyDAO {
  public ShareStudyDAOHibernate() {
    super();
  }

  /**
   * Inserts an instance of ShareStudy into the database.
   */
  public void insert(ShareStudy shareStudy) {
    this.getHibernateTemplate().save(shareStudy);
  }

  /**
   * Updates an instance of ShareStudy in the database.
   */
  public void update(ShareStudy shareStudy) {

    this.getHibernateTemplate().update(shareStudy);
  }

  /**
   * Updates an instance of ShareStudy in the database.
   */
  public void delete(ShareStudy shareStudy) {

    this.getHibernateTemplate().delete(shareStudy);
  }

  /**
   * Finds an instance of ShareStudy in the database by the ShareStudy ID.
   * 
   * @param expID
   *          ID of the ShareStudy
   * @return ShareStudy or null if not found
   */

  public ShareStudy findByStudyIdAndRegistrationId(Integer studyId, Integer registrationId) {
    String query = "from ShareStudy as shareStudy where shareStudy.studyId = ? and shareStudy.registration.registrationId = ?";
    ShareStudy shareStudy = null;
    Object[] parameters = { studyId, registrationId };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      shareStudy = (ShareStudy) list.get(0);
    }
    return shareStudy;
  }

  /**
   * Finds an instance of ShareStudy in the database by the ShareStudy ID.
   * 
   * @param expID
   *          ID of the ShareStudy
   * @return ShareStudy or null if not found
   */
  public ShareStudy findByID(Integer id) {
    String query = "from ShareStudy as shareStudy where shareStudy.shareStudyId = ?";
    ShareStudy shareStudy = null;
    Object[] parameters = { id };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      shareStudy = (ShareStudy) list.get(0);
    }
    return shareStudy;
  }

  @Override
  public ShareStudy updateDetached(ShareStudy shareStudy) {
    ShareStudy dbObject = findByID(shareStudy.getShareStudyId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, shareStudy);
      return (ShareStudy) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    @Override
    public List<ShareStudy> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
