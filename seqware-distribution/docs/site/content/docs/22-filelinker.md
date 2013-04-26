---

title:                 "File Linker"
toc_includes_sections: true
markdown:              basic

---

The FileLinker plugin was designed to import files from the LIMS into the SeqWare MetaDB for processing, but can be used to attach any file paths to any IUS or Lane. **This tool is only available over a direct database connection as of SeqWare version 0.12.2 (June 2012), not over the Web service.** 
 
## Add the Workflow 

The "FileImport" workflow is already added into the MetadataDB, but this method can be used to create new workflows to link to. 

	INSERT INTO workflow (name, description, version, seqware_version, create_tstmp) VALUES ('FileImport', 'Imports files into the database, \
	     links them to IUSs or Lanes and creates intermediate Processings. Initially used to import files from the LIMS and attach them to IUSes.', \
	     '0.1.1', '0.13.6', '2012-12-07 15:30:00');


## Command line parameters

See [Plugins](/docs/17-plugins/#filelinker/)


## Get the linking file

In one of the following formats, whitespace-separated. The first three columns are not parsed by the FileLinker, but this was the format of the file provided for mass import. The only columns that are used are ius_sw_accession, mime_type (if it exists) and file. The header is required, but all other columns can be empty and separated by tabs.

Mime-type (preferred):

	sequencer_run    sample    lane    ius_sw_accession    file_status    mime_type    file
	.                .         .       7937                .              txt          /absolute/file/path/myfile.txt
	...


No mime-type:

	sequencer_run    sample    lane    ius_sw_accession    file_status    file
	.                .         .       7937                .              /absolute/file/path/myfile.txt
	...

## Input File Alternatives 

The files above are tab delimited, but any single character delimiter may be used. Here comma is used as a delimiter.

	sequencer_run,sample,lane,ius_sw_accession,file_status,mime_type,size,md5sum,file,
	111214_h1068_0067_AD0EJ0ACXX,AOE_0001_nn_P_PE_270_WG,1,24635,OK,chemical/seq-na-fastq-gzip,12383198646,2f33208fef22f392ecddaa1eb89ebd24,/oicr/data/001.fastq.gz,
	111214_h1068_0067_AD0EJ0ACXX,AOE_0001_nn_P_PE_270_WG,2,24633,OK,chemical/seq-na-fastq-gzip,14493953643,50c25186dfe3fc5bb3a7f60e0696012b,/oicr/data/002.fastq.gz,

The fields '''size''' and '''md5sum''' are optional, but if include the information will be saved into the SeqWare database.

## Run the tool 

You can get the workflow accession using the --list-install option in BundleManager. 

	java -jar seqware-distribution-<%= seqware_release_version %>-SNAPSHOT-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.FileLinker -- \
	     --file-list-file /home/mtaschuk/Downloads/link_files_report.txt --workflow-accession 375894 --csv-separator ,

By default error messages will be written to standard out. To see more information, such as successfully linked entries create a '''log4j.properties''' file similar to the one below.

	log4j.logger.net.sourceforge.seqware.pipeline.plugins=DEBUG, console

	log4j.appender.console=org.apache.log4j.ConsoleAppender
	log4j.appender.console.layout=org.apache.log4j.PatternLayout
	log4j.appender.console.layout.ConversionPattern=%p %t %c - %m%n

Use '''-Dlog4j.configuration=file:./log4j.properties''' to specify the properties file when running the FileLinker command.

	java -Dlog4j.configuration=file:./log4j.properties -jar -jar seqware-distribution-<%= seqware_release_version %>-SNAPSHOT-full.jar \
	     --plugin net.sourceforge.seqware.pipeline.plugins.FileLinker -- \
	     --file-list-file /home/mtaschuk/Downloads/link_files_report.txt --workflow-accession 375894 --csv-separator ,
