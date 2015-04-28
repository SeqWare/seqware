/*
 * Copyright (C) 2015 SeqWare
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
package io.seqware;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author dyuen
 */
public class EnginesTest {

    /**
     * Test of isOozie method, of class Engines.
     */
    @Test
    public void testIsOozie() {
        String engine = Engines.TYPES.whitestar.getCliString();
        boolean result = Engines.isOozie(engine);
        assertEquals(false, result);
        engine = Engines.TYPES.oozie_sge.getCliString();
        result = Engines.isOozie(engine);
        assertEquals(true, result);
    }

    /**
     * Test of isWhiteStar method, of class Engines.
     */
    @Test
    public void testIsWhiteStar() {
        String engine = Engines.TYPES.whitestar.getCliString();
        boolean result = Engines.isWhiteStar(engine);
        assertEquals(true, result);
        engine = Engines.TYPES.oozie_sge.getCliString();
        result = Engines.isWhiteStar(engine);
        assertEquals(false, result);
    }

    /**
     * Test of isWhiteStarParallel method, of class Engines.
     */
    @Test
    public void testIsWhiteStarParallel() {
        String engine = Engines.TYPES.whitestar_parallel.getCliString();
        boolean result = Engines.isWhiteStarParallel(engine);
        assertEquals(true, result);
        engine = Engines.TYPES.oozie_sge.getCliString();
        result = Engines.isWhiteStarParallel(engine);
        assertEquals(false, result);
    }

}
