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
package net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mtaschuk
 */
public class ParseMisecFile extends BatchMetadataParser {

    private ReturnValue ret = new ReturnValue();

    public ParseMisecFile(Metadata metadata, Map<String, String> fields) {
        super(metadata, fields);
    }

    public RunInfo parseMiSecFile(String filepath) throws Exception {
        RunInfo run = null;
        File file = new File(filepath);
        try {
            BufferedReader freader = new BufferedReader(new FileReader(file));
            run = parseMiSecHeader(freader, filepath);
            String runName = prompt("Sequencer run name", run.getRunName(), Field.sequencer_run_name);
            String studyName = prompt("Study name", run.getStudyTitle(), Field.study_name);
            String expName = prompt("Experiment name", run.getExperimentName(), Field.experiment_name);
            run.setRunName(runName);
            run.setStudyTitle(studyName);
            run.setExperimentName(expName);

            Set<LaneInfo> lanes = parseMiSecData(freader);
            freader.close();

            run.setLanes(lanes);

        } catch (FileNotFoundException e) {
            Log.error(filepath, e);
            ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        } catch (IOException ex) {
            Log.error(filepath, ex);
            ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        }
        return run;
    }

    public Set<LaneInfo> parseMiSecData(BufferedReader freader) throws IOException, Exception {
        Set<SampleInfo> samples = new HashSet<SampleInfo>();

        //there is only one lane in MiSec
        LaneInfo laneInfo = generateLaneInfo("1", 4);
        laneInfo.setSamples(samples);

        String[] headerStrings = freader.readLine().split(",");
        List<String> header = Arrays.asList(headerStrings);

        String line;
        while ((line = freader.readLine()) != null) {
            String[] args = line.split(",");
            String[] sampleInfo = args[header.indexOf("Sample_ID")].split("-");

            String prettyName = args[header.indexOf("Sample_ID")];
            String projectName = sampleInfo[0];
            String individualNumber = sampleInfo[1];
            String librarySourceTemplateType = null;
            String tissueOrigin = null;
            String tissueType = null;
            String libraryType = null;
            String librarySizeCode = null;
            String targetedResequencing = null;
            String tissuePreparation = null;
            Integer organismId = findOrganismId(args[header.indexOf("GenomeFolder")].split("\\\\")[0].replace('_', ' '));
            String barcode = args[header.indexOf("index")];

            if (sampleInfo[2].contains("BLD")) {
                tissueType = "R";
                tissuePreparation = "Blood";
            } else if (sampleInfo[2].contains("BIO")) {
                tissueType = "P";
            } else if (sampleInfo[2].contains("ARC")) {
                tissueType = "P";
            } else {
                Log.stdout("Cannot parse tissue type from " + prettyName);
            }

            SampleInfo info = generateSampleInfo(prettyName, projectName, individualNumber,
                    librarySourceTemplateType, tissueOrigin, tissueType, libraryType,
                    librarySizeCode, barcode, organismId, targetedResequencing,
                    tissuePreparation);

            info.setSampleDescription(info.getName());
            info.setIusName(info.getBarcode());
            info.setIusDescription(info.getBarcode());


            String tissueRegion = sampleInfo[2].substring(0, 1);
            if (StringUtils.isNumeric(tissueRegion)) {
                info.addSampleAttribute("geo_tissue_region", tissueRegion);
            }
            samples.add(info);
        }

        Set<LaneInfo> lanes = new HashSet<LaneInfo>();
        lanes.add(laneInfo);
        return lanes;
    }

    public RunInfo parseMiSecHeader(BufferedReader freader, String fileName) throws IOException {
        String line = null;
        RunInfo runInfo = new RunInfo();

        Map<String, String> headerInfo = new HashMap<String, String>();
        while (!(line = freader.readLine()).startsWith("[Data]")) {
            if (!line.startsWith("[")) {
                String[] args = line.split(",");
                if (args.length >= 2) {
                    headerInfo.put(args[0].trim(), args[1].trim());
                }
            }
        }
        String[] bits = fileName.split(File.separator);
        runInfo.setRunName(bits[bits.length - 2]);
        runInfo.setRunDescription(runInfo.getRunName());
        runInfo.setPairedEnd(true);
        runInfo.setPlatformId(26);

        runInfo.setStudyTitle(headerInfo.get("Project Name").split("_")[0]);
        runInfo.setStudyCenterName("Ontario Institute for Cancer Research");
        runInfo.setStudyCenterProject(runInfo.getStudyTitle().replace(" ", ""));
        runInfo.setStudyDescription(runInfo.getStudyTitle());
        //runInfo.setStudyType(studyType);

        runInfo.setExperimentName(headerInfo.get("Experiment Name").split("_")[0]);
        runInfo.setExperimentDescription(runInfo.getExperimentName());


        runInfo.setWorkflowType(headerInfo.get("Workflow"));
        runInfo.setAssayType(headerInfo.get("Assay"));


        return runInfo;
    }
}
