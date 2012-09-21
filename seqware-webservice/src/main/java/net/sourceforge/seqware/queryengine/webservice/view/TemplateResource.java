/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.view;


import java.io.StringWriter;


import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;  


/**
 * @author boconnor
 *
 */
public class TemplateResource extends GenericTemplateResource {

  @Get
  public Representation represent() { 
    
    try {
      
      r = this.getRequest();
      
      r.getResourceRef();
      r.getResourceRef().getLastSegment();
      
      setup(r.getResourceRef().getLastSegment());
      
      System.out.println("SEGMENT: "+r.getResourceRef().getLastSegment()+".ftl");
      
      this.setTemplateFile(r.getResourceRef().getLastSegment()+".ftl");
      
      sw = new StringWriter();
      
      temp.process(root, sw);
      
      // return
      StringRepresentation repOutput = new StringRepresentation(sw.toString());
      repOutput.setMediaType(MediaType.TEXT_XML);
      return(repOutput);
      
    } catch (Exception e) {
      e.printStackTrace();
      StringRepresentation repOutput = new StringRepresentation(e.getMessage());
      repOutput.setMediaType(MediaType.TEXT_PLAIN);
      return(repOutput);
    }
  }
}
