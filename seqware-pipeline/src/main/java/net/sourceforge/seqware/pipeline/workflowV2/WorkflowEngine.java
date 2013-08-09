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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.module.ReturnValue;

/**
 * <p>WorkflowEngine interface.</p>
 *
 * @author yongliang
 * @version $Id: $Id
 */
public interface WorkflowEngine {
    /**
     * <p>launchBundle.</p>
     *
     * @param workflow a {@link java.lang.String} object.
     * @param version a {@link java.lang.String} object.
     * @param metadataFile a {@link java.lang.String} object.
     * @param bundle a {@link java.lang.String} object.
     * @param iniFiles a {@link java.util.ArrayList} object.
     * @param metadataWriteback a boolean.
     * @param parentAccessions a {@link java.util.ArrayList} object.
     * @param parentsLinkedToWR a {@link java.util.ArrayList} object.
     * @param wait a boolean.
     * @param cmdLineOptions a {@link java.util.List} object.
     * @param inputFiles the value of inputFiles
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    
    public ReturnValue launchBundle(String workflow, String version, String metadataFile, String bundle, ArrayList<String> iniFiles, boolean metadataWriteback, ArrayList<String> parentAccessions, ArrayList<String> parentsLinkedToWR, boolean wait, List<String> cmdLineOptions, Set<Integer> inputFiles);

    /**
     * <p>launchInstalledBundle.</p>
     *
     * @param workflowAccession a {@link java.lang.String} object.
     * @param workflowRunAccession a {@link java.lang.String} object.
     * @param iniFiles a {@link java.util.ArrayList} object.
     * @param metadataWriteback a boolean.
     * @param parentAccessions a {@link java.util.ArrayList} object.
     * @param parentsLinkedToWR a {@link java.util.ArrayList} object.
     * @param wait a boolean.
     * @param cmdLineOptions a {@link java.util.List} object.
     * @param inputFiles the value of inputFiles
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    
    public ReturnValue launchInstalledBundle(String workflowAccession, String workflowRunAccession, ArrayList<String> iniFiles, boolean metadataWriteback, ArrayList<String> parentAccessions, ArrayList<String> parentsLinkedToWR, boolean wait, List<String> cmdLineOptions, Set<Integer> inputFiles);

    /**
     * <p>launchScheduledBundle.</p>
     *
     * @param workflowAccession a {@link java.lang.String} object.
     * @param workflowRunAccession a {@link java.lang.String} object.
     * @param metadataWriteback a boolean.
     * @param wait a boolean.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    
    public ReturnValue launchScheduledBundle(String workflowAccession, String workflowRunAccession, boolean metadataWriteback, boolean wait);

    /**
     * <p>scheduleInstalledBundle.</p>
     *
     * @param workflowAccession a {@link java.lang.String} object.
     * @param workflowRunAccession a {@link java.lang.String} object.
     * @param iniFiles a {@link java.util.ArrayList} object.
     * @param metadataWriteback a boolean.
     * @param parentAccessions a {@link java.util.ArrayList} object.
     * @param parentsLinkedToWR a {@link java.util.ArrayList} object.
     * @param wait a boolean.
     * @param cmdLineOptions a {@link java.util.List} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public ReturnValue scheduleInstalledBundle(String workflowAccession,
	    String workflowRunAccession, ArrayList<String> iniFiles,
	    boolean metadataWriteback, ArrayList<String> parentAccessions,
	    ArrayList<String> parentsLinkedToWR, boolean wait,
	    List<String> cmdLineOptions);
}
