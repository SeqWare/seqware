package net.sourceforge.seqware.common.dao;

import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>RegistrationDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface RegistrationDAO {
  /**
   * <p>insert.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   */
  public void insert(Registration registration);

  /**
   * <p>update.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   */
  public void update(Registration registration);

  /**
   * <p>findByEmailAddress.</p>
   *
   * @param emailAddress a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.model.Registration} object.
   */
  public Registration findByEmailAddress(String emailAddress);

  /**
   * <p>findByEmailAddressAndPassword.</p>
   *
   * @param emailAddress a {@link java.lang.String} object.
   * @param password a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.model.Registration} object.
   */
  public Registration findByEmailAddressAndPassword(String emailAddress, String password);

  /**
   * <p>updateDetached.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link net.sourceforge.seqware.common.model.Registration} object.
   */
  public Registration updateDetached(Registration registration);
}
