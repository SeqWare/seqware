---

title:    "Glossary"
markdown: basic

---

<dl class="glossary">

	<dt>Amazon Web Services (AWS)</dt>
	<dd> 
        A cloud provider, Amazon provides on demand instances and storage and
        is the cloud system on which we deploy SeqWare.
        </dd>

	<dt>Archetype</dt>
	<dd> 
        A template system for Java's build system Maven. We use it in SeqWare
        as a template tool to quickly generate new Workflow Bundles, Modules, etc.
        </dd>

	<dt>FreeMarker template language (FTL)</dt>
	<dd> 
        A simple markup language for Java used by our older, legacy workflow language.
        </dd>

	<dt>High Performance Computing (HPC)</dt>
	<dd> 
        A term that describes traditional cluster compute environments common at Universities and
        large research organizations.
        </dd>

	<dt>Indivisible Unit of Sequence (IUS)</dt>
	<dd> 
        A term to describe de-multiplexed sequence from a sequencer run.
        </dd>

	<dt>MetaDB</dt>
	<dd> 
        The SeqWare MetaDB tracks both experimental events (studies, samples, experiments, etc) and computational events (workflows, workflow runs, and processing events).
        </dd>

	<dt>Metatype</dt>
	<dd> 
        Loosely based on MIME types, a unique identifier that describes the format of the file produced by a SeqWare Pipeline workflow.
        </dd>

	<dt>Module</dt>
	<dd> 
        An API for steps in a workflow. Can be used to extend the SeqWare system by core developers. Most workflow developers
        will simply use the BashJob to run command line tools as steps in workflows. 
        </dd>

	<dt>Next Generation Sequencing (NGS)</dt>
	<dd> 
        A collection of high-throughput sequencing technologies.
        </dd>

	<dt>Processing Event</dt>
	<dd> 
        Information about individual steps in a workflow are saved in the MetaDB using the processing table.
        </dd>

	<dt>Sequencer Run</dt>
	<dd> 
        The Sequencer Run table in the MetaDB stores information about physical runs of the sequencer.
        </dd>

	<dt>Virtual Machine (VM)</dt>
	<dd> 
        A virtualized OS running on another machine. We use VirtualBox to redistribute SeqWare for use locally and an AMI for use on Amazon's cloud.
        </dd>

	<dt>Workflow Bundle</dt>
	<dd> 
        A zip file (using Zip64) that contains the workflow definition, 
        binary files, data files, etc.  Everything that needs to be included 
        to run the workflow.
        </dd>

	<dt>Workflow Engine</dt>
	<dd> 
        There are two workflow engines in SeqWare 1) Oozie and 2) Pegasus.  The first uses the Hadoop system for running jobs while the second uses the Condor/Globus/Sun Grid Engine tools for running jobs.
        </dd>

	<dt>Workflow Run</dt>
	<dd> 
        Represents a run of a given workflow in the MetaDB.
        </dd>

<!--
TODO
* other terms...
-->

	<dt>More To Come...</dt>
	<dd> 
         We will add more in the near future, please suggest terms you would like defined in the comments section below.
        </dd>

</dl>
