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

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.dto.QESupporting.FSGIDPB;
import com.github.seqware.queryengine.dto.QESupporting.FSGIDPB.Builder;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.impl.protobufIO.FSGIDIO;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryFuture;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.system.ReferenceCreator;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.system.importers.SOFeatureImporter;
import com.github.seqware.queryengine.system.importers.workers.VCFVariantImportWorker;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.SGID;
import com.google.protobuf.TextFormat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;

/**
 * This is a quick and dirty sample application built on top of our API, created for
 * Poster testing. It demonstrates, times, and verifies by count several queries.
 * It also triggers a hack for secondary indexes.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class Poster {

    /** Constant <code>HG_19="hg_19"</code> */
    public static final String HG_19 = "hg_19";
//    public static final int CUT_OFF = 1000;
    /** Constant <code>BENCHMARKING_BATCH_SIZE=20000</code> */
    public static final int BENCHMARKING_BATCH_SIZE = 20000;
    private String[] args;
    Map<String, String> keyValues = new HashMap<String, String>();

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.io.IOException if any.
     */
    public static void main(String[] args) throws IOException {
        Poster dumper = new Poster(args);
        dumper.benchmark();
    }

    /**
     * <p>Constructor for Poster.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public Poster(String[] args) {
        this.args = args;
    }

    /**
     * <p>benchmark.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void benchmark() throws IOException {
        if (args.length != 2) {
            System.err.println(args.length + " arguments found");
            System.out.println(Poster.class.getSimpleName() + " <outputKeyValueFile> <input file dir>");
            System.exit(-1);
        }

        File outputFile = Utility.checkOutput(args[0]);

        // check if reference has been properly created
        Reference reference = SWQEFactory.getQueryInterface().getLatestAtomByRowKey("hg_19", Reference.class);
        if (reference == null) {
            SGID refID = ReferenceCreator.mainMethod(new String[]{HG_19});
            reference = SWQEFactory.getQueryInterface().getAtomBySGID(Reference.class, refID);
        }


        // record reference, starting disk space
        keyValues.put("referenceID", reference.getSGID().getRowKey());
        recordSpace("start");
        Utility.writeKeyValueFile(outputFile, keyValues);

        int count = 0;
        // go through all input files
        File fileDirectory = new File(args[1]);
        File[] listFiles = fileDirectory.listFiles();
        SGID fSet_sgid = null;
        for (File inputFile : listFiles) {
            // record start and finish time
            Date startDate = new Date();
            keyValues.put(count + "-start-date-long", Long.toString(startDate.getTime()));
            keyValues.put(count + "-start-date-human", startDate.toString());

            // run without unnecessary parameters
            if (fSet_sgid == null) {
                fSet_sgid =
                        SOFeatureImporter.runMain(new String[]{"-w", "VCFVariantImportWorker",
                            "-i", inputFile.getAbsolutePath(),
                            "-b", String.valueOf(BENCHMARKING_BATCH_SIZE),
                            // secondary index hack
                            "-x", "RV",
                            "-r", reference.getSGID().getRowKey()});
            } else {
                fSet_sgid =
                        SOFeatureImporter.runMain(new String[]{"-w", "VCFVariantImportWorker",
                            "-i", inputFile.getAbsolutePath(),
                            "-b", String.valueOf(BENCHMARKING_BATCH_SIZE),
                            "-x", "RV",
                            "-r", reference.getSGID().getRowKey(),
                            "-f", fSet_sgid.getRowKey()
                        });
            }

            FeatureSet fSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(fSet_sgid, FeatureSet.class);
            keyValues.put(count + "-featuresSet-id", fSet.getSGID().getRowKey());
            keyValues.put(count + "-featuresSet-id-timestamp", Long.toString(fSet.getSGID().getBackendTimestamp().getTime()));
            // runs count query, touches everything but does not write

            keyValues.put(count + "-start-count-date-long", Long.toString(System.currentTimeMillis()));
            long fsetcount = fSet.getCount();
            keyValues.put(count + "-features-loaded", Long.toString(fsetcount));
            keyValues.put(count + "-end-count-date-long", Long.toString(System.currentTimeMillis()));

            // test time for exporting via M/R VCF Dumper
//            keyValues.put(count + "-start-dump_query-date-long" , Long.toString(System.currentTimeMillis()));
//            File createTempFile = File.createTempFile("output", "txt");
//            // do some output comparisons, we may need to sort the results
//            VCFDumper.main(new String[]{fSet_sgid.getUuid().toString(), createTempFile.getAbsolutePath()});
//            keyValues.put(count + "-end-dump_query-count-date-long" , Long.toString(System.currentTimeMillis()));
            
            /**
             * PMCSI code - secondary index
             */
            keyValues.put(count + "-start-PMCSI_query-date-long", Long.toString(System.currentTimeMillis()));
            // go through the secondary index and try to pull back each FSGID
            fsetcount = 0;
            BufferedReader in = new BufferedReader(new FileReader(VCFVariantImportWorker.SECONDARY_INDEX));
            while(in.ready()){
                Builder newBuilder = FSGIDPB.newBuilder();
                StringBuffer buff = new StringBuffer();
                for(int i = 0; i < 14 ; i++){
                    buff.append(in.readLine());
                }
                String separator = in.readLine();
                assert(separator.equals("!"));
                TextFormat.merge(buff, newBuilder);
                FSGID fsgid = FSGIDIO.pb2m(newBuilder.build());
                Feature latestAtomBySGID = SWQEFactory.getQueryInterface().getLatestAtomBySGID(fsgid, Feature.class);
                if (latestAtomBySGID == null){
                    assert(false);
                }
                fsetcount++;
            }
            keyValues.put(count + "-end-PMCSI_query-date-long", Long.toString(System.currentTimeMillis()));
            keyValues.put(count + "-features-PMCSI_query-written", Long.toString(fsetcount));
            keyValues.put(count + "-end-PMCSI_query-count-date-long", Long.toString(System.currentTimeMillis()));
            /**
             * PMCSI code ends
             */  

            /**
             * PMC code follows
             */
