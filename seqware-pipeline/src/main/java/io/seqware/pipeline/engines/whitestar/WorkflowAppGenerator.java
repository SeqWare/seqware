package io.seqware.pipeline.engines.whitestar;

import java.io.File;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object.WorkflowApp;
import org.apache.hadoop.fs.Path;

public class WorkflowAppGenerator {
    /**
     * generate WorkflowApp from the object model
     * 
     * @param wfdm
     * @param output
     * @param nfsWorkDir
     * @param hdfsWorkDir
     * @param useSge
     * @param seqwareJar
     * @param maxMemorySgeParamFormat
     * @param threadsSgeParamFormat
     * @return
     */
    public WorkflowApp generateWorkflowXml(AbstractWorkflowDataModel wfdm, String output, String nfsWorkDir, Path hdfsWorkDir,
            boolean useSge, File seqwareJar, String threadsSgeParamFormat, String maxMemorySgeParamFormat) {
        return new WorkflowApp(wfdm, nfsWorkDir, hdfsWorkDir, useSge, seqwareJar, threadsSgeParamFormat, maxMemorySgeParamFormat);
    }
}
