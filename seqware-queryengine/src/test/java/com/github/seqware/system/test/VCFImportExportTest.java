package com.github.seqware.system.test;

import com.github.seqware.queryengine.system.exporters.VCFDumper;
import com.github.seqware.queryengine.system.importers.FeatureImporter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * System tests for importing and exporting VCF files.
 *
 * @author dyuen
 */
public class VCFImportExportTest {

    private static File testVCFFile = null;
    private static String randomRef= null; 
    
    @BeforeClass
    public static void setupTests() {
        String curDir = System.getProperty("user.dir");
        testVCFFile = new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/test.vcf");
        SecureRandom random = new SecureRandom();
        randomRef = "Random_ref_" + new BigInteger(20, random).toString(32);
    }
    
    @Test
    public void testGVFImport() throws IOException{
        File createTempFile = File.createTempFile("output", "txt");
        Assert.assertTrue("Cannot read VCF file for test", testVCFFile.exists() && testVCFFile.canRead());
        FeatureImporter.main(new String[]{"VCFVariantImportWorker", "1", "false", randomRef, testVCFFile.getAbsolutePath()});       
        // do some output comparisons, we may need to sort the results
        VCFDumper.main(new String[]{randomRef, createTempFile.getAbsolutePath()});
        BufferedReader in = new BufferedReader(new FileReader(createTempFile));
        List<String> output = new ArrayList<String>();
        while (in.ready()){
            String inLine = in.readLine();
            output.add(inLine);
        }
        Collections.sort(output);
        // compare against original VCF file
        
        BufferedReader controlIn = new BufferedReader(new FileReader(testVCFFile));
        List<String> control = new ArrayList<String>();
        while (controlIn.ready()){
            control.add(controlIn.readLine());
        }
        Collections.sort(control);
        for(int i = 0; i < output.size(); i++){
            String[] cLine = control.get(i).split("\t");
            String[] eLine = output.get(i).split("\t");
            Assert.assertTrue("VCF position does not match" , cLine[1].equals(eLine[1]));
            Assert.assertTrue("VCF ID does not match" , cLine[2].equals(eLine[2]));
            Assert.assertTrue("VCF REF does not match" , cLine[3].equals(eLine[3]));
            Assert.assertTrue("VCF ALT does not match" , cLine[4].equals(eLine[4]));
            Assert.assertTrue("VCF FILTER does not match" , cLine[6].equals(eLine[6]));
            Assert.assertTrue("VCF INFO does not match" , cLine[7].equals(eLine[7]));
        }
    }
}
