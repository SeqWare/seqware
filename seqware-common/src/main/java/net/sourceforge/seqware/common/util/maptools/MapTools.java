package net.sourceforge.seqware.common.util.maptools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;
import net.sourceforge.seqware.common.model.WorkflowRunParam;
import net.sourceforge.seqware.common.util.Log;
import static net.sourceforge.seqware.common.util.Rethrow.rethrow;
import org.apache.commons.io.IOUtils;

/**
 * <p>
 * MapTools class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class MapTools {

    /**
     * <p>
     * ini2Map.
     * </p>
     * 
     * @param iniFile
     *            a {@link java.lang.String} object.
     * @param hm
     *            a {@link java.util.Map} object.
     */
    public static void ini2Map(String iniFile, Map<String, String> hm) {
        ini2Map(iniFile, hm, false);
    }

    /**
     * This is a little different than the ini2Map since it allows us to read ini files where key-value annotations on the key-values appear
     * in comments on the previous line above the current such as:
     * 
     * key=bam_inputs:type=file:display=T:display_name=BAM Input(s):file_meta_type=application/bam
     * 
     * or
     * 
     * key=run_ends:type=pulldown:display=T:display_name=Single or Paired Ends:pulldown_items=Single End|1;Paired End|2
     * 
     * The extra information is used by the Portal and Web Service to display interfaces that let people call the workflows correctly. This
     * code doesn't know about the contents of the metadata key-values other than they are ":" delimited with key=value.
     * 
     * @param iniFile
     *            a {@link java.lang.String} object.
     * @param hm
     *            a HashMap to be filled with key and details as key-values in a nested HashMap.
     */
    public static void ini2RichMap(String iniFile, Map<String, Map<String, String>> hm) {

        HashMap<String, String> detailsMap = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(iniFile)));
            String line;
            while ((line = br.readLine()) != null) {
                // this deals with key value annotations
                if (line.startsWith("#") && line.matches("^#\\s*key=.*$")) {
                    detailsMap = new HashMap<>();
                    line = line.replaceAll("#\\s*", "");
                    String[] kvs = line.split(":");
                    for (String pair : kvs) {
                        String[] kv = pair.split("=");
                        if (kv.length == 2)
                            detailsMap.put(kv[0], kv[1]);
                        else
                            detailsMap.put(kv[0], "");
                    }
                    // this deals with keys
                } else if (isLineMatchesKeyValue(line)) {
                    String[] kv = line.split("\\s*=\\s*");
                    if (detailsMap == null || !kv[0].equals(detailsMap.get("key"))) {
                        detailsMap = new HashMap<>();
                        detailsMap.put("key", kv[0]);
                    }
                    if (kv.length == 1) {
                        detailsMap.put("default_value", "");
                    } else {
                        detailsMap.put("default_value", kv[1]);
                    }
                    hm.put(kv[0], detailsMap);
                }
            }
        } catch (Exception e) {
            throw rethrow(e);
        }

    }

    /**
     * <p>
     * ini2Map.
     * </p>
     * 
     * @param iniFile
     *            a {@link java.lang.String} object.
     * @param hm
     *            a {@link java.util.Map} object.
     * @param keyToUpper
     *            a boolean.
     */
    public static void ini2Map(String iniFile, Map<String, String> hm, boolean keyToUpper) {
        // Load config ini from disk
        FileInputStream iStream = null;
        try {
            iStream = new FileInputStream(iniFile);
            ini2Map(iStream, hm, keyToUpper);
        } catch (Exception e) {
            throw rethrow(e);
        } finally {
            IOUtils.closeQuietly(iStream);
        }
    }

    /**
     * <p>
     * ini2Map.
     * </p>
     * 
     * @param iniFile
     *            a {@link java.io.InputStream} object.
     * @param hm
     *            a {@link java.util.Map} object.
     * @param keyToUpper
     *            a boolean.
     * @throws java.io.IOException
     *             if any.
     */
    public static void ini2Map(InputStream iniFile, Map<String, String> hm, boolean keyToUpper) throws IOException {
        // Load config ini from stream
        Properties config = new Properties();
        config.load(iniFile);

        // Convert to hashmap
        Enumeration en = config.propertyNames();

        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            if (keyToUpper) {
                hm.put(key.toUpperCase(), config.getProperty(key));
            } else {
                hm.put(key, config.getProperty(key));
            }
        }
    }

    /**
     * Method to getValues all "--key=value" or "--key value" parameters and add them to hashmap
     * 
     * @param args
     *            an array of {@link java.lang.String} objects.
     * @param hm
     *            a {@link java.util.Map} object.
     */
    public static void cli2Map(String[] args, Map<String, String> hm) {
        if (args == null || hm == null) {
            Log.info("cliMap input NULL");
            return;
        }
        // Parse command line arguments for --key=value
        String currKey = "";
        for (String arg : args) {
            Log.info("CURR KEY: " + arg);
            // If it starts with --, try to split on =
            if (arg.startsWith("--")) {
                String[] split = arg.split("\\=");
                // If had =, turn key into args
                if (split.length == 2) {
                    // Strip starting --
                    hm.put(split[0].substring(2), split[1]);
                    currKey = "";
                } else {
                    Log.info("FOUND KEY " + currKey);
                    currKey = arg.substring(2);
                }
            } else {
                if (!"".equals(currKey)) {
                    Log.info("PUTTING KEY VALUE " + currKey + " " + arg);
                    hm.put(currKey, arg);
                }
            }
        }
    }

    /*
     * Iterate through a Map and change all Strings to ints where possible
     */
    /**
     * <p>
     * mapString2Int.
     * </p>
     * 
     * @param map
     *            a {@link java.util.Map} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map mapString2Int(Map map) {
        Map<String, Object> result = new HashMap<>();
        Iterator iter = map.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();

            // Try to convert string to number
            try {
                Long number = Long.parseLong((String) map.get(key));
                result.put(key, number);
            } catch (NumberFormatException e) {
                // Ignore exception, caust just means it is a string
                result.put(key, map.get(key));
            }
        }
        return (result);
    }

    public static final String VAR_RANDOM = "sqw.random";
    public static final String VAR_DATE = "sqw.date";
    public static final String VAR_DATETIME = "sqw.datetime";
    public static final String VAR_TIMESTAMP = "sqw.timestamp";
    public static final String VAR_UUID = "sqw.uuid";
    public static final String VAR_BUNDLE_DIR = "sqw.bundle-dir";
    public static final String LEGACY_VAR_RANDOM = "random";
    public static final String LEGACY_VAR_DATE = "date";
    public static final String LEGACY_VAR_BUNDLE_DIR = "workflow_bundle_dir";

    public static void provideBundleDir(Map<String, String> m, String bundleDir) {
        m.put(VAR_BUNDLE_DIR, bundleDir);
        m.put(LEGACY_VAR_BUNDLE_DIR, bundleDir);
    }

    public static Map<String, String> providedMap(String bundleDir) {
        Map<String, String> m = new HashMap<>();
        provideBundleDir(m, bundleDir);
        return m;
    }

    public static String generatedValue(String key) {
        if (key.equals(VAR_RANDOM)) return String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
        if (key.equals(VAR_DATE)) return DatatypeConverter.printDate(Calendar.getInstance());
        if (key.equals(VAR_DATETIME)) return DatatypeConverter.printDateTime(Calendar.getInstance());
        if (key.equals(VAR_TIMESTAMP)) return String.valueOf(System.currentTimeMillis());
        if (key.equals(VAR_UUID)) return UUID.randomUUID().toString();

        if (key.equals(LEGACY_VAR_RANDOM)) {
            Log.warn(String.format("Variable '%s' is deprecated. Please use '%s' instead.", LEGACY_VAR_RANDOM, VAR_RANDOM));
            return String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
        }
        if (key.equals(LEGACY_VAR_DATE)) {
            Log.warn(String.format("Variable '%s' is deprecated. Please use '%s' instead.", LEGACY_VAR_DATE, VAR_DATE));
            return DatatypeConverter.printDate(Calendar.getInstance());
        }

        return null;
    }

    private static final Pattern VAR = Pattern.compile("\\$\\{([^\\}]*)\\}");

    public static Map<String, String> expandVariables(Map<String, String> raw) {
        return expandVariables(raw, null);
    }

    public static Map<String, String> expandVariables(Map<String, String> raw, Map<String, String> provided) {
        return expandVariables(raw, provided, false);
    }

    public static Map<String, String> expandVariables(Map<String, String> raw, Map<String, String> provided, boolean allowMissingVars) {
        raw = new HashMap<>(raw); // don't mess with someone else's data structure
        Map<String, String> exp = new HashMap<>();

        int prevCount;
        do {
            prevCount = raw.size();
            Iterator<Map.Entry<String, String>> iter = raw.entrySet().iterator();
            entries: while (iter.hasNext()) {
                Map.Entry<String, String> e = iter.next();
                Matcher m = VAR.matcher(e.getValue());
                if (m.find()) {
                    // this value has variables
                    StringBuffer sb = new StringBuffer();
                    do {
                        String key = m.group(1);
                        String val = exp.get(key);
                        if (val == null && provided != null) val = provided.get(key);
                        if (val == null) val = generatedValue(key);

                        if (val != null) {
                            // found substitution, replace and then look for more
                            m.appendReplacement(sb, val);
                        } else {
                            // we don't yet have all the substitutions, skip this entry for now
                            continue entries;
                        }
                    } while (m.find());
                    // done substituting the variables
                    m.appendTail(sb);
                    exp.put(e.getKey(), sb.toString());
                    iter.remove();
                } else {
                    // no variables, move the entry
                    exp.put(e.getKey(), e.getValue());
                    iter.remove();
                }
            }
            // exit when nothing left to convert or no incremental improvement
        } while (0 < raw.size() && raw.size() < prevCount);

        if (!allowMissingVars && raw.size() > 0) {
            StringBuilder sb = new StringBuilder("Could not satisfy variable substitution:");
            for (Map.Entry<String, String> e : raw.entrySet()) {
                sb.append("\n");
                sb.append(e.getKey());
                sb.append("=");
                sb.append(e.getValue());
            }
            throw new RuntimeException(sb.toString());
        }

        return exp;
    }

    /**
     * <p>
     * iniString2Map.
     * </p>
     * 
     * @param iniString
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, String> iniString2Map(String iniString) {
        Map<String, String> result = new HashMap<>();
        String[] lines = iniString.split("\n");
        for (String line : lines) {
            if (isLineMatchesKeyValue(line)) {
                // seqware-1911 allow for second = in value
                String[] kv = line.split("\\s*=\\s*", 2);
                if (kv.length == 2) {
                    result.put(kv[0], kv[1]);
                } else if (kv.length == 1) {
                    result.put(kv[0], "");
                } else {
                    System.err.println("Found a line I couldn't parse: " + line);
                }
            }
        }
        return (result);
    }

    private static boolean isLineMatchesKeyValue(String line) {
        return !line.startsWith("#") && line.matches("\\S+\\s*=[^=]*");
    }

    /**
     * Convert what looks like a map of ini key-value pairs into a SortedSet of WorkflowRunParams
     * 
     * @param map
     * @return
     * @throws NumberFormatException
     */
    public static SortedSet createWorkflowRunParameters(HashMap<String, String> map) throws NumberFormatException {
        SortedSet<WorkflowRunParam> runParams = new TreeSet<>();
        for (String str : map.keySet()) {
            if (map.get(str) != null) {
                Log.info(str + " " + map.get(str));
                if (str.equals(ReservedIniKeys.PARENT_UNDERSCORE_ACCESSIONS.getKey())
                        || str.equals(ReservedIniKeys.PARENT_ACCESSION.getKey())
                        || str.equals(ReservedIniKeys.PARENT_DASH_ACCESSIONS.getKey())) {
                    String[] pAcc = map.get(str).split(",");
                    for (String parent : pAcc) {
                        WorkflowRunParam wrp = new WorkflowRunParam();
                        wrp.setKey(str);
                        wrp.setValue(parent);
                        wrp.setParentProcessingAccession(Integer.parseInt(parent));
                        wrp.setType("text");
                        runParams.add(wrp);
                    }
                } else {
                    if (str.trim().isEmpty() && map.get(str).trim().isEmpty()) {
                        continue;
                    }
                    WorkflowRunParam wrp = new WorkflowRunParam();
                    wrp.setKey(str);
                    wrp.setValue(map.get(str));
                    wrp.setType("text");
                    runParams.add(wrp);
                }
            } else {
                Log.info("Null: " + str + " " + map.get(str));
            }
        }
        return runParams;
    }
}
