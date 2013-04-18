---

title:                 "Getting Started"
markdown:              advanced
toc_includes_sections: true

---

The majority of this guide is dedicated to walking users and developers through the basics of calling and developing SeqWare workflows respectively. We assume that people are most interested in the SeqWare Pipeline sub-project and focus most of our time on that.  The examples below will all be based on a local VM but the environment on our cloud instance is almost identical, so most of the examples below will be applicable for both environments.

## By the End of These Tutorials

<!--
Take a look at the "Workflow Development Tutorials" on the internal wiki to make sure we've covered all items in the tutorials below.
-->

By the end of these tutorials you will:

* create studies, experiments, and samples in the MetaDB
* run a HelloWorld workflow in Pipeline
* create a new workflow bundle in Pipeline
* install and schedule your new workflow bundle in Pipeline and MetaDB
* generate a report on the outputs of your workflows in Pipeline and Portal
* get an overview of the administration process for SeqWare
* be prepared to move on to more detailed documentation for each sub-project

Please launch your local VM and log in as <kbd>seqware</kbd> at this time. If
you do not have a local VM or Amazon instance currently running please see the
[Installation Guide](/docs/2-installation/) before following these tutorials.
We expect users will want to follow these tutorials in the order presented
below:

## User Tutorial

The first step is to follow the [User
Tutorial](/docs/3-getting-started/user-tutorial/) which will explain how you
use the SeqWare system to launch workflows and analyze data.

## Developer Tutorial

The [Developer Tutorial](/docs/3-getting-started/developer-tutorial/) picks up
where the User Tutorial left off and shows you how to create new workflow
bundles and make those available to other users of the system.

## Administrator Tutorial

Finally, the last tutorial is the [Admin
Tutorial](/docs/3-getting-started/admin-tutorial/) which gives a general
overview of how the various SeqWare pieces fit together to automate NGS
analysis within a large institution like [OICR](http://oicr.on.ca).
