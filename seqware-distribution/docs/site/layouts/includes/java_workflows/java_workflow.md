You can see the full Java workflow source code by looking at [Workflow
Examples](/docs/15-workflow-examples/) or, in this case, just the
<tt>src/main/java/com/github/seqware/WorkflowClient.java</tt> file produced by the Maven Archetype above.

This Java class is pretty simple in its construction. It is used to define
input and output files along with the individual steps in the workflow and how
they relate to each other.  It is used to create a workflow object model which
is then handed of to a workflow engine that knows how to turn that into a
directed acyclic graph of jobs that can run on a cluster (local VM, an HPC
cluster, a cloud-based cluster, etc).


