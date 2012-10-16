package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>OrganismDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface OrganismDAO {
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
  public Organism updateDetached(Organism organism);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<Organism> list();
}
