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

import net.sourceforge.seqware.common.module.ReturnValue;

/**
 * 
 * @author yongliang
 */
public interface WorkflowEngine {
    public ReturnValue launchBundle(String workflow, String version,
	    String metadataFile, String bundle, ArrayList<String> iniFiles,
	    boolean metadataWriteback, ArrayList<String> parentAccessions,
	    ArrayList<String> parentsLinkedToWR, boolean wait,
	    List<String> cmdLineOptions);

    public ReturnValue launchInstalledBundle(String workflowAccession,
	    String workflowRunAccession, ArrayList<String> iniFiles,
	    boolean metadataWriteback, ArrayList<String> parentAccessions,
	    ArrayList<String> parentsLinkedToWR, boolean wait,
	    List<String> cmdLineOptions);

    public ReturnValue launchScheduledBundle(String workflowAccession,
	    String workflowRunAccession, boolean metadataWriteback, boolean wait);

    public ReturnValue scheduleInstalledBundle(String workflowAccession,
	    String workflowRunAccession, ArrayList<String> iniFiles,
	    boolean metadataWriteback, ArrayList<String> parentAccessions,
	    ArrayList<String> parentsLinkedToWR, boolean wait,
	    List<String> cmdLineOptions);
}
