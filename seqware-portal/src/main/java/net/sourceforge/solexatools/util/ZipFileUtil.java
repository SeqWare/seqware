package net.sourceforge.solexatools.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>ZipFileUtil class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ZipFileUtil {

  /**
   * Unpack a zip file
   *
   * @param theFile a {@link java.io.File} object.
   * @param targetDir a {@link java.io.File} object.
   * @return the file
   * @throws java.io.IOException if any.
   * @param request a {@link javax.servlet.http.HttpServletRequest} object.
   */
  public static java.io.File unpackArchive(File theFile, File targetDir, HttpServletRequest request) throws IOException {
	  
      if (!theFile.exists()) {
          throw new IOException(theFile.getAbsolutePath() + " does not exist");
      }
      if (!buildDirectory(targetDir)) {
          throw new IOException("Could not create directory: " + targetDir);
      }
      ZipFile zipFile = new ZipFile(theFile);
      for (Enumeration entries = zipFile.entries(); entries.hasMoreElements();) {
          ZipEntry entry = (ZipEntry) entries.nextElement();
          File file = new File(targetDir, File.separator + entry.getName());
          if (!buildDirectory(file.getParentFile())) {
              throw new IOException("Could not create directory: " + file.getParentFile());
          }
          if (!entry.isDirectory()) {
              copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(file)));
          } else {
              if (!buildDirectory(file)) {
                  throw new IOException("Could not create directory: " + file);
              }
          }
          
      }
      zipFile.close();
      return theFile;
  }

  /**
   * <p>copyInputStream.</p>
   *
   * @param in a {@link java.io.InputStream} object.
   * @param out a {@link java.io.OutputStream} object.
   * @throws java.io.IOException if any.
   */
  public static void copyInputStream(InputStream in, OutputStream out) throws IOException {
      byte[] buffer = new byte[1024];
      int len = in.read(buffer);
      while (len >= 0) {
          out.write(buffer, 0, len);
          len = in.read(buffer);
      }
      in.close();
      out.close();
  }

  /**
   * <p>buildDirectory.</p>
   *
   * @param file a {@link java.io.File} object.
   * @return a boolean.
   */
  public static boolean buildDirectory(File file) {
      return file.exists() || file.mkdirs();
  }
}
