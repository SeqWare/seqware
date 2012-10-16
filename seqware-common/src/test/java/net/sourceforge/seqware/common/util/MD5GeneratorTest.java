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

package net.sourceforge.seqware.common.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * <p>MD5GeneratorTest class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 * @since 0.13.3
 */
public class MD5GeneratorTest {

    /**
     * <p>Constructor for MD5GeneratorTest.</p>
     */
    public MD5GeneratorTest() {
    }

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


    /**
     * <p>testMd5sum.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testMd5sum() throws Exception {
        String path = getClass().getResource("MD5GeneratorTest_md5file.txt").getPath();
        String md5 = new MD5Generator().md5sum(path);
        assertEquals(md5, "36970fd5940ee207b2f5a575a108948f");
    }

    /**
     * <p>testMd5sum_leadingZero.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testMd5sum_leadingZero() throws Exception {
        String path = MD5Generator.class.getResource("MD5GeneratorTest_leading_zero.txt").getPath();
        String md5 = new MD5Generator().md5sum(path);
        assertEquals(md5, "08c61f3fd48f12fa7c88a7f5fd01df3d");
    }

}
