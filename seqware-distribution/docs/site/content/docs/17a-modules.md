---

title:                 "Modules"
toc_includes_sections: true
markdown:              advanced

---

##  BulkProvisionFiles
net.sourceforge.seqware.pipeline.modules.utilities.BulkProvisionFiles



| Command-line option | Description |
|--------------------|--------------|
|--i, --input-file|Required: input file, multiple should be specified seperately|
|--o, --output-dir|Required: output file location|
|--v, --verbose|Optional: verbose causes the S3 transfer status to display.|

##  GenericCommandRunner
net.sourceforge.seqware.pipeline.modules.GenericCommandRunner

This is a simple command runner.

| Command-line option | Description |
|--------------------|--------------|
|--gcr-algorithm|You can pass in an algorithm name that will be recorded in the metadb if you are writing back to the metadb, otherwise GenericCommandRunner is used.|
|--gcr-check-output-file|Specify the path to the file.|
|--gcr-command|The command being executed.|
|--gcr-output-file|Specify this option one or more times for each output file created by the command called by this module. The argument is a '::' delimited list of type, meta_type, and file_path.|
|--gcr-skip-if-missing|If the registered output files don't exist don't worry about it. Useful for workflows that can produce variable file outputs but also potentially dangerous.|
|--gcr-skip-if-output-exists|If the registered output files exist then this step won't be run again. This only works if gcr-output-file is defined too since we need to be able to check the output files to see if they exist. If this step produces no output files then it's hard to say if it was run successfully before.|

##  GenericMetadataSaver
net.sourceforge.seqware.pipeline.modules.GenericMetadataSaver

This is a simple metadata saver.

| Command-line option | Description |
|--------------------|--------------|
|--gms-algorithm|You can pass in an algorithm name that will be recorded in the metadb if you are writing back to the metadb.|
|--gms-output-file|Specify this option one or more times for each output file created by the command called by this module. The argument is a '::' delimited list of type, meta_type, and file_path.|
|--gms-suppress-output-file-check|If provided, this will suppress checking that the gms-output-file options contain valid file paths. Useful if these are remote resources like HTTP or S3 file URLs.|

##  ProvisionDependenciesBundle
net.sourceforge.seqware.pipeline.modules.utilities.ProvisionDependenciesBundle



| Command-line option | Description |
|--------------------|--------------|
|--i, --input-file|Required: input file, multiple should be specified seperately|
|--o, --output-dir|Required: output file location|

##  ProvisionFiles
net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles



| Command-line option | Description |
|--------------------|--------------|
|--a, --algorithm|Optional: by default the algorithm is 'ProvisionFiles' but you can override here if you like.|
|--d, --decrypt-key|Optional: if specified this key will be used to decrypt data when reading from its source.|
|--decrypt-key-from-settings, --dkfs|Optional: if flag is specified then the key will be read from the SW_DECRYPT_KEY field in your SeqWare settings file and used to decrypt data as its pulled from the source.  If this option is specified along with --decrypt-key the key provided by the latter will be used.|
|--e, --encrypt-key|Optional: if specified this key will be used to encrypt data before writing to its destination.|
|--ekfs, --encrypt-key-from-settings|Optional: if flag is specified then the key will be read from the SW_ENCRYPT_KEY field in your SeqWare settings file and used to encrypt data before writing to its destination.  If this option is specified along with --encrypt-key the key provided by the latter will be used.|
|--force-copy|Optional: if this is specified local to local file transfers are done with a copy rather than symlink. This is useful if you're writing to a temp area that will be deleted so you have to move the file essentially.|
|--i, --input-file|Required: use this or --input-file-metadata, this is the input file, multiple should be specified seperately|
|--im, --input-file-metadata|Required: use this or --input-file, this is the input file, multiple should be specified seperately|
|--o, --output-dir|Required: output file location|
|--of, --output-file|Optional: output file path, if this is provided than the program accepts exactly one --input-file and one --output file. If an --output-dir is also specified an error will be thrown.|
|--r, --recursive|Optional: if the input-file points to a local directory then this option will cause the program to recursively copy the directory and its contents to the destination. An actual copy will be done for local to local copies rather than symlinks.|
|--s3-connection-timeout|Optional: Sets the amount of time to wait (in milliseconds) when initially establishing a connection before giving up and timing out. Default is 50000|
|--s3-max-connections|Optional: Sets the maximum number of allowed open HTTPS connections. Default is 50|
|--s3-max-error-retries|Optional: Sets the maximum number of retry attempts for failed retryable requests (ex: 5xx error responses from services). Default is 3|
|--s3-max-socket-timeout|Optional: Sets the amount of time to wait (in milliseconds) for data to be transfered over an established, open connection before the connection times out and is closed. A value of 0 means infinity, and isn't recommended. Default is 50000|
|--s3-no-server-side-encryption|Optional: If specified, do not use S3 server-side encryption. Default is to use S3 server-side encryption for S3 destinations.|
|--skip-if-missing|Optional: useful for workflows with variable output files, this will silently skip any missing inputs (this is a little dangerous).|
|--v, --verbose|Optional: verbose causes the S3 transfer status to display.|

##  S3CreateFileURLs
net.sourceforge.seqware.pipeline.modules.utilities.S3CreateFileURLs



| Command-line option | Description |
|--------------------|--------------|
|--a, --all-files|Optional: if specified, the --s3-url should take the form s3://<bucket>. This option indicates all files in that bucket should have URLs created.|
|--l, --lifetime|How long (in minutes) should this URL be valid for (129600 = 90 days, 86400 = 60 days, 43200 = 30 days, 10080 = 7 days, 1440 = 1 day).|
|--u, --s3-url|A URL of the form s3://<bucket>/<path>/<file> or s3://<bucket> if using the --all-files option|

##  S3DeleteFiles
net.sourceforge.seqware.pipeline.modules.utilities.S3DeleteFiles



| Command-line option | Description |
|--------------------|--------------|
|--f, --s3-url-file|Optional: a file containing one URL per line of the form s3://<bucket>/<path>/<file>|
|--u, --s3-url|Optional: a URL of the form s3://<bucket>/<path>/<file>|

##  S3ListFiles
net.sourceforge.seqware.pipeline.modules.utilities.S3ListFiles



| Command-line option | Description |
|--------------------|--------------|
|--in-bytes|Optional: flag, if set values print in bytes rather than human friendsly|
|--l, --list-buckets|Optional: list all the buckets you own.|
|--reset-owner-permissions|Optional: this will give the bucket owner full read/write permissions, useful if many different people have been writing to the same bucket.|
|--s, --search-local-dir|Optional: attempt to match files in S3 with files in this local directory.|
|--u, --s3-url|Optional: a URL of the form s3://<bucket>/<path>/<file>|
|--t, --tab-output-file|Optional: tab-formated output file.|

##  S3UploadDirectory
net.sourceforge.seqware.pipeline.modules.utilities.S3UploadDirectory



| Command-line option | Description |
|--------------------|--------------|
|--b, --output-bucket|Required: the output bucket name in S3|
|--i, --input-dir|Required: the directory to copy recursively|
|--p, --output-prefix|Required: the prefix to add after the bucket name.|
