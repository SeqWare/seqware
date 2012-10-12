package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.LibraryStrategy;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>LibraryStrategyDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface LibraryStrategyDAO {
  /**
   * <p>list.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.util.List} object.
   */
  public List<LibraryStrategy> list(Registration registration);

  /**
   * <p>findByID.</p>
   *
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.LibraryStrategy} object.
   */
  public LibraryStrategy findByID(Integer id);

  /**
   * <p>updateDetached.</p>
   *
   * @param strategy a {@link net.sourceforge.seqware.common.model.LibraryStrategy} object.
   * @return a {@link net.sourceforge.seqware.common.model.LibraryStrategy} object.
   */
  public LibraryStrategy updateDetached(LibraryStrategy strategy);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<LibraryStrategy> list();

}
