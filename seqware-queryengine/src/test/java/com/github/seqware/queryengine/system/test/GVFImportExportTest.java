package com.github.seqware.queryengine.system.test;

import com.github.seqware.queryengine.system.exporters.VCFDumper;
import com.github.seqware.queryengine.system.importers.FeatureImporter;
import com.github.seqware.queryengine.util.SGID;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test demonstrates how one would import a GVF file and then export to a VCF file.
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class GVFImportExportTest {

    private static File testGVFFile = null;
    private static String randomRef= null; 
    
    /**
     * <p>setupTests.</p>
     */
    @BeforeClass
    public static void setupTests() {
        String curDir = System.getProperty("user.dir");
        testGVFFile = new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/test.gvf");
        SecureRandom random = new SecureRandom();
        randomRef = "Random_ref_" + new BigInteger(20, random).toString(32);
        Logger.getLogger(GVFImportExportTest.class.getName()).info("Using " + randomRef + " in " + GVFImportExportTest.class.getName());
    }
    
    /**
     * <p>testGVFImport.</p>
     *
     * @throws java.io.IOException if any.
     */
    @Test
    public void testGVFImport() throws IOException{
        File createTempFile = File.createTempFile("output", "txt");
        Assert.assertTrue("Cannot read GVF file for test", testGVFFile.exists() && testGVFFile.canRead());
        SGID main = FeatureImporter.naiveRun(new String[]{"GFF3VariantImportWorker", "1", "false", randomRef, testGVFFile.getAbsolutePath()});
        // do some output comparisons, we may need to sort the results
        VCFDumper.main(new String[]{main.getUuid().toString(), createTempFile.getAbsolutePath()});
        BufferedReader in = new BufferedReader(new FileReader(createTempFile));
        List<String> output = new ArrayList<String>();
        while (in.ready()){
            output.add(in.readLine());
        }
        Collections.sort(output);
        // can compare only #CHROM and POS for now
        
        Assert.assertTrue(output.size() == 10);
        String[] arr = output.get(1).split("\t");
        Assert.assertTrue(output.get(1).split("\t")[0].equals("chr16") && output.get(1).split("\t")[1].equals("49291141"));
        Assert.assertTrue(output.get(2).split("\t")[0].equals("chr16") && output.get(2).split("\t")[1].equals("49291360"));
        Assert.assertTrue(output.get(5).split("\t")[0].equals("chr16") && output.get(5).split("\t")[1].equals("49302700"));
        Assert.assertTrue(output.get(8).split("\t")[0].equals("chr16") && output.get(8).split("\t")[1].equals("49303427"));
        Assert.assertTrue(output.get(9).split("\t")[0].equals("chr16") && output.get(9).split("\t")[1].equals("49303596"));
        
    }
}
