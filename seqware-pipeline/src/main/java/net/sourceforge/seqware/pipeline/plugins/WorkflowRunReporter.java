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

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author mtaschuk
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowRunReporter extends Plugin {

    ReturnValue ret = new ReturnValue();
    private final String FILETYPE_ALL = "all";
    private final String LINKTYPE_SYM = "s";
    private String fileType = FILETYPE_ALL;
    private String linkType = LINKTYPE_SYM;
    private String csvFileName = null;
    private Writer writer;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss");

    public WorkflowRunReporter() {
        super();
        parser.acceptsAll(Arrays.asList("workflow-run-accession", "wra"),
                "The SWID of the workflow run").withRequiredArg();
        parser.acceptsAll(Arrays.asList("time-period", "t"), "Dates to check for "
                + "workflow runs. Dates are in format YYYY-MM-DD. If one date is "
                + "provided, from that point to the present is checked. If two, "
                + "separated by hyphen YYYY-MM-DDL:YYYY-MM-DD then it checks "
                + "that range").withRequiredArg();
        parser.acceptsAll(Arrays.asList("workflow-accession", "wa"), "The SWID of "
                + "a workflow. All the workflow runs for that workflow will be "
                + "retrieved.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("output-filename", "o"), "Optional: The output filename").withRequiredArg();
        parser.acceptsAll(Arrays.asList("stdout"), "Prints to standard out instead of to a file");
        ret.setExitStatus(ReturnValue.SUCCESS);
    }

    @Override
    public ReturnValue init() {
        return ret;
    }

    @Override
    public ReturnValue do_test() {
        return ret;
    }

    @Override
    public ReturnValue do_run() {

        try {

            String timePeriod = null;

            if (options.has("time-period")) {
                timePeriod = (String) options.valueOf("time-period");
            } else if (options.has("t")) {
                timePeriod = (String) options.valueOf("t");
            }

            Date firstDate = null, lastDate = null;
            if (timePeriod != null) {
                String[] dates = timePeriod.trim().split(":");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    firstDate = dateFormat.parse(dates[0].trim());
                    if (dates.length != 1) {
                        lastDate = dateFormat.parse(dates[1].trim());
                    }
                } catch (ParseException ex) {
                    Log.warn("Date not found. Date must be in format YYYY-MM-DD or YYYY-MM-DD:YYYY-MM-DD.", ex);
                }
                if (lastDate == null) {
                    lastDate = new Date();
                }
            }


            if (options.has("workflow-run-accession")) {
                String wra = (String) options.valueOf("workflow-run-accession");
                reportOnWorkflowRun(wra);
            } else if (options.has("wra")) {
                String wra = (String) options.valueOf("wra");
                reportOnWorkflowRun(wra);
            } else if (options.has("workflow-accession")) {
                String tp = (String) options.valueOf("workflow-accession");
                reportOnWorkflow(tp, firstDate, lastDate);
            } else if (options.has("wa")) {
                String tp = (String) options.valueOf("wa");
                reportOnWorkflow(tp, firstDate, lastDate);
            } else if (firstDate != null || lastDate != null) {
                reportOnWorkflowRuns(firstDate, lastDate);
            } else {
                println("Combination of parameters not recognized!");
                println(this.get_syntax());
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                return ret;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.error(e.getMessage(), e);
            ret.setExitStatus(ReturnValue.FILENOTREADABLE);
            ret.setDescription(e.getMessage());
        }
        return ret;
    }

    private void reportOnWorkflowRun(String workflowRunAccession) throws IOException {
        String title = "workflowrun" + workflowRunAccession;
        initWriter(title);
        String report = metadata.getWorkflowRunReport(Integer.parseInt(workflowRunAccession));
        writer.write(report);

    }

    private void reportOnWorkflow(String workflowAccession, Date earlyDate, Date lateDate) throws IOException {
        String title = "workflow" + workflowAccession;
        if (earlyDate != null) {
            title += "from" + dateFormat.format(earlyDate);
        }
        if (lateDate != null) {
            title += "to" + dateFormat.format(lateDate);
        }
        initWriter(title);
        String report = metadata.getWorkflowRunReport(Integer.parseInt(workflowAccession), earlyDate, lateDate);
        writer.write(report);
    }

    private void reportOnWorkflowRuns(Date earlyDate, Date lateDate) throws IOException {
        String title = "workflowruns";
        if (earlyDate != null) {
            title += "from" + dateFormat.format(earlyDate);
        }
        if (lateDate != null) {
            title += "to" + dateFormat.format(lateDate);
        }
        initWriter(title);
        String report = metadata.getWorkflowRunReport(earlyDate, lateDate);
        writer.write(report);
    }

    private void initWriter(String string) throws IOException {
        String currentDir = new File(".").getAbsolutePath();
        String filename = dateFormat.format(new Date()) + "__" + string;
        if (options.has("output-filename")) {
            filename = (String) options.valueOf("output-filename");
        }
        csvFileName = currentDir + File.separator + filename + ".csv";
        if (options.has("stdout")) {
            writer = new StringWriter();
        } else {
            writer = new BufferedWriter(new FileWriter(csvFileName, true));
        }
    }

    @Override
    public ReturnValue clean_up() {
        try {
            writer.flush();
            writer.close();
            if (options.has("stdout"))
            {
                Log.stdout(writer.toString());
            }

        } catch (IOException ex) {
            Log.error("Writer is already closed.", ex);
        }
        return ret;
    }

    @Override
    public String get_description() {
        return "This plugin creates a tab-separated file that describes one or more "
                + "workflow runs, including the identity and library samples and "
                + "input and output files. "
                + "For more information, see "
                + "https://sourceforge.net/apps/mediawiki/seqware/index.php?title=Workflow_Run_Reporter";
    }
}
