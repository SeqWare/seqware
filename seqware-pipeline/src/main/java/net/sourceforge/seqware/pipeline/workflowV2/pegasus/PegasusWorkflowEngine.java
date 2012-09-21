/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.pipeline.workflowV2.pegasus;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.pipeline.workflow.BasicWorkflow;
import net.sourceforge.seqware.pipeline.workflowV2.WorkflowClassFinder;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;

/**
 * 
 * @author yongliang
 */
public class PegasusWorkflowEngine extends BasicWorkflow {

    public PegasusWorkflowEngine(Metadata metadata, Map<String, String> config) {
	super(metadata, config);
    }

    private Map<String, String> getVariablesMap(String[] iniFiles,
	    Map<String, String> argMap, String[] extraArgs) {
	Map<String, String> map = null;
	if (argMap == null) {
	    map = new HashMap<String, String>();
	} else {
	    map = argMap;
	}
	// If a config.ini was mentioned, parse it. Otherwise instantiate a new
	// empty hash map
	if (iniFiles != null) {
	    for (int i = 0; i < iniFiles.length; i++) {
		MapTools.ini2Map(iniFiles[i], map);
	    }
	}

	// allow the command line options to override options in the map
	// Parse command line options for additional configuration. Note that we
	// do it last so it takes precedence over the INI
	MapTools.cli2Map(extraArgs, map);
	for (String key : map.keySet()) {
	    Log.debug("KEY AFTER CLI: " + key);
	    if (key != null && map.get(key.toString()) != null) {
		Log.error(" VALUE: " + map.get(key.toString()).toString());
	    } else {
		Log.error(" VALUE: null");
	    }
	    // Log.error(key+"="+map.get(key));
	}

	// Expand variables in the map
	MapTools.mapExpandVariables(map);

	Date date = new Date();
	map.put("date", date.toString());

	Random rand = new Random(System.currentTimeMillis());
	int randInt = rand.nextInt(100000000);
	map.put("random", (new Integer(randInt)).toString());
	return this.resolveMap(map);
    }

    private Map<String, String> resolveMap(Map<String, String> input) {
	Map<String, String> result = new HashMap<String, String>();
	for (Map.Entry<String, String> entry : input.entrySet()) {
	    String value = entry.getValue();
	    if (StringUtils.hasVariable(value)) {
		value = StringUtils.replace(value, input);
	    }
	    result.put(entry.getKey(), value);
	}
	return result;
    }

    @Override
    protected ReturnValue generateDaxFile(WorkflowInfo wi, File dax,
	    String iniFilesStr, Map<String, String> map,
	    List<String> cmdLineOptions) {
	// now create the DAX
	DaxgeneratorV2 daxGen = new DaxgeneratorV2();

	Log.info("Creating DAX in: " + dax.getAbsolutePath());
	Map<String, String> newMap = this.getVariablesMap(iniFilesStr
		.toString().split(","), map, cmdLineOptions
		.toArray(new String[0]));

	String clazzPath = wi.getClassesDir();
	clazzPath = clazzPath.replaceFirst("\\$\\{workflow_bundle_dir\\}",
		wi.getWorkflowDir());
	Log.info("CLASSPATH: " + clazzPath);

	// get user defined classes
	WorkflowClassFinder finder = new WorkflowClassFinder();
	Class<?> clazz = finder.findFirstWorkflowClass(clazzPath);
	Workflow wfObj = new Workflow(newMap);
	if (null != clazz) {
	    try {
		Object object = clazz.newInstance();
		Method m = clazz.getDeclaredMethod("generateWorkflow",
			Workflow.class);
		m.invoke(object, (Object) wfObj);
	    } catch (InstantiationException ex) {
		Log.error(ex);
	    } catch (IllegalAccessException ex) {
		Log.error(ex);
	    } catch (NoSuchMethodException ex) {
		Log.error(ex);
	    } catch (SecurityException ex) {
		Log.error(ex);
	    } catch (IllegalArgumentException ex) {
		Log.error(ex);
	    } catch (InvocationTargetException ex) {
		ex.printStackTrace();
	    }
	}
	Log.error("before generating dax");
	return daxGen.generateDax(wfObj, dax.getAbsolutePath());

    }

    protected Map<String, String> prepareData(WorkflowInfo wi,
	    String workflowRunAccession, ArrayList<String> iniFiles,
	    Map<String, String> preParsedIni, boolean metadataWriteback,
	    ArrayList<String> parentAccessions) {
	Map<String, String> ret = super.prepareData(wi, workflowRunAccession,
		iniFiles, preParsedIni, metadataWriteback, parentAccessions);
	ret.put("workflow_name", wi.getName());
	ret.put("workflow_version", wi.getVersion());
	ret.put("basedir", wi.getBaseDir());
	ret.put("workflow_seqware_version", wi.getWorkflowSqwVersion());
	return ret;
    }
}
