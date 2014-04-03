package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JobBatch extends AbstractJob {

    private List<Job> jobList = new ArrayList<>();

    public JobBatch(String algo) {
        super(algo);
    }

    public Job createBashJob(String algo) {
        AbstractJob job = new BashJob(algo);
        this.jobList.add(job);
        return job;
    }

    @Override
    public Command setCommand(String cmd) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Command getCommand() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractJob setThreads(int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getThreads() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Job setQueue(String queue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHasMetadataWriteback(boolean metadata) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasMetadataWriteback() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setParentAccessions(Collection<String> parentAccessions) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLocal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLocal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLocal(boolean runLocal) {
        throw new UnsupportedOperationException();
    }

    public List<Job> getJobList() {
        return jobList;
    }
}
