/*
 * Copyright (C) 2015 SeqWare
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
package io.seqware.pipeline.engines.whitestar;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.metadata.MetadataInMemory;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.SortedSet;
import java.util.TreeSet;

import static net.sourceforge.seqware.common.util.Rethrow.rethrow;

/**
 * This is a KISS implementation of persistence for WhiteStar relying upon JSON text files in the working directory.
 *
 * @author dyuen
 */
public class Persistence {

    public static final String PERSISTENT_DIR = "whitestar";
    public static final String WORKFLOW_RUN_FILENAME = "workflowRun.json";
    public static final String WORKFLOW_FILENAME = "workflow.json";
    public static final String STATE_FILENAME = "state.json";
    private final File persistDir;

    /**
     *
     * @param nfsWorkDir
     */
    public Persistence(File nfsWorkDir) {
        if (!nfsWorkDir.exists()) {
            throw new RuntimeException("Unable to locate working directory");
        }
        this.persistDir = new File(nfsWorkDir, Persistence.PERSISTENT_DIR);
        boolean dircreated = persistDir.exists() || persistDir.mkdir();
        boolean writeable = persistDir.setWritable(true, false);
        if (!dircreated || !writeable) {
            throw new RuntimeException("Unable to write to working directory");
        }
    }

    public synchronized SortedSet<String> readCompletedJobs() {
        try {
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
            String statesString = FileUtils.readFileToString(new File(persistDir, STATE_FILENAME), StandardCharsets.UTF_8);
            Type collectionType = new TypeToken<SortedSet<String>>() {
            }.getType();
            SortedSet<String> states = gson.fromJson(statesString, collectionType);
            return states;
        } catch (IOException ex) {
            Log.stdoutWithTime("Unable to read workflowrun state");
            rethrow(ex);
        }
        return null;
    }

    public synchronized WorkflowRun readWorkflowRun() {
        try {
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
            String workflowRunString = FileUtils.readFileToString(new File(persistDir, WORKFLOW_RUN_FILENAME),StandardCharsets.UTF_8);
            String workflowString = FileUtils.readFileToString(new File(persistDir, WORKFLOW_FILENAME),StandardCharsets.UTF_8);
            WorkflowRun workflowRun = gson.fromJson(workflowRunString, WorkflowRun.class);
            Workflow workflow = gson.fromJson(workflowString, Workflow.class);
            workflowRun.setWorkflow(workflow);
            SortedSet<WorkflowRun> runs = new TreeSet<>();
            runs.add(workflowRun);
            workflow.setWorkflowRuns(runs);
            // load into persistence store if needed
            Metadata metadata = MetadataFactory.get(ConfigTools.getSettings());
            if (metadata instanceof MetadataInMemory) {
                MetadataInMemory mim = (MetadataInMemory) metadata;
                mim.loadEntity(workflow);
                mim.loadEntity(workflowRun);
            }
            return workflowRun;
        } catch (IOException ex) {
            Log.stdoutWithTime("Unable to read workflowrun state");
            rethrow(ex);
        }
        return null;
    }

    public synchronized void persistState(int swid, SortedSet<String> completedJobs) {
        try {
            // write state of workflow run
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
            Metadata ws = MetadataFactory.get(ConfigTools.getSettings());
            WorkflowRun workflowRun = ws.getWorkflowRunWithWorkflow(String.valueOf(swid));
            Workflow workflow = workflowRun.getWorkflow();
            workflowRun.setWorkflow(null);
            workflow.setWorkflowRuns(null);
            if (workflow.getWorkflowParams() != null) {
                for (WorkflowParam param : workflow.getWorkflowParams()) {
                    param.setWorkflow(null);
                }
            }
            // ugly, need to avoid circular reference before serialization
            FileUtils.write(new File(persistDir, WORKFLOW_RUN_FILENAME), gson.toJson(workflowRun),StandardCharsets.UTF_8);
            FileUtils.write(new File(persistDir, WORKFLOW_FILENAME), gson.toJson(workflow),StandardCharsets.UTF_8);
            FileUtils.write(new File(persistDir, STATE_FILENAME), gson.toJson(completedJobs),StandardCharsets.UTF_8);
            workflowRun.setWorkflow(workflow);
            SortedSet<WorkflowRun> set = new TreeSet<>();
            set.add(workflowRun);
            workflow.setWorkflowRuns(set);
            if (workflow.getWorkflowParams() != null) {
                for (WorkflowParam param : workflow.getWorkflowParams()) {
                    param.setWorkflow(workflow);
                }
            }
        } catch (IOException ex) {
            Log.stdoutWithTime("Unable to write workflowrun state");
            rethrow(ex);
        }
    }
}
