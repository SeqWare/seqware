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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyType;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;

import org.openide.util.lookup.ServiceProvider;

/**
 * <p>BatchImport class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class BatchMetadataInjection extends Plugin {

    private ReturnValue ret = new ReturnValue();
    private boolean createStudy = false;

    /**
     * <p>Constructor for AttributeAnnotator.</p>
     */
    public BatchMetadataInjection() {
        super();
        parser.accepts("misec-sample-sheet", "the location of the MiSec Sample Sheet").withRequiredArg();
        parser.accepts("create-study", "Create a new study, if necessary, with the information in the file");
        ret.setExitStatus(ReturnValue.SUCCESS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue init() {
        createStudy = options.has("create-study");
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue do_test() {
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue do_run() {
        if (options.has("misec-sample-sheet")) {
            String filepath = (String) options.valueOf("misec-sample-sheet");
            RunInfo run = parseMiSecFile(filepath);
            inject(run);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue clean_up() {
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get_description() {
        return "Import objects into the database using different file formats.";
    }

    private ReturnValue inject(RunInfo run) {
        Study study = retrieveStudy(run);
        return ret;
    }

    private Study retrieveStudy(RunInfo run) {
        List<Study> studies = metadata.getAllStudies();
        Study study = null;
        for (Study st : studies) {
            if (st.getTitle().equals(run.getHeader().get("Project Name"))) {
                study = st;
            }
        }
        if (study == null) {
            if (createStudy) {
                for (StudyType st : metadata.getStudyTypes()) {
                    Log.stdout(st.toString());
                }
                int studyType = 4;
                Console c = System.console();
                String studyTypeStr = c.readLine("Type of study [Default 4] :");
                if (!studyTypeStr.trim().isEmpty()) {
                    studyType = Integer.parseInt(studyTypeStr);
                }

                ReturnValue r = metadata.addStudy("title", "description", null, null, "center name", "center project name", studyType);
                int studyId = r.getReturnValue();
            } else {
                Log.fatal("Study does not exist. Check the name of the study or add --create-study to your parameters");
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            }
        }
        return study;
    }

    protected RunInfo parseMiSecFile(String filepath) {
        RunInfo run = new RunInfo();
        File file = new File(filepath);
        try {
            BufferedReader freader = new BufferedReader(new FileReader(file));
            Map<String, String> header = parseMiSecHeader(freader);


            List<SampleInfo> samples = parseMiSecData(freader);
            freader.close();

            run.setHeader(header);
            run.setSamples(samples);

        } catch (FileNotFoundException e) {
            Log.error(filepath, e);
            ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        } catch (IOException ex) {
            Log.error(filepath, ex);
            ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        }
        return run;
    }

    protected List<SampleInfo> parseMiSecData(BufferedReader freader) throws IOException, NumberFormatException {
        List<SampleInfo> samples = new ArrayList<SampleInfo>();

        String line = freader.readLine(); //Discard header
        while ((line = freader.readLine()) != null) {
            String[] args = line.split(",");
            String[] sampleInfo = args[0].split("-");
            SampleInfo info = new SampleInfo();
            info.setName(args[0]);
            info.setParentSample(sampleInfo[0] + "-" + sampleInfo[1]);
            info.setBarcode(args[5]);
            info.setLane("1");
            info.setOrganism(args[8].split("\\\\")[0].replace('_', ' '));
            //info.setTargetedResequencing();
            if (sampleInfo[2].contains("BLD")) {
                info.setTissueType("R");
                info.setTissuePreparation("Blood");
            } else if (sampleInfo[2].contains("BIO")) {
                info.setTissueType("P");
            } else if (sampleInfo[2].contains("ARC")) {
                info.setTissueType("A");
            } else {
                Log.stdout("Unknown tissue type");
            }

            info.setTissueRegion(sampleInfo[2].substring(0, 1));
            //info.setTissueOrigin();
            samples.add(info);

        }
        return samples;
    }

    protected Map<String, String> parseMiSecHeader(BufferedReader freader) throws IOException {
        String line = null;
        Map<String, String> header = new HashMap<String, String>();
        while (!(line = freader.readLine()).startsWith("[Data]")) {
            if (!line.startsWith("[")) {
                String[] args = line.split(",");
                if (args.length >= 2) {
                    header.put(args[0].trim(), args[1].trim());
                }
            }
        }
        return header;
    }

    protected class RunInfo {

        private Map<String, String> header = null;
        private List<SampleInfo> samples = null;

        public Map<String, String> getHeader() {
            return header;
        }

        public void setHeader(Map<String, String> header) {
            this.header = header;
        }

        public List<SampleInfo> getSamples() {
            return samples;
        }

        public void setSamples(List<SampleInfo> samples) {
            this.samples = samples;
        }

        @Override
        public String toString() {
            String string = "RunInfo{\n" + "HEADER\n";
            for (String key : header.keySet()) {
                string += key + " : " + header.get(key) + "\n";
            }
            for (SampleInfo sample : samples) {
                string += sample.toString() + "\n";
            }
            string += '}';
            return string;
        }
    }

    protected class Header {

        private String studyTitle;
        private String runName;
        
        /**
         * Get the value of runName
         *
         * @return the value of runName
         */
        public String getRunName() {
            return runName;
        }

        /**
         * Set the value of runName
         *
         * @param runName new value of runName
         */
        public void setRunName(String runName) {
            this.runName = runName;
        }

        /**
         * Get the value of studyTitle
         *
         * @return the value of studyTitle
         */
        public String getStudyTitle() {
            return studyTitle;
        }

        /**
         * Set the value of studyTitle
         *
         * @param studyTitle new value of studyTitle
         */
        public void setStudyTitle(String studyTitle) {
            this.studyTitle = studyTitle;
        }

    }
    
    protected class SampleInfo {

        private String blank = "";
        private String name = blank;
        private String tissueType = blank;
        private String tissueRegion = blank;
        private String tissueOrigin = blank;
        private String tissuePreparation = blank;
        private String targetedResequencing = blank;
        private String templateType = blank;
        private String lane = blank;
        private String barcode = blank;
        private String organism = blank;
        private String parentSample = blank;

        /**
         * Get the value of parentSample
         *
         * @return the value of parentSample
         */
        public String getParentSample() {
            return parentSample;
        }

        /**
         * Set the value of parentSample
         *
         * @param parentSample new value of parentSample
         */
        public void setParentSample(String parentSample) {
            this.parentSample = parentSample;
        }

        /**
         * Get the value of organism
         *
         * @return the value of organism
         */
        public String getOrganism() {
            return organism;
        }

        /**
         * Set the value of organism
         *
         * @param organism new value of organism
         */
        public void setOrganism(String organism) {
            this.organism = organism;
        }

        /**
         * Get the value of barcode
         *
         * @return the value of barcode
         */
        public String getBarcode() {
            return barcode;
        }

        /**
         * Set the value of barcode
         *
         * @param barcode new value of barcode
         */
        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        /**
         * Get the value of lane
         *
         * @return the value of lane
         */
        public String getLane() {
            return lane;
        }

        /**
         * Set the value of lane
         *
         * @param lane new value of lane
         */
        public void setLane(String lane) {
            this.lane = lane;
        }

        /**
         * Get the value of templateType
         *
         * @return the value of templateType
         */
        public String getTemplateType() {
            return templateType;
        }

        /**
         * Set the value of templateType
         *
         * @param templateType new value of templateType
         */
        public void setTemplateType(String templateType) {
            this.templateType = templateType;
        }

        /**
         * Get the value of targetedResequencing
         *
         * @return the value of targetedResequencing
         */
        public String getTargetedResequencing() {
            return targetedResequencing;
        }

        /**
         * Set the value of targetedResequencing
         *
         * @param targetedResequencing new value of targetedResequencing
         */
        public void setTargetedResequencing(String targetedResequencing) {
            this.targetedResequencing = targetedResequencing;
        }

        /**
         * Get the value of tissuePreparation
         *
         * @return the value of tissuePreparation
         */
        public String getTissuePreparation() {
            return tissuePreparation;
        }

        /**
         * Set the value of tissuePreparation
         *
         * @param tissuePreparation new value of tissuePreparation
         */
        public void setTissuePreparation(String tissuePreparation) {
            this.tissuePreparation = tissuePreparation;
        }

        /**
         * Get the value of tissueOrigin
         *
         * @return the value of tissueOrigin
         */
        public String getTissueOrigin() {
            return tissueOrigin;
        }

        /**
         * Set the value of tissueOrigin
         *
         * @param tissueOrigin new value of tissueOrigin
         */
        public void setTissueOrigin(String tissueOrigin) {
            this.tissueOrigin = tissueOrigin;
        }

        /**
         * Get the value of tissueRegion
         *
         * @return the value of tissueRegion
         */
        public String getTissueRegion() {
            return tissueRegion;
        }

        /**
         * Set the value of tissueRegion
         *
         * @param tissueRegion new value of tissueRegion
         */
        public void setTissueRegion(String tissueRegion) {
            this.tissueRegion = tissueRegion;
        }

        /**
         * Get the value of tissueType
         *
         * @return the value of tissueType
         */
        public String getTissueType() {
            return tissueType;
        }

        /**
         * Set the value of tissueType
         *
         * @param tissueType new value of tissueType
         */
        public void setTissueType(String tissueType) {
            this.tissueType = tissueType;
        }

        /**
         * Get the value of name
         *
         * @return the value of name
         */
        public String getName() {
            return name;
        }

        /**
         * Set the value of name
         *
         * @param name new value of name
         */
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "SampleInfo{" + "\n\tname=" + name + " \n\ttissueType=" + tissueType
                    + " \n\ttissueRegion=" + tissueRegion + " \n\ttissueOrigin=" + tissueOrigin
                    + " \n\ttissuePreparation=" + tissuePreparation + " \n\ttargetedResequencing=" + targetedResequencing
                    + " \n\ttemplateType=" + templateType + " \n\tlane=" + lane
                    + " \n\tbarcode=" + barcode + " \n\torganism=" + organism + '}';
        }
    }
}
