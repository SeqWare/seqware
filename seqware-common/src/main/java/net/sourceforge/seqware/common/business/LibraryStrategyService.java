package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.LibraryStrategyDAO;
import net.sourceforge.seqware.common.model.LibraryStrategy;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>LibraryStrategyService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface LibraryStrategyService {
  /** Constant <code>NAME="LibraryStrategyService"</code> */
  public static final String NAME = "LibraryStrategyService";

  /**
   * <p>setLibraryStrategyDAO.</p>
   *
   * @param dao a {@link net.sourceforge.seqware.common.dao.LibraryStrategyDAO} object.
   */
  public void setLibraryStrategyDAO(LibraryStrategyDAO dao);

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<LibraryStrategy> list();
  
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
  LibraryStrategy updateDetached(LibraryStrategy strategy);

}
