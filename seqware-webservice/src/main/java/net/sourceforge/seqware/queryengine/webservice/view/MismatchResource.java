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

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author boconnor
 *
 */
public class MismatchResource extends ServerResource {

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

    // contig
    String requestContigs = form.getValues("filter.contig");
    if (requestContigs != null) {
      for (String requestContig : requestContigs.split(",\\s*")) {
        contigs.add(requestContig);
      }
    }
    else {
      contigs.add("all");
      getLogger().log(Level.SEVERE, "Contig is null!");
    }

    // Formatter
    DecimalFormat df = new DecimalFormat("##0.0");

    // lookup by tags with AND
    boolean lookupByTags = false;
    ArrayList<String> tags = new ArrayList<String>();
    ArrayList<String> andNotTags = new ArrayList<String>();
    String requestTags = form.getValues("filter.tag");
    String requestTagsAnd = form.getValues("filter.tag.and");
    String requestTagsAndNot = form.getValues("filter.tag.and.not");
    if (requestTags != null) {
      lookupByTags = true;
      for (String requestTag : requestTags.split(",\\s*")) {
        getLogger().log(Level.SEVERE, "TAG: "+requestTag);
        tags.add(requestTag);
      }
    }
    if (requestTagsAnd != null) {
      lookupByTags = true;
      for (String requestTag : requestTagsAnd.split(",\\s*")) {
        getLogger().log(Level.SEVERE, "AND TAG: "+requestTag);
        tags.add(requestTag);
      }
    }
    if (requestTagsAndNot != null) {
      lookupByTags = true;
      for (String requestTag : requestTagsAndNot.split(",\\s*")) {
        getLogger().log(Level.SEVERE, "AND NOT TAG: "+requestTag);
        andNotTags.add(requestTag);
      }
    }
    
    // lookup by tags with OR
    boolean lookupByOrTags = false;
    ArrayList<String> orTags = new ArrayList<String>();
    String requestTagsOr = form.getValues("filter.tag.or");
    if (requestTagsOr != null) {
      lookupByOrTags = true;
      for (String requestTag : requestTagsOr.split(",\\s*")) {
        getLogger().log(Level.SEVERE, "OR TAG: "+requestTag);
        orTags.add(requestTag);
      }
    }

    int minCoverage = 0;
    String requestMinCov = form.getFirstValue("filter.minCoverage");
    if (requestMinCov != null) { minCoverage = Integer.parseInt(requestMinCov); }
    int maxCoverage = Integer.MAX_VALUE;
    String requestMaxCov = form.getFirstValue("filter.maxCoverage");
    if (requestMaxCov != null) { maxCoverage = Integer.parseInt(requestMaxCov); }

    String requestMinPhred = form.getFirstValue("filter.minPhred");
    int minPhred = 0;
    if (requestMinPhred != null) { minPhred = Integer.parseInt(requestMinPhred);}
    boolean includeSNVs = true;
    String requestIncludeSNVs = form.getFirstValue("filter.includeSNVs");
    if (requestIncludeSNVs != null) { includeSNVs = "true".equals(requestIncludeSNVs); }
    boolean includeIndels = true;
    String requestIncludeIndels = form.getFirstValue("filter.includeIndels");
    if (requestIncludeIndels != null) { includeIndels = "true".equals(requestIncludeIndels); }    

    int minObservations = 0;
    String requestMinObs = form.getFirstValue("filter.minObservations");
    if (requestMinObs != null) { minObservations = Integer.parseInt(requestMinObs); }
    
    int sizeMin = -1;
    int sizeMax = -1;
    String sizeRange = form.getFirstValue("filter.size");
    if (sizeRange != null && sizeRange.matches("\\d+-\\d+")) { 
      Pattern pat = Pattern.compile("(\\d+)-(\\d+)");
      Matcher m = pat.matcher(sizeRange);
      if (m.find()) {
        sizeMin = Integer.parseInt(m.group(1));
        sizeMax = Integer.parseInt(m.group(2));
      }
    }

