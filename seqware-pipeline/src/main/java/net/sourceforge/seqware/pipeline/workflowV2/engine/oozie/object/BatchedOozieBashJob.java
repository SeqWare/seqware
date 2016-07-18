package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import org.jdom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for batching up jobs
 *
 * @author dyuen
 */
class BatchedOozieBashJob extends OozieJob {

    private final List<OozieBashJob> batchedJobs = new ArrayList<>();

    BatchedOozieBashJob(AbstractJob job, String name, String oozie_working_dir, boolean useSge, File seqwareJar,
            String threadsSgeParamFormat, String maxMemorySgeParamFormat, StringTruncator truncator) {
        super(job, name, oozie_working_dir, useSge, seqwareJar, threadsSgeParamFormat, maxMemorySgeParamFormat, truncator);
    }

    @Override
    protected Element createJavaElement() {
        throw new UnsupportedOperationException();
    }

    void attachJob(OozieBashJob job) {
        // mutate longName in order to avoid repeats
        job.setLongName(job.getLongName() + "_" + batchedJobs.size());
        batchedJobs.add(job);
    }

    public int getBatchSize() {
        return batchedJobs.size();
    }

    @Override
    public List<String> getAccessionFile() {
        List<String> list = new ArrayList<>();
        for (OozieJob job : this.batchedJobs) {
            List<String> accessionFile = job.getAccessionFile();
            list.addAll(accessionFile);
        }
        return list;
    }

    protected File emitRunnerScript() {
        File localFile = file(scriptsDir, runnerFileName(this.getLongName()), true);

        ArrayList<String> args = new ArrayList<>();
        for (OozieBashJob batchedJob : batchedJobs) {
            batchedJob.setUseCheckFile(true);
            args.add(concat(" ", batchedJob.generateRunnerLine()));
        }

        final String command = concat("\n", args);
        writeScript(command, localFile);
        final String commentedCommand = "#"+this.getLongName()+"\n" + command;
        archiver.archiveSeqWareMetadataCalls(commentedCommand);
        archiver.archiveWorkflowCommands(commentedCommand);
        return localFile;
    }

}
