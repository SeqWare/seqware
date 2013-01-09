package net.sourceforge.seqware.pipeline.daxgenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.freemarker.Freemarker;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import freemarker.template.TemplateException;
import java.io.File;
import net.sourceforge.seqware.common.util.Log;

/**
 * <p>Daxgenerator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Daxgenerator {

    /**
     * <p>getSyntax.</p>
     */
    public static void getSyntax() {
        Log.stdout("Syntax: java DAXGenerator [path_to_config/config1.ini [path_to_config/config2.ini] ...] [--key_to_override=value ...] path_to_template/template.ftl path_for_dax/output.dax");
        Log.stdout("Required file arguments:");
        Log.stdout("        .ftl: Freemarker template input      (e.g. ../workflows/HumanGenomicAlignment.ftl)");
        Log.stdout("        .dax: DAX file to output             (e.g. /tmp/HumanGenomicAlignment.dax)");
        Log.stdout("Optional file arguments:");
        Log.stdout("        .ini: 1 or more Config file inputs   (e.g. ../config/HumanGenomicAlignment.ini)");

        Log.stdout("Addition configuration can be specified using the --key=value, eg:");
        Log.stdout("        --experiment.ends=2");
        Log.stdout("        --bfasta.fasta_file=/scratch0/bfast/genomes/hg18/long/hg18.fa");
        Log.stdout("        --metadata.db_username=seqware");
        Log.stdout("");
        Log.stdout("Hierarchy for overriding parameters:");
        Log.stdout("        Lowest Priority:  first configuration ini listed");
        Log.stdout("        Overridden by:    additional (right-most) configuration ini files");
        Log.stdout("        Highest Priority: Command line parameters");

        System.exit(1);

    }

    /**
     * <p>processTemplate.</p>
     *
     * @param iniFiles an array of {@link java.lang.String} objects.
     * @param template a {@link java.lang.String} object.
     * @param output a {@link java.lang.String} object.
     * @param argMap a {@link java.util.Map} object.
     * @param extraArgs an array of {@link java.lang.String} objects.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public ReturnValue processTemplate(String[] iniFiles, String template, String output, Map<String, String> argMap, String[] extraArgs) {

        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        Map<String, String> map = null;
        if (argMap == null) {
            map = new HashMap<String, String>();
        } else {
            map = argMap;
        }

        // If a config.ini was mentioned, parse it. Otherwise instantiate a new
        // empty hash map
        if (iniFiles != null && iniFiles.length > 0) {
            for (int i = 0; i < iniFiles.length; i++) {
                Log.stdout("  INI FILE: "+iniFiles[i]);
                if ((new File(iniFiles[i])).exists()) {
                  MapTools.ini2Map(iniFiles[i], map);
                }
            }
        }

        // allow the command line options to override options in the map
        // Parse command line options for additional configuration. Note that we
        // do it last so it takes precedence over the INI
        MapTools.cli2Map(extraArgs, map);
        for (String key : map.keySet()) {
            Log.info("KEY AFTER CLI: " + key);
            if (key != null && map.get(key.toString()) != null) {
                Log.info(" VALUE: " + map.get(key.toString()).toString());
            } else {
                Log.info(" VALUE: null");
            }
            //Log.error(key+"="+map.get(key));
        }

        // Expand variables in the map
        MapTools.mapExpandVariables(map);

        // Change all integers from strings to int's
        Map<String, Object> newMap = MapTools.mapString2Int(map);

        // magic variables always set
        Date date = new Date();
        newMap.put("date", date.toString());

        Random rand = new Random(System.currentTimeMillis());
        int randInt = rand.nextInt(100000000);
        newMap.put("random", (new Integer(randInt)).toString());

        // Freemarker merge
        try {
            // Merge template with data
            boolean changed = Freemarker.merge(template, output, newMap);

            // While there are variables left, merge output file with hash
            while (changed == true) {
                changed = Freemarker.merge(output, output, newMap);
            }
        } catch (IOException e) {
            Log.error("IOException", e);
            //System.exit(ReturnValue.PROGRAMFAILED);
            ret.setExitStatus(ReturnValue.PROGRAMFAILED);
            return ret;
        } catch (TemplateException e) {
            // If we caught a template exception, warn and exit
            Log.error("Freemarker threw an exception: " + e.getMessage());
            //System.exit(ReturnValue.FREEMARKEREXCEPTION);
            ret.setExitStatus(ReturnValue.FREEMARKEREXCEPTION);
            return ret;
        }


        // FIXME: Should add a last pass through the DAX to:
        // Reformat XML, so indentations and all are proper, so make DAX easy to read
        // XmlTools.prettyPrint( output );
        // (2) Make sure it valid XML/DAX


        return (ret);
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        String template = null;
        String output = null;
        ArrayList<String> iniFiles = new ArrayList<String>();

        // Parse command line for options
        for (int i = 0; i < args.length; i++) {
            if (args[i].endsWith(".ini")) {
                iniFiles.add(args[i]);
            } else if (args[i].endsWith(".ftl")) {
                template = args[i];
            } else if (args[i].endsWith(".dax")) {
                output = args[i];
            }
        }

        // Make sure we have everything we need
        if (template == null || output == null) {
            getSyntax();
        }

        Daxgenerator dax = new Daxgenerator();

        ReturnValue ret = dax.processTemplate(iniFiles.toArray(new String[1]), template, output, null, null);

        System.exit(ret.getExitStatus());

    }
}
