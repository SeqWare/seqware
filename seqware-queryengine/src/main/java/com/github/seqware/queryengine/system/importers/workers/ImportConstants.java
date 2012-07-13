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
    public static final String SECOND_ID = "id";
    public static final String CALLED_BASE = "call_base";
    public static final String CONSENSUS_BASE = "con_base";
    public static final String REFERENCE_BASE = "ref_base";
    public static final String HETEROZYGOUS = "heterozygous";
    public static final String HOMOZYGOUS = "homozygous";
    public static final String READ_COUNTS = "read_count";
    // the following are all keys for Tags in GFF3
    public static final String GFF3_SNV = VCF_SNV;
    public static final String INSERTION = "insertion";
    public static final String DELETION = "deletion";
    public static final String UNKNOWN_TYPE = "unknown_type";
}
