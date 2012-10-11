package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.OrganismService;
import net.sourceforge.seqware.common.dao.OrganismDAO;
import net.sourceforge.seqware.common.dao.StudyTypeDAO;
import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.Registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OrganismServiceImpl implements OrganismService {
  private OrganismDAO organismDAO = null;
  private static final Log log = LogFactory.getLog(OrganismServiceImpl.class);

  public OrganismServiceImpl() {
    super();
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * StudyTypeDAO. This method is called by the Spring framework at run time.
   * 
   * @param studyTypeDAO
   *          implementation of StudyTypeDAO
   * @see StudyTypeDAO
   */
  public void setOrganismDAO(OrganismDAO organismDAO) {
    this.organismDAO = organismDAO;
  }

  public List<Organism> list(Registration registration) {
    return organismDAO.list(registration);
  }

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

  @Override
  public Organism updateDetached(Organism organism) {
    return organismDAO.updateDetached(organism);
  }

    @Override
    public List<Organism> list() {
        return organismDAO.list();
    }

}
