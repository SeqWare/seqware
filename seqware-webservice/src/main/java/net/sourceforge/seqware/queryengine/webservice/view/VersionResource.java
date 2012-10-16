/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.view;


import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;


import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;  
import org.restlet.resource.ServerResource;  



/**
 * <p>VersionResource class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class VersionResource extends ServerResource {

  //Get("text/html")
  /**
   * <p>represent.</p>
   *
   * @return a {@link org.restlet.representation.Representation} object.
   */
  @Get
  public Representation represent() {  

    // logging
    this.getLogger().log(Level.INFO, "Version Called");
    

    // now build a model  
    Map root = new HashMap();
    
    // output string to return to client
    StringBuffer output = new StringBuffer();
    
    output.append("<?xml version=\"1.0\"?>"+
    "<version major=\"0\" minor=\"11\" patch=\"0\"/>");

    StringRepresentation repOutput = new StringRepresentation(output.toString());
    repOutput.setMediaType(MediaType.TEXT_XML);
    return(repOutput);
    
  }
  
  /* @Post
  public Representation accept(Representation entity) throws Exception {
    Representation rep = null;
    if (entity != null) {
      if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),true)) {
     // 1/ Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(1000240);

        // 2/ Create a new file upload handler based on the Restlet
        // FileUpload extension that will parse Restlet requests and
        // generates FileItems.
        RestletFileUpload upload = new RestletFileUpload(factory);
        //ServletFileUpload upload = new ServletFileUpload(factory);

        List<FileItem> items;
        
        
        // 3/ Request is parsed by the handler which generates a
        // list of FileItems
        items = upload.parseRequest(getRequest());
        
        System.err.println("The file iterator: "+items.size());

      }
    }
    return(new StringRepresentation("OK", MediaType.TEXT_PLAIN));
  }*/
}
