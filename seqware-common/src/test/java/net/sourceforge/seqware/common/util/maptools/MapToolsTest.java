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
import net.sourceforge.seqware.common.util.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * <p>MD5GeneratorTest class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.6.2
 */
public class MapToolsTest {


    /**
     * <p>setUpClass.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    /**
     * <p>tearDownClass.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * <p>setUp.</p>
     */
    @Before
    public void setUp() {
    }

    /**
     * <p>tearDown.</p>
     */
    @After
    public void tearDown() {
    }


    @Test
    public void testNormalRichIni() throws Exception {
        String path = getClass().getResource("normal.ini").getPath();
        Map<String, Map<String, String>> hm = new HashMap<String, Map<String, String>>();
        MapTools.ini2RichMap(path, hm);
        Assert.assertTrue("normal ini2RichMap failed", hm.size() == 3);
        testNormalValues(hm);
    }
    
    @Test
    public void testRichIni_withBlanks() throws Exception {
        String path = getClass().getResource("normal_withBlanks.ini").getPath();
        Map<String, Map<String, String>> hm = new HashMap<String, Map<String, String>>();
        MapTools.ini2RichMap(path, hm);
        Assert.assertTrue("ini2RichMap with blanks failed", hm.size() == 7);
        testNormalValues(hm);
        
        Assert.assertTrue("blank defaults for ini2RichMap failed", hm.get("boogydown").size() == 2 && hm.get("boogydown").get("default_value").equals(""));
        Assert.assertTrue("blank defaults for ini2RichMap failed", hm.get("boogydown").size() == 2 && hm.get("boogydown").get("key").equals("boogydown"));
        Assert.assertTrue("blank defaults for ini2RichMap failed", hm.get("funkyparameter_with_extraSpace").size() == 2 && hm.get("funkyparameter_with_extraSpace").get("default_value").equals(""));
        Assert.assertTrue("blank defaults for ini2RichMap failed", hm.get("funkyparameter_with_extraSpace").size() == 2 && hm.get("funkyparameter_with_extraSpace").get("key").equals("funkyparameter_with_extraSpace")); 
        Assert.assertTrue("blank defaults for ini2RichMap failed", hm.get("funkyparameter").size() == 2 && hm.get("funkyparameter").get("default_value").equals(""));
        Assert.assertTrue("blank defaults for ini2RichMap failed", hm.get("funkyparameter").size() == 2 && hm.get("funkyparameter").get("key").equals("funkyparameter"));
        
        Assert.assertTrue("blank annotated defaults for ini2RichMap failed", hm.get("funky_annotated").size() == 5 && hm.get("funky_annotated").get("default_value").equals(""));
        Assert.assertTrue("blank annotated defaults for ini2RichMap failed", hm.get("funky_annotated").size() == 5 && hm.get("funky_annotated").get("key").equals("funky_annotated"));
        Assert.assertTrue("blank annotated defaults for ini2RichMap failed", hm.get("funky_annotated").size() == 5 && hm.get("funky_annotated").get("file_meta_type").equals("text/plain"));
        Assert.assertTrue("blank annotated defaults for ini2RichMap failed", hm.get("funky_annotated").size() == 5 && hm.get("funky_annotated").get("type").equals("file"));
        Assert.assertTrue("blank annotated defaults for ini2RichMap failed", hm.get("funky_annotated").size() == 5 && hm.get("funky_annotated").get("display").equals("F"));
    }

    private void testNormalValues(Map<String, Map<String, String>> hm) {
        Assert.assertTrue("normal defaults for ini2RichMap failed", hm.get("output_prefix").size() == 2 && hm.get("output_prefix").get("default_value").equals("./provisioned/"));
        Assert.assertTrue("normal defaults for ini2RichMap failed", hm.get("output_prefix").size() == 2 && hm.get("output_prefix").get("key").equals("output_prefix"));
        Assert.assertTrue("normal defaults for ini2RichMap failed", hm.get("output_dir").size() == 2 && hm.get("output_dir").get("default_value").equals("seqware-results"));
        Assert.assertTrue("normal defaults for ini2RichMap failed", hm.get("output_dir").size() == 2 && hm.get("output_dir").get("key").equals("output_dir"));
        Assert.assertTrue("normal defaults for ini2RichMap failed", hm.get("input_file").size() == 5 && hm.get("input_file").get("default_value").equals("${workflow_bundle_dir}/Workflow_Bundle_helloWorld/1.0-SNAPSHOT/data/input.txt"));
        Assert.assertTrue("normal defaults for ini2RichMap failed", hm.get("input_file").size() == 5 && hm.get("input_file").get("key").equals("input_file"));
        Assert.assertTrue("normal defaults for ini2RichMap failed", hm.get("input_file").size() == 5 && hm.get("input_file").get("file_meta_type").equals("text/plain"));
        Assert.assertTrue("normal defaults for ini2RichMap failed", hm.get("input_file").size() == 5 && hm.get("input_file").get("type").equals("file"));
        Assert.assertTrue("normal defaults for ini2RichMap failed", hm.get("input_file").size() == 5 && hm.get("input_file").get("display").equals("F"));
    }

   

}
