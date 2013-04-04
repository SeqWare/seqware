---

title:                 "SeqWare MetaDB"
markdown:              advanced
toc_includes_sections: true
is_dynamic:            true

---


## Overview

A database built with PostgreSQL, the SeqWare MetaDB includes tables to model the run of the sequencer, analysis "processing" events, and the ability to trigger standard analysis through triggering the SeqWare Pipeline. Features include: 

* Short Read Archive-compliant schema
* A "processing" table to model analytical processes and results for samples
* A common database for tracking metadata, used by the other SeqWare components 

## Setup

Please see the [Install Guide](/docs/github_readme/3-metadb/)

## Tables

The SeqWare MetaDB was built on top of the [Short Read Archive (SRA)](http://www.ncbi.nlm.nih.gov/Traces/sra) data model.  It is not a 100% translation of the data model, there may be places where the SRA model provides greater flexibility and places where we simplified the model for our own purposes.  But we generally tried to keep with the SRA model as much as possible.  You can find out more information from the following documents:

* [SRA Concepts](http://www.ncbi.nlm.nih.gov/Traces/sra/sra.cgi?cmd=show&f=concepts&m=doc&s=concepts)
* [Documentation](http://www.ncbi.nlm.nih.gov/Traces/sra/sra.cgi?cmd=show&f=rfc&m=doc&s=rfc SRA Schema)

![Simple DB](/assets/images/metadb/Db_simple.png)

Or, more complicatedly:

![Complicated DB](/assets/images/metadb/Db_complicated.png)


## Sequence Data ##

SeqWare uses the meta-DB to track processing events and to connect samples to processed data and/or to connect one set of processed data to another. For example linking between a biological sample, a sequencing run, the generated raw sequence data, and downstream processed sequence data is possible. This is actually a complicated process that depends on the specific path through a pipeline, with details like multiple samples being pooled for sequencing, or lanes contain data from multiple samples (multiplexed or so-called bar-coded samples) arising. First, a simple example.


![Simple DB](/assets/images/metadb/Study_hierarchy.png)

The 'proper' way to traverse the database from a study or sequencer_run is to go through ius, processing_ius, and processing. So from study, you traverse through experiment, sample to sample through the sample_hierarchy table, and ius, to processing_ius, and then to all of the processings. The processing tree starts from the processing linked through processing_ius, and then through the processing_relationship table. Each new processing event has the parent of the previous processing event; for example, if WorkflowA is run on IUSA, and then WorkflowB is also run on IUSA using the results from WorkflowA, the processing tree looks like IUSA -> processing_ius -> Processing of step 1,2,3 of WorkflowA -> Processing of step 1,2,3 of WorkflowB.  Processings are linked to workflow_runs, which have connections to IUSes through ius_workflow_runs and/or Lanes through lane_workflow_runs, as a convenience only. These links are not necessarily present (though we try to make sure they are). The safest way to find all of the processing events linked with a Study is to traverse through the IUS to the root Processing through processing_ius, not through ius_workflow_runs.

