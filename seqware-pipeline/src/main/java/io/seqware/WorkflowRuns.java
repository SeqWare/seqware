package io.seqware;

import io.seqware.common.model.WorkflowRunStatus;
import static io.seqware.common.model.WorkflowRunStatus.pending;
import static io.seqware.common.model.WorkflowRunStatus.running;
import static io.seqware.common.model.WorkflowRunStatus.submitted;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

public class WorkflowRuns {

    public static void submitCancel(int... workflowRunAccessions) {
        // Man, I really would like lambda expressions or something to kill this duplicate code
        ExecutorService pool = Executors.newFixedThreadPool(Math.min(10, workflowRunAccessions.length));
        List<Future<?>> futures = new ArrayList<>(workflowRunAccessions.length);
        for (int workflowRunAccession : workflowRunAccessions) {
            final int workflowRunAccessionCopy = workflowRunAccession;
            futures.add(pool.submit(new Runnable() {
                @Override
                public void run() {
                    cancelWorkflowRun(workflowRunAccessionCopy);
                }
            }));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                Log.fatal(ex);
            }
        }
        pool.shutdown();
    }

    private static void cancelWorkflowRun(int workflowRunAccession) throws UnsupportedOperationException {
        Metadata md = MetadataFactory.get(ConfigTools.getSettings());
        WorkflowRun wr = md.getWorkflowRun(workflowRunAccession);
        if (Engines.supportsCancel(wr.getWorkflowEngine())) {
            switch (wr.getStatus()) {
            case submitted:
            case pending:
            case running:
                wr.setStatus(WorkflowRunStatus.submitted_cancel);
                md.updateWorkflowRun(wr);
            default: // do nothing
            }
        } else {
            throw new UnsupportedOperationException("Workflow run cancellation not supported for engine: " + wr.getWorkflowEngine());
        }
    }

    public static void failWorkflowRuns(int... workflowRunAccessions) {
        ExecutorService pool = Executors.newFixedThreadPool(Math.min(10, workflowRunAccessions.length));
        List<Future<?>> futures = new ArrayList<>(workflowRunAccessions.length);
        for (int workflowRunAccession : workflowRunAccessions) {
            final int workflowRunAccessionCopy = workflowRunAccession;
            futures.add(pool.submit(new Runnable() {
                @Override
                public void run() {
                    failWorkflowRun(workflowRunAccessionCopy);
                }
            }));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                Log.fatal(ex);
            }
        }
        pool.shutdown();
    }

    private static void failWorkflowRun(int workflowRunAccession) {
        Metadata md = MetadataFactory.get(ConfigTools.getSettings());
        WorkflowRun wr = md.getWorkflowRun(workflowRunAccession);
        wr.setStatus(WorkflowRunStatus.failed);
        md.updateWorkflowRun(wr);
    }

    public static void submitRetry(int... workflowRunAccessions) {
        for (int workflowRunAccession : workflowRunAccessions) {
            Metadata md = MetadataFactory.get(ConfigTools.getSettings());
            WorkflowRun wr = md.getWorkflowRun(workflowRunAccession);
            if (Engines.supportsRetry(wr.getWorkflowEngine())) {
                switch (wr.getStatus()) {
                case failed:
                case cancelled:
                    wr.setStatus(WorkflowRunStatus.submitted_retry);
                    md.updateWorkflowRun(wr);
                default: // do nothing
                }
            } else {
                throw new UnsupportedOperationException("Workflow run retrying not supported for engine: " + wr.getWorkflowEngine());
            }
        }
    }

    public static String workflowRunIni(int workflowRunAccession) {
        Metadata md = MetadataFactory.get(ConfigTools.getSettings());
        WorkflowRun workflowRun = md.getWorkflowRun(workflowRunAccession);
        return workflowRun.getIniFile();
    }

}
