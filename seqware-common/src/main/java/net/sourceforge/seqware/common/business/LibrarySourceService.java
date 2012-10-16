package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.LibrarySourceDAO;
import net.sourceforge.seqware.common.model.LibrarySource;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>LibrarySourceService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface LibrarySourceService {
  /** Constant <code>NAME="LibrarySourceService"</code> */
  public static final String NAME = "LibrarySourceService";

  /**
   * <p>setLibrarySourceDAO.</p>
   *
   * @param dao a {@link net.sourceforge.seqware.common.dao.LibrarySourceDAO} object.
   */
  public void setLibrarySourceDAO(LibrarySourceDAO dao);

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<LibrarySource> list();
  
  /**
   * <p>list.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.util.List} object.
   */
  public List<LibrarySource> list(Registration registration);

  /**
   * <p>findByID.</p>
   *
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.LibrarySource} object.
   */
  public LibrarySource findByID(Integer id);

  /**
   * <p>updateDetached.</p>
   *
   * @param librarySource a {@link net.sourceforge.seqware.common.model.LibrarySource} object.
   * @return a {@link net.sourceforge.seqware.common.model.LibrarySource} object.
   */
  LibrarySource updateDetached(LibrarySource librarySource);
}
