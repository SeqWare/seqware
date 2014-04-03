/*
 * Copyright (C) 2012 SeqWare
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
package net.sourceforge.seqware.webservice.resources.queries;

import static net.sourceforge.seqware.webservice.resources.BasicResource.parseClientInt;
import net.sourceforge.seqware.common.hibernate.CheckForCycles;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;

/**
 * <p>CycleCheckResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class CycleCheckResource
        extends BasicRestlet {

    /**
     * <p>Constructor for CycleCheckResource.</p>
     *
     * @param context a {@link org.restlet.Context} object.
     */
    public CycleCheckResource(Context context) {
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        String id = request.getAttributes().get("studyId").toString();

        if (request.getMethod().compareTo(Method.GET) == 0) {
            CheckForCycles cfc = new CheckForCycles();
            String results = cfc.checkStudy(parseClientInt(id));
            response.setEntity(results, MediaType.TEXT_PLAIN);
        } else {
            response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
        }

    }
}
