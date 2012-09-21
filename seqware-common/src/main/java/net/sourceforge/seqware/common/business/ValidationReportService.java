package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.business.impl.ValidationReportServiceImpl.ReportEntry;

/**
 * A collection of reports useful when verifying that data in the SeqWare meta
 * db is correct.
 * 
 */
public interface ValidationReportService {

  /**
   * Runs the {@link #fileLinkReport(List)} including all files in the SeqWare
   * meta db.
   * 
   * @see #fileLinkReport(List)
   * @return A list of entries. Each entry indicates the file tested and the
   *         number of links the file has.
   */
  public List<ReportEntry> fileLinkReport();

  /**
   * Every file must be associated with at least one processing node and this
   * node in turn must be linked (either directly or through a parent) to one of
   * the following: sequencer_run, lane, ius, sample, experiment or study.
   * 
   * This report includes all files included in the given list of fileSwas (File
   * SeqWare accesssions) and notes the number of links that exist for that
   * file. The ideal number is 1. An orphan is indicated by the number 0. A
   * number greater that one is not a definite error, but is worthy of
   * investigation.
   * 
   * @param fileSwas
   *          A list of file SeqWare accessions to be included in the report.
   * @return A list of entries. Each entry indicates the file tested and the
   *         number of links the file has.
   */
  public List<ReportEntry> fileLinkReport(List<Integer> fileSwas);

  /**
   * Produces a text representation of a reverse hierarchy tree starting at a
   * file (indicated by the provided fileSwa). The tree included all the
   * processing nodes that led to the creation of the file as will as all the
   * links (sequencer_run, lane, ius, sample, experiment, study) to those
   * processing nodes.
   * 
   * This report is useful for answering the question: What steps led to the
   * creation of this file?
   * 
   * @param fileSwa
   *          SeqWare accession indicating a file.
   * @return A plain text representation of a tree.
   */
  public String fileReverseHierarchyDisplay(Integer fileSwa);
}
