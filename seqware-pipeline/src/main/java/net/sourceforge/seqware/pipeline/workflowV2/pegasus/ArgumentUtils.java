package net.sourceforge.seqware.pipeline.workflowV2.pegasus;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>ArgumentUtils class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ArgumentUtils {
    /**
     * <p>getDefaultJavaArguments.</p>
     *
     * @param map a {@link java.util.Map} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, String> getDefaultJavaArguments(
	    Map<String, String> map) {
	String path = "${basedir}/lib/seqware-distribution-${workflow_seqware_version}-full.jar";
	Map<String, String> results = new LinkedHashMap<String, String>();
	results.put("-Xmx1000M", null);
	String clazzPath = StringUtils.replace(path, map);
	results.put("-classpath", clazzPath);
	results.put("net.sourceforge.seqware.pipeline.runner.Runner", null);
	return results;
    }
}
