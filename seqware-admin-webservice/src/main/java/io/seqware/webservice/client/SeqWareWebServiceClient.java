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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
//import io.seqware.webservice.controller.CustomWorkflowRunFacadeREST.UnsettledWorkflowRunException;
import io.seqware.webservice.controller.ModelAccessionIDTuple;
import java.net.URI;
import java.util.Set;

/**
 * Separate custom code from base client that would be destroyed during a regeneration
 * @author dyuen
 */
public class SeqWareWebServiceClient extends io.seqware.webservice.generated.client.SeqWareWebserviceClient {
    
    private String baseUri = null;

    public SeqWareWebServiceClient(String modelName) {
        super(BASE_URI);
    }

   /**
    * Constructs a SeqWare web service client for the given resource and url.
    * 
    * @param modelName
    *           Lowercase resource name. (e.g. study, registration, sample)
    * @param baseUri
    *           Url for the webservice in the form
    *           {@code http://localhost:38080/seqware-admin-webservice/webresources}.
    */
   public SeqWareWebServiceClient(String modelName, String baseUri) {
       this.baseUri = baseUri;
      ClientConfig config = new DefaultClientConfig();
      config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
      config.getClasses().add(ModelAccessionIDTuple.class);
      super.setClient(Client.create(config));
      super.setWebResource(getClient().resource(baseUri).path("io.seqware.webservice.model." + modelName));
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
    
    public ModelAccessionIDTuple findTupleByAccession(String id) throws UniformInterfaceException {
        WebResource resource = getClient().resource(baseUri).path("utility");
        resource = resource.path(java.text.MessageFormat.format("{0}", id));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(ModelAccessionIDTuple.class);
    }
}
