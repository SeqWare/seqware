/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.view;


import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.seqware.queryengine.webservice.model.MetadataDB;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;  
import org.restlet.resource.ServerResource;  

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * @author boconnor
 *
 */
public class CoveragesResource extends ServerResource {

  @Get
  public Representation represent() { 
    
    // at this point the connection is setup
    int index = 0;

    Configuration cfg = new Configuration();
    // Specify the data source where the template files come from.
    // Here I set a file directory for it:
    try {
      cfg.setDirectoryForTemplateLoading(
          new File("templates"));
      // Specify how templates will see the data-model. This is an advanced topic...
      // but just use this:
      cfg.setObjectWrapper(new DefaultObjectWrapper());  

      // now build a model  
      Map root = new HashMap();
      MetadataDB metadataDB = new MetadataDB();
      ArrayList data = metadataDB.getMetadata();
      
      // get template
      //Template temp = cfg.getTemplate("coverage_list.ftl");
      Template temp = cfg.getTemplate("basecoverage.ftl");

      // write the output of template
      StringWriter sw = new StringWriter();
      root.put("data", data);
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
