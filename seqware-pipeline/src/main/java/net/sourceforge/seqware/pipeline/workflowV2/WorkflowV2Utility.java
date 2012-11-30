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
package net.sourceforge.seqware.pipeline.workflowV2;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Utility methods that have been refactored out
 *
 * @author dyuen
 */
public class WorkflowV2Utility {
    public static final String WORKFLOW_CLASS = "workflow_class";
    public static final String WORKFLOW_ENGINE = "workflow_engine";
    public static final String WORKFLOW_TYPE = "workflow_type";

    /**
     * Locate and parse metadata information and return a map representation
     *
     * @param bundle
     * @return map if the parse is successful, null if not
     */
    public static Map<String, String> parseMetaInfo(File bundle) {
        final String bundlePath = bundle.getAbsolutePath();
        //parse metadata.xml to Map<String,String>
        @SuppressWarnings("unchecked") //safe to use <File>
        Iterator<File> it = FileUtils.iterateFiles(bundle, new String[]{"xml"}, true);
        if (!it.hasNext()) {
            return null;
        }

        File metadataFile = null;
        while (it.hasNext()) {
            File file = it.next();
            if (file.getName().equals("metadata.xml")) {
                metadataFile = file;
                break;
            }
        }

        if (metadataFile == null) {
            return null;
        }
        return parseMetadataInfo(metadataFile, bundlePath);
    }

    /**
     * Parse the metadata info
     *
     * @param file
     * @param bundleDir
     * @return
     */
    private static Map<String, String> parseMetadataInfo(File file, String bundleDir) {
        Map<String, String> ret = new HashMap<String, String>();
        //parse metadataFile
        SAXBuilder builder = new SAXBuilder();
        try {
            Document document = (Document) builder.build(file);
            Element root = document.getRootElement();
            ret.put("bundle_version", root.getAttributeValue("version"));
            Element wf = root.getChild("workflow");
            ret.put("name", wf.getAttributeValue("name"));
            ret.put("workflow_version", wf.getAttributeValue("version"));
            ret.put("seqware_version", wf.getAttributeValue("seqware_version"));
            ret.put("description", wf.getChildText("description"));
            String basedir = wf.getAttributeValue("basedir");
            if(basedir!=null) {
            	basedir = wf.getAttributeValue("basedir").replaceFirst("\\$\\{workflow_bundle_dir\\}", bundleDir);
            	ret.put("basedir", basedir);
                //parse the workflow_directory_name
                String[] _arr = basedir.split("/");
                if (_arr.length > 2) {
                    String tmp = _arr[1];
                    String[] _arrtmp = tmp.split("_", 3);
                    if (_arrtmp.length == 3) {
                        ret.put("workflow_directory_name", _arrtmp[2]);
                    }
                }
            }
            Element command = wf.getChild("workflow_command");
            if (command != null) {
                ret.put("workflow_command", command.getAttributeValue("command").replaceFirst("\\$\\{workflow_bundle_dir\\}", bundleDir));
            }
            Element template = wf.getChild("workflow_template");
            if (template != null) {
                ret.put("workflow_template", template.getAttributeValue("path").replaceFirst("\\$\\{workflow_bundle_dir\\}", bundleDir));
            }
            Element java_class = wf.getChild(WORKFLOW_CLASS);
            if (java_class != null) {
                ret.put(WORKFLOW_CLASS, template.getAttributeValue("path").replaceFirst("\\$\\{workflow_bundle_dir\\}", bundleDir));
            }
            Element config = wf.getChild("config");
            if (config != null) {
                ret.put("config", config.getAttributeValue("path").replaceFirst("\\$\\{workflow_bundle_dir\\}", bundleDir));
            }
            Element classes = wf.getChild("classes");
            if (classes != null) {
                ret.put("classes", classes.getAttributeValue("path").replaceFirst("\\$\\{workflow_bundle_dir\\}", bundleDir));
            }
            Element build = wf.getChild("build");
            if (build != null) {
                ret.put("build", build.getAttributeValue("command").replaceFirst("\\$\\{workflow_bundle_dir\\}", bundleDir));
            }
            Element requirements = wf.getChild("requirements");
            if (requirements != null) {
                ret.put("compute", requirements.getAttributeValue("compute"));
                ret.put("memory", requirements.getAttributeValue("memory"));
                ret.put("network", requirements.getAttributeValue("network"));
                ret.put(WORKFLOW_ENGINE, requirements.getAttributeValue(WORKFLOW_ENGINE));
                ret.put(WORKFLOW_TYPE, requirements.getAttributeValue(WORKFLOW_TYPE));
            }
        } catch (JDOMException e) {
            Log.error("Error parsing metadata.xml", e);
        } catch (IOException e) {
            Log.error("IO Error parsing metadata.xml", e);
        }
        return ret;
    }

    /**
     * Determine the bundle path from the provided options
     *
     * @return bundlePath
     */
    public static String determineRelativeBundlePath(OptionSet options) {
        String bundlePath;
        // get bundle path
        if (options.has("bundle")) {
            bundlePath = (String) options.valueOf("bundle");
        } else {
            bundlePath = (String) options.valueOf("provisioned-bundle-dir");
        }
        return bundlePath;
    }
}
