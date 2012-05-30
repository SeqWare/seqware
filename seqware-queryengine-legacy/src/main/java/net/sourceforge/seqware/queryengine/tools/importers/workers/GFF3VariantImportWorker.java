/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.importers.workers;


import org.biojava3.genome.parsers.gff.FeatureList;
import org.biojava3.genome.parsers.gff.GFF3Reader;
import org.biojava3.genome.parsers.gff.FeatureI;

import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Variant;

/**
 * @author boconnor
 *
 * A simple worker thread to parse GFF3 files as defined at:
 * http://gmod.org/wiki/GFF3
 * 
 * This is a relatively fluid file format since much of the information is encoded in the 9th column using key/value pairs.
 * 
 * For the purposes of this SeqWare Query Engine variant import worker, the following key/values are recognized and handled specially:
 * isDbSNP, location, zygosity, kegg, omim, go, gene, consequence, variant
 * 
 * All other key/values are just treated like key values in the SeqWare database and are saved without modification.
 * 
 * Generally, for the GFF file format the 9 columns are as follows (copied from http://gmod.org/wiki/GFF3):

Column 1: "seqid"

    The ID of the landmark used to establish the coordinate system for the current feature. IDs may contain any characters, but must escape any characters not in the set [a-zA-Z0-9.:^*$@!+_?-|]. In particular, IDs may not contain unescaped whitespace and must not begin with an unescaped ">". 

    To escape a character in this, or any of the other GFF3 fields, replace it with the percent sign followed by its hexadecimal representation. For example, ">" becomes "%E3". See URL Encoding (or: 'What are those "%20" codes in URLs?') for details. 

Column 2: "source"

    The source is a free text qualifier intended to describe the algorithm or operating procedure that generated this feature. Typically this is the name of a piece of software, such as "Genescan" or a database name, such as "Genbank." In effect, the source is used to extend the feature ontology by adding a qualifier to the type creating a new composite type that is a subclass of the type in the type column. It is not necessary to specify a source. If there is no source, put a "." (a period) in this field. 

Column 3: "type"

    The type of the feature (previously called the "method"). This is constrained to be either: (a) a term from the "lite" sequence ontology, SOFA; or (b) a SOFA accession number. The latter alternative is distinguished using the syntax SO:000000. This field is required. 

Columns 4 & 5: "start" and "end"

    The start and end of the feature, in 1-based integer coordinates, relative to the landmark given in column 1. Start is always less than or equal to end. 

    For zero-length features, such as insertion sites, start equals end and the implied site is to the right of the indicated base in the direction of the landmark. These fields are required. 

Column 6: "score"

    The score of the feature, a floating point number. As in earlier versions of the format, the semantics of the score are ill-defined. It is strongly recommended that E-values be used for sequence similarity features, and that P-values be used for ab initio gene prediction features. If there is no score, put a "." (a period) in this field. 

Column 7: "strand"

    The strand of the feature. + for positive strand (relative to the landmark), - for minus strand, and . for features that are not stranded. In addition, ? can be used for features whose strandedness is relevant, but unknown. 

Column 8: "phase"

    For features of type "CDS", the phase indicates where the feature begins with reference to the reading frame. The phase is one of the integers 0, 1, or 2, indicating the number of bases that should be removed from the beginning of this feature to reach the first base of the next codon. In other words, a phase of "0" indicates that the next codon begins at the first base of the region described by the current line, a phase of "1" indicates that the next codon begins at the second base of this region, and a phase of "2" indicates that the codon begins at the third base of this region. This is NOT to be confused with the frame, which is simply start modulo 3. If there is no phase, put a "." (a period) in this field. 

    For forward strand features, phase is counted from the start field. For reverse strand features, phase is counted from the end field. 

    The phase is required for all CDS features. 

Column 9: "attributes"

    A list of feature attributes in the format tag=value. Multiple tag=value pairs are separated by semicolons. URL escaping rules are used for tags or values containing the following characters: ",=;". Spaces are allowed in this field, but tabs must be replaced with the %09 URL escape. This field is not required. 

Column 9 Tags

Column 9 tags have predefined meanings:

ID
    Indicates the unique identifier of the feature. IDs must be unique within the scope of the GFF file. 

Name
    Display name for the feature. This is the name to be displayed to the user. Unlike IDs, there is no requirement that the Name be unique within the file. 

Alias
    A secondary name for the feature. It is suggested that this tag be used whenever a secondary identifier for the feature is needed, such as locus names and accession numbers. Unlike ID, there is no requirement that Alias be unique within the file. 

Parent
    Indicates the parent of the feature. A parent ID can be used to group exons into transcripts, transcripts into genes, and so forth. A feature may have multiple parents. Parent can *only* be used to indicate a partof relationship. 

Target
    Indicates the target of a nucleotide-to-nucleotide or protein-to-nucleotide alignment. The format of the value is "target_id start end [strand]", where strand is optional and may be "+" or "-". If the target_id contains spaces, they must be escaped as hex escape %20. 

Gap
    The alignment of the feature to the target if the two are not collinear (e.g. contain gaps). The alignment format is taken from the CIGAR format described in the Exonerate documentation. http://cvsweb.sanger.ac.uk/cgi-bin/cvsweb.cgi/exonerate?cvsroot=Ensembl). See the GFF3 specification for more information. 

Derives_from
    Used to disambiguate the relationship between one feature and another when the relationship is a temporal one rather than a purely structural "part of" one. This is needed for polycistronic genes. See the GFF3 specification for more information. 

Note
    A free text note. 

Dbxref
    A database cross reference. See the GFF3 specification for more information. 

Ontology_term
    A cross reference to an ontology term. See the GFF3 specification for more information. 

Multiple attributes of the same type are indicated by separating the values with the comma "," character, as in:

Parent=AF2312,AB2812,abc-3

Note that attribute names are case sensitive. "Parent" is not the same as "parent".

All attributes that begin with an uppercase letter are reserved for later use. Attributes that begin with a lowercase letter can be used freely by applications. You can stash any semi-structured data into the database by using one or more unreserved (lowercase) tags.

 * 
 */
