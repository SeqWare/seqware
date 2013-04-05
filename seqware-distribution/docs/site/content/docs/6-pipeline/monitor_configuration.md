---

title:                 "SeqWare Pipeline Monitor Configuration"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

## Overview

You saw in the [Admin Tutorial](/docs/3-getting-started/admin-tutorial/) how to
both launch and monitor workflows that have been scheduled by a user. This
guide provides a bit more details on this process.

In the SeqWare Pipeline system there are two ways to launch workflows. A user
can use WorkflowLauncher to directly launch a workflow provided the workflow
engine is setup on this host (e.g. it is an Oozie or Pegasus submission host).
Alternatively, a user can "schedule" a workflow using WorkflowLauncher through
a web service. Another process can monitor the web service and launch the
workflows that have been scheduled.  On the SeqWare VM/AMI this all happens on
the same box.  But it is possible to have the user scheduling a workflow, the
SeqWare Web Service, and the workflow launcher for SeqWare Pipeline all can
(and likely should) be on seperate servers. This latter method for launching
workflows is the preferred mechansim, especially in a production environment,
since it allows the scheduling of workflows and the running to be decoupled
(different machines, different users, different user roles, etc).

<!-- TODO: a nice figure showing workflow launching -->

The setup and configuration of the Web Service and user command line tools are
covered elsewhere.  Here we detail the needed cron jobs running on the SeqWare
Pipeline host that will query the Web Service, launch scheduled workflows, and
monitor their progress.

## Limitations

One core limitation of SeqWare Pipeline is the lack of a single daemon for
controlling workflow launching and monitoring.  Instead the cooridination of
workflows happens via the SeqWare Web Service.  Each workflow that gets
scheduled by a user is associated with that users Web Service credentials. In
order to launch that scheduled workflow the WorkflowLauncher needs to connect
to the Web Service with the same credentials, find the workflow, and then
launch it on the SeqWare Pipeline box. If workflows are scheduled by multiple
accounts each account needs its own launcher and monitor cron job to
periodically launch and monitor workflows.

Typically this is not a huge limitation since the number of SeqWare accounts
responsible in a production environment is relatively limited. It does make it
more difficult, though, when the number of SeqWare users is high and each must
have their own distinct account to monitor and launch workflows.

## Configuration

Show table showing user launching and host interactions.  In this example the
user "Bob" logs into server1 and connects to the SeqWare Web Service running on
server2 using the login "bob@lab.net". He then schedules a workflow with
WorkflowLauncher and specifies the host to run this workflow on with <tt>--host
server2</tt>.  On server2 another user account, "seqware-bob" runs the daemons
documented below. They connect to the Web Service locally using the same
"bob@lab.net" username and find workflows that have been scheduled to run on
server2. These daemons launch the workflows on server2 and the resulting jobs
run on the cluster connected to server2 as user "seqware-bob", resulting in
files associated with this workflow being owned by the user "seqware-bob".

Host - server1         |               |         |Network |    Host - server2  |                      |
---------------------- | -------------- | ------- |--------| ------------------  | -------------------  |
User Scheduling |  Web Service Account  | Host param |   ->      | User Running  | Web Service Account      |
Bob             |  bob@lab.net          | server2    |   ->      | seqware-bob   |  bob@lab.net             |

<%= render '/includes/monitor_workflows/' %>
 
