package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ExperimentSpotDesignService;
import net.sourceforge.seqware.common.dao.ExperimentSpotDesignDAO;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExperimentSpotDesignServiceImpl implements ExperimentSpotDesignService {
  private ExperimentSpotDesignDAO dao = null;
  private static final Log log = LogFactory.getLog(ExperimentSpotDesignServiceImpl.class);

  public ExperimentSpotDesignServiceImpl() {
    super();
  }

  public void setExperimentSpotDesignDAO(ExperimentSpotDesignDAO dao) {
    this.dao = dao;
  }

  /**
   * Inserts an instance of ExperimentSpotDesign into the database.
   * 
   * @param experimentDAO
   *          instance of ExperimentSpotDesignDAO
   */
  public void insert(ExperimentSpotDesign obj) {
    dao.insert(obj);
  }

  /**
   * Updates an instance of ExperimentSpotDesign in the database.
   * 
   * @param experiment
   *          instance of ExperimentSpotDesign
   */
  public void update(ExperimentSpotDesign obj) {
    dao.update(obj);
  }

  public ExperimentSpotDesign findByID(Integer id) {
    ExperimentSpotDesign obj = null;
    if (id != null) {
      try {
        obj = dao.findByID(id);
      } catch (Exception exception) {
        log.error("Cannot find ExperimentSpotDesign by id " + id);
        log.error(exception.getMessage());
      }
    }
    return obj;
  }

  @Override
  public ExperimentSpotDesign updateDetached(ExperimentSpotDesign experiment) {
    return dao.updateDetached(experiment);
  }

    @Override
    public List<ExperimentSpotDesign> list() {
        return dao.list();
    }
}
