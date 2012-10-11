package net.sourceforge.seqware.common.business;

import net.sourceforge.seqware.common.dao.RegistrationDAO;
import net.sourceforge.seqware.common.invitation.InvitationParams;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.RegistrationDTO;

import org.springframework.mail.MailSender;

public interface RegistrationService {
  public static final String NAME = "registrationService";

  public void setRegistrationDAO(RegistrationDAO registrationDAO);

  public void insert(RegistrationDTO registration);

  public void update(RegistrationDTO registration);

  public RegistrationDTO findByEmailAddress(String emailAddress);

  public RegistrationDTO findByEmailAddressAndPassword(String emailAddress, String password);

  public boolean hasEmailAddressBeenUsed(String email);

  public void insert(String[] emails, InvitationParams invitationParams, MailSender sender);

  Registration updateDetached(Registration registration);

}

// ex:sw=4:ts=4:
