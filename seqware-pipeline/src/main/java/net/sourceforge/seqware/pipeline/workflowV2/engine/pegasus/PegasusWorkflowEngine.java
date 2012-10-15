package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.runtools.RunTools;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowTools;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowEngine;

public class PegasusWorkflowEngine extends AbstractWorkflowEngine {

	@Override
	public ReturnValue launchWorkflow(AbstractWorkflowDataModel objectModel) {
		//parse objectmodel 
		ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
		File dax = this.parseDataModel(objectModel);
		ret = this.runWorkflow(objectModel, dax);
		return ret;
	}

	private File parseDataModel(AbstractWorkflowDataModel objectModel) {
		ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

		File dax;
		try {
		    dax = FileTools.createFileWithUniqueName(new File("/tmp"), "dax");
		} catch (IOException e) {
		    e.printStackTrace();
		    ret.setExitStatus(ReturnValue.FAILURE);
		    ret.setStderr("Can't write DAX file! " + e.getMessage());
		    return (null);
		}

		Log.stdout("CREATING DAX IN: " + dax.getAbsolutePath());
		
		//generate dax
		DaxgeneratorV2 daxv2 = new DaxgeneratorV2();
		daxv2.generateDax(objectModel, dax.getAbsolutePath());
		return dax;
	}
	

	
	private ReturnValue runWorkflow(AbstractWorkflowDataModel objectModel, File dax) {
		ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
		
		// create the submission of the DAX to Pegasus
		String pegasusCmd = "pegasus-plan -Dpegasus.user.properties="
				+ objectModel.getEnv().getPegasusConfigDir() + "/properties --dax "
			+ dax.getAbsolutePath() + " --dir " + objectModel.getEnv().getDaxDir()
			+ " -o " + objectModel.getEnv().getSwCluster() + " --force --submit -s "
			+ objectModel.getEnv().getSwCluster();

		// run the pegasus submission
		Log.stdout("SUBMITTING TO PEGASUS: " + pegasusCmd);
		ArrayList<String> theCommand = new ArrayList<String>();
		theCommand.add("bash");
		theCommand.add("-lc");
		theCommand.add(pegasusCmd);
		ReturnValue retPegasus = RunTools.runCommand(theCommand
			.toArray(new String[0]));

		// figure out the status command
		String stdOut = retPegasus.getStdout();
		Pattern p = Pattern.compile("(pegasus-status -l \\S+)");
		Matcher m = p.matcher(stdOut);
		String statusCmd = null;
		if (m.find()) {
		    statusCmd = m.group(1);
		}

		// look for the status directory
		p = Pattern.compile("pegasus-status -l (\\S+)");
		m = p.matcher(stdOut);
		String statusDir = null;
		if (m.find()) {
		    statusDir = m.group(1);
		}
		
		Log.stdout("PEGASUS STATUS COMMAND: " + statusCmd);
		if (retPegasus.getProcessExitStatus() != ReturnValue.SUCCESS
				|| statusCmd == null) {
			return retPegasus;
		}

			// if the user passes in --wait then hang around until the workflow
			// finishes or fails
			// periodically checking the status in a robust way
		boolean success = true;
		if (objectModel.isWait()) {
			success = false;

			// now parse out the return status from the pegasus tool
			ReturnValue watchedResult = null;
			if (statusCmd != null && statusDir != null) {
				WorkflowTools workflowTools = new WorkflowTools();			
				watchedResult = workflowTools.watchWorkflow(statusCmd,
					statusDir);
			    }

			    if (watchedResult.getExitStatus() == ReturnValue.SUCCESS) {
				success = true;
				
			    if (watchedResult.getExitStatus() == ReturnValue.FAILURE) {
					Log.error("ERROR: problems watching workflow");
					// need to save back to the DB if watching
					
					ret.setExitStatus(ReturnValue.FAILURE);
					return (ret);
			    }
			}

			if (retPegasus.getExitStatus() != ReturnValue.SUCCESS || !success) {
			    Log.error("ERROR: failure with running the pegasus command");
			    // I previously saved this state to the DB so no need to do that
			    // here
			    ret.setExitStatus(ReturnValue.FAILURE);
			    return (ret);
			}
		}

		return retPegasus;

	}
	
    /**
     * This method just needs a sw_accession value from the workflow table and
     * an ini file(s) in order to schedule a workflow. All needed info is pulled
     * from the workflow table which was populated when the workflow was
     * installed. Keep in mind this does not actually trigger anything, it just
     * schedules the workflow to run by adding to the workflow_run table. This
     * lets you run workflows on a different host from where this command line
     * tool is run but requires an external process to launch workflows that
     * have been scheduled.
     * 
     * @param workflowAccession
     * @return
     */
    private ReturnValue scheduleInstalledBundle(String workflowAccession,
	    String workflowRunAccession, ArrayList<String> iniFiles,
	    boolean metadataWriteback, ArrayList<String> parentAccessions,
	    ArrayList<String> parentsLinkedToWR, boolean wait,
	    List<String> cmdLineOptions) {

		ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
/*	
		Map<String, String> workflowMetadata = this.metadata
			.get_workflow_info(Integer.parseInt(workflowAccession));
		WorkflowInfo wi = parseWorkflowMetadata(workflowMetadata);
		scheduleWorkflow(wi, workflowRunAccession, iniFiles, metadataWriteback,
			parentAccessions, parentsLinkedToWR, wait, cmdLineOptions);
	*/
		return (ret);
    }
}
