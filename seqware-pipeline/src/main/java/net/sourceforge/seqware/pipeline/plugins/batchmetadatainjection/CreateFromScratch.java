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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.runtools.ConsoleAdapter;

/**
 *
 * @author mtaschuk
 */
public class CreateFromScratch extends BatchMetadataParser {
    
    public CreateFromScratch(Metadata metadata, Map<String, String> fields, boolean interactive) {
        super(metadata, fields, interactive);
    }
    
    public RunInfo getRunInfo() {
        RunInfo runInfo = this.generateRunInfo(null, null, null, null, null, null, null, null, null, -1, -1, true, null, null);
        String projectCode = "ZZZZ";
        
        int numLanes = this.prompt("Number of lanes?", 8, Field.number_of_lanes);
        
        for (int i = 0; i < numLanes; i++) {
            LaneInfo lane = getLane(i);
            int numBarcodes = this.prompt("How many barcodes in lane " + lane.getLaneNumber() + "?", 1, null);
            for (int j = 0; j < numBarcodes; j++) {
                try {
                    boolean done = false;
                    SampleInfo sample = null;
                    while (!done) {
                        projectCode = ConsoleAdapter.getInstance().promptString("Project code (three or four letters)", projectCode);
                        String individualNumber = ConsoleAdapter.getInstance().promptString("Individual number (should be unique per project)", null);
                        sample = this.generateSampleInfo(projectCode + " " + individualNumber, projectCode, individualNumber,
                                null, null, null, null, null, null, -1, null, null, null, null, null);
                        Log.stdout(sample.toString());
                        done = ConsoleAdapter.getInstance().promptBoolean("Is this correct?", true);
                    }
                    lane.getSamples().add(sample);
                    
                } catch (Exception ex) {
                    Logger.getLogger(CreateFromScratch.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            runInfo.getLanes().add(lane);
        }
        
        return runInfo;
    }

    private LaneInfo getLane(int i) {
        boolean doneLane = false;
        LaneInfo lane = null;
        while (!doneLane) {
            lane = this.generateLaneInfo(String.valueOf(i + 1), -1);
            Log.stdout(lane.toString());
            doneLane = ConsoleAdapter.getInstance().promptBoolean("Is this correct?", true);
        }
        return lane;
    }
}
