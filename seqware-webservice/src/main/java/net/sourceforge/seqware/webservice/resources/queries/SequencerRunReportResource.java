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

import net.sourceforge.seqware.common.hibernate.SequencerRunReport;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;

/**
 * <p>SequencerRunReportResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class SequencerRunReportResource
        extends BasicRestlet {

    /**
     * <p>Constructor for SequencerRunReportResource.</p>
     *
     * @param context a {@link org.restlet.Context} object.
     */
    public SequencerRunReportResource(Context context) {
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        if (request.getMethod().compareTo(Method.GET) == 0) {
            SequencerRunReport srr = new SequencerRunReport();
            String s = srr.makeReport(request.getChallengeResponse().getIdentifier());
            response.setEntity(s, MediaType.TEXT_PLAIN);
        } else {
            response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
        }

    }
}
