package net.sourceforge.seqware.queryengine.webservice.security;

import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.model.Registration;

import net.sourceforge.seqware.common.factory.BeanFactory;
import org.apache.log4j.Logger;

import org.restlet.security.SecretVerifier;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class SeqWareVerifier extends SecretVerifier {

    @Override
    public int verify(String identifier, char[] secret) {
        RegistrationService registrationService = BeanFactory.getRegistrationServiceBean();
        //log.info("SeqWare Verifier called");
        Registration registration = registrationService.findByEmailAddress(identifier);
        Logger.getLogger(SeqWareVerifier.class).debug(registration);
        if (registration != null) {
            String pass = new String(secret).trim();
            if (registration.getPassword()==null)
            {                
                if (pass.isEmpty() || pass.equals("null"))
                {
                    return RESULT_VALID;
                }
                else
                {
                   return RESULT_INVALID; 
                }
            }
            else if (registration.getPassword().equals(pass))
            {
                return RESULT_VALID;
            }
            else
            {
                return RESULT_INVALID;
            }
        }
        else
        {
            return RESULT_MISSING;
        }
    }

}
