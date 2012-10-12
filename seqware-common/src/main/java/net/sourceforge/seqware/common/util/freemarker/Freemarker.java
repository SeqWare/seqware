package net.sourceforge.seqware.common.util.freemarker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.sourceforge.seqware.common.util.filetools.FileTools;

/**
 * <p>Freemarker class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Freemarker {
	/*
	 * Initialize freemarker template
	 */
	/**
	 * <p>setup.</p>
	 *
	 * @param template a {@link java.lang.String} object.
	 * @return a {@link freemarker.template.Template} object.
	 */
	public static Template setup(String template) {
		int index = template.lastIndexOf("/");
		String directory = template.substring(0, index);
		String filename = template.substring(index + 1);
		Template ReturnTemplate = null;

		// Initialize freemarker
		Configuration fmConfig = new Configuration();

		// Need to set number format so it doesn't add \, number groupings to large numbers
		fmConfig.setNumberFormat("#.#");
		
		try {
			fmConfig.setDirectoryForTemplateLoading(new File(directory));
		} catch (IOException e) {
			e.printStackTrace();
		}
		fmConfig.setObjectWrapper(new DefaultObjectWrapper());

		// Return the requested template
		try {
			ReturnTemplate = fmConfig.getTemplate(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ReturnTemplate;
	}

	
	/*
	 * Merge template with hash values
	 */
	/**
	 * <p>merge.</p>
	 *
	 * @param inputFile a {@link java.lang.String} object.
	 * @param outputFile a {@link java.lang.String} object.
	 * @param map a {@link java.util.Map} object.
	 * @return a boolean.
	 * @throws java.io.IOException if any.
	 * @throws freemarker.template.TemplateException if any.
	 */
	public static boolean merge( String inputFile, String outputFile, Map map  ) throws IOException, TemplateException {
		// Get starting md5
		String startMD5 = FileTools.md5sumFile(inputFile);

		// Setup freemarker
		Template temp = setup(inputFile);

		// Populate template with map, and write to disk
		Writer out = new FileWriter( outputFile );
		temp.process(map, out);
	  out.close();

		// Get ending MD5
		String endMD5 = FileTools.md5sumFile(outputFile);
		
		// Check if any changes were made
        if ( startMD5.compareTo( endMD5 ) == 0 ) {
			return false;
		}
		else {
			return true;
		}
	}

}
