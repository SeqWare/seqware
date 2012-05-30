AUTHOR:       boconnor@gmail.com
PROJECT:      SeqWare Query Engine
LAST UPDATED: 20120530

REQUIREMENTS

* java 1.6 or greater
* BerkeleyDB 4.7
* Maven 2.2.x for our build system

SEE ALSO

The latest docs can be found at http://seqware.sf.net

BUILDING

We use Maven for our build system.  You should be able to build the Query
Engine by going into the seqware-queryengine directory from source control and
doing a:

 mvn clean install

or if you want to skip the tests:

 mvn clean install -Dmaven.test.skip=true

The tests set to run are for BerkeleyDB and the binary libraries needed for
this should be pulled in by Maven. You'll need to do some configuration and
customization of the pom.xml (look for "groups") to get the HBase and/or
PostgreSQL tests to run.  Once configured, you can trigger the tests using the
above.

DESCRIPTION

This project is the legacy seqware query engine, which is currently being used 
as a guide to and an inspiration for the current seqware query engine. 

Everything beyond this point is fairly dated, probably quite out of date and
not helpful (we're not using ant anymore for example). It was originally
written in 2009 and a lot has changed since then...

This project seeks to develop a web-based query engine and backend tools for
dealing with raw sequence reads, alignments, and mismatch/coverage information
(BED/WIG).  The Short Read Format (SRF) will be used to store raw sequences,
the Binary Sequence Alignment/Map format (SAM/BAM) will be used for storing
alignments, and Binary BED/Binary WIG (BBED/BWIG) files will be used to store
mismatch and coverage information for a given alignment.  The first two files
are based on existing standards while the BBED/BWIG are custom formats based on
BED/WIG files made popular via the UCSC genome browser.  The BBED/BWIG files
were created using BerkeleyDB but they may be replaced with another format in
the future (database, distributed database, other flat file format etc).

The first goal of this project is to model the "pileup" output from the SAM
toolchain as binary files (BBED/BWIG).  Overall, what feeds into the query
engine looks like:

    sequencing of sample
        |
        V
    sequence in SRF format
        |
        V
    alignment
        |
        V
    alignment output in binary SAM (BAM) format
        |
        V
    SNP/indel calls using SAM Tools pileup command
        |
        V
    pileup to BBED/BWIG format
        |
        V
    loading files into "query engine"

These BBED/BWIG files are built using Berkeley DB but could also use a real
database.  They represent an efficient way of storing and querying the
information in a pileup record where there's a mismatch between the consensus
and what was sequenced (either homo or heterozygous) given whatever cutoff the
enduser sepcifies. These files should be versioned and each version should be
documented.  As the format is improved the version should be updated so that
parsers will know if they can/can't parse a given file.  For the BBED file the
first primary record will store pileup information and one secondary index
(genomic location) is required. For the BWIG file the primary record will be
1000 base windows of the genome with position/count pairs.

Once the SRF, BAM, and BBED/BWIG are created they are injected into the
solexatools DB backend as "processing" records.  These processing events might
be associated with samples or lanes or other entities in the DB.  These
particular files are then accessible via a DAS/2-like server interface.  At one
level, the interface provides a way to browse XML documents 

DIRECTORIES:

PROTOTYPES/BINARY_ALIGNMENT_FILE_FORMAT:

Deprecated, prototype for storing alignment files.  Not really used since we
found SAM/BAM format, see http://sf.net/projects/samtools.

PROTOTYPES/POSTGRESQL_PLUGIN:

Deprecated, but this was going to be a postgres plugin for the above file.

BACKEND:

The util directory houses the Java API for reading, writing, and querying
alignment information.  This information could exist in flat files or a
database but this API is designed to shield the end user from this.

The stores are currently flat files, SRF for storing raw sequences, SAM/BAM for
storing alignments, and BBED/BWIG for storing mismatch and coverage information
respectively.  There already exist APIs for SRF and SAM/BAM files and that work
won't be replicated here.  So this util directory is mainly focused on APIs for
reading, writing, and querying mismatch and coverage information.

These summary formats are essentially a binary version of BED/WIG-style files.
The first format type (BBED) represents mismatches to the reference genome.
The second file format type (BWIG) represents coverage across the reference
genome.  These files are used by the query engine to generate BED/WIG files on
the fly given certain filters. This folder contains Perl and Java APIs for
reading, writing, and querying these files.

The current way of generating this information is to go from SAM -> pileup and
then using a converter from pileup to BBED/BWIG files.  In the future the
BBED/BWIG formats could actually be postgres or mysql databases instead.

You can test this API by running:

>ant test-cmdline

And then look at the file:

data/tests/net/sourceforge/seqware/queryengine/tools/TextDumper/TextDump.new.txt


WEBSERVER:

A RESTful service for retrieving experiment data and querying variant calls.
Written in Java using the RESTLET toolkit.

WEBCLIENT:

This is currently unimplemented.

The client API code in many languages. This will interact with the backend via
the webserver component. It will let users easily query the backend to find and
query their variant data.
