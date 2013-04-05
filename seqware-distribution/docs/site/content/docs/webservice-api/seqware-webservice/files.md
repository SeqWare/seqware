---
title: Files | SeqWare API
---

# Files API

## Get a single file

    GET /files/:swa

### Response

<%= headers 200 %>
~~~
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<File>
    <description>A file output from the GenericMetadataSaver.</description>
    <fileId>17</fileId>
    <filePath>/oicr/data/archive/seqware/seqware_analysis/11779_ACAGTG_L002_R1_001.fastq.gz</filePath>
    <isSelected>false</isSelected>
    <metaType>chemical/seq-na-fastq-gzip</metaType>
    <swAccession>12055</swAccession>
    <type>BCLToFastq</type>
</File>
~~~


## Update a file

    PUT /files/:swa

### Input

~~~
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<File>
    <description>A file output from the GenericMetadataSaver.</description>
    <fileId>17</fileId>
    <filePath>/oicr/data/archive/seqware/seqware_analysis/Sample_11779/11779_ACAGTG_L002_R1_001.fastq.gz</filePath>
    <isSelected>false</isSelected>
    <swAccession>12055</swAccession>
    <metaType>chemical/seq-na-fastq-gzip</metaType>
    <type>BCLToFastq</type>
</File>
~~~

### Response
<%= headers 201 %>
~~~
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<File>
    <description>A file output from the GenericMetadataSaver.</description>
    <fileId>17</fileId>
    <filePath>/oicr/data/archive/seqware/seqware_analysis/results//11779_ACAGTG_L002_R1_001.fastq.gz</filePath>
    <isSelected>false</isSelected>
    <metaType>chemical/seq-na-fastq-gzip</metaType>
    <swAccession>12055</swAccession>
    <type>BCLToFastq</type>
</File>
~~~

## Get all files

    GET /files

### Response

<%= headers 200 %>
~~~
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<FileList>
    <list>
        <description>A file output from the GenericMetadataSaver.</description>
        <fileId>18</fileId>
        <filePath>/oicr/data/archive/seqware/seqware_analysis/results/11779_ACAGTG_L002_R2_001.fastq.gz</filePath>
        <isSelected>false</isSelected>
        <metaType>chemical/seq-na-fastq-gzip</metaType>
        <swAccession>12056</swAccession>
        <type>BCLToFastq</type>
    </list>
    <list>
        <description>A file output from the GenericMetadataSaver.</description>
        <fileId>17</fileId>
        <filePath>/oicr/data/archive/seqware/seqware_analysis/results/11779_ACAGTG_L002_R1_001.fastq.gz</filePath>
        <isSelected>false</isSelected>
        <metaType>chemical/seq-na-fastq-gzip</metaType>
        <swAccession>12055</swAccession>
        <type>BCLToFastq</type>
    </list>
</FileList>
~~~