//            keyValues.put(count + "-start-PMC_query-date-long", Long.toString(System.currentTimeMillis()));
//            // run a query that looks for tag called "PMC", should touch a small fraction of Features
//            QueryFuture<FeatureSet> queryFuture = SWQEFactory.getQueryInterface().getFeaturesByAttributes(1, fSet, new RPNStack(
//                    new RPNStack.TagOccurrence("RV")));
//            FeatureSet resultSet = queryFuture.get();
//            keyValues.put(count + "-end-PMC_query-date-long", Long.toString(System.currentTimeMillis()));
//            fsetcount = (int) resultSet.getCount();
//            keyValues.put(count + "-features-PMC_query-written", Long.toString(fsetcount));
//            keyValues.put(count + "-end-PMC_query-count-date-long", Long.toString(System.currentTimeMillis()));
            /**
             * PMC code ends
             */
            
            
//            /**
//             * QUAL code follows
//             */
//            keyValues.put(count + "-start-QUAL_query-date-long", Long.toString(System.currentTimeMillis()));
//            // run a query that looks for the QUAL field, should touch a small fraction of Features
//            QueryFuture<FeatureSet> queryFuture = SWQEFactory.getQueryInterface().getFeaturesByAttributes(1, fSet, new RPNStack(
//                    new RPNStack.FeatureAttribute("score"),
//                    new RPNStack.Constant(100.30d),
//                    RPNStack.Operation.EQUAL));
//            FeatureSet resultSet = queryFuture.get();
//            keyValues.put(count + "-end-QUAL_query-date-long", Long.toString(System.currentTimeMillis()));
//            fsetcount = (int) resultSet.getCount();
//            keyValues.put(count + "-features-QUAL_query-written", Long.toString(fsetcount));
//            keyValues.put(count + "-end-QUAL_query-count-date-long", Long.toString(System.currentTimeMillis()));
//            /**
//             * QUAL code ends
//             */
//            /**
//             * copy-all query code begins
//             */
//            keyValues.put(count + "-start-ALL_query-date-long", Long.toString(System.currentTimeMillis()));
//            // run a query that looks for tag called "PMC", should touch a small fraction of Features
//            queryFuture = SWQEFactory.getQueryInterface().getFeatures(1, fSet);
//            resultSet = queryFuture.get();
//            keyValues.put(count + "-end-ALL_query-date-long", Long.toString(System.currentTimeMillis()));
//            fsetcount = (int) resultSet.getCount();
//            keyValues.put(count + "-features-ALL_query-written", Long.toString(fsetcount));
//            keyValues.put(count + "-end-ALL_query-count-date-long", Long.toString(System.currentTimeMillis()));
//            /**
//             * copy-all query code ends
//             */
            Date endDate = new Date();
            keyValues.put(count + "-end-date-long", Long.toString(endDate.getTime()));
            keyValues.put(count + "-end-date-human", endDate.toString());
            recordSpace(String.valueOf(count));
            Utility.writeKeyValueFile(outputFile, keyValues);
            count++;
        }
    }

    private void recordSpace(String key) throws IOException {
        try {
            Configuration conf = new Configuration();
            HBaseStorage.configureHBaseConfig(conf);
            HBaseConfiguration.addHbaseResources(conf);
            FileSystem fs = FileSystem.get(conf);
            Path homeDirectory = fs.getHomeDirectory();
            Path root = homeDirectory.getParent().getParent();
            Path hbase = new Path(root, "hbase");
            ContentSummary contentSummary = fs.getContentSummary(hbase);
            long spaceConsumedinGB = convertToGB(contentSummary);
            keyValues.put(key + "-total-space-in-GB", Long.toString(spaceConsumedinGB));

            /**
             *
             * if (spaceConsumedinGB > CUT_OFF){ return; }
             *
             */
            Path featureTable = new Path(hbase, Constants.Term.NAMESPACE.getTermValue(String.class) + ".hbaseTestTable_v2.Feature." + HG_19);
            contentSummary = fs.getContentSummary(featureTable);
            spaceConsumedinGB = convertToGB(contentSummary);
            keyValues.put(key + "-feature-space-in-GB", Long.toString(spaceConsumedinGB));
        } catch (FileNotFoundException e) {
            /**
             * throw away, this is ok the first time *
             */
        }

    }

    private long convertToGB(ContentSummary contentSummary) {
        // odd, it seems like length reports the equivalent of "hadoop fs -du -s
        long spaceConsumedinGB = contentSummary.getLength() / 1024 / 1024 / 1024;
        return spaceConsumedinGB;
    }
}
