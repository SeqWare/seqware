package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.seqware.common.dao.ProcessingSamplesDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingSamples;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ProcessingSamplesDAOHibernate extends HibernateDaoSupport implements ProcessingSamplesDAO {
  @Override
  public void insert(ProcessingSamples processingSamples) {
    this.getHibernateTemplate().save(processingSamples);
  }

  @Override
  public void update(ProcessingSamples processingSamples) {
    this.getHibernateTemplate().update(processingSamples);
  }

  @Override
  public void delete(ProcessingSamples processingSamples) {
    this.getHibernateTemplate().delete(processingSamples);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public ProcessingSamples findByProcessingSample(Processing processing, Sample sample) {
    String query = "from ProcessingSamples as ps where ps.processing.processingId = ? and ps.sample.sampleId = ?";
    ProcessingSamples obj = null;
    Object[] parameters = { processing.getProcessingId(), sample.getSampleId() };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (ProcessingSamples) list.get(0);
    }
    return obj;
  }

  @Override
  public ProcessingSamples updateDetached(ProcessingSamples processingSamples) {
    ProcessingSamples dbObject = findByProcessingSample(processingSamples.getProcessing(),
        processingSamples.getSample());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, processingSamples);
      return (ProcessingSamples) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    @Override
    public List<ProcessingSamples> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
