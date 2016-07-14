package net.sourceforge.seqware.pipeline.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.util.filetools.FileTools;

import java.io.File;

/**
 * <p>
 * Zip class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class Zip {

    /**
     * <p>
     * main.
     * </p>
     * 
     * @param args
     *            an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {

        OptionSet options = null;
        OptionParser parser = new OptionParser();

        parser.accepts("input-dir").withRequiredArg().describedAs("The input directory you want to compress recursively.");
        parser.accepts("output-zip").withRequiredArg().describedAs("Output zip file name.");
        parser.accepts("compress").withOptionalArg().ofType(Boolean.class)
                .describedAs("Boolean flag for compression, default is no compression. [true|false] ").defaultsTo(false);

        options = parser.parse(args);

        FileTools.zipDirectoryRecursive(new File((String) options.valueOf("input-dir")), new File((String) options.valueOf("output-zip")),
                (Boolean) options.valueOf("compress"));
    }
}
