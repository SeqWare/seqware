package com.github.seqware.queryengine.system.importers.workers;

import com.github.seqware.queryengine.dto.QESupporting.FSGIDPB;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.protobufIO.FSGIDIO;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.system.importers.FeatureImporter;
import com.github.seqware.queryengine.system.importers.SOFeatureImporter;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.SGID;
import com.google.protobuf.TextFormat;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.log4j.Logger;

//import net.sourceforge.seqware.queryengine.backend.model.Coverage;
//import net.sourceforge.seqware.queryengine.backend.model.Variant;

/**
 * Ported VCFVariantImportWorker now using our Hibernate-like entry. Hopefully
 * this doesn't slow things down too much. Not tested yet.
 * 
 * @author boconnor
 * @author dyuen
 *
 * TODO: Variant is now our Feature. Coverage does not seem to be in use anyways
 *
 * FIXME: need to support indels FIXME: need to support alternative alleles,
 * each should get its own variant object I think
 *
 */
public class VCFVariantImportWorker extends ImportWorker {
    public static final String SECONDARY_INDEX = "sIndex.out";
    private CreateUpdateManager modelManager;
    private List<TagSet> potentialTagSets = new ArrayList<TagSet>();
    private TagSet adHocSet;
    private Map<String, Tag> localCache = new HashMap<String, Tag>();
    
    private PrintWriter out = null;
    

    public VCFVariantImportWorker() {
        if (SOFeatureImporter.SECONDARY_INDEX_HACK){
            try {
                out = new PrintWriter(new BufferedWriter(new FileWriter(SECONDARY_INDEX, true)));
            } catch (IOException ex) {
               Logger.getLogger(VCFVariantImportWorker.class.getName()).fatal(null, ex);
               System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
            }
        }
    }
    
    /**
     * Given a key, locate a potential match inside a list of tag sets, otherwise 
     * create a new one in the ad hoc set.
     * 
     * Looking through full list each time is slow, using a local cache
     * 
     * @param manager
     * @param potentialTagSets
     * @param adhocSet
     * @param key
     * @return 
     */
    public Tag getTagSpec(String key){
        Logger.getLogger(VCFVariantImportWorker.class.getName()).trace("getTagSpec() called on " + key);
        // check local cache first
        if (this.localCache.containsKey(key)){
            Logger.getLogger(VCFVariantImportWorker.class.getName()).trace(key + " found in localCache ");
            return this.localCache.get(key);
        }
        
        for(TagSet set : this.potentialTagSets){
            if (set.containsKey(key)){
                Logger.getLogger(VCFVariantImportWorker.class.getName()).trace(key + " found in provided set " + set.getSGID().getRowKey());
                Tag tagByKey = set.get(key);
                this.localCache.put(key, tagByKey);
                return tagByKey;
            }
        }
        Logger.getLogger(VCFVariantImportWorker.class.getName()).trace(key + " not found in potential tag sets");
        if (adHocSet.containsKey(key)){
            Logger.getLogger(VCFVariantImportWorker.class.getName()).trace(key + " found in ad hoc tag set " + adHocSet.getSGID().getRowKey());
            Tag tagByKey = adHocSet.get(key);
            this.localCache.put(key, tagByKey);
            return tagByKey;
        } else{
            Logger.getLogger(VCFVariantImportWorker.class.getName()).trace(key + " added to ad hoc tag set");
            Tag build = modelManager.buildTag().setKey(key).build();
            adHocSet.add(build);
            this.localCache.put(key, build);
            return build;
        } 
    }

