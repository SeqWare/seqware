package net.sourceforge.seqware.pipeline.modules;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.StringTokenizer;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.runtools.RunTools;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;

import org.apache.commons.lang.StringUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 * This is a module for SNVMix.
 * SNVMix is designed to detect single nucleotide variants from next generation sequencing data. 
 * SNVMix is a post-alignment tool. Given a pileup file (either Maq or Samtools format) as input 
 * and model parameters, SNVMix will output the probability that each position is one of three 
 * genotypes:  aa (homozygous for the reference allele, where the reference is the genome the 
 * reads were aligned to), ab (heterozygous) and bb (homozygous for a non-reference allele).  
 * 
 * A tool for fitting the model using expectation maximization is also supplied (use -T option).
 * 
 * A tool for filtering the output of SNVMix can also be used if availalble. The script snvmix2summary.pl
 * filters the output in 4 column depending on provided threshold parameters.
 * 
 * @author xion
 *
 */


/***
 * 
 * <code>
 * Syntax:
 *	./SNVMix2	-m <modelfile> [-i <infile>] [-o <outfile>] [-T | -C | -F] [-p < q | m | s >] [-t < mb | m | b | M | Mb | MB | SNVMix1>] [-q <#>] [-Q <#>] [-a/-b/-d <#,#,#>] [-M <trainP file>] [-h]
 *	Required:
 *	-m <modelfile>	model file, a line for mu and a line for pi, three
 *			space-separated values each, like:
 *			mu 0.xxxxxxxx 0.xxxxxxxx 0.xxxxxxxx
 *			pi 0.xxxxxxxx 0.xxxxxxxx 0.xxxxxxxx
	Optional:
	-i <infile>	quality pileup, from pileup2matlab.pl script (def: STDIN)
	-o <outfile>	where to put the results (def: STDOUT)
	-T | -C | -F	Train, Classify or Filter (def: Classify)
	-p q|m|s	Input pileup format (def: s)
			q = quality
			m = MAQ
			s = SAMtools
	-t mb|m|b|M|Mb|MB|SNVMix1
			Filter type (def: mb)
			mb	Lowest between map and base quality
			m	Filter on map, and use as surrogate for base quality
			b	Filter on base quality, take map quality as 1
			M	Filter on map quality but use only base quality
			Mb	Filter on map quality and use both map and base qualities
			MB	Filter on map quality AND base quality
			SNVMix1	Filter on base quality and map quality, afterwards consider them perfect
	-q #		Cutoff Phred value for Base Quality, anything <= this value is ignored (def: Q19)
	-Q #		Cutoff Phred value for Map Quality, anything <= this value is ignored (def: Q19)

	Training parameters:
	-a #,#,#	Provide alpha training parameters
	-b #,#,#	Provide beta training parameters
	-d #,#,#	Provide delta training parameters
	-M <file>	Provide a file containing training parameters (will override -a, -b and -d)
			Values are space-separated:
			alpha # # #
			beta # # #
			delta # # #

 	-apply-filter 	<yes/no>
 	-perl-path   	Path to perl installation
 	-filter-path 	Path to the filter script
 	-threshold  	THRESHOLD is given, then SNVs will be reported when the selected probability exceeds this
 	-c [2|3]  		TYPE is the number of classes to consider
               		'2' considers only AA and {AB U BB} (default)
               		'3' considers AA, AB and BB
               
	-h				this message
	
	</code>
*/
@ServiceProvider(service=ModuleInterface.class)
public class SNVMix extends Module {

	private OptionSet options = null;
	private File tempFile = new File("tempfile");
	private static final int PHRED_MAX = 200;

