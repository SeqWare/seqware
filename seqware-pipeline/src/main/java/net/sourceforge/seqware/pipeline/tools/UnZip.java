package net.sourceforge.seqware.pipeline.tools;

import java.io.File;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.util.filetools.FileTools;

public class UnZip {
  
  public static void main(String[] args) {

    OptionSet options = null;
    OptionParser parser = new OptionParser();
    
    parser.accepts("input-zip").withRequiredArg().describedAs("The input directory you want to compress recursively.");
    parser.accepts("output-dir").withRequiredArg().describedAs("Output zip file name.");
   
    options = parser.parse(args);
    
    FileTools.unzipFile(new File((String)options.valueOf("input-zip")), new File((String)options.valueOf("output-dir")));
    
  }

}
