package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.PlatformDAO;
import net.sourceforge.seqware.common.model.Platform;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>PlatformService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface PlatformService {
  /** Constant <code>NAME="PlatformService"</code> */
  public static final String NAME = "PlatformService";

  /**
   * <p>setPlatformDAO.</p>
   *
   * @param platformDAO a {@link net.sourceforge.seqware.common.dao.PlatformDAO} object.
   */
  public void setPlatformDAO(PlatformDAO platformDAO);

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<Platform> list();
  
  /**
   * <p>list.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.util.List} object.
   */
  public List<Platform> list(Registration registration);

  /**
   * <p>findByID.</p>
   *
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.Platform} object.
   */
  public Platform findByID(Integer id);

  /**
   * <p>updateDetached.</p>
   *
   * @param platform a {@link net.sourceforge.seqware.common.model.Platform} object.
   * @return a {@link net.sourceforge.seqware.common.model.Platform} object.
   */
  Platform updateDetached(Platform platform);

}
