/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.view;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.sourceforge.seqware.queryengine.webservice.model.MetadataDB;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;  
import org.restlet.resource.ServerResource;  



/**
 * <p>WorkflowRunStatusResource class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowRunStatusResource extends ServerResource {

  //Get("text/html")
  /**
   * <p>represent.</p>
   *
   * @return a {@link org.restlet.representation.Representation} object.
   */
  @Get
  public Representation represent() {  

    // logging
    this.getLogger().log(Level.INFO, "WorkflowResource Called");
    
    // get the ROOT URL for various uses
    //String rootURL = EnvUtil.getProperty("rooturl");
    Request req = getRequest();
    String host = req.getResourceRef().getHostIdentifier();
    String path = req.getResourceRef().getPath();
    String[] pathArr = path.split("/");
    String rootURL = host+"/"+pathArr[1];
    
    // get the swid
    String swid = (String)getRequestAttributes().get("workflowRunAccession");

    // now build a model  
    Map root = new HashMap();
    
    // output string to return to client
    StringBuffer output = new StringBuffer();

    // contains all the get params
    Form form = this.getRequest().getResourceRef().getQueryAsForm();
    
    MetadataDB metadataDB = new MetadataDB();
    HashMap<String,Object> map = metadataDB.getWorkflowRunInfo(Integer.parseInt(swid));
    
    output.append("<?xml version=\"1.0\"?>"+
    "<queryengine>"+
    "  <asynchronous>"+
    "          <workflow_run>");
    output.append("              <status uri=\""+rootURL+"/queryengine/asynchronous/workflow_run/status/"+swid+"\""+
    "              swid=\""+swid+"\" state=\""+map.get("status")+"\"/>");
    ArrayList procs = (ArrayList)map.get("procs");
    for(Object proc : procs) {
      String algo = (String)((HashMap)proc).get("algo");
      Integer procAccession = (Integer)((HashMap)proc).get("accession");
      String procStatus = (String)((HashMap)proc).get("status");
      output.append(" <processing state=\""+procStatus+"\" swid=\""+procAccession+"\">");
      HashMap<Integer, String> files = (HashMap<Integer, String>)((HashMap)proc).get("files");
      for (Integer file : files.keySet()) {
        String filePath = files.get(file);
        output.append("  <file uri=\""+rootURL+"/queryengine/asynchronous/workflow_run/file/"+file+"\"" +
        		" file_path=\""+filePath+"\" />");
      }
      output.append("</processing>");
    }
    
    output.append(
    "          </workflow_run>"+
    "  </asynchronous>"+
    "</queryengine>");

    StringRepresentation repOutput = new StringRepresentation(output.toString());
    repOutput.setMediaType(MediaType.TEXT_XML);
    return(repOutput);
    
  }
}
