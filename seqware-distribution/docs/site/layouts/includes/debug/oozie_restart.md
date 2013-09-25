If a workflow has failed due to a transient error (such as cluster downtime or a disk quota being reached), you can restart a workflow at the last failed step.

    $ seqware workflow-run retry --accession 28

Alternately, you can retry workflow runs directly in HUE (this will also give more fine-grained control over which jobs should be rerun).  Note that status checking is normally skipped for failed/cancelled runs, thus SeqWare will need to be informed that the run has been retried externally:

    $ seqware workflow-run propagate-statuses --accession 28

As with the cancel case, the status is first set to `submitted_retry`, and after the next status propagation will be set to `running` (or whatever status is appropriate).