    @Override
    public void run() {
        // grab FeatureSet reference
        // FeatureSets are totally new, hope this doesn't slow things too much
        FeatureSet fSet = SWQEFactory.getQueryInterface().getAtomBySGID(FeatureSet.class, this.featureSetID);   
        this.modelManager = SWQEFactory.getModelManager();
        modelManager.persist(fSet);
        
        // process potential tag sets
        if (this.getTagSetIDs() != null){
            for(SGID tagSetID : this.getTagSetIDs()){
                this.potentialTagSets.add(SWQEFactory.getQueryInterface().getLatestAtomBySGID(tagSetID, TagSet.class));
            }
        }
        this.adHocSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(this.getAdhoctagset(), TagSet.class);   
        modelManager.persist(adHocSet);
        
        // open the file
        BufferedReader inputStream = null;
        try {

            // first ask for a token from semaphore
            pmi.getLock();

            // Attempting to guess the file format
            if (compressed) {
                inputStream = handleCompressedInput(input);
            } else {
                inputStream = new BufferedReader(new FileReader(input));
            }
            String l;
            //Variant m = new Variant();
            Feature.Builder fBuilder = modelManager.buildFeature();


//      Coverage c = null;
//            int currBin = 0;
            int count = 0;
//            String previousPos = null;
//            Pattern p = Pattern.compile("-([ATGCNatgcn]+)");

            while ((l = inputStream.readLine()) != null) {

                // display progress
                count++;
                // we need to flush and clear the manager in order to release the memory consumed by the Features themselves
                // the featureSet has to be reattached
                if (count % this.getBatch_size() == 0){
                    modelManager.flush();
                    modelManager.clear();
                    modelManager.persist(adHocSet);
                    modelManager.persist(fSet);
                }

                // ignore commented lines
                if (!l.startsWith("#")) {

                    // pileup string
                    String[] t = l.split("\t+");

                    // load the variant object

                    //m.setContig(t[0]);
                    fBuilder.setSeqid(t[0]);
// should not rename this actually
//                    if (!".".equals(t[0]) && !t[0].startsWith("chr")) {
//                        //m.setContig("chr" + t[0]);
//                        fBuilder.setSeqid("chr" + t[0]);
//                    }

                    // cache our tags till our message is built
                    Set<Tag> tagSet = new HashSet<Tag>();
                    //TODO: link this up with proper TagSets, these are ad hoc tags
                    //tagSet.add(Tag.newBuilder().setKey(t[0]).build());
                    tagSet.add(getTagSpec(t[0]));
                    //m.addTag(t[0], null);
                    // referenceBase, consensusBase, and calledBase can be ad hoc tags for now
                    tagSet.add(getTagSpec(ImportConstants.VCF_REFERENCE_BASE).toBuilder().setValue(t[3].toUpperCase()).build());
                    tagSet.add(getTagSpec(ImportConstants.VCF_CONSENSUS_BASE).toBuilder().setValue(t[4].toUpperCase()).build());
                    Tag calledTag = getTagSpec(ImportConstants.VCF_CALLED_BASE).toBuilder().setValue(t[4].toUpperCase()).build();
                    tagSet.add(calledTag);
                    //m.setReferenceBase(t[3].toUpperCase());
                    //m.setConsensusBase(t[4].toUpperCase());
                    //m.setCalledBase(t[4].toUpperCase());

                    // figure out the consensusCallQuality
                    if (!".".equals(t[5])) {
                        fBuilder.setScore(Double.parseDouble(t[5]));
                    }
                    //m.setConsensusCallQuality(Float.parseFloat(t[5]));

                    // parse ID
                    if (!".".equals(t[2])) {
                        tagSet.add(getTagSpec(ImportConstants.VCF_SECOND_ID).toBuilder().setValue(t[2]).build());
                        //m.addTag("ID", t[2]);
                    }
// This had to be removed when adding tag support, adding every ID field as a tag would effectviely blow out our ad hoc tag sets to the size of a FeatureSet which 
// is not currently feasible
//                    if (!".".equals(t[2])) {
//                        tagSet.add(getTagSpec(t[2]).toBuilder().build());
//                        //m.addTag(t[2], null);
//                    }

                    // FIXME: only supports two alleles for now, see http://users.ox.ac.uk/~linc1775/blueprint.htm
                    // if there are multiple alleles then both the consensus and called bases should be 
                    String calledBase = t[4].toUpperCase() /**
                             * m.getCalledBase()
                             */
                            ;
                    if (t[4].toUpperCase().length() > 1 && t[4].toUpperCase().contains(",")) {
                        tagSet.remove(calledTag);
                        if ("C,A".equals(calledBase) || "A,C".equals(calledBase)) {
                            calledBase = "M";
                        } else if ("A,G".equals(calledBase) || "G,A".equals(calledBase)) {
                            calledBase = "R";
                        } else if ("A,T".equals(calledBase) || "T,A".equals(calledBase)) {
                            calledBase = "W";
                        } else if ("C,G".equals(calledBase) || "G,C".equals(calledBase)) {
                            calledBase = "S";
                        } else if ("C,T".equals(calledBase) || "T,C".equals(calledBase)) {
                            calledBase = "Y";
                        } else if ("G,T".equals(calledBase) || "T,G".equals(calledBase)) {
                            calledBase = "K";
                        } else {
                            // this doesn't work when consensus base comes back like:
                            // TGCACGTCA,TAA 
                            //throw new Exception("Don't know what "+m.getReferenceBase()+"->"+m.getConsensusBase()+" is!!!");
                        }
                        calledTag = getTagSpec(ImportConstants.VCF_CALLED_BASE).toBuilder().setValue(calledBase).build();
                        tagSet.add(calledTag);
                        //m.setCalledBase(calledBase);
                        // leave the consensus base as the original call syntax from the VCF file
                    }

                    /*
                     * I've seen another alternative way of representing alleles
                     * where the FQ and AF1 can be used to caall homozygous From
                     * http://seqanswers.com/forums/showthread.php?t=11651 In
                     * English, the first line means "check the FQ"; the FQ is
                     * negative when the SNP is homozygous, and positive when
                     * it's mixed, and the bigger the absolute value, the more
                     * confident the SNP.
                     *
                     * So if it's < 0, it does the first part of code (it checks
                     * against the AF1, and if the AF1 is > 0.5, which it should
                     * be for a homozygous SNP, it sets $b as the alternate
                     * letter, if for some reason the AF1 is < .5, it sets $b as
                     * the old reference letter.)
                     *
                     * If the FQ is positive, then the SNP should be mixed, and
                     * it concatenates the two letters, and checks the hash
                     * above to know what single letter to set $b to. Then it
                     * adds $b to the growing sequence.
                     *
                     * $q, which is derived from the FQ, ends up being the
                     * quality score, though it gets tweaked a little; it adds
                     * 33.449 to the figure, then converts it to a letter,
                     * capping it at a quality of 126.
                     *
                     * $q = int($q + 33 + .499); $q = chr($q <= 126? $q : 126);
                     *
                     * Gaps are handled as they were in the old program, where
                     * they are NOT added in, there is just a window of
                     * lowercase letters around them. Personally, I made a
                     * little perl script, and I feed it the genome, and a
                     * conservatively filtered list of SNPs, and I put the
                     * changes in that way.
                     */

                    // FIXME: hard-coded for now
                    fBuilder.setType(ImportConstants.VCF_SNV);
                    //m.setType(Variant.SNV);
                    // always save a tag
                    tagSet.add(getTagSpec(ImportConstants.VCF_SNV).toBuilder().build());
                    //m.addTag("SNV", null);
                    if (!".".equals(t[1])) {
                        Integer pos = Integer.parseInt(t[1]);
                        fBuilder.setStart(pos - 1);
                        fBuilder.setStop(pos);
                    }
                    //m.setStartPosition(pos - 1);
                    //m.setStopPosition(pos);

                    // now parse field 6
                    tagSet.add(getTagSpec(ImportConstants.VCF_FILTER).toBuilder().setValue(t[6]).build());
                    //m.addTag(t[6], null);
                    // added to prototype, record the info field
                    tagSet.add(getTagSpec(ImportConstants.VCF_INFO).toBuilder().setValue(t[7]).build());

                    // if FQ is < 0 and AF1 < 0.5 then the algorithm is calling homozygous reference so skip
                    boolean af1LtHalf = false;
                    boolean fqLt0 = false;

                    String[] tags = t[7].split(";");
                    for (String tag : tags) {
                        if (tag.contains("=")) {
                            String[] kv = tag.split("=");
                            tagSet.add(getTagSpec(kv[0]).toBuilder().setValue(kv[1]).build());
                            //m.addTag(kv[0], kv[1]);
                            if ("DP".equals(kv[0])) {
                                tagSet.add(getTagSpec(ImportConstants.VCF_READ_COUNTS).toBuilder().setValue(kv[1]).build());
                                //m.setReadCount(Integer.parseInt(kv[1]));
                            }
                            // see above
                            if ("FQ".equals(kv[0])) {
                                float fq = Float.parseFloat(kv[1]);
                                if (fq < 0) {
                                    tagSet.add(getTagSpec(ImportConstants.VCF_HOMOZYGOUS).toBuilder().build());
                                    //m.setZygosity(m.VCF_HOMOZYGOUS);
                                    //m.getTags().put("homozygous", null);
                                    fqLt0 = true;
                                } else {
                                    tagSet.add(getTagSpec(ImportConstants.VCF_HETEROZYGOUS).toBuilder().build());
                                    //m.setZygosity(m.VCF_HETEROZYGOUS);
                                    //m.getTags().put("heterozygous", null);
                                }
                            }
                            if ("AF1".equals(kv[0])) {
                                float af1 = Float.parseFloat(kv[1]);
                                if (af1 < 0.5) {
                                    af1LtHalf = true;
                                }
                            }
                        } else {
                            // this is dangerous because it could add an arbitrary number of additional Tags,
                            // but we need it for testing dbSNP
                            tagSet.add(getTagSpec(tag).toBuilder().build());
                            //m.addTag(tag, null);
                        }
                    }

                    // yet another way to encode hom/het
                    // FIXME: this doesn't conform to the standard
                    if (t.length > 9 && t[8].contains("GT") && t[9].contains("het")) {
                        tagSet.add(getTagSpec(ImportConstants.VCF_HETEROZYGOUS).toBuilder().build());
                        //m.setZygosity(m.VCF_HETEROZYGOUS);
                        //m.getTags().put("heterozygous", null);
                    } else if (t.length > 9 && t[8].contains("GT") && t[9].contains("hom")) {
                        tagSet.add(getTagSpec(ImportConstants.VCF_HOMOZYGOUS).toBuilder().build());
                        //m.setZygosity(m.VCF_HOMOZYGOUS);
                        //m.getTags().put("homozygous", null);
                    }

                    // if this is true then it's just being called homozygous reference so don't even store
                    if (af1LtHalf && fqLt0) {
                        System.out.println("Dropping variant because FQ < 0 and AF1 < 0.5!");
                    } else {
                        // our equivalent of store is just making the model manager aware of this by building it
                        Feature build = fBuilder.build();
                        for (Tag tag : tagSet) {
                            build.associateTag(tag);
                        }
                        
                        //store.putMismatch(m);
                        // this is new, add it to a featureSet
                        fSet.add(build);
                        
                        // hack to spit out secondary index
                        if (SOFeatureImporter.SECONDARY_INDEX_HACK){
                            if (build.getTagByKey(keyIndex) != null){
                               FSGID fsgid = (FSGID) build.getSGID();
                               FSGIDPB m2pb = FSGIDIO.m2pb(fsgid);
                               String message = TextFormat.printToString(m2pb);
                               out.print(message);
                               out.println("!");
                            }
                        }

                        if (count % this.getBatch_size() == 0) {
                            System.out.println(new Date().toString() + workerName + " adding mismatch to db: "+build.getSeqid()+":"+build.getStart()+"-"+build.getStop()+" total records added: "+build.getSeqid()+" total lines so far: "+count);
                        }
                    }

//                    if (count % 10000 == 0) {
//                        System.out.println(workerName + ": adding mismatch to db: " + m.getContig() + ":" + m.getStartPosition() + "-" + m.getStopPosition()
//                                + " total records added: " + m.getSeqid() + " total lines so far: " + count);
//                    }

                    // now prepare for next mismatch
                    fBuilder = modelManager.buildFeature();
                    //m = new Variant();
                }
            }

            if (SOFeatureImporter.SECONDARY_INDEX_HACK){
                out.close();
            }
            // close file
            inputStream.close();
        } catch (Exception e) {
            Logger.getLogger(VCFVariantImportWorker.class.getName()).fatal( "Exception thrown with file: " + input, e);
            //e.printStackTrace();
            throw new RuntimeException("Error in VCRVariantImportWorker");
        } finally {
            // new, this is needed to have the model manager write results to the DB in one big batch
            modelManager.close();
            pmi.releaseLock();
        }
    }

    public static BufferedReader handleCompressedInput(String input) throws CompressorException, FileNotFoundException {
        BufferedReader inputStream;
        if (input.endsWith("bz2") || input.endsWith("bzip2")) {
            inputStream = new BufferedReader(new InputStreamReader(new CompressorStreamFactory().createCompressorInputStream("bzip2", new BufferedInputStream(new FileInputStream(input)))));
        } else if (input.endsWith("gz") || input.endsWith("gzip")) {
            inputStream = new BufferedReader(new InputStreamReader(new CompressorStreamFactory().createCompressorInputStream("gz", new BufferedInputStream(new FileInputStream(input)))));
        } else {
            throw new RuntimeException("Don't know how to interpret the filename extension for: " + input + " we support 'bz2', 'bzip2', 'gz', and 'gzip'");
        }
        return inputStream;
    }
}
