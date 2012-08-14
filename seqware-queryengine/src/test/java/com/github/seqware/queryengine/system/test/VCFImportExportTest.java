package com.github.seqware.queryengine.system.test;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.FeatureSet;
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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static File testVCFFile_missingValues = null;
    private static File testVCFFile_invalid = null;
    private static String randomRef= null; 
    
    @BeforeClass
    public static void setupTests() {
        String curDir = System.getProperty("user.dir");
        testVCFFile = new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/test.vcf");
        testVCFFile_missingValues = new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/test_missingValues.vcf");
        testVCFFile_invalid = new File(curDir + "/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/test_invalid.vcf");
        SecureRandom random = new SecureRandom();
        randomRef = "Random_ref_" + new BigInteger(20, random).toString(32);
    }
    
    @Test
    public void testVCFImportParam() throws IOException{
        File createTempFile = File.createTempFile("output", "txt");
        Assert.assertTrue("Cannot read VCF file for test", testVCFFile.exists() && testVCFFile.canRead());
        FeatureImporter.naiveRun(new String[]{"VCFVariantImportWorker", "1", "false", randomRef, testVCFFile.getAbsolutePath(), createTempFile.getAbsolutePath()});        
        // check output file created
        BufferedReader in = new BufferedReader(new FileReader(createTempFile));
        String[] output = in.readLine().split("\t");
        boolean twoItems = output.length == 2;
        boolean idStart = output[0].contains("ID");
        Assert.assertTrue("output file not populated with correct output", twoItems && idStart);
        createTempFile.delete();
        // run again without input
        SGID main = FeatureImporter.naiveRun(new String[]{"VCFVariantImportWorker", "1", "false", randomRef, testVCFFile.getAbsolutePath()});        
        FeatureSet fSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(main, FeatureSet.class);
        long count = fSet.getCount();
        Assert.assertTrue("incorrect number of features in imported VCF, found " + count + " expected 93", count == 93);
    }
    
    @Test
    public void testNormalVCFImport(){
        testFile(testVCFFile, true);
    }
    
    @Test
    public void testMissingValueVCFImport(){
        // should handle this normally, all columns but POS look like they could use the missing value (".") according to the VCF specification
        testFile(testVCFFile_missingValues, true);
    }
    
    @Test
    public void testInvalidVCFImport(){
        // should bug out
        testFile(testVCFFile_invalid, false);
    }

    private void testFile(File testFile, boolean shouldSucceed) {
        BufferedReader in = null;
        BufferedReader controlIn = null;
        try {
            File createTempFile = File.createTempFile("output", "txt");
            Assert.assertTrue("Cannot read VCF file for test", testFile.exists() && testFile.canRead());
            SGID main = FeatureImporter.naiveRun(new String[]{"VCFVariantImportWorker", "1", "false", randomRef, testFile.getAbsolutePath()});
            if (!shouldSucceed){
                // would be an error code on the command-line
                Assert.assertTrue(main == null);
                return;
            }
            
            // do some output comparisons, we may need to sort the results
            VCFDumper.main(new String[]{main.getUuid().toString(), createTempFile.getAbsolutePath()});
            in = new BufferedReader(new FileReader(createTempFile));
            List<String> output = new ArrayList<String>();
            while (in.ready()){
                String inLine = in.readLine();
                output.add(inLine);
            }
            Collections.sort(output);
            // compare against original VCF file
            controlIn = new BufferedReader(new FileReader(testFile));
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
        } catch (IOException ex) {
            Logger.getLogger(VCFImportExportTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.assertTrue("IO Exception", false);
        }  finally {
            try {
                if (in != null) {in.close();}
                if (controlIn != null) {controlIn.close();}
            } catch (IOException ex) {
                /** we don't really care if the tests file closing fails*/
                return;
            }
        }
    }
}
