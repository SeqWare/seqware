---

title:                	SeqWare Pipeline Custom Deciders
toc_includes_sections:  true	
markdown:              	advanced 
is_dynamic:	 	false	

---


## Overview 

You will want to extend the [Basic Decider class](https://github.com/SeqWare/seqware/blob/master/seqware-pipeline/src/main/java/net/sourceforge/seqware/pipeline/deciders/BasicDecider.java). 

You can automatically generate a decoder via our maven archetype.

	mvn archetype:generate (select SeqWare Java Decider archetype)

There a number of methods that can be overridden in order to customize the behavior of your decider.

<!-- the following content is modified from from https://wiki.oicr.on.ca/display/PIPEDEVAL/Decider+Tutorial -->

A decider is a very small Java Maven project. It has one Java file and two resources files as well as one pom.xml file. The relatively deep file structure is required for Maven builds.

	.
	|-- pom.xml
	`-- src
	    `-- main
		|-- java
		|   `-- ca
		|       `-- on
		|           `-- oicr
		|               `-- seqware
		|                   `-- deciders
		|                       `-- HelloWorldDecider.java
		`-- resources
		    `-- decider.properties

The HelloWorldDecider.java is a Java class that extends net.sourceforge.seqware.pipeline.deciders.BasicDecider, the decider framework built into SeqWare. Most of the specific decider functionality is built into HelloWorldDecider.java in a series of methods that are extended from BasicDecider. The two resources files are properties files that change more often than the Java code. These are updated when:

* Migrating to a new database install
* A new version of a workflow is released

## Resources folder

There is one file in src/main/resources: decider.properties. 

### Decider.properties file

The decider.properties file contains a number of properties that will be set by a user once and rarely updated. The intention is that the production team update these as new workflows are installed into their database.

	parent-workflow-accessions=
	check-wf-accessions=
	workflow-accession=

The parent-workflow-accessions are the workflows from which the files of interest come from.

The workflow-accession is the accession number of the current workflow from the database install. This is the WORKFLOW_ACCESSION that was printed after successfully installing to the database.

<!-- the following content is direct from https://wiki.oicr.on.ca/display/PIPEDEVAL/Decider+Tutorial -->

## Overridable Methods

### Init method

The init method sets up any variables needed for the rest of the setup, including all of those command-line options mentioned earlier. Specifying either the meta-type or the parent-accession is required for the decider to work. Here we specify that the decider only runs on files of type "application/bam". More than one can be specified here. For a full list of possible meta-types, please see [Module Conventions](/docs/6-pipeline/file-types/).

	@Override
	public ReturnValue init() {
	        Log.debug("INIT");
	        //By default, the decider runs on a per-file basis (one file -> one workflow run)
	        //this.setHeader(Header.IUS_SWA);
	
	        //Launches only on BAM files
	        this.setMetaType(Arrays.asList("application/bam"));
	
	        //By calling super.init() last, anything defined on the command line at run-time 
	        //overrides the defaults set here.
	        ReturnValue val = super.init();
	        return val;
	}

### checkFileDetails method

Performs any additional checks on the file before adding it to the list of files to incorporate. This method should be extended for future deciders for custom behavior. You can also pull any details out of the file metadata here. The boolean specifies whether or not the file is added to the list.

	protected boolean checkFileDetails(ReturnValue returnValue, FileMetadata fm) {
	        Log.debug("CHECK FILE DETAILS:" + fm);
	        boolean isValid = super.checkFileDetails(returnValue, fm);
	        if (isValid) {
	            //If you need access to the information stored in the ReturnValue for each file, uncomment
	            //the next line and the line that instantiates the pathToAttributes map before the constructor
	            //pathToAttributes.put(fm.getFilePath(), returnValue);
	        }
		return isValid;
	}

### modifyIniFile method

Change the INI file from its defaults as specified in the workflow. The commaSeparatedFilePaths are the files that are going to be grouped together for one particular run of the decider, and the commaSeparatedParentAccessions are the accessions of the processing events that produced the file.

In this particular case, we want to substitute the INI field 'input_file' to be the comma-separated list of files. The rest of the INI file is auto-generated using the decider's INI file and the default INI file from the database.

	protected Map<String, String> modifyIniFile(String commaSeparatedFilePaths, String commaSeparatedParentAccessions) {
	        Log.debug("INI FILE:" + commaSeparatedFilePaths);
	        Map<String, String> iniFileMap = super.modifyIniFile(commaSeparatedFilePaths, commaSeparatedParentAccessions);

        	iniFileMap.put("input_file", commaSeparatedFilePaths);

		return iniFileMap;
	}

Ini attributes are applied in the following order:, where the greater than sign means 'takes precedence over': modifyIniFile method &gt; command line options &gt; Decider INI file &gt;  Default Database INI file

### handleGroupByAttribute method

If the contents of the group-by attribute needs to be modified at all, this is where you should do it.

For example, say that you wanted to group files based on tissue type. This information is captured in the SAMPLE_ATTRIBUTE column. However, the contents of the SAMPLE_ATTRIBUTE column include a variety of information, not all of which is relevant. Here is an example:

	SAMPLE_ATTRIBUTE = sample.geo_reaction_id=6223;sample.geo_template_id=17937;sample.geo_library_source_template_type=EX;sample.geo_targeted_resequencing=Agilent SureSelect ICGC/Sanger Exon;sample.geo_tissue_type=n;sample.geo_tissue_origin=Br;

In the handleGroupByAttribute, you would use a regular expression to pull out the information "sample.geo_tissue_type=n", which is used to put the file into a bucket and aggregate it with other files.

	protected String handleGroupByAttribute(String attribute) {
	return attribute;
	}

