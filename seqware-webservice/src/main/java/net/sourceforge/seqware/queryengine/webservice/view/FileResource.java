/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.view;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.sourceforge.seqware.queryengine.webservice.model.MetadataDB;
import net.sourceforge.seqware.queryengine.webservice.util.EnvUtil;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;  
import org.restlet.resource.ServerResource;  



/**
 * <p>FileResource class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class FileResource extends ServerResource {

  //Get("text/html")
  /**
   * <p>represent.</p>
   *
   * @return a {@link org.restlet.representation.Representation} object.
   */
  @Get
  public Representation represent() {  

    FileRepresentation rep = null;
    
    // logging
    this.getLogger().log(Level.INFO, "FileResource Called");
    
    // get the ROOT URL for various uses
    String rootURL = EnvUtil.getProperty("rooturl");
    
    // get the swid
    String swid = (String)getRequestAttributes().get("fileAccession");

    // now build a model  
    Map root = new HashMap();
    
    // output string to return to client
    StringBuffer output = new StringBuffer();

    // contains all the get params
    Form form = this.getRequest().getResourceRef().getQueryAsForm();
    
    MetadataDB metadataDB = new MetadataDB();
    
    String filePath = metadataDB.getFilePath(Integer.parseInt(swid));
    

      // Create a new representation based on disk file.
      // The content is arbitrarily sent as plain text.
      rep = new FileRepresentation(new File(filePath),
              MediaType.TEXT_PLAIN, 0);
   

    //StringRepresentation repOutput = new StringRepresentation(output.toString());
    //repOutput.setMediaType(MediaType.TEXT_XML);
    return(rep);
    
  }
}
