/*
 * Copyright (C) 2013 SeqWare
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
package net.sourceforge.seqware.common.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * A series of utility methods that produce human friendly output from tabbed output.
 * 
 * Not the most efficient, since we cannot stream Strings.
 * 
 * @author dyuen
 */
public class TabExpansionUtil {

    /**
     * Produce output resembling Postgres with one "table" per record
     * 
     * @param tabSeparated
     * @return
     */
    public static String expansion(String tabSeparated) {
        String[] lines = tabSeparated.split("\n");
        // get headers
        String[] header = lines[0].split("\t");
        // determine maximum header length and other formatting
        int maxHeader = 0;
        int maxContent = 0;
        for (String h : header) {
            maxHeader = Math.max(maxHeader, h.length());
        }
        List<String[]> records = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String[] record = lines[i].split("\t");
            for (String col : record) {
                maxContent = Math.max(col.length(), maxContent);
            }
            records.add(record);
        }
        maxContent++;
        // do output
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < records.size(); i++) {
            String head = "-[ RECORD " + i + " ]";
            buff.append(head);
            buff.append(StringUtils.repeat("-", maxHeader - head.length()));
            buff.append("+");
            buff.append(StringUtils.repeat("-", maxContent));
            buff.append("\n");
            int j = 0;
            for (String col : records.get(i)) {
                buff.append(header[j]);
                buff.append(StringUtils.repeat(" ", maxHeader - header[j].length()));
                buff.append("| ");
                buff.append(col);
                buff.append(StringUtils.repeat(" ", maxContent - col.length()));
                buff.append("\n");
                j++;
            }
        }
        return buff.toString();
    }

    /**
     * Produce aligned output that lines up properly in the terminal
     * 
     * @param tabSeparated
     * @return
     */
    public static String aligned(String tabSeparated) {
        String[] lines = tabSeparated.split("\n");
        // get headers
        String[] header = lines[0].split("\t");
        // determine maximum header length and other formatting
        int[] maxContent = new int[header.length];
        List<String[]> records = new ArrayList<>();
        for (String line : lines) {
            String[] record = line.split("\t");
            int j = 0;
            for (String col : record) {
                maxContent[j] = Math.max(col.length(), maxContent[j]);
                j++;
            }
            records.add(record);
        }
        // do output
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < records.size(); i++) {
            int j = 0;
            for (String col : records.get(i)) {
                buff.append(col);
                buff.append(StringUtils.repeat(" ", Math.max(0, (maxContent[j] - col.length()))));
                buff.append("|");
                j++;
            }
            if (i == 0) {
                buff.append("\n");
                for (int c : maxContent) {
                    buff.append(StringUtils.repeat("-", c));
                    buff.append("+");
                }
            }
            buff.append("\n");
        }
        return buff.toString();
    }

    public static void main(String[] args) {
        String input = "Workflow	Workflow Run SWID	Workflow Run Status	Workflow Run Create Timestamp	Workflow Run Host	Workflow Run Status Command	Library Sample Names	Library Sample SWIDs	Identity Sample Names	Identity Sample SWIDs	Input File Meta-Types	Input File SWIDs	Input File Paths	Output File Meta-Types	Output File SWIDs	Output File Paths	Workflow Run Time	\n"
                + "BamQC 1.0	408213	completed	2012-12-18 06:06:24.842	sqwprod.hpc.oicr.on.ca	pegasus-status -l /u/seqware/pegasus-dax/sqwprod/seqware/pegasus/BamQC/run1269	CPCG_0198	305613	CPCG_0198_Pr_P_PE_354_WG	218446	application/bam,chemical/seq-na-fastq-gzip,chemical/seq-na-fastq-gzip	319329,219987,219988	/oicr/data/archive/seqware/seqware_analysis_6/sqwprod/results/seqware-0.10.0_GenomicAlignmentNovoalign-0.10.1/40527172/SWID_218737_CPCG_0198_Pr_P_PE_354_WG_120530_SN1068_0090_AD0V1UACXX_NoIndex_L002_R1_001.fastq.gz.annotated.bam,/oicr/data/archive/seqware/seqware_analysis_3/sqwprod/results/seqware-0.10.0_IlluminaBaseCalling-1.8.2-1/86088819/Unaligned_120530_SN1068_0090_AD0V1UACXX_2/Project_na/Sample_SWID_218737_CPCG_0198_Pr_P_PE_354_WG_120530_SN1068_0090_AD0V1UACXX/SWID_218737_CPCG_0198_Pr_P_PE_354_WG_120530_SN1068_0090_AD0V1UACXX_NoIndex_L002_R1_001.fastq.gz,/oicr/data/archive/seqware/seqware_analysis_3/sqwprod/results/seqware-0.10.0_IlluminaBaseCalling-1.8.2-1/86088819/Unaligned_120530_SN1068_0090_AD0V1UACXX_2/Project_na/Sample_SWID_218737_CPCG_0198_Pr_P_PE_354_WG_120530_SN1068_0090_AD0V1UACXX/SWID_218737_CPCG_0198_Pr_P_PE_354_WG_120530_SN1068_0090_AD0V1UACXX_NoIndex_L002_R2_001.fastq.gz	text/json	409937	/oicr/data/archive/seqware/seqware_analysis_6/sqwprod/seqware-results/seqware-0.12.5_Workflow_Bundle_BamQC/1.0/93145099/SWID_218737_CPCG_0198_Pr_P_PE_354_WG_120530_SN1068_0090_AD0V1UACXX_NoIndex_L002_R1_001.fastq.gz.annotated.bam.BamQC.json	1h 50m\n"
                + "BamQC 1.0	408213	completed	2012-12-18 06:06:24.842	sqwprod.hpc.oicr.on.ca	pegasus-status -l /u/seqware/pegasus-dax/sqwprod/seqware/pegasus/BamQC/run1269	CPCG_0198	305613	CPCG_0198_Pr_P_PE_354_WG	218446	application/bam,chemical/seq-na-fastq-gzip,chemical/seq-na-fastq-gzip	319329,219987,219988	/oicr/data/archive/seqware/seqware_analysis_6/sqwprod/results/seqware-0.10.0_GenomicAlignmentNovoalign-0.10.1/40527172/SWID_218737_CPCG_0198_Pr_P_PE_354_WG_120530_SN1068_0090_AD0V1UACXX_NoIndex_L002_R1_001.fastq.gz.annotated.bam,/oicr/data/archive/seqware/seqware_analysis_3/sqwprod/results/seqware-0.10.0_IlluminaBaseCalling-1.8.2-1/86088819/Unaligned_120530_SN1068_0090_AD0V1UACXX_2/Project_na/Sample_SWID_218737_CPCG_0198_Pr_P_PE_354_WG_120530_SN1068_0090_AD0V1UACXX/SWID_218737_CPCG_0198_Pr_P_PE_354_WG_120530_SN1068_0090_AD0V1UACXX_NoIndex_L002_R1_001.fastq.gz,/oicr/data/archive/seqware/seqware_analysis_3/sqwprod/results/seqware-0.10.0_IlluminaBaseCalling-1.8.2-1/86088819/Unaligned_120530_SN1068_0090_AD0V1UACXX_2/Project_na/Sample_SWID_218737_CPCG_0198_Pr_P_PE_354_WG_120530_SN1068_0090_AD0V1UACXX/SWID_218737_CPCG_0198_Pr_P_PE_354_WG_120530_SN1068_0090_AD0V1UACXX_NoIndex_L002_R2_001.fastq.gz	text/json	409937	/oicr/data/archive/seqware/seqware_analysis_6/sqwprod/seqware-results/seqware-0.12.5_Workflow_Bundle_BamQC/1.0/93145099/SWID_218737_CPCG_0198_Pr_P_PE_354_WG_120530_SN1068_0090_AD0V1UACXX_NoIndex_L002_R1_001.fastq.gz.annotated.bam.BamQC.json	1h 50m ";

        System.out.println(TabExpansionUtil.expansion(input));
        System.out.println();
        System.out.println(TabExpansionUtil.aligned(input));

    }
}
