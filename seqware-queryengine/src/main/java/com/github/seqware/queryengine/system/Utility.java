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
package com.github.seqware.queryengine.system;

import com.github.seqware.queryengine.system.importers.FeatureImporter;
import com.github.seqware.queryengine.util.SGID;
import java.io.*;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.Logger;

/**
 * Utility procedures for interacting outside the API
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class Utility {
    /**
     * Parse a timestamp-less SGID from a String representation
     *
     * @param stringSGID a {@link java.lang.String} object.
     * @return a {@link com.github.seqware.queryengine.util.SGID} object.
     */
    public static SGID parseSGID(String stringSGID) {
        SGID sgid;
        try{
            UUID uuid = UUID.fromString(stringSGID);
            sgid = new SGID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(), 0, null);
        } catch (IllegalArgumentException e){
            String fRowKey = stringSGID;
            sgid = new SGID(0,0,0,fRowKey);
        }
        return sgid;
    }
    
    /**
     * Write to output file a tab separated key value file represented by map
     *
     * @param outputFile a {@link java.io.File} object.
     * @param map a {@link java.util.Map} object.
     */
    public static void writeKeyValueFile(File outputFile, Map<String, String> map) {
        if (outputFile != null) {
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
                for (Map.Entry<String, String> e : map.entrySet()) {
                    out.println(e.getKey() + "\t" + e.getValue());
                    Logger.getLogger(FeatureImporter.class.getName()).info("Writing "+e.getKey() + " " + e.getValue() +" file");
                }
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(FeatureImporter.class.getName()).fatal("Could not write to output file");
            }
        }
    }
    
    /**
     * Check whether we can create the output for a particular filename
     *
     * @param filename a {@link java.lang.String} object.
     * @return reference to the newly created output file
     * @throws java.io.IOException if any.
     */
    public static File checkOutput(String filename) throws IOException {
        File outputFile = new File(filename);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        try {
            outputFile.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(FeatureImporter.class.getName()).fatal("Could not create output file");
            throw ex;
        }
        return outputFile;
    }
}
