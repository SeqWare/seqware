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

import io.seqware.webservice.generated.client.SeqWareWebserviceClient;
import io.seqware.webservice.controller.ModelAccessionIDTuple;
import io.seqware.webservice.generated.model.Processing;
import io.seqware.webservice.generated.model.ProcessingFiles;
import io.seqware.webservice.generated.model.Sample;
import io.seqware.webservice.generated.model.WorkflowRun;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * Separate custom code from base client that would be destroyed during a regeneration
 * 
 * @author dyuen
 */
public class SeqWareWebServiceClient extends io.seqware.webservice.generated.client.SeqWareWebserviceClient {

    private String baseUri = null;

    /**
     * Constructor that uses default baseURI
     * 
     * @param modelName
     */
    public SeqWareWebServiceClient(String modelName) {
        super(modelName);
    }

    /**
     * Constructs a SeqWare web service client for the given resource and url.
     * 
     * @param modelName
     *            Lowercase resource name. (e.g. study, registration, sample)
     * @param baseUri
     *            Url for the webservice in the form {@code http://localhost:38080/seqware-admin-webservice/webresources}.
     */
    public SeqWareWebServiceClient(String modelName, String baseUri) {
        this.baseUri = baseUri;
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        config.getClasses().add(ModelAccessionIDTuple.class);
        setClient(Client.create(config));
        setWebResource(getClient().resource(baseUri).path("io.seqware.webservice.model." + modelName));
    }

    public Set<ModelAccessionIDTuple> find_JSON_rdelete(Class targetType, String id) throws UniformInterfaceException {
        WebResource resource = super.getWebResource();
        resource = resource.path(java.text.MessageFormat.format("{0}/rdelete/{1}", id, targetType.getSimpleName()));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(new GenericType<Set<ModelAccessionIDTuple>>() {
        });
    }

    public void remove_rdelete(Class targetType, String id, Set<ModelAccessionIDTuple> matchSet) throws UniformInterfaceException {
        super.getWebResource().path(java.text.MessageFormat.format("{0}/rdelete/{1}", id, targetType.getSimpleName()))
                .type(javax.ws.rs.core.MediaType.APPLICATION_JSON).header("X-HTTP-Method-Override", "DELETE").post(matchSet);
    }

    public void createSampleNullParentHierarchyRelationship(String id) throws UniformInterfaceException {
        super.getWebResource().path(java.text.MessageFormat.format("{0}/createNullHierarchy", id))
                .type(javax.ws.rs.core.MediaType.APPLICATION_JSON).put();
    }

    public ModelAccessionIDTuple findTupleByAccession(String id) throws UniformInterfaceException {
        WebResource resource = getClient().resource(baseUri);
        resource = resource.path(java.text.MessageFormat.format("utility/translateSWID/{0}", id));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(ModelAccessionIDTuple.class);
    }
    
    /**
     * This method will call the createAndReturn web method.
     * @param responseType The type of object to be returned.
     * @param entity The entity to persist.
     * @return An updated form of <em>entity</em>, after it has been persisted. 
     * All database-generated fields (such as IDs) should be populated on this returned
     * object.
     */
    public <T> T createAndReturn(Class<T> responseType, T entity) {
        return this.getWebResource()
                .path("createAndReturn")
                .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .post(responseType,entity);
    }
    
    /**
     * Gets samples, by name.
     * @param name the name of the sample to search for.
     * @return A list of samples whose name matches the input.
     */
    public Collection<Sample> getSamplesByName(String name)
    {
    	WebResource resource = getClient().resource(baseUri);
    	ClientResponse response = resource.path(java.text.MessageFormat.format("io.seqware.webservice.model.sample/withName/{0}", name)).accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(ClientResponse.class);
    	Sample[] samples = response.getEntity(Sample[].class);
    	return Arrays.asList(samples);
    	
    }

    /**
     * Sets the "skip" field on an entity to TRUE
     * @param entityId The Id of the entity to skip.
     */
    public void skip(String entityId)
    {
        this.getWebResource()
                .path(java.text.MessageFormat.format("skip/{0}", entityId))
                .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .post();
    }

