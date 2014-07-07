package io.seqware.webservice.generated.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * Jersey REST client generated for REST resource:OrganismFacadeREST [io.seqware.webservice.model.organism]<br>
 * USAGE:
 * 
 * <pre>
 *        SeqWareWebserviceClient client = new SeqWareWebserviceClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 * 
 * @author boconnor
 */
public class SeqWareWebserviceClient {
    private WebResource webResource;
    private Client client;
    private static final String BASE_URI = "http://localhost:38080/seqware-admin-webservice/webresources";

    public SeqWareWebserviceClient() {
        /** blank constructor for subclasses */
    }

    /**
     * Constructs a SeqWare web service client for the given resource and url.
     * 
     * @param modelName
     *            Lowercase resource name. (e.g. study, registration, sample)
     */
    public SeqWareWebserviceClient(String modelName) {
        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
        webResource = client.resource(BASE_URI).path("io.seqware.webservice.model." + modelName);
    }

    public void remove(String id) throws UniformInterfaceException {
        getWebResource().path(java.text.MessageFormat.format("{0}", new Object[] { id })).delete();
    }

    public String countREST() throws UniformInterfaceException {
        WebResource resource = getWebResource();
        resource = resource.path("count");
        return resource.accept(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    public <T> T findAll_XML(Class<T> responseType) throws UniformInterfaceException {
        WebResource resource = getWebResource();
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findAll_JSON(Class<T> responseType) throws UniformInterfaceException {
        WebResource resource = getWebResource();
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void edit_XML(Object requestEntity) throws UniformInterfaceException {
        getWebResource().type(javax.ws.rs.core.MediaType.APPLICATION_XML).put(requestEntity);
    }

    public void edit_JSON(Object requestEntity) throws UniformInterfaceException {
        getWebResource().type(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(requestEntity);
    }

    public void create_XML(Object requestEntity) throws UniformInterfaceException {
        getWebResource().type(javax.ws.rs.core.MediaType.APPLICATION_XML).post(requestEntity);
    }

    public void create_JSON(Object requestEntity) throws UniformInterfaceException {
        getWebResource().type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(requestEntity);
    }

    public <T> T findRange_XML(Class<T> responseType, String from, String to) throws UniformInterfaceException {
        WebResource resource = getWebResource();
        resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[] { from, to }));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findRange_JSON(Class<T> responseType, String from, String to) throws UniformInterfaceException {
        WebResource resource = getWebResource();
        resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[] { from, to }));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T find_XML(Class<T> responseType, String id) throws UniformInterfaceException {
        WebResource resource = getWebResource();
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] { id }));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T find_JSON(Class<T> responseType, String id) throws UniformInterfaceException {
        WebResource resource = getWebResource();
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] { id }));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void close() {
        getClient().destroy();
    }

    /**
     * @return the webResource
     */
    public WebResource getWebResource() {
        return webResource;
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    /**
     * @param client
     *            the client to set
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * @param webResource
     *            the webResource to set
     */
    public void setWebResource(WebResource webResource) {
        this.webResource = webResource;
    }

}
