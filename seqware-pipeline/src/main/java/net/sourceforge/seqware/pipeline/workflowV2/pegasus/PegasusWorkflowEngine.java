package net.sourceforge.seqware.pipeline.workflowV2.pegasus;

import java.io.File;
import java.io.IOException;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowEngine;

public class PegasusWorkflowEngine extends AbstractWorkflowEngine {

	@Override
	public ReturnValue launchWorkflow(AbstractWorkflowDataModel objectModel) {
		//parse objectmodel 
		ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
		this.parseDataModel(objectModel);
		return ret;
	}

	private ReturnValue parseDataModel(AbstractWorkflowDataModel objectModel) {
		ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
		// now create the DAX
		File dax;

		try {
		    dax = FileTools.createFileWithUniqueName(new File("/tmp"), "dax");
		} catch (IOException e) {
		    e.printStackTrace();
		    ret.setExitStatus(ReturnValue.FAILURE);
		    ret.setStderr("Can't write DAX file! " + e.getMessage());
		    return (ret);
		}

		Log.stdout("CREATING DAX IN: " + dax.getAbsolutePath());
		
		//generate dax
		DaxgeneratorV2 daxv2 = new DaxgeneratorV2();
		daxv2.generateDax(objectModel, dax.getAbsolutePath());
		return ret;
	}
}
