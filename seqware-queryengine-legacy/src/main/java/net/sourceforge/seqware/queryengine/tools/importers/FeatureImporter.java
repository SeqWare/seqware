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
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.tools.importers.workers.ImportWorker;
import net.sourceforge.seqware.queryengine.tools.importers.workers.PileupVariantImportWorker;
import net.sourceforge.seqware.queryengine.tools.iterators.processors.VariantProcessor;

/**
 * <p>FeatureImporter class.</p>
 *
 * @author boconnor
 *
 * TODO:
 * @version $Id: $Id
 */
public class FeatureImporter extends Importer {
  
  /**
   * <p>main.</p>
   *
   * @param args an array of {@link java.lang.String} objects.
   */
  public static void main(String[] args) {
      
      if (args.length < 8) {
          System.out.println("FeatureImporter <worker_module> <db_dir> <create_db> <cacheSize> <locks> " +
          		"<max_thread_count> <compressed_input> <input_file(s)>");
          System.exit(-1);
      }

      String workerModule = args[0];
      String dbDir = args[1];
      boolean create = false;
      if ("true".equals(args[2])) { create = true; }
      long cacheSize = Long.parseLong(args[3]);
      int locks = Integer.parseInt(args[4]);
      int threadCount = Integer.parseInt(args[5]);
      boolean compressed = false;
      if ("true".equals(args[6])) { compressed = true; }
      
      ArrayList<String> inputFiles = new ArrayList<String>();
      for (int i=7; i<args.length; i++) {
        inputFiles.add(args[i]);
      }
      
      // objects to access the mutation datastore
      BerkeleyDBFactory factory = new BerkeleyDBFactory();
      BerkeleyDBStore store = null;
      
      // a pointer to this object (for thread coordination)
      FeatureImporter pmi = new FeatureImporter(threadCount);
      
      try {
      	
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
            // FIXME: most of the rest aren't used, I should consider cleaning this up
            workerArray[index].setCompressed(compressed);
            workerArray[index].setMinCoverage(0);
            workerArray[index].setMaxCoverage(0);
            workerArray[index].setMinSnpQuality(0);
            workerArray[index].setIncludeSNV(false);
            workerArray[index].setFastqConvNum(0);
            workerArray[index].setIncludeIndels(false);
            workerArray[index].setIncludeCoverage(false);
            workerArray[index].setBinSize(0);
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
   * <p>Constructor for FeatureImporter.</p>
   *
   * @param threadCount a int.
   */
  public FeatureImporter(int threadCount) {
    super(threadCount);
  }

}
