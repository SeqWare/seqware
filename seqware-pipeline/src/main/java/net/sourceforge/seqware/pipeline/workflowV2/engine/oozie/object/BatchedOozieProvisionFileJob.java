package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import org.jdom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for batching up provision file jobs
 *
 * @author dyuen
 */
class BatchedOozieProvisionFileJob extends OozieJob {

    private final List<OozieProvisionFileJob> provisionJobs = new ArrayList<>();

    BatchedOozieProvisionFileJob(AbstractJob job, String name, String oozie_working_dir, boolean useSge, File seqwareJar,
            String threadsSgeParamFormat, String maxMemorySgeParamFormat, StringTruncator truncator) {
        super(job, name, oozie_working_dir, useSge, seqwareJar, threadsSgeParamFormat, maxMemorySgeParamFormat, truncator);
    }

    @Override
    public boolean addParentAccessionFile(String... pafs) {
        super.addParentAccessionFile(pafs);
        boolean added = false;
        // also propagate to provision jobs
        for(OozieProvisionFileJob job : provisionJobs){
            if (job.addParentAccessionFile(pafs)){
                added = true;
            }
        }
        return added;
    }

    @Override
    protected Element createJavaElement() {
        throw new UnsupportedOperationException();
    }

    void attachProvisionFileJob(OozieProvisionFileJob job) {
        // mutate longName in order to avoid repeats
        job.setLongName(job.getLongName() + "_" + provisionJobs.size());
        provisionJobs.add(job);
    }

    int getBatchSize() {
        return provisionJobs.size();
    }

    protected File emitRunnerScript() {
        File localFile = file(scriptsDir, runnerFileName(this.getLongName()), true);

        List<String> args = new ArrayList<>();
        for (OozieProvisionFileJob batchedJob : provisionJobs) {
            batchedJob.setUseCheckFile(true);
            args.add(concat(" ", batchedJob.generateRunnerLine()));
        }

        final String command = concat("\n", args);
        writeScript(command, localFile);

        // create archive of commands with metadata calls
        final String commentedCommand = "#"+this.getLongName()+"\n" + command;
        archiver.archiveSeqWareMetadataCalls(commentedCommand);

        // create command version without metadata calls
        this.turnOffMetadata();

        List<String> argsWithoutMetadata = new ArrayList<>();
        for (OozieProvisionFileJob batchedJob : provisionJobs) {
            batchedJob.turnOffMetadata();
            batchedJob.setUseCheckFile(true);
            argsWithoutMetadata.add(concat(" ", batchedJob.generateRunnerLine()));
        }
        final String commandWithoutMetadata = concat("\n", argsWithoutMetadata);
        final String commentedCommandWithoutMetadata = "#"+this.getLongName()+"\n" + commandWithoutMetadata;
        archiver.archiveWorkflowCommands(commentedCommandWithoutMetadata);
        return localFile;
    }

    @Override
    public List<String> getAccessionFile() {
        List<String> list = new ArrayList<>();
        for (OozieProvisionFileJob job : this.provisionJobs) {
            List<String> accessionFile = job.getAccessionFile();
            list.addAll(accessionFile);
        }
        return list;
    }
}
