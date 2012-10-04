package net.sourceforge.seqware.pipeline.plugins.filelinker;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.seqware.common.module.FileMetadata;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.collections.Sets;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;

public class FileLinkerParser {

   private static final Logger log = LoggerFactory.getLogger(FileLinkerParser.class);

   private final static String UTF8 = "UTF8";

   public static Map<Integer, FileMetadata> parse(String filename) throws FileNotFoundException {
      return parse(filename, '\t');
   }

   public static Map<Integer, FileMetadata> parse(String filename, char separator) throws FileNotFoundException {
      checkNotNull(filename);
      checkElementIndex(0, filename.length());
      checkNotNull(separator);
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
      List<FileLinkerLine> lines = getFileInfo(br, separator);
      requiredValuesPresent(lines);
      return fileMetadataFromFileInfo(lines);
   }

   @VisibleForTesting
   static List<FileLinkerLine> getFileInfo(Reader reader) {
      return getFileInfo(reader, '\t');
   }

   @VisibleForTesting
   static List<FileLinkerLine> getFileInfo(Reader reader, char separator) {
      CSVReader csvReader = new CSVReader(reader, separator);
      HeaderColumnNameTranslateMappingStrategy<FileLinkerLine> strat = new HeaderColumnNameTranslateMappingStrategy<FileLinkerLine>();
      strat.setType(FileLinkerLine.class);
      Map<String, String> map = Maps.newHashMap();
      map.put("sequencer_run", "sequencerRun");
      map.put("sample", "sample");
      map.put("lane", "laneString");
      map.put("ius_sw_accession", "seqwareAccessionString");
      map.put("file_status", "fileStatus");
      map.put("mime_type", "mimeType");
      map.put("size", "sizeString");
      map.put("md5sum", "md5sum");
      map.put("file", "filename");
      strat.setColumnMapping(map);

      CsvToBean<FileLinkerLine> csvToBean = new CsvToBean<FileLinkerLine>();
      List<FileLinkerLine> defaultUsers = csvToBean.parse(strat, csvReader);

      return defaultUsers;
   }

   private static Map<Integer, FileMetadata> fileMetadataFromFileInfo(List<FileLinkerLine> lines) {
      Map<Integer, FileMetadata> result = Maps.newHashMap();
      for (FileLinkerLine line : lines) {
         FileMetadata fileMetadata = new FileMetadata();
         fileMetadata.setMetaType(line.getMimeType());
         fileMetadata.setFilePath(line.getFilename());
         if (!StringUtils.isBlank(line.getMd5sum())) {
            fileMetadata.setMd5sum(line.getMd5sum());
         }
         if (line.getSize() != null) {
            fileMetadata.setSize(line.getSize());
         }
         result.put(line.getSeqwareAccession(), fileMetadata);
      }
      return result;
   }

   static void requiredValuesPresent(List<FileLinkerLine> lines) {
      boolean hasRequiredValues = true;
      Set<Integer> badLines = Sets.newHashSet();
      for (int i = 0; i < lines.size(); i++) {
         FileLinkerLine line = lines.get(i);
         if (!line.hasRequiredValues()) {
            hasRequiredValues = false;
            badLines.add(i + 2);
            log.error("Required value(s) missing in csv file at line number [{}]. Lane, seqwareAccession, mimeType and file are required. {}", i + 2, line);
         }
      }
      if(!hasRequiredValues) {
         throw new FileLinkerLineException("Csv file missing required values on the following lines: " + Arrays.toString(badLines.toArray()));
      }
   }

}
