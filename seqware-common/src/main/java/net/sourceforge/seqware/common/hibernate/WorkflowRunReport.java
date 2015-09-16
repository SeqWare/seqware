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

import io.seqware.common.model.WorkflowRunStatus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.hibernate.reports.WorkflowRunReportRow;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reports on workflow run metadata, returning either all workflow runs, all runs of a particular workflow, or a specific workflow run. The
 * results can be filtered according to Date on the WorkflowRun's createTimestamp.
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowRunReport {

    private final Logger logger = LoggerFactory.getLogger(WorkflowRunReport.class);
    // set to zero to default to earliest date
    private Date earliestDate = new Date(0);
    private Date latestDate = new Date();
    private WorkflowRunStatus status = null;

    /**
     * Set the value of latestDate. This date must be set prior to calling other methods in order to filter collections of workflow runs.
     *
     * @param latestDate
     *            new value of latestDate
     */
    public void setLatestDate(Date latestDate) {
        this.latestDate = latestDate;
    }

    /**
     * Set the value of earliestDate. This date must be set prior to calling other methods in order to filter collections of workflow runs.
     *
     * @param earliestDate
     *            new value of earliestDate
     */
    public void setEarliestDate(Date earliestDate) {
        this.earliestDate = earliestDate;
    }

    /**
     * Find all of the workflow runs, and report on each of the workflow runs the files, samples and processing events associated with each.
     * If the latest date and earliest date have been set in this class, the workflow runs will be filtered according to the
     * createTimestamp.
     *
     * @return a collection of workflow run reports
     */
    public Collection<WorkflowRunReportRow> getAllRuns() {
        WorkflowRunService ws = BeanFactory.getWorkflowRunServiceBean();
        List<WorkflowRun> workflowRuns = (List<WorkflowRun>) testIfNull(ws.list());
        Collection<WorkflowRunReportRow> rows = runThroughWorkflowRuns(workflowRuns);
        return rows;
    }

    /**
     * Using a workflow run SWID, report on the files, samples and processing events associated with the run. Setting the earliest and
     * latest date has no effect on this method.
     *
     * @return a workflow run report
     * @param workflowRunSWID
     *            a {@link java.lang.Integer} object.
     */
    public WorkflowRunReportRow getSingleWorkflowRun(Integer workflowRunSWID) {
        WorkflowRunService ws = BeanFactory.getWorkflowRunServiceBean();
        WorkflowRun workflowRun = (WorkflowRun) testIfNull(ws.findBySWAccession(workflowRunSWID));
        logger.debug("Found workflow run: " + workflowRun.getSwAccession());
        return fromWorkflowRun(workflowRun);
    }

    /**
     * Using a workflow SWID, find all of the workflow runs, and report on each of the workflow runs the files, samples and processing
     * events associated with each. If the latest date and earliest date have been set in this class, the workflow runs will be filtered
     * according to the createTimestamp.
     *
     * @param workflowSWID
     *            the SWID of the workflow
     * @return a collection of workflow run reports
     */
    public Collection<WorkflowRunReportRow> getRunsFromWorkflow(Integer workflowSWID) {
        WorkflowService ws = BeanFactory.getWorkflowServiceBean();
        Workflow w = (Workflow) testIfNull(ws.findBySWAccession(workflowSWID));
        Collection<WorkflowRunReportRow> rows = runThroughWorkflowRuns(w.getWorkflowRuns());
        return rows;
    }

    private Collection<WorkflowRunReportRow> runThroughWorkflowRuns(Collection<WorkflowRun> runs) {
        List<WorkflowRunReportRow> rows = new ArrayList<>();
        for (WorkflowRun wr : runs) {
            if (earliestDate != null && latestDate != null) {
                logger.debug("Checking dates: " + earliestDate.toString() + " and " + latestDate.toString());
                if (this.status != null && this.status != wr.getStatus()) {
                    logger.debug("Skip workflow run incorrect status: " + wr.getSwAccession());
                    continue;
                }
                if (wr.getCreateTimestamp().after(earliestDate) && wr.getCreateTimestamp().before(latestDate)) {
                    logger.debug("Add new workflow run within dates: " + wr.getSwAccession() + " " + wr.getCreateTimestamp());
                    rows.add(fromWorkflowRun(wr));
                }
            } else {
                logger.debug("Add new workflow run: " + wr.getSwAccession());
                rows.add(fromWorkflowRun(wr));
            }

        }
        return rows;
    }

    private WorkflowRunReportRow fromWorkflowRun(WorkflowRun workflowRun) {
        // Immediate parent processings are the processings that occur one level above the workflow run's processings.
        // Immediate input files are files that have been generated from immediate parent processings.
        Collection<Processing> processings = collectProcessings(workflowRun);
        Collection<Processing> allParentProcessings = findParents(processings, workflowRun.getSwAccession(), false);
        Collection<Processing> immediateParentProcessings = findParents(processings, workflowRun.getSwAccession(), true);

        Collection<File> allInputFiles = findFiles(allParentProcessings);
        Collection<File> immediateInputFiles = findFiles(immediateParentProcessings);
        Collection<File> outputFiles = findFiles(processings);

        Collection<Processing> useThese;
        if (!allParentProcessings.isEmpty()) {
            useThese = allParentProcessings;
        } else {
            useThese = processings;
        }
        Set<Sample> identitySamples = new TreeSet<>();

        for (Processing p : useThese) {
            identitySamples.addAll(findIdentitySamples(p));
        }
        Collection<Sample> librarySamples = findLibrarySamples(identitySamples);

        String timeSpent = calculateTotalTime(processings);

        WorkflowRunReportRow wrrr = new WorkflowRunReportRow();
        wrrr.setWorkflowRun(workflowRun);
        wrrr.setIdentitySamples(identitySamples);
        wrrr.setAllInputFiles(allInputFiles);
        wrrr.setImmediateInputFiles(immediateInputFiles);
        wrrr.setOutputFiles(outputFiles);
        wrrr.setLibrarySamples(librarySamples);
        wrrr.setParentProcessings(allParentProcessings);
        wrrr.setWorkflowRunProcessings(allParentProcessings);
        wrrr.setTimeTaken(timeSpent);

        return wrrr;
    }

    protected Collection<Processing> collectProcessings(WorkflowRun wr) {
        List<Processing> processings = new ArrayList<>();

        WorkflowRun newwr = BeanFactory.getWorkflowRunServiceBean().findByID(wr.getWorkflowRunId());

        logger.debug(newwr.getProcessings().size() + " Processings in direct links");
        logger.debug(newwr.getOffspringProcessings().size() + " Processings in ancestor links");
        processings.addAll(newwr.getProcessings());
        processings.addAll(newwr.getOffspringProcessings());
        logger.debug(processings.size() + " unique Processings in total");
        return processings;
    }

    /**
     * <p>
     * calculateTotalTime.
     * </p>
     *
     * @param processings
     *            a {@link java.util.Collection} object.
     * @return a {@link java.lang.String} object.
     */
    public String calculateTotalTime(Collection<Processing> processings) {
        if (processings.isEmpty()) return "";

        Date earlyDate = new Date(Long.MAX_VALUE), lateDate = new Date(0);

        for (Processing p : processings) {
            Date processingStart = p.getCreateTimestamp();
            if (processingStart.before(earlyDate)) {
                earlyDate = processingStart;
            }
            if (processingStart.after(lateDate)) {
                lateDate = processingStart;
            }
        }

        long milliseconds = lateDate.getTime() - earlyDate.getTime();
        logger.debug("Total time in ms: " + milliseconds);
        int msPerDay = 86400000;
        int msPerHour = 3600000;
        int msPerMinute = 60000;

        int days = (int) milliseconds / msPerDay;
        int hourMilliseconds = (int) milliseconds - days * msPerDay;
        int hours = hourMilliseconds / msPerHour;
        int minuteMilliseconds = hourMilliseconds - hours * msPerHour;
        int minutes = minuteMilliseconds / msPerMinute;
        int secondMilliseconds = minuteMilliseconds - minutes * msPerMinute;
        double seconds = secondMilliseconds / 1000;

        StringBuilder time = new StringBuilder();
        if (days > 0) {
            time.append(days).append("d ");
        }
        if (hours > 0) {
            time.append(hours).append("h ");
        }
        if (minutes > 0) {
            time.append(minutes).append("m ");
        }
        if (seconds > 0) {
            time.append(seconds).append("s ");
        }
        if (time.length() == 0) {
            time.append(milliseconds).append(" ms");
        }
        logger.debug("Total time:" + time.toString());
        return time.toString();
    }

    protected Collection<File> findFiles(Collection<Processing> processings) {
        List<File> files = new ArrayList<>();
        for (Processing processing : processings) {
            files.addAll(processing.getFiles());
        }
        logger.debug("Unique files: " + files.size());
        return files;
    }

    protected Collection<Processing> findParents(Collection<Processing> processings, int workflowRunSWID, boolean findImmediateOnly) {
        Set<Integer> seenPs = new TreeSet<>();

        List<Processing> allParentProcs = new ArrayList<>();
        Queue<Processing> queue = new LinkedList<>();
        queue.addAll(processings);

        while (!queue.isEmpty()) {
            Processing processing = queue.poll();
            for (Processing p : processing.getParents()) {
                // get the workflow run
                WorkflowRun wr = p.getWorkflowRun();
                if (wr == null) {
                    wr = p.getWorkflowRunByAncestorWorkflowRunId();
                }
                if (!seenPs.contains(p.getSwAccession())) {
                    // Add parent processing to queue only if we are traversing the entire tree.
                    if (!findImmediateOnly) queue.offer(p);
                    seenPs.add(p.getSwAccession());
                    // only add to the parent procs if it's not from the current workflow run
                    if (wr != null && wr.getSwAccession() != workflowRunSWID) {
                        logger.debug("Adding parent processing: " + p.getSwAccession());
                        allParentProcs.add(p);
                    } else if (wr == null) {
                        logger.debug("Adding processing without workflow run: " + p.getSwAccession());
                        allParentProcs.add(p);
                    }
                }
            }
        }
        logger.debug("Number of parent processings:" + allParentProcs.size());
        return allParentProcs;
    }

    private Collection<Sample> findIdentitySamples(Processing processing) {
        Set<IUS> iuses = processing.getIUS();
        // Set<Lane> lanes = processing.getLanes();
        Set<Sample> samples = processing.getSamples();

        // logger.debug("iuses: " + iuses.size() + " lanes: " + lanes.size() + " samples: " + samples.size());

        List<Sample> allIdentitySamples = new ArrayList<>();

        if (iuses != null) {
            logger.debug("iuses: " + iuses.size());
            for (IUS i : iuses) {
                allIdentitySamples.add(i.getSample());
            }
        }

        // if (lanes != null) {
        // for (Lane l : lanes) {
        // if (l.getIUS() != null) {
        // for (IUS i : l.getIUS()) {
        // allIdentitySamples.add(i.getSample());
        // }
        // }
        // }
        // }

        if (samples != null) {
            logger.debug("samples: " + samples.size());
            allIdentitySamples.addAll(samples);
        }

        logger.debug("Number of identity samples: " + allIdentitySamples.size());
        return allIdentitySamples;
    }

    private Collection<Sample> findLibrarySamples(Collection<Sample> allIdentitySamples) {

        List<Sample> allLibrarySamples = new ArrayList<>();
        Set<Integer> seenSams = new TreeSet<>();

        Queue<Sample> queue = new LinkedList<>();
        queue.addAll(allIdentitySamples);

        while (!queue.isEmpty()) {
            Sample sample = queue.poll();
            for (Sample p : sample.getParents()) {
                // add to the queue if we haven't seen it before
                if (!seenSams.contains(p.getSwAccession())) {
                    queue.offer(p);
                    seenSams.add(p.getSwAccession());
                    // only add to the library samples if it is a root node
                    if (p.getParents() == null || p.getParents().isEmpty()) {
                        logger.debug("Adding library sample: " + p.toString());
                        allLibrarySamples.add(p);
                    }
                }
            }
        }
        logger.debug("Number of library samples: " + allLibrarySamples.size());
        return allLibrarySamples;
    }

    /**
     * <p>
     * testIfNull.
     * </p>
     *
     * @param o
     *            a {@link java.lang.Object} object.
     * @return a {@link java.lang.Object} object.
     */
    protected Object testIfNull(Object o) {
        if (o == null) {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }
        return o;
    }

    public void setStatus(WorkflowRunStatus status) {
        this.status = status;
    }

    public Collection<WorkflowRunReportRow> getRunsByStatus(WorkflowRunStatus status) {
        WorkflowRunService ws = BeanFactory.getWorkflowRunServiceBean();
        List<WorkflowRun> runsWithValidStatus = ws.findByCriteria("wr.status = '" + status.toString() + "'");
        Collection<WorkflowRunReportRow> rows = runThroughWorkflowRuns(runsWithValidStatus);
        return rows;
    }
}
