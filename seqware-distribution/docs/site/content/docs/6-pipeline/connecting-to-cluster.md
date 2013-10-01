---

title:                 "Connecting SeqWare Pipeline to a Real Cluster"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

## Overview

This document describes the process of connecting a VM to an actual SGE cluster
for use with the Pegasus Workflow Engine or Hadoop cluster for use with the
Oozie Workflow Engine. Since the Pegasus engine is more throughly tested and
used for the longest time we will focus more on that one.

The reason you might like to do this process is setting up SeqWare from scratch
(see [Installing from Scratch](/docs/2a-installation-from-scratch/) is time
consuming and difficult.  So we maintain VMs (both cloud VMs for Amazon and a
local VM using VirtualBox) to quickly get people started with SeqWare. When it
comes time to do "real work" with SeqWare you need a cluster. Rather than
installing SeqWare from scratch you can simply connect the VM to an actual
cluster. At OICR, for example, we followed this process for installing SeqWare
at the institute (we use the Pegasus Workflow Engine):

0. Import the SeqWare Virtual Machine from VirtualBox to KVM for use in our virtual server environment
0. Install the matching version of SGE used on the cluster to the VM
0. Configure this VM to be a "submission host" to our production SGE cluster
0. Modify a small set of config files on the VM to point to this new SGE
0. Run a workflow to verify jobs flow to the real cluster

## Oozie Workflow Engine

These directions cover connecting the Oozie Workflow Engine to an actual Hadoop
cluster. This engine is an alternative to the SGE engine above.  

### Connecting to a Hadoop Cluster

We use the Cloudera packages on the SeqWare VM to install and configure the
Hadoop system.  Please see the excellent documentation on [Cloudera's
Website](http://www.cloudera.com/) that will walk you through the process of
building a Hadoop cluster. You will want to match the Cloudera version on the
SeqWare VM with that of your Hadoop cluster and you may want to turn off the
namenode, datanode, tasktracker, jobtracker, etc on the SeqWare VM since these
functions will use your real Hadoop cluster. Essentially you will just use the
SeqWare VM as the Oozie host (so you will want to leave that installed on the
VM).  See the Cloudera documentation for information on configuring Oozie on
the VM to talk to your Hadoop cluster.

From the SeqWare perspective you will need to tell SeqWare which HDFS/MapReduce
cluster to talk to, see the Oozie and Hadoop sections of the [SeqWare
Configuration Guide](/docs/6-pipeline/user-configuration/).  

## Pegasus Workflow Engine

How to connect the Pipeline's Pegasus Workflow Engine to a real SGE cluster.

### Importing the VM

This really depends on your organization and how they want to run a virtualized
server.  For information on VirtualBox see their
[website](https://www.virtualbox.org/).  For information on importing our
VirtualBox image into KVM see [this
link](http://cheznick.net/main/content/converting-a-virtual-machine-from-virtualbox-to-kvm).
If you use Xen as your server virtualization see [this
link](http://roymic.blogspot.ca/2012/02/how-to-convert-virtual-box-image-to-xen.html).
You can also just use VirtualBox to run your virtual SeqWare server, in which
case you will find the command line tools useful, see [this
link](http://www.ubuntugeek.com/how-to-control-virtual-machines-virtualbox-using-vboxmanage.html).

### Install the Corresponding SGE Version

This is really up to your local sysadmin.  You will need to use a common
version of SGE between the SeqWare VM and your real cluster. Typically this is
a common NFS mount that includes the SGE software, config files, and logs.
Consult the [GridEngine
wiki](http://wiki.gridengine.info/wiki/index.php/Main_Page) for more
information about obtaining and configuring SGE.  

### Configuring GRAM on the VM

This is the key step in the whole process.  The GRAM layer from the Globus Toolkit
actually handles the submission of workflow step to the cluster as a job.  This is
done via a Perl module that knows how to talk with SGE. We need to modify that
Perl module so it knows 1) where the SGE tools are located and 2) the settings particular
to the cluster being connected to.

#### Perl Submission Module

On the VM, this Perl module lives in: <tt>/usr/share/perl5/vendor_perl/Globus/GRAM/JobManager/sge.pm</tt>. 

You may have seen this document mentioned in the [Install from Scratch](/docs/2a-installation-from-scratch/) guide where we patched it.  Now, you need to ensure you setup the parallel environment (<tt>-pe</tt>) and max memory (<tt>-l vf=</tt>) correctly for your new cluster.  Annoyingly, the parameters used for this are actually configurable in SGE so you will need to discuss with your cluster Sys Admin to see what is right for your environment.  See the code snippet patches below.

<%= render '/includes/setup_gram_1/' %>

<%= render '/includes/setup_gram_2/' %>

You will also need to modify the following settings files:

  /etc/sysconfig/gridengine
  /etc/globus/globus-sge.conf

These contain file paths and cluster name parameters that you need to update to point to the correct values for the real cluster.

### Testing

Finally you can submit and run a workflow just as you normally do following the
[User Tutorial](/docs/3-getting-started/user-tutorial/). You should see jobs
running on the cluster rather than locally using a tool like <tt>qstat</tt>.

