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
package net.sourceforge.seqware.common.util.filetools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import net.sourceforge.seqware.common.util.*;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import static net.sourceforge.seqware.common.util.configtools.ConfigTools.SEQWARE_SETTINGS_PROPERTY;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * <p>FileToolsTest class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class FileToolsTest {

  /**
   * <p>Constructor for FileToolsTest.</p>
   */
  public FileToolsTest() {
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
     *
     * @throws IOException
     */
    @Test
    public void testSEQWARE1410() throws IOException {
        // test on SymLinkNullPointer
        try {
            File sudoersFile = new File("/etc/sudoers");
            if (!sudoersFile.exists()) {
                Log.fatal("Could not test FileTools on file where we have no read rights");
            }
            ArrayList<File> files = new ArrayList<File>();
            FileTools.listFilesRecursive(sudoersFile, files);
        } catch (Exception e) {
            Assert.assertTrue("Did not avoid crash when listing file with no read permissions", false);
        } 
    }

  /**
   * <p>testIsFileOwner.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testIsFileOwner() throws Exception {

    String path = FileToolsTest.class.getResource("FileToolsTest.txt").getPath();
    boolean isOwner = FileTools.isFileOwner(path);
    assertTrue(isOwner);

    // don't do this if you are root for some reason
    if (!"root".equals(System.getProperty("user.name"))) {
      // assumes the root filesystem is owned by 
      isOwner = FileTools.isFileOwner(File.separator);
      assertFalse(isOwner);
    }
  }

  /**
   * <p>testIsFileOwner.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testWhoAmI() throws Exception {

    // you can override this but I'm guessing people won't for testing
    String username = System.getProperty("user.name");

    assertEquals(username, FileTools.whoAmI());

  }
  
  @Test
  public void testOwnership() throws Exception{
      File tempFile = File.createTempFile("test", "test");
      String determineFilePermissions = FileTools.determineFilePermissions(tempFile.getAbsolutePath());
      Assert.assertTrue("incorrect file permissions", determineFilePermissions.equals("-rw-rw-r--"));
      
      // create a "settings file" and then check to see if its permissions are correct
      tempFile.setExecutable(false, false);
      tempFile.setWritable(false, false);
      tempFile.setWritable(true, true);
      tempFile.setReadable(false, false);
      tempFile.setReadable(true, true);
      System.setProperty(SEQWARE_SETTINGS_PROPERTY, tempFile.getPath());
      
      String settingPerms = FileTools.determineFilePermissions(ConfigTools.getSettingsFilePath());
      Assert.assertTrue("incorrect file permissions", settingPerms.equals("-rw-------"));
      tempFile.deleteOnExit();
  }
}