	/**
	 * getOptionParser is an internal method to parse command line args.
	 * 
	 * @return OptionParser this is used to get command line options
	 */
	protected OptionParser getOptionParser() {
		OptionParser parser = new OptionParser();
		parser.acceptsAll(Arrays.asList("modelfile", "m"))
				.withRequiredArg()
				.ofType(String.class)
				.describedAs(
						"model file, a line for mu and a line for pi, three \n "
								+ " space-separated values each, like: \n"
								+ "  mu 0.xxxxxxxx 0.xxxxxxxx 0.xxxxxxxx \n"
								+ "  pi 0.xxxxxxxx 0.xxxxxxxx 0.xxxxxxxx");

		parser.acceptsAll(Arrays.asList("infile", "i")).withRequiredArg()
				.ofType(String.class).defaultsTo("STDIN").describedAs("-i <infile>	quality pileup, from pileup2matlab.pl script (def: STDIN)");
		parser.acceptsAll(Arrays.asList("outfile", "o")).withRequiredArg()
				.ofType(String.class).defaultsTo("STDOUT").describedAs("-o <outfile>	where to put the results (def: STDOUT)");
		parser.accepts("script", "Path to C executable: SNVMix").withRequiredArg().ofType(String.class);
		parser.acceptsAll(Arrays.asList("T", "C", "F")).withOptionalArg()
				.defaultsTo("C").describedAs("Train, Classify or Filter (def: Classify)");
		parser.accepts("p").withOptionalArg().ofType(String.class)
				.defaultsTo("s").describedAs("Input pileup format (def: s) "+
						"q = quality " +
						"m = MAQ " +
						"s = SAMtools");

		parser.accepts("t").withRequiredArg().ofType(String.class)
				.defaultsTo("mb").describedAs("-t mb|m|b|M|Mb|MB|SNVMix1 " +
			"Filter type (def: mb) " +
			"mb	Lowest between map and base quality " +
			"m	Filter on map, and use as surrogate for base quality " +
			"b	Filter on base quality, take map quality as 1 " +
			"M	Filter on map quality but use only base quality " +
			"Mb	Filter on map quality and use both map and base qualities " +
			"MB	Filter on map quality AND base quality " +
			"SNVMix1 Filter on base quality and map quality, afterwards consider them perfect");
		parser.accepts("q").withRequiredArg().ofType(Integer.class)
				.defaultsTo(19).describedAs("-q # Cutoff Phred value for Base Quality, anything <= this value is ignored (def: Q19)");
		parser.accepts("Q").withRequiredArg().ofType(Integer.class)
				.defaultsTo(19).describedAs("-Q # Cutoff Phred value for Map Quality, anything <= this value is ignored (def: Q19)");

		parser.accepts("a").withRequiredArg().describedAs("-a #,#,#	Provide alpha training parameters");
		parser.accepts("b").withRequiredArg().describedAs("-b #,#,#	Provide beta training parameters");
		parser.accepts("d").withRequiredArg().describedAs("-d #,#,#	Provide delta training parameters");
		parser.accepts("M").withRequiredArg().describedAs("-M <file>	Provide a file containing training parameters (will override -a, -b and -d)");
		parser.accepts("h").withOptionalArg().describedAs("-h	help message");
		
		// To execute filters
		parser.accepts("apply-filter").withRequiredArg().ofType(String.class).defaultsTo("no").describedAs("Whether to apply filter or not.");
		parser.accepts("perl-path").withRequiredArg().ofType(String.class).describedAs("Path to the perl.");
		parser.accepts("filter-path").withRequiredArg().ofType(String.class).describedAs("Path to the filter script.");
		
		parser.accepts("c")
				.withRequiredArg()
				.describedAs(
						"TYPE is the number of classes to consider."
								+ " '2' considers only AA and {AB U BB} (default) "
								+ " '3' considers AA, AB and BB")
				.ofType(Integer.class).defaultsTo(2);

		parser.accepts("threshold")
				.withRequiredArg()
				.describedAs(
						"THRESHOLD is given, then SNVs will be reported "
								+ "when the selected probability exceeds this");
		
		return (parser);
	}

