package net.sourceforge.seqware.pipeline.plugins.filelinker;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.util.List;

import org.junit.Test;

public class FileLinkerParserTest {

   @Test
   public void test_csv_to_entry_samples() throws Exception {
      String schema = "sample\n";
      String line01 = "PCSI_0001_Pa_P_PE_230_EX\n";
      String line02 = "PCSI_0006_Pa_X_PE_400_WG\n";
      String csv = schema + line01 + line02;
      List<FileLinkerLine> lines = FileLinkerParser.getFileInfo(new StringReader(csv), ',');
      assertThat(lines.size(), is(2));
   }

   @Test
   public void test_parse_csv_full() throws Exception {
      String schema = "sequencer_run,sample,lane,ius_sw_accession,file_status,mime_type,size,md5sum,file\n";
      String line01 = "110316_I580_00038_612RG_LT,PCSI_0001_Pa_P_PE_230_EX,1,9037,OK,application/bam,1024,3e5g434,/file1\n";
      String line02 = "110316_I580_00038_612RG_LT,PCSI_0024_Pa_P_PE_230_EX,1,8378,OK,application/bam,2048,78a8e39,/file2\n";
      String csv = schema + line01 + line02;
      List<FileLinkerLine> lines = FileLinkerParser.getFileInfo(new StringReader(csv), ',');
      assertThat(lines.size(), is(2));
      assertThat(lines.get(1).getFilename(), is("/file2"));
      assertThat(lines.get(1).getSequencerRun(), is("110316_I580_00038_612RG_LT"));
      assertThat(lines.get(0).getSize(), is(1024L));
   }

   @Test
   public void test_parse_csv_bad_integer() throws Exception {
      String schema = "lane,ius_sw_accession\n";
      String line01 = "8,bad_seqware_accession_number\n";
      String line02 = "2,45693\n";
      String csv = schema + line01 + line02;
      List<FileLinkerLine> lines = FileLinkerParser.getFileInfo(new StringReader(csv), ',');
      assertThat(lines.get(0).getSeqwareAccession(), nullValue());
      assertThat(lines.get(1).getSeqwareAccession(), is(45693));
   }
   
   @Test(expected = FileLinkerLineException.class)
   public void test_parse_csv_missing_required_fields() throws Exception {
      String schema = "sequencer_run,sample,lane,ius_sw_accession,file_status,mime_type,size,md5sum,file\n";
      String line01 = ",,4,4999,,,,,\n";
      String line02 = "110316_I580_00038_612RG_LT,PCSI_0001_Pa_P_PE_230_EX,1,9037,OK,application/bam,1024,3e5g434,/file1\n";
      String line03 = ",,2,45693,,,,,\n";
      String csv = schema + line01 + line02 + line03;
      List<FileLinkerLine> lines = FileLinkerParser.getFileInfo(new StringReader(csv), ',');
      FileLinkerParser.requiredValuesPresent(lines);
   }

}
