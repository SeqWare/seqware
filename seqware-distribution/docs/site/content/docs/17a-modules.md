---

title:                 "Modules"
toc_includes_sections: true
markdown:              advanced

---

##  AddRGTags
net.sourceforge.seqware.pipeline.modules.BamFilters.AddRGTags

Add read group tags, update BAM header, sort by coordinate.

| Command-line option | Description |
|--------------------|--------------|
|--PicardConvert|Path to SamFormatConverter.jar|
|--PicardSort|Path to SortSam.jar|
|--RG|read group tag|
|--infile|Input BAM file|
|--java|Path to java|
|--outfile|Output BAM file (with RG tags)|
|--perl|Path to perl|
|--samtools|Path to samtools|
|--script|Path to perl script: sw_module_AddRGTags.pl|

##  AnnotateVariantDB
net.sourceforge.seqware.pipeline.modules.queryengine.AnnotateVariantDB



| Command-line option | Description |
|--------------------|--------------|
|--cache-size||

##  AnnotateWithCodingConsequence
net.sourceforge.seqware.pipeline.modules.queryengine.AnnotateWithCodingConsequence



| Command-line option | Description |
|--------------------|--------------|
|--bed-input-file||

##  AnnotateWithDbSNP
net.sourceforge.seqware.pipeline.modules.queryengine.AnnotateWithDbSNP



| Command-line option | Description |
|--------------------|--------------|
|--bed-input-file||

##  BAM2Count
net.sourceforge.seqware.pipeline.modules.quantification.BAM2Count

Parse BAM file and get count.

| Command-line option | Description |
|--------------------|--------------|
|--PicardConvert|Path to SamFormatConverter.jar|
|--bamInput|BAM input file|
|--java|Path to java|
|--medianTrxDB|Path to median length info of transcript database|
|--outfile|Out put count file|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_BAM2count.pl|
|--trxDB|Path to transcript database IDs|

##  BCtrend
net.sourceforge.seqware.pipeline.modules.qc.BCtrend

BaseCalling score trend

| Command-line option | Description |
|--------------------|--------------|
|--flagfile|Base calling score dropping flag indicator, flowcell.lane.BCflag.txt|
|--infile|per base statistics , sample.stat.txt from qualStat module|
|--outfile|Total read and unique read in a lane, sample.BCtrend.txt|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_BCtrend.pl|
|--trendPlot|Base calling score dropping trend, sample.BCtrend.png|

##  BWA
net.sourceforge.seqware.pipeline.modules.alignment.BWA

Align reads via BWA & sort by coordinate.

| Command-line option | Description |
|--------------------|--------------|
|--PicardConvert|Path to SamFormatConverter.jar|
|--PicardSort|Path to SortSam.jar|
|--bwa|Path to BWA|
|--bwa-threads|Number of threads for use with 'bwa aln'.|
|--fastaDB|Path to pre-indexed reference sequence database in fasta format|
|--fastq1|fastq format of input reads: first reads in pair if paired end; all reads if single end|
|--fastq2|fastq format of input reads: second reads in pair if paired end|
|--java|Path to java|
|--outfile|Output BAM file (reads aligned & sorted by coordinate)|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_BWA.pl|

##  BWA_0_5_9
net.sourceforge.seqware.pipeline.modules.alignment.bwa.BWA_0_5_9

Align reads via BWA & sort by coordinate.

| Command-line option | Description |
|--------------------|--------------|
|--PicardConvert|Path to SamFormatConverter.jar|
|--PicardSort|Path to SortSam.jar|
|--bwa|Path to BWA|
|--bwa-threads|Number of threads for use with 'bwa aln'.|
|--fastaDB|Path to pre-indexed reference sequence database in fasta format|
|--fastq1|fastq format of input reads: first reads in pair if paired end; all reads if single end|
|--fastq2|fastq format of input reads: second reads in pair if paired end|
|--java|Path to java|
|--outfile|Output BAM file (reads aligned & sorted by coordinate)|
|--outfile-intermediate1|Output BWA SAI file (which is an intermediate output from the aln step of bwa)|
|--save-intermediate|should the sai file be saved?|

