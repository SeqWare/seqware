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
package net.sourceforge.seqware.common.hibernate;

import java.io.IOException;
import java.io.Writer;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;

/**
 * Static methods that provide a bridge between BasicDecider and the old SymlinkFileReporter.
 * 
 * The history behind this is that the BasicDecider used to rely upon the code shared with SymlinkFileReporter. Deciders that are based on
 * BasicDecider thus code directly against these headers. This class also used to handle all of the actual traversal of the database
 * hierarchy to find files, hence the name.
 * 
 * @author mtaschuk
 * @version $Id: $Id
 */
public class FindAllTheFiles {

    public static void addAttributeToReturnValue(ReturnValue ret, String key, String value) {
        if (ret.getAttribute(key) != null) {
            ret.setAttribute(key, ret.getAttribute(key) + ";" + value);
        } else {
            ret.setAttribute(key, value);
        }
    }

    public enum Header {
        STUDY_TITLE("Study Title"), STUDY_SWA("Study SWID"), STUDY_TAG_PREFIX("study."), STUDY_ATTRIBUTES("Study Attributes"), EXPERIMENT_NAME(
                "Experiment Name"), EXPERIMENT_SWA("Experiment SWID"), EXPERIMENT_TAG_PREFIX("experiment."), EXPERIMENT_ATTRIBUTES(
                "Experiment Attributes"), PARENT_SAMPLE_NAME("Parent Sample Name"), PARENT_SAMPLE_SWA("Parent Sample SWID"), PARENT_SAMPLE_TAG_PREFIX(
                "parent_sample."), PARENT_SAMPLE_ATTRIBUTES("Parent Sample Attributes"), SAMPLE_NAME("Sample Name"), SAMPLE_SWA(
                "Sample SWID"), SAMPLE_TAG_PREFIX("sample."), SAMPLE_ATTRIBUTES("Sample Attributes"), IUS_SWA("IUS SWID"), IUS_TAG(
                "IUS Tag"), IUS_TAG_PREFIX("ius."), IUS_ATTRIBUTES("IUS Attributes"), LANE_NAME("Lane Name"), LANE_SWA("Lane SWID"), LANE_NUM(
                "Lane Number"), LANE_TAG_PREFIX("lane."), LANE_ATTRIBUTES("Lane Attributes"), SEQUENCER_RUN_NAME("Sequencer Run Name"), SEQUENCER_RUN_SWA(
                "Sequencer Run SWID"), SEQUENCER_RUN_TAG_PREFIX("sequencerrun."), SEQUENCER_RUN_ATTRIBUTES("Sequencer Run Attributes"), WORKFLOW_RUN_NAME(
                "Workflow Run Name"), WORKFLOW_RUN_SWA("Workflow Run SWID"), WORKFLOW_RUN_STATUS("Workflow Run Status"), WORKFLOW_NAME(
                "Workflow Name"), WORKFLOW_SWA("Workflow SWID"), WORKFLOW_VERSION("Workflow Version"), FILE_SWA("File SWID"), FILE_DESCRIPTION(
                "File Description"), FILE_MD5SUM("File Md5sum"), FILE_SIZE("File Size"), FILE_PATH("File Path"), FILE_META_TYPE(
                "File Meta-Type"), FILE_TAG_PREFIX("file."), FILE_ATTRIBUTES("File Attributes"), PROCESSING_DATE("Last Modified"), PROCESSING_SWID(
                "Processing SWID"), PROCESSING_ALGO("Processing Algorithm"), PROCESSING_TAG_PREFIX("processing."), PROCESSING_ATTRIBUTES(
                "Processing Attributes"), INPUT_FILE_META_TYPES("Input File Meta-Types"), INPUT_FILE_SWIDS("Input File SWIDs"), INPUT_FILE_PATHS(
                "Input File Paths"), SKIP("Skip");
        private final String title;

