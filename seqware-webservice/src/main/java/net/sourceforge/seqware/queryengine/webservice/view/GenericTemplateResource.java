/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.view;


import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sourceforge.seqware.queryengine.webservice.model.MetadataDB;
import net.sourceforge.seqware.queryengine.webservice.util.EnvUtil;

import org.restlet.Request;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.restlet.ext.wadl.WadlServerResource;

/**
 * @author boconnor
 * FIXME: all the db connection code below can be replaced with the db object
 */
public class GenericTemplateResource extends WadlServerResource {
  
  protected String templateFile = null;
  protected String urlString = null;
  protected Request r = null;
  protected Map root = null;
  protected ArrayList data = null;
  protected Template temp = null;
  protected StringWriter sw = null;
  protected Configuration cfg = null;
  
  public String getTemplateFile() {
    return templateFile;
  }

  public void setTemplateFile(String templateFile) throws Exception {
    this.templateFile = templateFile;
    cfg.getTemplate(templateFile);
  }

  public String getUrlString() {
    return urlString;
  }

  public void setUrlString(String urlString) {
    this.urlString = urlString;
  }
  
  public ArrayList getData() {
    return data;
  }

  public void setData(ArrayList data) {
    this.data = data;
  }
  
  /**
   * This allows you to search for different types of data in the metadb not just query engine databases
   * @param type
   * @throws Exception
   */
  public void setup(String type) throws Exception {
    MetadataDB metadata = new MetadataDB();
    if ("tags".equals(type)) {
      setup(metadata.getTagMetadata());
    } else if ("workflow_composition".equals(type)) {
      setup(metadata.getWorkflowMetadata());
    } else if ("asynchronous".equals(type) || "workflows".equals(type)) {
      // then this needs to display the metadb hash for workflows
      setup(metadata.getWorkflowMetadata());
    } else {
      setup();
    }
  }

  public void setup() throws Exception {
    MetadataDB metadata = new MetadataDB();
    setup(metadata.getMetadata());
  }
  
  public void setup(ArrayList data) throws Exception { 
    
    r = this.getRequest();
    String template = r.getResourceRef().getLastSegment();
    String rootURL = EnvUtil.getProperty("rooturl");
    
    System.out.println("Hostref: "+r.getResourceRef().getLastSegment());
    System.out.println("full URL: "+r.getResourceRef().getPath());    
    System.out.println("dbserver: "+EnvUtil.getProperty("dbserver"));
    System.out.println("user: "+EnvUtil.getProperty("user"));
    System.out.println("pass: "+EnvUtil.getProperty("pass"));
    System.out.println("root url: "+rootURL);
    System.out.println("template path: "+template+".ftl");
    System.out.println("template dir: "+new File("webapps/SeqWareQEWS/WEB-INF/templates").getAbsolutePath());
    
    //URL url = GenericTemplateResource.class.getResource("templates/"+template+".ftl");
    URL url = GenericTemplateResource.class.getClassLoader().getResource(".");
    
    ServletContext context = (ServletContext)
    getContext().getServerDispatcher().getContext()
    .getAttributes().get("org.restlet.ext.servlet.ServletContext"); 
    
    //ServletContext context = getServletContext();
    //InputStream is = context.getResourceAsStream("/templates/"+template+".ftl");
    url = context.getResource("/WEB-INF/templates/"+template+".ftl");
    System.out.println("template dir 2: "+context.getRealPath("/WEB-INF/templates/"+template+".ftl"));
    
    
    this.data = data;
    
    // at this point the connection is setup
    int index = 0;

    cfg = new Configuration();
    // Specify the data source where the template files come from.
    // Here I set a file directory for it:
    cfg.setDirectoryForTemplateLoading(
        new File(context.getRealPath("/WEB-INF/templates")));
        //new File("templates"));
    // Specify how templates will see the data-model. This is an advanced topic...
    // but just use this:
    cfg.setObjectWrapper(new DefaultObjectWrapper());  

    // now build a model  
    root = new HashMap();
    // data = new ArrayList();
    
    // get template
    temp = cfg.getTemplate(template+".ftl");
    
    // store the data
    root.put("data", data);
    root.put("rooturl", rootURL);
    
  }
}
