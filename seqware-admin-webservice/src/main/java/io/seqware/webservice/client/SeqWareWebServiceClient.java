/*
 * Copyright (C) 2013 SeqWare
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
package io.seqware.webservice.client;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import io.seqware.webservice.generated.client.SeqWareWebserviceClient;
import io.seqware.webservice.generated.controller.ModelAccessionIDTuple;
import java.util.Set;

/**
 * Separate custom code from base client that would be destroyed during a regeneration
 * @author dyuen
 */
public class SeqWareWebServiceClient extends SeqWareWebserviceClient {

    public SeqWareWebServiceClient(String modelName, String baseUri) {
        super(modelName, baseUri);
    }

    public SeqWareWebServiceClient(String modelName) {
        super(modelName);
    }

    public Set<ModelAccessionIDTuple> find_JSON_rdelete(Class targetType, String id) throws UniformInterfaceException {
        WebResource resource = super.getWebResource();
        resource = resource.path(java.text.MessageFormat.format("{0}/rdelete/{1}", id, targetType.getSimpleName()));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(new GenericType<Set<ModelAccessionIDTuple>>() {
        });
    }

    public void remove_rdelete(Class targetType, String id, Set<ModelAccessionIDTuple> matchSet) throws UniformInterfaceException {
        super.getWebResource().path(java.text.MessageFormat.format("{0}/rdelete/{1}", id, targetType.getSimpleName())).type(javax.ws.rs.core.MediaType.APPLICATION_JSON).header("X-HTTP-Method-Override", "DELETE").post(matchSet);
    }
}
