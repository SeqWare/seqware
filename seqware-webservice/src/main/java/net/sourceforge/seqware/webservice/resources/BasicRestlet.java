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

import java.util.*;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Registration;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * See
 * https://sourceforge.net/apps/mediawiki/seqware/index.php?title=How_to_extend_the_web_service#Basic_Resource
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class BasicRestlet extends Restlet {

    protected Map<String, String> queryValues;
    protected List<String> fields;
    protected Registration registration;

    /**
     * <p>Constructor for BasicRestlet.</p>
     *
     * @param context a {@link org.restlet.Context} object.
     */
    public BasicRestlet(Context context) {
        super(context);
    }
    /**
     * <p>init.</p>
     *
     * @param request a {@link org.restlet.Request} object.
     */
    protected void init(Request request) {
        Form form = request.getResourceRef().getQueryAsForm();
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
     * @param identifier a {@link java.lang.String} object.
     * @throws org.restlet.resource.ResourceException if any.
     */
    protected void authenticate(String identifier) throws ResourceException {
        registration = BeanFactory.getRegistrationServiceBean().
                findByEmailAddress(identifier);
    }
    
    protected String getQueryValue(String key){  
        if (queryValues != null && queryValues.get(key) != null) {
            return queryValues.get(key);
        } 
        return null;
    }

//    protected Session getSession() {
//        SessionFactory sessionFactory = (SessionFactory) ctx.getBean("sessionFactory");
//        Session session = SessionFactoryUtils.getSession(sessionFactory, true);
//        return session;
//    }
//
//    protected void removeSession(Session session) {
//        session.close();
//    }
}
