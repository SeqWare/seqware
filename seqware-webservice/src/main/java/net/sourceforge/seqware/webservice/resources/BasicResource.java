/*
 * Copyright (C) 2011 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.webservice.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Registration;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.resource.ResourceException;

import com.google.common.annotations.VisibleForTesting;

/**
 * See
 * https://sourceforge.net/apps/mediawiki/seqware/index.php?title=How_to_extend_the_web_service#Basic_Resource
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class BasicResource extends WadlServerResource {

    protected Map<String, String> queryValues;
    protected List<String> fields;
    protected Registration registration;
    private static RegistrationService registrationService;
    

    /**
     * <p>Constructor for BasicResource.</p>
     */
    public BasicResource() {
        super();
    }
    
    private static RegistrationService getRegistrationService() {
      if (BasicResource.registrationService == null) {
        BasicResource.registrationService = BeanFactory.getRegistrationServiceBean();
      }
      return BasicResource.registrationService;
    }
    
    /**
     * <p>Setter for the field <code>registrationService</code>.</p>
     *
     * @param registrationService a {@link net.sourceforge.seqware.common.business.RegistrationService} object.
     */
    @VisibleForTesting
    protected static void setRegistrationService(RegistrationService registrationService) {
      BasicResource.registrationService = registrationService;
    }

    /** {@inheritDoc} */
    @Override
    protected void doInit() throws ResourceException {
        super.doInit();
        Form form = getRequest().getResourceRef().getQueryAsForm();
        queryValues = form.getValuesMap();
        if (queryValues == null) {
            queryValues = new HashMap<String, String>();
        }
        fields = new ArrayList<String>();

        if (queryValues.containsKey("show")) {
            String[] fieldArr = queryValues.get("show").split(",");
            fields.addAll(Arrays.asList(fieldArr));
        }

    }

    /**
     * <p>authenticate.</p>
     *
     * @throws org.restlet.resource.ResourceException if any.
     */
    protected void authenticate() throws ResourceException {
        registration = BasicResource.getRegistrationService().
                findByEmailAddress(this.getRequest().getChallengeResponse().getIdentifier()); 
    }

    /**
     * <p>testIfNull.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link java.lang.Object} object.
     */
    public static <T> T testIfNull(T o) {
        if (o == null) {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Object cannot be found");
        }
        return o;
    }

    /**
     * Parses the ID field and throws resourceExceptions as needed
     * @return 
     */
    public static int parseClientInt(String attribute) throws ResourceException {
        try{
            return Integer.parseInt(attribute);
        } catch (NumberFormatException e){
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
        }
    } 
}
