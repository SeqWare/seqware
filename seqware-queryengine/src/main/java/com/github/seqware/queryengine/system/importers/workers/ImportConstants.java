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
 */
public class ImportConstants {
    // the following are all keys for Tags for VCF
    public static final String VCF_SNV = "SNV";
    public static final String VCF_SECOND_ID = "id";
    public static final String VCF_CALLED_BASE = "call_base";
    public static final String VCF_CONSENSUS_BASE = "con_base";
    public static final String VCF_REFERENCE_BASE = "ref_base";
    public static final String VCF_HETEROZYGOUS = "heterozygous";
    public static final String VCF_HOMOZYGOUS = "homozygous";
    public static final String VCF_READ_COUNTS = "read_count";
    // the following are all keys for Tags in GFF3
    public static final String GFF3_SNV = VCF_SNV;
    public static final String GFF3_INSERTION = "insertion";
    public static final String GFF3_DELETION = "deletion";
    public static final String GFF3_UNKNOWN_TYPE = "unknown_type";
    // the following are all keys for Tags in GVF
    public static final String GVF_DBXREF = "Dbxref";
    public static final String GVF_ALIAS = "Alias";
    public static final String GVF_VARIANT_SEQ = "Variant_seq";
    public static final String GVF_REFERENCE_SEQ = "Reference_seq";
    public static final String GVF_VARIANT_READS = "Variant_reads";
    public static final String GVF_TOTAL_READS = "Total_reads";
    public static final String GVF_ZYGOSITY = "Zygosity";
    public static final String GVF_VARIANT_FREQ = "Variant_freq";
    public static final String GVF_VARIANT_EFFECT = "Variant_effect";
    public static final String GVF_START_RANGE = "Start_range";
    public static final String GVF_END_RANGE = "End_range";
    public static final String GVF_PHASED = "Phased";
    public static final String GVF_GENOTYPE = "Genotype";
    public static final String GVF_INDIVIDUAL = "Individual";
    public static final String GVF_VARIANT_CODON = "Variant_codon";
    public static final String GVF_REFERENCE_CODON = "Reference_codon";
    public static final String GVF_VARIANT_AA = "Variant_aa";
    public static final String GVF_REFERENCE_AA = "Reference_aa";
    public static final String GVF_BREAKPOINT_DETAIL = "Breakpoint_detail";
    public static final String GVF_SEQUENCE_CONTEXT = "Sequence_context";
    
    public static final String GVF_HETEROZYGOUS = "heterozygous";
    public static final String GVF_HOMOZYGOUS = "homozygous";
    public static final String GVF_HEMIZYGOUS = "hemizygous";
    
    /**
     * Unprocessed attributes, many of these could be parsed further, but this would take work to account for 
     * multi-individual files and the like
     */
    public static final String[] UNPROCESSED_ATTRIBUTES = new String[]{GVF_ALIAS, GVF_DBXREF, GVF_VARIANT_SEQ,
        GVF_REFERENCE_SEQ, GVF_VARIANT_READS, GVF_TOTAL_READS, GVF_VARIANT_FREQ, GVF_VARIANT_EFFECT, 
        GVF_START_RANGE, GVF_END_RANGE, GVF_PHASED, GVF_GENOTYPE, GVF_INDIVIDUAL, GVF_VARIANT_CODON, 
        GVF_REFERENCE_CODON, GVF_VARIANT_AA, GVF_REFERENCE_AA, GVF_REFERENCE_AA, GVF_BREAKPOINT_DETAIL, 
        GVF_SEQUENCE_CONTEXT
    };
}
