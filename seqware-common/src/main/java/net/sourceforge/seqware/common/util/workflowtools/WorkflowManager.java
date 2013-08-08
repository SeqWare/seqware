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
package net.sourceforge.seqware.common.util.workflowtools;

import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.*;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;

import org.apache.commons.lang.NotImplementedException;

/**
 * <p>WorkflowManager class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowManager {

  private ReturnValue ret = new ReturnValue();

  // presume iniFiles, parentAccessions, parentsLinkedToWR, template are the
  // full complete paths separated by commas
  /**
   * <p>runWorkflow.</p>
   *
   * @param wi a {@link net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo} object.
   * @param workflowRunAccession a {@link java.lang.String} object.
   * @param iniFilesStr a {@link java.lang.String} object.
   * @param noMetadata a boolean.
   * @param parentAccessionsStr a {@link java.lang.String} object.
   * @param parentsLinkedToWR a {@link java.util.ArrayList} object.
   * @param owner a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public ReturnValue runWorkflow(WorkflowInfo wi, String workflowRunAccession, 
          String iniFilesStr, boolean noMetadata, String parentAccessionsStr, 
          ArrayList<String> parentsLinkedToWR, Registration owner) {

    // keep this id handy
    int accession = 0;

    // Map<String, String> config = ConfigTools.getSettings();

    // will be handed off to the template layer
    HashMap<String, String> map = new HashMap<String, String>();

    if (iniFilesStr != null) {
      String[] lines = iniFilesStr.split("\n");
      for (String line : lines) {
        if (!line.trim().startsWith("#")) {
          String[] splitLine = line.split("=");
          if (splitLine.length >= 2) {
            map.put(splitLine[0].trim(), splitLine[1].trim());
          } // for the case in which we have "parameter=" in the ini file,
          // meaning set it to nothing
          else if (splitLine.length == 1) {
            map.put(splitLine[0].trim(), "");
          }
        }
      }
    }

    map.put("workflow_bundle_dir", wi.getWorkflowDir());
    // starts with assumption of no metadata writeback
    map.put("metadata", "no-metadata");
    map.put("parent_accession", "0");
    map.put("parent_accessions", "0");
    // my new preferred variable name
    map.put("parent-accessions", "0");
    map.put("workflow_run_accession", "0");
    // my new preferred variable name
    map.put("workflow-run-accession", "0");
    // have to pass in the cluster name
    // map.put("seqware_cluster", config.get("SW_CLUSTER"));

    // if we're doing metadata writeback will need to parameterize the workflow
    // correctly

    if (noMetadata) {
      ret.setStdout("Switching to no metadata writeback.");
      Log.info(ret.getStdout());
    } else if (parentAccessionsStr == null || parentsLinkedToWR == null || parentAccessionsStr.trim().isEmpty()
        || parentsLinkedToWR.isEmpty()) {
        if (!noMetadata)
        {
            map.put("metadata", "metadata");
            map.put("workflow_run_accession", workflowRunAccession);
            // my new preferred variable name
            map.put("workflow-run-accession", workflowRunAccession);
        }
        else
        {
            ret.setStdout("No parent accessions. Switching to no metadata writeback.");
        }
      Log.info(ret.getStdout());
    } else {
      // tells the workflow it should save its metadata
      map.put("metadata", "metadata");
      map.put("parent_accession", parentAccessionsStr.toString());
      map.put("parent_accessions", parentAccessionsStr.toString());
      // my new preferred variable name
      map.put("parent-accessions", parentAccessionsStr.toString());

      map.put("workflow_run_accession", workflowRunAccession);
      // my new preferred variable name
      map.put("workflow-run-accession", workflowRunAccession);
    }

    StringBuilder iniBuffer = createINI(map);

    WorkflowRunService wrs = BeanFactory.getWorkflowRunServiceBean();
    WorkflowService ws = BeanFactory.getWorkflowServiceBean();
    if (workflowRunAccession == null) {
      WorkflowRun run = new WorkflowRun();
      run.setWorkflow(ws.findBySWAccession(wi.getWorkflowAccession()));
      run.setName(wi.getName());
      run.setCurrentWorkingDir(wi.getWorkflowDir());
      run.setIniFile(iniBuffer.toString());
      run.setCommand(wi.getCommand());
      run.setTemplate(wi.getTemplatePath());
      run.setStatus(WorkflowRunStatus.submitted);
      run.setOwner(owner);
      
      // FIXME: Deal with workflow Run Params. I am not handling parent files
      // correctly at the moment
      SortedSet<WorkflowRunParam> runParams = new TreeSet<WorkflowRunParam>();
      for (String str : map.keySet()) {

        if (map.get(str) != null) {
          Log.info(str + " " + map.get(str));
          if (str.equals("parent_accessions") || str.equals("parent_accession") || str.equals("parent-accessions")) {
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

            if (str.trim().isEmpty() && map.get(str).trim().isEmpty())
              continue;
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

      Map<String, List<File>> files = new HashMap<String, List<File>>();

      accession = wrs.insert(owner, run, runParams, files);
      if (accession == 0) {
        Log.error("Problem with inserting workflow run");
        ret.setExitStatus(ReturnValue.FAILURE);
        return ret;
      }
      Log.info("SW_ACCESSION: " + workflowRunAccession);

      // need to link all the parents to this workflow run accession
      for (String parentLinkedToWR : parentsLinkedToWR) {
        try {
          linkWorkflowRunAndParent(run, Integer.parseInt(parentLinkedToWR));
        } catch (Exception e) {
          e.printStackTrace();
          ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
          ret.setDescription(e.getMessage());
          return ret;
        }
      }
    } else {// if the workflow_run row exists get the workflow_run_id
      // run = wrs.findBySWAccession(Integer.parseInt(workflowRunAccession));
      // run.setStatus("pending");
      throw new NotImplementedException("Restarted failed runs is not yet implemented in the webservice");
    }

    ret.setReturnValue(accession);
    return (ret);
  }

  /**
   * <p>createINI.</p>
   *
   * @param map a {@link java.util.HashMap} object.
   * @return a {@link java.lang.StringBuilder} object.
   */
  public StringBuilder createINI(HashMap<String, String> map) {
    // after running the daxGen.processTemplate above the map should be filled
    // in with all the ini key/values
    // save this and the DAX to the database
    StringBuilder iniBuffer = new StringBuilder();
    for (String key : map.keySet()) {
      if (key != null) {
        Log.info("KEY: " + key + " VALUE: " + map.get(key.toString()));
        iniBuffer.append(key).append("=").append(map.get(key)).append("\t");
      }
    }
    return iniBuffer;
  }

  /**
   * <p>linkWorkflowRunAndParent.</p>
   *
   * @param wr a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @param parentAccession a int.
   * @return a boolean.
   * @throws java.sql.SQLException if any.
   */
  public boolean linkWorkflowRunAndParent(WorkflowRun wr, int parentAccession) throws SQLException {
    IUSService is = BeanFactory.getIUSServiceBean();
    LaneService ls = BeanFactory.getLaneServiceBean();
    WorkflowRunService wrs = BeanFactory.getWorkflowRunServiceBean();

    IUS ius = is.findBySWAccession(parentAccession);
    Lane lane = ls.findBySWAccession(parentAccession);

    if (ius != null) {
      SortedSet<IUS> iuses = new TreeSet<IUS>();
      iuses.add(ius);
      wr.setIus(iuses);
    } else if (lane != null) {
      SortedSet<Lane> lanes = new TreeSet<Lane>();
      lanes.add(lane);
      wr.setLanes(lanes);
    } else {
      Log.error("ERROR: SW Accession is neither a lane nor an IUS: " + parentAccession);
      return (false);
    }
    wrs.update(wr);

    return (true);
  }
}
