/*
 *  Copyright (C) 2011 SeqWare
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sourceforge.seqware.common.util.maptools;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.xml.bind.DatatypeConverter;
import net.sourceforge.seqware.common.util.Log;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>
 * MD5GeneratorTest class.
 * </p>
 * 
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.6.2
 */
public class MapToolsTest {

    /**
     * <p>
     * setUpClass.
     * </p>
     * 
     * @throws java.lang.Exception
     *             if any.
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    /**
     * <p>
     * tearDownClass.
     * </p>
     * 
     * @throws java.lang.Exception
     *             if any.
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * <p>
     * setUp.
     * </p>
     */
    @Before
    public void setUp() {
    }

    /**
     * <p>
     * tearDown.
     * </p>
     */
    @After
    public void tearDown() {
    }

    @Test
    public void testExpandVariables() throws Exception {
        String path = getClass().getResource("vars.ini").getPath();
        Map<String, String> raw = new HashMap<>();
        MapTools.ini2Map(path, raw);
        Map<String, String> provided = MapTools.providedMap("/u/seqware/provisioned-bundles");
        Map<String, String> exp = MapTools.expandVariables(raw, provided);

        assertEquals(raw.size(), exp.size());

        assertEquals("b", exp.get("foo"));
        assertEquals("d", exp.get("bar"));
        assertEquals("abcde", exp.get("test-multi"));

        assertEquals("/u/seqware/provisioned-bundles", exp.get("test-bundle-dir"));
        assertEquals("/u/seqware/provisioned-bundles", exp.get("test-legacy-bundle-dir"));

        Integer.parseInt(exp.get("test-random"));
        Integer.parseInt(exp.get("test-legacy-random"));

        DatatypeConverter.parseDate(exp.get("test-date"));
        DatatypeConverter.parseDate(exp.get("test-legacy-date"));

        DatatypeConverter.parseDateTime(exp.get("test-datetime"));

        Long.parseLong(exp.get("test-timestamp"));

        UUID.fromString(exp.get("test-uuid"));
    }

    @Test
    public void testNormalRichIni() throws Exception {
        String path = getClass().getResource("normal.ini").getPath();
        Map<String, Map<String, String>> hm = new HashMap<>();
        MapTools.ini2RichMap(path, hm);
        Assert.assertTrue("normal ini2RichMap failed", hm.size() == 3);
        testNormalValues(hm);
    }

    /**
     * Test is for SEQWARE-1434
     * 
     * @throws Exception
     */
    @Test
    public void testRichIni_withBlanks() throws Exception {
        String path = getClass().getResource("normal_withBlanks.ini").getPath();
        Map<String, Map<String, String>> hm = new HashMap<>();
        MapTools.ini2RichMap(path, hm);
        Assert.assertTrue("ini2RichMap with blanks failed", hm.size() == 7);
        testNormalValues(hm);

        Assert.assertTrue("blank defaults for ini2RichMap failed",
                hm.get("boogydown").size() == 2 && hm.get("boogydown").get("default_value").isEmpty());
        Assert.assertTrue("blank defaults for ini2RichMap failed", hm.get("boogydown").size() == 2
                && hm.get("boogydown").get("key").equals("boogydown"));
        Assert.assertTrue("blank defaults for ini2RichMap failed",
                hm.get("funkyparameter_with_extraSpace").size() == 2
                        && hm.get("funkyparameter_with_extraSpace").get("default_value").isEmpty());
        Assert.assertTrue(
                "blank defaults for ini2RichMap failed",
                hm.get("funkyparameter_with_extraSpace").size() == 2
                        && hm.get("funkyparameter_with_extraSpace").get("key").equals("funkyparameter_with_extraSpace"));
        Assert.assertTrue("blank defaults for ini2RichMap failed",
                hm.get("funkyparameter").size() == 2 && hm.get("funkyparameter").get("default_value").isEmpty());
        Assert.assertTrue("blank defaults for ini2RichMap failed",
                hm.get("funkyparameter").size() == 2 && hm.get("funkyparameter").get("key").equals("funkyparameter"));

        Assert.assertTrue("blank annotated defaults for ini2RichMap failed",
                hm.get("funky_annotated").size() == 5 && hm.get("funky_annotated").get("default_value").isEmpty());
        Assert.assertTrue("blank annotated defaults for ini2RichMap failed",
                hm.get("funky_annotated").size() == 5 && hm.get("funky_annotated").get("key").equals("funky_annotated"));
        Assert.assertTrue("blank annotated defaults for ini2RichMap failed",
                hm.get("funky_annotated").size() == 5 && hm.get("funky_annotated").get("file_meta_type").equals("text/plain"));
        Assert.assertTrue("blank annotated defaults for ini2RichMap failed",
                hm.get("funky_annotated").size() == 5 && hm.get("funky_annotated").get("type").equals("file"));
        Assert.assertTrue("blank annotated defaults for ini2RichMap failed",
                hm.get("funky_annotated").size() == 5 && hm.get("funky_annotated").get("display").equals("F"));
    }

