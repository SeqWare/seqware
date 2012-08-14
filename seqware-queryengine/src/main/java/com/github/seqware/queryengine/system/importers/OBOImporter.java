package com.github.seqware.queryengine.system.importers;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.TagSpecSet;
import com.github.seqware.queryengine.util.SGID;
import java.io.*;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.biojava.bio.seq.io.ParseException;
import org.biojava.ontology.Ontology;
import org.biojava.ontology.Term;
import org.biojava.ontology.io.OboParser;

;

/**
 * Quick and dirty OBO file importer.
 *
 * @author dyuen
 */
public class OBOImporter {

    public static void main(String[] args) {
        OBOImporter.mainMethod(args);
    }

    /**
     * Import a set of Features into a particular specified reference. The ID
     * for the FeatureSet we use is returned.
     *
     * @param args
     * @return
     */
    public static SGID mainMethod(String[] args) {

        if (args.length < 1) {
            System.err.println("Only " + args.length + " arguments found");
            System.out.println(OBOImporter.class.getSimpleName() + "<input_file>");
            System.exit(-1);
        }

        InputStream inStream = null;
        try {
            File file = new File(args[0]);
            OboParser parser = new OboParser();
            inStream = new FileInputStream(file);
            BufferedReader oboFile = new BufferedReader(new InputStreamReader(inStream));

            Ontology ontology = parser.parseOBO(oboFile, "sequence ontology", "from http://www.sequenceontology.org/index.html");

            Set keys = ontology.getTerms();
            Iterator iter = keys.iterator();
            Pattern p = Pattern.compile("SO:\\d+");

            CreateUpdateManager modelManager = SWQEFactory.getModelManager();
            TagSpecSet tagSet = modelManager.buildTagSpecSet().setName("Sequence Ontology").build();

            while (iter.hasNext()) {
                Term term = (Term) iter.next();
                if (!p.matcher(term.getName()).matches()) {
                    Logger.getLogger(OBOImporter.class.getName()).info("Skipping ... TERM: " + term.getDescription() + " DESC: " + term.getName());
                    continue;
                }
                // check for obselete terms
                if (term.getAnnotation().containsProperty("is_obsolete")){
                    Object property = term.getAnnotation().getProperty("is_obsolete");
                    if (property.equals(true)){
                        continue;
                    }
                }
                
                //System.out.println("Adding ... TERM: " + term.getDescription() + " DESC: " + term.getName());
                //System.out.println(term.getAnnotation());
                //Object[] synonyms = term.getSynonyms();
                //for (Object syn : synonyms) {
                //    System.out.println(syn);
                //}
                tagSet.add(modelManager.buildTagSpec().setKey(term.getDescription()).setValue(term.getName()).build());
            }

            Logger.getLogger(OBOImporter.class.getName()).info("Writing " + tagSet.getCount() + " tag specifications.");

            modelManager.close();

            // clean-up
            SWQEFactory.getStorage().closeStorage();
            System.out.println(tagSet.getCount() + " term TagSpecSet written with an ID of:");
            String outputID = tagSet.getSGID().getUuid().toString();
            System.out.println(outputID);
            return tagSet.getSGID();
        } catch (ParseException ex) {
            Logger.getLogger(OBOImporter.class.getName()).fatal(null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OBOImporter.class.getName()).fatal(null, ex);
        } finally {
            try {
                inStream.close();
            } catch (IOException ex) {
                Logger.getLogger(OBOImporter.class.getName()).fatal(null, ex);
            }
        }
        return null;
    }

    public OBOImporter() {
        /**
         * do nothing
         */
    }
}
