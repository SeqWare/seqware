package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.LibrarySelectionDAO;
import net.sourceforge.seqware.common.model.LibrarySelection;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>LibrarySelectionService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface LibrarySelectionService {
  /** Constant <code>NAME="LibrarySelectionService"</code> */
  public static final String NAME = "LibrarySelectionService";

  /**
   * <p>setLibrarySelectionDAO.</p>
   *
   * @param dao a {@link net.sourceforge.seqware.common.dao.LibrarySelectionDAO} object.
   */
  public void setLibrarySelectionDAO(LibrarySelectionDAO dao);

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<LibrarySelection> list();
  
  /**
   * <p>list.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.util.List} object.
   */
  public List<LibrarySelection> list(Registration registration);

  /**
   * <p>findByID.</p>
   *
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.LibrarySelection} object.
   */
  public LibrarySelection findByID(Integer id);

  /**
   * <p>updateDetached.</p>
   *
   * @param librarySelection a {@link net.sourceforge.seqware.common.model.LibrarySelection} object.
   * @return a {@link net.sourceforge.seqware.common.model.LibrarySelection} object.
   */
  LibrarySelection updateDetached(LibrarySelection librarySelection);

}
