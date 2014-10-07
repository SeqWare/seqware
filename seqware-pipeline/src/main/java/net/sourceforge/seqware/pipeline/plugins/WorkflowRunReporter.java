/*
 * Copyright (C) 2011 SeqWare
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
package net.sourceforge.seqware.pipeline.plugins;

import io.seqware.common.model.WorkflowRunStatus;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import joptsimple.ArgumentAcceptingOptionSpec;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.TabExpansionUtil;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.openide.util.lookup.ServiceProvider;

/**
 * <p>
 * WorkflowRunReporter class.
 * </p>
 * 
 * @author mtaschuk, boconnor
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowRunReporter extends Plugin {
    private static final String WRSTDERR = "wr-stderr";
    private static final String WRSTDOUT = "wr-stdout";

    private ReturnValue ret = new ReturnValue();
    private String csvFileName = null;
    private Writer writer;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss");

    private static final int STDOUT = 1;
    private static final int STDERR = 2;
    private final ArgumentAcceptingOptionSpec<WorkflowRunStatus> statusSpec;

    /**
     * <p>
     * Constructor for WorkflowRunReporter.
     * </p>
     */
    public WorkflowRunReporter() {
        super();
        parser.acceptsAll(Arrays.asList("workflow-run-accession", "wra"), "The SWID of the workflow run").withRequiredArg();
        parser.acceptsAll(Arrays.asList("workflow-accession", "wa"),
                "The SWID of " + "a workflow. All the workflow runs for that workflow will be " + "retrieved.").withRequiredArg();
        parser.acceptsAll(
                Arrays.asList("time-period", "t"),
                "Dates to check for " + "workflow runs. Dates are in format YYYY-MM-DD. If one date is "
                        + "provided, from that point to the present is checked. If two, "
                        + "separated by hyphen YYYY-MM-DDL:YYYY-MM-DD then it checks " + "that range").withRequiredArg();
        parser.acceptsAll(Arrays.asList("output-filename", "o"), "Optional: The output filename").withRequiredArg();
        parser.acceptsAll(Arrays.asList("stdout"), "Prints to standard out instead of to a file");
        parser.acceptsAll(Arrays.asList(WRSTDOUT),
                "Optional: will print the stdout of the workflow run, must specify the --workflow-run-accession");
        parser.acceptsAll(Arrays.asList(WRSTDERR),
                "Optional: will print the stderr of the workflow run, must specify the --workflow-run-accession");
        this.statusSpec = parser
                .accepts(
                        "status",
                        "Optional: Specify a particular status to restrict workflow runs that will be returned, status is one of "
                                + Arrays.toString(WorkflowRunStatus.values())).withRequiredArg().ofType(WorkflowRunStatus.class);
        parser.acceptsAll(Arrays.asList("human"), "Optional: will print output in expanded human friendly format");
        ret.setExitStatus(ReturnValue.SUCCESS);
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue init() {
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue do_test() {
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue do_run() {

        try {

            String timePeriod = null;
            WorkflowRunStatus status = options.valueOf(statusSpec);

            if (options.has("time-period")) {
                timePeriod = (String) options.valueOf("time-period");
            }

            /*
             * FIXME: I think this is problematic since the DB may use GMT whereas the Date() command returns something localized (I think).
             * We need to standardize on date/time across out DB and Java code.
             */
            Date firstDate = null, lastDate = null;
            if (timePeriod != null) {
                String[] dates = timePeriod.trim().split(":");
                SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    firstDate = localDateFormat.parse(dates[0].trim());
                    if (dates.length != 1) {
                        lastDate = localDateFormat.parse(dates[1].trim());
                    }
                } catch (ParseException ex) {
                    Log.warn("Date not found. Date must be in format YYYY-MM-DD or YYYY-MM-DD:YYYY-MM-DD.", ex);
                    println("Incorrect date");
                    ret = new ReturnValue(ReturnValue.INVALIDPARAMETERS);
                    return ret;
                }
                if (lastDate == null) {
                    lastDate = new Date();
                }
            }

            if (options.has(WRSTDERR) && options.has(WRSTDOUT)) {
                println("Combination of both stdout and stderr not recognized");
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                return ret;
            }

            if (options.has("workflow-run-accession")) {
                String wra = (String) options.valueOf("workflow-run-accession");
                if (options.has(WRSTDERR)) {
                    reportWorkflowRunStdErrOut(wra, STDERR);
                } else if (options.has(WRSTDOUT)) {
                    reportWorkflowRunStdErrOut(wra, STDOUT);
                } else {
                    reportOnWorkflowRun(wra);
                }
            } else if (options.has("workflow-accession") || options.has(statusSpec)) {
                String tp = (String) options.valueOf("workflow-accession");
                reportOnWorkflow(tp, status, firstDate, lastDate);
            }
            /**
             * SEQWARE-863 ability to query without specifying a specific workflow-accession was removed else if (firstDate != null ||
             * lastDate != null) { reportOnWorkflowRuns(firstDate, lastDate);
             */
            else {
                println("Combination of parameters not recognized!");
                println(this.get_syntax());
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                return ret;
            }
        } catch (IOException e) {
            Log.error(e.getMessage(), e);
            ret.setExitStatus(ReturnValue.FILENOTREADABLE);
            ret.setDescription(e.getMessage());
        }
        return ret;
    }

    private void reportOnWorkflowRun(String workflowRunAccession) throws IOException {
        String title = "workflowrun_" + workflowRunAccession;
        initWriter(title);
        String report;
        try {
            report = metadata.getWorkflowRunReport(Integer.parseInt(workflowRunAccession));
        } catch (RuntimeException e) {
            println("Workflow run not found");
            ret = new ReturnValue(ReturnValue.INVALIDPARAMETERS);
            return;
        }
        if (options.has("human")) {
            writer.write(TabExpansionUtil.expansion(report));
            return;
        }
        writer.write(report);

    }

    private void reportWorkflowRunStdErrOut(String workflowRunAccession, int streamType) throws IOException {
        String title = "workflowrun_" + workflowRunAccession;
        if (streamType == 1) {
            title = title + "_STDOUT";
            initWriter(title);
            writer.write(metadata.getWorkflowRunReportStdOut(Integer.parseInt(workflowRunAccession)));
        } else if (streamType == 2) {
            title = title + "_STDERR";
            initWriter(title);
            writer.write(metadata.getWorkflowRunReportStdErr(Integer.parseInt(workflowRunAccession)));
        } else {
            Log.error("Unknown stream type: " + streamType + " should be " + WorkflowRunReporter.STDERR + " for stderr or "
                    + WorkflowRunReporter.STDOUT + " for stdout!");
            initWriter(title);
            writer.write("Unknown stream type: " + streamType + " should be " + WorkflowRunReporter.STDERR + " for stderr or "
                    + WorkflowRunReporter.STDOUT + " for stdout!");
        }
    }

    private void reportOnWorkflow(String workflowAccession, WorkflowRunStatus status, Date earlyDate, Date lateDate) throws IOException {
        String title = "";
        if (workflowAccession != null) {
            title = "workflow_" + workflowAccession;
        }
        if (status != null) {
            title += "status_" + status.name();
        }
        if (earlyDate != null) {
            title += "from" + dateFormat.format(earlyDate);
        }
        if (lateDate != null) {
            title += "to" + dateFormat.format(lateDate);
        }
        initWriter(title);
        String report;
        try {
            Integer workflowParameter = null;
            if (workflowAccession != null) {
                workflowParameter = Integer.parseInt(workflowAccession);
            }
            report = metadata.getWorkflowRunReport(workflowParameter, status, earlyDate, lateDate);
        } catch (RuntimeException e) {
            Log.fatal("Workflow not found", e);
            ret = new ReturnValue(ReturnValue.INVALIDPARAMETERS);
            return;
        }
        if (options.has("human")) {
            writer.write(TabExpansionUtil.expansion(report));
            return;
        }
        writer.write(report);
    }

    private void initWriter(String string) throws IOException {
        String filename = dateFormat.format(new Date()) + "__" + string + ".csv";
        if (options.has("output-filename") && options.valueOf("output-filename") != null && !"".equals(options.valueOf("output-filename"))) {
            filename = (String) options.valueOf("output-filename");
        }
        csvFileName = filename;
        if (options.has("stdout")) {
            writer = new StringWriter();
        } else {
            writer = new BufferedWriter(new FileWriter(csvFileName, true));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue clean_up() {
        try {
            writer.flush();
            writer.close();
            if (options.has("stdout")) {
                Log.stdout(writer.toString());
            }

        } catch (IOException ex) {
            Log.error("Writer is already closed.", ex);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public String get_description() {
        return "This plugin creates a tab-separated file that describes one or more "
                + "workflow runs, including the identity, library samples and " + "input and output files. " + "For more information, see "
                + "see http://seqware.github.com/docs/19-workflow-run-reporter/";
    }
}
