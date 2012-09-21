/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.view;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.webservice.model.MetadataDB;
import net.sourceforge.seqware.queryengine.webservice.util.EnvUtil;

import org.restlet.data.Form;
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
public class TagResource extends ServerResource {

  //Get("text/html")
  @Get
  public Representation represent() {  

    Form responseHeaders = (Form) getResponse().getAttributes().get("org.restlet.http.headers");  
    if (responseHeaders == null)  
    {  
      responseHeaders = new Form();  
      getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);  
    }  
    responseHeaders.add("Access-Control-Allow-Origin", "*"); 
    // Samples
    // arguments
    //String requestContig = (String)getRequestAttributes().get("filter.contig");
    //String requestContigs = form.getFirstValue("filter.contig");
    // logging
    this.getLogger().log(Level.SEVERE, "TESTING");
    
    // get the ROOT URL for various uses
    String rootURL = EnvUtil.getProperty("rooturl");
    
    // get the swid
    String swid = (String)getRequestAttributes().get("tagsId");

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
    
    // track specific options
    String searchStr = form.getFirstValue("term");
    
    // tags
    HashMap<String,String> tagsMap = new HashMap<String,String>();

    // can check param count
    getLogger().log(Level.SEVERE, "Number of form fields: "+form.getNames().size());
    if (form.getNames().size() == 0 || "help".equals(format)) {
      output.append("<html><body>" +
    	  " <h1>Query Engine</h1>" +
          " <h2>/<a href=\"/queryengine\">Query Engine</a>/<a href=\"/queryengine/realtime\">Realtime Analysis Tools</a>/<a href=\"/queryengine/realtime/variants\">Variants</a>/<a href=\"/queryengine/realtime/variants/mismatches\">Mismatches</a></h2>" +
          " <h3>Documentation</h3>" +
          " This documentation explains how to construct a URL query for mismatches." +
          "  <h4>Required</h4>" +
          "   <ul>" +
          "       <li>format=[bed|tags|form|html|igv.xml|help]: the output format</li>" +
          "         <ul>" +
          "           <li>bed:  returns variant data in BED format suitable for loading in the <a href=\"http://genome.ucsc.edu\">UCSC genome browser</a></li>" +
          "           <li>tags: returns a non-redundant list of tags associated with these variants suitable for loading in the <a href=\"http://genome.ucsc.edu\">UCSC table browser</a></li>" +
          "           <li>form: returns an HTML form for constructing a query on this resource</li>" +
          "           <li>html: returns an HTML document that links to the UCSC and other browsers</li>" +
          "           <li>igv.xml: returns an XML session document used by the IGV genome browser that points to these results</li>" +
          "           <li>help: prints this help documentation</li>" +
          "         </ul>" +
          "   </ul>" +
          "  <h4>Optional</h4>" +
          "   <ul>" +
          "       <li>filter.contig=[contig_name|all]: one or more contigs must be specified, multiple contigs are given as separate params</li>" +
          "       <li>filter.tag[.and[.not]|.or]=tagstring: tag to filter by, multiple tags are given as separate params, append .and, .and.not, or .or for logical combo, defaults to and</li>" +
          "       <li>filter.size=int-int: size range for indels, inclusive</li>" +
          "       <li>filter.minCoverage=int: minimum coverage at a given position, default is 0</li>" +
          "       <li>filter.maxCoverage=int: maximum coverage at a given position, default is "+Integer.MAX_VALUE+"</li>" +
          "       <li>filter.minPhred=int: minimum phred score for the mismatch call, default is 0</li>" +
          "       <li>filter.minObservations=int: minimum number of times a mismatch must be seen at a given position, default is 0</li>" +
          "       <li>filter.minObservationsPerStrand=int: minimum number of times a mismatch must be seen at a given position on each strand, default is 0</li>" +
          "       <li>filter.minPercent=int: minimum percentage of time a mismatch must be seen at a given position, default is 0</li>" +
          "       <li>filter.includeSNVs=[true|false]: indicates whether to include single nucleotide variants, default is true</li>" +
          "       <li>filter.includeIndels=[true|false]: indicates whether to include small insertions/deletions, default is true</li>" +
          "       <li>track.name=string: the name for the track</li>" +
          "       <li>track.options=string: put options (key/values) for tracks in UCSC browser here, name can be placed here as name=<name> or in track.name field</li>" +
          "   </ul>" +
          "  <h4>Example</h4>" +
          "  <pre>http://{hostname}:{port}/queryengine/realtime/variants/mismatches/{id}?format=bed&filter.contig=chr22&filter.contig=chr10&filter.minObservations=4&filter.tag=early-termination</pre>" +
          "  This example queries a given mismatch resource, specified by id, and returns the indels and snvs on chr22 and chr10 in bed format with a min observations of 4 and resulting in a stop codon." +
      "</body></html>");
      // output representation
      StringRepresentation repOutput = new StringRepresentation(output.toString());
      repOutput.setMediaType(MediaType.TEXT_HTML);
      return(repOutput);
    }

    if ("form".equals(format)) {
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
        Map rootData = new HashMap();
        //root.put("url", "/seqware/queryengine/realtime/variants/mismatches/"+id);
        rootData.put("url", "");
        
        // get template
        Template temp = cfg.getTemplate("tags_query.ftl");
        
        // write the output of template
        StringWriter sw = new StringWriter();
        temp.process(rootData, sw);
        
        // return
        StringRepresentation repOutput = new StringRepresentation(sw.toString());
        repOutput.setMediaType(MediaType.TEXT_HTML);
        return(repOutput);
        
      } catch (Exception e) {
        e.printStackTrace();
        StringRepresentation repOutput = new StringRepresentation(e.getMessage());
        repOutput.setMediaType(MediaType.TEXT_PLAIN);
        return(repOutput);
      }

    } else {

      // guessing you want json back
      
      // db store used to connect
      Store store = null;

      try {
          MetadataDB metadataDB = new MetadataDB();
          root = metadataDB.getTagMetadata(Long.parseLong(swid));
          
          String filePath = (String)root.get("filePath");
          searchStr = searchStr.toLowerCase();
          
          // open the file
          BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
          
          output.append("[ ");
          // iterate over contents
          String line = null;
          boolean first = true;
          while((line = reader.readLine()) != null) {
            String[] tagArr = line.split("\t");
            String lowerLine = tagArr[0].toLowerCase();
            if (lowerLine.indexOf(searchStr) > -1) {
              // add those that match to output
              if (!first) {
                output.append(", ");
              }
              else if (first) {
                first = false;
              }
              output.append("{ \"id\": \""+tagArr[0]+"\", \"label\": \""+tagArr[0]+" ("+tagArr[1]+" tagged items)\", \"value\": \""+tagArr[0]+"\" }");
            }
          }
          
          // return as JSON
          output.append(" ]");
          
        
      } catch (Exception e) {
        e.printStackTrace();
        StringRepresentation repOutput = new StringRepresentation(e.getMessage());
        repOutput.setMediaType(MediaType.TEXT_PLAIN);
        return(repOutput);
      }
      if (output.length() > 0) {
        // output representation
        StringRepresentation repOutput = new StringRepresentation(output.toString());
        repOutput.setMediaType(MediaType.APPLICATION_JSON);
        return(repOutput);
      } 
    }
    
    
    //return "hello, world "+getRequestAttributes().get("mismatchId");  
    StringRepresentation repOutput = new StringRepresentation("[ ]");
    repOutput.setMediaType(MediaType.APPLICATION_JSON);
    return(repOutput);
  }
}