public class GFF3VariantImportWorker extends ImportWorker {


  public GFF3VariantImportWorker() { }


  public void run() {

    try {

      // first ask for a token from semaphore
      pmi.getLock();

      /* if (compressed) {
        inputStream = new BufferedInputStream((new GZIPInputStream(new FileInputStream(input)));
      } else {
        inputStream = 
          new BufferedInputStream(new FileInputStream(input));
      }*/
      String l = null;
      Variant m = new Variant();
      Coverage c = null;
      int count = 0;
      
      // now connect this to BioJava 
      FeatureList fl = GFF3Reader.read(input);

      for (FeatureI f : fl) {

        // display progress
        count++;
        if (count % 1000 == 0) { 
          System.out.print(count+"\r");
        }
        
        // now populate the variant object
        // type
        if ("SNV".equals(f.type())) { m.setType(m.SNV); }
        else if ("insertion".equals(f.type())) { m.setType(m.INSERTION); }
        else if ("deletion".equals(f.type())) { m.setType(m.DELETION); }
        else { m.setType(m.UNKNOWN_TYPE); }
        m.addTag(f.type(), null);
        // coord
        m.setContig(f.seqname());
        m.setStartPosition(f.location().getBegin());
        m.setStopPosition(f.location().getEnd());
        // FIXME
        m.setConsensusCallQuality(0);
        // dbSNP
        if (f.hasAttribute("isDbSNP")) { m.addTag("is_dbSNP", f.getAttribute("isDbSNP")); m.addTag(f.getAttribute("isDbSNP"), null); }
        else { m.addTag("not_dbSNP", null); }
        // zygosity
        if ("heterozygous".equals(f.getAttribute("zygosity"))) { m.setZygosity(m.HETEROZYGOUS); }
        else if ("homozygous".equals(f.getAttribute("zygosity"))) { m.setZygosity(m.HOMOZYGOUS); }
        else { m.setZygosity(m.UNKNOWN_ZYGOSITY); }
        // variant
        String variant = f.getAttribute("variant");
        String[] varArray = variant.split("->");
        m.setReferenceBase(varArray[0]);
        m.setCalledBase(varArray[1]);
        m.setConsensusBase(varArray[1]);
        // relative location called by Annovar
        if (f.hasAttribute("location")) {
          for (String loc : f.getAttribute("location").split(",")) {
            m.addTag("location", f.getAttribute("location")); m.addTag(loc, null);
          }
        }
        // gene name
        if (f.hasAttribute("gene")) { m.addTag("gene", f.getAttribute("gene")); m.addTag(f.getAttribute("gene"), null); }
        // go
        if (f.hasAttribute("go")) {
          String go = f.getAttribute("go");
          m.addTag("GO_terms", go);
          String[] goArray = go.split(",");
          for (String termPair : goArray) {
            String[] termArray = termPair.split("|");
            String accession = termArray[0];
            m.addTag(accession, null);
            String desc = termArray[1];
            // may have to leave this out but for now adding all terms from GO descriptors as tags
            for (String word : desc.split(" ")) {
              m.addTag(word, null);
            }
          }
        }
        // kegg
        if (f.hasAttribute("kegg")) {
          m.addTag("kegg_pathways", f.getAttribute("kegg"));
          for (String keggId : f.getAttribute("kegg").split(",")) {
            m.addTag(keggId, null);
          }
        }
        // omim
        if (f.hasAttribute("omim")) { m.addTag("omim_id", f.getAttribute("omim")); m.addTag(f.getAttribute("omim"), null); }
        // consequence
        if (f.hasAttribute("consequence")) { m.addTag(f.getAttribute("consequence"), null); m.addTag("consequence", f.getAttribute("consequence")); }
        // severity
        // this is calculated here based on a few factors
        int severity = 0;
        if (m.getZygosity() == m.HOMOZYGOUS) { severity++; }
        if (m.getType() == m.INSERTION || m.getType() == m.DELETION) { severity++; }
        if (m.getTagValue("is_dbSNP") == null) { severity++; }
        if ("exonic".equals(m.getTagValue("location"))) { severity++; }
        if ("exonic,splicing".equals(m.getTagValue("location"))) { severity++; }
        if (!"intergenic".equals(m.getTagValue("location"))) { severity++; }
        if (f.hasAttribute("omim")) { severity++; }
        if (f.hasAttribute("consequence") && "nonsynonymous".equals(f.getAttribute("consequence"))) { severity++; }
        if (f.hasAttribute("consequence") && "frameshift".equals(f.getAttribute("consequence"))) { severity+=2; }
        m.addTag("priority", (new Integer(severity)).toString());
        // look at navigenics, decode me, 23andme
        // save in DB
        store.putMismatch(m);
        m = new Variant();

      }
      System.out.print("\n");
    } 
    catch (Exception e) {
      System.out.println("Exception! "+e.getMessage());
      e.printStackTrace();
    } finally {
      pmi.releaseLock();
    }
  }
  
  private String comp(String nucleotide) throws Exception {
    String start = nucleotide;
    start = start.toUpperCase();
    if ("A".equals(start)) { return "T"; }
    else if ("T".equals(start)) { return "A"; }
    else if ("C".equals(start)) { return "G"; }
    else if ("G".equals(start)) { return "C"; }
    else { throw new Exception("unknown nucleo type "+start); }
  }
}
