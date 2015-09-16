package net.sourceforge.seqware.common.business;

import net.sourceforge.seqware.common.dao.RegistrationDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.RegistrationDTO;

/**
 * <p>
 * RegistrationService interface.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface RegistrationService {
    /** Constant <code>NAME="registrationService"</code> */
    public static final String NAME = "registrationService";

    /**
     * <p>
     * setRegistrationDAO.
     * </p>
     *
     * @param registrationDAO
     *            a {@link net.sourceforge.seqware.common.dao.RegistrationDAO} object.
     */
    public void setRegistrationDAO(RegistrationDAO registrationDAO);

    /**
     * <p>
     * insert.
     * </p>
     *
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.RegistrationDTO} object.
     */
    public void insert(RegistrationDTO registration);

    /**
     * <p>
     * update.
     * </p>
     *
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.RegistrationDTO} object.
     */
    public void update(RegistrationDTO registration);

    /**
     * <p>
     * findByEmailAddress.
     * </p>
     *
     * @param emailAddress
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.RegistrationDTO} object.
     */
    public RegistrationDTO findByEmailAddress(String emailAddress);

    /**
     * <p>
     * findByEmailAddressAndPassword.
     * </p>
     *
     * @param emailAddress
     *            a {@link java.lang.String} object.
     * @param password
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.RegistrationDTO} object.
     */
    public RegistrationDTO findByEmailAddressAndPassword(String emailAddress, String password);

    /**
     * <p>
     * hasEmailAddressBeenUsed.
     * </p>
     *
     * @param email
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean hasEmailAddressBeenUsed(String email);

    /**
     * <p>
     * updateDetached.
     * </p>
     *
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link net.sourceforge.seqware.common.model.Registration} object.
     */
    Registration updateDetached(Registration registration);

}

// ex:sw=4:ts=4:
