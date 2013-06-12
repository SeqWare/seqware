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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.TabExpansionUtil;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.openide.util.lookup.ServiceProvider;

/**
 * <p>SymLinkFileReporter class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class SymLinkFileReporter extends Plugin {
    private static final String SHOW_STATUS = "show-status";
    //Filenames have a max size of 144 chars on Ubuntu, believe it or not.
    public static final int MAX_FILE_NAME_SIZE = 143;
    
    private ReturnValue ret = new ReturnValue();
    private static final String LINKTYPE_SYM = "s";
    private String fileType = FindAllTheFiles.FILETYPE_ALL;
    private String linkType = LINKTYPE_SYM;
    private Writer writer;
    
    /**
     * <p>Constructor for SymLinkFileReporter.</p>
     */
    public SymLinkFileReporter() {
        super();
        parser.acceptsAll(Arrays.asList("study"), "Make symlinks for a study").withRequiredArg();
        parser.acceptsAll(Arrays.asList("sample"), "Make symlinks for a sample").withRequiredArg();
        //FIXME: SymLinking using SequencerRuns is not working properly yet, so it is disabled for now.
        parser.acceptsAll(Arrays.asList("sequencer-run"), "Make symlinks for a sequencerRun").withRequiredArg();
        parser.acceptsAll(Arrays.asList("file-type", "f"), "Optional: The file type to filter on. Only this type will be linked. Default is all files. Permissible file metatypes can found on our website under 'Module Conventions'").withRequiredArg();
        parser.acceptsAll(Arrays.asList("workflow-accession", "w"), "Optional: List all workflow runs with this workflow accession").withRequiredArg();
        parser.acceptsAll(Arrays.asList("link", "l"), "Optional: make hard links (P) or symlinks (s). Default is symlinks.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("prod-format"), "Optional: print the directories in prod format");
        parser.acceptsAll(Arrays.asList("duplicates"), "Optional: Allow duplications at the file path level");
        parser.acceptsAll(Arrays.asList("dump-all"), "Optional: Dumps all of the studies in the database to one file.");
        parser.acceptsAll(Arrays.asList("no-links"), "Optional: Create only the CSV file, not the symlinked directories.");
        parser.acceptsAll(Arrays.asList("output-filename"), "Optional: Name of the output CSV file (without the extension)").withRequiredArg();
        parser.acceptsAll(Arrays.asList("show-failed-and-running"), "Show all of the files regardless of the workflow run status. Default shows only successful runs.");
        parser.acceptsAll(Arrays.asList(SHOW_STATUS), "Show the workflow run status in the output CSV.");
        parser.acceptsAll(Arrays.asList("human"), "Optional: will print output in expanded human friendly format");
        parser.acceptsAll(Arrays.asList("stdout"), "Prints to standard out instead of to a file");
        ret.setExitStatus(ReturnValue.SUCCESS);
    }
    
    /** {@inheritDoc} */
    @Override
    public ReturnValue init() {
        
        return ret;
    }
    
    /** {@inheritDoc} */
    @Override
    public ReturnValue do_test() {
        return ret;
    }
    
    /** {@inheritDoc} */
    @Override
    public ReturnValue do_run() {
        String currentDir = new File(".").getAbsolutePath();
        fileType = getFileType();
        linkType = getLinkType();
        try {
            if (options.has("study")) {
                String study = (String) options.valueOf("study");
                initWriter(currentDir, study);
                reportOnStudy(study, currentDir);
            } else if (options.has("sample")) {
                String sample = (String) options.valueOf("sample");
                initWriter(currentDir, sample);
                ret = reportOnSample(sample, currentDir);
            } else if (options.has("sequencer-run")) {
                String sequencerRun = (String) options.valueOf("sequencer-run");
                initWriter(currentDir, sequencerRun);
                ret = reportOnSequencerRun(sequencerRun, currentDir);
            } else if (options.has("dump-all")) {
                initWriter(currentDir, "all");
                ret = reportAll(currentDir);
            } else {
                println("Combination of parameters not recognized!");
                println(this.get_syntax());
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            }
        } catch (IOException e) {
            Log.fatal("SymLinkFileReporter failed to write output",e);
            ret.setExitStatus(ReturnValue.FILENOTREADABLE);
            ret.setDescription(e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(SymLinkFileReporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return ret;
    }
    
    private void initWriter(String currentDir, String string) throws IOException {
        String filename = new Date().toString().replace(" ", "_") + "__" + string;
        if (options.has("output-filename")) {
            filename = (String) options.valueOf("output-filename");
        }
        String csvFileName = currentDir + File.separator + filename + ".csv";
        
        if (options.has("stdout")) {
            writer = new StringWriter();
        } else {
            writer = new BufferedWriter(new FileWriter(csvFileName, true));
        }       
    }
    
    private ReturnValue reportOnStudy(String studyName, String rootDirectory) throws IOException {
        println("Searching for study with title: " + studyName);
        List<ReturnValue> returnValues = metadata.findFilesAssociatedWithAStudy(studyName);
        okGo(returnValues, rootDirectory, studyName);
        return ret;
    }
    
    private ReturnValue reportOnSample(String sampleName, String rootDirectory) throws IOException {
        println("Searching for sample with title: " + sampleName);
        List<ReturnValue> returnValues = metadata.findFilesAssociatedWithASample(sampleName);
        okGo(returnValues, rootDirectory, null);
        return ret;
    }
    
    private ReturnValue reportOnSequencerRun(String sequencerRun, String rootDirectory) throws IOException {
        println("Searching for sequencer run with name: " + sequencerRun);
        List<ReturnValue> returnValues = metadata.findFilesAssociatedWithASequencerRun(sequencerRun);
        okGo(returnValues, rootDirectory, null);
        return ret;
    }
    
    private ReturnValue reportAll(String rootDirectory) throws IOException {
        println("Dumping all studies to file");
        List<Study> studies = metadata.getAllStudies();
        for (Study study : studies) {
            String name = study.getTitle();
            println("Dumping study: " + name);
            List<ReturnValue> returnValues = metadata.findFilesAssociatedWithAStudy(name);
            okGo(returnValues, rootDirectory, name);
        }
        return ret;
    }
    
    private void okGo(List<ReturnValue> returnValues, String rootDirectory, String studyName) throws IOException {
        println("There are " + returnValues.size() + " returnValues in total before filtering");
        println("Saving symlinks and creating CSV file");
        
        returnValues = FindAllTheFiles.filterReturnValues(returnValues, studyName,
                fileType, options.has("duplicates"), options.has("show-failed-and-running"),
                options.has(SHOW_STATUS));
        
        if (options.has("human")){
            StringWriter sWriter = new StringWriter();
            FindAllTheFiles.printTSVFile(sWriter, options.has(SHOW_STATUS),
                    returnValues, studyName);
            writer.write(TabExpansionUtil.expansion(sWriter.toString()));
            return;
        } else{
            FindAllTheFiles.printTSVFile(writer, options.has(SHOW_STATUS),
                    returnValues, studyName);
        }
        
        for (ReturnValue rv : returnValues) {
            StringBuilder directory = new StringBuilder();
            directory.append(rootDirectory).append(File.separator);

            //First pull all of the required information out of the ReturnValue
            String studySwa = rv.getAttribute(FindAllTheFiles.STUDY_SWA);
            String parentSampleName = rv.getAttribute(FindAllTheFiles.PARENT_SAMPLE_NAME);
            String parentSampleSwa = rv.getAttribute(FindAllTheFiles.PARENT_SAMPLE_SWA);
            String sampleName = rv.getAttribute(FindAllTheFiles.SAMPLE_NAME);
            String sampleSwa = rv.getAttribute(FindAllTheFiles.SAMPLE_SWA);
            String iusSwa = rv.getAttribute(FindAllTheFiles.IUS_SWA);
            String iusTag = rv.getAttribute(FindAllTheFiles.IUS_TAG);
            String laneNum = rv.getAttribute(FindAllTheFiles.LANE_NUM);
            String sequencerRunName = rv.getAttribute(FindAllTheFiles.SEQUENCER_RUN_NAME);
            String sequencerRunSwa = rv.getAttribute(FindAllTheFiles.SEQUENCER_RUN_SWA);
            String workflowRunName = rv.getAttribute(FindAllTheFiles.WORKFLOW_RUN_NAME);
            String workflowRunSwa = rv.getAttribute(FindAllTheFiles.WORKFLOW_RUN_SWA);
            String workflowName = rv.getAttribute(FindAllTheFiles.WORKFLOW_NAME);
            String workflowSwa = rv.getAttribute(FindAllTheFiles.WORKFLOW_SWA);
            String workflowVersion = rv.getAttribute(FindAllTheFiles.WORKFLOW_VERSION);
            
            if (!options.has("no-links")) {
                ///Save in the format requested
                if (options.has("prod-format")) {
                    saveProdFileName(directory, studyName, studySwa, parentSampleName,
                            parentSampleSwa, rv, workflowName, workflowVersion,
                            sampleName, sampleSwa);
                } else {
                    if (studyName != null && studySwa != null) {
                        directory.append(studyName).append("-").append(studySwa);
                        directory.append(File.separator);
                    }
                    directory.append(parentSampleName).append("-").append(parentSampleSwa);
                    directory.append(File.separator);
                    directory.append(sampleName).append("-").append(sampleSwa);
                    
                    saveSeqwareFileName(directory.toString(), workflowName, workflowSwa,
                            workflowRunName, workflowRunSwa, sequencerRunName,
                            sequencerRunSwa, laneNum, iusTag, iusSwa, sampleName,
                            sampleSwa, rv);
                }
            }
            
        }
        
    }

    /**
     * Links files in a loose interpretation of the Production format.
     *
     * @param directory the base directory to build the hierarchical structure
     * @param studyName
     * @param studySwa study seqware accession
     * @param parentSampleName
     * @param parentSampleSwa parent sample seqware accession
     * @param rv the ReturnValue where all the attributes are stored
     * @param workflowName
     * @param workflowVersion
     * @param sampleName
     * @param sampleSwa
     */
    private void saveProdFileName(StringBuilder directory, String studyName, String studySwa,
            String parentSampleName, String parentSampleSwa, ReturnValue rv, String workflowName, String workflowVersion, String sampleName,
            String sampleSwa) {
        if (studyName != null && studySwa != null) {
            directory.append(studyName).append("-").append(studySwa);
            directory.append(File.separator);
        }
        directory.append(parentSampleName).append("-").append(parentSampleSwa);
        directory.append(File.separator);
        directory.append(parentSampleName);
        directory.append(rv.getAttribute(FindAllTheFiles.SAMPLE_TAG_PREFIX + "geo_tissue_type"));
        directory.append(File.separator);
        directory.append(rv.getAttribute(FindAllTheFiles.SAMPLE_TAG_PREFIX + "geo_library_source_template_type"));
        directory.append(File.separator);
        directory.append(rv.getAlgorithm());
        directory.append(File.separator);
        directory.append(workflowName).append("-").append(workflowVersion);
        directory.append(File.separator);
        directory.append(sampleName).append("-").append(sampleSwa);
        
        saveFiles(rv.getFiles(), "", directory.toString());
    }

    /**
     * Links files in the SeqWare file format and directory hierarchy.
     *
     * @param directory
     * @param workflowName
     * @param workflowSwa
     * @param workflowRunName
     * @param workflowRunSwa
     * @param sequencerRunName
     * @param sequencerRunSwa
     * @param laneNum
     * @param iusTag
     * @param iusSwa
     * @param sampleName
     * @param sampleSwa
     * @param rv
     */
    private void saveSeqwareFileName(String directory, String workflowName,
            String workflowSwa, String workflowRunName, String workflowRunSwa,
            String sequencerRunName, String sequencerRunSwa, String laneNum,
            String iusTag, String iusSwa, String sampleName, String sampleSwa,
            ReturnValue rv) {
        
        
        StringBuilder fileNamePrefix = new StringBuilder();
        fileNamePrefix.append(workflowName).append("-");
        fileNamePrefix.append(workflowSwa).append("__");
        fileNamePrefix.append(workflowRunName).append("-");
        fileNamePrefix.append(workflowRunSwa).append("__");
        fileNamePrefix.append(sequencerRunName).append("-");
        fileNamePrefix.append(sequencerRunSwa).append("__");
        fileNamePrefix.append(laneNum).append("__");
        fileNamePrefix.append(iusTag).append("-");
        fileNamePrefix.append(iusSwa).append("__");
        fileNamePrefix.append(sampleName).append("-");
        fileNamePrefix.append(sampleSwa).append("__");
        saveFiles(rv.getFiles(), fileNamePrefix.toString(),
                directory);
    }

    /**
     * Link some files and creates a directory structure.
     *
     * @param files a list of the files
     * @param fileNamePrefix the prefix of the files
     * @param directory the directory in which to link the files
     */
    private void saveFiles(List<FileMetadata> files,
            String fileNamePrefix, String directory) {
        for (FileMetadata fm : files) {
            if (fm.getMetaType().equals(fileType) || fileType.equals(FindAllTheFiles.FILETYPE_ALL)) {
                String[] pathArr = fm.getFilePath().split(File.separator);
                String filename = fileNamePrefix + pathArr[pathArr.length - 1];

                //Filenames have a max size of 144 chars on Ubuntu, believe it or not.
                if (filename.length() >= MAX_FILE_NAME_SIZE) {
                    String pieces = fm.getDescription() + pathArr[pathArr.length - 1];
                    String ext = "." + fm.getMetaType().replace("/", ".");
                    if (pieces.length() > MAX_FILE_NAME_SIZE - ext.length()) {
                        filename = pieces.substring(0, MAX_FILE_NAME_SIZE - ext.length()) + ext;
                    }
                }
                
                
                try {
                    (new File(directory)).mkdirs();
                    Process process = Runtime.getRuntime().exec(
                            new String[]{"ln", "-" + linkType, fm.getFilePath(),
                                directory + File.separator + filename});
                    process.waitFor();
                } catch (InterruptedException ex) {
                    Logger.getLogger(SymLinkFileReporter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(SymLinkFileReporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private String getFileType() {
        if (options.has("file-type")) {
            return (String) options.valueOf("file-type");
        } else {
            return FindAllTheFiles.FILETYPE_ALL;
        }
    }
    
    private String getLinkType() {
        if (options.has("link")) {
            return (String) options.valueOf("link");
        } else {
            return LINKTYPE_SYM;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public ReturnValue clean_up() {
        if (options.has("stdout")) {
            Log.stdout(writer.toString());
        }
        return ret;
    }
    
    /** {@inheritDoc} */
    @Override
    public String get_description() {
        return "Create a nested tree structure of all of the output files from a "
                + "particular sample, or all of the samples in a study by using "
                + "the SymLinkFileReporter plugin. This plugin also creates a CSV "
                + "file with all of the accompanying information for every file. "
                + "For more information, see "
                + "see http://seqware.github.com/docs/21-study-reporter/";
    }
}
