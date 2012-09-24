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

import java.util.*;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import org.apache.log4j.Logger;

/**
 *
 * @author mtaschuk
 */
public class SequencerRunReport {
    private Logger logger = Logger.getLogger(SequencerRunReport.class);
    public String makeReport(String identity) {

        StringBuilder s = new StringBuilder();
        Map<String, Row> rows = new HashMap<String, Row>();
        SortedMap<String, String> workflows = new TreeMap<String, String>();

        SequencerRunService srs = BeanFactory.getSequencerRunServiceBean();
        List<SequencerRun> runs = srs.list();

        FindAllTheFiles fatf;
        for (SequencerRun sr : runs) {
            for (Lane lane : sr.getLanes()) {
                fatf = new FindAllTheFiles();
                fatf.setRequireFiles(false);
                List<ReturnValue> values = fatf.filesFromLane(lane, null, null);
                makeRowsGetWorkflows(values, rows, workflows);

            }
        }

        addHeader(s, workflows);

        for (Row row : rows.values()) {
            printRow(s, row, workflows);
            logger.debug(row.toString());
        }
        s.append("Total Number of Rows:").append(rows.size()).append("\n");
        return s.toString();

    }

    private void makeRowsGetWorkflows(Collection<ReturnValue> set, Map<String, Row> rows,
            SortedMap<String, String> workflows) {
        for (ReturnValue val : set) {

            String workflowName = val.getAttribute(FindAllTheFiles.WORKFLOW_NAME);
            if (workflowName != null) {
                workflowName = workflowName.replace(" ", "_");
            }
            String workflowSwa = val.getAttribute(FindAllTheFiles.WORKFLOW_SWA);
            String workflowVersion = val.getAttribute(FindAllTheFiles.WORKFLOW_VERSION);

            makeRows(val, rows, workflowSwa);

            if (workflowSwa != null) {
                if (!workflows.containsKey(workflowSwa)) {
                    workflows.put(workflowSwa, workflowName + "_" + workflowVersion);
                }
            }
            logger.debug(val.toString());
        }
    }

    private void addHeader(StringBuilder s, SortedMap<String, String> workflows) {
        s.append("Last Modified").append("\t");
        s.append("Sequencer Run Name").append("\t");
        s.append("Sequencer Run SWID").append("\t");
        s.append("Lane Name").append("\t");
        s.append("Lane SWID").append("\t");
        s.append("IUS Tag").append("\t");
        s.append("IUS SWID").append("\t");
        s.append("Sample Name").append("\t");
        s.append("Sample SWID").append("\t");
        for (String workflowSwa : workflows.keySet()) {
            s.append(workflows.get(workflowSwa)).append(" (").append(workflowSwa).append(")").append("\t");
        }
        s.append("\n");
    }

    private void printRow(StringBuilder s, Row row, SortedMap<String, String> workflows) {
        s.append((row.getDate() == null ? "" : row.getDate().toString())).append("\t");
        s.append(row.getRunName()).append("\t");
        s.append(row.getRunSwa()).append("\t");
        s.append(row.getLaneName()).append("\t");
        s.append(row.getLaneSwa()).append("\t");
        s.append(row.getIusTag()).append("\t");
        s.append(row.getIusSwa()).append("\t");
        s.append(row.getSampleName()).append("\t");
        s.append(row.getSampleSwa()).append("\t");

        SortedMap<String, String> w2 = new TreeMap<String, String>();
        for (String workflowSwa : workflows.keySet()) {
            w2.put(workflowSwa, "");
        }
        for (String workflowSwa : row.getWorkflowsAndRuns().keySet()) {
            w2.put(workflowSwa, row.getWorkflowsAndRuns().get(workflowSwa));
        }
        for (String workflowSwa : w2.keySet()) {
            s.append(w2.get(workflowSwa)).append("\t");
        }

        s.append("\n");
    }

