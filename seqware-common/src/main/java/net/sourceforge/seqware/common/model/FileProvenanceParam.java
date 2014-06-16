package net.sourceforge.seqware.common.model;

public enum FileProvenanceParam {
    study, experiment, sample, root_sample("root-sample"), organism, processing, sample_ancestor("sample-ancestor"), sequencer_run(
            "sequencer-run"), lane, ius, workflow, workflow_run("workflow-run"), workflow_run_status("workflow-run-status"), file, file_meta_type(
            "file-meta-type"), skip;

    private final String str;

    private FileProvenanceParam() {
        this.str = null;
    }

    private FileProvenanceParam(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        if (str == null) return super.toString();
        return str;
    }
}
