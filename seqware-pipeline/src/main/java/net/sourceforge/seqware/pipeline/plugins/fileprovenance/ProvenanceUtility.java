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
package net.sourceforge.seqware.pipeline.plugins.fileprovenance;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.FileProvenanceParam;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;

/**
 * Convenience methods that can be shared by utilities that access the
 * FileProvenance report
 *
 * @author dyuen
 */
public class ProvenanceUtility {

    private static final String AT_LEAST_ONE_OF = "At least one of sample-name, study-name, sequencer-run-name, lane-swa, ius-swa or all is required. ";
    public static final String LANE_SWID = "lane-SWID";
    public static final String IUS_SWID = "ius-SWID";
    public static final String STUDY_NAME = "study-name";
    public static final String SAMPLE_NAME = "sample-name";
    public static final String SEQUENCER_RUN_NAME = "sequencer-run-name";
    public static final String ALL = "all";

    public static Map<String, OptionSpec> configureFileProvenanceParams(OptionParser parser) {
        Map<String, OptionSpec> specMap = new HashMap<String, OptionSpec>();
        ArgumentAcceptingOptionSpec<String> studyTitleSpec = parser.acceptsAll(Arrays.asList(STUDY_NAME), "Full study name. " + AT_LEAST_ONE_OF + "Specify multiple names by repeating --study-name").withRequiredArg().ofType(String.class);
        specMap.put(STUDY_NAME, studyTitleSpec);
        ArgumentAcceptingOptionSpec<String> sampleNameSpec = parser.acceptsAll(Arrays.asList(SAMPLE_NAME), "Full sample name. " + AT_LEAST_ONE_OF + " Specify multiple names by repeating --sample-name").withRequiredArg().ofType(String.class);
        specMap.put(SAMPLE_NAME, sampleNameSpec);
        ArgumentAcceptingOptionSpec<String> sequencerRunNameSpec = parser.acceptsAll(Arrays.asList(SEQUENCER_RUN_NAME), "Full sequencer run name. " + AT_LEAST_ONE_OF + " Specify multiple names by repeating --sequencer-run-name").withRequiredArg().ofType(String.class);
        specMap.put(SEQUENCER_RUN_NAME, sequencerRunNameSpec);
        // adding lane and ius
        ArgumentAcceptingOptionSpec<Integer> laneSwaSpec = parser.acceptsAll(Arrays.asList(LANE_SWID), "Lane sw_accession. " + AT_LEAST_ONE_OF + " Specify multiple swas by repeating --lane-swa").withRequiredArg().ofType(Integer.class);
        specMap.put(LANE_SWID, laneSwaSpec);
        ArgumentAcceptingOptionSpec<Integer> iusSwaSpec = parser.acceptsAll(Arrays.asList(IUS_SWID), "IUS sw_accession. " + AT_LEAST_ONE_OF + " Specify multiple swas by repeating --ius-swa").withRequiredArg().ofType(Integer.class);
        specMap.put(IUS_SWID, iusSwaSpec);
        parser.acceptsAll(Arrays.asList(ALL), "Operate across everything. " + AT_LEAST_ONE_OF);
        return specMap;
    }
    
    public static boolean checkForValidOptions(OptionSet set){
        boolean hasConstraint = false;
        if (set.hasArgument(LANE_SWID) || set.hasArgument(IUS_SWID) || set.hasArgument(STUDY_NAME) || set.hasArgument(SAMPLE_NAME) || set.hasArgument(SEQUENCER_RUN_NAME)){
            hasConstraint = true;
        }
        boolean hasAll = false;
        if (set.hasArgument(ALL)){
            hasAll = true;
        }
        return hasConstraint ^ hasAll;
    }

    public static Map<FileProvenanceParam, List<String>> convertOptionsToMap(OptionSet options, Metadata metadata) {
        Map<FileProvenanceParam, List<String>> map = new EnumMap<FileProvenanceParam, List<String>>(FileProvenanceParam.class);
        if (options.has(ALL)) {
            /**
             * nothing special
             */
        } else {
            if (options.has(STUDY_NAME)) {
                List<String> studyNames = (List<String>) options.valuesOf(STUDY_NAME);
                List<String> studySWAs = new ArrayList<String>();
                for (String studyName : studyNames) {
                    Study studyByName = metadata.getStudyByName(studyName);
                    studySWAs.add(String.valueOf(studyByName.getSwAccession()));
                }
                map.put(FileProvenanceParam.study, new ImmutableList.Builder<String>().addAll(studySWAs).build());
            }
            if (options.has(SAMPLE_NAME)) {
                List<String> sampleNames = (List<String>) options.valuesOf(SAMPLE_NAME);
                List<String> sampleSWAs = new ArrayList<String>();

                for (String sampleName : sampleNames) {
                    List<Sample> samplesByName = metadata.getSampleByName(sampleName);
                    for (Sample sample : samplesByName) {
                        sampleSWAs.add(String.valueOf(sample.getSwAccession()));
                    }
                }
                map.put(FileProvenanceParam.sample, new ImmutableList.Builder<String>().addAll(sampleSWAs).build());
            }
            if (options.has(SEQUENCER_RUN_NAME)) {
                List<String> sequencerRunNames = (List<String>) options.valuesOf(SEQUENCER_RUN_NAME);
                List<String> sequencerRunSWAs = new ArrayList<String>();
                for (String sequencerRunName : sequencerRunNames) {
                    SequencerRun run = metadata.getSequencerRunByName(sequencerRunName);
                    sequencerRunSWAs.add(String.valueOf(run.getSwAccession()));
                }
                map.put(FileProvenanceParam.sequencer_run, new ImmutableList.Builder<String>().addAll(sequencerRunSWAs).build());
            }
            if (options.has(IUS_SWID)) {
                List<?> swa_values = options.valuesOf(IUS_SWID);
                List<String> swa_strings = new ArrayList<String>();
                for (Object swa : swa_values) {
                    swa_strings.add(String.valueOf(swa));
                }
                map.put(FileProvenanceParam.ius, new ImmutableList.Builder<String>().addAll(swa_strings).build());
            }
            if (options.has(LANE_SWID)) {
                List<?> swa_values = options.valuesOf(LANE_SWID);
                List<String> swa_strings = new ArrayList<String>();
                for (Object swa : swa_values) {
                    swa_strings.add(String.valueOf(swa));
                }
                map.put(FileProvenanceParam.lane, new ImmutableList.Builder<String>().addAll(swa_strings).build());
            }
        }
        return map;
    }
}
