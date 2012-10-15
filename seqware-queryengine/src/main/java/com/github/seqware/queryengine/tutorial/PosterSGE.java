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

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.system.ReferenceCreator;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.util.SGID;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.hadoop.fs.ContentSummary;

/**
 * This is a quick and dirty application built on top of our API, created for
 * Poster testing.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class PosterSGE {

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
        PosterSGE dumper = new PosterSGE(args);
        dumper.benchmark();
    }

    /**
     * <p>Constructor for PosterSGE.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public PosterSGE(String[] args) {
        this.args = args;
    }

    /**
     * <p>benchmark.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void benchmark() throws IOException {
        if (args.length != 3) {
            System.err.println(args.length + " arguments found");
            System.out.println(PosterSGE.class.getSimpleName() + " <outputKeyValueFile> <input file dir> <simultaneous jobs>");
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
        // create new FeatureSet id and pass it onto our children
        CreateUpdateManager manager = SWQEFactory.getModelManager();
        FeatureSet initialFeatureSet = manager.buildFeatureSet().setReference(reference).build();
        manager.flush();
        
        int count = 0;
        // go through all input files
        File fileDirectory = new File(args[1]);
        File[] listFiles = fileDirectory.listFiles();

        // record start and finish time
        Date startDate = new Date();
        keyValues.put(count + "-start-date-long", Long.toString(startDate.getTime()));
        keyValues.put(count + "-start-date-human", startDate.toString());

        // submit all jobs in parallel via SGE
        StringBuilder jobNames = new StringBuilder();
        for (File inputFile : listFiles) {
            // run without unnecessary parameters
            String cargs = "-w VCFVariantImportWorker -i " + inputFile.getAbsolutePath() +
                        " -b " + String.valueOf(BENCHMARKING_BATCH_SIZE) +
                        " -f " +  initialFeatureSet.getSGID().getRowKey() +
                        " -r " +  reference.getSGID().getRowKey();
            String command = "java -Xmx2048m -classpath "+System.getProperty("user.dir")+"/seqware-queryengine-0.12.0-full.jar com.github.seqware.queryengine.system.importers.SOFeatureImporter";
            command = command + " " + cargs;
            command = "qsub -q long -l h_vmem=3G -cwd -N dyuen-"+inputFile.getName() + " -b y " + command;                     
            
            jobNames.append("dyuen-").append(inputFile.getName()).append(",");
            System.out.println("Running: " + command);
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
        }
        String jobs = jobNames.toString().substring(0, jobNames.length()-1);
        
        // submit a job that just waits on all the preceding jobs for synchronization
        String command = "java -Xmx1024m -version";  
        command = "qsub -cwd -N dyuen-wait -hold_jid " + jobs + " -b y -sync y " + command;
        System.out.println("Running wait: " + command);
        
        CommandLine cmdLine = CommandLine.parse(command);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValues(null);
        int exitValue = executor.execute(cmdLine);

        FeatureSet fSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(initialFeatureSet.getSGID(), FeatureSet.class);
        keyValues.put(count + "-featuresSet-id", fSet.getSGID().getRowKey());
        keyValues.put(count + "-featuresSet-id-timestamp", Long.toString(fSet.getSGID().getBackendTimestamp().getTime()));

        //        // runs count query, touches everything but does not write
//
//        keyValues.put(count + "-start-count-date-long", Long.toString(System.currentTimeMillis()));
//        long fsetcount = fSet.getCount();
//        keyValues.put(count + "-features-loaded", Long.toString(fsetcount));
//        keyValues.put(count + "-end-count-date-long", Long.toString(System.currentTimeMillis()));


        Date endDate = new Date();
        keyValues.put(count + "-end-date-long", Long.toString(endDate.getTime()));
        keyValues.put(count + "-end-date-human", endDate.toString());
        recordSpace(String.valueOf(count));
        Utility.writeKeyValueFile(outputFile, keyValues);
        count++;

    }

    private void recordSpace(String key) throws IOException {
//        try {
//            Configuration conf = new Configuration();
//            HBaseStorage.configureHBaseConfig(conf);
//            HBaseConfiguration.addHbaseResources(conf);
//            FileSystem fs = FileSystem.get(conf);
//            Path homeDirectory = fs.getHomeDirectory();
//            Path root = homeDirectory.getParent().getParent();
//            Path hbase = new Path(root, "hbase");
//            ContentSummary contentSummary = fs.getContentSummary(hbase);
//            long spaceConsumedinGB = convertToGB(contentSummary);
//            keyValues.put(key + "-total-space-in-GB", Long.toString(spaceConsumedinGB));
//
//            /**
//             *
//             * if (spaceConsumedinGB > CUT_OFF){ return; }
//             *
//             */
//            Path featureTable = new Path(hbase, Constants.NAMESPACE + ".hbaseTestTable_v2.Feature." + HG_19);
//            contentSummary = fs.getContentSummary(featureTable);
//            spaceConsumedinGB = convertToGB(contentSummary);
//            keyValues.put(key + "-feature-space-in-GB", Long.toString(spaceConsumedinGB));
//        } catch (FileNotFoundException e) {
//            /**
//             * throw away, this is ok the first time *
//             */
//        }

    }

    private long convertToGB(ContentSummary contentSummary) {
        // odd, it seems like length reports the equivalent of "hadoop fs -du -s
        long spaceConsumedinGB = contentSummary.getLength() / 1024 / 1024 / 1024;
        return spaceConsumedinGB;
    }
}
