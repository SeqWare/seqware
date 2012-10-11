package net.sourceforge.seqware.common.business.impl;

import java.util.Date;

import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.dao.RegistrationDAO;
import net.sourceforge.seqware.common.invitation.InvitationParams;
import net.sourceforge.seqware.common.invitation.RandomInvitationCode;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.RegistrationDTO;
import net.sourceforge.seqware.common.util.Log;

import org.springframework.beans.BeanUtils;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class RegistrationServiceImpl implements RegistrationService {
  private RegistrationDAO registrationDAO = null;

  public RegistrationServiceImpl() {
    super();
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * RegistrationDAO. This method is called by the Spring framework at run time.
   * 
   * @see RegistrationDAO
   */
  public void setRegistrationDAO(RegistrationDAO registrationDAO) {
    Log.stderr("SETTING REGDAO HERE: " + registrationDAO + " " + this);
    this.registrationDAO = registrationDAO;
  }

  /* Inserts an instance of Registration into the database. */
  public void insert(RegistrationDTO registrationDTO) {
    Registration registration = this.populateRegistration(registrationDTO);
    registration.setCreateTimestamp(new Date());
    registrationDAO.insert(registration);
  }

  /* Updates an instance of Registration in the database. */
  public void update(RegistrationDTO registrationDTO) {
    Registration registration = this.populateRegistration(registrationDTO);
    registrationDAO.update(registration);
  }

  public void insert(String[] emails, InvitationParams invitationParams, MailSender sender) {
    String isInvitatonCode = invitationParams.getIsInvitationCode();
    String subjectEmail = invitationParams.getSubjectEmail();
    String templateEmail = invitationParams.getTemplateEmail();

    String patternEmail = "@email@";
    String patternCode = "@invitation.code@";
    String patternEnter = "@enter@";

    RandomInvitationCode ric = new RandomInvitationCode();
    SimpleMailMessage[] mailMessageArray = new SimpleMailMessage[emails.length];
    for (int index = 0; index < emails.length; index++) {
      String email = emails[index].trim().toLowerCase();

      String invitationCode = null;
      if ("true".equals(isInvitatonCode)) {
        invitationCode = ric.nextInvitationCode();
      }

      RegistrationDTO registrationDTO = new RegistrationDTO();
      registrationDTO.setEmailAddress(email);
      registrationDTO.setConfirmEmailAddress(email);

      registrationDTO.setInvitationCode(invitationCode);

      registrationDTO.setFirstName("");
      registrationDTO.setLastName("");
      registrationDTO.setLIMSAdmin(false);
      registrationDTO.setJoinDevelopersMailingList(false);
      registrationDTO.setJoinUsersMailingList(false);

      Log.debug("Email = " + registrationDTO.getEmailAddress());
      insert(registrationDTO);

      // set email param
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(email);

      String textEmail = templateEmail;

      // logger.debug("TEXT1 = " + textEmail);

      textEmail = replace(textEmail, patternEmail, email);
      textEmail = replace(textEmail, patternCode, invitationCode);
      textEmail = replace(textEmail, patternEnter, "\n");

      // logger.debug("TEXT2 = " + textEmail);

      message.setSubject(subjectEmail);
      message.setText(textEmail);

      mailMessageArray[index] = message;
    }

    Log.info("Sending email ....");
    sender.send(mailMessageArray);
  }

  private String replace(String str, String pattern, String replace) {
    int start = 0;
    int end = 0;
    if (replace == null)
      replace = "";
    StringBuffer result = new StringBuffer();
    while ((end = str.indexOf(pattern, start)) >= 0) {
      result.append(str.substring(start, end));
      result.append(replace);
      start = end + pattern.length();
    }
    result.append(str.substring(start));
    str = result.toString();
    return str;
  }

  /**
   * Finds an instance of Registration in the database by the Registration
   * emailAddress, and copies the Registration properties to an instance of
   * RegistrationDTO.
   * 
   * @param emailAddress
   *          emailAddress of the Registration
   * 
   * @return instance of RegistrationDTO, or null if a Registration cannot be
   *         found
   */
  public RegistrationDTO findByEmailAddress(String emailAddress) {

    RegistrationDTO registrationDTO = null;
    if (emailAddress != null) {
      if (registrationDAO == null) {
        Log.stderr("regDAW IS NULL! " + registrationDAO + " " + this);
        return (null);
      }
      try {
        Registration registration = registrationDAO.findByEmailAddress(emailAddress.trim().toLowerCase());
        if (registration != null) {
          registrationDTO = this.populateRegistrationDTO(registration);
        }
      } catch (Exception exception) {
        Log.stderr("EXCEPTION: " + exception.getMessage());
        Log.debug("Cannot find Registration by email address " + emailAddress);
      }
    }
    return registrationDTO;
  }

  /**
   * Finds an instance of Registration in the database by the Registration
   * emailAddress and password, and copies the Registration properties to an
   * instance of RegistrationDTO.
   * 
   * @param emailAddress
   *          emailAddress of the Registration
   * @param password
   *          password of the Registration
   * @return instance of RegistrationDTO, or null if a Registration cannot be
   *         found
   */
  public RegistrationDTO findByEmailAddressAndPassword(String emailAddress, String password) {
    RegistrationDTO registrationDTO = null;
    if (emailAddress != null && password != null) {
      try {
        Registration registration = registrationDAO.findByEmailAddressAndPassword(emailAddress.trim().toLowerCase(),
            password);
        if (registration != null) {
          registrationDTO = this.populateRegistrationDTO(registration);
        }
      } catch (Exception exception) {
        Log.debug("Cannot find Registration by email address " + emailAddress + " and password " + password);
      }
    }
    return registrationDTO;
  }

  /**
   * Determines if an email address has already been used.
   * 
   * @param oldEmail
   *          The previous email address, or null if this method is being called
   *          for a new email address.
   * @param newEmail
   *          The email address that is being checked.
   * @return true if newEmail is in use, false otherwise.
   */
  public boolean hasEmailAddressBeenUsed(String email) {
    /**
     * We do not want to check if an email address has been used if the user is
     * updating an existing registration and has not changed the emailAddress.
     */
    return (findByEmailAddress(email.trim().toLowerCase()) != null);
  }

  /**
   * Copies the properties of an instance of RegistrationDTO to an instance of
   * Registration.
   */
  private Registration populateRegistration(RegistrationDTO registrationDTO) {
    Registration registration = registrationDTO.getDomainObject();
    if (registration == null) {
      registration = new Registration();
      registrationDTO.setDomainObject(registration);
    }
    try {
      registrationDTO.setEmailAddress(registrationDTO.getEmailAddress().trim().toLowerCase());
      registrationDTO.setConfirmEmailAddress(registrationDTO.getConfirmEmailAddress().trim().toLowerCase());
      registrationDTO.setCreateTimestamp(registration.getCreateTimestamp());
      registrationDTO.setUpdateTimestamp(registration.getUpdateTimestamp());
      registrationDTO.setRegistrationId(registration.getRegistrationId());
      BeanUtils.copyProperties(registrationDTO, registration);
    } catch (Exception exception) {
      Log.error("Error copying RegistrationDTO to Registration.");
    }
    return registration;
  }

  /**
   * Copies the properties of an instance of Registration to an instance of
   * RegistrationDTO.
   */
  private RegistrationDTO populateRegistrationDTO(Registration registration) {
    RegistrationDTO registrationDTO = new RegistrationDTO();
    try {
      BeanUtils.copyProperties(registration, registrationDTO);
      registrationDTO.setConfirmEmailAddress(registration.getEmailAddress());
      registrationDTO.setConfirmPassword(registration.getPassword());
      registrationDTO.setDomainObject(registration);
    } catch (Exception exception) {
      Log.stderr("EXCEPTION2: " + exception.getMessage());
      Log.error("Error copying Registration to RegistrationDTO.");
    }
    return registrationDTO;
  }

  @Override
  public Registration updateDetached(Registration registration) {
    return registrationDAO.updateDetached(registration);
  }
}

// ex:sw=4:ts=4:
