package net.sourceforge.seqware.common.util.maptools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import net.sourceforge.seqware.common.util.Log;

/**
 * <p>MapTools class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class MapTools {

    /**
     * <p>ini2Map.</p>
     *
     * @param iniFile a {@link java.lang.String} object.
     * @param hm a {@link java.util.Map} object.
     */
    public static void ini2Map(String iniFile, Map<String, String> hm) {
        ini2Map(iniFile, hm, false);
    }

    /**
     * This is a little different than the ini2Map since it allows us to read
     * ini files where key-value annotations on the key-values appear in
     * comments on the previous line above the current such as:
     *
     * key=bam_inputs:type=file:display=T:display_name=BAM
     * Input(s):file_meta_type=application/bam
     *
     * or
     *
     * key=run_ends:type=pulldown:display=T:display_name=Single or Paired
     * Ends:pulldown_items=Single End|1;Paired End|2
     *
     * The extra information is used by the Portal and Web Service to display
     * interfaces that let people call the workflows correctly. This code
     * doesn't know about the contents of the metadata key-values other than
     * they are ":" delimited with key=value.
     *
     * @param iniFile a {@link java.lang.String} object.
     * @param hm a HashMap to be filled with key and details as key-values in a
     * nested HashMap.
     */
    public static void ini2RichMap(String iniFile, Map<String, Map<String, String>> hm) {

        HashMap<String, String> detailsMap = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(iniFile)));
            String line;
            while ((line = br.readLine()) != null) {
                // this deals with key value annotations
                if (line.startsWith("#") && line.matches("^#\\s*key=.*$")) {
                    detailsMap = new HashMap<String, String>();
                    line = line.replaceAll("#\\s*", "");
                    String[] kvs = line.split(":");
                    for (String pair : kvs) {
                        String[] kv = pair.split("=");
                        if (kv.length==2)
                            detailsMap.put(kv[0], kv[1]);
                        else
                            detailsMap.put(kv[0], "");
                    }
                // this deals with keys
                } else if (isLineMatchesKeyValue(line)) {
                    String[] kv = line.split("\\s*=\\s*");
                    if (detailsMap == null || !kv[0].equals(detailsMap.get("key"))) {
                        detailsMap = new HashMap<String, String>();
                        detailsMap.put("key", kv[0]);
                    }
                    if (kv.length == 1){
                        detailsMap.put("default_value", "");
                    } else{
                        detailsMap.put("default_value", kv[1]);
                    }
                    hm.put(kv[0], detailsMap);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.stderr(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.stderr(e.getMessage());
        }

    }

    /**
     * <p>ini2Map.</p>
     *
     * @param iniFile a {@link java.lang.String} object.
     * @param hm a {@link java.util.Map} object.
     * @param keyToUpper a boolean.
     */
    public static void ini2Map(String iniFile, Map<String, String> hm, boolean keyToUpper) {
        // Load config ini from disk
        try {
            ini2Map(new FileInputStream(iniFile), hm, keyToUpper);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>ini2Map.</p>
     *
     * @param iniFile a {@link java.io.InputStream} object.
     * @param hm a {@link java.util.Map} object.
     * @param keyToUpper a boolean.
     * @throws java.io.IOException if any.
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
     * Method to getValues all "--key=value" or "--key value" parameters and add
     * them to hashmap
     *
     * @param args an array of {@link java.lang.String} objects.
     * @param hm a {@link java.util.Map} object.
     */
    public static void cli2Map(String[] args, Map<String, String> hm) {
        if (args == null || hm == null) {
            Log.info("cliMap input NULL");
            return;
        }
        // Parse command line arguments for --key=value
        String currKey = "";
        for (int i = 0; i < args.length; i++) {
            Log.info("CURR KEY: " + args[i]);
            // If it starts with --, try to split on =
            if (args[i].startsWith("--")) {
                String[] split = args[i].split("\\=");
                // If had =, turn key into args
                if (split.length == 2) {
                    // Strip starting --
                    hm.put(split[0].substring(2), split[1]);
                    currKey = "";
                } else {
                    Log.info("FOUND KEY " + currKey);
                    currKey = args[i].substring(2);
                }
            } else {
                if (!"".equals(currKey)) {
                    Log.info("PUTTING KEY VALUE " + currKey + " " + args[i]);
                    hm.put(currKey, args[i]);
                }
            }
        }
    }

    /*
     * Iterate through a Map and change all Strings to ints where possible
     */
    /**
     * <p>mapString2Int.</p>
     *
     * @param map a {@link java.util.Map} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map mapString2Int(Map map) {
        Map<String, Object> result = new HashMap<String, Object>();
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

    /*
     * Iterate through a Map and change all Strings to ints where possible
     * FIXME: Here we are assuming the variable is the entire value. Instead,
     * parse the value in case it is embeded. i.e. key=foo${value2}xxx
     */
    /**
     * <p>mapExpandVariables.</p>
     *
     * @param map a {@link java.util.Map} object.
     */
    public static void mapExpandVariables(Map map) {
        Iterator iter = map.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String val = (String) map.get(key);

            while (val != null && val.startsWith("${") && val.endsWith("}")) {
                String variableKey = val.substring(2, val.length() - 1);
                val = (String) map.get(variableKey);

                if (val.compareTo("${" + variableKey + "}") == 0) {
                    Log.stderr("Problem: Variable cannot be resolved: " + val);
                    System.exit(1);
                } else {
                    map.put(key, val);
                }

            }
        }
    }

    /**
     * <p>iniString2Map.</p>
     *
     * @param iniString a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, String> iniString2Map(String iniString) {
        Map<String, String> result = new HashMap<String, String>();
        String[] lines = iniString.split("\n");
        for (String line : lines) {
            if (isLineMatchesKeyValue(line)) {
                String[] kv = line.split("\\s*=\\s*");
                if (kv.length == 2) {
                    result.put(kv[0], kv[1]);
                } else if (kv.length == 1){
                    result.put(kv[0], "");
                } 
                else {
                    System.err.println("Found a line I couldn't parse: "+line);
                }
            }
        }
        return (result);
    }

    private static boolean isLineMatchesKeyValue(String line) {
        return !line.startsWith("#") && line.matches("\\S+\\s*=[^=]*");
    }
}
