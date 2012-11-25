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
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.plugins.plugins.VCFDumperPlugin;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.system.importers.workers.ImportConstants;
import com.github.seqware.queryengine.system.importers.workers.VCFVariantImportWorker;
import com.github.seqware.queryengine.util.SGID;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * This will dump VCF files given a FeatureSet that was originally imported from
 * a VCF file.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class VCFDumper {
    /** Constant <code>VCF="VCFVariantImportWorker.VCF"</code> */
    public static final String VCF = VCFVariantImportWorker.VCF;

    private String[] args;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        VCFDumper dumper = new VCFDumper(args);
        dumper.export();
    }

    /**
     * <p>export.</p>
     */
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

    /**
     * <p>Constructor for VCFDumper.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public VCFDumper(String[] args) {
        this.args = args;
    }

    /**
     * <p>outputFeatureInVCF.</p>
     *
     * @param buffer a {@link java.lang.StringBuffer} object.
     * @param feature a {@link com.github.seqware.queryengine.model.Feature} object.
     * @return a boolean.
     */
    public static boolean outputFeatureInVCF(StringBuffer buffer, Feature feature) {
        boolean caughtNonVCF = false;
        buffer.append(feature.getSeqid()).append("\t").append(feature.getStart() + 1).append("\t");
        if (feature.getTagByKey(VCF, ImportConstants.VCF_SECOND_ID) == null) {
            buffer.append(".\t");
        } else {
            buffer.append(feature.getTagByKey(VCF,ImportConstants.VCF_SECOND_ID).getValue().toString()).append("\t");
        }
        try {
            buffer.append(feature.getTagByKey(VCF,ImportConstants.VCF_REFERENCE_BASE).getValue().toString()).append("\t");
            buffer.append(feature.getTagByKey(VCF,ImportConstants.VCF_CALLED_BASE).getValue().toString()).append("\t");
            buffer.append(feature.getScore() == null ? "." : feature.getScore()).append("\t");
            buffer.append(feature.getTagByKey(VCF,ImportConstants.VCF_FILTER).getValue().toString()).append("\t");
            buffer.append(feature.getTagByKey(VCF,ImportConstants.VCF_INFO).getValue().toString());
        } catch (NullPointerException npe) {
            if (!caughtNonVCF) {
                Logger.getLogger(VCFDumper.class.getName()).info("VCF exporting non-VCF feature");

            }
            // this may occur when exporting Features that were not originally VCF files
            caughtNonVCF = true;
        }
        return caughtNonVCF;
    }

    /**
     * <p>dumpVCFFromFeatureSetID.</p>
     *
     * @param fSet a {@link com.github.seqware.queryengine.model.FeatureSet} object.
     * @param file a {@link java.lang.String} object.
     */
    public static void dumpVCFFromFeatureSetID(FeatureSet fSet, String file) {
        BufferedWriter outputStream = null;

        try {
            if (file != null) {
                outputStream = new BufferedWriter(new FileWriter(file));
            } else {
                outputStream = new BufferedWriter(new OutputStreamWriter(System.out));
            }
            outputStream.append("#CHROM	POS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\n");
        } catch (IOException e) {
            Logger.getLogger(VCFDumper.class.getName()).fatal("Exception thrown starting export to file:", e);
            System.exit(-1);
        }

        boolean caughtNonVCF = false;
        boolean mrSuccess = false;
        if (SWQEFactory.getQueryInterface() instanceof MRHBasePersistentBackEnd) {
            // hack to use VCF MR
            if (SWQEFactory.getModelManager() instanceof MRHBaseModelManager) {
                try {
                    // pretend that the included com.github.seqware.queryengine.plugins.hbasemr.MRFeaturesByAttributesPlugin is an external plug-in
                    Class<? extends PluginInterface> arbitraryPlugin;
                    arbitraryPlugin = VCFDumperPlugin.class;
                    // get a FeatureSet from the back-end
                    QueryFuture<File> future = SWQEFactory.getQueryInterface().getFeaturesByPlugin(0, arbitraryPlugin, fSet);
                    File get = future.get();
                    BufferedReader in = new BufferedReader(new FileReader(get));
                    IOUtils.copy(in, outputStream);
                    in.close();
                    get.deleteOnExit();
                    outputStream.flush();
                    outputStream.close();
                    mrSuccess = true;
                } catch (IOException e) {
                    // fail out on IO error
                    Logger.getLogger(VCFDumper.class.getName()).fatal("Exception thrown exporting to file:", e);
                    System.exit(-1);
                } catch(Exception e){
                    Logger.getLogger(VCFDumper.class.getName()).info("MapReduce exporting failed, falling-through to normal exporting to file");
                    // fall-through and do normal exporting if Map Reduce exporting fails
                }
            } // TODO: clearly this should be expanded to include closing database etc 
        }
        if (mrSuccess) {
            return;
        }
        // fall-through if plugin-fails
        try {
            for (Feature feature : fSet) {
                StringBuffer buffer = new StringBuffer();
                boolean caught = outputFeatureInVCF(buffer, feature);
                if (caught) {
                    caughtNonVCF = true;
                }
                outputStream.append(buffer);
                outputStream.newLine();
            }
            outputStream.flush();
        } catch (IOException e) {
            Logger.getLogger(VCFDumper.class.getName()).fatal("Exception thrown exporting to file:", e);
            System.exit(-1);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }
}
