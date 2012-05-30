/**
 * 
 */
package net.sourceforge.seqware.queryengine.prototypes.hadoop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;

/**
 * @author boconnor
 * 
 * This is a simple program that imports a series of pileup, dbSNP, and coding
 * consequence files using either a BerkeleyDB or HBase backend. It collects timing
 * and object count information and outputs this to a report file.
 *
 * TODO:
 */
public class ImporterTest2 {

  /**
   * @param args
   */
  public static void main(String[] args) {

    if (args.length < 8) {
      System.out.println("ImporterTest2 <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <db_dir> <table> " +
      		"<cacheSize> <locks> <inter_translocation_file> <intra_translocation_file> <chr_list> <pileup_dir> <dbSNP_dir> " +
      		"<consequence_dir> <output_report_file>");
      System.exit(-1);
    }

    String backendType = args[0];
    String genomeId = args[1];
    String referenceId = args[2];
    String dbDir = args[3];
    String table = args[4];
    long cacheSize = Long.parseLong(args[5]);
    int locks = Integer.parseInt(args[6]);
    String interTransFile = args[7];
    String intraTransFile = args[8];
    String[] chrList = args[9].split(",");
    String pileupDir = args[10];
    String dbsnpDir = args[11];
    String consequenceDir = args[12];
    String reportFile = args[13];

    BerkeleyDBFactory factory = new BerkeleyDBFactory();
    Store store = null;

    try {
      /*"VariantImporter <worker_module> <db_dir> <create_db> <min_coverage> <max_coverage> <min_snp_quality> " +
      "<compressed> <include_indels> <include_snv> <fastqConvNum> <cacheSize> <locks> " +
      "<max_thread_count> <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <input_file(s)>"*/
      
      for (String chr : chrList) {
      
        // import translocations
        
        // import snvs
        
        // import indels
        
        // import coverage
      
      }
      
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.out.println(e.getMessage());
    }

  }

}