    @Test
    public void testIniString2MapWithNovoalign() {
        String path = getClass().getResource("novoalign.ini").getPath();
        Map<String, Map<String, String>> hm = new HashMap<>();
        MapTools.ini2RichMap(path, hm);
        Assert.assertTrue("ini2RichMap with novoalign failed", hm.size() == 34);
        StringBuffer mapBuffer = createStringFromMap(hm);

        Map<String, String> iniString2Map = MapTools.iniString2Map(mapBuffer.toString());
        // a map re-created from a String "loaded into the DB" should be the same size as before
        Assert.assertTrue("iniString2Map with novoalign failed", iniString2Map.size() == 34);
    }

    @Test
    public void testIniString2MapWithBlanks() {
        String path = getClass().getResource("normal_withBlanks.ini").getPath();
        Map<String, Map<String, String>> hm = new HashMap<>();
        MapTools.ini2RichMap(path, hm);
        Assert.assertTrue("ini2RichMap with blanks failed", hm.size() == 7);
        testNormalValues(hm);
        StringBuffer mapBuffer = createStringFromMap(hm);

        Map<String, String> iniString2Map = MapTools.iniString2Map(mapBuffer.toString());
        // a map re-created from a String "loaded into the DB" should be the same size as before
        Assert.assertTrue("iniString2Map with blanks failed", iniString2Map.size() == 7);
    }

    /**
     * Test is for SEQWARE-1444
     * 
     * @throws Exception
     */
    @Test
    public void testRichIni_valuesWithSpaces() throws Exception {
        String path = getClass().getResource("workflow_fromPDE_1444.ini").getPath();
        Map<String, Map<String, String>> hm = new HashMap<>();
        MapTools.ini2RichMap(path, hm);
        Assert.assertTrue("ini2RichMap with spaced values failed", hm.size() == 5);

        Assert.assertTrue(
                "white spaced value for ini2RichMap failed",
                hm.get("annotate_params").size() == 2
                        && hm.get("annotate_params")
                                .get("default_value")
                                .equals("--dbsnp ${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}/data/resources/dbSNP135_chr.vcf -resource:snp ${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}/data/resources/dbSNP135_chr.vcf -E snp.dbSNPBuildID -E snp.G5 -E snp.G5A -E snp.GMAF -comp:1KG_CEU ${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}/data/resources/1000g_20100804_chr.vcf -comp:HapMap ${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}/data/resources/HapMap_r27_nr_hg19_chr.vcf -comp:Nmblhg19 ${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}/data/resources/nimblegen_hg19_2.1M_Human_Exome.gatk.vcf.gz -comp:AgilentICGChg19 ${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}/data/resources/agilent_icgc_sanger.exons.hg19.sorted.fixed.gatk.vcf.gz -comp:MOUSE0001 ${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}/data/resources/MOUSE0001.bam.realigned.recal.bam.snps.raw.filtered.vcf.gz -comp:MOUSE0002 ${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}/data/resources/MOUSE0002.bam.realigned.recal.bam.snps.raw.filtered.vcf.gz"));
        Assert.assertTrue("white spaced value defaults for ini2RichMap failed",
                hm.get("annotate_params").size() == 2 && hm.get("annotate_params").get("key").equals("annotate_params"));
    }

