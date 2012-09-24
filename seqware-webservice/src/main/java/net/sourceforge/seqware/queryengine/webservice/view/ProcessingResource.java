/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.view;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.sourceforge.seqware.queryengine.webservice.util.EnvUtil;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;  
import org.restlet.resource.ServerResource;  





/**
 * @author boconnor
 *
 * This is still being worked on
 *
 */
public class ProcessingResource extends ServerResource {

  //Get("text/html")
  @Get
  public Representation represent() {  

    // Samples
    // arguments
    //String requestContig = (String)getRequestAttributes().get("filter.contig");
    //String requestContigs = form.getFirstValue("filter.contig");
    // logging
    this.getLogger().log(Level.SEVERE, "TESTING");

    // get the ROOT URL for various uses
    String rootURL = EnvUtil.getProperty("rooturl");

    // get the swid
    String swid = (String)getRequestAttributes().get("processingId");

    // now build a model  
    Map root = new HashMap();

    // output string to return to client
    StringBuffer output = new StringBuffer();

    // bunch-o-hardcoded
    ArrayList<String> contigs = new ArrayList<String>();

    // contains all the get params
    Form form = this.getRequest().getResourceRef().getQueryAsForm();

    // format
    String format = form.getFirstValue("format");

    // tags
    HashMap<String,String> tagsMap = new HashMap<String,String>();

    // can check param count
    getLogger().log(Level.SEVERE, "Number of form fields: "+form.getNames().size());
    if (form.getNames().size() == 0 || "help".equals(format)) {
      output.append("<html><body>" +
          " <h1>SeqWare API</h1>" +
          " <p>Help message goes here...</p>" +
      "</body></html>");
      // output representation
      StringRepresentation repOutput = new StringRepresentation(output.toString());
      repOutput.setMediaType(MediaType.TEXT_HTML);
      return(repOutput);
    }

    // contig
    //String contig = form.getValues("filter.contig");

    // Formatter
    DecimalFormat df = new DecimalFormat("##0.0");

    try {
      
      output.append("<h1>Hello World</h1>");

      // LEFT OFF WITH: not sure why this class can't be found...
      // need to finish hibernate use here
      //Session session = MetadataDB.getSessionFactory().getCurrentSession();
      
      

    } catch (Exception e) {
      e.printStackTrace();
      StringRepresentation repOutput = new StringRepresentation(e.getMessage());
      repOutput.setMediaType(MediaType.TEXT_PLAIN);
      return(repOutput);
    }



    if (output.length() > 0) {
      
      
      String html = "<html>" +
          "  <head> "+
          "  </head><body> "+
          output.toString() +
      		"</body></html>";
      
      // output representation
      StringRepresentation repOutput = new StringRepresentation(html);
      repOutput.setMediaType(MediaType.TEXT_HTML);
      return(repOutput);
    } 

    //return "hello, world "+getRequestAttributes().get("mismatchId");  
    StringRepresentation repOutput = new StringRepresentation("# No Results!");
    repOutput.setMediaType(MediaType.TEXT_PLAIN);
    return(repOutput);
  }
}