        Header(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    /** Constant <code>STUDY_TITLE="Header.STUDY_TITLE.getTitle()"</code> */
    public static final String STUDY_TITLE = Header.STUDY_TITLE.getTitle();
    /** Constant <code>STUDY_SWA="Header.STUDY_SWA.getTitle()"</code> */
    public static final String STUDY_SWA = Header.STUDY_SWA.getTitle();
    /** Constant <code>STUDY_TAG_PREFIX="Header.STUDY_TAG_PREFIX.getTitle()"</code> */
    public static final String STUDY_TAG_PREFIX = Header.STUDY_TAG_PREFIX.getTitle();
    /** Constant <code>STUDY_ATTRIBUTES="Header.STUDY_ATTRIBUTES.getTitle()"</code> */
    public static final String STUDY_ATTRIBUTES = Header.STUDY_ATTRIBUTES.getTitle();
    /** Constant <code>EXPERIMENT_NAME="Header.EXPERIMENT_NAME.getTitle()"</code> */
    public static final String EXPERIMENT_NAME = Header.EXPERIMENT_NAME.getTitle();
    /** Constant <code>EXPERIMENT_SWA="Header.EXPERIMENT_SWA.getTitle()"</code> */
    public static final String EXPERIMENT_SWA = Header.EXPERIMENT_SWA.getTitle();
    /** Constant <code>EXPERIMENT_TAG_PREFIX="Header.EXPERIMENT_TAG_PREFIX.getTitle()"</code> */
    public static final String EXPERIMENT_TAG_PREFIX = Header.EXPERIMENT_TAG_PREFIX.getTitle();
    /** Constant <code>EXPERIMENT_ATTRIBUTES="Header.EXPERIMENT_ATTRIBUTES.getTitle()"</code> */
    public static final String EXPERIMENT_ATTRIBUTES = Header.EXPERIMENT_ATTRIBUTES.getTitle();
    /** Constant <code>PARENT_SAMPLE_NAME="Header.PARENT_SAMPLE_NAME.getTitle()"</code> */
    public static final String PARENT_SAMPLE_NAME = Header.PARENT_SAMPLE_NAME.getTitle();
    /** Constant <code>PARENT_SAMPLE_SWA="Header.PARENT_SAMPLE_SWA.getTitle()"</code> */
    public static final String PARENT_SAMPLE_SWA = Header.PARENT_SAMPLE_SWA.getTitle();
    /** Constant <code>PARENT_SAMPLE_TAG_PREFIX="Header.PARENT_SAMPLE_TAG_PREFIX.getTitl"{trunked}</code> */
    public static final String PARENT_SAMPLE_TAG_PREFIX = Header.PARENT_SAMPLE_TAG_PREFIX.getTitle();
    /** Constant <code>PARENT_SAMPLE_ATTRIBUTES="Header.PARENT_SAMPLE_ATTRIBUTES.getTitl"{trunked}</code> */
    public static final String PARENT_SAMPLE_ATTRIBUTES = Header.PARENT_SAMPLE_ATTRIBUTES.getTitle();
    /** Constant <code>SAMPLE_NAME="Header.SAMPLE_NAME.getTitle()"</code> */
    public static final String SAMPLE_NAME = Header.SAMPLE_NAME.getTitle();
    /** Constant <code>SAMPLE_SWA="Header.SAMPLE_SWA.getTitle()"</code> */
    public static final String SAMPLE_SWA = Header.SAMPLE_SWA.getTitle();
    /** Constant <code>SAMPLE_TAG_PREFIX="Header.SAMPLE_TAG_PREFIX.getTitle()"</code> */
    public static final String SAMPLE_TAG_PREFIX = Header.SAMPLE_TAG_PREFIX.getTitle();
    /** Constant <code>SAMPLE_ATTRIBUTES="Header.SAMPLE_ATTRIBUTES.getTitle()"</code> */
    public static final String SAMPLE_ATTRIBUTES = Header.SAMPLE_ATTRIBUTES.getTitle();
    /** Constant <code>IUS_SWA="Header.IUS_SWA.getTitle()"</code> */
    public static final String IUS_SWA = Header.IUS_SWA.getTitle();
    /** Constant <code>IUS_TAG="Header.IUS_TAG.getTitle()"</code> */
    public static final String IUS_TAG = Header.IUS_TAG.getTitle();
    /** Constant <code>IUS_TAG_PREFIX="Header.IUS_TAG_PREFIX.getTitle()"</code> */
    public static final String IUS_TAG_PREFIX = Header.IUS_TAG_PREFIX.getTitle();
    /** Constant <code>IUS_ATTRIBUTES="Header.IUS_ATTRIBUTES.getTitle()"</code> */
    public static final String IUS_ATTRIBUTES = Header.IUS_ATTRIBUTES.getTitle();
    /** Constant <code>LANE_NAME="Header.LANE_NAME.getTitle()"</code> */
    public static final String LANE_NAME = Header.LANE_NAME.getTitle();
    /** Constant <code>LANE_SWA="Header.LANE_SWA.getTitle()"</code> */
    public static final String LANE_SWA = Header.LANE_SWA.getTitle();
    /** Constant <code>LANE_NUM="Header.LANE_NUM.getTitle()"</code> */
    public static final String LANE_NUM = Header.LANE_NUM.getTitle();
    /** Constant <code>LANE_TAG_PREFIX="Header.LANE_TAG_PREFIX.getTitle()"</code> */
    public static final String LANE_TAG_PREFIX = Header.LANE_TAG_PREFIX.getTitle();
    /** Constant <code>LANE_ATTRIBUTES="Header.LANE_ATTRIBUTES.getTitle()"</code> */
    public static final String LANE_ATTRIBUTES = Header.LANE_ATTRIBUTES.getTitle();
    /** Constant <code>SEQUENCER_RUN_NAME="Header.SEQUENCER_RUN_NAME.getTitle()"</code> */
    public static final String SEQUENCER_RUN_NAME = Header.SEQUENCER_RUN_NAME.getTitle();
    /** Constant <code>SEQUENCER_RUN_SWA="Header.SEQUENCER_RUN_SWA.getTitle()"</code> */
    public static final String SEQUENCER_RUN_SWA = Header.SEQUENCER_RUN_SWA.getTitle();
    /** Constant <code>SEQUENCER_RUN_TAG_PREFIX="Header.SEQUENCER_RUN_TAG_PREFIX.getTitl"{trunked}</code> */
    public static final String SEQUENCER_RUN_TAG_PREFIX = Header.SEQUENCER_RUN_TAG_PREFIX.getTitle();
    /** Constant <code>SEQUENCER_RUN_ATTRIBUTES="Header.SEQUENCER_RUN_ATTRIBUTES.getTitl"{trunked}</code> */
    public static final String SEQUENCER_RUN_ATTRIBUTES = Header.SEQUENCER_RUN_ATTRIBUTES.getTitle();
    /** Constant <code>WORKFLOW_RUN_NAME="Header.WORKFLOW_RUN_NAME.getTitle()"</code> */
    public static final String WORKFLOW_RUN_NAME = Header.WORKFLOW_RUN_NAME.getTitle();
    /** Constant <code>WORKFLOW_RUN_SWA="Header.WORKFLOW_RUN_SWA.getTitle()"</code> */
    public static final String WORKFLOW_RUN_SWA = Header.WORKFLOW_RUN_SWA.getTitle();
    /** Constant <code>WORKFLOW_RUN_STATUS="Header.WORKFLOW_RUN_STATUS.getTitle()"</code> */
    public static final String WORKFLOW_RUN_STATUS = Header.WORKFLOW_RUN_STATUS.getTitle();
    /** Constant <code>WORKFLOW_NAME="Header.WORKFLOW_NAME.getTitle()"</code> */
    public static final String WORKFLOW_NAME = Header.WORKFLOW_NAME.getTitle();
    /** Constant <code>WORKFLOW_SWA="Header.WORKFLOW_SWA.getTitle()"</code> */
    public static final String WORKFLOW_SWA = Header.WORKFLOW_SWA.getTitle();
    /** Constant <code>WORKFLOW_VERSION="Header.WORKFLOW_VERSION.getTitle()"</code> */
    public static final String WORKFLOW_VERSION = Header.WORKFLOW_VERSION.getTitle();
    /** Constant <code>FILE_SWA="Header.FILE_SWA.getTitle()"</code> */
    public static final String FILE_SWA = Header.FILE_SWA.getTitle();
    public static final String FILE_TAG_PREFIX = Header.FILE_TAG_PREFIX.getTitle();
    public static final String FILE_ATTRIBUTES = Header.FILE_ATTRIBUTES.getTitle();
    public static final String FILE_DESCRIPTION = Header.FILE_DESCRIPTION.getTitle();
    public static final String FILE_SIZE = Header.FILE_SIZE.getTitle();
    public static final String FILE_MD5SUM = Header.FILE_MD5SUM.getTitle();
    public static final String FILE_PATH = Header.FILE_PATH.getTitle();

    /** Constant <code>PROCESSING_DATE="Header.PROCESSING_DATE.getTitle()"</code> */
    public static final String PROCESSING_DATE = Header.PROCESSING_DATE.getTitle();
    /** Constant <code>PROCESSING_SWID="Header.PROCESSING_SWID.getTitle()"</code> */
    public static final String PROCESSING_SWID = Header.PROCESSING_SWID.getTitle();
    /** Constant <code>PROCESSING_ALGO="Header.PROCESSING_ALGO.getTitle()"</code> */
    public static final String PROCESSING_ALGO = Header.PROCESSING_ALGO.getTitle();
    /** Constant <code>PROCESSING_TAG_PREFIX="Header.PROCESSING_TAG_PREFIX.getTitle()"</code> */
    public static final String PROCESSING_TAG_PREFIX = Header.PROCESSING_TAG_PREFIX.getTitle();
    /** Constant <code>PROCESSING_ATTRIBUTES="Header.PROCESSING_ATTRIBUTES.getTitle()"</code> */
    public static final String PROCESSING_ATTRIBUTES = Header.PROCESSING_ATTRIBUTES.getTitle();

    public static final String INPUT_FILE_META_TYPES = Header.INPUT_FILE_META_TYPES.getTitle();
    public static final String INPUT_FILE_FILE_PATHS = Header.INPUT_FILE_PATHS.getTitle();
    public static final String INPUT_FILE_SWIDS = Header.INPUT_FILE_SWIDS.getTitle();

    public static void print(Writer writer, ReturnValue ret, String studyName, boolean showStatus, FileMetadata fm) throws IOException {
        print(writer, ret, studyName, showStatus, fm, false);
    }

    /**
     * Prints a line to the Excel spreadsheet.
     * 
     * @param reportInputFiles
     * @throws java.io.IOException
     *             if any.
     * @param writer
     *            a {@link java.io.Writer} object.
     * @param ret
     *            a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     * @param studyName
     *            a {@link java.lang.String} object.
     * @param showStatus
     *            a boolean.
     * @param fm
     *            a {@link net.sourceforge.seqware.common.module.FileMetadata} object.
     */
    public static void print(Writer writer, ReturnValue ret, String studyName, boolean showStatus, FileMetadata fm, boolean reportInputFiles)
            throws IOException {
        StringBuilder parentSampleTag = new StringBuilder();
        StringBuilder sampleTag = new StringBuilder();
        StringBuilder laneTag = new StringBuilder();
        StringBuilder studyTag = new StringBuilder();
        StringBuilder experimentTag = new StringBuilder();
        StringBuilder iusTag = new StringBuilder();
        StringBuilder seqencerrunTag = new StringBuilder();
        StringBuilder processingTag = new StringBuilder();
        StringBuilder fileTag = new StringBuilder();
        for (String key : ret.getAttributes().keySet()) {
            if (key.startsWith(FindAllTheFiles.PARENT_SAMPLE_TAG_PREFIX)) {
                parentSampleTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
            } else if (key.startsWith(FindAllTheFiles.SAMPLE_TAG_PREFIX)) {
                sampleTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
            } else if (key.startsWith(FindAllTheFiles.LANE_TAG_PREFIX)) {
                laneTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
            } else if (key.startsWith(FindAllTheFiles.STUDY_TAG_PREFIX)) {
                studyTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
            } else if (key.startsWith(FindAllTheFiles.EXPERIMENT_TAG_PREFIX)) {
                experimentTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
            } else if (key.startsWith(FindAllTheFiles.IUS_TAG_PREFIX)) {
                iusTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
            } else if (key.startsWith(FindAllTheFiles.SEQUENCER_RUN_TAG_PREFIX)) {
                seqencerrunTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
            } else if (key.startsWith(FindAllTheFiles.PROCESSING_TAG_PREFIX)) {
                processingTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
            } else if (key.startsWith(FindAllTheFiles.FILE_TAG_PREFIX)) {
                fileTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(ret.getAttribute(PROCESSING_DATE)).append("\t");
        sb.append(studyName).append("\t");
        sb.append(ret.getAttribute(STUDY_SWA)).append("\t");
        sb.append(studyTag.toString()).append("\t");
        sb.append(ret.getAttribute(EXPERIMENT_NAME)).append("\t");
        sb.append(ret.getAttribute(EXPERIMENT_SWA)).append("\t");
        sb.append(experimentTag.toString()).append("\t");
        sb.append(ret.getAttribute(PARENT_SAMPLE_NAME)).append("\t");
        sb.append(ret.getAttribute(PARENT_SAMPLE_SWA)).append("\t");
        sb.append(parentSampleTag.toString()).append("\t");
        sb.append(ret.getAttribute(SAMPLE_NAME)).append("\t");
        sb.append(ret.getAttribute(SAMPLE_SWA)).append("\t");
        sb.append(sampleTag.toString()).append("\t");
        sb.append(ret.getAttribute(SEQUENCER_RUN_NAME)).append("\t");
        sb.append(ret.getAttribute(SEQUENCER_RUN_SWA)).append("\t");
        sb.append(seqencerrunTag.toString()).append("\t");
        sb.append(ret.getAttribute(LANE_NAME)).append("\t");
        sb.append(ret.getAttribute(LANE_NUM)).append("\t");
        sb.append(ret.getAttribute(LANE_SWA)).append("\t");
        sb.append(laneTag.toString()).append("\t");
        sb.append(ret.getAttribute(IUS_TAG)).append("\t");
        sb.append(ret.getAttribute(IUS_SWA)).append("\t");
        sb.append(iusTag.toString()).append("\t");
        sb.append(ret.getAttribute(WORKFLOW_NAME)).append("\t");
        sb.append(ret.getAttribute(WORKFLOW_VERSION)).append("\t");
        sb.append(ret.getAttribute(WORKFLOW_SWA)).append("\t");
        sb.append(ret.getAttribute(WORKFLOW_RUN_NAME)).append("\t");
        if (showStatus) {
            sb.append(ret.getAttribute(WORKFLOW_RUN_STATUS)).append("\t");
        }
        sb.append(ret.getAttribute(WORKFLOW_RUN_SWA)).append("\t");
        sb.append(ret.getAttribute(PROCESSING_ALGO)).append("\t");
        sb.append(ret.getAttribute(PROCESSING_SWID)).append("\t");
        sb.append(processingTag.toString()).append("\t");
        sb.append(fm.getMetaType()).append("\t");
        sb.append(ret.getAttribute(FILE_SWA)).append("\t");
        sb.append(fm.getFilePath()).append("\t");
        sb.append(fileTag.toString());
        if (reportInputFiles) {
            sb.append("\t");
            sb.append(ret.getAttribute(INPUT_FILE_META_TYPES)).append("\t");
            sb.append(ret.getAttribute(INPUT_FILE_SWIDS)).append("\t");
            sb.append(ret.getAttribute(INPUT_FILE_FILE_PATHS)).append("\t");
        }
        sb.append("\n");

        writer.write(sb.toString());

    }

    public static void printHeader(Writer writer, boolean showStatus) throws IOException {
        printHeader(writer, showStatus, false);
    }

    /**
     * Print the header of the Excel spreadsheet to file.
     * 
     * @param reportInputFiles
     * @throws java.io.IOException
     *             if any.
     * @param writer
     *            a {@link java.io.Writer} object.
     * @param showStatus
     *            a boolean.
     */
    public static void printHeader(Writer writer, boolean showStatus, boolean reportInputFiles) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(PROCESSING_DATE).append("\t");
        sb.append(STUDY_TITLE).append("\t");
        sb.append(STUDY_SWA).append("\t");
        sb.append(STUDY_ATTRIBUTES).append("\t");
        sb.append(EXPERIMENT_NAME).append("\t");
        sb.append(EXPERIMENT_SWA).append("\t");
        sb.append(EXPERIMENT_ATTRIBUTES).append("\t");
        sb.append(PARENT_SAMPLE_NAME).append("\t");
        sb.append(PARENT_SAMPLE_SWA).append("\t");
        sb.append(PARENT_SAMPLE_ATTRIBUTES).append("\t");
        sb.append(SAMPLE_NAME).append("\t");
        sb.append(SAMPLE_SWA).append("\t");
        sb.append(SAMPLE_ATTRIBUTES).append("\t");
        sb.append(SEQUENCER_RUN_NAME).append("\t");
        sb.append(SEQUENCER_RUN_SWA).append("\t");
        sb.append(SEQUENCER_RUN_ATTRIBUTES).append("\t");
        sb.append(LANE_NAME).append("\t");
        sb.append(LANE_NUM).append("\t");
        sb.append(LANE_SWA).append("\t");
        sb.append(LANE_ATTRIBUTES).append("\t");
        sb.append(IUS_TAG).append("\t");
        sb.append(IUS_SWA).append("\t");
        sb.append(IUS_ATTRIBUTES).append("\t");
        sb.append(WORKFLOW_NAME).append("\t");
        sb.append(WORKFLOW_VERSION).append("\t");
        sb.append(WORKFLOW_SWA).append("\t");
        sb.append(WORKFLOW_RUN_NAME).append("\t");
        if (showStatus) {
            sb.append(WORKFLOW_RUN_STATUS).append("\t");
        }
        sb.append(WORKFLOW_RUN_SWA).append("\t");
        sb.append(PROCESSING_ALGO).append("\t");
        sb.append(PROCESSING_SWID).append("\t");
        sb.append(PROCESSING_ATTRIBUTES).append("\t");
        sb.append("File Meta-Type").append("\t");
        sb.append(FILE_SWA).append("\t");
        sb.append("File Path").append("\t");
        sb.append(FILE_ATTRIBUTES);
        if (reportInputFiles) {
            sb.append("\t");
            sb.append(INPUT_FILE_META_TYPES).append("\t");
            sb.append(INPUT_FILE_SWIDS).append("\t");
            sb.append(INPUT_FILE_FILE_PATHS).append("\t");
        }
        sb.append("\n");

        writer.write(sb.toString());
    }
}
