---

title:                 "Debugging, Troubleshooting, & Restarting Workflow"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

## Overview

In the future we intend on completely hiding the debugging and restarting of
workflows behind SeqWare command line tools. But for the time being debugging
and restart directions are tied to the particular Workflow Engine used.

## The Pegasus Engine

The Pegasus Engine uses <tt>pegasus-status</tt> as the primary command for monitoring the status of workflows.

### Debugging Workflows

<%= render '/includes/debug/pegasus_debug/' %>

### Restarting Workflows 

<%= render '/includes/debug/pegasus_restart/' %>

## The Oozie Engine

The Oozie Engine has both command line tools and a web interface for interacting with workflow status information.
The web interface is particularlyly helpful, you can find it typically at:

	http://<host>:11000/oozie/

### Debugging Workflows

Navigate to the Oozie console at the URL above. Recent workflows and their status can be found here. Click on the 
workflow for more information including stderr/stdout for the jobs.

### Restarting Workflows 

While Oozie does support workflow re-submission the recommened approach for failed workflows in SeqWare is simply to 
resubmit the workflows.
