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
package com.github.seqware.queryengine.tutorial;

import com.github.seqware.queryengine.dto.QESupporting;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.impl.protobufIO.FSGIDIO;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.system.importers.workers.VCFVariantImportWorker;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.SGID;
import com.google.protobuf.TextFormat;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;

/**
 * This is a quick and sample application built on top of our API, created for
 * Poster testing. Various sections were commented and uncommented to compile various pieces of data for
 * our Genome Informatics poster.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class GITest {

    private String[] args;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        GITest dumper = new GITest(args);
        try {
            dumper.export();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GITest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GITest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * <p>export.</p>
     *
     * @throws java.io.FileNotFoundException if any.
     * @throws java.io.IOException if any.
     */
    public void export() throws FileNotFoundException, IOException {
        
         Configuration config = HBaseConfiguration.create();
         HBaseStorage.configureHBaseConfig(config);
         HBaseAdmin hba = new HBaseAdmin(config);
         boolean ran = hba.balancer();
         System.out.append("balancer status : " + ran);
        

//        if (args.length < 1 || args.length > 2) {
//            System.err.println(args.length + " arguments found");
//            System.out.println(GITest.class.getSimpleName() + " <featureSetID> [outputFile]");
//            System.exit(-1);
//        }

//        // parse a SGID from a String representation, we need a more elegant solution here
//        SGID sgid = Utility.parseSGID("57389392-bcbc-4127-8bfc-35afb14f8196");
//        FeatureSet fSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(sgid, FeatureSet.class);
//        System.out.println("Features: " + fSet.getCount());

//        // if this featureSet does not exist
//        if (fSet == null) {
//            System.out.println("referenceID not found");
//            System.exit(-2);
//        }
//
//        int fsetcount = 0;
//        BufferedReader in = new BufferedReader(new FileReader(VCFVariantImportWorker.SECONDARY_INDEX));
//        while (in.ready()) {
//            QESupporting.FSGIDPB.Builder newBuilder = QESupporting.FSGIDPB.newBuilder();
//            StringBuffer buff = new StringBuffer();
//            for (int i = 0; i < 14; i++) {
//                buff.append(in.readLine());
//            }
//            String separator = in.readLine();
//            assert (separator.equals("!"));
//            TextFormat.merge(buff, newBuilder);
//            FSGID fsgid = FSGIDIO.pb2m(newBuilder.build());
//            Feature latestAtomBySGID = SWQEFactory.getQueryInterface().getLatestAtomBySGID(fsgid, Feature.class);
//            if (latestAtomBySGID == null) {
//                assert (false);
//            }
//            fsetcount++;
//        }

        // get a FeatureSet from the back-end
//        QueryFuture<FeatureSet> future = SWQEFactory.getQueryInterface().getFeaturesByAttributes(1, fSet, new RPNStack(
//                new RPNStack.TagOccurrence("PMC")
//                ));
//
//        BufferedWriter outputStream = null;
//        try {
//
//            if (args.length == 2) {
//                outputStream = new BufferedWriter(new FileWriter(args[1]));
//            } else {
//                outputStream = new BufferedWriter(new OutputStreamWriter(System.out));
//            }
//            outputStream.append("#CHROM	POS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\n");
//            //SeqWareIterable<FeatureSet> featureSets = SWQEFactory.getQueryInterface().getFeatureSets();
//            boolean caughtNonVCF = false;
//            for (Feature feature : future.get()) {
//                outputStream.append(feature.getSeqid() + "\t" + (feature.getStart() + 1) + "\t");
//                if (feature.getTagByKey(ImportConstants.VCF_SECOND_ID) == null) {
//                    outputStream.append(".\t");
//                } else {
//                    outputStream.append(feature.getTagByKey(ImportConstants.VCF_SECOND_ID).getValue().toString() + "\t");
//                }
//                try {
//                    outputStream.append(feature.getTagByKey(ImportConstants.VCF_REFERENCE_BASE).getValue().toString() + "\t");
//                    outputStream.append(feature.getTagByKey(ImportConstants.VCF_CALLED_BASE).getValue().toString() + "\t");
//                    outputStream.append(feature.getScore() + "\t");
//                    outputStream.append(feature.getTagByKey(ImportConstants.VCF_FILTER).getValue().toString() + "\t");
//                    outputStream.append(feature.getTagByKey(ImportConstants.VCF_INFO).getValue().toString());
//                } catch (NullPointerException npe) {
//                    if (!caughtNonVCF) {
//                        Logger.getLogger(GITest.class.getName()).log(Level.INFO, "VCF exporting non-VCF feature");
//
//                    }
//                    // this may occur when exporting Features that were not originally VCF files
//                    caughtNonVCF = true;
//                }
//                outputStream.newLine();
//            }
//        } // TODO: clearly this should be expanded to include closing database etc 
//        catch (Exception e) {
//            Logger.getLogger(GITest.class.getName()).log(Level.SEVERE, "Exception thrown exporting to file:", e);
//            System.exit(-1);
//        } finally {
//            try {
//                outputStream.flush();
//                outputStream.close();
//                SWQEFactory.getStorage().closeStorage();
//            } catch (IOException ex) {
//                Logger.getLogger(GITest.class.getName()).log(Level.SEVERE, "Exception thrown flushing to file:", ex);
//            }
//        }
    }

    /**
     * <p>Constructor for GITest.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public GITest(String[] args) {
        this.args = args;
    }
}
