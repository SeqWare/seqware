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
package com.github.seqware.queryengine.system.exporters;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.MRHBaseModelManager;
import com.github.seqware.queryengine.impl.MRHBasePersistentBackEnd;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryFuture;
import com.github.seqware.queryengine.plugins.AnalysisPluginInterface;
import com.github.seqware.queryengine.plugins.hbasemr.MRVCFDumperPlugin;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.system.importers.workers.ImportConstants;
import com.github.seqware.queryengine.util.SGID;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.log4j.Logger;

/**
 * This will dump VCF files given a FeatureSet that was originally imported from
 * a VCF file.
 *
 * @author dyuen
 */
public class VCFDumper {

    private String[] args;
    /**
     * This does not work quite right, I need to copy from hdfs and parse the
     * output a little Performance (speed) should be correct though.
     */
    public static final boolean EXPERIMENTAL_MAP_REDUCE_EXPORT = false;

    public static void main(String[] args) {
        VCFDumper dumper = new VCFDumper(args);
        dumper.export();
    }

    public void export() {

        if (args.length < 1 || args.length > 2) {
            System.err.println(args.length + " arguments found");
            System.out.println("VCFDumper <featureSetID> [outputFile]");
            System.exit(-1);
        }

        // parse a SGID from a String representation, we need a more elegant solution here
        String featureSetID = args[0];
        SGID sgid = Utility.parseSGID(featureSetID);
        FeatureSet fSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(sgid, FeatureSet.class);

        // if this featureSet does not exist
        if (fSet == null) {
            System.out.println("featureSet ID not found");
            System.exit(-2);
        }
        dumpVCFFromFeatureSetID(fSet, (args.length == 2 ? args[1] : null));
    }

    public VCFDumper(String[] args) {
        this.args = args;
    }

    public static boolean outputFeatureInVCF(StringBuffer buffer, Feature feature) {
        boolean caughtNonVCF = false;
        buffer.append(feature.getSeqid()).append("\t").append(feature.getStart() + 1).append("\t");
        if (feature.getTagByKey(ImportConstants.VCF_SECOND_ID) == null) {
            buffer.append(".\t");
        } else {
            buffer.append(feature.getTagByKey(ImportConstants.VCF_SECOND_ID).getValue().toString()).append("\t");
        }
        try {
            buffer.append(feature.getTagByKey(ImportConstants.VCF_REFERENCE_BASE).getValue().toString()).append("\t");
            buffer.append(feature.getTagByKey(ImportConstants.VCF_CALLED_BASE).getValue().toString()).append("\t");
            buffer.append(feature.getScore() == null ? "." : feature.getScore()).append("\t");
            buffer.append(feature.getTagByKey(ImportConstants.VCF_FILTER).getValue().toString()).append("\t");
            buffer.append(feature.getTagByKey(ImportConstants.VCF_INFO).getValue().toString());
        } catch (NullPointerException npe) {
            if (!caughtNonVCF) {
                Logger.getLogger(VCFDumper.class.getName()).info("VCF exporting non-VCF feature");

            }
            // this may occur when exporting Features that were not originally VCF files
            caughtNonVCF = true;
        }
        return caughtNonVCF;
    }

    public static void dumpVCFFromFeatureSetID(FeatureSet fSet, String file) {
        BufferedWriter outputStream = null;
        try {

            if (file != null) {
                outputStream = new BufferedWriter(new FileWriter(file));
            } else {
                outputStream = new BufferedWriter(new OutputStreamWriter(System.out));
            }
            outputStream.append("#CHROM	POS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\n");
            boolean caughtNonVCF = false;

            if (SWQEFactory.getBackEnd() instanceof MRHBasePersistentBackEnd && EXPERIMENTAL_MAP_REDUCE_EXPORT) {
                // hack to use VCF MR
                if (SWQEFactory.getModelManager() instanceof MRHBaseModelManager) {
                    // pretend that the included com.github.seqware.queryengine.plugins.hbasemr.MRFeaturesByAttributesPlugin is an external plug-in
                    Class<? extends AnalysisPluginInterface> arbitraryPlugin;
                    arbitraryPlugin = MRVCFDumperPlugin.class;

                    // get a FeatureSet from the back-end
                    QueryFuture<File> future = SWQEFactory.getQueryInterface().getFeaturesByPlugin(0, arbitraryPlugin, fSet);
                    // check that Features are present match
                    File get = future.get();
                    BufferedReader in = new BufferedReader(new FileReader(get));
                    while (in.ready()) {
                        outputStream.write(in.readLine());
                        outputStream.newLine();
                    }
                } // TODO: clearly this should be expanded to include closing database etc 
            } else {
                for (Feature feature : fSet) {
                    StringBuffer buffer = new StringBuffer();
                    boolean caught = outputFeatureInVCF(buffer, feature);
                    if (caught) {
                        caughtNonVCF = true;
                    }
                    outputStream.append(buffer);
                    outputStream.newLine();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(VCFDumper.class.getName()).fatal("Exception thrown exporting to file:", e);
            System.exit(-1);
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
                SWQEFactory.getStorage().closeStorage();
            } catch (IOException ex) {
                Logger.getLogger(VCFDumper.class.getName()).fatal("Exception thrown flushing to file:", ex);
            }
        }
    }
}
