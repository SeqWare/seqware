---

title:                 "Admin Tutorial"
markdown:              advanced
toc_includes_sections: true

---

**This guide is a work in progress.**
This guide will, in the near future, focus on how to setup SeqWare at your site or on the cloud.
It focuses on what you need to do to get “real” work done e.g. to run workflows you create on
datasets that require multiple nodes to analyze the data in a reasonable amount of time.
There are basically two approaches for this, connect the VirtualBox VM to a cluster at your
local site or to launch a full SeqWare cluster on EC2 using Starcluster. Either of these
approaches will leave you with a system that can process large amounts of data. This guide
assumes you are an IT admin at your site or are working with an admin since some of the
steps will require “root” privileges.

## By the End of These Tutorials

By the end of these tutorials you will:

* see how to connect a local VM to a local cluster for running large-scale workflows
* see how to launch a cluster on Amazon’s cloud for running large-scale workflows
* more to come
