package net.sourceforge.seqware.queryengine.webservice.security;

import io.seqware.util.PasswordStorage;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Registration;
import org.apache.log4j.Logger;
import org.restlet.security.SecretVerifier;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * <p>
 * SeqWareVerifier class.
 * </p>
 * 
 * @author morgantaschuk
 * @version $Id: $Id
 */
public class SeqWareVerifier extends SecretVerifier {

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public int verify(String identifier, char[] secret) {
        RegistrationService registrationService = BeanFactory.getRegistrationServiceBean();
        // log.info("SeqWare Verifier called");
        Registration registration = registrationService.findByEmailAddress(identifier);
        Logger.getLogger(SeqWareVerifier.class).debug(registration);
        if (registration != null) {
            String pass = new String(secret).trim();
            try {
                final boolean b = PasswordStorage.verifyPassword(pass, registration.getPassword());
                if (b){
                    return RESULT_VALID;
                }
            } catch (PasswordStorage.CannotPerformOperationException e) {
                return RESULT_INVALID;
            } catch (PasswordStorage.InvalidHashException e) {
                return RESULT_INVALID;
            }
        } else {
            return RESULT_MISSING;
        }
        return RESULT_INVALID;
    }

}
