package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.seqware.common.dao.ShareExperimentDAO;
import net.sourceforge.seqware.common.model.ShareExperiment;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ShareExperimentDAOHibernate extends HibernateDaoSupport implements ShareExperimentDAO {

  @Override
  public void insert(ShareExperiment shareExperiment) {
    this.getHibernateTemplate().save(shareExperiment);
  }

  @Override
  public void update(ShareExperiment shareExperiment) {
    this.getHibernateTemplate().update(shareExperiment);
  }

  @Override
  public void delete(ShareExperiment shareExperiment) {
    this.getHibernateTemplate().delete(shareExperiment);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public ShareExperiment findByID(Integer shareExperimentID) {
    String query = "from ShareExperiment as s where s.shareExperimentId = ?";
    ShareExperiment obj = null;
    Object[] parameters = { shareExperimentID };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (ShareExperiment) list.get(0);
    }
    return obj;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ShareExperiment> findByOwnerID(Integer registrationID) {
    String query = "from ShareExperiment as s where s.registration.registrationId = ?";
    Object[] parameters = { registrationID };
    return this.getHibernateTemplate().find(query, parameters);
  }

  @Override
  @SuppressWarnings({ "unchecked" })
  public ShareExperiment findBySWAccession(Integer swAccession) {
    String query = "from ShareExperiment as s where s.swAccession = ?";
    ShareExperiment shareExperiment = null;
    Object[] parameters = { swAccession };
    List<ShareExperiment> list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      shareExperiment = (ShareExperiment) list.get(0);
    }
    return shareExperiment;
  }

  @Override
  public ShareExperiment updateDetached(ShareExperiment shareExperiment) {
    ShareExperiment dbObject = findByID(shareExperiment.getShareExperimentId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, shareExperiment);
      return (ShareExperiment) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    @Override
    public List<ShareExperiment> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
