/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.exporters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.BufferedReader;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Consequence;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.util.iterators.CursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.LocatableSecondaryCursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.SecondaryCursorIterator;

/**
 * @author boconnor
 *
 * A very simple program that will iterate over the mismatches
 *  and display info about each one for testing.
 *
 * FIXME: this object should actually become a regression test not a tool, I should also provide a dumper util, though, that can dump based on chr, start, and stop.
 * 
 */
public class TextDumper {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        if (args.length < 3) {
            System.out.println("TextDumper <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <dbDir> <cacheSize> <locks> <outputFile>");
            System.exit(0);
        }

        String backendType = args[0];
        String genomeId = args[1];
        String referenceId = args[2];
        String dbDir = args[3];
        long cacheSize = Long.parseLong(args[4]);
        int locks = Integer.parseInt(args[5]);
        String outputFile = args[6];
        
        BerkeleyDBFactory factory = new BerkeleyDBFactory();
        Store store = null;
        
        try {
        	
        	if ("BerkeleyDB".equals(backendType)) {
                // settings
                SeqWareSettings settings = new SeqWareSettings();
                settings.setStoreType("berkeleydb-mismatch-store");
                settings.setFilePath(dbDir);
                settings.setCacheSize(cacheSize);
                settings.setCreateMismatchDB(false);
                settings.setCreateConsequenceAnnotationDB(false);
                settings.setCreateDbSNPAnnotationDB(false);
                settings.setCreateCoverageDB(false);
                settings.setReadOnly(false);
                settings.setMaxLockers(locks);
                settings.setMaxLockObjects(locks);
                settings.setMaxLocks(locks);
                // store object
                store = factory.getStore(settings);
              } else if ("HBase".equals(backendType)) {
                SeqWareSettings settings = new SeqWareSettings();
                settings.setStoreType("hbase-mismatch-store");
                settings.setGenomeId(genomeId);
                settings.setReferenceId(referenceId);
                store = new HBaseStore();
                store.setSettings(settings);
                store.setup(settings);
              }

            if (store != null) {
                       
                // open the file
                BufferedWriter outputStream = null;
                
                try {

                    outputStream = new BufferedWriter(new FileWriter(outputFile));
                    
                    SeqWareIterator cursor = store.getMismatches();
                    
                    int count = 0;
                    
                    outputStream.write("mismatch_id\tref_qual_call\tcons_qual_call\tmax_mapping_qual\tref_max_seq_qual\tref_ave_seq_qual\tcons_max_seq_qual\tcons_ave_seq_qual"+
                            "\tzygosity\tbase_count_forward\tbase_count_reverse\tbase_count\tcontig\tstart\tstop\tref_base\tcons_base\tcalled_base\tread_count"+
                            "\ttype\tread_bases\tbase_qual\tcall_one\tcall_two\treads_support_call_one\treads_support_call_two\treads_support_call_three\n");
                    
                    while (cursor.hasNext()) {
                        
                        count++;
                        if (count % 1000 == 0) { 
                            System.out.print(count+"\r");
                        }
                        
                        // the mismatch record
                        Variant m = (Variant)cursor.next();
                        
                        outputStream.write("Variant: "+m.getId()+"\t"
                                +m.getReferenceCallQuality()+"\t"
                                +m.getConsensusCallQuality()+"\t"
                                +m.getMaximumMappingQuality()+"\t"
                                +m.getReferenceMaxSeqQuality()+"\t"
                                +m.getReferenceAveSeqQuality()+"\t"
                                +m.getConsensusMaxSeqQuality()+"\t"
                                +m.getConsensusAveSeqQuality()+"\t"
                                +m.getZygosity()+"\t"
                                +m.getCalledBaseCountForward()+"\t"
                                +m.getCalledBaseCountReverse()+"\t"
                                +m.getCalledBaseCount()+"\t"
                                +m.getContig()+"\t"
                                +m.getStartPosition()+"\t"
                                +m.getStopPosition()+"\t"
                                +m.getReferenceBase()+"\t"
                                +m.getConsensusBase()+"\t"
                                +m.getCalledBase()+"\t"
                                +m.getReadCount()+"\t"
                                +m.getType()+"\t"
                                +m.getReadBases()+"\t"
                                +m.getBaseQualities()+"\t"
                                +m.getCallOne()+"\t"
                                +m.getCallTwo()+"\t"
                                +m.getReadsSupportingCallOne()+"\t"
                                +m.getReadsSupportingCallTwo()+"\t"
                                +m.getReadsSupportingCallThree()+"\n"
                        );
                        
                        // pull back tags (which includes dbSNP entries)
                        HashMap<String,String> tags = m.getTags();
                        Iterator it = tags.keySet().iterator();
                        outputStream.write("\ttags:\n");
                        while(it.hasNext()) {
                            String tag = (String)it.next();
                            outputStream.write("\t\t"+tag);
                            String value = tags.get(tag);
                            if (value != null) {
                              outputStream.write(":"+value);
                            }
                            outputStream.write("\n");
                        }           
                    }
                    cursor.close();
                    System.out.print("\n");
                    
                    // FIXME: hardcoded for my test examples
                    // Now print out consequence reports by tag
                    /*SecondaryCursorIterator cci = store.getConsequencesByTag("early-termination");
                    outputStream.write("\nTest Consequence Retrieval via Tag: early-termination\n");
                    
                    while(cci.hasNext()) {
                        Consequence c = (Consequence)cci.next();
                        outputStream.write("Consequence: "+c.getId()+"\t"+c.getMismatchId()+"\n");
                        HashMap<String,String> tags = c.getTags();
                        Iterator it = tags.keySet().iterator();
                        outputStream.write("\ttags:\n");
                        while(it.hasNext()) {
                            String tag = (String)it.next();
                            outputStream.write("\t\t"+tag);
                            String value = tags.get(tag);
                            if (value != null) {
                              outputStream.write(":"+value);
                            }
                            outputStream.write("\n");
                        }
                    }
                    cci.close();
                    System.out.print("\n");
                    
                    // Now test getting a consequence report by mismatchId
                    cci = store.getConsequencesByMismatch(9);
                    outputStream.write("\nTest Consequence Retrieval via IntegerId: 9\n");
                    while(cci.hasNext()) {
                      Consequence c = (Consequence)cci.next();
                      outputStream.write("Consequence: "+c.getId()+"\t"+c.getMismatchId()+"\n");
                      HashMap<String,String> tags = c.getTags();
                      Iterator it = tags.keySet().iterator();
                      outputStream.write("\ttags:\n");
                      while(it.hasNext()) {
                          String tag = (String)it.next();
                          outputStream.write("\t\t"+tag);
                          String value = tags.get(tag);
                          if (value != null) {
                            outputStream.write(":"+value);
                          }
                          outputStream.write("\n");
                      }
                    }
                    cci.close();
                    System.out.print("\n");
                    
                    // Now test coverage
                    outputStream.write("\nTest coverage information\n");
                    
                    LocatableSecondaryCursorIterator coverageIt = store.getCoverages("chr22", 14432000, 14439999);
                    while(coverageIt.hasNext()) {
                    	Coverage c = (Coverage)coverageIt.next();
                    	if (c == null) {
                    		outputStream.write("C is null");
                    	} else {
                    		outputStream.write("Coverage: "+c.getCount()+" start: "+c.getStartPosition()+" stop: "+c.getStopPosition()+"\n");
                    		HashMap<Integer, Integer> coverages = c.getCoverage();
                    		Iterator<Integer> it = coverages.keySet().iterator();
                    		outputStream.write("\t");
                    		while(it.hasNext()) {
                    			Integer cov = it.next();
                    			outputStream.write(cov+":"+coverages.get(cov)+",");
                    		}
                    		outputStream.write("\n");
                    	}
                    }
                    coverageIt.close();
                    System.out.print("\n");*/
                    
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (outputStream != null) { outputStream.close(); }
                }
                
                // finally close
                store.close();
            }
        } catch (SeqWareException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
