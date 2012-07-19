package com.github.seqware.queryengine.system.importers.workers;

import com.github.seqware.queryengine.factory.Factory;
import com.github.seqware.queryengine.factory.ModelManager;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Tag;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.biojava3.genome.parsers.gff.FeatureI;
import org.biojava3.genome.parsers.gff.FeatureList;
import org.biojava3.genome.parsers.gff.GFF3Reader;

//import net.sourceforge.seqware.queryengine.backend.model.Coverage;
//import net.sourceforge.seqware.queryengine.backend.model.Variant;
/**
 * Ported GFF3VariantImportWorker now using our Hibernate-like entry. Hopefully
 * this doesn't slow things down too much. Not tested yet. All following
 * documentation refers to the previous prototype and has not been verified on
 * the current one. It seems that GVF is backwards compatible with GFF3 parsers
 * so let's hope this works.
 * 
 * This does not support multi-individual files yet.
 * 
 * The other issue is that the old converter only picked up specific tags. Other datasets like the 
 * 10Gen Data set seem to use pretty different tags. Maybe we should automatically load tags on the fly?
 *
 * @author boconnor
 * @author dyuen
 *
 * A simple worker thread to parse GFF3 files as defined at:
 * http://gmod.org/wiki/GFF3
 *
 * This is a relatively fluid file format since much of the information is
 * encoded in the 9th column using key/value pairs.
 *
 * For the purposes of this SeqWare Query Engine variant import worker, the
 * following key/values are recognized and handled specially: isDbSNP, location,
 * zygosity, kegg, omim, go, gene, consequence, variant
 *
 * All other key/values are just treated like key values in the SeqWare database
 * and are saved without modification.
 *
 * Generally, for the GFF file format the 9 columns are as follows (copied from
 * http://gmod.org/wiki/GFF3):
 *
 * Column 1: "seqid"
 *
 * The ID of the landmark used to establish the coordinate system for the
 * current feature. IDs may contain any characters, but must escape any
 * characters not in the set [a-zA-Z0-9.:^*$@!+_?-|]. In particular, IDs may not
 * contain unescaped whitespace and must not begin with an unescaped ">". * To
 * escape a character in this, or any of the other GFF3 fields, replace it with
 * the percent sign followed by its hexadecimal representation. For example, ">"
 * becomes "%E3". See URL Encoding (or: 'What are those "%20" codes in URLs?')
 * for details. * Column 2: "source"
 *
 * The source is a free text qualifier intended to describe the algorithm or
 * operating procedure that generated this feature. Typically this is the name
 * of a piece of software, such as "Genescan" or a database name, such as
 * "Genbank." In effect, the source is used to extend the feature ontology by
 * adding a qualifier to the type creating a new composite type that is a
 * subclass of the type in the type column. It is not necessary to specify a
 * source. If there is no source, put a "." (a period) in this field. * Column
 * 3: "type"
 *
 * The type of the feature (previously called the "method"). This is constrained
 * to be either: (a) a term from the "lite" sequence ontology, SOFA; or (b) a
 * SOFA accession number. The latter alternative is distinguished using the
 * syntax SO:000000. This field is required. * Columns 4 & 5: "start" and "end"
 *
 * The start and end of the feature, in 1-based integer coordinates, relative to
 * the landmark given in column 1. Start is always less than or equal to end. *
 * For zero-length features, such as insertion sites, start equals end and the
 * implied site is to the right of the indicated base in the direction of the
 * landmark. These fields are required. * Column 6: "score"
 *
 * The score of the feature, a floating point number. As in earlier versions of
 * the format, the semantics of the score are ill-defined. It is strongly
 * recommended that E-values be used for sequence similarity features, and that
 * P-values be used for ab initio gene prediction features. If there is no
 * score, put a "." (a period) in this field. * Column 7: "strand"
 *
 * The strand of the feature. + for positive strand (relative to the landmark),
 * - for minus strand, and . for features that are not stranded. In addition, ?
 * can be used for features whose strandedness is relevant, but unknown. *
 * Column 8: "phase"
 *
 * For features of type "CDS", the phase indicates where the feature begins with
 * reference to the reading frame. The phase is one of the integers 0, 1, or 2,
 * indicating the number of bases that should be removed from the beginning of
 * this feature to reach the first base of the next codon. In other words, a
 * phase of "0" indicates that the next codon begins at the first base of the
 * region described by the current line, a phase of "1" indicates that the next
 * codon begins at the second base of this region, and a phase of "2" indicates
 * that the codon begins at the third base of this region. This is NOT to be
 * confused with the frame, which is simply start modulo 3. If there is no
 * phase, put a "." (a period) in this field. * For forward strand features,
 * phase is counted from the start field. For reverse strand features, phase is
 * counted from the end field. * The phase is required for all CDS features. *
 * Column 9: "attributes"
 *
 * A list of feature attributes in the format tag=value. Multiple tag=value
 * pairs are separated by semicolons. URL escaping rules are used for tags or
 * values containing the following characters: ",=;". Spaces are allowed in this
 * field, but tabs must be replaced with the %09 URL escape. This field is not
 * required. * Column 9 Tags
 *
 * Column 9 tags have predefined meanings:
 *
 * ID Indicates the unique identifier of the feature. IDs must be unique within
 * the scope of the GFF file. * Name Display name for the feature. This is the
 * name to be displayed to the user. Unlike IDs, there is no requirement that
 * the Name be unique within the file. * Alias A secondary name for the feature.
 * It is suggested that this tag be used whenever a secondary identifier for the
 * feature is needed, such as locus names and accession numbers. Unlike ID,
 * there is no requirement that Alias be unique within the file. * Parent
 * Indicates the parent of the feature. A parent ID can be used to group exons
 * into transcripts, transcripts into genes, and so forth. A feature may have
 * multiple parents. Parent can *only* be used to indicate a partof
 * relationship. * Target Indicates the target of a nucleotide-to-nucleotide or
 * protein-to-nucleotide alignment. The format of the value is "target_id start
 * end [strand]", where strand is optional and may be "+" or "-". If the
 * target_id contains spaces, they must be escaped as hex escape %20. * Gap The
 * alignment of the feature to the target if the two are not collinear (e.g.
 * contain gaps). The alignment format is taken from the CIGAR format described
 * in the Exonerate documentation.
 * http://cvsweb.sanger.ac.uk/cgi-bin/cvsweb.cgi/exonerate?cvsroot=Ensembl). See
 * the GFF3 specification for more information. * Derives_from Used to
 * disambiguate the relationship between one feature and another when the
 * relationship is a temporal one rather than a purely structural "part of" one.
 * This is needed for polycistronic genes. See the GFF3 specification for more
 * information. * Note A free text note. * Dbxref A database cross reference.
 * See the GFF3 specification for more information. * Ontology_term A cross
 * reference to an ontology term. See the GFF3 specification for more
 * information. * Multiple attributes of the same type are indicated by
 * separating the values with the comma "," character, as in:
 *
 * Parent=AF2312,AB2812,abc-3
 *
 * Note that attribute names are case sensitive. "Parent" is not the same as
 * "parent".
 *
 * All attributes that begin with an uppercase letter are reserved for later
 * use. Attributes that begin with a lowercase letter can be used freely by
 * applications. You can stash any semi-structured data into the database by
 * using one or more unreserved (lowercase) tags.
 *
 *
 */
