---

title:                 "Sequencer Run Reporter"
toc_includes_sections: true
markdown:              basic
is_dynamic:             true

---

The Sequencer Run Reporter can be used in scripts or on its own. It generates a tab-delimited overview of the sequencer runs, lanes, barcodes, and associated analysis events. 
 
## Requirements
In order to run the SequencerRunReporter plugin, you must have the following available to you:

* SeqWare Pipeline JAR (0.12.0 or higher)
* SeqWare settings file set up to contact the SeqWare Web service (contact your local SeqWare admin to get the path)

If you are working on the SeqWare VM, these will already be setup for you. 


## Command line parameters

This is a very basic tool and just outputs a simple tab-delimited file. There are no options other than the output file name. 

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.SequencerRunReporter -- --output-filename foo.txt

## Output 

	Last Modified	Sequencer Run Name	Sequencer Run SWID	Lane Name	Lane SWID	IUS Tag	IUS SWID	Sample Name	Sample SWID	GenomicAlignmentNovoalign_0.9.1 (10949)	CASAVA_1.8.2 (11034)	GATKRecalibrationAndVariantCalling_1.3.16 (17736)	GenomicAlignmentNovoalign_0.10.1 (23341)	FileImport_0.1.0 (25743)	CASAVA_1.8.2-1 (37742)	CASAVA-RTA_1.8.2-1 (37743)	GATKRecalibrationAndVariantCallingHg19Exomes_1.3.16-4 (49879)	GATKRecalibrationAndVariantCallingHg19Exomes_1.3.16-5 (62689)	
	2012-02-18 22:33:49.555121	120106_h804_0076_AD0LFWACXX	36217	120106_h804_0076_AD0LFWACXX_lane_8	36218	ACAGTG	36219	PCSI_0139_Pa_X_PE_425_EX	22603		37396:completed		41972:completed	114854:completed					
	2012-03-11 07:22:39.619474	110915_SN203_0124_BC00C4ACXX	10080	110915_SN203_0124_BC00C4ACXX_lane_7	10083	NoIndex	10084	CPCG_0003_Pr_P_PE_300_WG	838				169533:completed			154843:completed			
	2012-03-07 15:47:47.672034	110915_h239_0128_BC055RACXX	10071	110915_h239_0128_BC055RACXX_lane_4	10085	NoIndex	10086	CPCG_0003_Pr_P_PE_300_WG	838				166681:completed		154844:completed				
	2012-03-05 21:04:24.217953	110915_SN203_0124_BC00C4ACXX	10080	110915_SN203_0124_BC00C4ACXX_lane_8	10081	NoIndex	10082	CPCG_0003_Pr_P_PE_300_WG
