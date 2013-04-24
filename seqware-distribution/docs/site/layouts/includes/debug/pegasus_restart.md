If a workflow has failed due to a transient error (such as cluster downtime or a disk quota being reached), you can restart a workflow at the last failed step.

        cd /home/seqware/pegasus-dax/seqware/pegasus/seqware-archetype-java-workflow/run0126
        pegasus-run -Dpegasus.user.properties=pegasus.*.properties --nodatabase `pwd`
        Rescued /home/seqware/pegasus-dax/seqware/pegasus/seqware-archetype-java-workflow/run0126/seqware-archetype-java-workflow-0.log as /home/seqware/pegasus-dax/seqware/pegasus/seqware-archetype-java-workflow/run0126/seqware-archetype-java-workflow-0.log.000

        Running rescue DAG 1
        -----------------------------------------------------------------------
        File for submitting this DAG to Condor           : seqware-archetype-java-workflow-0.dag.condor.sub
        Log of DAGMan debugging messages                 : seqware-archetype-java-workflow-0.dag.dagman.out
        Log of Condor library output                     : seqware-archetype-java-workflow-0.dag.lib.out
        Log of Condor library error messages             : seqware-archetype-java-workflow-0.dag.lib.err
        Log of the life of condor_dagman itself          : seqware-archetype-java-workflow-0.dag.dagman.log

        -no_submit given, not submitting DAG to Condor.  You can do this with:
        "condor_submit seqware-archetype-java-workflow-0.dag.condor.sub"
        -----------------------------------------------------------------------
        Submitting job(s).
        1 job(s) submitted to cluster 2851.

        Your Workflow has been started and runs in base directory given below

        cd /home/seqware/pegasus-dax/seqware/pegasus/seqware-archetype-java-workflow/run0126

        *** To monitor the workflow you can run ***

        pegasus-status -l /home/seqware/pegasus-dax/seqware/pegasus/seqware-archetype-java-workflow/run0126

        *** To remove your workflow run ***
        pegasus-remove -d 2848.0
        or
        pegasus-remove /home/seqware/pegasus-dax/seqware/pegasus/seqware-archetype-java-workflow/run0126

        [seqware@seqwarevm run0126]$ condor_q


        -- Submitter: seqwarevm : <10.0.2.15:57652> : seqwarevm
         ID      OWNER            SUBMITTED     RUN_TIME ST PRI SIZE CMD
        2851.0   seqware         3/28 11:31   0+00:00:05 R  0   0.3  condor_dagman -f -
	1 jobs; 0 completed, 0 removed, 0 idle, 1 running, 0 held, 0 suspended

You will also need to have the workflow status checker reset the status of the workflow_run via a command like:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowStatusChecker -- --wra 525814 --check-failed
