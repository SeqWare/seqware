package io.seqware.webservice.client;

import io.seqware.webservice.model.Processing;
import io.seqware.webservice.model.ProcessingFiles;
import io.seqware.webservice.model.WorkflowRun;

import java.util.Collection;
import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import io.seqware.webservice.controller.ModelAccessionIDTuple;
import java.util.Set;

/**
 * Jersey REST client generated for REST resource:OrganismFacadeREST
 * [io.seqware.webservice.model.organism]<br>
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

   public static void main(String args[]) throws UniformInterfaceException {

      // some testing for workflow_runs
      SeqWareWebserviceClient processingClient = new SeqWareWebserviceClient("processing");
      SeqWareWebserviceClient client1 = new SeqWareWebserviceClient("workflowrun");
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

   /**
    * Constructs a SeqWare web service client for the given resource with the
    * default url {@code http://localhost:38080/seqware-admin-webservice/webresources}.
    * 
    * @param modelName
    *           Lowercase resource name. (e.g. study, registration, sample)
    */
   public SeqWareWebserviceClient(String modelName) {
      this(modelName, BASE_URI);
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
   public SeqWareWebserviceClient(String modelName, String baseUri) {
      ClientConfig config = new DefaultClientConfig();
      config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
      config.getClasses().add(ModelAccessionIDTuple.class);
      client = Client.create(config);
      webResource = client.resource(baseUri).path("io.seqware.webservice.model." + modelName);
   }

   public void remove(String id) throws UniformInterfaceException {
      webResource.path(java.text.MessageFormat.format("{0}", new Object[] { id })).delete();
   }

   public String countREST() throws UniformInterfaceException {
      WebResource resource = webResource;
      resource = resource.path("count");
      return resource.accept(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
   }

   public <T> T findAll_XML(Class<T> responseType) throws UniformInterfaceException {
      WebResource resource = webResource;
      return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
   }

   public <T> T findAll_JSON(Class<T> responseType) throws UniformInterfaceException {
      WebResource resource = webResource;
      return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
   }

   public void edit_XML(Object requestEntity) throws UniformInterfaceException {
      webResource.type(javax.ws.rs.core.MediaType.APPLICATION_XML).put(requestEntity);
   }

   public void edit_JSON(Object requestEntity) throws UniformInterfaceException {
      webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(requestEntity);
   }

   public void create_XML(Object requestEntity) throws UniformInterfaceException {
      webResource.type(javax.ws.rs.core.MediaType.APPLICATION_XML).post(requestEntity);
   }

   public void create_JSON(Object requestEntity) throws UniformInterfaceException {
      webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(requestEntity);
   }

   public <T> T findRange_XML(Class<T> responseType, String from, String to) throws UniformInterfaceException {
      WebResource resource = webResource;
      resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[] { from, to }));
      return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
   }

   public <T> T findRange_JSON(Class<T> responseType, String from, String to) throws UniformInterfaceException {
      WebResource resource = webResource;
      resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[] { from, to }));
      return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
   }

   public <T> T find_XML(Class<T> responseType, String id) throws UniformInterfaceException {
      WebResource resource = webResource;
      resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] { id }));
      return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
   }

   public <T> T find_JSON(Class<T> responseType, String id) throws UniformInterfaceException {
      WebResource resource = webResource;
      resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] { id }));
      return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
   }
   
   /**
   *    Code that follows was not automatically generated and will need to be merged in if this is re-generated
   */
   
   public Set<ModelAccessionIDTuple> find_JSON_rdelete(Class targetType, String id) throws UniformInterfaceException {
      WebResource resource = webResource;
      resource = resource.path(java.text.MessageFormat.format("{0}/rdelete/{1}",  id, targetType.getSimpleName()));
      return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(new GenericType<Set<ModelAccessionIDTuple>>(){});
   }
   
   public void remove_rdelete(Class targetType, String id, Set<ModelAccessionIDTuple> matchSet) throws UniformInterfaceException {
       webResource.path(java.text.MessageFormat.format("{0}/rdelete/{1}",id, targetType.getSimpleName())).type(javax.ws.rs.core.MediaType.APPLICATION_JSON).delete(matchSet);
   }

   public void close() {
      client.destroy();
   }

}
