---

title:                 "SeqWare Pipeline"
toc_includes_sections: true
markdown:              basic

---

##Overview

##Admin Setup

##Features

##Workflow Manuals

* [Writing Workflows](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=How_to_Write_a_Bundled_Workflow) : A series of analysis tools and modules chained together to produce a particular result
* [Writing Modules](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=Developing_New_SeqWare_Pipeline_Modules) : Java wrappers around tools for use in a workflow
* [Command-Line Tools](/manuals/command_line_tools/) : Tools to create samples, experiments and studies on the command line, upload data, launch workflows, and more.
* [SeqWare Conventions](/docs/16-module-conventions/): File MIME-types, Error codes
* [Generic Pipeline Modules](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=Available_Modules) : Generic Modules for your workflows
* <a href="/docs/14-workflow-mvn/">Creating Workflows Using Maven Archetypes</a>: Creating New Workflow Bundles and Modules Using Maven Archetypes.
* [Deciders](/docs/18-deciders/): Small programs that link the results of a MetaDB query to a particular workflow template 
* TODO, Magic Variables: we need to document all the magic variables that are filled in in the various layers (config, metadata.xml, FTL, etc)
* TODO, Building Workflow Bundles with Large Dependencies: as you write workflows using the Maven Archetype system you will want to include large files (such as genome indexes) within the bundles but the time it takes to include these in the build process ('mvn install') will get really rediculous.  Worse, every time you build the workflow using Maven it will copy these resources again, turning a process that should take a few minutes into something that can take hours! This is a simple workaround using a symlink plugin in Maven to eliminate these time-consuming copy steps.

##Reporting Manuals

* [Study Reporter](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=SymLink_Reporter) : Create a nested tree structure of all of the output files from a particular sample, or all of the samples in a study
* [Sequencer Run Reporter](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=Sequencer_Run_Reporter): Gives you a view of all the sequencer runs/lanes/barcodes and the associated analysis processing events.
* [Workflow Run Reporter](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=Workflow_Run_Reporter): Find the identity and library samples and input and output files from one or more workflow runs.

## Importing Tools

* [FileLinker](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=FileLinker) : Import files into the MetaDB and link them with IUS's or lanes.

## Metadata Tools

* <a href="/docs/12-attribute-annotator/">AttributeAnnotator</a>: Annotate items in the MetaDB with 'skip' or key-value pairs.




