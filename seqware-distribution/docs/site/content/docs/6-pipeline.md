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
[User](/docs/3-getting-started/user-tutorial/),
[Developer](/docs/3-getting-started/developer-tutorial/), and
[Admin](/docs/3-getting-started/admin-tutorial/) tutorials since the guides below
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

See [About](/about/) for more information.

## Installation

* [Installation](/docs/2-installation/)
: This is our installation guide based on VMs that we recommend for most users. You will be left with a functioning SeqWare install including SeqWare Pipeline.
* [Installation From Scratch](/docs/2a-installation-from-scratch/)
: This guide walks you through how we built the VMs and will be of interest to anyone that needs to see the details of SeqWare setup starting with an empty Linux server. It is complicated so we highly recommend using a VM (which can be connected to a real cluster).

## Setup 

* [User Settings](/docs/6-pipeline/user-configuration/)
: Information about configuring user settings files.
* [Monitor Configuration](/docs/6-pipeline/monitor_configuration/)
: Setting up the SeqWare-associated tools that need to run so workflow triggering and monitoring work.
* [Connecting to a Real Cluster](/docs/6-pipeline/connecting-to-cluster/)
: Once you are happy with writing, installing, and running workflows on a stand-alone VM you will want to connect to a "real" cluster. This guide walks you through the process of connecting a VM to a cluster (HPC & Hadoop, depending on your workflow engine of choice).

## Workflows

Workflows define a series of steps and how they relate to each other.
Typcially, these encode a series of calls to command line tools that operate on
files read from and written to a shared filesystem. Individual steps ususally
run on a randomly chosen cluster node.

* [Legacy FTL Workflows](/docs/6-pipeline/legacy-ftl-workflows/)
: This is our older, more verbose workflow language based on FreeMarker. This provides information on the structure of a typical workflow. See also http://sourceforge.net/apps/mediawiki/seqware/index.php?title=How_to_Write_a_Bundled_Workflow
* [Java Workflows](/docs/6-pipeline/java-workflows/)
: This is our newer workflow language that is much simplier than the FTL and more expressive. We recommend this for all new workflow development.
* [Workflow Bundle Conventions](/docs/6-pipeline/workflow_bundles/)
: We rely on a bundle format for packaging up and exchanging workflows. This document describes the format and directory structure.
* [Workflow Config Files](/docs/6-pipeline/config_files/)
: This document describes the ini configuration file used to describe (and type) workflow parameters.
* [Workflow Metadata File](/docs/6-pipeline/metadata_files/)
: This document describes the metadata XML file used to describe workflows. It provides workflow names, versions, descriptions, and information for running and testing the workflow.
* [File Type Conventions](/docs/6-pipeline/file-types/) 
: This document describes the standardized file meta types (MIME-like types) we use in the project and how to add files to a community-writable file type registration.
* [Debugging, Troubleshooting, & Restarting Workflow](/docs/6-pipeline/debug-workflows/)
: A guide to debugging, troubleshooting, and restarting failed workflows for both workflow engines (Pegasus and Oozie).

## Modules

Modules are really optional for those interested in workflow development since
most workflows simply refer to command line tools bundled inside the workflow.
For those interested in extending the underlying SeqWare system, Modules
provide a way to define new step types and could be useful for writing custom
steps that interact with databases, trigger analysis in other frameworks
(Pig/Hive/MapReduce), make calls to web services, etc. We use Modules to
provide core services in SeqWare (such as file provisioning and bash shell
execution). Again, Modules are mainly targeted at core SeqWare developers not
general workflow developers.

* [Writing Modules](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=Developing_New_SeqWare_Pipeline_Modules)
: How to extend SeqWare with Java tool wrappers. Can be used in workflows or as stand-alone utilities that know how to record provenance data back to SeqWare MetaDB.

## Deciders

The Deciders framework allows for the automatic parameterization and calling of workflows in SeqWare Pipeline. It allows you to easily encode the parent workflow and file types that, when present, enable a subsequent workflow to be launched.

* [Basic Deciders](/docs/6-pipeline/basic_deciders/)
: A generic Decider that can be used to launch a workflow using simple criteria like parent workflow and input file type.
* [Making a Custom Decider](/docs/6-pipeline/custom_deciders/)
: How to create a custom decider for your workflow, useful if your logic for running your workflow is more complicated than simple parent workflow + input file requirements.

## Reporting

A major focus of the SeqWare Web Service is providing reporting resources. These are command line tools that are particularly useful for generating reports for SeqWare entities such as workflow runs and their outputs.

* [Study Reporter](/docs/21-study-reporter/)
: Create a nested tree structure of all of the output files from a particular sample, or all of the samples in a study
* [Sequencer Run Reporter](/docs/20-sequencer-run-reporter/)
: Gives you a view of all the sequencer runs/lanes/barcodes and the associated analysis processing events.
* [Workflow Run Reporter](/docs/19-workflow-run-reporter/)
: Find the identity and library samples and input and output files from one or more workflow runs.

## Other Tools 

Other useful tools used for import, export, and annotation of results.

* [File Import](/docs/22-filelinker/)
: Import files into the SeqWare MetaDB so they can be used as inputs to workflows.
* [File Export](/docs/6-pipeline/file_export/)
: Export files that are the outputs from SeqWare workflows.
* [Attribute Annotator](/docs/12-attribute-annotator/)
: The underlying SeqWare MetaDB supports the annotation of entities in the system using flexible key/value pairs. This guide shows you how to annotate entities in the underlying data model using this approach.

## Command Line Reference

This documentation is auto-generated and covers our Plugins (which are utility tools used outside of workflows) and our Modules (which model custom steps in workflows and know how to integrate with the SeqWare MetaDB for metadata writeback).

* [Plugins](/docs/17-plugins/)
: The command line utilities of SeqWare.
* [Modules](/docs/17a-modules/)
: Can be used as custom steps in workflows or on the command line. The most important modules are the GenericCommandRunner and the ProvisionFiles modules. These are used to call individual Bash steps in workflows and to move input/outputs around respectively.


