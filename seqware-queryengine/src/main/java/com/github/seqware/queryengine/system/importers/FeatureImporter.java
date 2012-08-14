package com.github.seqware.queryengine.system.importers;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.system.importers.workers.ImportWorker;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

;

/**
 * Port of the class from the original prototype, adapted to use the new classes
 * in the new API. In the old API, there was very little "management" code so
 * all of the workers shared a reference to the same Store class with a small
 * dab of synchronization. In the new API, we have to contend with the
 * CreateUpdateManager, so I think it makes more sense to have one
 * CreateUpdateManager per thread rather than freeze all threads when one wants
 * to synchronize for example.
 *
 * @author boconnor
 * @author dyuen
 */
public class FeatureImporter extends Importer {
    
    public static int EXIT_CODE_INVALID_ARGS = 1;
    public static int EXIT_CODE_INVALID_FILE = 10;

    /**
     * This method does the actual work of importing given properly parsed parameters
     * @param referenceID
     * @param threadCount
     * @param inputFiles
     * @param workerModule
     * @param compressed
     * @param outputFile
     * @return 
     */
    protected static SGID performImport(String referenceID, int threadCount, ArrayList<String> inputFiles, String workerModule, boolean compressed, File outputFile) {
        // objects to access the mutation datastore
        //      BerkeleyDBFactory factory = new BerkeleyDBFactory();
        //      BerkeleyDBStore store = null;
        CreateUpdateManager modelManager = SWQEFactory.getModelManager();
        SeqWareIterable<Reference> references = SWQEFactory.getQueryInterface().getReferences();
        Reference ref = null;
        for (Reference reference : references) {
            if (reference.getName().equals(referenceID)) {
                ref = reference;
                break;
            }
        }
        // see if this referenceID already exists
        if (ref == null) {
            ref = modelManager.buildReference().setName(referenceID).build();
            modelManager.flush();
        }
        // create a centralized FeatureSet
        FeatureSet featureSet = modelManager.buildFeatureSet().setReference(ref).build();
        // we don't really need the central model manager past this point 
        modelManager.close();

        // a pointer to this object (for thread coordination)
        final FeatureImporter pmi = new FeatureImporter(threadCount);

        try {

//            // settings
//            SeqWareSettings settings = new SeqWareSettings();
//            settings.setStoreType("berkeleydb-mismatch-store");
//            settings.setFilePath(dbDir);
//            settings.setCacheSize(cacheSize);
//            settings.setCreateMismatchDB(create);
//            settings.setCreateConsequenceAnnotationDB(create);
//            settings.setCreateDbSNPAnnotationDB(create);
//            settings.setCreateCoverageDB(create);
//            settings.setMaxLockers(locks);
//            settings.setMaxLockObjects(locks);
//            settings.setMaxLocks(locks);

            // store object
            // store = factory.getStore(settings);

            if (modelManager /**
                     * store
                     */
                    != null) {

                Iterator<String> it = inputFiles.iterator();
                ImportWorker[] workerArray = new ImportWorker[inputFiles.size()];
                int index = 0;
                while (it.hasNext()) {

                    // print message
                    String input = (String) it.next();
                    Logger.getLogger(FeatureImporter.class.getName()).info("Starting worker thread to process file: " + input);

                    // make a worker and launch it
                    Class processorClass = Class.forName("com.github.seqware.queryengine.system.importers.workers." + workerModule);
                    workerArray[index] = (ImportWorker) processorClass.newInstance();
                    workerArray[index].setWorkerName("PileupWorker" + index);
                    workerArray[index].setPmi(pmi);
//                    workerArray[index].setStore(modelManager);
                    workerArray[index].setInput(input);
                    workerArray[index].setFeatureSetID(featureSet.getSGID());
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
                    
                    // set up exception handling
                    workerArray[index].setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(Thread thread, Throwable thrwbl) {
                            pmi.failedWorkers.add((ImportWorker)thread);
                        }
                    });
                    
                    workerArray[index].start();
                    index++;

                }
                Logger.getLogger(FeatureImporter.class.getName()).info("Joining threads");
                // join the threads, wait for each to finish
                for (int i = 0; i < workerArray.length; i++) {
                    workerArray[i].join();
                }
                Logger.getLogger(FeatureImporter.class.getName()).info("Threads finished");

                // finally close, checkpoint is part of the process
                // modelManager.close();
                //store.close();

            }
        } // TODO: clearly this should be expanded to include closing database etc 
        catch (Exception e) {
            Logger.getLogger(FeatureImporter.class.getName()).fatal("Exception thrown with file: \n", e);
            return null;
        }
        // check for failed workers
        if (pmi.failedWorkers.size() > 0){
            return null;
        }
        
        // clean-up
        SWQEFactory.getStorage().closeStorage();
        System.out.println("FeatureSet written with an ID of:");
        String outputID = featureSet.getSGID().getUuid().toString();
        System.out.println(outputID);
        if (outputFile != null) {
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
                out.println("FeatureSetID\t" + outputID);
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(FeatureImporter.class.getName()).fatal("Could not write to output file");
            }
        }
        return featureSet.getSGID();
    }
    
    private List<ImportWorker> failedWorkers = new ArrayList<ImportWorker>();


    public static void main(String[] args) {
        SGID mainMethod = FeatureImporter.naiveRun(args);
        if (mainMethod == null){
            System.exit(EXIT_CODE_INVALID_FILE);
        }
    }

    /**
     * Import a set of Features into a particular specified reference. The ID
     * for the FeatureSet we use is returned.
     *
     * @param args
     * @return
     */
    public static SGID naiveRun(String[] args) {

        if (args.length < 5) {
            System.err.println("Only " + args.length + " arguments found");
            //System.out.println("FeatureImporter <worker_module> <db_dir> <create_db> <cacheSize> <locks> "
            //        + "<max_thread_count> <compressed_input> <input_file(s)>");
            System.out.println("FeatureImporter <worker_module> <max_thread_count> <compressed_input> <reference name> <input_file1[,input_file(s)]> [output_file]");
            System.exit(EXIT_CODE_INVALID_ARGS);
        }

        String workerModule = args[0];
        int threadCount = Integer.parseInt(args[1]);
        boolean compressed = false;
        if ("true".equals(args[2])) {
            compressed = true;
        }

        String referenceID = args[3];

        ArrayList<String> inputFiles = new ArrayList<String>();
        inputFiles.addAll(Arrays.asList(args[4].split(",")));

        // handle output
        File outputFile = null;
        if (args.length == 6) {
            outputFile = new File(args[5]);
            if (outputFile.exists()) {
                outputFile.delete();
            }
            try {
                boolean created = outputFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(FeatureImporter.class.getName()).fatal("Could not create output file");
                return null;
            }
        }
        return performImport(referenceID, threadCount, inputFiles, workerModule, compressed, outputFile);
    }

    public FeatureImporter(int threadCount) {
        super(threadCount);
    }

    public void reportException(ImportWorker aThis) {
        this.failedWorkers.add(aThis);
    }
}
