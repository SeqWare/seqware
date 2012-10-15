package com.github.seqware.queryengine.system.importers;

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.obo.OboParser;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.biojava.bio.seq.io.ParseException;
import org.biojava.ontology.Ontology;
import org.biojava.ontology.Synonym;
import org.biojava.ontology.Term;

/**
 * Quick and dirty OBO file importer.
 *
 * This importer will create tagSets with term keys in the format [0-9]+(
 * [0-9]+)*, with leading 0's removed. A wrapper is provided for use with
 * RPNStack, so that full SO accessions can be passed as an argument (e.g.
 * SO:0001019), and the correct shortened accession will be looked up in the tag
 * set.
 *
 * A term SO:001023 is the unique identifier of an SO ontology entry, referring
 * to the term independent of its name or synonyms.
 *
 * @author dyuen
 * @author jbaran
 * @version $Id: $Id
 */
public class OBOImporter {

    /** Constant <code>TAG_CHAR='n'</code> */
    public static final char TAG_CHAR = 'n';
    /**
     * Determines whether we use "acccession-only" tags.
     */
    public static final boolean ACCESSION_ONLY = false;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        SGID mainMethod = OBOImporter.mainMethod(args);
        if (mainMethod == null) {
            System.exit(FeatureImporter.EXIT_CODE_INVALID_FILE);
        }
    }

    /**
     * Import a set of Tag specifications into a new TagSet.
     *
     * @param args an array of {@link java.lang.String} objects.
     * @return a {@link com.github.seqware.queryengine.util.SGID} object.
     */
    public static SGID mainMethod(String[] args) {

        if (args.length < 1) {
            System.err.println("Only " + args.length + " arguments found");
            System.out.println(OBOImporter.class.getSimpleName() + " [-" + TAG_CHAR + " optional name] <input_file> [output_file]");
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        }

        Options options = new Options();
        Option option1 = OptionBuilder.withArgName("name").withDescription("(optional) name for the SO TagSet").isRequired(false).hasArgs(1).create(TAG_CHAR);
        options.addOption(option1);
        String friendlyName = null;

        try {
            CommandLineParser clparser = new PosixParser();
            CommandLine cmd = clparser.parse(options, args);
            friendlyName = cmd.getOptionValue(TAG_CHAR);
            args = cmd.getArgs();
        } catch (org.apache.commons.cli.ParseException ex) {
            Logger.getLogger(OBOImporter.class.getName()).info(null, ex);
        }

        InputStream inStream = null;
        try {
            File file = new File(args[0]);
            OboParser parser = new OboParser();
            inStream = new FileInputStream(file);
            BufferedReader oboFile = new BufferedReader(new InputStreamReader(inStream));

            // handle output
            File outputFile = null;
            if (args.length == 2) {
                outputFile = Utility.checkOutput(args[1]);
            }

            Ontology ontology = parser.parseOBO(oboFile, "sequence ontology", "from http://www.sequenceontology.org/index.html");

            Set keys = ontology.getTerms();
            Iterator iter = keys.iterator();
            Pattern p = Pattern.compile("SO:\\d+");

            CreateUpdateManager modelManager = SWQEFactory.getModelManager();
            TagSet tagSet;
            if (friendlyName == null) {
                tagSet = modelManager.buildTagSet().setName("Sequence Ontology").build();
            } else {
                tagSet = modelManager.buildTagSet().setName("Sequence Ontology").setFriendlyRowKey(friendlyName).build();
            }


            while (iter.hasNext()) {
                Term term = (Term) iter.next();
                if (!p.matcher(term.getName()).matches()) {
                    Logger.getLogger(OBOImporter.class.getName()).trace("Skipping ... TERM: " + term.getDescription() + " DESC: " + term.getName());
                    continue;
                }
                // check for obsolete terms
                if (term.getAnnotation().containsProperty("is_obsolete")) {
                    Object property = term.getAnnotation().getProperty("is_obsolete");
                    if (property.equals(true)) {
                        continue;
                    }
                }

                Logger.getLogger(OBOImporter.class.getName()).trace("Adding ... TERM: " + term.getDescription() + " DESC: " + term.getName());


                // record the "head-liner" version of the Tag
                /* Removed storage of term names, since they are subject to change -- even
                 more so than synonyms. Rely only on accessions here.
                 * 
                 */
                if (!OBOImporter.ACCESSION_ONLY) {
                    String key1 = term.getName() + "::" + term.getDescription();
                    Logger.getLogger(OBOImporter.class.getName()).trace("Adding ... KEY: " + key1);
                    tagSet.add(modelManager.buildTag().setKey(key1).build());
                }


                String key2 = term.getName();
                Logger.getLogger(OBOImporter.class.getName()).trace("Adding ... KEY: " + key2);
                tagSet.add(modelManager.buildTag().setKey(key2).build());

                if (!OBOImporter.ACCESSION_ONLY) {
                    /* Removed storage of synonyms, since they are subject to change. Ontology
                     terms are sufficiently identified by their accession.
                     */
                    List<Synonym> synonyms = parser.getSynonymMap().get(term); //term.getSynonyms();
                    if (synonyms == null) {
                        continue;
                    }
                    for (Synonym syn : synonyms) {
                        String key3 = term.getName() + "::" + syn.getName();
                        Logger.getLogger(OBOImporter.class.getName()).trace("Adding synonym ... KEY: " + key3);
                        // for each of the synonyms, record them in the TagSet
                        tagSet.add(modelManager.buildTag().setKey(key3).build());
                    }
                }
            }

            Logger.getLogger(OBOImporter.class.getName()).info("Writing " + tagSet.getCount() + " tag specifications.");

            modelManager.close();

            // clean-up
            SWQEFactory.getStorage().closeStorage();
            System.out.println(tagSet.getCount() + " terms written to a TagSet written with an ID of:");
            String outputID = tagSet.getSGID().getRowKey();
            System.out.println(outputID);
            Map<String, String> keyValues = new HashMap<String, String>();
            keyValues.put("TagSetID", outputID);
            keyValues.put("namespace", Constants.Term.NAMESPACE.getTermValue(String.class));
            Utility.writeKeyValueFile(outputFile, keyValues);
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

    /**
     * <p>Constructor for OBOImporter.</p>
     */
    public OBOImporter() {
        /**
         * do nothing
         */
    }
}
