/*
 * Copyright (C) 2014 SeqWare
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

package io.seqware.pipeline.api;

import io.seqware.Engines;
import io.seqware.pipeline.SqwKeys;
import io.seqware.pipeline.engines.whitestar.WhiteStarWorkflowEngine;
import java.util.Map;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.OozieWorkflowEngine;
import net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object.OozieJob;

/**
 * This class contains tools for interacting with WorkflowEngines in an abstract manner.
 * 
 * @author dyuen
 */
public class WorkflowTools {

    /**
     * 
     * @param dataModel
     * @param config
     * @param createDirectories
     *            when constructing the engine to launch a job, we need to create a NFS and HDFS directory
     * @return
     */
    public static WorkflowEngine getWorkflowEngine(AbstractWorkflowDataModel dataModel, Map<String, String> config,
            boolean createDirectories) {
        WorkflowEngine wfEngine = null;
        String engine = dataModel.getWorkflow_engine();
        if (engine == null || engine.equalsIgnoreCase("pegasus")) {
            throw new RuntimeException("Pegasus workflow engine is no longer supported");
        } else if (engine.equalsIgnoreCase("oozie")) {
            wfEngine = new OozieWorkflowEngine(dataModel, false, null, null, createDirectories);
        } else if (engine.equalsIgnoreCase("oozie-sge")) {
            String threadsSgeParamFormat = config.get(SqwKeys.OOZIE_SGE_THREADS_PARAM_FORMAT.getSettingKey());
            String maxMemorySgeParamFormat = config.get(SqwKeys.OOZIE_SGE_MAX_MEMORY_PARAM_FORMAT.getSettingKey());
            if (threadsSgeParamFormat == null) {
                System.err.println("WARNING: No entry in settings for " + SqwKeys.OOZIE_SGE_THREADS_PARAM_FORMAT.getSettingKey()
                        + ", omitting threads option from qsub. Fix by providing the format of qsub threads option, using the '"
                        + OozieJob.SGE_THREADS_PARAM_VARIABLE + "' variable.");
            }
            if (maxMemorySgeParamFormat == null) {
                System.err.println("WARNING: No entry in settings for " + SqwKeys.OOZIE_SGE_MAX_MEMORY_PARAM_FORMAT.getSettingKey()
                        + ", omitting max-memory option from qsub. Fix by providing the format of qsub max-memory option, using the '"
                        + OozieJob.SGE_MAX_MEMORY_PARAM_VARIABLE + "' variable.");
            }
            wfEngine = new OozieWorkflowEngine(dataModel, true, threadsSgeParamFormat, maxMemorySgeParamFormat, createDirectories);
        } else if (Engines.isWhiteStar(engine)) {
            String threadsSgeParamFormat = config.get(SqwKeys.OOZIE_SGE_THREADS_PARAM_FORMAT.getSettingKey());
            String maxMemorySgeParamFormat = config.get(SqwKeys.OOZIE_SGE_MAX_MEMORY_PARAM_FORMAT.getSettingKey());
            if (threadsSgeParamFormat == null) {
                System.err.println("WARNING: No entry in settings for " + SqwKeys.OOZIE_SGE_THREADS_PARAM_FORMAT.getSettingKey()
                        + ", omitting threads option from qsub. Fix by providing the format of qsub threads option, using the '"
                        + OozieJob.SGE_THREADS_PARAM_VARIABLE + "' variable.");
            }
            if (maxMemorySgeParamFormat == null) {
                System.err.println("WARNING: No entry in settings for " + SqwKeys.OOZIE_SGE_MAX_MEMORY_PARAM_FORMAT.getSettingKey()
                        + ", omitting max-memory option from qsub. Fix by providing the format of qsub max-memory option, using the '"
                        + OozieJob.SGE_MAX_MEMORY_PARAM_VARIABLE + "' variable.");
            }
            return new WhiteStarWorkflowEngine(dataModel, engine.contains("sge"), threadsSgeParamFormat, maxMemorySgeParamFormat,
                    createDirectories);
        } else {
            throw new IllegalArgumentException("Unknown workflow engine: " + engine);
        }
        return wfEngine;
    }
}
