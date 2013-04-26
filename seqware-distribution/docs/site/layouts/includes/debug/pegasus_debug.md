WorkflowLauncher prints the <tt>pegasus-status</tt> command used to monitor the running workflow.  For example:

	pegasus-status -l /home/seqware/SeqWare/pegasus-dax/seqware/pegasus/seqware-archetype-java-workflow/run0126

This directory shown is actually the logging directory so, if anything goes wrong with the workflow, information
will be written here.  Look for the <tt>*.out.00*</tt> files which contain the stderr and stdout logs.

Also, the WorkflowLauncher tool is capable of printing the stderr/stdout of failed jobs within the launched workflow.
