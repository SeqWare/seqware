---

title:                 "Symlink (Study) Reporter"
toc_includes_sections: true
markdown:              basic
is_dynamic:             true

---

Create a nested tree structure of all of the output files from a particular sample, or all of the samples in a study by using the SymLinkFileReporter plugin. This plugin also creates a CSV file with all of the accompanying information for every file.

The SymLinker has several functions. First of all, it creates an ordered hierarchy of symlinked result files that can be used for further processing. The directory and file names are in a standardized format that allows users to query the database to determine which workflows were run in order to produce the data. It also provides the information needed to run subsequent workflows through SeqWare using the data and metadata tracking.

Secondly, the SymLinker produces a CSV file with the same data as in the directory structure, but in a more easily parseable format. This CSV file can be used for the same purposes as the directory structure since it contains similar information, but in a more human-readable way with column headers. The file also has more information about attributes applied to lanes and samples, the processing algorithm, and the date of processing.

The SymLinker can be used in scripts or on its own. 
 
## Requirements
In order to run the SymLink plugin, you must have the following available to you:

* SeqWare Pipeline JAR (0.10.0 or higher)
* SeqWare settings file set up to contact the SeqWare Web service (contact your local SeqWare admin to get the path)

If you are working on the SeqWare VM, these will already be setup for you. 


## Command line parameters

See [Plugins](/docs/17-plugins/#symlinkfilereporter/)

## Linked Files 

The links that are produced have one of two name structures:

### 1. SeqWare format
The SeqWare format concentrates upon as much information as possible being in the path of the linked file. Most of the information is concentrated in the filename, with a minimum of information in the directory structure. This is the default output format.

	studyTitle-studySWA/parentSampleName-parentSampleSWA/sampleName-sampleSWA/workflowName-workflowSWA__workflowRunName-workflowRunSWA__sequencerRunName-sequencerRunSWA__laneNum__iusTag-iusSWA__sampleName-sampleSwa__filename.

Where laneNum is lane_index + 1, iusTag is "NoIndex" if tag in ius table is null, and where all spaces in any of the fields above are replaced with "_".

Here is an example:

	PCSI-63/-/PCSI_0128_SP_R_PE_382_EX-24257/CASAVA-RTA-37743__null-50371__120113_h203_0139_AC0C3JACXX-37467__7__GCCAAT-37471__PCSI_0128_SP_R_PE_382_EX-24257__SWID_37528_CPCG_0042_Pr_P_PE_287_WG_120113_h203_0139_AC0C3JACXX_NoIndex_L004_R2_001.fastq.gz

* studyName: PCSI
* studySWA: 63
* parentSampleName/SWA : empty
* sampleName : PCSI_0128_SP_R_PE_382_EX
* sampleSWA : 24257
* workflowName: CASAVA-RTA
* workflowSWA: 37743
* workflowRunName: null
* workflowRunSWA: 50371
* sequencerRunName: 120113_h203_0139_AC0C3JACXX
* sequencerRunSWA: 37467
* laneNum : 7
* iusTag: GCCAAT
* iusSWA: 37471
* sampleName: PCSI_0128_SP_R_PE_382_EX
* sampleSWA: 24257
* filename: SWID_37528_CPCG_0042_Pr_P_PE_287_WG_120113_h203_0139_AC0C3JACXX_NoIndex_L004_R2_001.fastq.gz


#### Parent Samples
When the parent samples are present, you may see a parent folder name that looks like this:

	PCSI_0006_Pa_X_PE_234_EX:PCSI_0006_Pa_X_PE_185_WG:PCSI_0006_Pa_X_nn_1_D_1:PCSI_0006_Pa_X_nn_1_D:PCSI_0006_Pa_X_nn_1:PCSI_0006:-4838:4837:48
36:4834:4833:4831:

This directory name describes the complete sample hierarchy in the filename, starting with the most specific and going to the most general, separated by colons, and then the SWIDs for each of the samples. In this case, we have:

	Sample name                        SWID
	PCSI_0006_Pa_X_PE_234_EX           4838
	PCSI_0006_Pa_X_PE_185_WG           4837
	PCSI_0006_Pa_X_nn_1_D_1            4836
	PCSI_0006_Pa_X_nn_1_D              4834
	PCSI_0006_Pa_X_nn_1                4833
	PCSI_0006                          4831

The samples in this folder would be the leaf the hierarchy going from PCSI_0006 to PCSI_0006_Pa_X_PE_234_EX.

### 2. Production format
The production format mimics the data structure used by the production team that is expected by some of their tools. It can be toggled on using the --prod-format tag.


## Excel CSV File

The plugin also produces a CSV file with file information. If any field is unavailable, 'null' is printed. Each line corresponds to a separate file. The following information is included. The column headers should be fairly self-explanatory; where more information is necessary, a description has been added. Most types of columns have an accession number called a SWID, which is the SeqWare accession. The SeqWare accession is a unique identifier that allows an object to be identified in the database regardless of its type. It is used throughout SeqWare to link entities together.

For more information on these entities and how they link together, please see [Understanding the SeqWare MetaDB](/docs/4-metadb/).

* Last Modified: The date that the file was processed
* Study Title
* Study SWID
* Experiment Name
* Experiment SWID
* Parent Sample Name: Parent samples are the root sample in the sample hierarchy
* Parent Sample SWID
* Parent Sample Attributes
* Sample Name: Samples are the leaf sample in the sample hierarchy
* Sample SWID
* Sample Attributes
* Sequencer Run Name
* Sequencer Run SWID
* Lane Name
* Lane Number
* Lane SWID
* Lane Attributes: Lane attributes are other key-value attributes that have been transferred from the LIMS to the MetadataDB
* IUS Tag
* IUS SWID
* Workflow Name
* Workflow Version
* Workflow SWID
* Workflow Run Name
* Workflow Run SWID
* Processing Algorithm
* Processing SWID
* File Meta-Type: One of the types listed on the [[Module Conventions]] page
* File SWID
* File Path

## Examples

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter -- --study PCSI
Creates a directory for the study with the title "PCSI" with all filetypes linked inside. Also creates a CSV file in the current directory with all of the file information

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter -- --dump-all --no-links --output-filename DumpAllTheStudies
Creates only a CSV file named "DumpAllTheStudies.csv" with all of the files from all of the studies in the database.

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar --plugin  net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter -- --sample PCSI_0119_Sp_R_PE_428_EX --prod-format
Creates a directory and CSV file for the files from the sample named "PCSI_0119_Sp_R_PE_428_EX". The directory will be output in production format (see below).

