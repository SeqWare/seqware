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
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.hibernate.reports.WorkflowRunReportRow;
import net.sourceforge.seqware.common.model.*;
import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Reports on workflow run metadata, returning either all workflow runs, all
 * runs of a particular workflow, or a specific workflow run. The results can be
 * filtered according to Date on the WorkflowRun's createTimestamp.
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowRunReport {

    private Logger logger = Logger.getLogger(WorkflowRunReport.class);
    private Date earliestDate = new Date(0);
    private Date latestDate = new Date();

    /**
     * Set the value of latestDate. This date must be set prior to calling other
     * methods in order to filter collections of workflow runs.
     *
     * @param latestDate new value of latestDate
     */
    public void setLatestDate(Date latestDate) {
        this.latestDate = latestDate;
    }

    /**
     * Set the value of earliestDate. This date must be set prior to calling
     * other methods in order to filter collections of workflow runs.
     *
     * @param earliestDate new value of earliestDate
     */
    public void setEarliestDate(Date earliestDate) {
        this.earliestDate = earliestDate;
    }

    /**
     * Find all of the workflow runs, and report on each of the workflow runs
     * the files, samples and processing events associated with each. If the
     * latest date and earliest date have been set in this class, the workflow
     * runs will be filtered according to the createTimestamp.
     *
     * @return a collection of workflow run reports
     */
    public Collection<WorkflowRunReportRow> getAllRuns() {
        WorkflowRunService ws = BeanFactory.getWorkflowRunServiceBean();
        List<WorkflowRun> workflowRuns = (List<WorkflowRun>)testIfNull(ws.list());
        Collection<WorkflowRunReportRow> rows = runThroughWorkflowRuns(workflowRuns);
        return rows;
    }

    /**
     * Using a workflow run SWID, report on the files, samples and processing
     * events associated with the run. Setting the earliest and latest date has
     * no effect on this method.
     *
     * @return a workflow run report
     * @param workflowRunSWID a {@link java.lang.Integer} object.
     */
    public WorkflowRunReportRow getSingleWorkflowRun(Integer workflowRunSWID) {
        WorkflowRunService ws = BeanFactory.getWorkflowRunServiceBean();
        WorkflowRun workflowRun = (WorkflowRun)testIfNull(ws.findBySWAccession(workflowRunSWID));
        logger.debug("Found workflow run: " + workflowRun.getSwAccession());
        return fromWorkflowRun(workflowRun);
    }

    /**
     * Using a workflow SWID, find all of the workflow runs, and report on each
     * of the workflow runs the files, samples and processing events associated
     * with each. If the latest date and earliest date have been set in this
     * class, the workflow runs will be filtered according to the
     * createTimestamp.
     *
     * @param workflowSWID the SWID of the workflow
     * @return a collection of workflow run reports
     */
    public Collection<WorkflowRunReportRow> getRunsFromWorkflow(Integer workflowSWID) {
        WorkflowService ws = BeanFactory.getWorkflowServiceBean();
        Workflow w = (Workflow)testIfNull(ws.findBySWAccession(workflowSWID));
        Collection<WorkflowRunReportRow> rows = runThroughWorkflowRuns(w.getWorkflowRuns());
        return rows;
    }

    private Collection<WorkflowRunReportRow> runThroughWorkflowRuns(Collection<WorkflowRun> runs) {
        List<WorkflowRunReportRow> rows = new ArrayList<WorkflowRunReportRow>();
        for (WorkflowRun wr : runs) {
            if (earliestDate != null && latestDate != null) {
                logger.debug("Checking dates: " + earliestDate.toString() + " and " + latestDate.toString());
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
        Collection<Processing> processings = collectProcessings(workflowRun);

        Collection<Processing> parentProcessings = findParents(processings, workflowRun.getSwAccession());

        Collection<File> inputFiles = findFiles(parentProcessings);
        Collection<File> outputFiles = findFiles(processings);


        Collection<Processing> useThese;
        if (!parentProcessings.isEmpty()) {
            useThese = parentProcessings;
        } else {
            useThese = processings;
        }
        Set<Sample> identitySamples = new HashSet<Sample>();

        for (Processing p : useThese) {
            identitySamples.addAll(findIdentitySamples(p));
        }
        Collection<Sample> librarySamples = findLibrarySamples(identitySamples);

        String timeSpent = calculateTotalTime(processings);

        WorkflowRunReportRow wrrr = new WorkflowRunReportRow();
        wrrr.setWorkflowRun(workflowRun);
        wrrr.setIdentitySamples(identitySamples);
        wrrr.setInputFiles(inputFiles);
        wrrr.setOutputFiles(outputFiles);
        wrrr.setLibrarySamples(librarySamples);
        wrrr.setParentProcessings(parentProcessings);
        wrrr.setWorkflowRunProcessings(parentProcessings);
        wrrr.setTimeTaken(timeSpent);


        return wrrr;
    }

    protected Collection<Processing> collectProcessings(WorkflowRun wr) {
        List<Processing> processings = new ArrayList<Processing>();

	WorkflowRun newwr =  BeanFactory.getWorkflowRunServiceBean().findByID(wr.getWorkflowRunId());
	
		
        logger.debug(newwr.getProcessings().size() + " Processings in direct links");
        logger.debug(newwr.getOffspringProcessings().size() + " Processings in ancestor links");
        processings.addAll(newwr.getProcessings());
        processings.addAll(newwr.getOffspringProcessings());
        logger.debug(processings.size() + " unique Processings in total");
        return processings;
    }

    /**
     * <p>calculateTotalTime.</p>
     *
     * @param processings a {@link java.util.Collection} object.
     * @return a {@link java.lang.String} object.
     */
    public String calculateTotalTime(Collection<Processing> processings) {
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
        int MS_PER_DAY = 86400000;
        int MS_PER_HOUR = 3600000;
        int MS_PER_MINUTE = 60000;

        int days = (int) milliseconds / MS_PER_DAY;
        int hourMilliseconds = (int) milliseconds - days * MS_PER_DAY;
        int hours = hourMilliseconds / MS_PER_HOUR;
        int minuteMilliseconds = hourMilliseconds - hours * MS_PER_HOUR;
        int minutes = minuteMilliseconds / MS_PER_MINUTE;
        int secondMilliseconds = minuteMilliseconds - minutes * MS_PER_MINUTE;
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
        List<File> files = new ArrayList<File>();
        for (Processing processing : processings) {
            files.addAll(processing.getFiles());
        }
        logger.debug("Unique files: " + files.size());
        return files;
    }

    protected Collection<Processing> findParents(Collection<Processing> processings, int workflowRunSWID) {
        Set<Integer> seenPs = new TreeSet<Integer>();

        List<Processing> allParentProcs = new ArrayList<Processing>();
        Queue<Processing> queue = new LinkedList<Processing>();
        queue.addAll(processings);

        while (!queue.isEmpty()) {
            Processing processing = queue.poll();
            for (Processing p : processing.getParents()) {
                //get the workflow run
                WorkflowRun wr = p.getWorkflowRun();
                if (wr == null) {
                    wr = p.getWorkflowRunByAncestorWorkflowRunId();
                }
                //add to the queue if we haven't seen it before
                if (!seenPs.contains(p.getSwAccession())) {
                    queue.offer(p);
                    seenPs.add(p.getSwAccession());
                    //only add to the parent procs if it's not from the current workflow run
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
//        Set<Lane> lanes = processing.getLanes();
        Set<Sample> samples = processing.getSamples();

//        logger.debug("iuses: " + iuses.size() + " lanes: " + lanes.size() + " samples: " + samples.size());
        logger.debug("iuses: " + iuses.size() + " samples: " + samples.size());

        List<Sample> allIdentitySamples = new ArrayList<Sample>();

        if (iuses != null) {
            for (IUS i : iuses) {
                allIdentitySamples.add(i.getSample());
            }
        }

//        if (lanes != null) {
//            for (Lane l : lanes) {
//                if (l.getIUS() != null) {
//                    for (IUS i : l.getIUS()) {
//                        allIdentitySamples.add(i.getSample());
//                    }
//                }
//            }
//        }

        if (samples != null) {
            allIdentitySamples.addAll(samples);
        }

        logger.debug("Number of identity samples: " + allIdentitySamples.size());
        return allIdentitySamples;
    }

    private Collection<Sample> findLibrarySamples(Collection<Sample> allIdentitySamples) {

        List<Sample> allLibrarySamples = new ArrayList<Sample>();
        Set<Integer> seenSams = new TreeSet<Integer>();

        Queue<Sample> queue = new LinkedList<Sample>();
        queue.addAll(allIdentitySamples);

        while (!queue.isEmpty()) {
            Sample sample = queue.poll();
            for (Sample p : sample.getParents()) {
                //add to the queue if we haven't seen it before
                if (!seenSams.contains(p.getSwAccession())) {
                    queue.offer(p);
                    seenSams.add(p.getSwAccession());
                    //only add to the library samples if it is a root node
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
     * <p>testIfNull.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link java.lang.Object} object.
     */
    protected Object testIfNull(Object o) {
        if (o == null) {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }
        return o;
    }
}
