package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.seqware.common.dao.ProcessingLanesDAO;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingLanes;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ProcessingLanesDAOHibernate extends HibernateDaoSupport implements ProcessingLanesDAO {

  @Override
  public void insert(ProcessingLanes processingLanes) {
    this.getHibernateTemplate().save(processingLanes);
  }

  @Override
  public void update(ProcessingLanes processingLanes) {
    this.getHibernateTemplate().update(processingLanes);
  }

  @Override
  public void delete(ProcessingLanes processingLanes) {
    this.getHibernateTemplate().delete(processingLanes);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public ProcessingLanes findByProcessingLane(Processing processing, Lane lane) {
    String query = "from ProcessingLanes as pl where pl.processing.processingId = ? and pl.lane.laneId = ?";
    ProcessingLanes obj = null;
    Object[] parameters = { processing.getProcessingId(), lane.getLaneId() };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (ProcessingLanes) list.get(0);
    }
    return obj;
  }

  @Override
  public ProcessingLanes updateDetached(ProcessingLanes processingLanes) {
    ProcessingLanes dbObject = findByProcessingLane(processingLanes.getProcessing(), processingLanes.getLane());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, processingLanes);
      return (ProcessingLanes) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    @Override
    public List<ProcessingLanes> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
