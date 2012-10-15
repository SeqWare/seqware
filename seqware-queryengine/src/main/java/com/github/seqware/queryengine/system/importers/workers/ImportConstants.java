/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.seqware.queryengine.system.importers.workers;

/**
 * Noticed that a few of the file types repeat a lot of information that we
 * might want to store as fields (and are fields in the old prototype).
 * Recording them here for now
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class ImportConstants {
    // the following are all keys for Tags for VCF
    /** Constant <code>VCF_SNV="SNV"</code> */
    public static final String VCF_SNV = "SNV";
    /** Constant <code>VCF_SECOND_ID="id"</code> */
    public static final String VCF_SECOND_ID = "id";
    /** Constant <code>VCF_CALLED_BASE="call_base"</code> */
    public static final String VCF_CALLED_BASE = "call_base";
    /** Constant <code>VCF_CONSENSUS_BASE="con_base"</code> */
    public static final String VCF_CONSENSUS_BASE = "con_base";
    /** Constant <code>VCF_REFERENCE_BASE="ref_base"</code> */
    public static final String VCF_REFERENCE_BASE = "ref_base";
    /** Constant <code>VCF_HETEROZYGOUS="heterozygous"</code> */
    public static final String VCF_HETEROZYGOUS = "heterozygous";
    /** Constant <code>VCF_HOMOZYGOUS="homozygous"</code> */
    public static final String VCF_HOMOZYGOUS = "homozygous";
    /** Constant <code>VCF_READ_COUNTS="read_count"</code> */
    public static final String VCF_READ_COUNTS = "read_count";
    
    /** Constant <code>VCF_FILTER="filter"</code> */
    public static final String VCF_FILTER = "filter";
    /** Constant <code>VCF_INFO="info"</code> */
    public static final String VCF_INFO = "info";
        
    // the following are all keys for Tags in GFF3
    /** Constant <code>GFF3_SNV="VCF_SNV"</code> */
    public static final String GFF3_SNV = VCF_SNV;
    /** Constant <code>GFF3_INSERTION="insertion"</code> */
    public static final String GFF3_INSERTION = "insertion";
    /** Constant <code>GFF3_DELETION="deletion"</code> */
    public static final String GFF3_DELETION = "deletion";
    /** Constant <code>GFF3_UNKNOWN_TYPE="unknown_type"</code> */
    public static final String GFF3_UNKNOWN_TYPE = "unknown_type";
    // the following are all keys for Tags in GVF
    /** Constant <code>GVF_DBXREF="Dbxref"</code> */
    public static final String GVF_DBXREF = "Dbxref";
    /** Constant <code>GVF_ALIAS="Alias"</code> */
    public static final String GVF_ALIAS = "Alias";
    /** Constant <code>GVF_VARIANT_SEQ="Variant_seq"</code> */
    public static final String GVF_VARIANT_SEQ = "Variant_seq";
    /** Constant <code>GVF_REFERENCE_SEQ="Reference_seq"</code> */
    public static final String GVF_REFERENCE_SEQ = "Reference_seq";
    /** Constant <code>GVF_VARIANT_READS="Variant_reads"</code> */
    public static final String GVF_VARIANT_READS = "Variant_reads";
    /** Constant <code>GVF_TOTAL_READS="Total_reads"</code> */
    public static final String GVF_TOTAL_READS = "Total_reads";
    /** Constant <code>GVF_ZYGOSITY="Zygosity"</code> */
    public static final String GVF_ZYGOSITY = "Zygosity";
    /** Constant <code>GVF_VARIANT_FREQ="Variant_freq"</code> */
    public static final String GVF_VARIANT_FREQ = "Variant_freq";
    /** Constant <code>GVF_VARIANT_EFFECT="Variant_effect"</code> */
    public static final String GVF_VARIANT_EFFECT = "Variant_effect";
    /** Constant <code>GVF_START_RANGE="Start_range"</code> */
    public static final String GVF_START_RANGE = "Start_range";
    /** Constant <code>GVF_END_RANGE="End_range"</code> */
    public static final String GVF_END_RANGE = "End_range";
    /** Constant <code>GVF_PHASED="Phased"</code> */
    public static final String GVF_PHASED = "Phased";
    /** Constant <code>GVF_GENOTYPE="Genotype"</code> */
    public static final String GVF_GENOTYPE = "Genotype";
    /** Constant <code>GVF_INDIVIDUAL="Individual"</code> */
    public static final String GVF_INDIVIDUAL = "Individual";
    /** Constant <code>GVF_VARIANT_CODON="Variant_codon"</code> */
    public static final String GVF_VARIANT_CODON = "Variant_codon";
    /** Constant <code>GVF_REFERENCE_CODON="Reference_codon"</code> */
    public static final String GVF_REFERENCE_CODON = "Reference_codon";
    /** Constant <code>GVF_VARIANT_AA="Variant_aa"</code> */
    public static final String GVF_VARIANT_AA = "Variant_aa";
    /** Constant <code>GVF_REFERENCE_AA="Reference_aa"</code> */
    public static final String GVF_REFERENCE_AA = "Reference_aa";
    /** Constant <code>GVF_BREAKPOINT_DETAIL="Breakpoint_detail"</code> */
    public static final String GVF_BREAKPOINT_DETAIL = "Breakpoint_detail";
    /** Constant <code>GVF_SEQUENCE_CONTEXT="Sequence_context"</code> */
    public static final String GVF_SEQUENCE_CONTEXT = "Sequence_context";
    
    /** Constant <code>GVF_HETEROZYGOUS="heterozygous"</code> */
    public static final String GVF_HETEROZYGOUS = "heterozygous";
    /** Constant <code>GVF_HOMOZYGOUS="homozygous"</code> */
    public static final String GVF_HOMOZYGOUS = "homozygous";
    /** Constant <code>GVF_HEMIZYGOUS="hemizygous"</code> */
    public static final String GVF_HEMIZYGOUS = "hemizygous";
    
    /**
     * Unprocessed attributes, many of these could be parsed further, but this would take work to account for 
     * multi-individual files and the like
     */
    static final String[] UNPROCESSED_ATTRIBUTES = new String[]{GVF_ALIAS, GVF_DBXREF, GVF_VARIANT_SEQ,
        GVF_REFERENCE_SEQ, GVF_VARIANT_READS, GVF_TOTAL_READS, GVF_VARIANT_FREQ, GVF_VARIANT_EFFECT, 
        GVF_START_RANGE, GVF_END_RANGE, GVF_PHASED, GVF_GENOTYPE, GVF_INDIVIDUAL, GVF_VARIANT_CODON, 
        GVF_REFERENCE_CODON, GVF_VARIANT_AA, GVF_REFERENCE_AA, GVF_REFERENCE_AA, GVF_BREAKPOINT_DETAIL, 
        GVF_SEQUENCE_CONTEXT
    };
}