##  Bfast
net.sourceforge.seqware.pipeline.modules.alignment.Bfast




##  BulkProvisionFiles
net.sourceforge.seqware.pipeline.modules.utilities.BulkProvisionFiles



| Command-line option | Description |
|--------------------|--------------|
|--i, --input-file|Required: input file, multiple should be specified seperately|
|--o, --output-dir|Required: output file location|
|--v, --verbose|Optional: verbose causes the S3 transfer status to display.|

##  Compress
net.sourceforge.seqware.pipeline.modules.utilities.Compress



| Command-line option | Description |
|--------------------|--------------|
|--b, --binary|Required: Binary file for compression|
|--c, --compress|Specifies we want to compress the file (Cannot be combined with --decompress)|
|--d, --decompress|Specifies we want to decompress the file (Cannot be combined with --compress)|
|--i, --input|Required: input file location|
|--k, --keep|Specifies we want to keep the original file (Default: Remove the original input file after compression)|
|--o, --output|Required: output file location|

##  ConvertBAMTranscript2Genome
net.sourceforge.seqware.pipeline.modules.BamFilters.ConvertBAMTranscript2Genome

Translate reads from transcript coordinates to genomic coordinates.

| Command-line option | Description |
|--------------------|--------------|
|--PicardConvert|Path to SamFormatConverter.jar|
|--PicardSort|Path to SortSam.jar|
|--SQheader|data file containing @SQ records for genome in SAM format|
|--TranscriptDB|data file specifying transcript-to-genome mapping, this is the flat file output of the PrepTranscriptDB module; format = each transcript on a new line, tab-delimited, 7 columns: transcript, associated gene, transcript length, genomic coordinates, transcript coordinates, & CDS start and stop in transcript coordinates; example: uc004fvz.2{tab}CDY1|9085{tab}2363{tab}chrY:26194161-26192244,26191823-26191379:-{tab}1-1918,1919-2363{tab}327{tab}1991|
|--infile|Input BAM file with reads mapped to transcript coordinates|
|--java|Path to java|
|--outfile|Output BAM file with reads mapped to genomic coordinates; <outfile>.bai is also generated.|
|--perl|Path to perl|
|--samtools|Path to samtools|
|--script|Path to perl script: sw_module_ConvertBAMTranscript2Genome.pl|

##  CreateVariantDB
net.sourceforge.seqware.pipeline.modules.queryengine.CreateVariantDB



| Command-line option | Description |
|--------------------|--------------|
|--cache-size||

##  DumpVariantDB
net.sourceforge.seqware.pipeline.modules.queryengine.DumpVariantDB



| Command-line option | Description |
|--------------------|--------------|
|--bed-output-name||

##  ExonCountsRPKM
net.sourceforge.seqware.pipeline.modules.quantification.ExonCountsRPKM

Calculates base counts, coverage, & RPKM per exon for a given BAM file and list of exons.

| Command-line option | Description |
|--------------------|--------------|
|--PicardConvert|Path to SamFormatConverter.jar|
|--exonlist|tab-delimited text file specifying exons to evaluate; only first column is used; expected exon format = chrN:positionA-positionB:strand (positionA does not need to be less than positionB)|
|--infile|Input BAM file, expects reads aligned to genomic coordinates.|
|--java|Path to java|
|--outEXON|Output file for exon summary (base counts, coverage, & RPKM).|
|--perl|Path to perl|
|--samtools|Path to samtools|
|--script|Path to perl script: sw_module_GeneCountsRPKM.pl|

##  Flat2TableTranscriptDB
net.sourceforge.seqware.pipeline.modules.alignment.Flat2TableTranscriptDB

Create & populate database tables given flat files for a transcript reference set.