	/**
	 * A method used to return the syntax for this module
	 * 
	 * @return a string describing the syntax
	 */
	@Override
	public String get_syntax() {
		OptionParser parser = getOptionParser();
		StringWriter output = new StringWriter();
		try {
			parser.printHelpOn(output);
			return (output.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return (e.getMessage());
		}
	}

	/**
	 * Initialization of the module.
	 * 
	 * @return A ReturnValue object that contains information about the status
	 *         of init
	 */
	@Override
	public ReturnValue init() {

		// setup the return value object, notice that we use
		// ExitStatus, this is what SeqWare uses to track the status
		ReturnValue ret = new ReturnValue();
		ret.setExitStatus(ReturnValue.SUCCESS);
		// fill in the algorithm field in the processing table
		ret.setAlgorithm("SVMMix-module");
		// fill in the description field in the processing table
		ret.setDescription("This module implements the SVNMix ");

		try {
			OptionParser parser = getOptionParser();

			// The parameters object is actually an ArrayList of Strings created
			// by splitting the command line options by space. JOpt expects a
			// String[]
			options = parser.parse(this.getParameters().toArray(new String[0]));
			ret.setStdout(ret.getStdout() + "Output: "
					+ (String) options.valueOf("outfile") + "\n");

		} catch (OptionException e) {
			e.printStackTrace();
			ret.setStderr(e.getMessage());
			ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
		} 
		catch (Exception e) {
			e.printStackTrace();
			ret.setStderr(e.getMessage());
			ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
		}

		// now return the ReturnValue
		return (ret);

	}

	/**
	 * Verifies that the parameters make sense. Have to verify the parameters
	 * provided.
	 * 
	 * @return a ReturnValue object
	 */
	@Override
	public ReturnValue do_verify_parameters() {

		// most methods return a ReturnValue object
		ReturnValue ret = new ReturnValue();
		ret.setExitStatus(ReturnValue.SUCCESS);
		if (!options.has("script")) {
			ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
			String stdErr = ret.getStderr();
			ret.setStderr(stdErr + " Must include parameter: --" + "script"
					+ "\n");
		}
		if (!options.has("i") && !options.has("infile")) {
			ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
			String stdErr = ret.getStderr();
			ret.setStderr(stdErr + " Must include parameter: --" + "i/infile"
					+ "\n");
		}
		// now look at the options and make sure they make sense
		for (String option : new String[] { "m", "modelfile" }) {
			if (!options.has(option)) {
				ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
				String stdErr = ret.getStderr();
				ret.setStderr(stdErr + "Must include parameter: --" + option
						+ "\n");
			}
		}

		if (options.has("p") && !Arrays.asList(new String[] { "q", "m", "s" }).contains(
				options.valueOf("p"))) {
			ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
			String stdErr = ret.getStderr();
			ret.setStderr(stdErr
					+ "The pile up format parameter -p must be one of \"q, m, s\"\n");
		}
		
		if (options.has("t") && !Arrays.asList(
				new String[] { "m", "b", "M", "mb", "Mb", "MB", "SNVMix1" })
				.contains(options.valueOf("t"))) {
			ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
			String stdErr = ret.getStderr();
			ret.setStderr(stdErr
					+ "The filter type parameter -t must be one of \"m, b, M, mb, Mb, MB, SNVMix1\"\n");
		}

		if (options.has("q")) {
			int bQ = (Integer) options.valueOf("q");
			if (bQ < 0 || bQ >= PHRED_MAX) {
				ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
				String stdErr = ret.getStderr();
				ret.setStderr(stdErr + "quality threshold value Q" + bQ
						+ " out of range");
			}
		}
		
		if (options.has("Q")) {
			int mQ = (Integer) options.valueOf("Q");
			if (mQ < 0 || mQ >= PHRED_MAX) {
				ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
				String stdErr = ret.getStderr();
				ret.setStderr(stdErr + "quality threshold value Q" + mQ
						+ " out of range");
			}
		}
		
		for (String prms : new String[] { "a", "b", "d" }) {
			if (options.has(prms)) {
				String aValue = (String) options.valueOf(prms);
				StringTokenizer t = new StringTokenizer(aValue, ",");
				int numValues = t.countTokens();
				if (numValues != 3) {
					ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
					String stdErr = ret.getStderr();
					ret.setStderr(stdErr
							+ "could not read alpha parameters, expecting: #,#,#");
				}
				while (t.hasMoreTokens()) {
					try {
						Double d = Double.parseDouble(t.nextToken());
					} catch (NumberFormatException nfe) {
						ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
						String stdErr = ret.getStderr();
						ret.setStderr(stdErr
								+ "could not read alpha parameters, expecting: #,#,#");
					}
				}
			}
		}
		
		// Handle filter option
		if (options.has("apply-filter")) {
			if ((!options.valueOf("apply-filter").equals("yes"))
					&& !(options.valueOf("apply-filter").equals("no"))) {
				ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
				ret.setStderr("apply-filter parameter either should be a \'yes\' or \'no\'");
				return (ret);
			}

			if (options.has("c")) {
				int value = (Integer) options.valueOf("c");
				if (value != 2 && value != 3) {
					ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
					ret.setStderr("TYPE is the number of classes to consider."
							+ "It should be either 2 or 3");
					return (ret);
				}
			}
			
			if (!options.has("filter-path")) {
				ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
				ret.setStderr("Path to filter script snvmix2summary.pl must be specified.");
				return (ret);
			}
			
			if (!options.has("perl-path")) {
				ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
				ret.setStderr("Path to the perl must be specified..");
				return (ret);
			}
			
			if (options.has("threshold")) {
				try {
					Double d = Double.parseDouble((String) options
							.valueOf("threshold"));
				} catch (NumberFormatException nfe) {
					ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
					String stdErr = ret.getStderr();
					ret.setStderr(stdErr
							+ "could not read threshold parameters, expecting: #.#");
				}
			}
		}
		
		if (!options.has("apply-filter")) {
			String stdErr = ret.getStderr();
			if (options.has("filter-path")) {
				ret.setStderr(stdErr
						+ " Ignoring filter-path parameters as apply-filter is not specified"
						+ "\n");
			}

			if (options.has("c")) {
				ret.setStderr(stdErr
						+ " Ignoring c parameters as apply-filter is not specified"
						+ "\n");
			}
			
			if (options.has("threshold")) {
				ret.setStderr(stdErr
						+ " Ignoring threshold parameters as apply-filter is not specified"
						+ "\n");
			}
		}
		
		if(options.has("h")){
			ret.setStderr(get_syntax());
			ret.setExitStatus(ReturnValue.SUCCESS);
		}

		return ret;
	}

	/**
	 * The do_verify_input method ensures that the model files exist. It also
	 * does the validation of the input files or anything that is needed to make
	 * sure the module has everything it needs to run.
	 * 
	 * @return a ReturnValue object
	 */
	@Override
	public ReturnValue do_verify_input() {
		ReturnValue ret = new ReturnValue();
		ret.setExitStatus(ReturnValue.SUCCESS);
		// If input-file argument was specified, make sure it exits and is
		// readable
		String scriptFile="script";
		ReturnValue scriptRet = FileTools.fileExistsAndExecutable(new File(
				(String) options.valueOf(scriptFile)));
		if (scriptRet.getExitStatus() != ReturnValue.SUCCESS) {
			ret.setExitStatus(ReturnValue.FILENOTREADABLE);
			ret.setStderr("Can't execute script file "
					+ (String) options.valueOf(scriptFile) + ": "
					+ scriptRet.getStderr());
			return (ret);
		}
		
		String modelFileOption;
		if (options.has("m")) {
			modelFileOption = "m";
		} else {
			modelFileOption = "modelfile";
		}
		ReturnValue modelRet = FileTools.fileExistsAndReadable(new File(
				(String) options.valueOf(modelFileOption)));
		if (modelRet.getExitStatus() != ReturnValue.SUCCESS) {
			ret.setExitStatus(ReturnValue.FILENOTREADABLE);
			ret.setStderr("Can't read from model file "
					+ (String) options.valueOf(modelFileOption) + ": "
					+ modelRet.getStderr());
			return (ret);
		}
		
		String inputFileOption = null;
		if (options.has("i")) {
			inputFileOption = "i";
		} else if (options.has("infile")){
			inputFileOption = "infile";
		}
		ReturnValue inputRet = FileTools.fileExistsAndReadable(new File(
				(String) options.valueOf(inputFileOption)));
		if (inputRet.getExitStatus() != ReturnValue.SUCCESS) {
			ret.setExitStatus(ReturnValue.FILENOTREADABLE);
			ret.setStderr("Can't read from input file "
					+ (String) options.valueOf(inputFileOption) + ": "
					+ inputRet.getStderr());
			return (ret);
		}
		
		boolean toFilter = options.has("apply-filter") && options.valueOf("apply-filter").equals("yes");
		if (toFilter) {
			for (String path : new String[] {
					(options.valueOf("perl-path") + File.separator + "perl"),
					((options.valueOf("filter-path") + File.separator) + "snvmix2summary.pl") }) {
				if (FileTools.fileExistsAndReadable(new File(path))
						.getExitStatus() != ReturnValue.SUCCESS) {
					ret.setExitStatus(ReturnValue.FILENOTREADABLE);
					ret.setStderr("Can't read file: " + path + " with value: "
							+ options.valueOf(path));
					return (ret);
				}
			}
		}
		return (ret);
	}

	/**
	 * Not implemented.
	 * 
	 * @return a ReturnValue object
	 */
	@Override
	public ReturnValue do_test() {
		ReturnValue ret = new ReturnValue();
		ret.setExitStatus(ReturnValue.NOTIMPLEMENTED);
		return (ret);
	}

	/**
	 * The SNVMix and filter if specified are combined here.
	 * @return a ReturnValue object
	 */
	@Override
	public ReturnValue do_run() {
	    
	    String scriptFile = (String)options.valueOf("script") + ' ';
	    String filterCommand="";
	    String outFile =null;
	    boolean toFilter = options.has("apply-filter") && options.valueOf("apply-filter").equals("yes");
	    
	    if(toFilter){
	    	filterCommand += (options.valueOf("perl-path")+File.separator)+"perl ";	
	    	filterCommand += (options.valueOf("filter-path")+File.separator) + "snvmix2summary.pl ";
	    	if(options.has("c")){
	    		filterCommand += " -c " + options.valueOf("c");
	    	}
	    	if(options.has("threshold")){
	    		filterCommand += " -t " + options.valueOf("threshold");	
	    	}
	    	
			if(options.has("o")){
				outFile = (String)options.valueOf("o");
				this.getParameters().remove("-o");
				this.getParameters().remove(options.valueOf("o"));
			}
			if(options.has("outfile")){
				outFile = (String)options.valueOf("outfile");
				this.getParameters().remove("-outfile");
				this.getParameters().remove(options.valueOf("outfile"));
			}
	    }
	    
	    this.getParameters().remove("-script");
		this.getParameters().remove(options.valueOf("script"));
		
		if(options.has("apply-filter")) {
			this.getParameters().remove("-apply-filter");
			this.getParameters().remove(options.valueOf("apply-filter"));
		}
		if (options.has("perl-path")) {
			this.getParameters().remove("-perl-path");
			this.getParameters().remove(options.valueOf("perl-path"));
		}
		if (options.has("filter-path")) {
			this.getParameters().remove("-filter-path");
			this.getParameters().remove(options.valueOf("filter-path"));
		}
		if (options.has("c")) {
			this.getParameters().remove("-c");
			this.getParameters().remove(options.valueOf("c"));
		}
		if (options.has("threshold")) {
			this.getParameters().remove("-threshold");
			this.getParameters().remove(options.valueOf("threshold"));
		}
		
		String cmd = StringUtils.join( this.getParameters(), ' ' );
		String scriptFileAndOptions = scriptFile.concat(cmd);
		ReturnValue result;
		
		if (toFilter) {
			scriptFileAndOptions += " -o " + "tempfile";
			// Launch the process
			result = RunTools.runCommand(new String[] { "bash", "-c",
					scriptFileAndOptions });
		} else {
			if(outFile != null) {
				scriptFileAndOptions += " -o " + outFile;
				result = RunTools.runCommand(scriptFileAndOptions);
			}
			else{
				result = RunTools.runCommand(scriptFileAndOptions);	
			}
		}
	    
	    if(toFilter && result.getProcessExitStatus() == ReturnValue.SUCCESS) {
	    	if(outFile != null) {
	    		this.setStdoutFile(new File(outFile));
	    		filterCommand += " -i " + "tempfile" + " > " + outFile;
	    		result = RunTools.runCommand(new String[]{"bash", "-c", filterCommand});
	    	}
	    	else {
	    		filterCommand += " -i " + "tempfile";
	    		result = RunTools.runCommand( filterCommand );
	    	}
	    }
	    return result;
	}

	/**
	 * A method to check to make sure the output was created correctly
	 * 
	 * @return a ReturnValue object
	 */
	@Override
	public ReturnValue do_verify_output() {
		// this is easy, just make sure the file exists
		ReturnValue ret = new ReturnValue();
		ret.setExitStatus(ReturnValue.SUCCESS);
		if (options.has("o") || options.has("outfile")) {
			return (FileTools.fileExistsAndReadable(new File((String) options
					.valueOf("outfile"))));
		}
		return ret;
	}

	/**
	 * A cleanup method, this cleans up the temporary file created.
	 */
	@Override
	public ReturnValue clean_up() {
		boolean toFilter = options.has("apply-filter") && options.valueOf("apply-filter").equals("yes");
		ReturnValue ret = new ReturnValue();
		ret.setExitStatus(ReturnValue.SUCCESS);
		
		if (toFilter && !tempFile.delete()) {
			ret.setExitStatus(ReturnValue.FILENOTWRITABLE);
			ret.setStderr("Can't delete file: " + tempFile.getAbsolutePath());
		}
		return (ret);
	}
}