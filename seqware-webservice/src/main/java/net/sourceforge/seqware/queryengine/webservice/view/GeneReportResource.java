/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.view;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.store.impl.PostgreSQLStore;
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



/**
 * <p>GeneReportResource class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class GeneReportResource extends ServerResource {

  //Get("text/html")
  /**
   * <p>represent.</p>
   *
   * @return a {@link org.restlet.representation.Representation} object.
   */
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
    String swid = (String)getRequestAttributes().get("mismatchId");

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
    String trackName = form.getFirstValue("track.name");

    // track specific options
    String trackOptions = form.getFirstValue("track.options");

    // tags
    HashMap<String,String> tagsMap = new HashMap<String,String>();

    // can check param count
    getLogger().log(Level.SEVERE, "Number of form fields: "+form.getNames().size());
    if (form.getNames().size() == 0 || "help".equals(format)) {
      output.append("<html><body>" +
          " <h1>Query Engine</h1>" +
          " <h2>/<a href=\"/queryengine\">"+rootURL+"Query Engine</a>/<a href=\"/queryengine/realtime\">Realtime Analysis Tools</a>/<a href=\"/queryengine/realtime/variants\">Variants</a>/<a href=\"/queryengine/realtime/variants/mismatches\">Mismatches</a></h2>" +
          " <h3>Documentation</h3>" +
          " This documentation explains how to construct a URL query for mismatches." +
          "  <h4>Required</h4>" +
          "   <ul>" +
          "       <li>format=[html|help]: the output format</li>" +
          "         <ul>" +
          "           <li>html: html report</li>" +
          "           <li>help: prints this help documentation</li>" +
          "         </ul>" +
          "   </ul>" +
          "  <h4>Optional</h4>" +
          "   <ul>" +
          "       <li>filter.contig=[contig_name|all]: one or more contigs must be specified, multiple contigs are given as separate params</li>" +
          "       <li>filter.start=int: start position</li>" +
          "       <li>filter.stop=int: stop position</li>" +
          "       <li>track.gene=string: the name of the gene, used to create a thumbnail and for display</li>" +
          "   </ul>" +
          "  <h4>Example</h4>" +
          "  ..." +
      "</body></html>");
      // output representation
      StringRepresentation repOutput = new StringRepresentation(output.toString());
      repOutput.setMediaType(MediaType.TEXT_HTML);
      return(repOutput);
    }

    // contig
    String contig = form.getValues("filter.contig");
    int start = Integer.parseInt(form.getValues("filter.start"));
    int stop = Integer.parseInt(form.getValues("filter.stop"));
    String gene = form.getValues("filter.gene");
    String contigNoChr = contig.replace("chr", "");

    // Formatter
    DecimalFormat df = new DecimalFormat("##0.0");

    // not used for now
    int minCoverage = 0;
    int  maxCoverage = Integer.MAX_VALUE;
    int minPhred = 0;
    boolean includeSNVs = true;
    int minObservations = 0;
    int minObservationsPerStrand = 0;
    int minPercent = 0;
    boolean includeIndels = true;

    // counting
    int totalVariants = 0;
    int totalSNVs = 0;
    int totalIndels = 0;
    int inOMIM = 0;
    int withSIFT = 0;
    int withDelSIFT = 0;
    int inDbSNP = 0;

    // db store used to connect
    Store store = null;

    try {
      MetadataDB metadataDB = new MetadataDB();
      root = metadataDB.getMetadata(Long.parseLong(swid));

      // FIXME: this is fragile, it assumes that the parameters are always stored as "cache_size" and "lock_counts"...
      //        need to pull from a metatable from the DB file itself if possible (maybe isn't!)
      if ("application/seqware-qe-postgresql-db".equals(root.get("metatype"))) {
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
      } else {
        store = BackendPool.getStore((String)root.get("filePath"), Long.parseLong((String)root.get("cache_size")), Integer.parseInt((String)root.get("lock_counts")));
      }

      if (store == null) { 
        getLogger().log(Level.SEVERE, "The Store is NULL!");
        throw new Exception("Store is null"); 
      }
      if (store != null) {


        getLogger().log(Level.SEVERE, "Processing Contig: "+contig);

        // get iterator of mismatches
        SeqWareIterator matchIt = null;
        if (start > 0 && stop > 0 && start <= stop) {
          getLogger().log(Level.SEVERE, "Getting Contig: "+contig+" start "+start+" stop "+stop);
          matchIt = store.getMismatches(contig, start, stop);
        }
        else { 
          getLogger().log(Level.SEVERE, "Getting Contig: "+contig);
          matchIt = store.getMismatches(contig);
        }

        // iterate over contents
        while(matchIt.hasNext()) {

          Variant m = (Variant) matchIt.next();

          getLogger().log(Level.SEVERE, "GOT THE VARIANT BACK!: "+m.getId());

          if (m != null && m.getReadCount() >= minCoverage && m.getReadCount() <= maxCoverage 
              && m.getConsensusCallQuality() >= minPhred) { 
            
            totalVariants++;

            //System.out.println("VARIANT PASSED FILTER 1!: "+m.getId());                

            // keep track of passing filters
            boolean passesSizeFilter = true; 
            boolean passesTagFilter = true;

            // process a SNV
            if (passesSizeFilter && passesTagFilter && m.getType() == Variant.SNV && includeSNVs && ("all".equals(contig) || m.getContig().equals(contig))) {
              //System.out.println(m.getContig()+" "+m.getStartPosition()+" "+m.getConsensusBase()); 

              totalSNVs++;
              
              // now at this point all this data has been calcualted when the mismatch object was created
              double calledPercent = ((double)m.getCalledBaseCount() / (double)m.getReadCount()) * (double)100.0;
              double calledFwdPercent = ((double)m.getCalledBaseCountForward() / (double)m.getReadCount()) * (double)100.0;
              double calledRvsPercent = ((double)m.getCalledBaseCountReverse() / (double)m.getReadCount()) * (double)100.0;

              String color = "80,175,175";
              String callStr = "heterozygous";
              if (m.getZygosity() == m.HOMOZYGOUS) { color = "0,50,180"; callStr = "homozygous"; }

              //System.out.println("VARIANT PASSED FILTER 6!: "+m.getId());  

              if (m.getCalledBaseCount() >= minObservations
                  && m.getCalledBaseCountForward() >= minObservationsPerStrand 
                  && m.getCalledBaseCountReverse() >= minObservationsPerStrand
                  && calledPercent >= minPercent) {

                //System.out.println("VARIANT PASSED FILTER 7!: "+m.getId());  
                int testTotal = m.getCalledBaseCountForward() + m.getCalledBaseCountReverse();
                //if (testTotal != m.getCalledBaseCount()) { throw new Exception("Forward and reverse don't add to total\n"); }
                output.append(m.getContig()+"\t"+m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+m.getReferenceBase()+"->"+m.getCalledBase()+"("+
                    m.getReadCount()+":"+m.getCalledBaseCount()+":"+df.format(calledPercent)+"%[F:"+m.getCalledBaseCountForward()+":"+df.format(calledFwdPercent)+"%|R:"+m.getCalledBaseCountReverse()+":"+df.format(calledRvsPercent)+"%]"+
                    "call="+callStr+":genome_phred="+m.getReferenceCallQuality()+":snp_phred="+m.getConsensusCallQuality()+
                    ":max_mapping_qual="+m.getMaximumMappingQuality()+":genome_max_qual="+m.getReferenceMaxSeqQuality()+":genome_ave_qual="+df.format(m.getReferenceAveSeqQuality())+
                    ":snp_max_qual="+m.getConsensusMaxSeqQuality()+":snp_ave_qual="+df.format(m.getConsensusAveSeqQuality())+":mismatch_id="+m.getId());
                Iterator<String> tagIt = m.getTags().keySet().iterator();
                while(tagIt.hasNext()) {
                  String tag = tagIt.next();
                  tag = tag.replace(' ', '_');
                  String value = m.getTagValue(tag);
                  output.append(":"+tag);
                  if (value != null) { value = value.replace(' ', '_'); output.append("="+value); }
                  tagsMap.put(tag, value);
                  if ("inOmimGene".equals(tag)) { inOMIM++; }
                  if ("hasSift".equals(tag)) { withSIFT++; }
                  if ("siftDeleterious".equals(tag)) { withDelSIFT++; }
                  if ("isDbSNP".equals(tag)) {inDbSNP++;}
                }
                int blockSize = m.getStopPosition() - m.getStartPosition();
                output.append(")\t"+m.getConsensusCallQuality()+"\t+\t"+
                    m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+color+"\t1\t"+blockSize+"\t0\n"
                );
                // FIXME: looks like the mean qual is not getting done properly
              }

            } else if (passesSizeFilter && passesTagFilter && (m.getType() == Variant.INSERTION || m.getType() == Variant.DELETION)
                && includeIndels && ("all".equals(contig) || m.getContig().equals(contig))) {

              totalIndels++;
              
              double calledPercent = ((double)m.getCalledBaseCount() / (double)m.getReadCount()) * (double)100.0;
              double calledFwdPercent = ((double)m.getCalledBaseCountForward() / (double)m.getReadCount()) * (double)100.0;
              double calledRvsPercent = ((double)m.getCalledBaseCountReverse() / (double)m.getReadCount()) * (double)100.0;

              String color = "80,175,175";
              String callStr = "heterozygous";
              if (m.getZygosity() == Variant.HOMOZYGOUS) { color = "0,50,180"; callStr = "homozygous"; }

              if (m.getCalledBaseCount() >= minObservations
                  && m.getCalledBaseCountForward() >= minObservationsPerStrand 
                  && m.getCalledBaseCountReverse() >= minObservationsPerStrand
                  && calledPercent >= minPercent) {

                //System.out.println("VARIANT PASSED FILTER 8!: "+m.getId());  

                // make the string used in the printout
                String bedString = null;
                StringBuffer lengthString = new StringBuffer();
                int blockSize = 1;
                for (int i=0; i<m.getCalledBase().length(); i++) { lengthString.append("-"); }
                if (m.getType() == Variant.INSERTION) {
                  bedString = "INS:"+lengthString+"->"+m.getCalledBase();
                } else if (m.getType() == Variant.DELETION) {
                  bedString = "DEL:"+m.getCalledBase()+"->"+lengthString;
                  blockSize = lengthString.length();
                } else { throw new Exception("What is type: "+m.getType()); }

                int testTotal = m.getCalledBaseCountForward() + m.getCalledBaseCountReverse();
                if (testTotal != m.getCalledBaseCount()) { throw new Exception("Forward and reverse don't add to total\n"); }
                // FIXME: this looks like some sort of bug!
                if (m.getType() == Variant.INSERTION && m.getStartPosition() != m.getStopPosition() -1) {
                  int bugStop = m.getStartPosition() + 1;
                  output.append(m.getContig()+"\t"+m.getStartPosition()+"\t"+bugStop+"\t"+bedString+"("+
                      m.getReadCount()+":"+m.getCalledBaseCount()+":"+df.format(calledPercent)+"%[F:"+m.getCalledBaseCountForward()+":"+df.format(calledFwdPercent)+"%|R:"+m.getCalledBaseCountReverse()+":"+df.format(calledRvsPercent)+"%]"+
                      "call="+callStr+":genome_phred="+m.getReferenceCallQuality()+":snp_phred="+m.getConsensusCallQuality()+
                      ":max_mapping_qual="+m.getMaximumMappingQuality()+":mismatch_id="+m.getId());
                } else {
                  output.append(m.getContig()+"\t"+m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+bedString+"("+
                      m.getReadCount()+":"+m.getCalledBaseCount()+":"+df.format(calledPercent)+"%[F:"+m.getCalledBaseCountForward()+":"+df.format(calledFwdPercent)+"%|R:"+m.getCalledBaseCountReverse()+":"+df.format(calledRvsPercent)+"%]"+
                      "call="+callStr+":genome_phred="+m.getReferenceCallQuality()+":snp_phred="+m.getConsensusCallQuality()+
                      ":max_mapping_qual="+m.getMaximumMappingQuality()+":mismatch_id="+m.getId());
                }
                Iterator<String> tagIt = m.getTags().keySet().iterator();
                while(tagIt.hasNext()) {
                  String tag = tagIt.next();
                  tag = tag.replace(' ', '_');
                  String value = m.getTagValue(tag);
                  output.append(":"+tag);
                  if (value != null) { value = value.replace(' ', '_'); output.append("="+value); }
                  tagsMap.put(tag, value);
                  if ("inOmimGene".equals(tag)) { inOMIM++; }
                  if ("hasSift".equals(tag)) { withSIFT++; }
                  if ("siftDeleterious".equals(tag)) { withDelSIFT++; }
                  if ("isDbSNP".equals(tag)) {inDbSNP++;}
                }
                if (m.getType() == Variant.INSERTION && m.getStartPosition() != m.getStopPosition() -1) {
                  int bugStop = m.getStartPosition() + 1;
                  output.append(")\t"+m.getConsensusCallQuality()+"\t+\t"+
                      m.getStartPosition()+"\t"+bugStop+"\t"+color+"\t1\t"+blockSize+"\t0\n"
                  );
                } else {
                  output.append(")\t"+m.getConsensusCallQuality()+"\t+\t"+
                      m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+color+"\t1\t"+blockSize+"\t0\n"
                  );
                }
              }
            }
          }
        }
        matchIt.close();

      }

      // finally close
      if (!"application/seqware-qe-hbase-db".equals(root.get("metatype")) && !"application/seqware-qe-postgresql-db".equals(root.get("metatype"))) {
        BackendPool.releaseStore((String)root.get("filePath"));
      }
      else { // everything else, just release
        store.close();
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



    if (output.length() > 0) {
      
      int notOMIM = totalVariants - inOMIM;
      int notDel = withSIFT - withDelSIFT;
      int notSIFT = totalVariants - withSIFT;
      
      String html = "<html>" +
          "  <head> "+
    "<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>"+
    "<script type=\"text/javascript\">"+
      "google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});"+
      "google.setOnLoadCallback(drawChart);"+
      "function drawChart() {"+
        "var data = new google.visualization.DataTable();"+
        "data.addColumn('string', 'Variant Type');"+
        "data.addColumn('number', 'Count');"+
        "data.addRows(2);"+
        "data.setValue(0, 0, 'SNV');"+
        "data.setValue(0, 1, "+totalSNVs+");"+
        "data.setValue(1, 0, 'Indel');"+
        "data.setValue(1, 1, "+totalIndels+");"+

        "var chart = new google.visualization.PieChart(document.getElementById('chart_div_1'));"+
        "chart.draw(data, {width: 450, height: 300, title: 'Variant Types'});"+
     " }"+
     "google.setOnLoadCallback(drawChart2);"+
     "function drawChart2() {"+
       "var data = new google.visualization.DataTable();"+
       "data.addColumn('string', 'OMIM');"+
       "data.addColumn('number', 'Count');"+
       "data.addRows(2);"+
       "data.setValue(0, 0, 'in OMIM');"+
       "data.setValue(0, 1, "+inOMIM+");"+
       "data.setValue(1, 0, 'not in OMIM');"+
       "data.setValue(1, 1, "+notOMIM+");"+

       "var chart = new google.visualization.PieChart(document.getElementById('chart_div_2'));"+
       "chart.draw(data, {width: 450, height: 300, title: 'OMIM Annotations'});"+
    " }"+
    "google.setOnLoadCallback(drawChart3);"+
    "function drawChart3() {"+
      "var data = new google.visualization.DataTable();"+
      "data.addColumn('string', 'SIFT Score');"+
      "data.addColumn('number', 'Count');"+
      "data.addRows(3);"+
      "data.setValue(0, 0, 'SIFT Deleterious');"+
      "data.setValue(0, 1, "+withDelSIFT+");"+
      "data.setValue(1, 0, 'SIFT Benign');"+
      "data.setValue(1, 1, "+notDel+");"+
      "data.setValue(2, 0, 'without SIFT');"+
      "data.setValue(2, 1, "+notSIFT+");"+

      "var chart = new google.visualization.PieChart(document.getElementById('chart_div_3'));"+
      "chart.draw(data, {width: 450, height: 300, title: 'SIFT Mutation Severity Score Annotations'});"+
   " }"+
   " </script>"+
 " </head>"+
      		"<body>" +
      		"<h2>Gene Report for " + gene +"</h2>" +
      		"<p>This is a basic report summarizing the variants located in gene " +gene+" within the genomic range of: "+
      		contig+":" + start +"-"+stop+"</p>"+
      		"<h3>Gene Visualized with Ensembl</h3><img src=\"http://useast.ensembl.org/Homo_sapiens/Component/Gene/Web/TranscriptsImage?_rmd=474e;db=core;g="+gene+";export=png-0.8\"/>"+
      		"<p>" +
      		"<a href=\"http://genome.ucsc.edu/cgi-bin/hgTracks?position="+contig+":"+start+"-"+stop+"\">View in UCSC Browser...</a>" +
      				"  <a href=\"http://useast.ensembl.org/Homo_sapiens/Location/View?r="+contigNoChr+":"+start+"-"+stop+"\"> View in Ensembl Browser...</a></p>" +
      		"</p>"+
      		"<h3>Tabular Summary</h3><table border=\"1\">" +
      		"<tr><td>Summary</td><td>Value</td>" +
          "<tr><td>Total Variants</td><td>"+totalVariants+"</td>" +
          "<tr><td>Total SNVs</td><td>"+totalSNVs+"</td>" +
          "<tr><td>Total Indels</td><td>"+totalIndels+"</td>" +
          "<tr><td>Variants in OMIM</td><td>"+inOMIM+"</td>" +
          "<tr><td>Variants with SIFT score</td><td>"+withSIFT+"</td>" +
          "<tr><td>Variants with deleterious SIFT score</td><td>"+withDelSIFT+"</td>" +
          "<tr><td>Variants in dbSNP 132</td><td>"+inDbSNP+"</td>" +
      		"</table>" +
      		"<h3>Charts</h3><div id=\"chart_div_1\"></div><div id=\"chart_div_2\"></div><div id=\"chart_div_3\"></div>"+
      		"<h3>Variant Ouput in BED Format</h3>" +
      		"<textarea rows=\"30\" cols=\"150\">" +output.toString()+"</textarea>"+
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
