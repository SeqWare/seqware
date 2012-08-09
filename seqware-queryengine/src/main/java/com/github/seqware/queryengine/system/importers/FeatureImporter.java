/**
 *
 */
package com.github.seqware.queryengine.system.importers;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.system.importers.workers.GFF3VariantImportWorker;
import com.github.seqware.queryengine.system.importers.workers.ImportWorker;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Port of the class from the original prototype, adapted to use the new classes
 * in the new API. In the old API, there was very little "management" code so
 * all of the workers shared a reference to the same Store class with a small dab of 
 * synchronization. In the new API, we have to contend with the CreateUpdateManager, so
 * I think it makes more sense to have one CreateUpdateManager per thread rather than freeze 
 * all threads when one wants to synchronize for example. 
 *
 * @author boconnor
 * @author dyuen
 */
public class FeatureImporter extends Importer {

    public static void main(String[] args){
        FeatureImporter.mainMethod(args);
    }
    
    /**
     * Import a set of Features into a particular specified reference.
     * The ID for the FeatureSet we use is returned.
     * @param args
     * @return 
     */
    public static SGID mainMethod(String[] args) {

        if (args.length < 5) {
            System.err.println("Only " + args.length + " arguments found");
            //System.out.println("FeatureImporter <worker_module> <db_dir> <create_db> <cacheSize> <locks> "
            //        + "<max_thread_count> <compressed_input> <input_file(s)>");
            System.out.println("FeatureImporter <worker_module> <max_thread_count> <compressed_input> <reference name> <input_file(s)>");
            System.exit(-1);
        }

        String workerModule = args[0];
        int threadCount = Integer.parseInt(args[1]);
        boolean compressed = false;
        if ("true".equals(args[2])) {
            compressed = true;
        }

        String referenceID = args[3];

        ArrayList<String> inputFiles = new ArrayList<String>();
        for (int i = 4; i < args.length; i++) {
            inputFiles.add(args[i]);
        }
        // objects to access the mutation datastore
        //      BerkeleyDBFactory factory = new BerkeleyDBFactory();
        //      BerkeleyDBStore store = null;
        CreateUpdateManager modelManager = SWQEFactory.getModelManager();
        SeqWareIterable<Reference> references = SWQEFactory.getQueryInterface().getReferences();
        Reference ref = null;
        for(Reference reference : references){
            if (reference.getName().equals(referenceID)){
                ref = reference;
                break;
            }
        }
        // see if this referenceID already exists
        if (ref == null){
            ref = modelManager.buildReference().setName(referenceID).build();
            modelManager.flush();
        }
        // create a centralized FeatureSet
        FeatureSet featureSet = modelManager.buildFeatureSet().setReference(ref).build();
        // we don't really need the central model manager past this point 
        modelManager.close();

        // a pointer to this object (for thread coordination)
        FeatureImporter pmi = new FeatureImporter(threadCount);

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
                    System.out.println("Starting worker thread to process file: " + input);

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
                    workerArray[index].start();
                    index++;

                }

                System.out.println("Joining threads");
                // join the threads, wait for each to finish
                for (int i = 0; i < workerArray.length; i++) {
                    workerArray[i].join();
                }
                System.out.println("Threads finished");

                // finally close, checkpoint is part of the process
                // modelManager.close();
                //store.close();

            }
        } // TODO: clearly this should be expanded to include closing database etc 
        catch (Exception e) {
            Logger.getLogger(GFF3VariantImportWorker.class.getName()).log(Level.SEVERE, "Exception thrown with file: \n", e);
            System.exit(-1);
        }
        // clean-up
        SWQEFactory.getStorage().closeStorage();
        System.out.println("Success, FeatureSet written with an ID of:");
        System.out.println(featureSet.getSGID().getUuid().toString());
        return featureSet.getSGID();
    }

    public FeatureImporter(int threadCount) {
        super(threadCount);
    }
}
