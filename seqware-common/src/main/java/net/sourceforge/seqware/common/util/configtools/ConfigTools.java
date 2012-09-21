package net.sourceforge.seqware.common.util.configtools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sourceforge.seqware.common.util.maptools.MapTools;

public class ConfigTools {

  public static final String SEQWARE_SETTINGS_PROPERTY = "SEQWARE_SETTINGS";

  /**
   * The output keys are always uppercase!
   * 
   * @return Map<String, String> settingsMap
   * @throws Exception
   */
  public static Map<String, String> getSettings() throws Exception {

    // first, try to figure out the location of the settings file
    File settingsFile = null;
    String settings = ConfigTools.getProperty(SEQWARE_SETTINGS_PROPERTY);
    if (settings == null || "".equals(settings)) {
      settings = ConfigTools.getProperty("HOME") + File.separator + ".seqware/settings";
    }
    if (ConfigTools.getProperty("HOME") == null || "".equals(ConfigTools.getProperty("HOME"))) {
      settings = ConfigTools.getProperty("user.home") + File.separator + ".seqware/settings";
    }

    // Log.info(settings);
    settingsFile = new File(settings);

    if (!settingsFile.exists()) {
      throw new Exception("The settings file " + settings + " does not exist!");
    } else if (!settingsFile.isFile()) {
      throw new Exception("The settings file " + settings + " is not a file!");
    }

    // else it should be OK
    // make sure the permissions are OK
    settingsFile.setExecutable(false, false);
    settingsFile.setWritable(false, false);
    settingsFile.setWritable(true, true);
    settingsFile.setReadable(false, false);
    settingsFile.setReadable(true, true);

    HashMap<String, String> hm = new HashMap<String, String>();

    // now read back the values from the settings file
    MapTools.ini2Map(settings, hm, true);

    return (hm);
  }

  public static String getProperty(String name) {

    String value = null;

    // try pulling from standard env variable for running as standalone web
    // server
    value = System.getProperty(name);

    if (value == null) {
      value = System.getenv(name);
    }

    // if that's null then try pulling from web.xml or context.xml
    if (value == null) {
      Context initCtx;
      try {
        initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        value = (String) envCtx.lookup(name);
      } catch (NamingException e) {
        // WARNING: This method of pulling env vars from a Context works
        // in a web server, this may fail in a stand-alone application.
        // If you see this warning you probably don't have a
        // needed/recommended environmental variable set
        // (e.g. SEQWARE_SETTINGS). SEQWARE_SETTINGS by default points
        // to ~/.seqware/settings.");
        // e.printStackTrace();
        value = null;
      }
    }

    return value;

  }
}
