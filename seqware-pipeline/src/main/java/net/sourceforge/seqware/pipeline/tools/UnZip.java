package net.sourceforge.seqware.pipeline.tools;

import java.io.File;
import java.io.IOException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.util.filetools.FileTools;


/**
 * <p>
 * Unzip tool, seemingly unique only in its ability to unzip Zip64 files
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class UnZip {

    /**
     * <p>
     * main.
     * </p>
     * 
     * @param args
     *            an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) throws IOException {

        OptionSet options;
        OptionParser parser = new OptionParser();

        parser.accepts("input-zip").withRequiredArg().describedAs("The input directory you want to compress recursively").required();
        parser.accepts("output-dir").withRequiredArg().describedAs("Output zip file name").required();

        try {
            options = parser.parse(args);
        } catch(Exception ex){
            parser.printHelpOn(System.out);
            throw new RuntimeException(ex);
        }


        FileTools.unzipFile(new File((String) options.valueOf("input-zip")), new File((String) options.valueOf("output-dir")));

    }

}
