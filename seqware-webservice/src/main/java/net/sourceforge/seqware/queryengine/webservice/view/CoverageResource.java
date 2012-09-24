/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.view;


import java.io.File;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
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
 */
public class CoverageResource extends ServerResource {

  @Get
  public Representation represent() {  

    // logging
    this.getLogger().log(Level.SEVERE, "TESTING");
    
    // get the ROOT URL for various uses
    String rootURL = EnvUtil.getProperty("rooturl");

    // get the swid
    String swid = (String)getRequestAttributes().get("coverageId");

    // now build a model  
    Map root = new HashMap();
    
    // output string to return to client
    StringBuffer output = new StringBuffer();

    // template variables
    Configuration cfg = new Configuration();
    // Specify the data source where the template files come from.
    // Here I set a file directory for it:

    try {
    cfg.setDirectoryForTemplateLoading(new File("templates"));
    } catch (Exception e) { 
      //ignore 
    }
    // Specify how templates will see the data-model. This is an advanced topic...
    // but just use this:
    cfg.setObjectWrapper(new DefaultObjectWrapper());  

    // now build a model  
    Map rootData = new HashMap();
    ArrayList data = new ArrayList();

    // bunch-o-hardcoded
    ArrayList<String> contigs = new ArrayList<String>();

    // contains all the get params
    Form form = this.getRequest().getResourceRef().getQueryAsForm();

    // format
    String format = form.getFirstValue("format");
    
    // track specific options
    String trackName = form.getFirstValue("track.name");
    
    // track specific options
    String trackOptions = form.getFirstValue("track.options");

    // tags
    HashMap<String,String> tagsMap = new HashMap<String,String>();

    // can check param count
    getLogger().log(Level.SEVERE, "Number of form fields: "+form.getNames().size());

    // return help message
    if (form.getNames().size() == 0 || "help".equals(format)) {

      // output
      StringWriter sw = new StringWriter();
      
      // get template
      try {
      Template temp = cfg.getTemplate("coverage_help.ftl");
      rootData.put("intMax", Integer.MAX_VALUE);
      
      temp.process(rootData, sw);
      } catch (Exception e ) {
        // ignore
      }

      // output representation
      StringRepresentation repOutput = new StringRepresentation(sw.toString());
      repOutput.setMediaType(MediaType.TEXT_HTML);
      return(repOutput);
    }

    // add to contig list
    String requestContigs = form.getValues("filter.contig");
    if (requestContigs != null) {
      for (String requestContig : requestContigs.split(",")) {
        contigs.add(requestContig);
      }
    }
    else {
      contigs.add("all");
      getLogger().log(Level.SEVERE, "Contig is null!");
    }

    // Formatter
    DecimalFormat df = new DecimalFormat("##0.0");

    // return the HTML form
    if ("form".equals(format)) {

      // Specify the data source where the template files come from.
      // Here I set a file directory for it:
      try { 

        // now build a model
        rootData = new HashMap();
        //root.put("url", "/seqware/queryengine/realtime/coverage/basecoverages/"+id);
        rootData.put("url", "");

        // get template
        Template temp = cfg.getTemplate("coverage_query.ftl");

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

    }
    // return the WIG document
    else if ("wig".equals(format) || "wig_verbose".equals(format) || "ave_wig".equals(format) || "bedgraph".equals(format)) {

      Store store = null;

      try {

        MetadataDB metadataDB = new MetadataDB();
        root = metadataDB.getMetadata(Long.parseLong(swid));
          
        // FIXME: this is fragile, it assumes that the parameters are always stored as "cache_size" and "lock_counts"...
        //        need to pull from a metatable from the DB file itself if possible (maybe isn't!)
        if ("application/seqware-qe-hbase-db".equals(root.get("metatype"))) {
          SeqWareSettings settings = new SeqWareSettings();
          settings.setStoreType("hbase-mismatch-store");
          settings.setGenomeId((String)root.get("genomeId"));
          settings.setReferenceId((String)root.get("referenceId"));
          store = new HBaseStore();
          store.setSettings(settings);
          store.setup(settings);
        } else {
          store = BackendPool.getStore((String)root.get("filePath"), Long.parseLong((String)root.get("cache_size")), Integer.parseInt((String)root.get("lock_counts")));
        }
        
        if (store == null) { throw new Exception("Store is null"); }
        if (store != null) {

          Iterator<String> it = contigs.iterator();
          
          // print the track header
          if (trackName == null) { trackName = "SeqWare WIG "+(new Date()).toString(); }
          if (trackOptions == null) { trackOptions = ""; }
          output.append("track type=wiggle_0 name=\""+trackName+"\" "+trackOptions+"\n");

          while (it.hasNext()) {

            // chr
            String contig = (String) it.next();
            getLogger().log(Level.SEVERE, "Processing Contig: "+contig);

            // get iterator of coverage objects
            SeqWareIterator covIt = null;
            if ("all".equals(contig)) { 
              // FIXME: need to implement this!
              covIt = null;
            } else if (contig.matches("(\\S+):(\\d+)-(\\d+)")) {
              String[] t = contig.split("[-:]");
              contig = t[0];
              covIt = store.getCoverages(contig, Integer.parseInt(t[1]), Integer.parseInt(t[2]));
            } else {
              covIt = store.getCoverages(contig);
            }

            // iterate over contents
            while(covIt != null && covIt.hasNext()) {

              // get the coverage object
              Coverage cov = (Coverage) covIt.next();
              
              // bin size
              int binSize = -1;
              
              // now do something with cov object
              if (cov != null && cov.getCount() > 0 ) {
                
                if ("wig".equals(format) || "wig_verbose".equals(format)) {
                  HashMap covMap = cov.getCoverage();
                  int start = cov.getStartPosition();
                  int stop = cov.getStopPosition();
                  String covContig = cov.getContig();
                  Integer iInt = null;
                  
                  // print the WIG header
                  output.append("variableStep chrom="+contig+"\n");
                  
                  for (int i=start; i<=stop; i++) {
                    Integer count = (Integer) covMap.get(start);
                    int iplus = i+1;
                    iInt = new Integer(i);
                    if (cov.getCoverage().get(iInt) == null && "wig_verbose".equals(format)) {
                      //output.append(cov.getContig()+"\t"+i+"\t"+iplus+"\t0\n");
                      output.append(i+"\t0\n");
                    }
                    if (cov.getCoverage().get(iInt) != null) {
                      //System.out.println(cov.getContig()+"\t"+i+"\t"+iplus+"\t"+cov.getCoverage().get(iInt));
                      //output.append(cov.getContig()+"\t"+i+"\t"+iplus+"\t"+cov.getCoverage().get(iInt)+"\n");
                      output.append(i+"\t"+cov.getCoverage().get(iInt)+"\n");
                    }
                  }
                } else if ("ave_wig".equals(format)) {
                  // print the header
                  if (binSize == -1) {
                    binSize = (cov.getStopPosition() - cov.getStartPosition()) + 1;
                    output.append("variableStep chrom="+contig+" span="+binSize+"\n");
                  }
                  binSize = (cov.getStopPosition() - cov.getStartPosition()) + 1;
                  int sum = cov.getSum();
                  int ave = sum / binSize;
                  int stop = cov.getStopPosition()+1;
                  //output.append(cov.getContig()+"\t"+cov.getStartPosition()+"\t"+stop+"\t"+ave+"\n");
                  output.append(cov.getStartPosition()+"\t"+ave+"\n");
                }
              }
            }
            if (covIt != null) { covIt.close(); }
          }

          // finally close
          if (!"application/seqware-qe-hbase-db".equals(root.get("metatype"))) {
            BackendPool.releaseStore((String)root.get("filePath"));
          }
        }
      } catch (SeqWareException e) {
        e.printStackTrace();
        StringRepresentation repOutput = new StringRepresentation(e.getMessage());
        repOutput.setMediaType(MediaType.TEXT_PLAIN);
        return(repOutput);
      } catch (Exception e) {
        e.printStackTrace();
        StringRepresentation repOutput = new StringRepresentation(e.getMessage());
        repOutput.setMediaType(MediaType.TEXT_PLAIN);
        return(repOutput);
      }
      if (output.length() > 0 && ("wig".equals(format) || "wig_verbose".equals(format) || "ave_wig".equals(format))) {
        // output representation
        StringRepresentation repOutput = new StringRepresentation(output.toString());
        repOutput.setMediaType(MediaType.TEXT_PLAIN);
        return(repOutput);
      }
    }
    
    if ("html".equals(format) || "igv.xml".equals(format)) {
      output = new StringBuffer();
      String rootURLEncoded = rootURL;
      rootURLEncoded = rootURLEncoded.replaceAll(":", "%3A");
      rootURLEncoded = rootURLEncoded.replaceAll("/", "%2F");
      String displayPosition = contigs.get(0);
      // FIXME: there are assumptions here about how the URL should be constructed e.g. hg18 and chr22, these should be params
      if ("all".equals(displayPosition)) { displayPosition = "chr22"; }
      StringBuffer customURL = new StringBuffer();
      customURL.append(rootURLEncoded+"%2Fqueryengine%2Frealtime%2Fcoverage%2Fbasecoverage%2F"+swid+"%3Fformat%3Dwig");
      if (trackName != null) { customURL.append("%26track.name%3D"+trackName); }
      if (trackOptions != null) { customURL.append("%26track.options%3D"+trackOptions); }
      if (requestContigs != null) { customURL.append("%26filter.contig%3D"+requestContigs); }
      customURL.append("%26format%3Dwig"); //IGV only works with URL ending in proper file extension!
      
      if ("html".equals(format)) {
      output.append(
      "<html>" +
      "<h1>Query Engine</h1>" +
          "<h2>/<a href=\"/queryengine\">Query Engine</a>/<a href=\"/queryengine/realtime\">Realtime Analysis Tools</a>/<a href=\"/queryengine/realtime/coverage\">Coverage</a>/<a href=\"/queryengine/realtime/coverage/basecoverage\">Base Coverage</a></h2>" +
          "<p>The following links will load the results of your query in a given genome browser. Note that very large, long-running queries may fail to load since the client genome browser may time out. If this is the case break your query into smaller ranges or download a BED/WIG file an upload manually.</p>" +
      // UCSC
      // FIXME: notice the hard-coded genome below, should be parameterized via database!
          "<h3>UCSC Browser</h3>" +
      "<p><a href=\"http://genome.ucsc.edu/cgi-bin/hgTracks?org=hg18&position="+displayPosition+"&hgt.customText="+
        customURL.toString().replaceAll(" ", "_") +
        "\">Load standard WIG coverage results in UCSC browser</a></p>" +
        "<p><a href=\"http://genome.ucsc.edu/cgi-bin/hgTracks?org=hg18&position="+displayPosition+"&hgt.customText=" +
        customURL.toString().replaceAll("format%3Dwig", "format%3Dave_wig").replaceAll(" ", "_") +
        "\">Load WIG coverage results averaged by blocks in UCSC browser</a></p>"
      );
    
    // IGV
    String customIgvURL = customURL.toString().replaceAll("%26", "%26amp;");
    customIgvURL = customIgvURL.replaceAll("format%3Dwig", "format%3Digv.xml");
    customIgvURL = customIgvURL.replaceAll(" ", "_");
    
    output.append("<h3>Integrative Genomics Viewer</h3>" +
        "<a href=\"http://www.broadinstitute.org/igv/dynsession/igv.jnlp?sessionURL="+customIgvURL+"&locus="+displayPosition+"&user=SeqWareQueryEngine>Load standard WIG coverage results in IGV browser</a>"
        );
    output.append("<p>" +
        "<a href=\"http://www.broadinstitute.org/igv/dynsession/igv.jnlp?sessionURL="+customIgvURL+"&locus="+displayPosition+"&user=SeqWareQueryEngine>Load WIG coverage results averaged by blocks in IGV browser</a>" +
        		"</p>"
        );
    
    output.append("</html>");
      StringRepresentation repOutput = new StringRepresentation(output.toString());
      repOutput.setMediaType(MediaType.TEXT_HTML);
      return(repOutput);
    
      } else if ("igv.xml".equals(format)) {
        
        // need to decode the URL
        String customURLDecoded = customURL.toString();
        customURLDecoded = customURLDecoded.replaceAll("%2F", "/");
        //customURLDecoded = customURLDecoded.replaceAll("%26", "&");
        customURLDecoded = customURLDecoded.replaceAll("%26", "&amp;");
        customURLDecoded = customURLDecoded.replaceAll("%3F", "?");
        customURLDecoded = customURLDecoded.replaceAll("%3D", "=");
        customURLDecoded = customURLDecoded.replaceAll("%3A", ":");
        customURLDecoded = customURLDecoded.replaceAll(" ", "_");

        output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
              "<Global genome=\"hg18\" version=\"2\">" +
              "<Files>" +
                //"<DataFile name=\"http://99.71.130.238:8181/queryengine/static/1.bed\"/>" +
                "<DataFile name=\""+customURLDecoded+"\"/>" +
                //"<DataFile name=\"http://www.broadinstitute.org/igvdata/omega.12mer.tdf\"/>" +
              "</Files>" +
              "</Global>"
        );
          StringRepresentation repOutput = new StringRepresentation(output.toString());
            repOutput.setMediaType(MediaType.TEXT_XML);
            return(repOutput);
      }
    }
    
    //return "hello, world "+getRequestAttributes().get("mismatchId");  
    StringRepresentation repOutput = new StringRepresentation("# No Results!");
    repOutput.setMediaType(MediaType.TEXT_PLAIN);
    return(repOutput);
  }
}
