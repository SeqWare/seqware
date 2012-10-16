package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.OrganismDAO;
import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>OrganismService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface OrganismService {
  /** Constant <code>NAME="OrganismService"</code> */
  public static final String NAME = "OrganismService";

  /**
   * <p>setOrganismDAO.</p>
   *
   * @param platformDAO a {@link net.sourceforge.seqware.common.dao.OrganismDAO} object.
   */
  public void setOrganismDAO(OrganismDAO platformDAO);

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<Organism> list();
  
  /**
   * <p>list.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.util.List} object.
   */
  public List<Organism> list(Registration registration);

  /**
   * <p>findByID.</p>
   *
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.Organism} object.
   */
  public Organism findByID(Integer id);

  /**
   * <p>updateDetached.</p>
   *
   * @param organism a {@link net.sourceforge.seqware.common.model.Organism} object.
   * @return a {@link net.sourceforge.seqware.common.model.Organism} object.
   */
  Organism updateDetached(Organism organism);

}
