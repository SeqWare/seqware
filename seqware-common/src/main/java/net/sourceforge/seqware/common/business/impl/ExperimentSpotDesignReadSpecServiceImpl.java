package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ExperimentSpotDesignReadSpecService;
import net.sourceforge.seqware.common.dao.ExperimentSpotDesignReadSpecDAO;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExperimentSpotDesignReadSpecServiceImpl implements ExperimentSpotDesignReadSpecService {
  private ExperimentSpotDesignReadSpecDAO dao = null;
  private static final Log log = LogFactory.getLog(ExperimentSpotDesignReadSpecServiceImpl.class);

  public ExperimentSpotDesignReadSpecServiceImpl() {
    super();
  }

  public void setExperimentSpotDesignReadSpecDAO(ExperimentSpotDesignReadSpecDAO dao) {
    this.dao = dao;
  }

  /**
   * Inserts an instance of ExperimentSpotDesignReadSpec into the database.
   * 
   * @param experimentDAO
   *          instance of ExperimentSpotDesignReadSpecDAO
   */
  public void insert(ExperimentSpotDesignReadSpec obj) {
    dao.insert(obj);
  }

  /**
   * Updates an instance of ExperimentSpotDesignReadSpec in the database.
   * 
   * @param experiment
   *          instance of ExperimentSpotDesignReadSpec
   */
  public void update(ExperimentSpotDesignReadSpec obj) {
    dao.update(obj);
  }

  public void delete(ExperimentSpotDesignReadSpec obj) {
    dao.delete(obj);
  }

  public ExperimentSpotDesignReadSpec findByID(Integer id) {
    ExperimentSpotDesignReadSpec obj = null;
    if (id != null) {
      try {
        obj = dao.findByID(id);
      } catch (Exception exception) {
        log.error("Cannot find ExperimentSpotDesignReadSpec by id " + id);
        log.error(exception.getMessage());
      }
    }
    return obj;
  }

  @Override
  public ExperimentSpotDesignReadSpec updateDetached(ExperimentSpotDesignReadSpec experiment) {
    return dao.updateDetached(experiment);
  }

    @Override
    public List<ExperimentSpotDesignReadSpec> list() {
        return dao.list();
    }
}
