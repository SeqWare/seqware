/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.view;


import java.io.File;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.model.Consequence;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.webservice.model.BackendPool;
import net.sourceforge.seqware.queryengine.webservice.model.MetadataDB;

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
 * <p>ConsequenceResource class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ConsequenceResource extends ServerResource {

  /**
   * <p>represent.</p>
   *
   * @return a {@link org.restlet.representation.Representation} object.
   */
  @Get
  public Representation represent() {  

    // logging
    this.getLogger().log(Level.SEVERE, "TESTING");

    // get the swid
    String swid = (String)getRequestAttributes().get("consequenceId");

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
    Map root = new HashMap();

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
      Template temp = cfg.getTemplate("consequence_help.ftl");
      root.put("intMax", Integer.MAX_VALUE);
      
      temp.process(root, sw);
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
    
    // lookup by tags with AND
    boolean lookupByTags = false;
    ArrayList<String> tags = new ArrayList<String>();
    ArrayList<String> andNotTags = new ArrayList<String>();
    String requestTags = form.getValues("filter.tag");
    String requestTagsAnd = form.getValues("filter.tag.and");
    String requestTagsAndNot = form.getValues("filter.tag.and.not");
    if (requestTags != null) {
      lookupByTags = true;
      for (String requestTag : requestTags.split(",")) {
        getLogger().log(Level.SEVERE, "TAG: "+requestTag);
        tags.add(requestTag);
      }
    }
    if (requestTagsAnd != null) {
      lookupByTags = true;
      for (String requestTag : requestTagsAnd.split(",")) {
        getLogger().log(Level.SEVERE, "AND TAG: "+requestTag);
        tags.add(requestTag);
      }
    }
    if (requestTagsAndNot != null) {
      lookupByTags = true;
      for (String requestTag : requestTagsAndNot.split(",")) {
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
      for (String requestTag : requestTagsOr.split(",")) {
        getLogger().log(Level.SEVERE, "OR TAG: "+requestTag);
        orTags.add(requestTag);
      }
    }

    int minCoverage = 1;
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

    // return the HTML form
    if ("form".equals(format)) {

      // Specify the data source where the template files come from.
      // Here I set a file directory for it:
      try { 

        // now build a model
        root = new HashMap();
        //root.put("url", "/seqware/queryengine/realtime/coverage/basecoverages/"+id);
        root.put("url", "");

        // get template
        Template temp = cfg.getTemplate("consequence_query.ftl");

        // write the output of template
        StringWriter sw = new StringWriter();
        temp.process(root, sw);

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
    else if ("tab".equals(format)) {

      //BerkeleyDBFactory factory = new BerkeleyDBFactory();
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
          output.append("#consequence_id\tcoding_region\tgene_model_id\tmismatch_id\tmismatch_position\tmismatch_codon_change\tmismatch_amino_acid_change\ttags\n");
          
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
                matchIt = store.getMismatchesByTag(tags.get(0)); 
              } else {
                matchIt = store.getMismatches(); 
              }
            } else if (contig.matches("(\\S+):(\\d+)-(\\d+)")) {
              String[] t = contig.split("[-:]");
              contig = t[0];
              matchIt = store.getMismatches(t[0], Integer.parseInt(t[1]), Integer.parseInt(t[2]));
            }
            else { 
              //System.out.println("test contig: "+contig); System.exit(0); 
              matchIt = store.getMismatches(contig);
            }

            // iterate over contents
            while(matchIt.hasNext()) {
            
            Variant m = (Variant) matchIt.next();

              if (m != null && m.getReadCount() >= minCoverage && m.getReadCount() <= maxCoverage 
                  && m.getConsensusCallQuality() >= minPhred) { 
  
                // keep track of passing filters
                boolean passesSizeFilter = true;
                // check the size if defined and it's an indel
                if (sizeMin > -1 && sizeMax > -1 && (m.getType() == m.INSERTION || m.getType() == m.DELETION)) {
                  if (m.getCalledBase().length() < sizeMin || m.getCalledBase().length() > sizeMax) {
                    passesSizeFilter = false;
                  }
                }
  
                // ALL tags, must pass all tags
                // FIXME: could imagine wanting OR and NOT operations
                boolean passesTagFilter = true;
                int seen = 0;
                HashMap<String,String> mTags = null;
                if (tags.size() > 0) {
                  if (mTags == null) { mTags = m.getTags(); }
                  for (String tag : tags) {
                    //System.out.println("Testing tag: "+tag);
                    if (mTags.containsKey(tag)) { seen++; }
                  }
                  if (seen == tags.size()) { passesTagFilter = true; }
                  else { passesTagFilter = false; }
                }
                
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
               
                // process a SNV
                if (passesSizeFilter && passesTagFilter && ((m.getType() == Variant.SNV && includeSNVs) ||
                    (m.getType() == Variant.INSERTION && includeIndels) || (m.getType() == Variant.DELETION && includeIndels))
                    && ("all".equals(contig) || m.getContig().equals(contig))) {
                  //System.out.println(m.getContig()+" "+m.getStartPosition()+" "+m.getConsensusBase()); 
  
                  // now at this point all this data has been calcualted when the mismatch object was created
                  double calledPercent = ((double)m.getCalledBaseCount() / (double)m.getReadCount()) * (double)100.0;
                  //double calledFwdPercent = ((double)m.getCalledBaseCountForward() / (double)m.getReadCount()) * (double)100.0;
                  //double calledRvsPercent = ((double)m.getCalledBaseCountReverse() / (double)m.getReadCount()) * (double)100.0;
  
                  //String color = "80,175,175";
                  //String callStr = "heterozygous";
                  //if (m.getZygosity() == m.HOMOZYGOUS) { color = "0,50,180"; callStr = "homozygous"; }
  
  
                  if (m.getCalledBaseCount() >= minObservations
                      && m.getCalledBaseCountForward() >= minObservationsPerStrand 
                      && m.getCalledBaseCountReverse() >= minObservationsPerStrand
                      && calledPercent >= minPercent) {
  
                    //int testTotal = m.getCalledBaseCountForward() + m.getCalledBaseCountReverse();
                    //if (testTotal != m.getCalledBaseCount()) { throw new Exception("Forward and reverse don't add to total\n"); }

                    // now lookup a consequence
                    
                    SeqWareIterator consIt = store.getConsequencesByMismatch(m.getId());
                    if (consIt != null) {
                      while(consIt.hasNext()) {
                        Consequence c = (Consequence)consIt.next();
                        
                        Iterator<String> tagIt = c.getTags().keySet().iterator();
                        StringBuffer tagsBuffer = new StringBuffer();
                        boolean first = true;
                        while(tagIt.hasNext()) {
                          String tag = tagIt.next();
                          String value = m.getTagValue(tag);
                          if (first) { first = false; tagsBuffer.append(tag); } 
                          else { tagsBuffer.append(":"+tag); }
                          if (value != null) { tagsBuffer.append("="+value); }
                          tagsMap.put(tag, value);
                        }
                        
                        // Just because the mismatch record passed the tag filter doesn't mean the consequence record will
                        // so we filter again by tags
                        // ALL tags, must pass all tags
                        // FIXME: could imagine wanting OR and NOT operations
                        boolean passesConsTagFilter = true;
                        int cSeen = 0;
                        HashMap<String,String> cTags = null;
                        if (tags.size() > 0) {
                          if (cTags == null) { cTags = c.getTags(); }
                          for (String tag : tags) {
                            //System.out.println("Testing tag: "+tag);
                            if (cTags.containsKey(tag)) { cSeen++; }
                          }
                          if (cSeen == tags.size()) { passesConsTagFilter = true; }
                          else { passesConsTagFilter = false; }
                        }
                        
                        // AND NOT Tags, must contain no matches
                        if (andNotTags.size() > 0) {
                          cSeen = 0;
                          if (cTags == null) { cTags = c.getTags(); }
                          for (String tag : andNotTags) {
                            //System.out.println("Testing tag: "+tag);
                            if (cTags.containsKey(tag)) { cSeen++; }
                          }
                          if (cSeen == 0 && passesConsTagFilter != false) { passesConsTagFilter = true; }
                          else { passesConsTagFilter = false; }
                        }
                        
                        // OR Tags, must contain one or more
                        if (orTags.size() > 0) {
                          cSeen = 0;
                          if (cTags == null) { cTags = c.getTags(); }
                          for (String tag : orTags) {
                            //System.out.println("Testing tag: "+tag);
                            if (cTags.containsKey(tag) && passesConsTagFilter != false) { passesConsTagFilter = true; cSeen = 1; break; }
                          }
                          if (cSeen == 0) {
                          	passesConsTagFilter = false;
                          }
                        }
                        
                        if (passesConsTagFilter) {
                      	  output.append(c.getId()+"\t"+c.getMismatchChr()+":"+c.getCodingStart()+"-"+c.getCodingStop()+"\t"+c.getGeneId()+"\t"+
                            c.getMismatchId()+"\t"+c.getMismatchChr()+":"+c.getMismatchStart()+"-"+c.getMismatchStop()+"\t"+c.getMismatchCodonChange()+"\t"+
                            c.getMismatchAminoAcidChange()+"\t"+tagsBuffer.toString()+"\n"
                            );
                        }
                      }
                    }
                  }
                }
              }
            }
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
      if (output.length() > 0 && "tab".equals(format)) {
        // output representation
        StringRepresentation repOutput = new StringRepresentation(output.toString());
        repOutput.setMediaType(MediaType.TEXT_PLAIN);
        return(repOutput);
      }
    }
    //return "hello, world "+getRequestAttributes().get("mismatchId");  
    StringRepresentation repOutput = new StringRepresentation("# No Results!");
    repOutput.setMediaType(MediaType.TEXT_PLAIN);
    return(repOutput);
  }
}
