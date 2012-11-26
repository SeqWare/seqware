---

title:                 "Installation"
markdown:              basic
toc_includes_sections: true
is_dynamic:            true

---

## Environment

SeqWare is designed to work in a 64-bit Linux environment, we do not support Windows
or MacOS.  You can still use SeqWare on your local Mac or Windows PC through
virtualization software.  You need to be comfortable with Linux before moving
forward with SeqWare, specifically the Bash shell, using an editor like vim or
emacs, and some experience programming in Java is extremely helpful.

To learn about Linux, the Bash shell, and Java we recommend the O'Reilly series of books:

* [Linux in a Nutshell](http://amzn.com/B0093T2G3I)
* [Learning the bash Shell](http://amzn.com/B0043GXMSY)
* [Learning Java](http://amzn.com/B0043EWVDI)

## Installing with a Local VM

We have created a VirtualBox VM with all SeqWare projects pre-installed along
with some sample data.  This is the easiest way to get started with SeqWare
since it requires no installation and configuration on your part.  While the
download is pretty large, you will not incur per hour fees as you do when
running on Amazon so it is a great way to get started with SeqWare, kick the
tires, and see if this is something you want to use.

### Getting the VM

Getting the local VM is a two-step process.  First, download (or install via
your OS's package management system) the VirtualBox application. This is a
great piece of software that will let you run the SeqWare CentOS VM on whatever
OS you currently have.

* [Oracle Virtual Box](https://www.virtualbox.org/)

Next, you will need to download our current VM image which is linked below.
Please make sure you are using the latest version, when we post a new one we
will tweet about it so consider following us on Twitter.

* [SeqWareVM_0.12.5-r5163_CentOS_6.2_20120904.ova](https://s3.amazonaws.com/nimbusinformatics.exchange/public/SeqWareVM_0.12.5_r5163_CentOS_6.2_20120904b.ova)

Open VirtualBox and use the "Import Appliance" tool to create a new SeqWare VM
on your system.  You can then launch it and you will be presented with a fairly
standard Linux desktop with links to the project's documentation to give you an
idea of where to begin (the "Getting Started" guide here which provides a
walk-through of using this VM).

<p class="warning"><strong>Note:</strong>
When you import the VM you need to set your memory and CPU correctly.  Do not
over-commit resources (especially memory) since that will cause the machine to
"swap" memory to disk and slow your computer to a crawl. This VM is intended
for you to see SeqWare in action and then create your own workflows. If you are
making a workflow that will take 16GB of memory then you will need a VM running
on a machine that can afford to give 16GB of memory to VirtualBox.  Also, we
only distribute 64-bit VMs which all modern PCs will support and VirtualBox
makes use of various virtualization extensions which most modern PCs support
(but you may want to check with your computer vendor).  </p>

### Logging In

The login username is <kbd>seqware</kbd> and the password is <kbd>seqware</kbd>. If you need to become <kbd>root</kbd> the password is the same.

### What Can You Do With It?

As the note above mentioned, you can use this VM to see what each of the
SeqWare tools can do, kick the tires, explore the docs more, etc. If you decide
to use SeqWare then this VM can also be your development and testing
environment for workflows.  Once you have created and tested a given workflow,
you can also use the SeqWare commands on this machine to "install" the workflow
bundle into a production system.  Or you can even hook up this VM to a real
local grid environment (such as an SGE cluster) for running workflows.  This is
exactly what we did at OICR, our SeqWare hosts are all virtual machines that
can submit workflow jobs to our physical cluster. You can find more information
on this later in the guide.

<img src="/assets/images/vm_screen.png" alt="SeqWare VM" width="600px"/>

## Installing with a Cloud VM

We currently support running SeqWare on the Amazon cloud on HPC nodes. These
are high-performance, cluster-compute nodes well suited for research, for specs
see [Cluster Compute Quadruple Extra Large
Instance](http://aws.amazon.com/ec2/instance-types/). These machines provide
23GB of memory and 2x quad core "Nehalem" processors which is typically
sufficient for analyzing a human exome within about 4 hours depending on the
specifics of your workflows.

### Getting an Amazon Cloud Account

To use Amazon's cloud (also know as Amazon Web Services or AWS) you need to sign up for an account and that requires a credit card.  Also, while launching these instances is simply point and click using Amazon's very nice AWS console, actually using these machines requires the same level of experience as using the local VM above.  If you are looking for a completely graphical, web-based service you should consider using one of the cloud-based providers like Nimbus Informatics or DNAnexus.

Signup for the Amazon cloud [here](http://aws.amazon.com/)

### Running the VM

What makes Amazon's cloud so amazingly awesome is its excellent support for both graphical UIs and programmatic APIs for controlling the cloud.  It is your choice on how you want to launch the VM, either through the console or via one of the many command line and programmatic tools available. The details are subject to change so we refer to Amazon's thorough [documentation](https://aws.amazon.com/documentation/).

Our current public AMI(s):

* SWStandaloneCentOS_v2_GenericDevBox_v5: ami-cb0fb9a2

An example of the launching wizard in the Amazon AWS console can be seen below:

<img src="/assets/images/amazon_launcher.png" alt="SeqWare AWS Console" width="600px"/>

<p class="warning"><strong>Tip:</strong>Make sure you open port 22 for SSH and port 8080 for the SeqWare Portal and Web Service.</p>

### Logging In

Unlike the local VM there is no graphical desktop to log into.  Instead you will need to follow the directions on the Amazon site for using <kbd>ssh</kbd> to log into your running VM.  There you will have a command line interface to interact with the SeqWare tools. You can also view the SeqWare Portal and SeqWare Web Service remotely in your browser if you have previously opened the ports 22 and 8080.

### What Can You Do With It?

You can launch this public AMI which will give you a server with all the SeqWare projects pre-configured and loaded with some small examples.  You can then use the running VM to test workflows and write your own.  Since this VM runs on the fairly powerful HPC node type, you can actually use it to get real work done.  This instance is powerful enough to analyze a whole human exome in 4-8 hours and you can, of course, launch many instances simultaneously to process multiple exomes in parallel.


<p class="warning"><strong>Note:</strong>Keep in mind Amazon charges approximately $1.30 USD per hour to run these instances, it is your responsibility to monitor your cloud usage and turn your VMs off when not in use!</p>

### More to Come...

TODO: In the near future we will document the process to:

* launch clusters of SeqWare instances
* pass configuration information to instances (like your AWS keys)


## Installing from Scratch

<p class="warning"><strong>Note:</strong> This is not for the faint of heart.  We do not recommend you attempt to do this
unless you a very familiar with Linux and comfortable with complex software
configuration. We really recommend using the local VM in production and
configure it to submit jobs to a real cluster since the
Pegasus/Condor/Globus/SGE stack are difficult to setup correctly.</p>

This guide will be ported to this manual but for now the best instructions for
setting up SeqWare from scratch can be found on our [SourceForge
wiki](https://sourceforge.net/apps/mediawiki/seqware/index.php?title=Creating_a_SeqWare_VM).

## Next Step

Now that you have seen how to launch either a local or cloud-based SeqWare instance follow the 
"[Getting Started](/docs/3-getting-started/)" guide to see how to run the sample HelloWorld
workflow.
