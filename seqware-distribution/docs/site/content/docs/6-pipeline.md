---

title:                 "SeqWare Pipeline"
toc_includes_sections: true
markdown:              advanced

---

##Overview

The SeqWare Pipeline sub-project is really the heart of the overall SeqWare
project. This provides the core functionality of SeqWare; it is workflow
developer environment and a series of tools for installing, running, and
monitoring workflows.

<img width="600" src="/assets/images/seqware_hpc_oozie.png"/>

We currently support two workflow languages (FTL markup and Java) and two
workflow engines (Oozie and Pegasus). Our current recommended combination is
Java workflows with the Pegasus engine.

We assume you have already gone through the
[User](docs/3-getting-started/user-tutorial),
[Developer](docs/3-getting-started/developer-tutorial), and
[Admin](docs/3-getting-started/admin-tutorial) tutorials since the guides below
assume you already have.

##Features

SeqWare Pipeline has several key features that distinguish it from other open source and private workflow solutions. These include:

* tool-agnostic
* developer framework focused
* focused on automated analysis
* includes cluster abstraction
* supports detailed provenance tracking
* supports user-created workflows
* implements a self-contained workflow packaging standard
* includes fault tolerance
* focuses on meeting workflow needs of big projects (thousands of samples)
* is open source

See [About](/about) for more information.

## Installation

* [Installation](/docs/2-installation)
: This is our installation guide based on VMs that we recommend for most users. You will be left with a functioning SeqWare install including SeqWare Pipeline.
* [Installation From Scratch](/docs/2a-installation-from-scratch/)
: This guide walks you through how we built the VMs and will be of interest to anyone that needs to see the details of SeqWare setup starting with an empty Linux server. It is complicated so we highly recommend using a VM (which can be connected to a real cluster).

## Setup 

* [User Settings](/docs/6-pipeline/user-configuration/)
: Information about configuring user settings files.
* [Monitor Configuration](/docs/6-pipeline/monitor-configuration/)
: Setting up the SeqWare-associated tools that need to run so workflow triggering and monitoring work.
* [Connecting to a Real Cluster](/docs/6-pipeline/connecting-to-cluster/)
: Once you are happy with writing, installing, and running workflows on a stand-alone VM you will want to connect to a "real" cluster. This guide walks you through the process of connecting a VM to a cluster (HPC & Hadoop, depending on your workflow engine of choice).

## Workflows

* Dev environment setup (anything needed to get started with dev?)
* Legacy FTL Workflows (see http://sourceforge.net/apps/mediawiki/seqware/index.php?title=How_to_Write_a_Bundled_Workflow):
* Java Workflows
* Config File (INI)
* File Type Conventions (MIME)
* Debugging, Troubleshooting, & Restarting (Pegasus/Oozie)

## Deciders

* Basic Decider
* Making a Custom Decider

## Reporting

* Study Reporter
* Sequencer Run Reporter
* Workflow Run Reporter
* Other reports?


## Tools 

* File Import
* File Export
* Attribute Annotator
* Other Plugins?

## Command Line Reference

* Plugins
* Modules



##Setup

* [Installation](/docs/2-installation/): our basic installation guide. 
* [Installation From Scratch](/docs/2a-installation-from-scratch/): The recommended way to install SeqWare Pipeline is to use one of the VMs (AMI or VirtualBox), see [Installation](/docs/2-installation/). The "Installation From Scratch" guide, however, walks you through how we built the VMs and will be of interest to anyone that needs to see the details of SeqWare setup starting with an empty Linux server.

##Configuration

* [System Configuration](): 

##Command Line Tools

* [Plugins](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=Available_Modules) : Generic Modules for your workflows
* [Modules](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=Available_Modules) : Generic Modules for your workflows

##Workflows

* [Writing Workflows](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=How_to_Write_a_Bundled_Workflow) : A series of analysis tools and modules chained together to produce a particular result
* [Writing Modules](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=Developing_New_SeqWare_Pipeline_Modules) : Java wrappers around tools for use in a workflow
* [Command-Line Tools](/manuals/command_line_tools/) : Tools to create samples, experiments and studies on the command line, upload data, launch workflows, and more.
* [SeqWare Conventions](/docs/16-module-conventions/): File MIME-types, Error codes

##Deciders

* [Deciders](/docs/18-deciders/): Small programs that link the results of a MetaDB query to a particular workflow template 

##Reporting

* [Study Reporter](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=SymLink_Reporter) : Create a nested tree structure of all of the output files from a particular sample, or all of the samples in a study
* [Sequencer Run Reporter](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=Sequencer_Run_Reporter): Gives you a view of all the sequencer runs/lanes/barcodes and the associated analysis processing events.
* [Workflow Run Reporter](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=Workflow_Run_Reporter): Find the identity and library samples and input and output files from one or more workflow runs.

## Importing

* [FileLinker](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=FileLinker) : Import files into the MetaDB and link them with IUS's or lanes.

## Metadata Tools

* <a href="/docs/12-attribute-annotator/">AttributeAnnotator</a>: Annotate items in the MetaDB with 'skip' or key-value pairs.


## Other Items

* links directory
* checking in workflows to source control

