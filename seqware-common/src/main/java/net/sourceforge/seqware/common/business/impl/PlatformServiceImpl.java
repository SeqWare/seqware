package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.PlatformService;
import net.sourceforge.seqware.common.dao.PlatformDAO;
import net.sourceforge.seqware.common.dao.StudyTypeDAO;
import net.sourceforge.seqware.common.model.Platform;
import net.sourceforge.seqware.common.model.Registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>PlatformServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class PlatformServiceImpl implements PlatformService {
  private PlatformDAO platformDAO = null;
  private static final Log log = LogFactory.getLog(PlatformServiceImpl.class);

  /**
   * <p>Constructor for PlatformServiceImpl.</p>
   */
  public PlatformServiceImpl() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * Sets a private member variable with an instance of an implementation of
   * StudyTypeDAO. This method is called by the Spring framework at run time.
   * @see StudyTypeDAO
   */
  public void setPlatformDAO(PlatformDAO platformDAO) {
    this.platformDAO = platformDAO;
  }

  /** {@inheritDoc} */
  public List<Platform> list(Registration registration) {
    return platformDAO.list(registration);
  }

  /** {@inheritDoc} */
  public Platform findByID(Integer id) {
    Platform obj = null;
    if (id != null) {
      try {
        obj = platformDAO.findByID(id);
      } catch (Exception exception) {
        log.error("Cannot find Platform by id " + id);
        log.error(exception.getMessage());
      }
    }
    return obj;
  }

  /** {@inheritDoc} */
  @Override
  public Platform updateDetached(Platform platform) {
    return platformDAO.updateDetached(platform);
  }

    /** {@inheritDoc} */
    @Override
    public List<Platform> list() {
        return platformDAO.list();
    }
}
