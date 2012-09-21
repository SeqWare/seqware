/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.view;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.store.impl.PostgreSQLStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.webservice.model.BackendPool;
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
 * This is a resource that will first attempt to open a cached tag file and, if it can't be found, does a live query of the DB, writes the file out, and returns the result.
 */
public class CachedTagResource extends ServerResource {

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
          " This documentation explains how to construct a URL query for tags." +
          "  <h4>Required</h4>" +
          "   <ul>" +
          "       <li>format=[json|help]: the output format</li>" +
          "         <ul>" +
          "           <li>json: tags in json format</li>" +
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

    else if ("form".equals(format)) {
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
        // FIXME: this template will need to be parameterized properly
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
        
          this.getLogger().log(Level.SEVERE, "SWID: "+swid+" dbserver: "+EnvUtil.getProperty("dbserver")+" db: "+EnvUtil.getProperty("db")+
              " user: "+EnvUtil.getProperty("user")+" pass: "+EnvUtil.getProperty("pass")+" rooturl: "+EnvUtil.getProperty("rooturl"));
        
          MetadataDB metadataDB = new MetadataDB();
          root = metadataDB.getTagMetadata(Long.parseLong(swid));

          this.getLogger().log(Level.SEVERE, "Metatype: "+root.get("metatype"));

          // FIXME: this is fragile, it assumes that the parameters are always stored as "cache_size" and "lock_counts"...
          //        need to pull from a metatable from the DB file itself if possible (maybe isn't!)
          if ("application/seqware-qe-postgresql-db".equals(root.get("metatype"))) { //application/seqware-qe-postgresql-db
            SeqWareSettings settings = new SeqWareSettings();
            settings.setStoreType("postgresql-mismatch-store");
            // FIXME: need to make these params at some point
            settings.setDatabase((String)root.get("db"));
            settings.setUsername((String)root.get("user"));
            settings.setPassword((String)root.get("pass"));
            settings.setServer((String)root.get("dbserver"));
            settings.setGenomeId((String)root.get("genomeId"));
            settings.setReferenceId((String)root.get("referenceId"));
            // hard coding for now
            settings.setPostgresqlPersistenceStrategy(SeqWareSettings.FIELDS);
            store = new PostgreSQLStore();
            store.setSettings(settings);
            store.setup(settings);
          } else if ("application/seqware-qe-hbase-db".equals(root.get("metatype"))) {
            SeqWareSettings settings = new SeqWareSettings();
            settings.setStoreType("hbase-mismatch-store");
            settings.setGenomeId((String)root.get("genomeId"));
            settings.setReferenceId((String)root.get("referenceId"));
            store = new HBaseStore();
            store.setSettings(settings);
            store.setup(settings);
          } else if ("application/seqware-qe-db".equals(root.get("metatype"))) {
            store = BackendPool.getStore((String)root.get("filePath"), Long.parseLong((String)root.get("cache_size")), Integer.parseInt((String)root.get("lock_counts")));
          }
        
          if (store == null) { throw new Exception("Store is null"); }
          
          if (store != null) {
            
            // before anything else, try to find a cache file
            File cacheFile = null;
            if ((String)root.get("filePath") != null && !"".equals((String)root.get("filePath")) && !"NA".equals((String)root.get("filePath")) && (new File((String)root.get("filePath")).exists() || new File((String)root.get("filePath")).mkdirs())) {
              cacheFile = new File((String)root.get("filePath")+File.separator+"tagCache.txt");
              this.getLogger().log(Level.SEVERE, "file temp path will be: "+root.get("filePath")+File.separator+"tagCache.txt");
            }
            if ((cacheFile == null || !cacheFile.canWrite()) && root.get("referenceId") != null && root.get("genomeId") != null) {
              String tempDir = EnvUtil.getProperty("java.io.tmpdir");
              cacheFile = new File(tempDir+File.separator+(String)root.get("referenceId")+"-"+(String)root.get("genomeId")+"-TagCache.txt");
              this.getLogger().log(Level.SEVERE, "file temp path to system temp will be: "+tempDir+File.separator+(String)root.get("referenceId")+"-"+(String)root.get("genomeId")+"-TagCache.txt");

            }
            
            // now figure out if we're reading or writing
            // if this file exists and can be read then use it
            if (cacheFile != null && cacheFile.canRead() && cacheFile.exists() && cacheFile.length() > 0) {

              this.getLogger().log(Level.SEVERE, "CACHE: The cache file already exisits and is non-empty."); 
              
              // open the file
              BufferedReader reader = new BufferedReader(new FileReader(cacheFile));

              output.append("[ ");
              // iterate over contents
              String line = null;
              boolean first = true;
              while((line = reader.readLine()) != null) {
                String[] tagArr = line.split("\t");
                String lowerLine = tagArr[0].toLowerCase();
                if (searchStr == null || lowerLine.indexOf(searchStr.toLowerCase()) > -1) {
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
              
              reader.close();

            } 
            // otherwise create the cache file if we can
            // && (!cacheFile.exists() || cacheFile.length() == 0)
            else if (cacheFile != null && (cacheFile.createNewFile() || cacheFile.length() == 0) && cacheFile.canWrite()) {
              
              this.getLogger().log(Level.SEVERE, "CACHE: The cache file does not exist so we need to get all tags and make it.");
              
              BufferedWriter writer = new BufferedWriter(new FileWriter(cacheFile));
              
              output.append("[ ");
              
              // get everything
              SeqWareIterator swi = store.getMismatchesTags();
              
              boolean first = true;  
              while(swi.hasNext()) {
                ArrayList<String> tagArr = (ArrayList<String>)swi.next();
                String lowerLine = tagArr.get(0).toLowerCase();
                writer.write(tagArr.get(0)+"\t"+tagArr.get(1));
                writer.newLine();
                if (searchStr == null || lowerLine.indexOf(searchStr.toLowerCase()) > -1) {
                  if (!first) { output.append(", "); }
                  first = false;
                  output.append("{ \"id\": \""+tagArr.get(0)+"\", \"label\": \""+tagArr.get(0)+" ("+tagArr.get(1)+" tagged items)\", \"value\": \""+tagArr.get(0)+"\" }");
                }
              }
              
              // return as JSON
              output.append(" ]");
              
              writer.close();
              
            } else {
              
              this.getLogger().log(Level.SEVERE, "CACHE: Using traditional tag query approach.");
            
              // otherwise just do what we've done before
              output.append("[ ");
              SeqWareIterator swi = null;
              if (searchStr != null && !"".equals(searchStr)) { 
                swi = store.getMismatchTagsBySearch(searchStr);
              } else {
                swi = store.getMismatchesTags();
              }
              boolean first = true;  
              while(swi.hasNext()) {
                ArrayList<String> tagArr = (ArrayList<String>)swi.next();
                if (!first) { output.append(", "); }
                first = false;
                output.append("{ \"id\": \""+tagArr.get(0)+"\", \"label\": \""+tagArr.get(0)+" ("+tagArr.get(1)+" tagged items)\", \"value\": \""+tagArr.get(0)+"\" }");
              }
              // return as JSON
              output.append(" ]");
                          
            }
            
            // finally close
            if (!"application/seqware-qe-hbase-db".equals(root.get("metatype")) && !"application/seqware-qe-postgresql-db".equals(root.get("metatype"))) {
              BackendPool.releaseStore((String)root.get("filePath"));
            }
            else { // everything else, just release
              store.close();
            }
            
          }
        
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