    private void makeRows(ReturnValue val, Map<String, Row> rows, String workflowSwa) {
        String iusSwa = val.getAttribute(FindAllTheFiles.IUS_SWA);
        String workflowRunSwa = val.getAttribute(FindAllTheFiles.WORKFLOW_RUN_SWA);
        String workflowRunStatus = val.getAttribute(FindAllTheFiles.WORKFLOW_RUN_STATUS);

        Date updateTimestamp = val.getRunStopTstmp();
        if (rows.containsKey(iusSwa)) {
            Row row = rows.get(iusSwa);
            if (workflowSwa != null) {
                row.addWorkflowRun(workflowSwa, workflowRunSwa, workflowRunStatus);
            }
            if (row.getDate() != null) {
                if (row.getDate().before(updateTimestamp)) {
                    row.setDate(updateTimestamp);
                }
            }

            rows.put(iusSwa, row);
        } else {
            String sampleName = val.getAttribute(FindAllTheFiles.SAMPLE_NAME);
            if (sampleName != null) {
                sampleName = sampleName.replace(" ", "_");
            }
            String sampleSwa = val.getAttribute(FindAllTheFiles.SAMPLE_SWA);
            String iusTag = val.getAttribute(FindAllTheFiles.IUS_TAG);

            String laneName = val.getAttribute(FindAllTheFiles.LANE_NAME);
            if (laneName != null) {
                laneName = laneName.replace(" ", "_");
            }
            String laneSwa = val.getAttribute(FindAllTheFiles.LANE_SWA);
            String laneNum = val.getAttribute(FindAllTheFiles.LANE_NUM);

            String sequencerRunName = val.getAttribute(FindAllTheFiles.SEQUENCER_RUN_NAME);
            if (sequencerRunName != null) {
                sequencerRunName = sequencerRunName.replace(" ", "_");
            }
            String sequencerRunSwa = val.getAttribute(FindAllTheFiles.SEQUENCER_RUN_SWA);

            Row row = new Row(updateTimestamp, sequencerRunName, sequencerRunSwa, laneName, laneSwa, iusTag, iusSwa,
                    sampleName, sampleSwa);
            if (workflowSwa != null) {
                row.addWorkflowRun(workflowSwa, workflowRunSwa, workflowRunStatus);
            }
            rows.put(iusSwa, row);
        }
    }

    public class Row implements Comparable<Row> {

        private Date date;
        private String runName;
        private String runSwa;
        private String laneName;
        private String laneSwa;
        private String iusTag;
        private String iusSwa;
        private String sampleName;
        private String sampleSwa;
        private SortedMap<String, String> workflowsAndRuns = new TreeMap<String, String>();

        public Row(Date date, String runName, String runSwa, String laneName, String laneSwa, String iusTag, String iusSwa,
                String sampleName, String sampleSwa) {
            this.date = date;
            this.runName = runName;
            this.runSwa = runSwa;
            this.laneName = laneName;
            this.laneSwa = laneSwa;
            this.iusTag = iusTag;
            this.iusSwa = iusSwa;
            this.sampleName = sampleName;
            this.sampleSwa = sampleSwa;
        }

        public void addWorkflowRun(String workflowSwa, String workflowRunSwa, String workflowRunStatus) {
            String info = workflowRunSwa + ":" + workflowRunStatus;
            if (workflowsAndRuns.containsKey(workflowSwa)) {
                String value = workflowsAndRuns.get(workflowSwa);
                if (!value.contains(info)) {
                    value += ";" + info;
                    workflowsAndRuns.put(workflowSwa, value);
                }
            } else {
                workflowsAndRuns.put(workflowSwa, info);
            }
        }

        @Override
        public int compareTo(Row t) {
            return this.iusSwa.compareTo(t.iusSwa);
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getIusSwa() {
            return iusSwa;
        }

        public String getIusTag() {
            return iusTag;
        }

        public String getLaneName() {
            return laneName;
        }

        public String getLaneSwa() {
            return laneSwa;
        }

        public String getRunName() {
            return runName;
        }

        public String getRunSwa() {
            return runSwa;
        }

        public String getSampleName() {
            return sampleName;
        }

        public String getSampleSwa() {
            return sampleSwa;
        }

        public SortedMap<String, String> getWorkflowsAndRuns() {
            return workflowsAndRuns;
        }

        @Override
        public String toString() {
            return "Row{" + "date=" + date + ", runName=" + runName + ", runSwa=" + runSwa + ", laneName=" + laneName
                    + ", laneSwa=" + laneSwa + ", iusTag=" + iusTag + ", iusSwa=" + iusSwa + ", sampleName=" + sampleName
                    + ", sampleSwa=" + sampleSwa + ", workflowsAndRuns=" + workflowsAndRuns + '}';
        }
    }
}