    /**
     * Test is for SEQWARE-1444
     * 
     * @throws Exception
     */
    @Test
    public void testRichIni_valuesWithSpaces2() throws Exception {
        String path = getClass().getResource("workflow_fromPDE_1444_1.ini").getPath();
        Map<String, Map<String, String>> hm = new HashMap<>();
        MapTools.ini2RichMap(path, hm);
        Assert.assertTrue("ini2RichMap with spaced values2 failed", hm.size() == 4);

        Assert.assertTrue(
                "white spaced value2 for ini2RichMap failed",
                hm.get("novoalign_index").size() == 2
                        && hm.get("novoalign_index")
                                .get("default_value")
                                .equals("-d ${workflow_bundle_dir}/bundle_GenomicAlignmentNovoalign/0.9.2/data/indexes/novoalign/hg19/hg19_random/hg19_random.nix"));
        Assert.assertTrue("white spaced value2 defaults for ini2RichMap failed",
                hm.get("novoalign_index").size() == 2 && hm.get("novoalign_index").get("key").equals("novoalign_index"));
    }

    /**
     * Test is for SEQWARE-1444
     * 
     * @throws Exception
     */
    @Test
    public void testRichIni_novoalign() throws Exception {
        String path = getClass().getResource("novoalign.ini").getPath();
        Map<String, Map<String, String>> hm = new HashMap<>();
        MapTools.ini2RichMap(path, hm);
        Assert.assertTrue("ini2RichMap with spaced values2 failed", hm.size() == 34);
    }

    private void testNormalValues(Map<String, Map<String, String>> hm) {
        Assert.assertTrue(
                "normal defaults for ini2RichMap failed",
                hm.get(ReservedIniKeys.OUTPUT_PREFIX.getKey()).size() == 2
                        && hm.get(ReservedIniKeys.OUTPUT_PREFIX.getKey()).get("default_value").equals("./provisioned/"));
        Assert.assertTrue("normal defaults for ini2RichMap failed",
                hm.get(ReservedIniKeys.OUTPUT_PREFIX.getKey()).size() == 2
                        && hm.get(ReservedIniKeys.OUTPUT_PREFIX.getKey()).get("key").equals(ReservedIniKeys.OUTPUT_PREFIX.getKey()));
        Assert.assertTrue("normal defaults for ini2RichMap failed",
                hm.get(ReservedIniKeys.OUTPUT_DIR.getKey()).size() == 2
                        && hm.get(ReservedIniKeys.OUTPUT_DIR.getKey()).get("default_value").equals("seqware-results"));
        Assert.assertTrue(
                "normal defaults for ini2RichMap failed",
                hm.get(ReservedIniKeys.OUTPUT_DIR.getKey()).size() == 2
                        && hm.get(ReservedIniKeys.OUTPUT_DIR.getKey()).get("key").equals(ReservedIniKeys.OUTPUT_DIR.getKey()));
        Assert.assertTrue(
                "normal defaults for ini2RichMap failed",
                hm.get(ReservedIniKeys.INPUT_FILE.getKey()).size() == 5
                        && hm.get(ReservedIniKeys.INPUT_FILE.getKey()).get("default_value")
                                .equals("${workflow_bundle_dir}/Workflow_Bundle_helloWorld/1.0-SNAPSHOT/data/input.txt"));
        Assert.assertTrue(
                "normal defaults for ini2RichMap failed",
                hm.get(ReservedIniKeys.INPUT_FILE.getKey()).size() == 5
                        && hm.get(ReservedIniKeys.INPUT_FILE.getKey()).get("key").equals(ReservedIniKeys.INPUT_FILE.getKey()));
        Assert.assertTrue("normal defaults for ini2RichMap failed",
                hm.get(ReservedIniKeys.INPUT_FILE.getKey()).size() == 5
                        && hm.get(ReservedIniKeys.INPUT_FILE.getKey()).get("file_meta_type").equals("text/plain"));
        Assert.assertTrue(
                "normal defaults for ini2RichMap failed",
                hm.get(ReservedIniKeys.INPUT_FILE.getKey()).size() == 5
                        && hm.get(ReservedIniKeys.INPUT_FILE.getKey()).get("type").equals("file"));
        Assert.assertTrue("normal defaults for ini2RichMap failed",
                hm.get(ReservedIniKeys.INPUT_FILE.getKey()).size() == 5
                        && hm.get(ReservedIniKeys.INPUT_FILE.getKey()).get("display").equals("F"));
    }

    private StringBuffer createStringFromMap(Map<String, Map<String, String>> hm) {
        // make a single string from the map
        StringBuffer mapBuffer = new StringBuffer();
        for (String key : hm.keySet()) {
            Log.info("KEY: " + key + " VALUE: " + hm.get(key));
            // Log.error(key+"="+map.get(key));
            mapBuffer.append(key).append("=").append(hm.get(key).get("default_value")).append("\n");
        }
        return mapBuffer;
    }

}
