/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.importers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.BufferedReader;

import com.sleepycat.db.Transaction;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.store.impl.PostgreSQLBulkWriterStore;
import net.sourceforge.seqware.queryengine.backend.store.impl.PostgreSQLStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.tools.importers.workers.ImportWorker;
import net.sourceforge.seqware.queryengine.tools.importers.workers.PileupVariantImportWorker;
import net.sourceforge.seqware.queryengine.tools.iterators.processors.VariantProcessor;

/**
 * <p>VariantImporter class.</p>
 *
 * @author boconnor
 *
 * TODO:
 * @version $Id: $Id
 */
public class VariantImporter extends Importer {
  
  /**
   * <p>main.</p>
   *
   * @param args an array of {@link java.lang.String} objects.
   */
  public static void main(String[] args) {
      
      if (args.length < 17) {
          System.out.println("VariantImporter <worker_module> <db_dir> <create_db> <min_coverage> <max_coverage> <min_snp_quality> " +
          		"<compressed> <include_indels> <include_snv> <fastqConvNum> <cacheSize> <locks> " +
          		"<max_thread_count> <backend_type_[BerkeleyDB|HBase|PostgreSQL]> <genome_id> <reference_genome_id> <input_file(s)>");
          System.exit(-1);
      }

      String workerModule = args[0];
      String dbDir = args[1];
      boolean create = false;
      if ("true".equals(args[2])) { create = true; }
      int minCoverage = Integer.parseInt(args[3]);
      int maxCoverage = Integer.parseInt(args[4]);
      int minSnpQuality = Integer.parseInt(args[5]);
      boolean compressed = false;
      if ("true".equals(args[6])) { compressed = true; }
      boolean includeIndels = false;
      if ("true".equals(args[7])) { includeIndels = true; }
      boolean includeSNV = false;
      if ("true".equals(args[8])) { includeSNV = true; }
      int fastqConvNum = Integer.parseInt(args[9]);
      long cacheSize = Long.parseLong(args[10]);
      int locks = Integer.parseInt(args[11]);
      int threadCount = Integer.parseInt(args[12]);
      String backendType = args[13];
      String genomeId = args[14];
      String referenceId = args[15];
      
      boolean importCoverage = false;
      // TODO: would be nice to import at the same time but this causes transactional issues
      //if ("true".equals(args[12])) { importCoverage = true; }
      int binSize = 0; //Integer.parseInt(args[13]);
      
      ArrayList<String> inputFiles = new ArrayList<String>();
      for (int i=16; i<args.length; i++) {
        inputFiles.add(args[i]);
      }
      
      // objects to access the mutation datastore
      // FIXME: should be abstracted!
      BerkeleyDBFactory factory = new BerkeleyDBFactory();
      Store store = null;
      
      // a pointer to this object (for thread coordination)
      VariantImporter pmi = new VariantImporter(threadCount);
      
      try {
      	
        if ("BerkeleyDB".equals(backendType)) {
          // settings
        	SeqWareSettings settings = new SeqWareSettings();
        	settings.setStoreType("berkeleydb-mismatch-store");
        	settings.setFilePath(dbDir);
        	settings.setCacheSize(cacheSize);
        	settings.setCreateMismatchDB(create);
        	settings.setCreateConsequenceAnnotationDB(create);
        	settings.setCreateDbSNPAnnotationDB(create);
        	settings.setCreateCoverageDB(create);
          settings.setMaxLockers(locks);
          settings.setMaxLockObjects(locks);
          settings.setMaxLocks(locks);
          
        	// store object
          store = factory.getStore(settings);
        } else if ("HBase".equals(backendType)) {
          SeqWareSettings settings = new SeqWareSettings();
          settings.setCreateMismatchDB(create);
          settings.setCreateConsequenceAnnotationDB(create);
          settings.setCreateDbSNPAnnotationDB(create);
          settings.setCreateCoverageDB(create);
          settings.setStoreType("hbase-mismatch-store");
          settings.setGenomeId(genomeId);
          settings.setReferenceId(referenceId);
          store = new HBaseStore();
          store.setSettings(settings);
          store.setup(settings);
        } else if ("PostgreSQL".equals(backendType)) {
          SeqWareSettings settings = new SeqWareSettings();
          settings.setStoreType("postgresql-mismatch-store");
          // FIXME: need to make these params at some point
          settings.setDatabase(System.getProperty("db"));
          settings.setUsername(System.getProperty("user"));
          settings.setPassword(System.getProperty("pass"));
          settings.setServer(System.getProperty("dbserver"));
          settings.setReturnIds(false);
          settings.setPostgresqlPersistenceStrategy(settings.FIELDS);
          settings.setGenomeId(genomeId);
          settings.setReferenceId(referenceId);
          store = new PostgreSQLStore();
          store.setSettings(settings);
          store.setup(settings);
        } else if ("PostgreSQLBulkWriter".equals(backendType)) {
          SeqWareSettings settings = new SeqWareSettings();
          settings.setStoreType("postgresql-bulk-writer-mismatch-store");
          // FIXME: need to make these params at some point
          settings.setGenomeId(genomeId);
          settings.setReferenceId(referenceId);
          store = new PostgreSQLBulkWriterStore();
          store.setSettings(settings);
          store.setup(settings);
        }
        
        if (store != null) {
          
          Iterator<String> it = inputFiles.iterator();
          ImportWorker[] workerArray = new ImportWorker[inputFiles.size()];
          int index = 0;
          while (it.hasNext()) {
            
            // print message
            String input = (String) it.next();
            System.out.println("Starting worker thread to process file: "+input);
            
            // make a worker and launch it
            Class processorClass = Class.forName("net.sourceforge.seqware.queryengine.tools.importers.workers."+workerModule);
            workerArray[index] = (ImportWorker) processorClass.newInstance();
            workerArray[index].setWorkerName("PileupWorker"+index);
            workerArray[index].setPmi(pmi);
            workerArray[index].setStore(store);
            workerArray[index].setInput(input);
            workerArray[index].setCompressed(compressed);
            workerArray[index].setMinCoverage(minCoverage);
            workerArray[index].setMaxCoverage(maxCoverage);
            workerArray[index].setMinSnpQuality(minSnpQuality);
            workerArray[index].setIncludeSNV(includeSNV);
            workerArray[index].setFastqConvNum(fastqConvNum);
            workerArray[index].setIncludeIndels(includeIndels);
            workerArray[index].setIncludeCoverage(importCoverage);
            workerArray[index].setBinSize(binSize);
            workerArray[index].start();
            index++;
            
          }
          
          System.out.println("Joining threads");
          // join the threads, wait for each to finish
          for (int i = 0; i<workerArray.length; i++) {
             workerArray[i].join();
          }
          System.out.println("Threads finished");
          
          // finally close, checkpoint is part of the process
          store.close();
          
        }
      } // TODO: clearly this should be expanded to include closing database etc 
      catch (Exception e) {
          System.out.println("Exception!: "+e.getLocalizedMessage());
          e.printStackTrace();
          System.exit(-1);
      }
  }

  /**
   * <p>Constructor for VariantImporter.</p>
   *
   * @param threadCount a int.
   */
  public VariantImporter(int threadCount) {
    super(threadCount);
  }
  
}