| Command-line option | Description |
|--------------------|--------------|
|--BlatTable|Output table will resemble UCSC.hg19.kgTargetAli.|
|--DBhost|Host for database connnection (e.g. dbi:mysql:DBname:DBhost|
|--DBname|Database name for database connection (e.g. dbi:mysql:DBname:DBhost)|
|--ExonTable|Output table will resemble UCSC.hg19.knownGene, though not all columns are used.|
|--GeneTable|Output table will resemble UCSC.hg19.knownToLocusLink, though not all columns are used.|
|--RefLink|If gene type is [entrez], output table will resemble UCSC.hg19.refLink for example, though not all columns are used.  If gene type is [other], this table is not created.|
|--SeqTable|Output table will resemble UCSC.hg19.knownGeneMrna, though not all columns are used.|
|--genetype|[entrez] for LocusLink/EntrezIDs, [other] for anything else.|
|--infasta|(Required) input file of transcripts in FASTA format.|
|--intrmap|(Optional) input file of transcript association & coordinate data, must be in specified format (7 tab-delimited columns): TranscriptID [must exactly match fasta input], GeneID ['geneSYM|geneID' for entrez, otherwise entire string is taken as gene id, empty if no gene], TranscriptLength, GenomicCoordinates [from pairwise transcript/genome alignment; should always correspond with transcript coordinates (which are always ascending), thus a transcript mapped to the + strand should have ascending genomic coordinates (chrX:1000-3000,50000-55000:+) while a transcript mapped to the - strand should have descending genomic coordinates (chrX:55000-50000,3000-1000:-); empty if no mapping], TranscriptCoordinates [from pairwise transcript/genome alignment; should always be ascending regardless of which genome strand the transcript maps to; empty if no mapping], CDSstart_Transcript [transcript position of the first base of the CDS; 'unk' if no or unknown CDS], CDSend_Transcript [transcript position of the last base of the CDS; 'unk' if no or unknown CDS]|
|--outsummary|Output text file line count per new table.  Not generated if all line counts = 0.|
|--password|Password for database connection.|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_Flat2TableTranscriptDB.pl|
|--username|Username for database connection.|

##  GeneCountsRPKM
net.sourceforge.seqware.pipeline.modules.quantification.GeneCountsRPKM

Calculates read counts, coverage, & RPKM per transcript and per gene for a given BAM file.

| Command-line option | Description |
|--------------------|--------------|
|--PicardConvert|Path to SamFormatConverter.jar|
|--TranscriptDB|data file specifying transcript-to-genome mapping, this is the flat file output of the PrepTranscriptDB module; format = each transcript on a new line, tab-delimited, 7 columns: transcript, associated gene, transcript length, genomic coordinates, transcript coordinates, & CDS start and stop in transcript coordinates; example: uc004fvz.2{tab}CDY1|9085{tab}2363{tab}chrY:26194161-26192244,26191823-26191379:-{tab}1-1918,1919-2363{tab}327{tab}1991|
|--genelength|[median,mean,shortest,longest] of transcripts in TranscriptDB; note that the calculated mean & median are rounded to the nearest integer|
|--infile|Input BAM file, expects reads aligned to transcripts.|
|--java|Path to java|
|--outGENE|Output file for gene summary (read counts, coverage, & RPKM).|
|--outTR|Output file for transcript summary (read counts, coverage, & RPKM).|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_GeneCountsRPKM.pl|

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

##  GenericQCGenome
net.sourceforge.seqware.pipeline.modules.qc.GenericQCGenome

Alignment against various reference sequence sets

| Command-line option | Description |
|--------------------|--------------|
|--DBlist|tab-delimited text file specifying reference sequence DBs; 3 columns = DB name, path to FASTA, alignment type [BWA or miRNA]; note that for BWA alignments, the script assumes the FASTA is already BWA-indexed|
|--bwa|Path to BWA|
|--fastq|fastq format of input reads|
|--outfile|tab-delimited text file, %aligned to each sequence DB|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_GenericQC.pl|

##  IllQualStat
net.sourceforge.seqware.pipeline.modules.qc.IllQualStat

Parses quality report from Illumina pipeline (Gerald)

| Command-line option | Description |
|--------------------|--------------|
|--lane|An integer between 1 and 8|
|--outfile|flowcell.lane.vendorQCsummary.txt|
|--perl|Path to perl|
|--runFolder|A path to the runfolder for a particular flowcell|
|--script|Path to perl script: sw_module_IllQualStat.pl|

##  Illumina2SRF
net.sourceforge.seqware.pipeline.modules.io.Illumina2SRF

This converts a given lane/barcode combo in the Illumina run folder to an SRF file.

| Command-line option | Description |
|--------------------|--------------|
|--barcode|CURRENTLY IGNORED: this will let you pass in the barcode sequence. The net result is an SRF file that only contains reads with this barcode.|
|--illumina2srf-bin|This is the path to the binary executable illumina2srf that does the heavy lifting here|
|--lane|The lane to convert (starting at 1). If the code for this module breaks it's likely here if Illumina changes its file naming conventions. It currently uses <qseq-path>/s_<lane>_*_qseq.txt|
|--num-qseq-files|The number of <qseq-path>/s_<lane>_*_qseq.txt files per lane. As of RTA 1.6.x this is 120 on the Illumina GAII. Clearly this value can/will change|
|--num-reads|The number of reads, for the current Illumina platform this is 1 or 2.|
|--output-file|This is the output SRF file name|
|--qseq-path|This is the path to qseq files in an Illumina-supplied run folder|

##  MakeBigWig
net.sourceforge.seqware.pipeline.modules.alignment.MakeBigWig

Make bigWig given sorted BAM (reads aligned to genome).

| Command-line option | Description |
|--------------------|--------------|
|--bamfile|input: sorted BAM, reads aligned to genomic coordinates|
|--bedGraphToBigWig|Path to UCSC binary tool bedGraphToBigWig|
|--chrsizes|chrom.sizes for genome to which reads are aligned; output for UCSC binary tool fetchChromSizes|
|--outfile|output: bigWig|
|--perl|Path to perl|
|--samtools|Path to samtools|
|--script|Path to perl script: sw_module_MakeBigWig.pl|

##  MapSplice
net.sourceforge.seqware.pipeline.modules.alignment.MapSplice

Align reads via MapSplice.

| Command-line option | Description |
|--------------------|--------------|
|--B|The path and basename of index to be searched by Bowtie. |
|--DEBUG|Turn debug output on.|
|--E|The maximum number of mismatches (Hamming distance) that are allowed in a unspliced aligned read and segment. The default is 1. Must be in range [0-3]|
|--L|Length of segment reads|
|--Q|Format of input reads, FASTA OR FASTQ|
|--R|The maximum number of mismatches that are allowed for remapping. The default is 2. Should be in range [0-3]|
|--S|?|
|--X|Number of threads to run bowtie to map reads|
|--bam|BAM input, unmapped reads will be extracted and used|
|--c|The directory containing the sequence files corresponding to the reference genome (in FASTA format)|
|--full-running|If specified, run remap step to increase the junction coverage|
|--fusion|If specified, output fusion junctions (reads should be long enough to be divided into more than 2 segments for fusion alignment). Reads not aligned as normal unspliced or spliced alignments are consider as fusion candidates. The outputs are "fusion.junction" and "fusion_junction.unique"|
|--fusion-non-canonical|Whether or not the semi-canonical and non-canonical junctions should be outputted.|
|--i|The "minimum intron length". Mapsplice will not report the alignment with a gap less than these many bases. The default is 10.|
|--log-file|This is the output log file name|
|--m|The maximum number of mismatches that are allowed in a segment crossing splice junction. The default is 1.|
|--map-segments-directly|If yes, MapSplice will try to find spliced alignments and unspliced alignments of a read, and select best alignment. (will use more running time)|
|--max-hits|max_hits x 10 is the maximal repeated hits during segments mapping and reads mapping(default is 4 x 10 = 40) |
|--min-output-seg|An option to output incomplete alignments. The minimal number of segments contained in alignment. eg. If read length is 75bp, segment_length is 25, set min_output_seg = 2 will output 50bp alignments if there are no 75bp alignments for the corresponding reads. The default is output alignments of full read length .|
|--n|The anchor length that will be used for single anchored spliced alignment|
|--non-canonical|Whether or not the semi-canonical and non-canonical junctions should be outputted. If --non-canonical specified, output all junctions. If --semi-canonical specified, output semi-canonical and canonical junctions. If none of them specified, only output canonical junction.|
|--not-rem-temp|If specified, not remove tmp directory after MapSplice is finished running|
|--o|The name of the directory in which MapSplice will write its output. The default is "mapsplice_out/" under current directory where you run MapSplice. |
|--output-dir|The output directory|
|--pairend|Whether or not the input reads are paired end or single|
|--pyscript|Path to MapSplice python script: mapsplice_segments.py|
|--python|Path to python|
|--r|The maximal small indels length(default is 3, suggested to be in [0-3])|
|--run-MapPER|If specified, run MapPER and generate reads mappings based on a probabilistic framework (39), valid for PER reads|
|--search-whole-chromosome| If specified, search whole chromosomes to find splice alignment, instead of searching exonic regions. Able to find small exons which have length < segment length at head and tai, but will use more running time.|
|--w|Input read length, read length can be arbitrary long|
|--x|The "maximum intron length". Mapsplice will not report the alignment with a gap longer than these many bases apart for single anchored spliced alignment. The default is 200000.|

##  Picard
net.sourceforge.seqware.pipeline.modules.Picard




##  PrepQCgenomes
net.sourceforge.seqware.pipeline.modules.qc.PrepQCgenomes

Make datafiles files for QC Genomes sequences (human rRNA + viral genomes).

| Command-line option | Description |
|--------------------|--------------|
|--outputDIR|directory for ~.fa and ~.key output files|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_PrepQCgenomes.pl|

##  PrepTranscriptDB
net.sourceforge.seqware.pipeline.modules.alignment.PrepTranscriptDB

Generate transcript reference set given database tables.

| Command-line option | Description |
|--------------------|--------------|
|--BlatTable|See UCSC.hg19.kgTargetAli for example.  The following query must be successful: SELECT strand,qName,qSize,tName,blockSizes,qStarts,tStarts FROM BlatTable.|
|--DBhost|Host for database connnection (e.g. dbi:mysql:DBname:DBhost|
|--DBname|Database name for database connection (e.g. dbi:mysql:DBname:DBhost)|
|--ExonTable|See UCSC.hg19.knownGene for example.  The following query must be successful: SELECT name,txStart,txEnd,cdsStart,cdsEnd FROM ExonTable.|
|--GeneTable|See UCSC.hg19.knownToLocusLink for example.  If gene type is [entrez], the following query must be successful: SELECT DISTINCT GeneTable.name,GeneTable.value,RefLink.name FROM GeneTable LEFT JOIN RefLink.If gene type is [other], the following query must be successful: SELECT name,value FROM GeneTable.|
|--RefLink|See UCSC.hg19.refLink for example.  If gene type is [entrez], see GeneTable for required query. If gene type is [other], this table is not used.|
|--SeqTable|See UCSC.hg19.knownGeneMrna for example.  The following query must be successful: SELECT name,seq FROM SeqTable.|
|--bwa|Path to BWA|
|--canonical|[yes]/[no]: restrict transcripts to only those entries mapped to canonical human chromosomes (chr1-22,X,Y,M).|
|--genetype|[entrez] for LocusLink/EntrezIDs, [other] for anything else.|
|--outcomposite|Output file of composite transcripts & pairwise mapping info.|
|--outexon|Output file of composite exons.|
|--outfasta|Output file of transcripts in FASTA format.|
|--outgtf|Output file of composite transcripts in GTF format.|
|--outinfo|Output file of transcript association & coordinate data.|
|--outjunction|Output file of known splice junctions.|
|--password|Password for database connection.|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_PrepTranscriptDB.pl|
|--username|Username for database connection.|

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
|--r, --recursive|Optional: if the input-file points to a local directory then this option will cause the program to recursively copy the directory and its contents to the destination. An actual copy will be done for local to local copies rather than symlinks.|
|--s3-connection-timeout|Optional: Sets the amount of time to wait (in milliseconds) when initially establishing a connection before giving up and timing out. Default is 50000|
|--s3-max-connections|Optional: Sets the maximum number of allowed open HTTPS connections. Default is 50|
|--s3-max-error-retries|Optional: Sets the maximum number of retry attempts for failed retryable requests (ex: 5xx error responses from services). Default is 3|
|--s3-max-socket-timeout|Optional: Sets the amount of time to wait (in milliseconds) for data to be transfered over an established, open connection before the connection times out and is closed. A value of 0 means infinity, and isn't recommended. Default is 50000|
|--s3-no-server-side-encryption|Optional: If specified, do not use S3 server-side encryption. Default is to use S3 server-side encryption for S3 destinations.|
|--skip-if-missing|Optional: useful for workflows with variable output files, this will silently skip any missing inputs (this is a little dangerous).|
|--v, --verbose|Optional: verbose causes the S3 transfer status to display.|

##  QCGenome
net.sourceforge.seqware.pipeline.modules.qc.QCGenome

Getting qc genome assessment

| Command-line option | Description |
|--------------------|--------------|
|--bwa|Path to BWA|
|--fastq1|fastq format of input reads: first reads in pair if paired end; all reads if single end|
|--fastq2|fastq format of input reads: second reads in pair if paired end|
|--outfile|QC genome assessment: flowcell.lane.qcGenome.txt|
|--perl|Path to perl|
|--qcdbfa|Path to QC genome database in fasta format|
|--qcdbkey|QC Genomes sequence key, 2 tab-delimited columns: sequence accession & type (e.g. rRNA, viral)|
|--script|Path to perl script: sw_module_RiboAndViralAln.pl|

##  RemoveIndelsAndTrim
net.sourceforge.seqware.pipeline.modules.BamFilters.RemoveIndelsAndTrim



| Command-line option | Description |
|--------------------|--------------|
|--f, --fasta|Required: The reference fasta file|
|--filter-script|Required: The filter script|
|--i, --input|Required: The input file|
|--o, --output|Required: The output file|
|--perl-path|Required: Perl binary location|
|--samtools-binary|Required: Samtools binary location|
|--trim-size|Required: Number of bases to trim off|

##  RemoveSingleIndelNotOnEnds
net.sourceforge.seqware.pipeline.modules.BamFilters.RemoveSingleIndelNotOnEnds



| Command-line option | Description |
|--------------------|--------------|
|--f, --fasta|Required: The reference fasta file|
|--filter-script|Required: The filter script|
|--i, --input|Required: The input file|
|--o, --output|Required: The output file|
|--perl-path|Required: Perl binary location|

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

##  SNVMix
net.sourceforge.seqware.pipeline.modules.SNVMix

This module implements the SVNMix 

| Command-line option | Description |
|--------------------|--------------|
|--C, --F, --T||
|--script|Path to C executable: SNVMix|
[SeqWare Pipeline] ERROR [2013/04/03 16:48:15] | INPUT: null
[SeqWare Pipeline] ERROR [2013/04/03 16:48:15] | INPUT: null

##  SRF2Fastq
net.sourceforge.seqware.pipeline.modules.io.SRF2Fastq




##  SRF2Fastq_0_7_2
net.sourceforge.seqware.pipeline.modules.io.SRF2Fastq_0_7_2




##  SamTools
net.sourceforge.seqware.pipeline.modules.SamTools




##  SpliceJunctionCounts
net.sourceforge.seqware.pipeline.modules.quantification.SpliceJunctionCounts

Calculates raw read counts per junction given BAM file and list of splice junctions.

| Command-line option | Description |
|--------------------|--------------|
|--PicardConvert|Path to SamFormatConverter.jar|
|--infile|Input BAM file, expects reads aligned to genomic coordinates.|
|--java|Path to java|
|--junclist|tab-delimited text file specifying junctions to evaluate; only first column is used; expected exon format = chrN:positionA,chrN:positionB or chrN:positionA:strand,chrN:positionB:strand (positionA does not need to be less than positionB); designed to handle only intra-chromosomal junctions|
|--outSJ|Output file for splice junction summary (raw read counts).|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_SpliceJunctionCounts.pl|

##  TrimAdapter
net.sourceforge.seqware.pipeline.modules.alignment.TrimAdapter

Trim reads to remove adapter segments before alignment.

| Command-line option | Description |
|--------------------|--------------|
|--infastq|input: fastq format of reads|
|--outfastq|required output: fastq format of trimmed reads|
|--outstats|optional output: some trimming statistics (distribution of effective read lengths, removed reads, etc)|
|--perl|Path to perl|
|--primerseq|sequencing primer; adapter is reverse complement of this sequence|
|--script|Path to perl script: sw_module_TrimAdapter.pl|

##  TrimAllReads
net.sourceforge.seqware.pipeline.modules.alignment.TrimAllReads

Trim all reads to a specific length.

| Command-line option | Description |
|--------------------|--------------|
|--infastq|input: reads in fastq format|
|--outfastq|output: trimmed reads in fastq format|
|--perl|Path to perl|
|--readsize|length (in bases) of output reads|
|--script|Path to perl script: sw_module_TrimAllReads.pl|

##  TrimCountAdapter
net.sourceforge.seqware.pipeline.modules.alignment.TrimCountAdapter

Trim reads to remove adapter segments before alignment & quantify adapter contamination.

| Command-line option | Description |
|--------------------|--------------|
|--infastq|input: fastq format of reads|
|--outfastq|required output: fastq format of trimmed reads|
|--outqc|required output: quantification of reads containing adapter|
|--outstats|optional output: some trimming statistics (distribution of effective read lengths, removed reads, etc)|
|--perl|Path to perl|
|--primerseq|sequencing primer; adapter is reverse complement of this sequence|
|--script|Path to perl script: sw_module_TrimCountAdapter.pl|

##  adapterCont
net.sourceforge.seqware.pipeline.modules.qc.adapterCont

Getting adapter contamination conditino

| Command-line option | Description |
|--------------------|--------------|
|--adapterSeq|adapter sequence to detect in the Illumina raw sequence file|
|--infile|fastq format Illumina raw file|
|--outfile|adapter contamination report, adapter_cont.txt|
|--perl|Path to perl|
|--script|Path to perl script: calculate_adapter_contamination.pl|

##  alignStat
net.sourceforge.seqware.pipeline.modules.qc.alignStat

Provide post alignment stat mapped reads to a database.

| Command-line option | Description |
|--------------------|--------------|
|--PicardConvert|Path to SamFormatConverter.jar|
|--infile|Input BAM file.|
|--java|Path to java|
|--outfile|Output file flowcell.lane.alignStat.txt|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_alignStat.pl|

##  basicQC
net.sourceforge.seqware.pipeline.modules.qc.basicQC



| Command-line option | Description |
|--------------------|--------------|
|--R, --path-to-R|Required: The path to the R binary|
|--color-space, --cs|Required for pre: Which space to use|
|--dqc, --path-to-dqc|Required: The path to the dqc binary|
|--f, --fasta-file|Required for post: Path to fasta file|
|--ib, --input-bam|Required for post: Input file|
|--if, --input-fastq|Required for pre: Input file|
|--r, --insert-size-range|Required for post: Insert size range|
|--m, --minimum-mapping-quality|Required for post: Minimum mapping quality|
|--o, --output-prefix|Required for pre and post: Prefix for output files|
|--post|Do post-alignment QC|
|--pre|Do pre-alignment QC|

##  bmfsplit
net.sourceforge.seqware.pipeline.modules.bmfsplit




##  collectFlags
net.sourceforge.seqware.pipeline.modules.qc.collectFlags

Collecting available qc flags

| Command-line option | Description |
|--------------------|--------------|
|--infile|flowcell.lane.srf/fastq|
|--outfile|Collected flag information flowcell.lane.flagCollection.txt|
|--perl|Path to perl|
|--qcFilePath|A path to all the qc files|
|--script|Path to perl script: sw_module_extractFlag.pl|

##  coverageXTranscript
net.sourceforge.seqware.pipeline.modules.qc.coverageXTranscript

Getting relative coverage cross transcript plot

| Command-line option | Description |
|--------------------|--------------|
|--Rcom|Path to R, /urs/bin/R|
|--Rscript|Path to R: /home/jyli/svnroot/seqware/trunk/seqware-pipeline/R/plots_TRcov.R|
|--bamfile|aligned bam to human transcript database|
|--coveragePlotFile|output figure file|
|--mapfile|~/svnroot/seqware-complete/trunk/seqware-pipeline/data/annotation_reference/hg19_transcripts.hg19.20091027.trmap|
|--outfile|two column no header column 1 is relative position in transcript [0-100] and column 2 is counts|
|--perl|Path to perl|
|--samtoolPath|/usr/bin/samtools|
|--script|Path to perl script: sw_module_CoverageAcrossTranscript.pl|

##  dqc
net.sourceforge.seqware.pipeline.modules.qc.dqc



| Command-line option | Description |
|--------------------|--------------|
|--R, --path-to-R|Required: The path to the R binary|
|--color-space, --cs|Required for pre: Which space to use|
|--dqc, --path-to-dqc|Required: The path to the dqc binary|
|--f, --fasta-file|Required for post: Path to fasta file|
|--ib, --input-bam|Required for post: Input file|
|--if, --input-fastq|Required for pre: Input file|
|--r, --insert-size-range|Required for post: Insert size range|
|--m, --minimum-mapping-quality|Required for post: Minimum mapping quality|
|--o, --output-prefix|Required for pre and post: Prefix for output files|
|--post|Do post-alignment QC|
|--pre|Do pre-alignment QC|

##  geneCoverage
net.sourceforge.seqware.pipeline.modules.qc.geneCoverage

Getting gene coverage plot

| Command-line option | Description |
|--------------------|--------------|
|--Rcom|Path to R, /urs/bin/R|
|--Rscript|Path to R: /home/jyli/svnroot/seqware/trunk/seqware-pipeline/R/plot_geneCoverage.R|
|--geneCoveragePlot|output figure file|
|--infile|gene level quantification file|
|--outfile|gene coverage output file with 2 column, average coverage & # of genes|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_geneCoverage.pl|

##  plotStats
net.sourceforge.seqware.pipeline.modules.qc.plotStats

Per lane based statistics plottings

| Command-line option | Description |
|--------------------|--------------|
|--boxplot|Per base calling scores box plot, sample_bxp.png|
|--fastxBoxplot|/usr/bin/fastq_quality_boxplot_graph.sh|
|--fastxNTdistribution|/usr/bin/fastx_nucleotide_distribution_graph.sh|
|--infile|fastq format Illumina raw file|
|--ntDistribution|Per base/cycle % nucleotide distribution sample_nt_distr.png|

##  qualFilter
net.sourceforge.seqware.pipeline.modules.qc.qualFilter

Getting quality filtering condition

| Command-line option | Description |
|--------------------|--------------|
|--infile|fastq format Illumina raw file|
|--outfile|quality filtering procedure (cost time) , flowcell.lane.qualFilter.txt|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_qualFilter.pl|

##  qualStat
net.sourceforge.seqware.pipeline.modules.qc.qualStat

Per lane based statistics

| Command-line option | Description |
|--------------------|--------------|
|--convertScript|Path to perl script: sanger2Ill.pl|
|--fastxStats|/usr/bin/fastx_quality_stats |
|--infile|fastq format Illumina raw file|
|--outSummary|25, 50 and 75% on specific cycle,sample.stat.summary.txt|
|--outfile|per base statistics , sample.stat.txt|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_perBaseStat.pl|

##  reportCardSingle
net.sourceforge.seqware.pipeline.modules.quantification.reportCardSingle

Parse BAM file and get count.

| Command-line option | Description |
|--------------------|--------------|
|--PicardConvert|Path to SamFormatConverter.jar|
|--bamInput|BAM input file|
|--java|Path to java|
|--medianTrxDB|Path to median length info of transcript database|
|--outfile|Out put count file|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_BAM2count.pl|
|--trxDB|Path to transcript database IDs|

##  srf2fastq
net.sourceforge.seqware.pipeline.modules.srf2fastq




##  uniqRead
net.sourceforge.seqware.pipeline.modules.qc.uniqRead

Per lane based statistics

| Command-line option | Description |
|--------------------|--------------|
|--infile|fastq format Illumina raw file|
|--outfile|Total read and unique read in a lane, flowcell.lane.uniqRead.txt|
|--perl|Path to perl|
|--script|Path to perl script: sw_module_readDepth.pl|
