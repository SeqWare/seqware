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

package net.sourceforge.seqware.pipeline.module;
//import org.aspectj.weaver.ast.Test;
import net.sourceforge.seqware.common.util.Log;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * User: xiao
 * Date: 7/25/11
 * Time: 11:09 PM
 */
@StdoutRedirect
@StderrRedirect
public class ModuleMethodTest {

    public ModuleMethodTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAnnotation() {
        for (ModuleMethod m : ModuleMethod.values()) {
            Log.info("m.name() = " + m.name());
        }
        StdoutRedirect stoann = getClass().getAnnotation(StdoutRedirect.class);
        StderrRedirect steann = getClass().getAnnotation(StderrRedirect.class);
        assertEquals(stoann.startsBefore(), ModuleMethod.do_run);
        assertEquals(stoann.endsAfter(), ModuleMethod.do_run);
        assertEquals(steann.startsBefore(), ModuleMethod.do_run);
        assertEquals(steann.endsAfter(), ModuleMethod.do_run);
    }
}