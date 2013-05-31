---

title:                 "Module Conventions"
toc_includes_sections: true
markdown:              advanced
is_dynamic:            true

---

In order for SeqWare Pipeline modules to work well within complex workflows we need to agree on some naming conventions, that's the purpose of this document.

## Background

Each module in SeqWare Pipeline results in the creation of a processing table entry.  And every processing table entry is associated with 0 or more files noted in the file table.  See below.  The processing table stores the status, stderr/out, description and other fields while the file rows store file path, mime type, and URL are stored.  A module does not directly input data into these tables. Instead it populates a ReturnValue object which the module runner then uses to populate these tables.  This ReturnValue object contains an array of FileMetadata objects, each of which contain the mime type along with other information such as file path.

						       Table "public.processing"
	       Column        |            Type             |                             Modifiers                              
	---------------------+-----------------------------+--------------------------------------------------------------------
	 processing_id       | integer                     | not null default nextval('processing_processing_id_seq'::regclass)
	 workflow_run_id     | integer                     | 
	 algorithm           | text                        | 
	 status              | text                        | 
	 description         | text                        | 
	 url                 | text                        | 
	 url_label           | text                        | 
	 version             | text                        | 
	 parameters          | text                        | 
	 stdout              | text                        | 
	 stderr              | text                        | 
	 exit_status         | integer                     | 
	 process_exit_status | integer                     | 
	 task_group          | boolean                     | default false
	 sw_accession        | integer                     | default nextval('sw_accession_seq'::regclass)
	 create_tstmp        | timestamp without time zone | not null
	 update_tstmp        | timestamp without time zone | 

				       Table "public.file"
	    Column    |  Type   |                       Modifiers
	--------------+---------+--------------------------------------------------------
	 file_id      | integer | not null default nextval('file_file_id_seq'::regclass)
	 file_path    | text    | not null
	 url          | text    | 
	 url_label    | text    | 
	 type         | text    | 
	 meta_type    | text    | 
	 description  | text    | 
	 sw_accession | integer | default nextval('sw_accession_seq'::regclass)

## Module Algorithm Values

The algorithm values fro the processing table should be distinct for each module.  For example, here are a sampling of the current algorithms:

* ConvertBAMTranscript2Genome
* AddRGTags

Our convention is to simply use the module name here.  If that's not unique, add in the package path to make it unique, separated by "-" rather than "." as you would do in Java. For example if there are two Foo modules (alignment.foo and utilities.foo) you could distinguish them by:

	 utilities-foo
	 alignment-foo

## Module Status Values

The processing table actually has 3 places to store status.  The "status" field should be used for a human readable status field and is actually used by SeqWare LIMS in it's display of processing records.  The "exit_status" field is used by the modules to track their own state/error state and potential values are constants defined in the ReturnValue object. Finally the "process-exit-status" field is used 

### Status 

Every processing event contains a status field and this is primarily a human-readable status message.  Module authors can write whatever status message they want here.  However, the SeqWare LIMS expects a few standard values in this field:

* processing
* processed 
* error

For this reason it's recommended that modules follow this convention and use one of these three values.

### Exit Status

The authoritative object that includes the exit status codes is net.sourceforge.seqware.common.module.ReturnValue.  Please look there for the latest list but this should be fairly complete:

	  // generally it's a good idea to offset by 10 so if new ones need to be added
	  // they can be added "between" existing constants
	  public static final int NULL = -99;
	  public static final int NOTIMPLEMENTED = -1;
	  public static final int SUCCESS = 0;
	  public static final int PROGRAMFAILED = 1;
	  public static final int INVALIDPARAMETERS = 2;
	  public static final int DIRECTORYNOTREADABLE = 3;
	  public static final int FILENOTREADABLE = 4;
	  public static final int FILENOTWRITABLE = 5;
	  public static final int RUNTIMEEXCEPTION = 6;
	  public static final int INVALIDFILE = 7;
	  public static final int METADATAINVALIDIDCHAIN = 8; // Problem either getting
	  // parentID or setting
	  // processingID to a file
	  // for the next job
	  public static final int INVALIDARGUMENT = 9;
	  public static final int FILENOTEXECUTABLE = 10;
	  public static final int DIRECTORYNOTWRITABLE = 11;
	  public static final int FILEEMPTY = 12;
	  public static final int SETTINGSFILENOTFOUND = 13;
	  public static final int ENVVARNOTFOUND = 14;
	  public static final int FAILURE = 15;
	  public static final int FREEMARKEREXCEPTION = 70;
	  public static final int DBCOULDNOTINITIALIZE = 80;
	  public static final int DBCOULDNOTDISCONNECT = 81;
	  public static final int SQLQUERYFAILED = 82;
	  public static final int STDOUTERR = 90; // Problem when trying to redirect
	  // stdout to a file
	  public static final int RUNNERERR = 91; // Some problem internal to the runner
	  // these can be used to indicate a module is queued or currently running
	  public static final int PROCESSING = 100;
	  public static final int QUEUED = 101;
	  public static final int RETURNEDHELPMSG = 110;
	  public static final int INVALIDPLUGIN = 120;

### Process Exit Status

This is simply the value returned by the command line tool called by a given module.  If multiple command line tools are called it's the job of the module to decide what int value to return here.

## Module MIME Types

<%= render '/includes/MIME/' %>
