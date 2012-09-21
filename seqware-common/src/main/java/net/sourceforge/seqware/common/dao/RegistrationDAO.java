package net.sourceforge.seqware.common.dao;

import net.sourceforge.seqware.common.model.Registration;

public interface RegistrationDAO {
  public void insert(Registration registration);

  public void update(Registration registration);

  public Registration findByEmailAddress(String emailAddress);

  public Registration findByEmailAddressAndPassword(String emailAddress, String password);

  public Registration updateDetached(Registration registration);
}
