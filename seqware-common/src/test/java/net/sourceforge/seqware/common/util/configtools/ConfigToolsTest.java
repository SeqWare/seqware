/*
 *  Copyright (C) 2011 mtaschuk
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

package net.sourceforge.seqware.common.util.configtools;

import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static net.sourceforge.seqware.common.util.configtools.ConfigTools.SEQWARE_SETTINGS_PROPERTY;

/**
 * <p>ConfigToolsTest class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 * @since 0.13.3
 */
public class ConfigToolsTest {

    /**
     * <p>Constructor for ConfigToolsTest.</p>
     */
    public ConfigToolsTest() {
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
   * Test of getSettings method, of class ConfigTools.
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testGetSettings() throws Exception {
    System.setProperty(SEQWARE_SETTINGS_PROPERTY, getClass().getResource("ConfigToolsTest_settings").getPath());

    Map<String, String> settings = ConfigTools.getSettings(true);

    assertEquals(settings.get("SETTING1"), "one");
    assertEquals(settings.get("SETTING2"), "TWO");
  }

}
