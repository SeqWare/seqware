package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.OrganismService;
import net.sourceforge.seqware.common.dao.OrganismDAO;
import net.sourceforge.seqware.common.dao.StudyTypeDAO;
import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.Registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>OrganismServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class OrganismServiceImpl implements OrganismService {
  private OrganismDAO organismDAO = null;
  private static final Log log = LogFactory.getLog(OrganismServiceImpl.class);

  /**
   * <p>Constructor for OrganismServiceImpl.</p>
   */
  public OrganismServiceImpl() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * Sets a private member variable with an instance of an implementation of
   * StudyTypeDAO. This method is called by the Spring framework at run time.
   * @see StudyTypeDAO
   */
  public void setOrganismDAO(OrganismDAO organismDAO) {
    this.organismDAO = organismDAO;
  }

  /** {@inheritDoc} */
  public List<Organism> list(Registration registration) {
    return organismDAO.list(registration);
  }

  /** {@inheritDoc} */
  public Organism findByID(Integer id) {
    Organism obj = null;
    if (id != null) {
      try {
        obj = organismDAO.findByID(id);
      } catch (Exception exception) {
        log.error("Cannot find Organism by id " + id);
        log.error(exception.getMessage());
      }
    }
    return obj;
  }

  /** {@inheritDoc} */
  @Override
  public Organism updateDetached(Organism organism) {
    return organismDAO.updateDetached(organism);
  }

    /** {@inheritDoc} */
    @Override
    public List<Organism> list() {
        return organismDAO.list();
    }

}
