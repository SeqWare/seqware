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

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.resource.ClientResource;

/**
 *
 * @author mtaschuk
 */
public class ClientResourceInstance {

    private static ClientResource clientResource = null;

    private ClientResourceInstance() {
    }

    public static ClientResource getChild(String relativeURL) {
        if (clientResource == null) {
            clientResource = new ClientResource("http://localhost:8889");
            Client client = new Client(new Context(), Protocol.HTTP);
            client.getContext().getParameters().add("useForwardedForHeader", "false");
            clientResource.setNext(client);
            clientResource.setFollowingRedirects(false);
            clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "jane.smith@abc.com", "test");
        }
        return clientResource.getChild("/seqware-webservice" + relativeURL);
    }
}
