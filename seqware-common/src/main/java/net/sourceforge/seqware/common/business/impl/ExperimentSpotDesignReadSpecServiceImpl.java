package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ExperimentSpotDesignReadSpecService;
import net.sourceforge.seqware.common.dao.ExperimentSpotDesignReadSpecDAO;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>ExperimentSpotDesignReadSpecServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ExperimentSpotDesignReadSpecServiceImpl implements ExperimentSpotDesignReadSpecService {
  private ExperimentSpotDesignReadSpecDAO dao = null;
  private static final Log log = LogFactory.getLog(ExperimentSpotDesignReadSpecServiceImpl.class);

  /**
   * <p>Constructor for ExperimentSpotDesignReadSpecServiceImpl.</p>
   */
  public ExperimentSpotDesignReadSpecServiceImpl() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public void setExperimentSpotDesignReadSpecDAO(ExperimentSpotDesignReadSpecDAO dao) {
    this.dao = dao;
  }

  /**
   * {@inheritDoc}
   *
   * Inserts an instance of ExperimentSpotDesignReadSpec into the database.
   */
  @Override
  public void insert(ExperimentSpotDesignReadSpec obj) {
    dao.insert(obj);
  }

  /**
   * {@inheritDoc}
   *
   * Updates an instance of ExperimentSpotDesignReadSpec in the database.
   */
  @Override
  public void update(ExperimentSpotDesignReadSpec obj) {
    dao.update(obj);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ExperimentSpotDesignReadSpec obj) {
    dao.delete(obj);
  }

  /** {@inheritDoc} */
  @Override
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

  /** {@inheritDoc} */
  @Override
  public ExperimentSpotDesignReadSpec updateDetached(ExperimentSpotDesignReadSpec experiment) {
    return dao.updateDetached(experiment);
  }

    /** {@inheritDoc} */
    @Override
    public List<ExperimentSpotDesignReadSpec> list() {
        return dao.list();
    }
}