    /**
     * Sets the "skip" field on an entity to FALSE.
     * @param entityId The Id of the entity to un-skip.
     */
    public void unskip(String entityId)
    {
        this.getWebResource()
                .path(java.text.MessageFormat.format("unskip/{0}", entityId))
                .accept(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .post();
    }
    
    /**
     * A generic method that will find some entity, where some field matches a given value. <br/>
     * @param returnType The type of entity that will be returned.
     * @param field The field to search on.
     * @param value The value to match <em>field</em> to.
     * @return A List of objects of <em>returnType</em>.<br/>
     * Usage:<br/>
     * <code>List&lt;Sample&gt; samples2 = sampleClient.&lt;Sample&gt;getEntitiesWhereFieldMatchesValue(Sample.class,"name", "Fifty");</code>
     */
    public <T extends Serializable> List<T> getEntitiesWhereFieldMatchesValue(final Class<T> returnType, String field, String value)
    {
        ParameterizedType parameterizedGenericType = new ParameterizedType() {
            public Type[] getActualTypeArguments() {
                return new Type[] { returnType };
            }

            public Type getRawType() {
                return List.class;
            }

            public Type getOwnerType() {
                return List.class;
            }
        };
        
        List<T> results = this.getWebResource()
                           .path(java.text.MessageFormat.format("where/{0}/matches/{1}", field, value))
                           .get(new GenericType<List<T>>(parameterizedGenericType) {});
        
        return results;
    }
    
    /**
     * Example for using the client
     * 
     * @param args
     * @throws UniformInterfaceException
     */
    public static void main(String args[]) throws UniformInterfaceException {

        // some testing for workflow_runs
        SeqWareWebserviceClient processingClient = new SeqWareWebserviceClient("processing");
        SeqWareWebserviceClient client1 = new SeqWareWebserviceClient("workflowrun");

        // some testing to investigate seqware admin webservice
        SeqWareWebserviceClient sampleClient = new SeqWareWebserviceClient("sample");
        ClientResponse response0 = sampleClient.find_XML(ClientResponse.class, "127665");
        GenericType<Sample> genericType0 = new GenericType<Sample>() {
        };
        Sample sample = response0.getEntity(genericType0);
        // test out sample null creation
        io.seqware.webservice.client.SeqWareWebServiceClient sampleClient2 = new SeqWareWebServiceClient("sample");
        sampleClient2.createSampleNullParentHierarchyRelationship("127665");

        ClientResponse response = client1.findRange_XML(ClientResponse.class, "1", "5");
        GenericType<List<WorkflowRun>> genericType = new GenericType<List<WorkflowRun>>() {
        };
        List<WorkflowRun> data = response.getEntity(genericType);
        for (WorkflowRun obj : data) {
            System.out.println("WORKFLOWRUN: " + obj.getWorkflowRunId() + " WORKFLOW NAME: " + obj.getWorkflowId().getName()
                    + " WORKFLOW VERSION: " + obj.getWorkflowId().getVersion());
            Collection<Processing> procs = obj.getProcessingCollection1();
            if (procs != null) {
                for (Processing currProc : procs) {
                    System.out.println("  PROC: " + currProc.getStatus() + " ACCESSION: " + currProc.getSwAccession() + " FILES: ");
                    Collection<ProcessingFiles> procFiles = currProc.getProcessingFilesCollection();
                    if (procFiles != null) {
                        for (ProcessingFiles procFile : procFiles) {
                            System.out.println("  PROC FILE: " + procFile.getFileId() + " PATH: " + procFile.getFileId().getFilePath());
                        }
                    } else {
                        System.out.println("  Can't get proc files for processing ID: " + currProc.getProcessingId());
                        ClientResponse procRes = processingClient.find_XML(ClientResponse.class, currProc.getProcessingId().toString());
                        Processing procData = procRes.getEntity(new GenericType<Processing>() {
                        });
                        if (procData != null) {
                            System.out.println("   PROC FILE2: " + procData.getProcessingFilesCollection());
                        }
                    }
                }
            } else {
                System.out.println(" NULL: " + obj.getProcessingCollection1() + " " + obj.getProcessingCollection());
            }
        }

    }
}
