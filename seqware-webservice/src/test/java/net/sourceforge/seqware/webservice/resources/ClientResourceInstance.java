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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
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
    private static String webservicePrefix = "/seqware-webservice";

    private ClientResourceInstance() {
    }

    public static ClientResource getChild(String relativeURL) {
        Map<String, String> settings = new HashMap<String, String>();
        if (clientResource == null) {
            String hostURL = "http://localhost:8889"; 
            try {
                settings = ConfigTools.getSettings();
            } catch (Exception e) {
                Log.stderr("Error reading settings file: " + e.getMessage());
            }
            if (settings.containsKey(BasicTestDatabaseCreator.BASIC_TEST_DB_HOST_KEY)) {
                String restURL = settings.get("SW_REST_URL");
                Pattern pattern = Pattern.compile("(https?://.*)(/.*)");
                Matcher matcher = pattern.matcher(restURL);
                matcher.find();
                hostURL = matcher.group(1);
                webservicePrefix = matcher.group(2);
                Log.fatal("Detected overriden hostURL as: " + hostURL);
                Log.fatal("Detected overriden webservicePrefix as: " + webservicePrefix);
            } 
            clientResource = new ClientResource(hostURL);
            Client client = new Client(new Context(), Protocol.HTTP);
            client.getContext().getParameters().add("useForwardedForHeader", "false");
            clientResource.setNext(client);
            clientResource.setFollowingRedirects(false);
            clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, "jane.smith@abc.com", "test");
        }
        return clientResource.getChild(webservicePrefix + relativeURL);
    }
}