public class GFF3VariantImportWorker extends ImportWorker {

    public GFF3VariantImportWorker() {
    }

    @Override
    public void run() {

        ModelManager mManager = Factory.getModelManager();
        try {
            // first ask for a token from semaphore
            pmi.getLock();

            /*
             * if (compressed) { inputStream = new BufferedInputStream((new
             * GZIPInputStream(new FileInputStream(input))); } else {
             * inputStream = new BufferedInputStream(new
             * FileInputStream(input)); }
             */
            String l = null;
            //Variant m = new Variant();
            //Coverage c = null;
            Feature.Builder fBuilder = mManager.buildFeature();
            // FeatureSets are totally new, hope this doesn't slow things too much
            FeatureSet fSet = mManager.buildFeatureSet().setDescription("from file: " + input).setReferenceID(referenceID).build();

            int count = 0;

            // now connect this to BioJava 
            FeatureList fl = GFF3Reader.read(input);

            for (FeatureI f : fl) {
                // implementation of FeatureI is always Feature in GFF3Reader and it has more info
                org.biojava3.genome.parsers.gff.Feature fI = (org.biojava3.genome.parsers.gff.Feature) f;
                Set<Tag> tagSet = new HashSet<Tag>();
                // display progress
                count++;
                if (count % 10000 == 0) {
                    System.out.print(count + "\r");
                }

                // now populate the variant object
                // type
                boolean insertionOrDeletion = false;
                if ("SNV".equals(f.type())) {
                    fBuilder.setType(ImportConstants.GFF3_SNV);
                    //m.setType(m.SNV);
                } else if ("insertion".equals(f.type())) {
                    fBuilder.setType(ImportConstants.GFF3_INSERTION);
                    insertionOrDeletion = true;
                    //m.setType(m.GFF3_INSERTION);
                } else if ("deletion".equals(f.type())) {
                    fBuilder.setType(ImportConstants.GFF3_DELETION);
                    insertionOrDeletion = true;
                    //m.setType(m.GFF3_DELETION);
                } else {
                    fBuilder.setType(ImportConstants.GFF3_UNKNOWN_TYPE);
                    //m.setType(m.GFF3_UNKNOWN_TYPE);
                }
                tagSet.add(Tag.newBuilder().setKey(f.type()).build());
                //m.addTag(f.type(), null);
                // coord
                fBuilder.setId(f.seqname());
                // m.setContig(f.seqname());
                fBuilder.setStart(f.location().getBegin());
                fBuilder.setStop(f.location().getEnd());
                //m.setStartPosition(f.location().getBegin());
                //m.setStopPosition(f.location().getEnd());
                // FIXME
                fBuilder.setScore(fI.score());
                //m.setConsensusCallQuality(0);
                // dbSNP
                if (f.hasAttribute("isDbSNP")) {
                    tagSet.add(Tag.newBuilder().setKey("is_dbSNP").setValue("isDbSNP").build());
                    //m.addTag("is_dbSNP", f.getAttribute("isDbSNP"));
                    tagSet.add(Tag.newBuilder().setKey(f.getAttribute("isDbSNP")).build());
                    //m.addTag(f.getAttribute("isDbSNP"), null);
                } else {
                    tagSet.add(Tag.newBuilder().setKey("not_dbSNP").build());
                    //m.addTag("not_dbSNP", null);
                }
                // zygosity
                String zygosity;
                if (ImportConstants.VCF_HETEROZYGOUS.equals(f.getAttribute("zygosity"))) {
                    zygosity = ImportConstants.VCF_HETEROZYGOUS;
                    tagSet.add(Tag.newBuilder().setKey(zygosity).build());
//                    m.setZygosity(m.VCF_HETEROZYGOUS);
                } else if (ImportConstants.VCF_HOMOZYGOUS.equals(f.getAttribute("zygosity"))) {
                    zygosity = ImportConstants.VCF_HOMOZYGOUS;
                    tagSet.add(Tag.newBuilder().setKey(zygosity).build());
//                    m.setZygosity(m.VCF_HOMOZYGOUS);
                } else {
                    zygosity = ImportConstants.GFF3_UNKNOWN_TYPE;
                    tagSet.add(Tag.newBuilder().setKey(zygosity).build());
//                    m.setZygosity(m.UNKNOWN_ZYGOSITY);
                }
                // GVF compatbility has been added by the following bunch of additions
                // As tested on the 10Gen DataSet
                // According to the doc, they should be backwards compatible
                /** GVF additions start */
                if(f.hasAttribute(ImportConstants.GVF_ALIAS)){
                    tagSet.add(Tag.newBuilder().setKey(ImportConstants.GVF_DBXREF).setValue(f.getAttribute(ImportConstants.GVF_DBXREF)).build());
                }
                for(String attr : ImportConstants.UNPROCESSED_ATTRIBUTES){
                    if (f.hasAttribute(attr)){
                        tagSet.add(Tag.newBuilder().setKey(attr).setValue(f.getAttribute(attr)).build());
                    }
                }
                if(f.hasAttribute(ImportConstants.GVF_ZYGOSITY)){
                    if (f.getAttribute(ImportConstants.GVF_ZYGOSITY).equals(ImportConstants.GVF_HETEROZYGOUS)){
                        zygosity = ImportConstants.GVF_HETEROZYGOUS;
                        tagSet.add(Tag.newBuilder().setKey(zygosity).build());
                    } else if (f.getAttribute(ImportConstants.GVF_ZYGOSITY).equals(ImportConstants.GVF_HOMOZYGOUS)){
                        zygosity = ImportConstants.GVF_HOMOZYGOUS;
                        tagSet.add(Tag.newBuilder().setKey(zygosity).build());
                    } else if(f.getAttribute(ImportConstants.GVF_ZYGOSITY).equals(ImportConstants.GVF_HEMIZYGOUS)){
                        zygosity = ImportConstants.GVF_HEMIZYGOUS;
                        tagSet.add(Tag.newBuilder().setKey(zygosity).build());
                    }
                }
                /** GVF additions end */
                
                // variant
                // this is newly added during the port. It doesn't look the 10Gen dataset files have a "variant" attribute
                if (f.hasAttribute("variant")) {
                    String variant = f.getAttribute("variant");
                    String[] varArray = variant.split("->");
                    tagSet.add(Tag.newBuilder().setKey(ImportConstants.VCF_REFERENCE_BASE).setValue(varArray[0]).build());
                    tagSet.add(Tag.newBuilder().setKey(ImportConstants.VCF_CONSENSUS_BASE).setValue(varArray[1]).build());
                    tagSet.add(Tag.newBuilder().setKey(ImportConstants.VCF_CALLED_BASE).setValue(varArray[1]).build());
//                m.setReferenceBase(varArray[0]);
//                m.setCalledBase(varArray[1]);
//                m.setConsensusBase(varArray[1]);
                    // relative location called by Annovar
                    // TODO: this is from the old prototype, but would this not just overwrite the same key many times?
                }

                Tag location = null;
                if (f.hasAttribute("location")) {
                    for (String loc : f.getAttribute("location").split(",")) {
                        location = Tag.newBuilder().setKey("location").setValue(f.getAttribute("location")).build();
                        tagSet.add(location);
//                        m.addTag("location", f.getAttribute("location"));
                        tagSet.add(Tag.newBuilder().setKey(loc).build());
//                        m.addTag(loc, null);
                    }
                }
                // gene name
                if (f.hasAttribute("gene")) {
                    tagSet.add(Tag.newBuilder().setKey("gene").setValue(f.getAttribute("gene")).build());
                    tagSet.add(Tag.newBuilder().setKey(f.getAttribute("gene")).build());
//                    m.addTag("gene", f.getAttribute("gene"));
//                    m.addTag(f.getAttribute("gene"), null);
                }
                // go
                if (f.hasAttribute("go")) {
                    String go = f.getAttribute("go");
                    tagSet.add(Tag.newBuilder().setKey("GO_terms").setValue(go).build());
//                    m.addTag("GO_terms", go);
                    String[] goArray = go.split(",");
                    for (String termPair : goArray) {
                        String[] termArray = termPair.split("|");
                        String accession = termArray[0];
                        tagSet.add(Tag.newBuilder().setKey(accession).build());
//                        m.addTag(accession, null);
                        String desc = termArray[1];
                        // may have to leave this out but for now adding all terms from GO descriptors as tags
                        for (String word : desc.split(" ")) {
                            tagSet.add(Tag.newBuilder().setKey(word).build());
//                            m.addTag(word, null);
                        }
                    }
                }
                // kegg
                if (f.hasAttribute("kegg")) {
                    tagSet.add(Tag.newBuilder().setKey("kegg_pathways").setValue("kegg").build());
                    //m.addTag("kegg_pathways", f.getAttribute("kegg"));
                    for (String keggId : f.getAttribute("kegg").split(",")) {
                        tagSet.add(Tag.newBuilder().setKey(keggId).build());
                        //m.addTag(keggId, null);
                    }
                }
                // omim
                if (f.hasAttribute("omim")) {
                    //m.addTag("omim_id", f.getAttribute("omim"));
                    tagSet.add(Tag.newBuilder().setKey("omim_id").setValue(f.getAttribute("omim")).build());
                    //m.addTag(f.getAttribute("omim"), null);
                    tagSet.add(Tag.newBuilder().setKey(f.getAttribute("omim")).build());
                }
                // consequence
                if (f.hasAttribute("consequence")) {
                    tagSet.add(Tag.newBuilder().setKey("consequence").setValue(f.getAttribute("consequence")).build());
                    //m.addTag(f.getAttribute("consequence"), null);
                    //m.addTag("consequence", f.getAttribute("consequence"));
                    tagSet.add(Tag.newBuilder().setKey(f.getAttribute("consequence")).build());
                }
                // severity
                // this is calculated here based on a few factors
                int severity = 0;
                if (zygosity.equals(ImportConstants.VCF_HOMOZYGOUS) || zygosity.equals(ImportConstants.GVF_HOMOZYGOUS)) {
                    severity++;
                }
                if (insertionOrDeletion) {
                    severity++;
                }
                if (!f.hasAttribute("isDbSNP")) {
                    severity++;
                }
//                if (m.getZygosity() == m.VCF_HOMOZYGOUS) {
//                    severity++;
//                }
//                if (m.getType() == m.GFF3_INSERTION || m.getType() == m.GFF3_DELETION) {
//                    severity++;
//                }
//                if (m.getTagByKey("is_dbSNP") == null) {
//                    severity++;
//                }
                if (location != null && "exonic".equals(location.getTagByKey("location"))) {
                    severity++;
                }
                if (location != null && "exonic,splicing".equals(location.getTagByKey("location"))) {
                    severity++;
                }
                if (location != null && !"intergenic".equals(location.getTagByKey("location"))) {
                    severity++;
                }
//                if ("exonic".equals(m.getTagByKey("location"))) {
//                    severity++;
//                }
//                if ("exonic,splicing".equals(m.getTagByKey("location"))) {
//                    severity++;
//                }
//                if (!"intergenic".equals(m.getTagByKey("location"))) {
//                    severity++;
//                }
                if (f.hasAttribute("omim")) {
                    severity++;
                }
                if (f.hasAttribute("consequence") && "nonsynonymous".equals(f.getAttribute("consequence"))) {
                    severity++;
                }
                if (f.hasAttribute("consequence") && "frameshift".equals(f.getAttribute("consequence"))) {
                    severity += 2;
                }
                tagSet.add(Tag.newBuilder().setKey("priority").setValue(severity).build());
                //m.addTag("priority", severity);
                //m.addTag("priority", (new Integer(severity)).toString());
                // look at navigenics, decode me, 23andme
                // save in DB
                Feature build = fBuilder.build();
                for (Tag tag : tagSet) {
                    build.associateTag(tag);
                }
                //store.putMismatch(m);
                // this is new, add it to a featureSet
                fSet.add(build);

                //store.putMismatch(m);
                fBuilder = mManager.buildFeature();
                //m = new Variant();
            }
            System.out.print("\n");
        } catch (Exception e) {
            Logger.getLogger(GFF3VariantImportWorker.class.getName()).log(Level.SEVERE, "Exception thrown with file: " + input + "\n", e);
            System.out.println("Exception with file: " + input + "\n" + e.getMessage());
            //e.printStackTrace();
        } finally {
            // new, this is needed to have the model manager write results to the DB in one big batch
            System.out.println("Closing in thread: " + Thread.currentThread().getName());
            mManager.close();
            pmi.releaseLock();
            System.out.println("Releasing lock in thread: " + Thread.currentThread().getName());
        }
    }

    private String comp(String nucleotide) throws Exception {
        String start = nucleotide;
        start = start.toUpperCase();
        if ("A".equals(start)) {
            return "T";
        } else if ("T".equals(start)) {
            return "A";
        } else if ("C".equals(start)) {
            return "G";
        } else if ("G".equals(start)) {
            return "C";
        } else {
            throw new Exception("unknown nucleo type " + start);
        }
    }
}
