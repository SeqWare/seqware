package com.github.seqware.queryengine.system;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.TagSpecSet;
import com.github.seqware.queryengine.system.importers.FeatureImporter;
import com.github.seqware.queryengine.util.SGID;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Quick and dirty TagSet creator.
 * 
 * TODO: merge back-end with ReferenceCreator
 *
 * @author dyuen
 */
public class TagSpecSetCreator {

    public static void main(String[] args) {
        SGID mainMethod = TagSpecSetCreator.mainMethod(args);
        if (mainMethod == null){
            System.exit(FeatureImporter.EXIT_CODE_INVALID_FILE);
        }
    }

    /**
     * Create a reference given just a name for the new reference to be created.
     *
     * @param args
     * @return
     */
    public static SGID mainMethod(String[] args) {

        if (args.length < 1) {
            System.err.println("Only " + args.length + " arguments found");
            System.out.println(TagSpecSetCreator.class.getSimpleName() + " <TagSpecSet name> [output_file]");
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        }

        
        try {
            CreateUpdateManager modelManager = SWQEFactory.getModelManager();
            TagSpecSet build = modelManager.buildTagSpecSet().setName(args[0]).build();
            // handle output
            File outputFile = null;
            if (args.length == 2) {
                outputFile = Utility.checkOutput(args[1]);
            }
            Logger.getLogger(TagSpecSetCreator.class.getName()).info("Writing TagSpecSet");
            modelManager.close();
            // clean-up
            SWQEFactory.getStorage().closeStorage();
            System.out.println("TagSpecSet written with an ID of:");
            String outputID = build.getSGID().getUuid().toString();
            System.out.println(outputID);
            Map<String, String> keyValues = new HashMap<String, String>();
            keyValues.put("TagSpecSetID", outputID);
            Utility.writeKeyValueFile(outputFile, keyValues);
            return build.getSGID();
        } catch (IOException ex) {
            Logger.getLogger(TagSpecSetCreator.class.getName()).fatal(null, ex);
        }
        return null;
    }

    public TagSpecSetCreator() {
        /**
         * do nothing
         */
    }
}
