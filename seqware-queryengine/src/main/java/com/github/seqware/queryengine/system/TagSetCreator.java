package com.github.seqware.queryengine.system;

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.TagSet;
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
 * @version $Id: $Id
 */
public class TagSetCreator {

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        try {
            SGID mainMethod = TagSetCreator.mainMethod(args);
            if (mainMethod == null) {
                System.exit(FeatureImporter.EXIT_CODE_INVALID_FILE);
            }
        } catch (IllegalArgumentException e) {
            Logger.getLogger(ReferenceCreator.class.getName()).fatal(e);
            System.exit(FeatureImporter.EXIT_CODE_EXISTING_NAME);
        }
    }

    /**
     * Create a reference given just a name for the new reference to be created.
     *
     * @param args an array of {@link java.lang.String} objects.
     * @return a {@link com.github.seqware.queryengine.util.SGID} object.
     * @throws java.lang.IllegalArgumentException if any.
     */
    public static SGID mainMethod(String[] args) throws IllegalArgumentException {

        if (args.length < 1) {
            System.err.println("Only " + args.length + " arguments found");
            System.out.println(TagSetCreator.class.getSimpleName() + " <TagSet name> [output_file]");
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        }

        // check that the Reference does not already exist
        TagSet checkAtom = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(args[0], TagSet.class);
        if (checkAtom != null) {
            throw new IllegalArgumentException();
        }

        try {
            CreateUpdateManager modelManager = SWQEFactory.getModelManager();
            TagSet build = modelManager.buildTagSet().setFriendlyRowKey(args[0]).setName(args[0]).build();
            // handle output
            File outputFile = null;
            if (args.length == 2) {
                outputFile = Utility.checkOutput(args[1]);
            }
            Logger.getLogger(TagSetCreator.class.getName()).info("Writing TagSet");
            modelManager.close();
            // clean-up
            SWQEFactory.getStorage().closeStorage();
            System.out.println("TagSet written with an ID of:");
            String outputID = build.getSGID().getRowKey();
            System.out.println(outputID);
            Map<String, String> keyValues = new HashMap<String, String>();
            keyValues.put("TagSetID", outputID);
            keyValues.put("namespace", Constants.Term.NAMESPACE.getTermValue(String.class));
            Utility.writeKeyValueFile(outputFile, keyValues);
            return build.getSGID();
        } catch (IOException ex) {
            Logger.getLogger(TagSetCreator.class.getName()).fatal(null, ex);
        }
        return null;
    }

    /**
     * <p>Constructor for TagSetCreator.</p>
     */
    public TagSetCreator() {
        /**
         * do nothing
         */
    }
}