    int minObservationsPerStrand = 0;
    String requestMinObsPerStrand = form.getFirstValue("filter.minObservationsPerStrand");
    if (requestMinObsPerStrand != null) { minObservationsPerStrand = Integer.parseInt(requestMinObsPerStrand); }

    int minPercent = 0;
    String requestMinPercent = form.getFirstValue("filter.minPercent");
    if (requestMinPercent != null) { minPercent = Integer.parseInt(requestMinPercent); }

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
        Template temp = cfg.getTemplate("mismatch_query.ftl");
        
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

    } else if ("bed".equals(format) || "tags".equals(format)) {

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
      
        if (store == null) { throw new Exception("Store is null"); }
        if (store != null) {

          Iterator<String> it = contigs.iterator();

          while (it.hasNext()) {

            // chr
            String contig = (String) it.next();
            getLogger().log(Level.SEVERE, "Processing Contig: "+contig);

            // get iterator of mismatches
            SeqWareIterator matchIt = null;
            if ("all".equals(contig)) { 
              //System.out.println("test: all "+contig); System.exit(0);
              if (lookupByTags && tags.size() > 0) {
                // only uses first tag for this query, better choose well!
                getLogger().log(Level.SEVERE, "Looking up by Tag: "+tags.get(0));
                matchIt = store.getMismatchesByTag(tags.get(0)); 
              } else {
                getLogger().log(Level.SEVERE, "Getting all mismatches");
                matchIt = store.getMismatches(); 
              }
            } else if (contig.matches("(\\S+):(\\d+)-(\\d+)")) {
              getLogger().log(Level.SEVERE, "Contig matches chr:start-stop");
              String[] t = contig.split("[-:]");
              contig = t[0];
              matchIt = store.getMismatches(t[0], Integer.parseInt(t[1]), Integer.parseInt(t[2]));
            }
            else { 
              //System.out.println("test contig: "+contig); System.exit(0); 
              getLogger().log(Level.SEVERE, "Contig matches chr only");
              matchIt = store.getMismatches(contig);
            }

            // iterate over contents
            while(matchIt.hasNext()) {

              Variant m = (Variant) matchIt.next();
              
              //System.out.println("GOT THE VARIANT BACK!: "+m.getId());

              if (m != null && m.getReadCount() >= minCoverage && m.getReadCount() <= maxCoverage 
                  && m.getConsensusCallQuality() >= minPhred) { 
                
                //System.out.println("VARIANT PASSED FILTER 1!: "+m.getId());                

                // keep track of passing filters
                boolean passesSizeFilter = true;
                // check the size if defined and it's an indel
                if (sizeMin > -1 && sizeMax > -1 && (m.getType() == m.INSERTION || m.getType() == m.DELETION)) {
                  if (m.getCalledBase().length() < sizeMin || m.getCalledBase().length() > sizeMax) {
                    passesSizeFilter = false;
                  }
                }
                //System.out.println("VARIANT PASSED FILTER 2!: "+m.getId());  
                // ALL tags, must pass all tags
                // FIXME: could imagine wanting OR and NOT operations
                
                // testing variant tags
                //System.out.println("Testing variant tags: ");
                //for (String tag : m.getTags().keySet()) {
                  //System.out.println("tag: "+tag+" value: "+m.getTagValue(tag));
                //}
                
                boolean passesTagFilter = true;
                int seen = 0;
                HashMap<String,String> mTags = null;
                if (tags.size() > 0) {
                  if (mTags == null) { mTags = m.getTags(); }
                  for (String tag : tags) {
                    //System.out.println("Testing tag: "+tag);
                    if (mTags.containsKey(tag)) {
                      seen++; //System.out.println("Variant has the tag: "+tag);
                    }
                  }
                  if (seen == tags.size()) { passesTagFilter = true; }
                  else { passesTagFilter = false; }
                }
                //System.out.println("VARIANT PASSED FILTER 3!: "+m.getId());  
                
                // AND NOT Tags, must contain no matches
                if (andNotTags.size() > 0) {
                  seen = 0;
                  if (mTags == null) { mTags = m.getTags(); }
                  for (String tag : andNotTags) {
                    //System.out.println("Testing tag: "+tag);
                    if (mTags.containsKey(tag)) { seen++; }
                  }
                  if (seen == 0 && passesTagFilter != false) { passesTagFilter = true; }
                  else { passesTagFilter = false; }
                }
                //System.out.println("VARIANT PASSED FILTER 4!: "+m.getId());  
                
                // OR Tags, must contain one or more
                if (orTags.size() > 0) {
                  seen = 0;
                  if (mTags == null) { mTags = m.getTags(); }
                  for (String tag : orTags) {
                    //System.out.println("Testing tag: "+tag);
                    if (mTags.containsKey(tag) && passesTagFilter != false) { passesTagFilter = true; seen = 1; break; }
                  }
                  if (seen == 0) {
                    passesTagFilter = false;
                  }
                }
                //System.out.println("VARIANT PASSED FILTER 5!: "+m.getId()+" passesSizeFilter: "+passesSizeFilter+" passestagfilter: "+
                //    passesTagFilter+" type: "+m.getType()+" contig: "+contig);  
               
                // process a SNV
                if (passesSizeFilter && passesTagFilter && m.getType() == Variant.SNV && includeSNVs && ("all".equals(contig) || m.getContig().equals(contig))) {
                  //System.out.println(m.getContig()+" "+m.getStartPosition()+" "+m.getConsensusBase()); 

                  // now at this point all this data has been calcualted when the mismatch object was created
                  double calledPercent = 0;
                  if (m.getCalledBaseCount() > 0 && m.getReadCount() > 0) { calledPercent = ((double)m.getCalledBaseCount() / (double)m.getReadCount()) * (double)100.0; }
                  double calledFwdPercent = 0;
                  double calledRvsPercent = 0;
                  if (m.getCalledBaseCountForward() > 0 && m.getCalledBaseCountReverse() > 0 && m.getReadCount() > 0) { 
                    calledFwdPercent = ((double)m.getCalledBaseCountForward() / (double)m.getReadCount()) * (double)100.0;
                    calledRvsPercent = ((double)m.getCalledBaseCountReverse() / (double)m.getReadCount()) * (double)100.0;
                  }

                  String color = "80,175,175";
                  String callStr = "heterozygous";
                  if (m.getZygosity() == m.HOMOZYGOUS) { color = "0,50,180"; callStr = "homozygous"; }

                  //System.out.println("VARIANT PASSED FILTER 6!: "+m.getId()+" "+calledPercent+" "+m.getCalledBaseCountForward()+" "+m.getCalledBaseCountReverse()+" "+m.getCalledBaseCount());  
                  
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
                    }
                    int blockSize = m.getStopPosition() - m.getStartPosition();
                    output.append(")\t"+m.getConsensusCallQuality()+"\t+\t"+
                        m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+color+"\t1\t"+blockSize+"\t0\n"
                    );
                    // FIXME: looks like the mean qual is not getting done properly
                  }

                } else if (passesSizeFilter && passesTagFilter && (m.getType() == Variant.INSERTION || m.getType() == Variant.DELETION)
                    && includeIndels && ("all".equals(contig) || m.getContig().equals(contig))) {

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
      if (output.length() > 0 && "bed".equals(format)) {
        // output representation
        if (trackName == null) { trackName = "SeqWare BED "+(new Date()).toString(); }
        if (trackOptions == null) { trackOptions = ""; }
        StringRepresentation repOutput = new StringRepresentation("track name=\""+trackName+"\" "+trackOptions+"\n"+output.toString());
        repOutput.setMediaType(MediaType.TEXT_PLAIN);
        return(repOutput);
      } else if ("tags".equals(format)) {
        output = new StringBuffer();
        for (String key : tagsMap.keySet()) {
          output.append(key+"\n");
        }
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
  	  rootURLEncoded = rootURLEncoded.replaceAll(" ", "%20");
  	  String displayPosition = contigs.get(0);
  	  // FIXME: there are assumptions here about how the URL should be constructed e.g. hg18 and chr22, these should be params
  	  if ("all".equals(displayPosition)) { displayPosition = "chr17"; }
  	  StringBuffer customURL = new StringBuffer();
  	  customURL.append(rootURLEncoded+"%2Fqueryengine%2Frealtime%2Fvariants%2Fmismatches%2F"+swid+"%3Fformat%3Dbed");
	  	if (trackName != null) { customURL.append("%26track.name%3D"+trackName); }
	  	if (trackOptions != null) { customURL.append("%26track.options%3D"+trackOptions); }
	  	if (requestContigs != null) { customURL.append("%26filter.contig%3D"+requestContigs); }
	  	if (requestTags != null) { customURL.append("%26filter.tag%3D"+requestTags); }
	  	if (requestTagsAnd != null) { customURL.append("%26filter.tag.and%3D"+requestTagsAnd); }
	  	if (requestTagsAndNot != null) { customURL.append("%26filter.tag.and.not%3D"+requestTagsAndNot); }
	  	if (requestTagsOr != null) { customURL.append("%26filter.tag.or%3D"+requestTagsOr); }
	  	if (requestMinCov != null) { customURL.append("%26filter.minCoverage%3D"+requestMinCov); }
	  	if (requestMaxCov != null) { customURL.append("%26filter.maxCoverage%3D"+requestMaxCov); }
	  	if (requestMinPhred != null) { customURL.append("%26filter.minPhred%3D"+requestMinPhred); }
	  	if (requestIncludeSNVs != null) { customURL.append("%26filter.includeSNVs%3D"+requestIncludeSNVs); }
	  	if (requestIncludeIndels != null) { customURL.append("%26filter.includeIndels%3D"+requestIncludeIndels); }
	  	if (requestMinObs != null) { customURL.append("%26filter.minObservations%3D"+requestMinObs); }
	  	if (sizeRange != null) { customURL.append("%26filter.size%3D"+sizeRange); }
	  	if (requestMinObsPerStrand != null) { customURL.append("%26filter.minObservationsPerStrand%3D"+requestMinObsPerStrand); }
	  	if (requestMinPercent != null) { customURL.append("%26filter.minPercent%3D"+requestMinPercent); }
	  	customURL.append("%26format%3Dbed");
	  	
	  	if ("html".equals(format)) {
  	  output.append(
  		"<html>" +
  		"<!--"+rootURL+"-->" +
  		"<h1>Query Engine</h1>" +
          "<h2>/<a href=\"/queryengine\">Query Engine</a>/<a href=\"/queryengine/realtime\">Realtime Analysis Tools</a>/<a href=\"/queryengine/realtime/variants\">Variants</a>/<a href=\"/queryengine/realtime/variants/mismatches\">Mismatches</a></h2>" +
          "<p>The following links will load the results of your query in a given genome browser. Note that very large, long-running queries may fail to load since the client genome browser may time out. If this is the case break your query into smaller ranges or download a BED/WIG file an upload manually.</p>" +
  		// UCSC
          "<h3>UCSC Browser</h3>" +
  		"<a href=\"http://genome.ucsc.edu/cgi-bin/hgTracks?org=hg19&position="+displayPosition+"&hgt.customText="+
  		  customURL.toString().replaceAll(" ", "%20"));
	  output.append("\">Load results in UCSC browser</a>");
	  
	  // IGV
	  String customIgvURL = customURL.toString().replaceAll("%26", "%26amp;");
	  customIgvURL = customIgvURL.replaceAll("format%3Dbed", "format%3Digv.xml");
	  customIgvURL = customIgvURL.replaceAll(" ", "%20");
	  
	  output.append("<h3>Integrative Genomics Viewer</h3>" +
	  		"<a href=\"http://www.broadinstitute.org/igv/dynsession/igv.jnlp?sessionURL="+customIgvURL+"&locus="+displayPosition+"&user=SeqWareQueryEngine\">Load result in IGV browser</a>"
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

	  		output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
              "<Global genome=\"hg19\" version=\"2\">" +
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
